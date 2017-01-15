package de.in.uulm.map.quartett.stats.ranking;

import android.content.Context;
import android.widget.TextView;


import de.in.uulm.map.quartett.data.Highscore;
import de.in.uulm.map.quartett.stats.StatsContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by alexanderrasputin on 11.01.17.
 */

public class RankingPresenter implements StatsContract.RankingPresenter {

    /**
     * Reference to view
     */
    StatsContract.RankingView mView;

    /**
     * Context needed for intent construction
     */
    Context mContext;

    /**
     * all data which is needed in future process for settings views
     */
    private List<Highscore> mScores;

    /**
     * basic constructor
     *
     * @param mView    StatsContract.Ranking view
     * @param mContext contexts of ranking view
     */
    public RankingPresenter(StatsContract.RankingView mView, Context mContext) {

        this.mView = mView;
        this.mContext = mContext;
    }

    /**
     * this method is getting all scores sorting them and setting mScores;
     */
    @Override
    public void start() {

        mScores = Highscore.listAll(Highscore.class);

        /**
         * sorting score
         */
        Collections.sort(mScores, new Comparator<Highscore>() {
            @Override
            public int compare(Highscore o1, Highscore o2) {

                if (o1.mValue == o2.mValue) {
                    return 0;
                }
                return o1.mValue > o2.mValue ? -1 : 1;
            }
        });

        /**
         * getting top 10
         */
        if (mScores.size() > 10) {
            mScores = new ArrayList<>(mScores.subList(0, 10));
        }
    }

    /**
     * this method is called from the adapter to get the score list
     */
    @Override
    public List<Highscore> getScoreList() {

        return mScores;
    }

    /**
     * setting the text view from a specific holder, this method will be called
     * form onBindViewHolder in RankingContract
     *
     * @param textView id/stats_ranking_placing
     * @param pos placing of the score
     */
    @Override
    public void setPosView(TextView textView, int pos) {
        pos++;
        String place = String.valueOf(pos);
        textView.setText(place);
    }

    /**
     * setting the name view from a specific holder, this method will be called
     * form onBindViewHolder in RankingContract
     *
     * @param textView id/stats_ranking_name
     * @param score    score which should be set
     */
    @Override
    public void setNameView(TextView textView, Highscore score) {

        textView.setText(score.mName);
    }

    /**
     * setting the score view from a specific holder, this method will be called
     * form onBindViewHolder in RankingContract
     *
     * @param textView id/stats_ranking_score
     * @param score    score which should be set
     */
    @Override
    public void setScoreView(TextView textView, Highscore score) {

        textView.setText(String.valueOf(score.mValue));
    }
}
