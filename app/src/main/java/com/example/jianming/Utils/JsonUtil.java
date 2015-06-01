package com.example.jianming.Utils;

import com.example.jianming.annotations.JsonName;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by knightingal on 15-5-18.
 */
public class JsonUtil {
    private static final String TAG = "JsonUtil";
    public static JSONObject getJson(Object object) {
        JSONObject json = new JSONObject();
        Class<?> objClass = object.getClass();
        Field[] fields = objClass.getDeclaredFields();
        for(Field field: fields) {
            //Log.d(TAG, field.getName());
            String fieldName = field.getName();
            String outputName = fieldName;
            if (field.isAnnotationPresent(JsonName.class)) {
                JsonName jsonName = field.getAnnotation(JsonName.class);
                outputName = jsonName.value();
            }
            String fieldGetterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            try {
                Method getter = objClass.getMethod(fieldGetterName);
                Object ret = getter.invoke(object);
                json.put(outputName, ret);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return json;
    }
}
