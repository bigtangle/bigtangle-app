package net.bigtangle.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.core.response.GetStringResponse;
import net.bigtangle.utils.Json;
import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.aichat.adapter.AiChatItemListAdapter;
import net.bigtangle.wallet.activity.aichat.model.AiChatItem;
import net.bigtangle.wallet.activity.wallet.adapters.WalletAccountItemListAdapter;
import net.bigtangle.wallet.activity.wallet.dialog.WalletPasswordDialog;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountItem;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.utils.CommonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AiChatActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final int REQUESTCODE_FROM_ACTIVITY = 1000;
    String question;
    String answer;
    @BindView(R.id.recycler_view_container)
    RecyclerView recyclerViewContainer;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;
    private AiChatItemListAdapter mAdapter;

    private List<AiChatItem> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.itemList == null) {
            this.itemList = new ArrayList<AiChatItem>();
        }
        setContentView(R.layout.activity_aichat);

        ButterKnife.bind(this);
        this.swipeContainer.setOnRefreshListener(this);
        this.recyclerViewContainer.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(this);
        this.recyclerViewContainer.setLayoutManager(layoutManager);
        this.mAdapter = new AiChatItemListAdapter(this, itemList);
        this.recyclerViewContainer.setAdapter(this.mAdapter);

        findViewById(R.id.skip_btn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AiChatActivity.this, VerifyWalletActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
        );
        findViewById(R.id.btn_send).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText questionText = (EditText) findViewById(R.id.questionText);
                        question = questionText.getText().toString();
                        AiChatItem questionItem = new AiChatItem();
                        questionItem.setInfo(question);
                        itemList.add(questionItem);
                        mAdapter.notifyDataSetChanged();
                        try {
                            ask();
                            AiChatItem answerItem = new AiChatItem();
                            answerItem.setInfo(answer);
                            itemList.add(answerItem);
                            mAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            new LovelyInfoDialog(AiChatActivity.this)
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setIcon(R.drawable.ic_error_white_24px)
                                    .setTitle(AiChatActivity.this.getString(R.string.dialog_title_info))
                                    .setMessage(e.getMessage())
                                    .show();
                        }
                    }
                }
        );
    }

    private void ask()
            throws InterruptedException, ExecutionException {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        @SuppressWarnings({"unchecked", "rawtypes"}) final Future<String> handler = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                doAsk(question);

                return "";
            }
        });
        try {
            handler.get(600000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            handler.cancel(true);
        } finally {
            executor.shutdownNow();
        }


    }

    public void doAsk(String question) throws Exception {

        String tradeserviceUrl = "https://bigtangle.de:8092/";
        HashMap<String, String> requestParam = new HashMap<String, String>();
        requestParam.put("prompt", question);
        byte[] data = OkHttp3Util.postString(tradeserviceUrl + "relay",
                Json.jsonmapper().writeValueAsString(requestParam));
        GetStringResponse resp = Json.jsonmapper().readValue(data, GetStringResponse.class);

        JSONObject responseJson = new JSONObject(resp.getText());

        JSONArray choices = responseJson.getJSONArray("choices");
        String text = choices.getJSONObject(0).getString("text");
        answer = text;

    }

    @Override
    public void onRefresh() {

    }
}





