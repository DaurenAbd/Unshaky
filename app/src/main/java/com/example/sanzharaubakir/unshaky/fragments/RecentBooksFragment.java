package com.example.sanzharaubakir.unshaky.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.sanzharaubakir.unshaky.R;
import com.example.sanzharaubakir.unshaky.adapters.LazyAdapter;
import com.example.sanzharaubakir.unshaky.utils.TinyDB;

import java.util.ArrayList;

public class RecentBooksFragment extends BookFragment {
    public static final String TAG = "RecentBooksFragment";
    private ListView listView;
    private LazyAdapter adapter;
    private ArrayList<String> bookUris;
    public RecentBooksFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recent_books_view, container,
                false);
        listView = (ListView)rootView.findViewById(R.id.list);

        bookUris = getUris();
        adapter = new LazyAdapter(getActivity(), bookUris);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                openBook(bookUris.get(position));
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           final int index, long arg3) {
                CharSequence options[] = new CharSequence[] {"Yes", "No"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Remove the book from the list");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                TinyDB tinyDB = new TinyDB(getContext());
                                Resources resources = getResources();
                                ArrayList<String> uris = getUris();
                                uris.remove(index);
                                tinyDB.putListString(resources.getString(R.string.saved_books_uris), uris);
                                bookUris = uris;
                                adapter.notifyDataSetChanged();
                                listView.invalidateViews();
                                listView.refreshDrawableState();
                                break;
                            case 1:
                                break;
                        }
                    }
                });
                builder.show();
                return true;
            }
        });
        return rootView;
    }

    private ArrayList<String> getUris() {
        TinyDB tinyDB = new TinyDB(getContext());
        Resources resources = getResources();
        ArrayList<String> savedBooks = tinyDB.getListString(resources.getString(R.string.saved_books_uris));
        return savedBooks;
    }


}
