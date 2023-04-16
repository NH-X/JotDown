package com.example.jotdown.utils;

import android.util.Log;
import android.view.Menu;
import android.view.Window;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MenuUtil {
    //显示OverflowMenu的Icon
    public static void setOverflowIconVisible(int featureId, Menu menu) {
        //ActionBar的featureId是8，Toolbar的featureId是108
        if (featureId % 100 == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                Log.d(MenuUtil.class.getName(), "setOverflowIconVisible: ");
                try {
                    //setOptionalIconsVisible是个隐藏方法方法，需要通过反射机制调用
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}