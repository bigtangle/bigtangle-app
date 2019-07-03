package com.eletac.tronwallet.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.eletac.tronwallet.R;
import com.eletac.tronwallet.WrapContentLinearLayoutManager;
import com.eletac.tronwallet.wallet.CreateWalletActivity;

public class SelectWalletActivity extends AppCompatActivity {

    private FloatingActionButton mAdd_FloatingActionButton;
    private RecyclerView mWallets_RecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_wallet);

        mAdd_FloatingActionButton = findViewById(R.id.SelectWallet_add_floatingActionButton);
        mWallets_RecyclerView = findViewById(R.id.SelectWallet_wallets_recyclerView);

        mLayoutManager = new WrapContentLinearLayoutManager(this);

        mAdd_FloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectWalletActivity.this, CreateWalletActivity.class);
                startActivity(intent);
            }
        });
    }
}
