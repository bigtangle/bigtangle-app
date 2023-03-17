package net.bigtangle.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.wallet.dialog.WalletPasswordDialog;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AiChatActivity extends AppCompatActivity {
    private static final int REQUESTCODE_FROM_ACTIVITY = 1000;
    String question;
    String answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_aichat);


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
                        try {
                            ask();
                            EditText answerText = (EditText) findViewById(R.id.answerText);
                            answerText.setText(answer);
                        } catch (Exception e) {

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
        // Your API Key
        String apiKey = "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";

        // The prompt to complete
        String prompt = question;

        // The URL of the API endpoint
        String endpoint = "https://api.openai.com/v1/completions";

        // The request headers
        Headers headers = new Headers.Builder().add("Content-Type", "application/json")
                .add("Authorization", "Bearer " + apiKey).build();

        // The request payload
        JSONObject payload = new JSONObject();
        payload.put("prompt", prompt);
        payload.put("model", "text-davinci-003");
        payload.put("max_tokens", 2000);
        payload.put("temperature", 0.5);
        payload.put("top_p", 1);

        // Create the request body
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), payload.toString());

        // Create the request
        Request request = new Request.Builder().url(endpoint).headers(headers).post(body).build();

        // Make the request
        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES).writeTimeout(5, TimeUnit.MINUTES).build();
        Response response = client.newCall(request).execute();

        // Parse the response
        String responseString = response.body().string();
        JSONObject responseJson = new JSONObject(responseString);


        JSONArray choices = responseJson.getJSONArray("choices");
        String text = choices.getJSONObject(0).getString("text");
        answer = text;

    }
}





