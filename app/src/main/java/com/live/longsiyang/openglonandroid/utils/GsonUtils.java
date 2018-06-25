package com.live.longsiyang.openglonandroid.utils;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

/**
 * Created by oceanlong on 2018/6/25.
 */

public class GsonUtils {

    public static <T> List<T> jsonToList(String json, Class<T[]> clazz)
    {
        Gson gson = new Gson();
        T[] array = gson.fromJson(json, clazz);
        return Arrays.asList(array);
    }
}
