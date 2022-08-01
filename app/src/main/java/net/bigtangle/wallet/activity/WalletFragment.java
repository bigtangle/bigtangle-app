package net.bigtangle.wallet.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.wallet.WalletAccountCertificateFragment;
import net.bigtangle.wallet.activity.wallet.WalletAccountFragment;
import net.bigtangle.wallet.activity.wallet.WalletAccountIdentityFragment;
import net.bigtangle.wallet.activity.wallet.WalletSecretkeyFragment;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.SectionsPagerAdapter;
import net.bigtangle.wallet.core.update.UpdateManager;
import net.bigtangle.wallet.core.utils.CommonUtil;
import net.bigtangle.wallet.core.utils.UpdateUtil;

import java.util.ArrayList;

import butterknife.BindView;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class WalletFragment extends BaseLazyFragment {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    private static final int NOT_NOTICE = 2; //如果勾选了不再询问
    private SectionsPagerAdapter mAdapter;
    private UpdateManager mUpdateManager;

    public static WalletFragment newInstance() {
        return new WalletFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requetPermission();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLazyLoad() {
        mTabLayout.setupWithViewPager(mViewPager);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(WalletAccountFragment.newInstance());
        fragments.add(WalletSecretkeyFragment.newInstance());
        fragments.add(WalletAccountIdentityFragment.newInstance());
        fragments.add(WalletAccountCertificateFragment.newInstance());
        String[] title = new String[]{
                this.getString(R.string.wallet_tab_account),
                this.getString(R.string.wallet_tab_secretkey),
                this.getString(R.string.wallet_tab_identity),
                this.getString(R.string.wallet_tab_certificate)
        };

        mAdapter = new SectionsPagerAdapter(getChildFragmentManager(), fragments, title);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(4);
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_wallet, container, false);
    }

    @Override
    public void initEvent() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOT_NOTICE) {
            //由于不知道是否选择了允许所以需要再次判断
            requetPermission();
        }
    }

    private void requetPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private boolean checkVersion() {

        //这里来检测版本是否需要更新
        getActivity().setContentView(R.layout.progress);
        mUpdateManager = new UpdateManager(getActivity());
        return mUpdateManager.checkUpdateInfo();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {//选择了“始终允许”
                    Toast.makeText(getActivity(), "" + getString(R.string.permissions) + permissions[i] + getString(R.string.successful_application), Toast.LENGTH_SHORT).show();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[i])) {//用户选择了禁止不再询问
                        new LovelyStandardDialog(getContext(), LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                                .setTopColorRes(R.color.colorPrimary)
                                .setButtonsColor(Color.WHITE)
                                .setIcon(R.drawable.ic_error_white_24px)
                                .setTitle(getString(R.string.dialog_title_info))
                                .setMessage(getString(R.string.click_permit))
                                .setPositiveButton(getString(R.string.to_allow), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                                        Uri uri = Uri.fromParts("package", "net.bigtangle.wallet", null);//注意就是"package",不用改成自己的包名
                                        intent.setData(uri);
                                        startActivityForResult(intent, NOT_NOTICE);
                                    }
                                }).setNegativeButton(android.R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
                    } else {//选择禁止
                        new LovelyStandardDialog(getContext(), LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                                .setTopColorRes(R.color.colorPrimary)
                                .setButtonsColor(Color.WHITE)
                                .setIcon(R.drawable.ic_error_white_24px)
                                .setTitle(getString(R.string.dialog_title_info))
                                .setMessage(getString(R.string.click_permit))
                                .setPositiveButton(getString(R.string.to_allow), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ActivityCompat.requestPermissions(getActivity(),
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                    }
                                }).setNegativeButton(android.R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
                    }
                }
            }
        }
    }
}
