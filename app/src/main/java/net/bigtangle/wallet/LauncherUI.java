package net.bigtangle.wallet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.bigtangle.wallet.components.WalletInputPasswordDialog;

public class LauncherUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        WalletInputPasswordDialog dialog = new WalletInputPasswordDialog(this, R.style.CustomDialogStyle);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }
}
