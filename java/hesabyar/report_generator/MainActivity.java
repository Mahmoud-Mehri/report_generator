package hesabyar.report_generator;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ViewPagerAdapter pagerAdapter;
    ViewPager viewPager;
    TextView titleTxt;

    NavigationView navView;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeButtonEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setTitle("");

        DrawerLayout drawerlayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerlayout, toolbar, 0, 0);
        drawerToggle.syncState();

        drawerlayout.addDrawerListener(drawerToggle);

        pagerAdapter = new ViewPagerAdapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                titleTxt.setText(pagerAdapter.getPageTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(pagerAdapter);

        titleTxt = findViewById(R.id.titleTxt);

        navView = findViewById(R.id.nav_view);
        navView.inflateMenu(R.menu.navigation_menu);
        navView.setNavigationItemSelectedListener(this);
        navView.setBackgroundColor(Color.WHITE);
//        navView.inflateHeaderView(R.layout.menu_header);

        setNavigationSelectedItem(R.id.nav_menu_addfile);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        ViewPager viewPager = (ViewPager)findViewById(R.id.mainViewPager);
        setNavigationSelectedItem(item.getItemId());

        return true;
    }

    public void setNavigationSelectedItem(int itemId){

        boolean isRighToLeft = (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault())== View.LAYOUT_DIRECTION_RTL);
        DrawerLayout mDrawer = findViewById(R.id.drawer_layout);
        mDrawer.closeDrawer(GravityCompat.START);

        switch(itemId){
            case R.id.nav_menu_addfile : {
                if(isRighToLeft) {
                    viewPager.setCurrentItem(0);
                }else{
                    viewPager.setCurrentItem(4);
                }
                titleTxt.setText(R.string.file_fragment_title);
                navView.setCheckedItem(R.id.nav_menu_addfile);
            } break;
            case R.id.nav_menu_hesab : {
                if(isRighToLeft) {
                    viewPager.setCurrentItem(1);
                }else{
                    viewPager.setCurrentItem(3);
                }
                titleTxt.setText(R.string.hesab_fragment_title);
                navView.setCheckedItem(R.id.nav_menu_hesab);
            } break;
            case R.id.nav_menu_moein : {
                viewPager.setCurrentItem(2);
//                if(isRighToLeft) {
//                    viewPager.setCurrentItem(2);
//                }else{
//                    viewPager.setCurrentItem(2);
//                }
                titleTxt.setText(R.string.moein_fragment_title);
                navView.setCheckedItem(R.id.nav_menu_moein);
            } break;
            case R.id.nav_menu_gardesh : {
                if(isRighToLeft) {
                    viewPager.setCurrentItem(3);
                }else{
                    viewPager.setCurrentItem(1);
                }
                titleTxt.setText(R.string.gardesh_fragment_title);
                navView.setCheckedItem(R.id.nav_menu_moein);
            } break;
            case R.id.nav_menu_barnameh : {
                if(isRighToLeft) {
                    viewPager.setCurrentItem(4);
                }else{
                    viewPager.setCurrentItem(0);
                }
                titleTxt.setText(R.string.barnameh_fragment_title);
                navView.setCheckedItem(R.id.nav_menu_barnameh);
            }
        };
    }

}
