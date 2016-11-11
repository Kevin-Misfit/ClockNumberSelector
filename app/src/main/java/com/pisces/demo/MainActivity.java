package com.pisces.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.pisces.demo.commons.widget.ClockNumberSelectorView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
