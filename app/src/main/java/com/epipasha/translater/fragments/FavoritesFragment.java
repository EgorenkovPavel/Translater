package com.epipasha.translater.fragments;


import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.epipasha.translater.R;
import com.epipasha.translater.db.DbHelper;
import com.epipasha.translater.db.DbManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener,
        DialogInterface.OnClickListener{

    ListView list;
    SimpleCursorAdapter mAdapter;
    String mCurFilter;

    SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorites, container, false);
        list = (ListView)v.findViewById(R.id.list);

        setHasOptionsMenu(true);

        db = new DbHelper(getActivity()).getReadableDatabase();

        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.item_favorites, null,
                new String[] {DbHelper.INPUT_TEXT, DbHelper.OUTPUT_TEXT, DbHelper.INPUT_CODE, DbHelper.OUTPUT_CODE},
                new int[] { R.id.inputText, R.id.outputText, R.id.inputCode, R.id.outputCode }, 0);
        list.setAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Place an action bar item for searching.
        inflater.inflate(R.menu.list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        SearchView sv = new SearchView(getActivity());
        sv.setOnQueryTextListener(this);
        menu.findItem(R.id.menu_search).setActionView(sv);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_clear:{
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setPositiveButton(R.string.ok, this);
                builder.setNegativeButton(R.string.cancel, null);
                builder.setMessage(R.string.dialog_clear_message)
                        .setTitle(R.string.dialog_clear_title);
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        DbManager.getInstance(getActivity()).deleteFavorites();
        getLoaderManager().restartLoader(0, null, this);
    }

    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed.  Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Don't care about this.
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new FavoritesFragment.MyCursorLoader(getActivity(), db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }


    static class MyCursorLoader extends CursorLoader {

        SQLiteDatabase db;

        public MyCursorLoader(Context context, SQLiteDatabase db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = db.query(DbHelper.TABLE_FAVORITES, null, null, null, null, null, DbHelper._ID + " DESC");
            return cursor;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        db.close();
    }

}
