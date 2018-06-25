package com.live.longsiyang.openglonandroid.effects.data;

import android.content.Context;

import com.google.gson.Gson;
import com.live.longsiyang.openglonandroid.utils.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by oceanlong on 2018/6/25.
 */

public class EffectDataManager {

    public static List<LocalEffect> getLocalEffectList(Context context , String name){
        List<LocalEffect> effectList = new ArrayList<LocalEffect>();
        String jsonStr = FileUtils.Companion.getStringFromAssets(context, name);
        LocalEffect[] effects = new Gson().fromJson(jsonStr , LocalEffect[].class);
        return Arrays.asList(effects);
    }
}
