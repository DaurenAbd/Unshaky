package com.example.sanzharaubakir.unshaky.activities;

import android.app.Activity;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sanzharaubakir.unshaky.R;
import com.example.sanzharaubakir.unshaky.sensor.Accelerometer;
import com.example.sanzharaubakir.unshaky.sensor.AccelerometerListener;
import com.example.sanzharaubakir.unshaky.utils.OnSwipeTouchListener;
import com.example.sanzharaubakir.unshaky.utils.TinyDB;
import com.github.mertakdut.BookSection;
import com.github.mertakdut.Reader;
import com.github.mertakdut.exception.OutOfPagesException;
import com.github.mertakdut.exception.ReadingException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BookReaderActivity extends Activity implements AccelerometerListener {
    private static final String TAG = BookReaderActivity.class.getSimpleName();

    private View layoutSensor;
    private TextView textView;
    private SensorManager sensorManager;
    private Accelerometer accelerometer;
    private String text;
    private int page = 0;
    private String bookUri;
    private Button prevPage;
    private Button nextPage;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reading_view);
        initViews();
        Resources resources = getResources();
        Bundle bundle = getIntent().getBundleExtra(resources.getString(R.string.arguments));
        String uri = bundle.getString(resources.getString(R.string.book_uri));
        bookUri = uri;
        saveUri(bookUri);
        textView = (TextView) findViewById(R.id.txt_test);
        textView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeRight() {
                readNextPage();
            }
            public void onSwipeLeft() {
                readPrevPage();
            }

        });
        prevPage = (Button) findViewById(R.id.prev_page);
        prevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readPrevPage();
            }
        });
        nextPage = (Button) findViewById(R.id.next_page);
        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readNextPage();
            }
        });
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = new Accelerometer(sensorManager);
        accelerometer.setListener(this);
    }

    private void saveUri(String bookUri) {
        TinyDB tinyDB = new TinyDB(getApplicationContext());
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
    protected void onResume() {
        super.onResume();
        accelerometer.enable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        accelerometer.disable();
    }


    private void initViews() {
        View layoutRoot = findViewById(R.id.layout_root);

        layoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accelerometer.reset();
            }
        });

        layoutSensor = findViewById(R.id.layout_sensor);
    }

    @Override
    public void onPositionChanged(@NotNull float[] position) {
        layoutSensor.setTranslationX(-position[0]);
        layoutSensor.setTranslationY(position[1]);
    }
    public void readNextPage(){
        Log.d(TAG, "reading next page");
        if (bookUri != null){
            Reader reader = new Reader();
            BookSection bookSection = null;

            try{
                reader.setMaxContentPerSection(1000); // Max string length for the current page.
                reader.setIsIncludingTextContent(true); // Optional, to return the tags-excluded version.
                reader.setFullContent(bookUri); // Must call before readSection.
                bookSection = reader.readSection(page + 1);
                page++;
                while (bookSection.getSectionContent() == null){
                    page++;
                    bookSection = reader.readSection(page);
                    Log.d(TAG, "incrementing index - " + page);
                }
                String sectionContent = bookSection.getSectionContent(); // Returns content as html.
                String sectionTextContent = bookSection.getSectionTextContent(); // Excludes html tags.
                text = sectionTextContent;
                textView.setText(text);
            } catch (ReadingException e) {
                e.printStackTrace();
            } catch (OutOfPagesException e) {
                e.printStackTrace();
            }
        }
    }
    public void readPrevPage(){
        Log.d(TAG, "reading prev page");
        if (page != 0 && bookUri != null){
            Reader reader = new Reader();
            BookSection bookSection = null;

            try{
                reader.setMaxContentPerSection(1000); // Max string length for the current page.
                reader.setIsIncludingTextContent(true); // Optional, to return the tags-excluded version.
                reader.setFullContent(bookUri); // Must call before readSection.
                bookSection = reader.readSection(page - 1);
                page--;
                while (bookSection.getSectionContent() == null){
                    page++;
                    bookSection = reader.readSection(page);
                    Log.d(TAG, "incrementing index - " + page);
                }
                String sectionContent = bookSection.getSectionContent(); // Returns content as html.
                String sectionTextContent = bookSection.getSectionTextContent(); // Excludes html tags.
                text = sectionTextContent;
                textView.setText(text);
            } catch (ReadingException e) {
                e.printStackTrace();
            } catch (OutOfPagesException e) {
                e.printStackTrace();
            }
        }
    }
}
