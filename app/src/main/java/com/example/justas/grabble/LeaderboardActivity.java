package com.example.justas.grabble;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.justas.grabble.helper.Leaderboard;
import com.example.justas.grabble.helper.Player;

public class LeaderboardActivity extends AppCompatActivity implements ScoreFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onListFragmentInteraction(Player item) {
        Log.d("LISTFRAGMENT", "interaction");
    }

    public class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case Leaderboard.TODAY:
                    return ScoreFragment.newInstance(pos);
                case Leaderboard.ALL_TIME:
                    return ScoreFragment.newInstance(pos);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return Leaderboard.count();
        }

        @Override
        public CharSequence getPageTitle(int pos) {
            switch (pos) {
                case Leaderboard.TODAY:
                    return Leaderboard.titleOf(pos);
                case Leaderboard.ALL_TIME:
                    return Leaderboard.titleOf(pos);
                default:
                    return null;
            }
        }
    }
}