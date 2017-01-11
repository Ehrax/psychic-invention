package de.in.uulm.map.quartett.stats.stats;

import android.content.Context;

import de.in.uulm.map.quartett.stats.StatsContract;

/**
 * Created by alexanderrasputin on 11.01.17.
 */

public class StatsPresenter  implements StatsContract.StatsPresenter {

    /**
     * Reference to view
     */
    StatsContract.StatsView mView;

    /**
     * Abstracted reference to the enclosing Activity
     */
    StatsContract.Backend mBackend;

    /**
     * Context needed for intent construction.
     */
    Context mContext;

    StatsPresenter(StatsContract.StatsView view,
                   StatsContract.Backend backend) {
        this.mView = view;
        this.mBackend = backend;
    }

    /**
     * this could be used for further initialization
     */
    @Override
    public void start() {

    }
}
