package hesabyar.report_generator;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by Mahmood_M on 07/01/2018.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    FileFragment fileFragment;
    HesabFragment hesabFragment;
    MoeinFragment moeinFragment;
    GardeshFragment gardeshFragment;
    BarnamehFragment barnamehFragment;


    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        boolean isRighToLeft = (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault())== View.LAYOUT_DIRECTION_RTL);
        if(isRighToLeft) {
            switch (position) {
                case 0: {
                    if (fileFragment == null) {
                        fileFragment = new FileFragment();
                    }
                    return fileFragment;
                }
                case 1: {
                    if (hesabFragment == null) {
                        hesabFragment = new HesabFragment();
                    }
                    return hesabFragment;
                }
                case 2: {
                    if (moeinFragment == null) {
                        moeinFragment = new MoeinFragment();
                    }
                    return moeinFragment;
                }
                case 3: {
                    if (gardeshFragment == null) {
                        gardeshFragment = new GardeshFragment();
                    }
                    return gardeshFragment;
                }
                case 4: {
                    if (barnamehFragment == null) {
                        barnamehFragment = new BarnamehFragment();
                    }
                    return barnamehFragment;
                }
                default:
                    return null;
            }
        }else{
            switch (position) {
                case 4: {
                    if (fileFragment == null) {
                        fileFragment = new FileFragment();
                    }
                    return fileFragment;
                }
                case 3: {
                    if (hesabFragment == null) {
                        hesabFragment = new HesabFragment();
                    }
                    return hesabFragment;
                }
                case 2: {
                    if (moeinFragment == null) {
                        moeinFragment = new MoeinFragment();
                    }
                    return moeinFragment;
                }
                case 1: {
                    if (gardeshFragment == null) {
                        gardeshFragment = new GardeshFragment();
                    }
                    return gardeshFragment;
                }
                case 0: {
                    if (barnamehFragment == null) {
                        barnamehFragment = new BarnamehFragment();
                    }
                    return barnamehFragment;
                }
                default:
                    return null;
            }
        }
    }

//    @Override
//    public Fragment getItem(int position) {
//        switch(position){
//            case 2 : return new FileFragment();
//            case 1 : return new HesabFragment();
//            case 0 : return new MoeinFragment();
//            default : return null;
//        }
//    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position){
        boolean isRighToLeft = (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault())== View.LAYOUT_DIRECTION_RTL);
        if(isRighToLeft) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.file_fragment_title);
                case 1:
                    return mContext.getString(R.string.hesab_fragment_title);
                case 2:
                    return mContext.getString(R.string.moein_fragment_title);
                case 3:
                    return mContext.getString(R.string.gardesh_fragment_title);
                case 4:
                    return mContext.getString(R.string.barnameh_fragment_title);
                default:
                    return null;
            }
        }else{
            switch (position) {
                case 4:
                    return mContext.getString(R.string.file_fragment_title);
                case 3:
                    return mContext.getString(R.string.hesab_fragment_title);
                case 2:
                    return mContext.getString(R.string.moein_fragment_title);
                case 1:
                    return mContext.getString(R.string.gardesh_fragment_title);
                case 0:
                    return mContext.getString(R.string.barnameh_fragment_title);
                default:
                    return null;
            }
        }
    }
}
