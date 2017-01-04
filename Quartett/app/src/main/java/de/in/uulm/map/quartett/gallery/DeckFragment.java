package de.in.uulm.map.quartett.gallery;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bartoszlipinski.flippablestackview.FlippableStackView;
import com.bartoszlipinski.flippablestackview.StackPageTransformer;

import de.in.uulm.map.quartett.R;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by maxka on 25.12.2016. This Fragment displays all cards of a given
 * deck in a animated stack view.
 */

public class DeckFragment extends Fragment implements GalleryContract.View {

    private GalleryContract.Presenter mPresenter;
    private long currentDeckID;

    private FlippableStackView mFlippableStack;
    private CardFragmentAdapter mCardFragmentAdapter;
    private List<Fragment> mDeckCards;

    public static DeckFragment newInstance() {

        return new DeckFragment();
    }

    public void setCurrentDeckID(long currentDeckID) {

        this.currentDeckID = currentDeckID;
    }

    @Override
    public void setPresenter(@NonNull GalleryContract.Presenter presenter) {

        mPresenter = checkNotNull(presenter);
    }


    @Override
    public void onActivityCreated(Bundle savedInstance) {

        super.onActivityCreated(savedInstance);

        mDeckCards = mPresenter.createCardFragments(currentDeckID);

        mCardFragmentAdapter = new CardFragmentAdapter(getActivity()
                .getSupportFragmentManager(), mDeckCards);

        /*
        Initialise the FlippableStackView witch holds the cards
         */
        mFlippableStack = (FlippableStackView) getActivity().findViewById(R
                .id.deck_stack_view);
        boolean portrait = getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT;
        mFlippableStack.initStack(mDeckCards.size(), portrait ?
                StackPageTransformer.Orientation.VERTICAL : StackPageTransformer.Orientation.HORIZONTAL);
        mFlippableStack.setAdapter(mCardFragmentAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_deck, container, false);
    }

    /**
     * Adapter for the FlippableStackView
     */
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
