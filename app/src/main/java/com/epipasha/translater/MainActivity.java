package com.epipasha.translater;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
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

public class MainActivity extends AppCompatActivity {

    private static final String SELECTED_MENU_ID = "selectedMenuId";
    private static final String TAG_FAVORITE = "favorite";
    private static final String TAG_HISTORY = "history";
    private static final String TAG_TRANSLATE = "translate";


    BottomNavigationView navigation;

    private Fragment favoritesFrag;
    private Fragment historyFrag;
    private Fragment translateFrag;

    private int selectedItemPosition = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction tr = fm.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_translate:
                    tr.setCustomAnimations(R.animator.slide_in_left, R.animator.zoom_out);
                    tr.replace(R.id.content, translateFrag, TAG_TRANSLATE);
                    tr.commit();
                    selectedItemPosition = 0;
                    return true;
                case R.id.navigation_favorites:
                    if (selectedItemPosition == 0){
                        tr.setCustomAnimations(R.animator.slide_in_right, R.animator.zoom_out);
                    }else{
                        tr.setCustomAnimations(R.animator.slide_in_left, R.animator.zoom_out);
                    }
                    tr.replace(R.id.content, favoritesFrag, TAG_FAVORITE);
                    tr.commit();
                    selectedItemPosition = 1;
                    return true;
                case R.id.navigation_history:
                    tr.setCustomAnimations(R.animator.slide_in_right, R.animator.zoom_out);
                    tr.replace(R.id.content, historyFrag, TAG_HISTORY);
                    tr.commit();
                    selectedItemPosition = 2;
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(savedInstanceState==null){
            favoritesFrag = new FavoritesFragment();
            favoritesFrag.setRetainInstance(true);
            historyFrag = new HistoryFragment();
            historyFrag.setRetainInstance(true);
            translateFrag = new TranslateFragment();
            translateFrag.setRetainInstance(true);

            navigation.setSelectedItemId(R.id.navigation_translate);
        }else{
            favoritesFrag = getFragmentManager().findFragmentByTag(TAG_FAVORITE);
            translateFrag = getFragmentManager().findFragmentByTag(TAG_TRANSLATE);
            historyFrag = getFragmentManager().findFragmentByTag(TAG_HISTORY);

            if (favoritesFrag==null){
                favoritesFrag = new FavoritesFragment();
                favoritesFrag.setRetainInstance(true);
            }

            if (historyFrag==null){
                historyFrag = new HistoryFragment();
                historyFrag.setRetainInstance(true);
            }

            if (translateFrag==null){
                translateFrag = new TranslateFragment();
                translateFrag.setRetainInstance(true);
            }

            navigation.setSelectedItemId(savedInstanceState.getInt(SELECTED_MENU_ID));
        };


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_MENU_ID, navigation.getSelectedItemId());
    }
}
