package net.bigtangle.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.bigtangle.wallet.activity.MainActivity;
import net.bigtangle.wallet.components.WalletInputPasswordDialog;
import net.bigtangle.wallet.core.WalletContextHolder;

public class LauncherUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        if (WalletContextHolder.get().checkWalletHavePassword()) {
            WalletInputPasswordDialog dialog = new WalletInputPasswordDialog(this, R.style.CustomDialogStyle, new WalletInputPasswordDialog.OnGetWalletPasswordListenter() {
                @Override
                public void getWalletPassword(String password) {
                    Intent intent = new Intent(LauncherUI.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}
