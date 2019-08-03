package net.bigtangle.wallet.activity.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.LocalStorageContext;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingWalletActivity extends AppCompatActivity {

    @BindView(R.id.wallet_name_text_view)
    TextView walletNameTextView;

    @BindView(R.id.wallet_path_text_view)
    TextView walletPathTextView;

    @BindView(R.id.toolbar_localMain)
    Toolbar toolbarLocalMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_wallet);
        ButterKnife.bind(this);

        setSupportActionBar(toolbarLocalMain);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        this.walletNameTextView.setText(LocalStorageContext.get().readWalletFilePrefix());
        this.walletPathTextView.setText(LocalStorageContext.get().readWalletDirectory());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
