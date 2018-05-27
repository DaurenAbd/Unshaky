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
import com.example.sanzharaubakir.unshaky.utils.HighlightData;
import com.example.sanzharaubakir.unshaky.utils.RealPathGetter;
import com.example.sanzharaubakir.unshaky.utils.TinyDB;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.folioreader.FolioReader;
import com.folioreader.model.HighLight;
import com.folioreader.model.ReadPosition;
import com.folioreader.model.ReadPositionImpl;
import com.folioreader.ui.base.OnSaveHighlight;
import com.folioreader.util.ObjectMapperSingleton;
import com.folioreader.util.OnHighlightListener;
import com.folioreader.util.ReadPositionListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
        CharSequence options[] = new CharSequence[] {"FolioActivity", "DefaultActivity"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose how to open the book");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        folioReader = FolioReader.getInstance(getContext())
                                .setOnHighlightListener(new OnHighlightListener() {
                                    @Override
                                    public void onHighlight(HighLight highlight, HighLight.HighLightAction type) {

                                    }
                                })
                                .setReadPositionListener(new ReadPositionListener() {
                                    @Override
                                    public void saveReadPosition(ReadPosition readPosition) {

                                    }
                                });

                        folioReader.openBook(uri);
                        getHighlightsAndSave();
                        getLastReadPosition();
                        break;
                    case 1:
                        Bundle bundle = new Bundle();
                        Resources resources = getResources();
                        bundle.putString(resources.getString(R.string.book_uri), uri);
                        Intent intent = new Intent(getContext(), BookReaderActivity.class);
                        intent.putExtra(resources.getString(R.string.arguments), bundle);
                        startActivityForResult(intent, Constants.READ_BOOK);
                }
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
    private void getLastReadPosition() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                ObjectReader objectReader = ObjectMapperSingleton.getObjectMapper().reader();
                ReadPosition readPosition = null;

                try {
                    readPosition = objectReader.forType(ReadPositionImpl.class)
                            .readValue(getActivity().getAssets().open("read_positions/read_position.json"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                folioReader.setReadPosition(readPosition);
            }
        }).start();
    }

    /*
     * For testing purpose, we are getting dummy highlights from asset. But you can get highlights from your server
     * On success, you can save highlights to FolioReader DB.
     */
    private void getHighlightsAndSave() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<HighLight> highlightList = null;
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    highlightList = objectMapper.readValue(
                            loadAssetTextAsString("highlights/highlights_data.json"),
                            new TypeReference<List<HighlightData>>() {
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (highlightList == null) {
                    folioReader.saveReceivedHighLights(highlightList, new OnSaveHighlight() {
                        @Override
                        public void onFinished() {
                            //You can do anything on successful saving highlight list
                        }
                    });
                }
            }
        }).start();
    }

    private String loadAssetTextAsString(String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = getActivity().getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e("HomeActivity", "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("HomeActivity", "Error closing asset " + name);
                }
            }
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FolioReader.clear();
    }
}
