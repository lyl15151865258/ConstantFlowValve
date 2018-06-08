package cn.njmeter.constantflowvalve.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;

/**
 * 软件语言设置工具
 * Created by LiYuliang on 2018/1/11.
 *
 * @author LiYuliang
 * @version 2018/1/11
 */

public class LanguageUtils {

    public static void setLanguageLocal(String language) {
        SharedPreferencesUtils.getInstance().saveData("language", language);
    }

    public static String getLanguageLocal() {
        return (String) SharedPreferencesUtils.getInstance().getData("language", "");
    }

    public static Context attachBaseContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, getLanguageLocal());
        } else {
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Resources resources = context.getResources();
        Locale locale = new Locale(getLanguageLocal());

        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLocales(new LocaleList(locale));
        return context.createConfigurationContext(configuration);
    }
}
