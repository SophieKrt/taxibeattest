package skritikou.taxibeattest.Views;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import skritikou.taxibeattest.R;

public class CustomDialogFragment extends Fragment {

    private View dialog;
    private ImageView mBlurBackImg;
    private static String textForView, textForViewSmall;
    private MainActivity mainActivity;


    public CustomDialogFragment() {
        // Required empty public constructor
    }

    public static CustomDialogFragment newInstance(String prompt, String promptSmall) {
        CustomDialogFragment fragment = new CustomDialogFragment();
        textForView = prompt;
        textForViewSmall = promptSmall;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainActivity = ((MainActivity) getActivity());
        dialog = inflater.inflate(R.layout.fragment_custom_dialog, container, false);
        return dialog;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        mBlurBackImg = (ImageView) dialog.findViewById(R.id.blur_back_imageview);
//        mBlurBackImg.setImageDrawable(ContextCompat.getDrawable(mainActivity.getApplicationContext(),R.drawable.backblur));

        if (textForView != null) {
            TextView textCustomized = (TextView) dialog.findViewById(R.id.title_text);
            TextView textCustomizedSmall = (TextView) dialog.findViewById(R.id.reminder_tv);
            textCustomized.setText(textForView);
            textCustomizedSmall.setText(textForViewSmall);
        }
    }
}

