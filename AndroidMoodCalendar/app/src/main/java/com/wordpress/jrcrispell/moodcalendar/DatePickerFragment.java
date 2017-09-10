package com.wordpress.jrcrispell.moodcalendar;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jrcrispell on 9/10/17.
 */

public class DatePickerFragment extends Fragment {


    public static DatePickerFragment newInstance() {
        return new DatePickerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_date_picker, container, false);
    }
}
