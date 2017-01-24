package de.in.uulm.map.quartett.gallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.Gallery;
import android.widget.ImageView;

import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.CardImage;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.DeckInfo;
import de.in.uulm.map.quartett.data.Image;
import de.in.uulm.map.quartett.game.GameActivity;
import de.in.uulm.map.quartett.gamesettings.GameSettingsPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxka on 25.12.2016.
 */

public class GalleryPresenter implements GalleryContract.Presenter {

    @NonNull
    private final Context mCtx;

    private final GalleryContract.View mView;

    private final GalleryContract.Backend mBackend;

    private final GalleryContract.Model mModel;

    public GalleryPresenter(@NonNull Context ctx,
                            @NonNull GalleryContract.View galleryView,
                            @NonNull GalleryContract.Backend backend,
                            @NonNull GalleryContract.Model model) {

        mCtx = ctx;
        mView = galleryView;
        mBackend = backend;
        mModel = model;
    }

    @Override
    public void start() {

        mBackend.loadDecks();

        GalleryMode mode =
                (GalleryMode) mBackend.getIntent().getSerializableExtra("mode");

        if(mode == GalleryMode.CHOOSE) {
            return;
        }

        mBackend.loadServerDecks();
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
     * This method is used by the CardFragment to access the underlying data.
     *
     * @param deckId   the id of the deck the card belongs to
     * @param position the position of the card in the deck
     * @return a Card object
     */
    @Override
    public Card getCard(long deckId, int position) {

        return Deck.findById(Deck.class, deckId).getCards().get(position);
    }

    /**
     * This method is used by the GalleryAdapter to asynchronously load deck
     * images from the server.
     *
     * @param url       the url of the image
     * @param imageView the image view to load the image into
     */
    @Override
    public void loadServerImage(String url, ImageView imageView) {

        mBackend.loadServerImage(url, imageView);
    }

    /**
     * This method will be called on confirmation of the download dialog.
     *
     * @param deck the deck to be downloaded
     */
    @Override
    public void onDownloadDialogOk(Deck deck) {

        mBackend.downloadDeck(deck);
        deck.mDeckInfo.mState = DeckInfo.State.DOWNLOADING;
        mModel.update();
    }

    /**
     * This method will be called on confirmation of the download dialog.
     *
     * @param deck the deck to be deleted
     */
    @Override
    public void onDeleteDialogOk(Deck deck) {

        mModel.getDecks().remove(deck);

        for(Card c : deck.getCards()) {
            for(CardImage i : c.getCardImages()) {
                mCtx.deleteFile(i.mImage.mUri);
            }
        }

        deck.delete();
        mBackend.loadServerDecks();
    }

    /**
     * This method will be called when Deck objects have been loaded.
     *
     * @param decks all decks in the database
     */
    @Override
    public void onDeckLoaded(List<Deck> decks) {

        ArrayList<Deck> modelDecks = mModel.getDecks();

        for (Deck d : decks) {
            boolean contains = false;
            for (Deck dl : modelDecks) {
                contains = contains || (
                        dl.mDeckInfo.mSource.equals(d.mDeckInfo.mSource) &&
                                dl.mTitle.equals(d.mTitle));
            }
            if (!contains) {
                modelDecks.add(d);
            }
        }

        mModel.update();
    }

    /**
     * This method will be called when the download of a deck is completed.
     *
     * @param oldDeck the original Deck from the model
     * @param newDeck the new Deck that is stored in the database
     */
    @Override
    public void onDeckDownloaded(Deck oldDeck, Deck newDeck) {

        if(newDeck == null) {
            return;
        }

        ArrayList<Deck> decks = mModel.getDecks();
        int oldIndex = decks.indexOf(oldDeck);

        decks.remove(oldDeck);
        decks.add(oldIndex, newDeck);

        mModel.update();
    }

    /**
     * This is called if the delete button of an online Deck was clicked.
     *
     * @param deck the deck on which was clicked
     */
    @Override
    public void onDeleteDeckClicked(Deck deck) {

        mView.showDeleteDialog(deck);
    }

    /**
     * Use this method to switch to the deck detail fragment or to pass the deck
     * to further activities.
     *
     * @param deck the model of the Deck on which was clicked
     */
    @Override
    public void onDeckClicked(Deck deck) {

        GalleryMode mode = (GalleryMode)
                mBackend.getIntent().getSerializableExtra("mode");

        if (mode == GalleryMode.CHOOSE) {
            Intent intent = new Intent(mCtx, GameActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtras(mBackend.getIntent());
            intent.putExtra(GameSettingsPresenter.DECK, deck.getId());
            intent.removeExtra("mode");
            mBackend.startActivity(intent);
            return;
        }

        switch (deck.mDeckInfo.mState) {

            case DISK:
                DeckFragment deckFragment = DeckFragment.newInstance();
                deckFragment.setCurrentDeckID(deck.getId());
                mBackend.switchToView(deckFragment);
                break;
            case SERVER:
                mView.showDownloadDialog(deck);
                break;
            default:
                break;
        }
    }
}
