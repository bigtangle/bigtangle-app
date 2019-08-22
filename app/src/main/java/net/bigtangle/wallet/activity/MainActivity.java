package net.bigtangle.wallet.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.components.ExtendedViewPager;
import net.bigtangle.wallet.components.SectionsPagerAdapter;
import net.bigtangle.wallet.components.SwipeDirection;
import net.bigtangle.wallet.activity.update.UpdateManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private UpdateManager mUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //checkVersion();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomBar bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setDefaultTab(R.id.tab_market);

        ExtendedViewPager mViewPager = findViewById(R.id.main_container);
        mViewPager.setAllowedSwipeDirection(SwipeDirection.none); // Disable swiping

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(TransactionFragment.newInstance());
        fragments.add(WalletFragment.newInstance());
        fragments.add(MarketFragment.newInstance());
        fragments.add(TokenFragment.newInstance());
        fragments.add(SettingsFragment.newInstance());

        String[] title = new String[]{getString(R.string.title_tab_transaction),
                getString(R.string.title_tab_wallet),
                getString(R.string.title_tab_market),
                getString(R.string.title_tab_token),
                getString(R.string.title_tab_settings)};
        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(), fragments, title));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                bottomBar.selectTabAtPosition(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setOffscreenPageLimit(5);

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                int position = 0;

                switch (tabId) {
                    case R.id.tab_transaction:
                        position = 0;
                        break;
                    case R.id.tab_wallet:
                        position = 1;
                        break;
                    case R.id.tab_market:
                        position = 2;
                        break;
                    case R.id.tab_token:
                        position = 3;
                        break;
                    case R.id.tab_settings:
                        position = 4;
                        break;
                }

                mViewPager.setCurrentItem(position);
            }
        });
    }

    private void checkVersion(){
        //这里来检测版本是否需要更新
        mUpdateManager = new UpdateManager(this);
        mUpdateManager.checkUpdateInfo();
    }
}
