package de.in.uulm.map.quartett.game;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import de.in.uulm.map.quartett.data.Attribute;
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

        void chooseAttribute(Attribute chosenAttr);

        Drawable getCompareImage(boolean fromUser);

        float getCompareAttributeValue(boolean fromUser);

        void onClickCompare(RoundWinner winner);

        void startAI();

    }

    interface View extends BaseView<Presenter> {

    }

    interface BackEnd {

        void switchToView(GameContract.View view);

        void startActivity(Intent intent);
    }

}
