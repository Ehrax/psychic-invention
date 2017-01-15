package de.in.uulm.map.quartett.gallery;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.CardImage;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.Image;
import de.in.uulm.map.quartett.game.GameActivity;
import de.in.uulm.map.quartett.gameend.GameEndPresenter;
import de.in.uulm.map.quartett.gamesettings.GameSettingsPresenter;
import de.in.uulm.map.quartett.mainmenu.MainMenuActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxka on 25.12.2016.
 */

public class GalleryPresenter implements GalleryContract.Presenter {

    @NonNull
    private final GalleryContract.View mView;
    private final Context mCtx;
    private final GalleryContract.Backend mBackend;


    public GalleryPresenter(@NonNull GalleryContract.View galleryView,
                            Context ctx, GalleryContract.Backend
                                    viewSwitcher) {

        mView = galleryView;
        this.mCtx = ctx;
        mBackend = viewSwitcher;

    }

    @Override
    public void start() {

    }

    /**
     * Fill a list with empty fragment. This method is used to initialize the
     * FlippableStackView and load the the Actual card fragments on the fly.
     *
     * @param deckID the deckID from the deck you later want to load in the
     *               flippable stack view. This is important to initialise the
     *               list with the correct size.
     */
    @Override
    public List<Fragment> createDummyList(long deckID) {

        Deck currentDeck = Deck.findById(Deck.class, deckID);
        List<Fragment> dummyList = new ArrayList<>();
        for (int i = 0; i < currentDeck.getCards().size(); i++) {
            dummyList.add(new Fragment());
        }
        return dummyList;
    }

    /**
     * This method is called when a long click on a CardImage has been
     * detected.
     *
     * @param image the CardImage on which has been long clicked
     */
    @Override
    public void onImageLongClicked(Image image) {

        if (mView instanceof GalleryContract.SubView) {
            ((GalleryContract.SubView) mView).showImageDescription(image);
        }
    }

    /**
     * create a card fragment from a card in a given deck on a given position
     *
     * @param deckID   the deckId you want the card from
     * @param position the position of the card you want to load
     * @return a complete card fragment from the wanted card
     */
    @Override
    public Fragment createCardFragment(long deckID, int position) {

        Deck currentDeck = Deck.findById(Deck.class, deckID);
        List<Card> cards = currentDeck.getCards();
        Card card = cards.get(position);

        CardFragment currentCard = CardFragment.newInstance();
        currentCard.setPresenter(this);
        currentCard.setCard(card);

        return currentCard;
    }

    /**
     * Use this method to get a ArrayList of all available decks.
     *
     * @return A ArrayList of all available decks
     */
    @Override
    public List<Deck> populateDeckList() {

        List<Deck> decks;
        try {
            decks = Deck.listAll(Deck.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return decks;
    }

    /**
     * Use this method to switch to the deck detail fragment or to pass the deck
     * to further activities.
     *
     * @param deckID the ID of the deck you want to show in detail.
     */
    @Override

    public void onDeckClicked(long deckID) {

        Intent callingIntent = mBackend.getIntent();
        GalleryMode mode =
                (GalleryMode) callingIntent.getSerializableExtra("mode");

        if (mode == null || mode == GalleryMode.VIEW) {
            DeckFragment deckFragment = DeckFragment.newInstance();
            deckFragment.setCurrentDeckID(deckID);
            mBackend.switchToView(deckFragment);
        } else {
            Intent intent = new Intent(mCtx, GameActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtras(mBackend.getIntent());
            intent.putExtra(GameSettingsPresenter.DECK, deckID);
            intent.removeExtra("mode");
            mBackend.startActivity(intent);
        }

    }
}
