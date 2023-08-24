package com.example.jotdown.utils;

/**
 * @author NH-X
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Chinese: 将数据转换为json语句或将json语句转换为具体数据
 *
 * English: Convert data to json statements or convert json statements to concrete data
 */
public class JsonUtil {
    private static final String TAG = "JsonUtil";

    public static <T> String dataToJson(List<T> dataList){
        JSONArray jsonArray = new JSONArray();

        try{
            for (T item : dataList){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data",item.toString());
                jsonArray.put(jsonObject);
            }

            return jsonArray.toString();
        } catch (JSONException e) {
            Log.d(TAG, "dataToJson: "+e.getMessage());

            return "";
        }
    }
}
