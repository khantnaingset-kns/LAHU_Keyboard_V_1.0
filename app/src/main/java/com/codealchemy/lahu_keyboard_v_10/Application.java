package com.codealchemy.lahu_keyboard_v_10;

/**
 * Created by Khant Naing Set on 2/4/2017.
 */

public final class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/Lahu_Zawgyi.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Lahu_Zawgyi.ttf");
    /*FontsOverride.setDefaultFont(this, "MONOSPACE", "MyFontAsset2.ttf");
    FontsOverride.setDefaultFont(this, "SERIF", "MyFontAsset3.ttf");
    FontsOverride.setDefaultFont(this, "SANS_SERIF", "MyFontAsset4.ttf");*/
    }
}