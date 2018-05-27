package com.example.sanzharaubakir.unshaky.activities;

import android.app.Activity;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sanzharaubakir.unshaky.R;
import com.example.sanzharaubakir.unshaky.sensor.Accelerometer;
import com.example.sanzharaubakir.unshaky.sensor.AccelerometerListener;
import com.example.sanzharaubakir.unshaky.utils.OnSwipeTouchListener;
import com.github.mertakdut.BookSection;
import com.github.mertakdut.Reader;
import com.github.mertakdut.exception.OutOfPagesException;
import com.github.mertakdut.exception.ReadingException;

import org.jetbrains.annotations.NotNull;

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
        RelativeLayout frameLayout = findViewById(R.id.reading_view_layout);
        textView = (TextView) findViewById(R.id.txt_test);

        frameLayout.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
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
        View layoutRoot = findViewById(R.id.reading_view_layout);

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
        page++;
        readPage(page);
    }
    public void readPrevPage(){
        Log.d(TAG, "reading prev page");
        readPage(page);

    }
    public void readPage(int pageToRead){
        if (page != 0 && bookUri != null){
            Reader reader = new Reader();
            BookSection bookSection = null;

            try{
                reader.setMaxContentPerSection(1000); // Max string length for the current page.
                reader.setIsIncludingTextContent(true); // Optional, to return the tags-excluded version.
                reader.setFullContent(bookUri); // Must call before readSection.
                bookSection = reader.readSection(pageToRead);
                while (bookSection.getSectionContent() == null){
                    pageToRead++;
                    bookSection = reader.readSection(pageToRead);
                }
                String sectionContent = bookSection.getSectionContent(); // Returns content as html.
                String sectionTextContent = bookSection.getSectionTextContent(); // Excludes html tags.
                text = sectionTextContent;
                textView.setText(sectionTextContent);
                page = pageToRead;
            } catch (ReadingException e) {
                e.printStackTrace();
            } catch (OutOfPagesException e) {
                e.printStackTrace();
            }
        }

    }
}
