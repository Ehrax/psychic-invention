package de.in.uulm.map.quartett.stats.ranking;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Highscore;
import de.in.uulm.map.quartett.stats.StatsContract;

import java.util.List;

/**
 * Created by alexanderrasputin on 14.01.17.
 */

public class RankingContractsAdapter extends RecyclerView
        .Adapter<RankingContractsAdapter.ViewHolder> {

    /**
     * reference to Ranking Presenter
     */
    private StatsContract.RankingPresenter mPresenter;

    /**
     * store top 10 scores
     */
    private List<Highscore> mScores;

    /**
     * stores the context for easy access
     */
    Context mContext;

    /**
     * basic constructor
     */
    public RankingContractsAdapter(StatsContract.RankingPresenter presenter
            , Context mContext) {

        this.mPresenter = presenter;
        this.mContext = mContext;

        mScores = presenter.getScoreList();
    }

    /**
     * inflating a layout form XML and returning the holder
     */
    @Override
    public RankingContractsAdapter.ViewHolder onCreateViewHolder
    (ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View rankingRow = inflater.inflate(R.layout.ranking_row, parent, false);

        return new ViewHolder(rankingRow);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Highscore score = mScores.get(position);

        mPresenter.setPosView(holder.posView, position);
        mPresenter.setNameView(holder.nameView, score);
        mPresenter.setScoreView(holder.scoreView, score);
    }

    @Override
    public int getItemCount() {

        return mScores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView posView;
        public TextView nameView;
        public TextView scoreView;

        public ViewHolder(View itemView) {

            super(itemView);

            posView = (TextView) itemView.findViewById
                    (R.id.stats_ranking_placing);
            nameView = (TextView) itemView.findViewById
                    (R.id.stats_ranking_name);
            scoreView = (TextView) itemView.findViewById(R.id
                    .stats_ranking_score);
        }
    }
}
