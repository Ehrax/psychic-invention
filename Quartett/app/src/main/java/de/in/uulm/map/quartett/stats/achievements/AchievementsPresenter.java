package de.in.uulm.map.quartett.stats.achievements;

import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import de.in.uulm.map.quartett.data.Achievement;
import de.in.uulm.map.quartett.stats.StatsContract;

import java.util.List;

/**
 * Created by alexanderrasputin on 11.01.17.
 */

public class AchievementsPresenter implements StatsContract.AchievementsPresenter {

    /**
     * Reference to the view
     */
    StatsContract.AchievementsView mView;

    /**
     * Context needed for intent construction
     */
    Context mContext;

    /**
     * all achievements
     */
    private List<Achievement> mAchievements;

    /**
     * all done achievements
     */
    private List<Achievement> mDoneAchievements;

    public AchievementsPresenter(StatsContract.AchievementsView mView, Context
            mContext) {

        this.mView = mView;
        this.mContext = mContext;
    }

    /**
     * getting all achievements after the view has been loaded
     */
    @Override
    public void start() {

        mAchievements = Achievement.listAll(Achievement.class);

        mDoneAchievements = Achievement.findWithQuery(Achievement.class,
                "SELECT * FROM Achievement WHERE m_Value==m_Target_Value");
    }

    @Override
    public List<Achievement> getAchievements() {

        return mAchievements;
    }

    /**
     * this method is setting the achievement title view
     */
    @Override
    public void setAchievementTitle(TextView textView) {

        String title;
        if (mDoneAchievements.size() == 0) {
            title = 0 + "/" + mAchievements.size() + " unlocked";
        } else {
            title = mDoneAchievements.size() + "/" + mAchievements.size() +
                    " unlocked";
        }

        textView.setText(title);
    }

    /**
     * calculation the progress percentage the user has done in his achievements
     * and setting afterwards the progress bar
     */
    @Override
    public void setAchievementProgress(ProgressBar progress) {

        float percentage;

        if (mAchievements.isEmpty() || mDoneAchievements.isEmpty()) {
            percentage = 0;
        } else {
            percentage = (float) mDoneAchievements.size() / (float)
                    mAchievements.size() * 100;
        }

        progress.setMax(100);
        progress.setProgress((int) percentage);
    }

    /**
     * this method calculates the progress the user has achieved of one
     * achievement and is setting his corresponding view holder
     */
    @Override
    public void setAchievementDonut(DonutProgress donutProgress,
                                    Achievement achievement) {

        float percentage = achievement.mValue / achievement.mTargetValue * 100;

        donutProgress.setProgress((int) percentage);
    }

    /**
     * this method is setting the title of the corresponding view holder
     */
    @Override
    public void setAchievementRowTitle(TextView textView,
                                       Achievement achievement) {

        textView.setText(achievement.mTitle);
    }

    /**
     * ths method is setting the description of the corresponding view holder
     */
    @Override
    public void setAchievementRowDescription(TextView textView,
                                             Achievement achievement) {

        textView.setText(achievement.mDescription);
    }
}
