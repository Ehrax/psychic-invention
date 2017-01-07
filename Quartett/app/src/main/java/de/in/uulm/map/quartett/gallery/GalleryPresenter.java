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
     * fragment. This method can run for a while and should be called Async.
     *
     * @param deckID ID of the deck you want to show
     * @return A ArrayList of fragments which hold the cards.
     */
    @Override
    public List<Fragment> createCardFragments(long deckID) {
        try {
            Deck currentDeck = Deck.findById(Deck.class, deckID);
            List<Card> cards = currentDeck.getCards();
            List<Fragment> cardFragments = new ArrayList<>();

            for (Card card : cards) {
                CardFragment currentCard = CardFragment.newInstance();
                currentCard.setCardImageUris(card.getCardImages());
                currentCard.setCardTitle(card.mTitle);
                currentCard.setCardAttributeValues(card.getAttributeValues());
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
