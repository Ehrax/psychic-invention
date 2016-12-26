package de.in.uulm.map.quartett.gallery;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.in.uulm.map.quartett.R;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by maxka on 25.12.2016.
 */

public class DeckFragment extends Fragment implements GalleryContract.View {

    private GalleryContract.Presenter mPresenter;

    public static DeckFragment newInstance() {

        return new DeckFragment();
    }

    @Override
    public void setPresenter(@NonNull GalleryContract.Presenter presenter) {

        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {

        super.onActivityCreated(savedInstance);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_deck, container, false);
    }

    private class CardFragmentAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public CardFragmentAdapter(FragmentManager fm, List<Fragment>
                fragments) {

            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {

            return this.fragments.get(position);
        }

        @Override
        public int getCount() {

            return this.fragments.size();
        }
    }
}
