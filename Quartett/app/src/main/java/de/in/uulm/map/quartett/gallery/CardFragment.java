package de.in.uulm.map.quartett.gallery;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import de.in.uulm.map.quartett.R;

/**
 * Created by maxka on 25.12.2016.
 */

public class CardFragment extends Fragment {

    private ImageSwitcher mImageSwitcher;
    private Button btnPrev,btnNext;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        btnNext=(Button) getActivity().findViewById(R.id.btn_next);
        btnPrev=(Button) getActivity().findViewById(R.id.btn_prev);

        mImageSwitcher = (ImageSwitcher) getActivity().findViewById(R.id.img_switcher_card);
        mImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {

                ImageView imgView = new ImageView(getActivity()
                        .getApplicationContext());
                imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imgView.setLayoutParams(new ImageSwitcher.LayoutParams
                        (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                                .LayoutParams.WRAP_CONTENT));
                return imgView;
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mImageSwitcher.setImageResource(R.drawable.ic_cards_playing);
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mImageSwitcher.setImageResource(R.drawable.ic_menu_editor);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_card, container, false);
    }

}
