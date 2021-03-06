package com.example.sanzharaubakir.unshaky.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.sanzharaubakir.unshaky.R;
import com.example.sanzharaubakir.unshaky.activities.BookReaderActivity;
import com.example.sanzharaubakir.unshaky.utils.Constants;
import com.example.sanzharaubakir.unshaky.utils.RealPathGetter;
import com.example.sanzharaubakir.unshaky.utils.TinyDB;
import com.folioreader.FolioReader;

import java.util.ArrayList;

public abstract class BookFragment extends Fragment {
    protected static final int PERMISSION_REQUEST = 1;
    public static final String TAG = "BookFragment";
    private FolioReader folioReader;
    protected void browseSDcard(){
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, Constants.REQUEST_BROWSE);
    }
    protected void openBook(final String uri){

        saveBookUri(uri);
        final CharSequence options[] = new CharSequence[] {
                getResources().getString(R.string.spring_dumper),
                getResources().getString(R.string.hidden_markov_model)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose how to open the book");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle bundle = new Bundle();
                Resources resources = getResources();
                bundle.putString(resources.getString(R.string.book_uri), uri);
                bundle.putString(resources.getString(R.string.model_type), options[which].toString());
                Intent intent = new Intent(getContext(), BookReaderActivity.class);
                intent.putExtra(resources.getString(R.string.arguments), bundle);
                startActivityForResult(intent, Constants.READ_BOOK);
            }
        });
        builder.show();
    }

    protected void saveBookUri(String bookUri){
            TinyDB tinyDB = new TinyDB(getContext());
            Resources resources = getResources();
            ArrayList<String> savedBooks = tinyDB.getListString(resources.getString(R.string.saved_books_uris));
            for (String uri : savedBooks){
                if (uri.equals(bookUri)){
                    return;
                }
            }
            savedBooks.add(bookUri);
            tinyDB.putListString(resources.getString(R.string.saved_books_uris), savedBooks);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_BROWSE
                && resultCode == Activity.RESULT_OK && data != null) {
           String uri = RealPathGetter.getRealPathFromURI(getContext(), data.getData());
            if (uri != null) {
                openBook(uri);
            }
        }
        else if (requestCode == Constants.READ_BOOK
                && resultCode == Activity.RESULT_OK && data != null){
            Log.d(TAG, "Book closed");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    browseSDcard();
                } else {
                    // TODO handle permission denied case
                }
                return;
            }
        }
    }
}
