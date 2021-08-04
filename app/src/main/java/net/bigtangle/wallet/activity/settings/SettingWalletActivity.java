package net.bigtangle.wallet.activity.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.settings.dialog.ResetPasswordDialog;
import net.bigtangle.wallet.core.LocalStorageContext;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingWalletActivity extends AppCompatActivity {



    @BindView(R.id.toolbar_localMain)
    Toolbar toolbarLocalMain;

    @BindView(R.id.reset_password_button)
    Button resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_wallet);
        ButterKnife.bind(this);

        setSupportActionBar(toolbarLocalMain);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        this.resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ResetPasswordDialog(SettingWalletActivity.this, R.style.CustomDialogStyle)
                        .show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
