package net.bigtangle.wallet.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.settings.SettingConnectionActivity;
import net.bigtangle.wallet.activity.settings.SettingContactActivity;
import net.bigtangle.wallet.activity.settings.SettingWalletActivity;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.core.utils.UpdateUtil;

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
                    showVersionDialog();
                }
            });
        }
    }

    private void showVersionDialog() {
        String versionName = UpdateUtil.getVersionName(getContext());

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), R.style.AppTheme);

        dialog.setPositiveButton(getContext().getString(R.string.ok), (dialog1, which) -> dialog1.dismiss());
        dialog.setCancelable(true);
        AlertDialog alertDialog = dialog.create();
        TextView title = new TextView(getContext());
        title.setText(getContext().getString(R.string.about_app));
        title.setPadding(10, 30, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(18);
        title.setTextColor(Color.WHITE);

        alertDialog.setCustomTitle(title);
        alertDialog.setMessage(getContext().getString(R.string.version) + "  " + versionName);
        alertDialog.setCancelable(false);
        alertDialog.show();

        Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams cancelBtnPara = (LinearLayout.LayoutParams) button.getLayoutParams();
        //设置按钮的大小
        cancelBtnPara.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        cancelBtnPara.width = LinearLayout.LayoutParams.MATCH_PARENT;
        //设置文字居中
        cancelBtnPara.gravity = Gravity.CENTER;
        //设置按钮左上右下的距离
        cancelBtnPara.setMargins(10, 30, 10, 10);
        button.setLayoutParams(cancelBtnPara);
        button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_bg));
        button.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        button.setTextSize(16);
    }
}
