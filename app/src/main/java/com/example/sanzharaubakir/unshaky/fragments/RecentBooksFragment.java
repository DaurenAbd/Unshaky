package com.example.sanzharaubakir.unshaky.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.sanzharaubakir.unshaky.R;
import com.example.sanzharaubakir.unshaky.utils.LazyAdapter;
import com.example.sanzharaubakir.unshaky.utils.TinyDB;

import java.util.ArrayList;

public class RecentBooksFragment extends BookFragment {
    public static final String TAG = "RecentBooksFragment";
    private ListView listView;
    private LazyAdapter adapter;
    public RecentBooksFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recent_books_view, container,
                false);
        listView = (ListView)rootView.findViewById(R.id.list);

        final ArrayList<String> bookUris = getUris();
        adapter = new LazyAdapter(getActivity(), bookUris);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                openBook(bookUris.get(position));
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
