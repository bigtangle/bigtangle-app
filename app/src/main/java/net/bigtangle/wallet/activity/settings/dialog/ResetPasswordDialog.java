package net.bigtangle.wallet.activity.settings.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.protobuf.ByteString;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.crypto.KeyCrypterScrypt;
import net.bigtangle.wallet.Protos;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.WalletContextHolder;

import org.apache.commons.lang3.StringUtils;
import org.spongycastle.crypto.params.KeyParameter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResetPasswordDialog extends Dialog {

    private Context context;

    @BindView(R.id.password_text_input)
    TextInputEditText passwordTextInput;

    @BindView(R.id.cancle_button)
    Button cancle_button;

    @BindView(R.id.positive_button)
    Button positiveButton;

    public static final Protos.ScryptParameters SCRYPT_PARAMETERS = Protos.ScryptParameters.newBuilder().setP(6).setR(8)
            .setN(32768).setSalt(ByteString.copyFrom(KeyCrypterScrypt.randomSalt())).build();

    public ResetPasswordDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    public void show() {
        this.setCanceledOnTouchOutside(false);
        this.setCancelable(false);
        super.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_reset_password, null);

        setContentView(view);
        ButterKnife.bind(this, view);
        initView();
    }

    private void initView() {
        if (cancle_button != null) {
            cancle_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        if (positiveButton !=null){
            positiveButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    final String password = passwordTextInput.getText().toString();
                    if (StringUtils.isBlank(password)) {
                        new LovelyInfoDialog(context)
                                .setTopColorRes(R.color.colorPrimary)
                                .setIcon(R.drawable.ic_error_white_24px)
                                .setTitle(context.getString(R.string.dialog_title_error))
                                .setMessage("设置密码不可以为空")
                                .show();
                        return;
                    }

                    try {
                        KeyCrypterScrypt scrypt = new KeyCrypterScrypt(SCRYPT_PARAMETERS);
                        KeyParameter aesKey = scrypt.deriveKey(password);
                        if (WalletContextHolder.get().wallet().isEncrypted()) {
                            WalletContextHolder.get().wallet().decrypt(WalletContextHolder.get().getCurrentPassword());
                        }
                        WalletContextHolder.get().wallet().encrypt(scrypt, aesKey);

                        Toast toast = Toast.makeText(context, "密码设置成功", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();

                        WalletContextHolder.get().savePasswordToLocal(password);

                        dismiss();
                    } catch (Exception e) {
                        new LovelyInfoDialog(context)
                                .setTopColorRes(R.color.colorPrimary)
                                .setIcon(R.drawable.ic_error_white_24px)
                                .setTitle(context.getString(R.string.dialog_title_error))
                                .setMessage("设置密码失败")
                                .show();
                    }
                }
            });
        }
    }
}
