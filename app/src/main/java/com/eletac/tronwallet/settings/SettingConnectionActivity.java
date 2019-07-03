package com.eletac.tronwallet.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.eletac.tronwallet.R;

import org.apache.commons.lang3.StringUtils;

public class SettingConnectionActivity extends AppCompatActivity {

    private EditText mIP_EditText;
    private EditText mPort_EditText;
    private Button mReset_Button;
    private Button mSave_Button;

    private EditText mIP_Solidty_EditText;
    private EditText mPort_Solidty_EditText;
    private Button mReset_Solidty_Button;
    private Button mSave_Solidty_Button;

    boolean mIsColdWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_connection);

        Toolbar toolbar = findViewById(R.id.SettingConnection_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mIP_EditText = findViewById(R.id.SettingConnection_node_ip_editText);
        mPort_EditText= findViewById(R.id.SettingConnection_node_port_editText);
        mReset_Button= findViewById(R.id.SettingConnection_reset_button);
        mSave_Button= findViewById(R.id.SettingConnection_save_button);

        mIP_Solidty_EditText = findViewById(R.id.SettingConnection_node_sol_ip_editText);
        mPort_Solidty_EditText= findViewById(R.id.SettingConnection_node_sol_port_editText);
        mReset_Solidty_Button= findViewById(R.id.SettingConnection_reset_sol_button);
        mSave_Solidty_Button= findViewById(R.id.SettingConnection_save_sol_button);

        loadNodes();

        mSave_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mReset_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mSave_Solidty_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mReset_Solidty_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void loadNodes() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String ip = "", ip_sol = "";
        int port = 0, port_sol = 0;

        try {
             ip = sharedPreferences.getString(getString(R.string.ip_key), getString(R.string.fullnode_ip));
             port = sharedPreferences.getInt(getString(R.string.port_key), Integer.parseInt(getString(R.string.fullnode_port)));

             ip_sol = sharedPreferences.getString(getString(R.string.ip_sol_key), getString(R.string.soliditynode_ip));
             port_sol = sharedPreferences.getInt(getString(R.string.port_sol_key), Integer.parseInt(getString(R.string.soliditynode_port)));

        } catch(NumberFormatException e) {
            e.printStackTrace();
        }
        mIP_EditText.setText(ip);
        mPort_EditText.setText(!StringUtils.isEmpty(ip) ? String.valueOf(port) : "");

        mIP_Solidty_EditText.setText(ip_sol);
        mPort_Solidty_EditText.setText(!StringUtils.isEmpty(ip_sol) ? String.valueOf(port_sol) : "");
    }
}
