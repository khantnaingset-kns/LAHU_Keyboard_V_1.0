package com.codealchemy.lahu_keyboard_v_10;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Khant Naing Set on 2/24/2017.
 */

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingFragment())
                .commit();

    }
}
