package de.in.uulm.map.quartett.gallery;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.transition.AutoTransition;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ProgressBar;

import com.bartoszlipinski.flippablestackview.FlippableStackView;
import com.bartoszlipinski.flippablestackview.StackPageTransformer;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.CardImage;
import de.in.uulm.map.quartett.data.Image;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by maxka on 25.12.2016. This Fragment displays all cards of a given
 * deck in a animated stack view.
 */

public class DeckFragment extends Fragment implements GalleryContract.SubView {

    private GalleryContract.Presenter mPresenter;
    private long currentDeckID;

    private FlippableStackView mFlippableStack;
    private CardFragmentAdapter mCardFragmentAdapter;

    public static DeckFragment newInstance() {

        return new DeckFragment();
    }

    /**
     * This method should be called after you created a new instance of deck
     * fragment to set the shown decks id.
     *
     * @param currentDeckID id of the deck you want to show.
     */
    public void setCurrentDeckID(long currentDeckID) {

        this.currentDeckID = currentDeckID;
    }

    @Override
    public void setPresenter(@NonNull GalleryContract.Presenter presenter) {

        mPresenter = checkNotNull(presenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_deck, container, false);
        mFlippableStack = (FlippableStackView) view.findViewById(R
                .id.deck_stack_view);

        final int cardsLength = (int) Card.count(
                Card.class,
                "m_deck = ?", new
                String[]{"" + currentDeckID});

        mCardFragmentAdapter =
                new CardFragmentAdapter(getChildFragmentManager(), cardsLength);

        mFlippableStack.initStack(2,
                StackPageTransformer.Orientation.VERTICAL);

        mFlippableStack.setAdapter(mCardFragmentAdapter);

        return view;
    }

    /**
     * This method should be used to create a Dialog that shows the description
     * of an Image from a Card.
     *
     * @param image the CardImage to take the description from
     */
    @Override
    public void showImageDescription(Image image) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.image_description_title);
        builder.setMessage(image.mDescription);
        builder.setPositiveButton("OK", null);

        builder.show();
    }

    /**
<<<<<<< HEAD
=======
     * This AsyncTask is used to load the cards from the database and build a
     * fragment for each card. After the heavy lifting is done this class
     * initializes the FlippableStack and removes the progress bar.
     */
    public class AsyncDeckInitializer extends AsyncTask<Long, Void, Long> {

        /**
         * Building a card fragment for each card in the given deck.
         *
         * @param params just the deck id
         * @return if the task was canceled return is null otherwise its the
         * deck id
         */
        @Override
        protected Long doInBackground(Long... params) {

            mDeckCards = mPresenter.createDummyList(params[0]);
            return isCancelled() ? null : params[0];
        }

        /**
         * This method checks if the task was canceled and if not it removes the
         * progress bar and initializes the flippable stack view.
         *
         * @param deckID the id of the currently loading deck
         */
        @Override
        protected void onPostExecute(Long deckID) {

            if (deckID != null && !isCancelled()) {
                mCardFragmentAdapter = new CardFragmentAdapter(getChildFragmentManager(), mDeckCards);
                ViewGroup rootView = (ViewGroup) getActivity().findViewById(R
                        .id.linear_layout_deck_fragment);
                //animating the removing of the progressbar
                AutoTransition transition =new AutoTransition();
                TransitionManager.beginDelayedTransition(rootView,transition);
                //removing the progress bar
                ProgressBar progressBar = (ProgressBar) getActivity()
                        .findViewById(R.id.progress_bar_deck);
                rootView.removeView(progressBar);
                View placeHolderView = getActivity().findViewById(R.id
                        .deck_placeholder_view);
                rootView.removeView(placeHolderView);

                //initialising the flippable stack view
                mFlippableStack.initStack(mDeckCards.size(),
                        StackPageTransformer.Orientation.VERTICAL);
                mFlippableStack.setAdapter(mCardFragmentAdapter);
            }

        }
    }

    /**
>>>>>>> develop
     * Adapter for the FlippableStackView
     */
    private class CardFragmentAdapter extends FragmentPagerAdapter {

        private final int mSize;

        public CardFragmentAdapter(FragmentManager fm, int size) {

            super(fm);

            mSize = size;
        }

        @Override
        public Fragment getItem(int position) {

            CardFragment fragment = new CardFragment();
            fragment.setPresenter(mPresenter);
            fragment.setDeckId(currentDeckID);
            fragment.setPosition(position);

            return fragment;
        }

        @Override
        public int getCount() {

            return mSize;
        }
    }
}
