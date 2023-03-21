package net.bigtangle.wallet.activity.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.core.response.GetStringResponse;
import net.bigtangle.utils.Json;
import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.AiChatActivity;
import net.bigtangle.wallet.activity.AliMainActivity;
import net.bigtangle.wallet.activity.SPUtil;
import net.bigtangle.wallet.activity.aichat.adapter.AiChatItemListAdapter;
import net.bigtangle.wallet.activity.aichat.model.AiChatItem;
import net.bigtangle.wallet.activity.wallet.adapters.WalletAccountIdentityListAdapter;
import net.bigtangle.wallet.activity.wallet.model.IdentityVO;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountIdentiyItem;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.http.URLUtil;
import net.bigtangle.wallet.core.utils.CommonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
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

public class AiChatFragment extends BaseLazyFragment implements SwipeRefreshLayout.OnRefreshListener {
    String question;
    String answer;
    @BindView(R.id.recycler_view_container)
    RecyclerView recyclerViewContainer;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    private AiChatItemListAdapter mAdapter;

    private List<AiChatItem> itemList;
    @BindView(R.id.btn_send)
    Button sendButton;
    @BindView(R.id.questionText)
    EditText questionText;

    public static AiChatFragment newInstance() {
        return new AiChatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.itemList == null) {
            this.itemList = new ArrayList<AiChatItem>();
        }
        setFroceLoadData(true);
        this.mAdapter = new AiChatItemListAdapter(getContext(), this.itemList);

    }

    @Override
    public void onRefresh() {


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.swipeContainer.setOnRefreshListener(this);

        this.recyclerViewContainer.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext());
        this.recyclerViewContainer.setLayoutManager(layoutManager);
        this.recyclerViewContainer.setAdapter(this.mAdapter);
    }

    @Override
    public void onLazyLoad() {

    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_aichat, container, false);
    }

    @Override
    public void initEvent() {
        sendButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        question = questionText.getText().toString();
                        AiChatItem questionItem = new AiChatItem();
                        questionItem.setInfo(question);
                        itemList.add(questionItem);
                        mAdapter.notifyDataSetChanged();
                        questionText.setText("");
                        try {
                            ask();
                            AiChatItem answerItem = new AiChatItem();
                            answerItem.setInfo(answer);
                            itemList.add(answerItem);
                            mAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            new LovelyInfoDialog(getContext())
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setIcon(R.drawable.ic_error_white_24px)
                                    .setTitle(getString(R.string.dialog_title_info))
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

}
