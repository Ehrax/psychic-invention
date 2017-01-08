package de.in.uulm.map.quartett.game;

import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.data.LocalGameState;
import de.in.uulm.map.quartett.gallery.CardFragment;
import de.in.uulm.map.quartett.util.BasePresenter;
import de.in.uulm.map.quartett.util.BaseView;

/**
 * Created by maxka on 08.01.2017.
 */

public interface GameContract {

    interface Presenter extends BasePresenter {

        CardFragment getCurrentCardFragment();
        void saveGameState();
        LocalGameState getCurrentGameState();
        void chooseAttribute(AttributeValue chosenAttr);
    }

    interface View extends BaseView<Presenter> {

    }

}
