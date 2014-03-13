package com.example.GitHub;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.Android.R;

/**
 * Created by wouter on 10/03/14.
 */
public class SearchOpponentFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_opponent_fragment, container, false);
    }

    public void searchRandomOpponent(View view) {

    }
}