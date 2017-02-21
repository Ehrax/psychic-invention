package de.in.uulm.map.quartett.multiplayer;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;

import android.content.Intent;
import android.widget.ImageView;

import de.in.uulm.map.quartett.data.Attribute;
import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.gallery.CardFragment;
import de.in.uulm.map.quartett.util.BasePresenter;
import de.in.uulm.map.quartett.util.BaseView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by maxka on 29.01.2017.
 */

public interface MultiplayerContract {

    interface Presenter extends BasePresenter {

        void connectToGoogleGames();

        void disconnectFromGoogleGames();

        void onResolveConnectionFailureResult(int resultCode, int requestCode);

        void startSelectOpponent();

        void createMatch(Intent data);

        GoogleApiClient getClient();

        void startSelectedGame(TurnBasedMatch match);

        void leaveMatch(TurnBasedMatch match);

        void setView (MultiplayerContract.View view);

        TurnData getCurrentTurnData();

        TurnBasedMatch getCurrentTurnBasedMatch();

        CardFragment createCurrentCardFragment();

        String getOpponentName();

        byte getThisPlayersPoints();

        byte getOtherPlayersPoints();

        Card getCard(long deckID,int position);

        void chooseAttribute(Attribute chosenAttribute);

        void setCompareImage(ImageView imgView,boolean fromThisPlayer);

        Attribute getCurrentAttribute();

        AttributeValue getPlayerCompareAttributeValue(boolean fromFirst);

        MultiplayerTurnWinner getWinner();

        boolean isThisPlayerFirstPlayer();

        void onClickCompare();

        void releaseAllBuffer();

        void setSawCompare(boolean sawCompare);

    }

    interface View extends BaseView<Presenter> {

        void makeSnackBar(String message);

        void replaceProgressBar();

        void setDelayedEnterTransition(boolean delayedEnterTransition);

    }

    interface GameView extends BaseView<Presenter>{

    }

    interface Backend {

        boolean resolveConnectionFailure(GoogleApiClient client,
                                         ConnectionResult result);

        void showActivityResultError(int requestCode, int resultCode);

        void startSelectOpponent(GoogleApiClient client);

        void switchToView(MultiplayerContract.View view);

        MultiplayerActivity getActivity();

    }

    interface Model {

        void update();

        void notifyAddedItem(int position);

        ArrayList<TurnBasedMatch> getMatchList();

        int getAmountOfPendingRemovalsForAnimation();

        void decrementAmountOfPendingRemovalForAnimation();

    }
}
