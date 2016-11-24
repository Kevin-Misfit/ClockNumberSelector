package com.pisces.demo;

import android.os.Bundle;
import android.widget.TextView;

import com.pisces.demo.commons.widget.ClockNumberSelectorView;

import java.util.ArrayList;

public class ClockNumberSelectorActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_number_selector);
        setUpToolBar();

        final TextView textView = (TextView) findViewById(R.id.instruction_tv);

        ClockNumberSelectorView clockNumberSelectorView =
                (ClockNumberSelectorView) findViewById(R.id.clock_number_selector);

        clockNumberSelectorView.setOnClockNumberSelectListener(
                new ClockNumberSelectorView.OnClockNumberSelectListener() {
                    @Override
                    public void onNumberSelected(int clockNumber) {
                        textView.setText("Selected number: " + clockNumber);
                    }
                });

        clockNumberSelectorView.updateNumbersAndGetDefaultNumber(12, new ArrayList<Integer>());
        textView.setText("Selected number: " + 12);
    }
}
