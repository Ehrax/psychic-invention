package de.in.uulm.map.quartett.multiplayer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.util.MetricsUtils;

/**
 * Created by maxka on 29.01.2017.
 */

public class MultiplayerFragment extends Fragment implements
        MultiplayerContract.View, View.OnClickListener {

    private MultiplayerContract.Presenter mPresenter;
    private ProgressBar mProgressBarMain;
    private RecyclerView mRecyclerViewCurrentGames;
    private FloatingActionButton mFAB;

    private RecyclerView.Adapter mAdapter;

    private MultiplayerContract.Model mModel;


    public static MultiplayerFragment newInstance() {

        return new MultiplayerFragment();
    }

    @Override
    public void setDelayedEnterTransition(boolean delayedEnterTransition) {

    }

    @Override
    public void setPresenter(MultiplayerContract.Presenter presenter) {

        mPresenter = presenter;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {

        mAdapter = adapter;
        mModel = (CurrentGamesAdapter)adapter;
    }

    @Override
    public void onPause() {

        super.onPause();
        //mPresenter.releaseAllBuffer();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_multiplayer, container,
                false);

        mProgressBarMain = (ProgressBar) view.findViewById(R.id
                .progress_bar_multiplayer_main);
        mRecyclerViewCurrentGames = (RecyclerView) view.findViewById(R.id
                .recycler_view_multiplayer);
        setUpRecyclerView();

        mFAB = (FloatingActionButton) view.findViewById(R.id.fab_multiplayer);
        mFAB.setOnClickListener(this);

        return view;
    }

    private void setUpRecyclerView() {

        mRecyclerViewCurrentGames.setLayoutManager(new LinearLayoutManager
                (getContext()));
        mRecyclerViewCurrentGames.setAdapter(mAdapter);

        setUpItemTouchHelper();
    }

    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                    Drawable mBackground;
                    Drawable mDismissDrawable;
                    int mDismissMargin;
                    boolean mInitiated;

                    private void init() {

                        mBackground = new ColorDrawable(Color.RED);
                        mDismissDrawable = ContextCompat.getDrawable
                                (getContext(), R.drawable.ic_delete);
                        mDismissMargin = (int) getContext().getResources()
                                .getDimension(R.dimen.dissmiss_game_margin);
                        mInitiated = true;
                    }

                    //not important because we don´t want drag and drop
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                        return false;
                    }

                    @Override
                    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

                        int position = viewHolder.getAdapterPosition();
                        CurrentGamesAdapter adapter = (CurrentGamesAdapter)
                                recyclerView.getAdapter();
                        if (adapter.isPendingRemoval(position)) {
                            return 0;
                        }
                        return super.getSwipeDirs(recyclerView, viewHolder);
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                        int swipedPosition = viewHolder.getAdapterPosition();
                        CurrentGamesAdapter adapter = (CurrentGamesAdapter)
                                mRecyclerViewCurrentGames.getAdapter();
                        adapter.pendingRemoval(swipedPosition);
                    }

                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                        View itemView = viewHolder.itemView;

                        //this method get´s called for view holder that are
                        // already swiped away so we ignore them
                        if (viewHolder.getAdapterPosition() == -1) {
                            return;
                        }

                        if (!mInitiated) {
                            init();
                        }

                        mBackground.setBounds(itemView.getRight() + (int) dX,
                                itemView.getTop(), itemView.getRight(),
                                itemView.getBottom());
                        mBackground.draw(c);

                        int itemHeight = itemView.getHeight();
                        int intrinsicWidth = mDismissDrawable.getIntrinsicWidth();
                        int intrinsicHeight = mDismissDrawable.getIntrinsicHeight();

                        int xMarkLeft = itemView.getRight() - mDismissMargin -
                                intrinsicWidth;
                        int xMarkRight = itemView.getRight() - mDismissMargin;
                        int xMarkTop = itemView.getTop()+(itemHeight/2)
                                -(intrinsicHeight/2);
                        int xMarkBottom = xMarkTop + intrinsicHeight;
                        mDismissDrawable.setBounds(xMarkLeft, xMarkTop, xMarkRight,
                                xMarkBottom);

                        mDismissDrawable.draw(c);


                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerViewCurrentGames);
    }

    @Override
    public void makeSnackBar(String message) {

        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void replaceProgressBar() {

        mProgressBarMain.setVisibility(View.GONE);
        mRecyclerViewCurrentGames.setVisibility(View.VISIBLE);
        mFAB.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fab_multiplayer:
                mPresenter.startSelectOpponent();
                break;
            default:
                break;
        }
    }
}
