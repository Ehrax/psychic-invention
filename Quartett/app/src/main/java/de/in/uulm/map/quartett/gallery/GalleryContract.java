package de.in.uulm.map.quartett.gallery;

import android.content.Intent;
import android.widget.ImageView;

import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.Image;
import de.in.uulm.map.quartett.util.BasePresenter;
import de.in.uulm.map.quartett.util.BaseView;

import java.util.List;

/**
 * Created by maxka on 25.12.2016.
 */

public interface GalleryContract {

    interface Presenter extends BasePresenter {

        void onDeckClicked(Deck deck);

        void onImageLongClicked(Image image);

        Card getCard(long deckId, int position);

        void loadServerImage(String url, ImageView imageView);
    }

    interface View extends BaseView<Presenter> {

    }

    interface Backend {

        Intent getIntent();

        void startActivity(Intent intent);

        void switchToView(GalleryContract.View view);

        void loadServerImage(String url, ImageView imageView);

        void downloadDeck(Deck deck);
    }

    interface SubView extends View {

        void showImageDescription(Image image);
    }
}
