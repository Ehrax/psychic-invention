package de.in.uulm.map.quartett.gallery;

import android.support.v4.app.Fragment;

import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.util.BasePresenter;
import de.in.uulm.map.quartett.util.BaseView;

import java.util.List;

/**
 * Created by maxka on 25.12.2016.
 */

public interface GalleryContract {

    interface Presenter extends BasePresenter {

        Fragment createCardFragment(long deckID,int position);

        List<Deck> populateDeckList();

        void showDeckDetail(long deckID);

        List<Fragment> createDummyList(long deckID);

    }

    interface View extends BaseView<Presenter> {

    }

}
