package com.example.jianming.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jianming.myapplication.R;

/**
 * Created by Jianming on 2015/3/9.
 */
public class ScreenSlidePageFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false
        );
        return rootView;
    }
}
