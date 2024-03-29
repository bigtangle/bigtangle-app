package net.bigtangle.wallet.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.token.TokenSearchFragment;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.SectionsPagerAdapter;

import java.util.ArrayList;

import butterknife.BindView;

public class TokenFragment extends BaseLazyFragment {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    private SectionsPagerAdapter mAdapter;

    public static TokenFragment newInstance() {
        return new TokenFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLazyLoad() {
        this.mTabLayout.setupWithViewPager(mViewPager);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(TokenSearchFragment.newInstance());

        String[] title = new String[]{
                getContext().getString(R.string.title_tab_token)
        };
        this.mAdapter = new SectionsPagerAdapter(getChildFragmentManager(), fragments, title);
        this.mViewPager.setAdapter(mAdapter);
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_token, container, false);
    }

    @Override
    public void initEvent() {
    }
}
