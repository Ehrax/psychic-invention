package de.in.uulm.map.quartett.gallery;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import de.in.uulm.map.quartett.data.Attribute;
import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.CardImage;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxka on 25.12.2016.
 */

public class GalleryPresenter implements GalleryContract.Presenter {

    @NonNull
    private final GalleryContract.View mView;
    private final Context mCtx;

    private GalleryActivity.ViewSwitcher mViewSwitcher;

    public GalleryPresenter(@NonNull GalleryContract.View view,
                            Context ctx, GalleryActivity.ViewSwitcher
                                    viewSwitcher) {

        mView = view;
        this.mCtx = ctx;
        mViewSwitcher = viewSwitcher;
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
     * This method is called when a long click on a CardImage has been detected.
     *
     * @param cardImage the CardImage on which has been long clicked
     */
    @Override
    public void onCardImageLongClicked(CardImage cardImage) {

        if(mView instanceof GalleryContract.SubView) {
            ((GalleryContract.SubView) mView).showCardImageDescription(cardImage);
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
        currentCard.setCardImageUris(card.getCardImages(), mCtx);
        currentCard.setCardTitle(card.mTitle);
        currentCard.setCardAttributeValues(card.getAttributeValues());

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
     * Use this method to switch to the deck detail fragment.
     *
     * @param deckID the ID of the deck you want to show in detail.
     */
    @Override
    public void showDeckDetail(long deckID) {

        DeckFragment deckFragment = DeckFragment.newInstance();
        deckFragment.setCurrentDeckID(deckID);
        mViewSwitcher.switchToView(deckFragment);
    }
}
