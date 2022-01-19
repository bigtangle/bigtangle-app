package net.bigtangle.wallet.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import net.bigtangle.wallet.R;

/**
 * 隐私政策
 *
 * @author zhongfg
 */
public class PrivacyPolicyActivity extends AppCompatActivity {

    private static final String TAG = PrivacyPolicyActivity.class.getSimpleName();

    private FrameLayout web_view_container;
    private WebView web_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_privacy_policy);

        initView();
    }

    private void initView() {

        web_view_container = findViewById(R.id.web_view_container);
        web_view = new WebView(getApplicationContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        web_view.setLayoutParams(params);
        web_view.setWebViewClient(new WebViewClient());
        //动态添加WebView，解决在xml引用WebView持有Activity的Context对象，导致内存泄露
        web_view_container.addView(web_view);


        web_view.loadUrl("file:///android_asset/privacy_policy.html");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        web_view_container.removeAllViews();
        web_view.destroy();
    }
}
