package com.example.justas.grabble;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class InventoryActivity extends AppCompatActivity implements HistoryStatsFragment.OnFragmentInteractionListener,
        CurrentInventoryFragment.OnFragmentInteractionListener {
    private boolean mShowingInventory;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragmentManager = getFragmentManager();

        setContentView(R.layout.activity_inventory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        initFragments();

        final Button inventory_fragment_button = (Button) findViewById(R.id.inventory_fragment_button);

        inventory_fragment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                Fragment statsFragment = mFragmentManager.findFragmentById(R.id.inventory_fragment_stats);
                Fragment inventoryFragment = mFragmentManager.findFragmentById(R.id.inventory_fragment_current_inventory);

                if (mShowingInventory) {
                    hideFragment(inventoryFragment);
                    showFragment(statsFragment);

                    mShowingInventory = false;
                    inventory_fragment_button.setText("INVENTORY");
                } else {
                    hideFragment(statsFragment);
                    showFragment(inventoryFragment);

                    mShowingInventory = true;
                    inventory_fragment_button.setText("STATS & HISTORY");
                }

                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    private void initFragments() {
        Fragment inventoryFragment = mFragmentManager.findFragmentById(R.id.inventory_fragment_stats);
        Fragment statsFragment = mFragmentManager.findFragmentById(R.id.inventory_fragment_stats);

        showFragment(inventoryFragment);
        hideFragment(statsFragment);

        mShowingInventory = true;
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.show(fragment);
        ft.commit();
    }

    private void hideFragment(Fragment fragment) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.hide(fragment);
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void onBackPressed() {
        Log.d("BACK CLICKED", "");
        finish();
    }
}
