package com.epipasha.translater;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.epipasha.translater.fragments.FavoritesFragment;
import com.epipasha.translater.fragments.HistoryFragment;
import com.epipasha.translater.fragments.TranslateFragment;

import java.util.Map;

public class MainActivity extends AppCompatActivity
{

    private TextView mTextMessage;

    private Fragment favoritesFrag;
    private Fragment historyFrag;
    private Fragment translateFrag;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction tr = fm.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_translate:
                    tr.replace(R.id.content, translateFrag);
                    tr.commit();
                    return true;
                case R.id.navigation_favorites:
                    tr.replace(R.id.content, favoritesFrag);
                    tr.commit();
                    return true;
                case R.id.navigation_history:
                    tr.replace(R.id.content, historyFrag);
                    tr.commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        favoritesFrag = new FavoritesFragment();
        historyFrag = new HistoryFragment();
        translateFrag = new TranslateFragment();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_translate);

    }


}
