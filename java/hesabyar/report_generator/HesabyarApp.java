package hesabyar.report_generator;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatDelegate;

import java.lang.reflect.Field;
import java.util.Locale;

import io.realm.Realm;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Mahmood_M on 11/01/2018.
 */

public class HesabyarApp extends Application {

    static
    {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        overrideFont(getApplicationContext(), "SERIF", "fonts/sahel_fd.ttf");
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/sahel_fd.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        Realm.init(this);
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        LocaleManager.setNewLocale(this, "fa");
//    }

    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {
        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
