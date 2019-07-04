package com.eletac.tronwallet.wallet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.arasthel.asyncjob.AsyncJob;
import com.eletac.tronwallet.R;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.HashMap;

public class VoteActivity extends AppCompatActivity {

    public static final String VOTES_UPDATED = "com.eletac.tronwallet.vote_activity.votes_updated";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private TextView mRemaining_TextView;
    private Button mSubmit_Button;

    private HashMap<String, String> mVoteWitnesses;
    private VotesUpdatedBroadcastReceiver mVotesUpdatedBroadcastReceiver;

    private AccountUpdatedBroadcastReceiver mAccountUpdatedBroadcastReceiver;

    private boolean mLoadVotesOnNextAccountUpdate;

    private long mFrozen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        Toolbar toolbar = findViewById(R.id.Vote_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mVoteWitnesses = new HashMap<>();
        mVotesUpdatedBroadcastReceiver = new VotesUpdatedBroadcastReceiver();
        mAccountUpdatedBroadcastReceiver = new AccountUpdatedBroadcastReceiver();

        mViewPager = findViewById(R.id.Vote_container);
        mRemaining_TextView = findViewById(R.id.Vote_remaining_textView);
        mSubmit_Button = findViewById(R.id.Vote_submit_button);

        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.Vote_tabs_tabLayout);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // Update event to load the new vote amounts on the fragments | Delayed because UI lag @TODO move iterations to background thread
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent updatedIntent = new Intent(VOTES_UPDATED);
                        LocalBroadcastManager.getInstance(VoteActivity.this).sendBroadcast(updatedIntent);
                    }
                }, 250);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mSubmit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String textBackup = mSubmit_Button.getText().toString();

                mSubmit_Button.setEnabled(false);
                mSubmit_Button.setText(R.string.loading);

                AsyncJob.doInBackground(() -> {});
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mVotesUpdatedBroadcastReceiver, new IntentFilter(WitnessItemListAdapter.VOTES_UPDATED));
        LocalBroadcastManager.getInstance(this).registerReceiver(mAccountUpdatedBroadcastReceiver, new IntentFilter(AccountUpdater.ACCOUNT_UPDATED));

        loadVotes();
        updateRemain();

        // No votes
        if(mFrozen == 0) {
            new LovelyStandardDialog(VoteActivity.this)
                    .setTopColorRes(R.color.colorPrimary)
                    .setIcon(R.drawable.ic_info_white_24px)
                    .setTitle(R.string.missing_votes)
                    .setMessage(R.string.to_get_votes_you_have_to_freeze_trx)
                    .setPositiveButton(R.string.freeze, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(VoteActivity.this, SendReceiveActivity.class);
                            intent.putExtra("page", 2);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mVotesUpdatedBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAccountUpdatedBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadVotes() {
    }

    private void updateRemain() {
    }

    public HashMap<String, String> getVoteWitnesses() {
        return mVoteWitnesses;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = VoteWitnessesFragment.newInstance();
                    break;
                case 1:
                    fragment = OwnVotesFragment.newInstance();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tab_title_candidates);
                case 1:
                    return getString(R.string.tab_title_votes);
            }
            return null;
        }
    }

    private class VotesUpdatedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateRemain();
        }
    }

    private class AccountUpdatedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
