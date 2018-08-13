package com.example.sanzharaubakir.unshaky.activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.sanzharaubakir.unshaky.R;
import com.example.sanzharaubakir.unshaky.utils.HighlightData;
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

public class FolioBookReaderActivity extends AppCompatActivity implements OnHighlightListener, ReadPositionListener {

    private static final String LOG_TAG = FolioBookReaderActivity.class.getSimpleName();
    private FolioReader folioReader;
    private String bookUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.folio_reading_view);
        Resources resources = getResources();
        Bundle bundle = getIntent().getBundleExtra(resources.getString(R.string.arguments));
        bookUri = bundle.getString(resources.getString(R.string.book_uri));

        folioReader = FolioReader.getInstance(getApplicationContext())
                .setOnHighlightListener(this)
                .setReadPositionListener(this);


        findViewById(R.id.btn_assest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folioReader.openBook(bookUri);
            }
        });

        getHighlightsAndSave();
        getLastReadPosition();

    }



    private void getLastReadPosition() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                ObjectReader objectReader = ObjectMapperSingleton.getObjectMapper().reader();
                ReadPosition readPosition = null;

                try {
                    readPosition = objectReader.forType(ReadPositionImpl.class)
                            .readValue(getAssets().open("read_positions/read_position.json"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                folioReader.setReadPosition(readPosition);
            }
        }).start();
    }

    @Override
    public void saveReadPosition(ReadPosition readPosition) {
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
            InputStream is = getAssets().open(name);
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
    protected void onDestroy() {
        super.onDestroy();
        FolioReader.clear();
    }

    @Override
    public void onHighlight(HighLight highlight, HighLight.HighLightAction type) {
    }
}