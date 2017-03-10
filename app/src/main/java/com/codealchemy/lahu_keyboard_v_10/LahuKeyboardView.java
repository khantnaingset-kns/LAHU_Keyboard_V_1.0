package com.codealchemy.lahu_keyboard_v_10;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodSubtype;

import java.util.List;
import java.util.Locale;

/**
 * Created by Khant Naing Set on 1/20/2017.
 */

public class LahuKeyboardView extends KeyboardView {

    static final int KEYCODE_LANGUAGE_SWITCH = -101;
    static final int KEYCODE_OPTIONS = -100;

    public LahuKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LahuKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean onLongPress(Keyboard.Key key) {
        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
            getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
            return true;
        /*} else if (key.codes[0] == 113) {

            return true; */
        } else {
            //Log.d("LatinKeyboardView", "KEY: " + key.codes[0]);
            return super.onLongPress(key);
        }
    }

    void setSubtypeOnSpaceKey(final InputMethodSubtype subtype) {
        final LahuKeyboard keyboard = (LahuKeyboard) getKeyboard();
        //keyboard.setSpaceIcon(getResources().getDrawable(subtype.getIconResId()));
        invalidateAllKeys();
    }

    private int[] getScreenSize() {
        int[] i = new int[2];

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        i[0] = width;
        i[1] = height;
        return i;
    }

    @Override
    public void onDraw(Canvas canvas) {
        int paint1keywidth = 0;
        int paint1keyyplus = 0;
        int paint2keywidth = 0;
        int paint2keyyplus = 0;
        super.onDraw(canvas);
        int i[] = getScreenSize();
        AssetManager am = getContext().getApplicationContext().getAssets();
        Typeface typeface = Typeface.createFromAsset(am, String.format(Locale.ENGLISH, "fonts/%s", "Zawgyi_Lahu.ttf"));

        if (i[0] == 720 && i[1] == 1280) {
            paint1keywidth = 30;
            paint1keyyplus = 35;
            paint2keywidth = 2;
            paint2keyyplus = 100;
        } else if (i[0] == 1080 && i[1] == 1920) {
            paint1keywidth = 25;
            paint1keyyplus = 45;
            paint2keywidth = 2;
            paint2keyyplus = 155;
        }
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        Paint paintrow1 = new Paint();
        paint.setTypeface(typeface);

        paintrow1.setTextAlign(Paint.Align.CENTER);
        paintrow1.setTextSize(25);
        paintrow1.setColor(getResources().getColor(R.color.colorAccent));


        Paint paintrow2 = new Paint();

        paintrow2.setTextAlign(Paint.Align.CENTER);
        paintrow2.setTextSize(20);
        paintrow2.setColor(getResources().getColor(R.color.colorAccent));


        List<Keyboard.Key> keys = getKeyboard().getKeys();
        for (Keyboard.Key key : keys) {


            if (key.label != null) {


                if (key.label.equals("q")) {
                    canvas.drawText("1", key.x + (key.width - paint1keywidth), key.y + paint1keyyplus, paintrow1);

                } else if (key.label.equals("w")) {
                    canvas.drawText("2", key.x + (key.width - paint1keywidth), key.y + paint1keyyplus, paintrow1);
                } else if (key.label.equals("e")) {
                    canvas.drawText("3", key.x + (key.width - paint1keywidth), key.y + paint1keyyplus, paintrow1);
                } else if (key.label.equals("r")) {
                    canvas.drawText("4", key.x + (key.width - paint1keywidth), key.y + paint1keyyplus, paintrow1);
                } else if (key.label.equals("t")) {
                    canvas.drawText("5", key.x + (key.width - paint1keywidth), key.y + paint1keyyplus, paintrow1);
                } else if (key.label.equals("y")) {
                    canvas.drawText("6", key.x + (key.width - paint1keywidth), key.y + paint1keyyplus, paintrow1);
                } else if (key.label.equals("u")) {
                    canvas.drawText("7", key.x + (key.width - paint1keywidth), key.y + paint1keyyplus, paintrow1);

                } else if (key.label.equals("i")) {
                    canvas.drawText("8", key.x + (key.width - paint1keywidth), key.y + paint1keyyplus, paintrow1);
                } else if (key.label.equals("o")) {
                    canvas.drawText("9", key.x + (key.width - paint1keywidth), key.y + paint1keyyplus, paintrow1);
                } else if (key.label.equals("p")) {
                    canvas.drawText("0", key.x + (key.width - paint1keywidth), key.y + paint1keyyplus, paintrow1);
                } else if (key.label.equals("a")) {
                    canvas.drawText("@", key.x + (key.width / paint2keywidth), key.y + paint2keyyplus, paintrow2);

                } else if (key.label.equals("s")) {
                    canvas.drawText("$", key.x + (key.width / paint2keywidth), key.y + paint2keyyplus, paintrow2);
                } else if (key.label.equals("d")) {
                    canvas.drawText("/", key.x + (key.width / paint2keywidth), key.y + paint2keyyplus, paintrow2);
                } else if (key.label.equals("f")) {
                    canvas.drawText("&", key.x + (key.width / paint2keywidth), key.y + paint2keyyplus, paintrow2);
                } else if (key.label.equals("g")) {
                    canvas.drawText("'", key.x + (key.width / paint2keywidth), key.y + paint2keyyplus, paintrow2);
                } else if (key.label.equals("h")) {
                    canvas.drawText("±", key.x + (key.width / paint2keywidth), key.y + paint2keyyplus, paintrow2);
                } else if (key.label.equals("j")) {
                    canvas.drawText("|", key.x + (key.width / paint2keywidth), key.y + paint2keyyplus, paintrow2);
                } else if (key.label.equals("k")) {
                    canvas.drawText("[", key.x + (key.width / paint2keywidth), key.y + paint2keyyplus, paintrow2);
                } else if (key.label.equals("l")) {
                    canvas.drawText("]", key.x + (key.width / paint2keywidth), key.y + paint2keyyplus, paintrow2);
                } else if (key.label.equals("z")) {
                    canvas.drawText("?", key.x + (key.width / paint2keywidth), key.y + paint2keyyplus, paintrow2);
                } else if (key.label.equals("x")) {
                    canvas.drawText("*", key.x + (key.width / paint2keywidth), key.y + paint2keyyplus, paintrow2);
                } else if (key.label.equals("c")) {
                    canvas.drawText("-", key.x + (key.width / paint2keywidth), key.y + paint2keyyplus, paintrow2);
                } else if (key.label.equals("v")) {
                    canvas.drawText("+", key.x + (key.width / paint2keywidth), key.y + paint2keyyplus, paintrow2);
                } else if (key.label.equals("b")) {
                    canvas.drawText("=", key.x + (key.width / paint2keywidth), key.y + paint2keyyplus, paintrow2);
                } else if (key.label.equals("n")) {
                    canvas.drawText("<", key.x + (key.width / paint2keywidth), key.y + paint2keyyplus, paintrow2);
                } else if (key.label.equals("m")) {
                    canvas.drawText(">", key.x + (key.width / paint2keywidth), key.y + paint2keyyplus, paintrow2);
                }



            }


        }
    }
}
