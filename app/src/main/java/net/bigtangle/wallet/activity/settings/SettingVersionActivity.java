package net.bigtangle.wallet.activity.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.utils.UpdateUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingVersionActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_localMain)
    Toolbar toolbarLocalMain;

    @BindView(R.id.version_text_view)
    TextView versionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_version);
        ButterKnife.bind(this);

        setSupportActionBar(toolbarLocalMain);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        String versionName = UpdateUtil.getVersionName(SettingVersionActivity.this);
        this.versionTextView.setText(versionName);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
