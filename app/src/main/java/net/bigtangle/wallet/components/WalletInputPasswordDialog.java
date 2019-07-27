package net.bigtangle.wallet.components;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletInputPasswordDialog extends Dialog {

    private Context context;

    @BindView(R.id.password_text_input)
    TextInputEditText passwordTextInput;

    @BindView(R.id.positive_button)
    Button positiveButton;

    public WalletInputPasswordDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_wallet_password, null);

        setContentView(view);
        ButterKnife.bind(this, view);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                dismiss();
            }
        });
    }
}
