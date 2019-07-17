package net.bigtangle.wallet.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.market.MarketFragment;
import net.bigtangle.wallet.activity.settings.SettingsFragment;
import net.bigtangle.wallet.activity.transaction.TransactionFragment;
import net.bigtangle.wallet.activity.wallet.WalletFragment;
import net.bigtangle.wallet.components.ExtendedViewPager;
import net.bigtangle.wallet.components.SwipeDirection;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.utils.RoutePathUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomBar bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_wallet);

        WalletContextHolder.get().initWalletData(RoutePathUtil.getBasePath(this), "bigtangle");

        ExtendedViewPager mViewPager = findViewById(R.id.Main_container);
        mViewPager.setAllowedSwipeDirection(SwipeDirection.none); // Disable swiping
        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(5);
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
                    case R.id.tab_settings:
                        position = 3;
                        break;
                }

                mViewPager.setCurrentItem(position);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = TransactionFragment.newInstance();
                    break;
                case 1:
                    fragment = WalletFragment.newInstance();
                    break;
                case 2:
                    fragment = MarketFragment.newInstance();
                    break;
                case 3:
                    fragment = SettingsFragment.newInstance();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
