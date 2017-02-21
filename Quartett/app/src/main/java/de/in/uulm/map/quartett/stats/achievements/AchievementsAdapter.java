package de.in.uulm.map.quartett.stats.achievements;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Achievement;
import de.in.uulm.map.quartett.stats.StatsContract;
import de.in.uulm.map.quartett.views.BetterArcProgress;

import java.util.List;

/**
 * Created by alexanderrasputin on 15.01.17.
 */

public class AchievementsAdapter extends RecyclerView
        .Adapter<AchievementsAdapter.ViewHolder> {

    /**
     * reference to Achievement presenter
     */
    private StatsContract.AchievementsPresenter mPresenter;

    /**
     * stores all achievements
     */
    private List<Achievement> mAchievements;

    /**
     * stores the context for easy access
     */
    private Context mContext;


    public AchievementsAdapter(StatsContract.AchievementsPresenter presenter,
                               Context mContext) {

        this.mPresenter = presenter;
        this.mContext = mContext;

        mAchievements = presenter.getAchievements();
    }

    /**
     * inflating a layout from XML and returning the holder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View achievementRow = inflater.inflate(R.layout.achievement_row,
                parent, false);

        return new ViewHolder(achievementRow);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Achievement achievement = mAchievements.get(position);

        mPresenter.setAchievementDonut(holder.mProgress, achievement);
        mPresenter.setAchievementRowTitle(holder.mTitle, achievement);
        mPresenter.setAchievementRowDescription(holder.mDescription, achievement);
    }

    @Override
    public int getItemCount() {

        return mAchievements.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public BetterArcProgress mProgress;
        public TextView mTitle;
        public TextView mDescription;

        public ViewHolder(View itemView) {

            super(itemView);
            mProgress = (BetterArcProgress) itemView.findViewById
                    (R.id.achievement_row_circle);
            mTitle = (TextView) itemView.findViewById
                    (R.id.achievement_row_title);
            mDescription = (TextView) itemView.findViewById
                    (R.id.achievement_row_description);
        }
    }

}
