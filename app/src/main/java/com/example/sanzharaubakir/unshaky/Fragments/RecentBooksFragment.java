package com.example.sanzharaubakir.unshaky.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.sanzharaubakir.unshaky.Activities.BookReaderActivity;
import com.example.sanzharaubakir.unshaky.R;
import com.example.sanzharaubakir.unshaky.utils.Constants;


public class RecentBooksFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "RecentBooksFragment";
    private Button browseSDcard;
    public RecentBooksFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recent_books, container,
                false);
        browseSDcard = (Button) rootView.findViewById(R.id.browse_sd_card);
        browseSDcard.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.browse_sd_card:
                if (checkPermissions()) {

                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, Constants.REQUEST_BROWSE);
                }
                break;
        }
    }

    private boolean checkPermissions() {
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_BROWSE
                && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                Log.d(TAG, String.valueOf(uri));
                Bundle bundle = new Bundle();
                Resources resources = getResources();
                bundle.putString(resources.getString(R.string.book_uri), String.valueOf(uri));
                Intent intent = new Intent(getContext(), BookReaderActivity.class);
                intent.putExtra(resources.getString(R.string.arguments), bundle);
                startActivityForResult(intent, Constants.READ_BOOK);
            }
        }
        else if (requestCode == Constants.READ_BOOK
                && resultCode == Activity.RESULT_OK && data != null){
            Log.d(TAG, "Book closed");
        }
    }
}
