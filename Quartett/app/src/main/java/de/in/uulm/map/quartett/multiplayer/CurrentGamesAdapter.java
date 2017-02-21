package de.in.uulm.map.quartett.multiplayer;

import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.views.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by maxka on 30.01.2017.
 */

public class CurrentGamesAdapter extends RecyclerView
        .Adapter<CurrentGamesAdapter.ViewHolder> implements
        MultiplayerContract.Model {

    /*
    Time to wait after item was swiped out to actually delete it and leave the
     game and remove the undo button
     */
    private static final int PENDING_REMOVAL_TIMEOUT = 3000;
    private Handler mHandler = new Handler();
    /*
    Map of matches to pending runnables so we can cancel a removal if we need
     to.
     */
    private HashMap<TurnBasedMatch, Runnable> mPendingRunnables = new HashMap<>();


    final private Context mContext;
    final private ArrayList<TurnBasedMatch> mMatchList;
    private ArrayList<TurnBasedMatch> mMatchListPendingRemoval;
    private MultiplayerContract.Presenter mPresenter;

    private int mAmountPendingRemovalForAnimation;


    //Used to remember the last item shown on screen to animate the items only
    // on scroll down and not again when scrolling up
    private int mLastPosition = -1;

    public CurrentGamesAdapter(Context ctx) {

        mMatchList = new ArrayList<>();
        mMatchListPendingRemoval = new ArrayList<>();
        mContext = ctx;
        mAmountPendingRemovalForAnimation = 0;
    }

    public void setPresenter(MultiplayerContract.Presenter presenter) {

        mPresenter = presenter;
    }

    @Override
    public void update() {

        notifyDataSetChanged();
    }


    @Override
    public void notifyAddedItem(int position) {

        notifyItemInserted(position);
    }

    @Override
    public ArrayList<TurnBasedMatch> getMatchList() {

        return mMatchList;
    }

    @Override
    public CurrentGamesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .multiplayer_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {

        holder.clearAnimation();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final TurnBasedMatch currentMatch = mMatchList.get(position);

        if (mMatchListPendingRemoval.contains(currentMatch)) {
            holder.itemView.setBackgroundColor(Color.RED);
            holder.mTextViewOpponentName.setVisibility(View.GONE);
            holder.mProfilePicture.setVisibility(View.GONE);
            holder.mTextViewWhosTurn.setVisibility(View.GONE);
            holder.mTextViewCurrentDeck.setVisibility(View.GONE);
            holder.mTextViewCurrentTurn.setVisibility(View.GONE);
            holder.mBtnUndoLeaveGame.setVisibility(View.VISIBLE);
            holder.mBtnUndoLeaveGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Runnable pendingRemovalRunnable = mPendingRunnables.get(currentMatch);
                    mPendingRunnables.remove(currentMatch);
                    if (pendingRemovalRunnable != null) {
                        mHandler.removeCallbacks(pendingRemovalRunnable);
                    }
                    mMatchListPendingRemoval.remove(currentMatch);
                    notifyItemChanged(mMatchList.indexOf(currentMatch));
                }
            });

        } else {
            holder.itemView.setBackgroundColor(mContext.getColor(R.color
                    .materialDarkBackground));
            holder.mTextViewOpponentName.setVisibility(View.VISIBLE);
            holder.mProfilePicture.setVisibility(View.VISIBLE);
            holder.mTextViewWhosTurn.setVisibility(View.VISIBLE);
            holder.mTextViewCurrentDeck.setVisibility(View.VISIBLE);
            holder.mTextViewCurrentTurn.setVisibility(View.VISIBLE);
            holder.mBtnUndoLeaveGame.setVisibility(View.GONE);

            if (currentMatch.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN) {
                holder.mTextViewWhosTurn.setText(mContext.getString(R.string
                        .your_turn));
            } else {
                holder.mTextViewWhosTurn.setText(mContext.getString(R.string.ai_turn));
            }

            ArrayList<String> participantIds = currentMatch.getParticipantIds();
            Participant opponent = null;
            for (String participantId : participantIds) {
                opponent = currentMatch.getParticipant(participantId);
                if (!opponent.getDisplayName().equals(Games
                                .Players.getCurrentPlayer(mPresenter.getClient()).getDisplayName())) {
                    break;
                }
                opponent=null;
            }
            holder.mTextViewOpponentName.setText(opponent != null ? opponent
                    .getDisplayName() : mContext.getString(R.string
                    .create_automatch));

            if(opponent!=null) {
                ImageManager.create(mContext).loadImage(holder.mProfilePicture,
                        opponent.getIconImageUri(),R.drawable.no_picture);
            }




            if(currentMatch.getData()!=null) {
                TurnData currentData = TurnData.convertFromBytes(currentMatch
                        .getData());
                holder.mTextViewCurrentTurn.setText(currentData.getCurrentTurn()
                        + "/" + mContext.getString(R.string.max_multiplayer_turns));
            }else{
                holder.mTextViewCurrentTurn.setText("1/"+mContext.getString(R
                        .string.max_multiplayer_turns));
            }


            holder.mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mPresenter.startSelectedGame(currentMatch);
                }
            });

            setAnimation(holder.mContainer, position);
        }

    }

    public void pendingRemoval(int position) {

        final TurnBasedMatch match = mMatchList.get(position);
        if (!mMatchListPendingRemoval.contains(match)) {
            mMatchListPendingRemoval.add(match);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {

                    if (mMatchList.contains(match)) {
                        remove(mMatchList.indexOf(match));
                    }
                }
            };
            mHandler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            mPendingRunnables.put(match, pendingRemovalRunnable);
            mAmountPendingRemovalForAnimation++;
        }
    }

    public void remove(int position) {

        TurnBasedMatch match = mMatchList.get(position);
        mPresenter.leaveMatch(match);
        if (mMatchListPendingRemoval.contains(match)) {
            mMatchListPendingRemoval.remove(match);
        }
        if (mMatchList.contains(match)) {
            mMatchList.remove(position);
            notifyItemRemoved(position);
        }


    }

    @Override
    public void decrementAmountOfPendingRemovalForAnimation() {

        mAmountPendingRemovalForAnimation--;
    }

    @Override
    public int getAmountOfPendingRemovalsForAnimation() {

        return mAmountPendingRemovalForAnimation;
    }

    public boolean isPendingRemoval(int position) {

        TurnBasedMatch match = mMatchList.get(position);
        return mMatchListPendingRemoval.contains(match);
    }


    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > mLastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R
                    .anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            mLastPosition = position;
        }
    }

    @Override
    public int getItemCount() {

        return mMatchList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView mContainer;
        ImageView mProfilePicture;
        TextView mTextViewWhosTurn;
        TextView mTextViewOpponentName;
        TextView mTextViewCurrentDeck;
        TextView mTextViewCurrentTurn;
        Button mBtnUndoLeaveGame;

        ViewHolder(View v) {

            super(v);
            mContainer = (CardView) v.findViewById(R.id.card_view_multiplayer);
            mProfilePicture = (ImageView) v.findViewById(R.id
                    .img_opponent_profile_pic);
            mTextViewCurrentDeck = (TextView) v.findViewById(R.id
                    .txt_current_deck_multiplayer);
            mTextViewCurrentTurn = (TextView) v.findViewById(R.id
                    .txt_multiplayer_current_turn);
            mTextViewOpponentName = (TextView) v.findViewById(R.id
                    .txt_opponent_name);
            mTextViewWhosTurn = (TextView) v.findViewById(R.id
                    .txt_multiplayer_whos_turn);
            mBtnUndoLeaveGame = (Button) v.findViewById(R.id.btn_undo_leave_game);


        }

        void clearAnimation() {

            mContainer.clearAnimation();
        }
    }
}
