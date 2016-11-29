package com.pisces.demo;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import com.github.lzyzsd.randomcolor.RandomColor;
import com.pisces.demo.commons.utils.TypefaceUtils;
import com.pisces.demo.commons.widget.InstructionView;

import java.util.Locale;
import java.util.Random;

public class InstructionViewActivity extends BaseActivity {


    public static boolean mIsLocaleInAr = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mIsLocaleInAr) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        setContentView(R.layout.activity_instruction_view);
        setUpToolBar();

        final int[] fontType = new int[]{
                TypefaceUtils.TYPEFACE_REGULAR,
                TypefaceUtils.TYPEFACE_LIGHT,
                TypefaceUtils.TYPEFACE_MEDIUM
        };

        final CharSequence[] addArr = getResources()
                .getStringArray(R.array.tmp_instructions);

        final InstructionView instructionView = (InstructionView) findViewById(R.id.lap_instructions);

        findViewById(R.id.add_string).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence[] texts = instructionView.getCharSequence();
                if(texts == null) {
                    texts = new CharSequence[]{};
                }

                CharSequence[] combinedInstructions = new CharSequence[texts.length +
                        addArr.length];

                System.arraycopy(texts, 0, combinedInstructions, 0, texts.length);
                System.arraycopy(addArr, 0, combinedInstructions,
                        texts.length, addArr.length);

                instructionView.setCharSequences(combinedInstructions);
            }
        });

        findViewById(R.id.change_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instructionView.setTextColor(new RandomColor().randomColor());
            }
        });

        findViewById(R.id.change_language).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Resources resources = getResources();
                Configuration config = resources.getConfiguration();
                DisplayMetrics dm = resources.getDisplayMetrics();
                if (!mIsLocaleInAr) {
                    config.locale = new Locale("ar");
                    mIsLocaleInAr = true;
                } else {
                    config.locale = Locale.ENGLISH;
                    mIsLocaleInAr = false;
                }

                resources.updateConfiguration(config, dm);
                reEnterThisPage();
            }
        });

        findViewById(R.id.change_font).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instructionView.setTypeFace(TypefaceUtils
                        .getTypeface(view.getContext(),
                                fontType[new Random().nextInt(fontType.length)]));
            }
        });

    }

    private void reEnterThisPage() {
        Intent intent = new Intent();
        intent.setClass(this, InstructionViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
