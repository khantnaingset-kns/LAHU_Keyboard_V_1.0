package com.codealchemy.lahu_keyboard_v_10;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;

import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.text.method.MetaKeyKeyListener;
import android.util.DisplayMetrics;

import android.util.Log;
import android.view.Display;

import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import android.widget.EditText;
import android.widget.LinearLayout;

import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconsPopup;


/**
 * Created by Khant Naing Set on 1/20/2017.
 */

public class LahuKeyboardIME extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private LahuKeyboardView mKeyboardView;
    private LahuKeyboard lkeyboard;
    private LahuKeyboard mEmojiKeyboarda1;
    private LahuKeyboard mEmojiKeyboarda2;
    private LahuKeyboard mEmojiKeyboarda3;
    private LahuKeyboard mEmojiKeyboarda4;
    private LahuKeyboard mEmojiKeyboardb1;
    private LahuKeyboard mEmojiKeyboardb2;
    private LahuKeyboard mEmojiKeyboardc1;
    private LahuKeyboard mEmojiKeyboardc2;
    private LahuKeyboard mEmojiKeyboardc3;
    private LahuKeyboard mEmojiKeyboardc4;
    private LahuKeyboard mEmojiKeyboardc5;
    private LahuKeyboard mEmojiKeyboardd1;
    private LahuKeyboard mEmojiKeyboardd2;
    private LahuKeyboard mEmojiKeyboardd3;
    private LahuKeyboard mEmojiKeyboarde1;
    private LahuKeyboard mEmojiKeyboarde2;
    private LahuKeyboard mEmojiKeyboarde3;
    private LahuKeyboard mEmojiKeyboarde4;
    private LahuKeyboard symbolkeyboard;
    private LahuKeyboard thaikeyboard;
    private LahuKeyboard thaishiftkeyboard;
    private LahuKeyboard symbolshiftkeyboard;
    private LahuKeyboard mmkeyboard;
    private LahuKeyboard mmsymbolkeyboard;
    private LahuKeyboard mmshiftkeyboard;
    private boolean caps = false;
    private boolean mCompletionOn;
    private CandidateView mCandidateView;
    private boolean mCapsLock;
    private StringBuilder mComposing;
    private LahuKeyboard mCurkeyboard;
    private long mMetaState;
    private boolean mPredictionOn;
    private long mLastShiftTime;
    private String mWordSeparators;

    private InputMethodManager minputMethodManager;
    private int mLastDisplayWidth;
    private CompletionInfo[] mCompletions;


    public LahuKeyboardIME() {
        this.mComposing = new StringBuilder();
    }

    private void checkToggleCapsLock() {
        long now = System.currentTimeMillis();
        if (this.mLastShiftTime + 800 > now) {
            this.mCapsLock = !this.mCapsLock;
            this.mLastShiftTime = 0;
        } else {
            this.mLastShiftTime = now;
        }
    }

    private String getWordSeparators() {
        return this.mWordSeparators;
    }


    private boolean isAlphabet(int code) {
        return Character.isLetter(code);
    }


    private void handleShift() {
        if (this.mKeyboardView == null) {
            return;
        }

        Keyboard currentKeyboard = this.mKeyboardView.getKeyboard();
        if (this.lkeyboard == currentKeyboard) {

            this.checkToggleCapsLock();
            this.mKeyboardView.setShifted(this.caps || !this.mKeyboardView.isShifted());

        } else if (currentKeyboard == this.symbolkeyboard) {

            this.symbolkeyboard.setShifted(true);
            this.mKeyboardView.setKeyboard(this.symbolshiftkeyboard);
            this.symbolshiftkeyboard.setShifted(true);

        } else if (currentKeyboard == this.symbolshiftkeyboard) {

            this.symbolshiftkeyboard.setShifted(false);
            this.mKeyboardView.setKeyboard(this.symbolkeyboard);
            this.symbolkeyboard.setShifted(false);

        }
    }


    private void showOptionsMenu() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showInputMethodPicker();
    }

    private boolean translateKeyDown(int keyCode, KeyEvent event) {
        this.mMetaState = MetaKeyKeyListener.handleKeyDown(this.mMetaState, keyCode, event);
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(this.mMetaState));
        this.mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
        InputConnection ic = this.getCurrentInputConnection();

        if (c == 0 || ic == null) {
            return false;
        }

        boolean dead = false;

        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
            dead = true;
            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        }

        if (this.mComposing.length() > 0) {
            char accent = this.mComposing.charAt(this.mComposing.length() - 1);
            int composed = KeyEvent.getDeadChar(accent, c);

            if (composed != 0) {
                c = composed;
                this.mComposing.setLength(this.mComposing.length() - 1);
            }
        }

        this.onKey(c, null);

        return true;
    }

    public boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        char word = (char) code;
        return separators.contains(String.valueOf(word));
    }


    @Override
    public void onInitializeInterface() {
        if (this.lkeyboard != null) {
            int displayWidth = getMaxWidth();

            if (displayWidth == mLastDisplayWidth) {
                return;
            }

            mLastDisplayWidth = displayWidth;
        }
        this.lkeyboard = new LahuKeyboard(this, R.xml.qwerty);
        this.mmkeyboard = new LahuKeyboard(this, R.xml.mm_lahu);
        this.thaikeyboard = new LahuKeyboard(this, R.xml.th_normal);

        this.symbolkeyboard = new LahuKeyboard(this, R.xml.symbol);
        this.symbolshiftkeyboard = new LahuKeyboard(this, R.xml.symbol_shift);
        this.mmsymbolkeyboard = new LahuKeyboard(this, R.xml.mm_lahu_symbol);
        this.mmshiftkeyboard = new LahuKeyboard(this, R.xml.mm_lahul_shift);
        this.thaishiftkeyboard = new LahuKeyboard(this, R.xml.th_shift);

        this.mEmojiKeyboarda1 = new LahuKeyboard(this, R.xml.emoji_a1);
        this.mEmojiKeyboarda2 = new LahuKeyboard(this, R.xml.emoji_a2);
        this.mEmojiKeyboarda3 = new LahuKeyboard(this, R.xml.emoji_a3);
        this.mEmojiKeyboarda4 = new LahuKeyboard(this, R.xml.emoji_a4);

        this.mEmojiKeyboardb1 = new LahuKeyboard(this, R.xml.emoji_b1);
        this.mEmojiKeyboardb2 = new LahuKeyboard(this, R.xml.emoji_b2);

        this.mEmojiKeyboardc1 = new LahuKeyboard(this, R.xml.emoji_c1);
        this.mEmojiKeyboardc2 = new LahuKeyboard(this, R.xml.emoji_c2);
        this.mEmojiKeyboardc3 = new LahuKeyboard(this, R.xml.emoji_c3);
        this.mEmojiKeyboardc4 = new LahuKeyboard(this, R.xml.emoji_c4);
        this.mEmojiKeyboardc5 = new LahuKeyboard(this, R.xml.emoji_c5);

        this.mEmojiKeyboardd1 = new LahuKeyboard(this, R.xml.emoji_d1);
        this.mEmojiKeyboardd2 = new LahuKeyboard(this, R.xml.emoji_d2);
        this.mEmojiKeyboardd3 = new LahuKeyboard(this, R.xml.emoji_d3);

        this.mEmojiKeyboarde1 = new LahuKeyboard(this, R.xml.emoji_e1);
        this.mEmojiKeyboarde2 = new LahuKeyboard(this, R.xml.emoji_e2);
        this.mEmojiKeyboarde3 = new LahuKeyboard(this, R.xml.emoji_e3);
        this.mEmojiKeyboarde4 = new LahuKeyboard(this, R.xml.emoji_e4);

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateInputView() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String color = sharedPreferences.getString("colorSelector", "NULL");

        mKeyboardView = (LahuKeyboardView) getLayoutInflater().inflate(R.layout.lahu_keyboard_layout, null);
        this.mWordSeparators = getResources().getString(R.string.word_separators);
        lkeyboard = new LahuKeyboard(this, R.xml.qwerty);

        if (color.equals("grey")) {
            mKeyboardView.setBackgroundColor(getResources().getColor(R.color.grey));

        } else if (color.equals("dark_grey")) {
            mKeyboardView.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        } else if (color.equals("cuttie_pink")) {
            mKeyboardView.setBackgroundColor(getResources().getColor(R.color.cuttie_pink));
        } else if (color.equals("material_blue")) {
            mKeyboardView.setBackgroundColor(getResources().getColor(R.color.material_blue));
        } else if (color.equals("material_green")) {
            mKeyboardView.setBackgroundColor(getResources().getColor(R.color.material_green));
        }


        mKeyboardView.setKeyboard(lkeyboard);

        mKeyboardView.setOnKeyboardActionListener(this);
        return mKeyboardView;
    }


    private void playClick(int keyCode) {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        switch (keyCode) {
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);

        this.mComposing.setLength(0);


        if (!restarting) {
            this.mMetaState = 0;
        }

        this.mPredictionOn = false;
        this.mCompletionOn = false;
        this.mCompletions = null;

        switch (attribute.inputType & EditorInfo.TYPE_MASK_CLASS) {
            case EditorInfo.TYPE_CLASS_NUMBER:        // 2
                this.mCurkeyboard.setImeOptions(getResources(), attribute.imeOptions);
                break;
            case EditorInfo.TYPE_CLASS_DATETIME:    // 4
                this.mCurkeyboard = this.symbolkeyboard;
                break;
            case EditorInfo.TYPE_CLASS_PHONE:        // 3
                this.mCurkeyboard = this.symbolkeyboard;
                break;
            case EditorInfo.TYPE_CLASS_TEXT:        // 1
                this.mCurkeyboard = this.lkeyboard;
                this.mPredictionOn = true;

                int variation = attribute.inputType & EditorInfo.TYPE_MASK_VARIATION;
                if (variation == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD || variation == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    this.mPredictionOn = false;
                }

                if (variation == EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS || variation == EditorInfo.TYPE_TEXT_VARIATION_URI || variation == EditorInfo.TYPE_TEXT_VARIATION_FILTER) {
                    this.mPredictionOn = false;
                }

                if ((attribute.inputType & EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE) != 0) {
                    this.mPredictionOn = false;
                    this.mCompletionOn = this.isFullscreenMode();
                }


                break;
            default:
                this.mCurkeyboard = this.lkeyboard;

                break;
        }
    }

    @Override
    public View onCreateCandidatesView() {
        this.mCandidateView = new CandidateView(this);
        this.mCandidateView.setService(this);

        return this.mCandidateView;
    }

    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        this.mKeyboardView.setKeyboard(this.mCurkeyboard);
        this.mKeyboardView.closing();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String color = sharedPreferences.getString("colorSelector", "NULL");

        if (color.equals("grey")) {
            mKeyboardView.setBackgroundColor(getResources().getColor(R.color.grey));

        } else if (color.equals("dark_grey")) {
            mKeyboardView.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        } else if (color.equals("cuttie_pink")) {
            mKeyboardView.setBackgroundColor(getResources().getColor(R.color.cuttie_pink));
        } else if (color.equals("material_blue")) {
            mKeyboardView.setBackgroundColor(getResources().getColor(R.color.material_blue));
        } else if (color.equals("material_green")) {
            mKeyboardView.setBackgroundColor(getResources().getColor(R.color.material_green));
        } else if (color.equals("material_brown")) {
            mKeyboardView.setBackgroundColor(getResources().getColor(R.color.material_brown));
        }
    }


    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }


    private void changeEmojiKeyboard(LahuKeyboard[] emojiKeyboard) {
        int j = 0;
        for (int i = 0; i < emojiKeyboard.length; i++) {
            if (emojiKeyboard[i] == this.mKeyboardView.getKeyboard()) {
                j = i;
                break;
            }
        }

        if (j + 1 >= emojiKeyboard.length) {
            this.mKeyboardView.setKeyboard(emojiKeyboard[0]);
        } else {
            this.mKeyboardView.setKeyboard(emojiKeyboard[j + 1]);
        }
    }

    private void changeEmojiKeyboardReverse(LahuKeyboard[] emojiKeyboard) {
        int j = emojiKeyboard.length - 1;
        for (int i = emojiKeyboard.length - 1; i >= 0; i--) {
            if (emojiKeyboard[i] == this.mKeyboardView.getKeyboard()) {
                j = i;
                break;
            }
        }

        if (j - 1 < 0) {
            this.mKeyboardView.setKeyboard(emojiKeyboard[emojiKeyboard.length - 1]);
        } else {
            this.mKeyboardView.setKeyboard(emojiKeyboard[j - 1]);
        }
    }

    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    private void sendKey(int keyCode) {
        switch (keyCode) {
            case '\n':
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                if (keyCode >= '0' && keyCode <= '9') {
                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
                } else {
                    getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
                }
                break;
        }
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        am.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
                AudioManager.VIBRATE_SETTING_ON);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean vibrate = sharedPreferences.getBoolean("prefVirbateOn", false);

        playClick(primaryCode);


        if (this.isWordSeparator(primaryCode)) {

        }
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;
            case -93:
                caps = !caps;
                mKeyboardView.setShifted(caps);
                mKeyboardView.invalidateAllKeys();
                break;

            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.KEYCODE_SEARCH,KeyEvent.KEYCODE_ENTER));

                break;
            case -101:
                mKeyboardView.setKeyboard(mmkeyboard);
                break;
            case -102:
                mKeyboardView.setKeyboard(lkeyboard);
                break;
            case -133:
                mKeyboardView.setKeyboard(thaikeyboard);
                break;
            case -110:
                mKeyboardView.setKeyboard(symbolkeyboard);
                break;
            case -64:
                mKeyboardView.setKeyboard(lkeyboard);
                break;
            case -65:
                mKeyboardView.setKeyboard(symbolshiftkeyboard);
                break;
            case -67:
                mKeyboardView.setKeyboard(symbolkeyboard);
                break;
            case -40:
                mKeyboardView.setKeyboard(mmsymbolkeyboard);
                break;
            case -90:
                mKeyboardView.setKeyboard(mmkeyboard);
                break;
            case -22:
                mKeyboardView.setKeyboard(mmshiftkeyboard);
                break;
            case -43:
                mKeyboardView.setKeyboard(mmkeyboard);
                break;
            case -35:
                mKeyboardView.setKeyboard(thaishiftkeyboard);
                break;
            case -36:
                mKeyboardView.setKeyboard(thaikeyboard);
                break;
            case -37:
                changeEmojiKeyboard(new LahuKeyboard[]{
                        this.mEmojiKeyboarda1,
                        this.mEmojiKeyboarda2,
                        this.mEmojiKeyboarda3,
                        this.mEmojiKeyboarda4,
                        this.mEmojiKeyboardb1,
                        this.mEmojiKeyboardb2,
                        this.mEmojiKeyboardc1,
                        this.mEmojiKeyboardc2,
                        this.mEmojiKeyboardc3,
                        this.mEmojiKeyboardc4,
                        this.mEmojiKeyboardc5,
                        this.mEmojiKeyboardd1,
                        this.mEmojiKeyboardd2,
                        this.mEmojiKeyboardd3,
                        this.mEmojiKeyboarde1,
                        this.mEmojiKeyboarde2,
                        this.mEmojiKeyboarde3,
                        this.mEmojiKeyboarde4
                });
                break;

            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }
                if (vibrate) {
                    Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(100);
                }

                ic.commitText(String.valueOf(code), 1);


        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {
        Log.d("Main", "swipe left");
        this.changeEmojiKeyboard(new LahuKeyboard[]{
                this.lkeyboard, this.mmkeyboard, this.mmshiftkeyboard, this.thaikeyboard,
                this.mEmojiKeyboarda1, this.mEmojiKeyboarda2, this.mEmojiKeyboarda3, this.mEmojiKeyboarda4,
                this.mEmojiKeyboardb1, this.mEmojiKeyboardb2,
                this.mEmojiKeyboardc1, this.mEmojiKeyboardc2, this.mEmojiKeyboardc3, this.mEmojiKeyboardc4, this.mEmojiKeyboardc5,
                this.mEmojiKeyboardd1, this.mEmojiKeyboardd2, this.mEmojiKeyboardd3,
                this.mEmojiKeyboarde1, this.mEmojiKeyboarde2, this.mEmojiKeyboarde3, this.mEmojiKeyboarde4,
        });
    }

    @Override
    public void swipeRight() {
        Log.d("Main", "swipe right");
        this.changeEmojiKeyboardReverse(new LahuKeyboard[]{
                this.lkeyboard, this.mmkeyboard, this.mmshiftkeyboard, this.thaikeyboard,
                this.mEmojiKeyboarda1, this.mEmojiKeyboarda2, this.mEmojiKeyboarda3, this.mEmojiKeyboarda4,
                this.mEmojiKeyboardb1, this.mEmojiKeyboardb2,
                this.mEmojiKeyboardc1, this.mEmojiKeyboardc2, this.mEmojiKeyboardc3, this.mEmojiKeyboardc4, this.mEmojiKeyboardc5,
                this.mEmojiKeyboardd1, this.mEmojiKeyboardd2, this.mEmojiKeyboardd3,
                this.mEmojiKeyboarde1, this.mEmojiKeyboarde2, this.mEmojiKeyboarde3, this.mEmojiKeyboarde4,
        });
    }

    private IBinder getToken() {
        final Dialog dialog = getWindow();
        if (dialog == null) {
            return null;
        }
        final Window window = dialog.getWindow();
        if (window == null) {
            return null;
        }
        return window.getAttributes().token;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void handleLanguageswitch() {
        minputMethodManager.switchToNextInputMethod(getToken(), false);
    }


    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    public void restartIME() {
        this.onFinishInput();
    }


}
