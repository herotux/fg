package org.telegram.felegram.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by reza on 4/21/2016 AD.
 */
public class Utils {
    public static boolean isPackageInstalled(Context context, String packageName){
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        }catch (PackageManager.NameNotFoundException e){
            return false;
        }
    }
}
