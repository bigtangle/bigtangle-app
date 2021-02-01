package net.bigtangle.wallet.activity.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.settings.adapter.ServerItemListAdapter;
import net.bigtangle.wallet.activity.settings.model.ServerInfoItem;
import net.bigtangle.wallet.core.LocalStorageContext;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingConnectionActivity extends AppCompatActivity {

    @BindView(R.id.server_connection_spinner)
    Spinner serverConnectionSpinner;

    @BindView(R.id.save_button)
    Button saveButton;

    @BindView(R.id.toolbar_localMain)
    Toolbar toolbarLocalMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_connection);
        ButterKnife.bind(this);

        List<ServerInfoItem> itemList = getServerInfoItems();
        ServerItemListAdapter adapter = new ServerItemListAdapter(this, itemList);
        this.serverConnectionSpinner.setAdapter(adapter);

        String serverURL = LocalStorageContext.get().readServerURL();
        int index = 0;
        for (ServerInfoItem serverInfoItem : itemList) {
            if (serverInfoItem.getConnectionURL().equals(serverURL)) {
                break;
            }
            index++;
        }

        if (index >= itemList.size()) {
            index = 0;
        }

        this.serverConnectionSpinner.setSelection(index, true);

        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView connectionUrlTextView = serverConnectionSpinner.getSelectedView().findViewById(R.id.connection_url_text_view);
                final String connectionURL = connectionUrlTextView.getText().toString();
                LocalStorageContext.get().writeServerURL(connectionURL);

                new LovelyInfoDialog(SettingConnectionActivity.this)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_info_white_24px)
                        .setTitle(SettingConnectionActivity.this.getString(R.string.dialog_title_info))
                        .setMessage(SettingConnectionActivity.this.getString(R.string.save_server_connection_information_successfully))
                        .show();
            }
        });

        setSupportActionBar(toolbarLocalMain);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    /*private List<ServerInfoItem> getTestServerInfoItems() {
        List<ServerInfoItem> itemList = new ArrayList<ServerInfoItem>();
        itemList.add(ServerInfoItem.build("bigtangle.org", "https://p.bigtangle.org:8088/"));
        itemList.add(ServerInfoItem.build("bigtangle.info", "https://p.bigtangle.info:8088/"));
        itemList.add(ServerInfoItem.build("bigtangle.de", "https://p.bigtangle.de:8088/"));
        return itemList;
    }*/

    private List<ServerInfoItem> getServerInfoItems() {
        List<ServerInfoItem> itemList = new ArrayList<ServerInfoItem>();
        itemList.add(ServerInfoItem.build("bigtangle.org", "https://p.bigtangle.org:8088/"));
        itemList.add(ServerInfoItem.build("bigtangle.info", "https://p.bigtangle.info:8088/"));
        itemList.add(ServerInfoItem.build("bigtangle.de", "https://p.bigtangle.de:8088/"));
        itemList.add(ServerInfoItem.build("LOCAL", "http://10.0.3.2:8088/"));
        itemList.add(ServerInfoItem.build("test", "https://test.bigtangle.info:8089/"));
        return itemList;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
