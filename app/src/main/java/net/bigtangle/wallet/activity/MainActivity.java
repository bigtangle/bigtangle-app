package net.bigtangle.wallet.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.pgyersdk.update.DownloadFileListener;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.pgyersdk.update.javabean.AppBean;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.market.MarketFragment;
import net.bigtangle.wallet.activity.settings.SettingsFragment;
import net.bigtangle.wallet.activity.token.TokenFragment;
import net.bigtangle.wallet.activity.transaction.TransactionFragment;
import net.bigtangle.wallet.activity.wallet.WalletFragment;
import net.bigtangle.wallet.components.ExtendedViewPager;
import net.bigtangle.wallet.components.SectionsPagerAdapter;
import net.bigtangle.wallet.components.SwipeDirection;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

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
        new PgyUpdateManager.Builder()
                .setForced(true)                //设置是否强制提示更新,非自定义回调更新接口此方法有用
                .setUserCanRetry(false)         //失败后是否提示重新下载，非自定义下载 apk 回调此方法有用
                .setDeleteHistroyApk(false)     // 检查更新前是否删除本地历史 Apk， 默认为true
                .setUpdateManagerListener(new UpdateManagerListener() {
                    @Override
                    public void onNoUpdateAvailable() {
                        //没有更新是回调此方法
                        Log.d("pgyer", "there is no new version");
                    }
                    @Override
                    public void onUpdateAvailable(AppBean appBean) {
                        //有更新回调此方法
                        Log.d("pgyer", "there is new version can update"
                                + "new versionCode is " + appBean.getVersionCode());
                        //调用以下方法，DownloadFileListener 才有效；
                        //如果完全使用自己的下载方法，不需要设置DownloadFileListener
                        PgyUpdateManager.downLoadApk(appBean.getDownloadURL());
                    }

                    @Override
                    public void checkUpdateFailed(Exception e) {
                        //更新检测失败回调
                        //更新拒绝（应用被下架，过期，不在安装有效期，下载次数用尽）以及无网络情况会调用此接口
                        Log.e("pgyer", "check update failed ", e);
                    }
                })
                //注意 ：
                //下载方法调用 PgyUpdateManager.downLoadApk(appBean.getDownloadURL()); 此回调才有效
                //此方法是方便用户自己实现下载进度和状态的 UI 提供的回调
                //想要使用蒲公英的默认下载进度的UI则不设置此方法
                .setDownloadFileListener(new DownloadFileListener() {
                    @Override
                    public void downloadFailed() {
                        //下载失败
                        Log.e("pgyer", "download apk failed");
                    }

                    @Override
                    public void downloadSuccessful(Uri uri) {
                        Log.e("pgyer", "download apk failed");
                        // 使用蒲公英提供的安装方法提示用户 安装apk
                        PgyUpdateManager.installApk(uri);
                    }

                    @Override
                    public void onProgressUpdate(Integer... integers) {
                        Log.e("pgyer", "update download apk progress" + integers);
                    }})
                .register();
    }
}
