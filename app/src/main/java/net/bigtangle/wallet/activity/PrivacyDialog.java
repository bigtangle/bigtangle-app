package net.bigtangle.wallet.activity;

import android.app.Dialog;
import android.content.Context;

import net.bigtangle.wallet.R;

public class PrivacyDialog extends Dialog {

    public PrivacyDialog(Context context) {
        super(context, R.style.PrivacyThemeDialog);

        setContentView(R.layout.dialog_privacy);

        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }
}
