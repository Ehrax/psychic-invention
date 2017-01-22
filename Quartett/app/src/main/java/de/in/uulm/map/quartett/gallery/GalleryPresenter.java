package de.in.uulm.map.quartett.gallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.Image;
import de.in.uulm.map.quartett.game.GameActivity;
import de.in.uulm.map.quartett.gamesettings.GameSettingsPresenter;

/**
 * Created by maxka on 25.12.2016.
 */

public class GalleryPresenter implements GalleryContract.Presenter {

    @NonNull
    private final Context mCtx;
    private final GalleryContract.View mView;
    private final GalleryContract.Backend mBackend;


    public GalleryPresenter(@NonNull GalleryContract.View galleryView,
                            Context ctx,
                            GalleryContract.Backend backend) {

        mCtx = ctx;
        mView = galleryView;
        mBackend = backend;
    }

    @Override
    public void start() {

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

        if (deck.mDeckInfo.mOnDisk) {
            DeckFragment deckFragment = DeckFragment.newInstance();
            deckFragment.setCurrentDeckID(deck.getId());
            mBackend.switchToView(deckFragment);
        } else {
            mBackend.downloadDeck(deck);
        }
    }
}
