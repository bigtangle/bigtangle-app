package net.bigtangle.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.settings.SettingConnectionActivity;
import net.bigtangle.wallet.activity.settings.SettingContactActivity;
import net.bigtangle.wallet.activity.settings.SettingVersionActivity;
import net.bigtangle.wallet.activity.settings.SettingWalletActivity;
import net.bigtangle.wallet.components.BaseLazyFragment;

import butterknife.BindView;

public class SettingsFragment extends BaseLazyFragment {

    @BindView(R.id.connection_button)
    Button connectionButton;

    @BindView(R.id.wallet_button)
    Button walletButton;

    @BindView(R.id.about_button)
    Button aboutButton;

    @BindView(R.id.contact_button)
    Button contactButton;

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

        this.walletButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingWalletActivity.class);
                startActivity(intent);
            }
        });

        this.contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingContactActivity.class);
                startActivity(intent);
            }
        });

        if (aboutButton != null) {
            this.aboutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), SettingVersionActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}
