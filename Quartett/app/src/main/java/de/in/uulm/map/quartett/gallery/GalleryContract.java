package de.in.uulm.map.quartett.gallery;

import android.content.Intent;
import android.widget.ImageView;

import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.Image;
import de.in.uulm.map.quartett.util.BasePresenter;
import de.in.uulm.map.quartett.util.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxka on 25.12.2016.
 */

public interface GalleryContract {

    interface Presenter extends BasePresenter {

        void onDeckLoaded(List<Deck> decks);

        void onDeleteDeckClicked(Deck deck);

        void onDeckClicked(Deck deck);

        void onImageLongClicked(Image image);

        void loadServerImage(String url, ImageView imageView);

        void onDownloadProgress(int deckId, int progress);

        void onDownloadDialogOk(Deck deck);

        void onDeleteDialogOk(Deck deck);

        Card getCard(long deckId, int position);
    }

    interface View extends BaseView<Presenter> {

        void showDownloadDialog(Deck deck);

        void showDeleteDialog(Deck deck);
    }

    interface SubView extends View {

        void showImageDescription(Image image);
    }

    interface Backend {

        Intent getIntent();

        void startActivity(Intent intent);

        void switchToView(GalleryContract.View view);

        void loadServerImage(String url, ImageView imageView);

        void loadDecks();

        void loadServerDecks();

        void downloadDeck(Deck deck);
    }

    interface Model {

        void update();

        void update(int position, Object payload);

        ArrayList<Deck> getDecks();
    }

}
