package de.in.uulm.map.quartett.gallery;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import de.in.uulm.map.quartett.data.Attribute;
import de.in.uulm.map.quartett.data.Card;
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

    public GalleryPresenter(@NonNull GalleryContract.View galleryView,
                            Context ctx, GalleryActivity.ViewSwitcher
                                    viewSwitcher) {

        mView = galleryView;
        this.mCtx = ctx;
        mViewSwitcher = viewSwitcher;
    }

    @Override
    public void start() {

    }

    /**
     * Use this method to populate the FlippableCardView in the deck detail
     * fragment.
     *
     * @param deckID ID of the deck you want to show
     * @return A ArrayList of fragments which hold the cards.
     */
    @Override
    public List<Fragment> createCardFragments(long deckID) {
        //try catch just for testing until json parser is available
        try {
            Deck currentDeck = Deck.findById(Deck.class, deckID);
            List<Card> cards = currentDeck.mCards;
            List<Fragment> cardFragments = new ArrayList<>();

            for (Card card : cards) {
                CardFragment currentCard = CardFragment.newInstance();
                currentCard.setCardImageUris(card.mImages);
                currentCard.setCardAttributes(currentDeck.mAttributes);
                currentCard.setCardTitle(card.mTitle);
                currentCard.setCardAttributeValues(card.mAttributeValues);
                cardFragments.add(currentCard);

            }
            return cardFragments;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Use this method to get a ArrayList of all available decks.
     *
     * @return A ArrayList of all available decks
     */
    @Override
    public List<Deck> populateDeckList() {

        List<Deck> decks = new ArrayList<>();
        try {
            decks = Deck.listAll(Deck.class);
        } catch (Exception e) {

        }
        /* just for testing can be removed if json parser is implemented ;) */
        if (decks.size() == 0) {
            decks.add(new Deck("TestDeck", "Test description", new Image(Uri.parse
                    ("android.resource://" + mCtx.getPackageName()
                            + "/drawable/main_menu_car"), "the description"),
                    new ArrayList<Card>(), new ArrayList<Attribute>()));
            decks.add(new Deck("TestDeck", "Test description", new Image(Uri.parse
                    ("android.resource://" + mCtx.getPackageName()
                            + "/drawable/test"), "the description"),
                    new ArrayList<Card>(), new ArrayList<Attribute>()));
            decks.add(new Deck("TestDeck", "Test description", new Image(Uri.parse
                    ("android.resource://" + mCtx.getPackageName()
                            + "/drawable/main_menu_car"), "the description"),
                    new ArrayList<Card>(), new ArrayList<Attribute>()));
            decks.add(new Deck("TestDeck", "Test description", new Image(Uri.parse
                    ("android.resource://" + mCtx.getPackageName()
                            + "/drawable/test"), "the description"),
                    new ArrayList<Card>(), new ArrayList<Attribute>()));
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
