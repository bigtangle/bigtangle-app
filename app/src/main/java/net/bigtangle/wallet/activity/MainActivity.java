package net.bigtangle.wallet.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.components.ExtendedViewPager;
import net.bigtangle.wallet.components.SectionsPagerAdapter;
import net.bigtangle.wallet.components.SwipeDirection;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomBar bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setDefaultTab(R.id.tab_wallet);

        ExtendedViewPager mViewPager = findViewById(R.id.main_container);
        mViewPager.setAllowedSwipeDirection(SwipeDirection.none); // Disable swiping

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(TransactionFragment.newInstance());
        fragments.add(WalletFragment.newInstance());
        fragments.add(MarketFragment.newInstance());
        fragments.add(TokenFragment.newInstance());
        fragments.add(SettingsFragment.newInstance());
        fragments.add(ShopingFragment.newInstance());

        String[] title = new String[]{getString(R.string.title_tab_transaction),
                getString(R.string.title_tab_wallet),
                getString(R.string.title_tab_market),
                getString(R.string.title_tab_token),
                getString(R.string.title_tab_settings),
                getString(R.string.title_tab_shoping)
                };
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
        mViewPager.setOffscreenPageLimit(6);

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
//                    case R.id.tab_shoping:
//                        position = 5;
//                        break;
                }

                mViewPager.setCurrentItem(position);
            }
        });
    }
    public void loadScanKitBtnClick(View view) {
        requestPermission(CAMERA_REQ_CODE, DECODE);
    }

    /**
     * Apply for permissions.
     */
    private void requestPermission(int requestCode, int mode) {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                requestCode);
    }

    /**
     * Call back the permission application result. If the permission application is successful, the code scanning view is displayed.
     *
     * @param requestCode   Permission application code.
     * @param permissions   Permission array.
     * @param grantResults: Permission application result array.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions == null || grantResults == null) {
            return;
        }
        if (grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (requestCode == CAMERA_REQ_CODE) {
            ScanUtil.startScan(this, REQUEST_CODE_SCAN_ONE, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE).create());
        }
    }

    /**
     * vent for receiving the activity result.
     *
     * @param requestCode Request code.
     * @param resultCode  Result code.
     * @param data        Result.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj != null) {
                this.textView.setText(obj.originalValue);
            }
        }
    }
}
