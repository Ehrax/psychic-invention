package de.in.uulm.map.quartett.gallery;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import de.in.uulm.map.quartett.R;
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
    private final Context ctx;

    public GalleryPresenter(@NonNull GalleryContract.View galleryView, Context ctx) {

        mView = galleryView;
        this.ctx = ctx;
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
    public List<Fragment> createCardFragments(int deckID) {

        return null;
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
                    ("android.resource://" + ctx.getPackageName()
                            + "/drawable/main_menu_car"), "the description"),
                    new ArrayList<Card>(), new ArrayList<Attribute>()));
            decks.add(new Deck("TestDeck", "Test description", new Image(Uri.parse
                    ("android.resource://" + ctx.getPackageName()
                            + "/drawable/main_menu_car"), "the description"),
                    new ArrayList<Card>(), new ArrayList<Attribute>()));
            decks.add(new Deck("TestDeck", "Test description", new Image(Uri.parse
                    ("android.resource://" + ctx.getPackageName()
                            + "/drawable/main_menu_car"), "the description"),
                    new ArrayList<Card>(), new ArrayList<Attribute>()));
            decks.add(new Deck("TestDeck", "Test description", new Image(Uri.parse
                    ("android.resource://" + ctx.getPackageName()
                            + "/drawable/main_menu_car"), "the description"),
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

    }
}
