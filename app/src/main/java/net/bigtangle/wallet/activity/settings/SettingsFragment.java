package net.bigtangle.wallet.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.components.BaseLazyFragment;

import butterknife.BindView;

public class SettingsFragment extends BaseLazyFragment {

    @BindView(R.id.connection_button)
    Button connectionButton;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onLazyLoad() {

    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void initEvent() {
        this.connectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingConnectionActivity.class);
                startActivity(intent);
            }
        });
    }
}
