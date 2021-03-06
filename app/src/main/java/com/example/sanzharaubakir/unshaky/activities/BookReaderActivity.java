package com.example.sanzharaubakir.unshaky.activities;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sanzharaubakir.unshaky.R;
import com.example.sanzharaubakir.unshaky.listeners.OnSwipeTouchListener;
import com.example.sanzharaubakir.unshaky.models.ModelListener;
import com.example.sanzharaubakir.unshaky.models.UnshakyModel;
import com.example.sanzharaubakir.unshaky.models.hmm.HiddenMarkovModel;
import com.example.sanzharaubakir.unshaky.models.spring_dumper.SpringDumper;
import com.example.sanzharaubakir.unshaky.sensor.Accelerometer;
import com.github.mertakdut.BookSection;
import com.github.mertakdut.Reader;
import com.github.mertakdut.exception.OutOfPagesException;
import com.github.mertakdut.exception.ReadingException;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

public class BookReaderActivity extends Activity implements ModelListener {
    private static final String TAG = BookReaderActivity.class.getSimpleName();

    private View layoutSensor;
    private TextView textView;
    private ImageView coverImageView;

    private SensorManager sensorManager;
    private Accelerometer accelerometer;

    private UnshakyModel springDumper;
    private UnshakyModel hiddenMarkovModel;
    private List<UnshakyModel> modelList = new ArrayList();

    private String text;
    private int page = 0;
    private String bookUri, modelType;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reading_view);
        initViews();
        Resources resources = getResources();
        Bundle bundle = getIntent().getBundleExtra(resources.getString(R.string.arguments));
        String uri = bundle.getString(resources.getString(R.string.book_uri));
        String type = bundle.getString(resources.getString(R.string.model_type));
        bookUri = uri;
        modelType = type;
        RelativeLayout frameLayout = findViewById(R.id.reading_view_layout);
        textView = (TextView) findViewById(R.id.txt_test);
        coverImageView = (ImageView) findViewById(R.id.cover_image);
        if (!showCoverImage()){
            readPage(0);
        }
        frameLayout.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeRight() {
                readPrevPage();
            }
            public void onSwipeLeft() {
                readNextPage();
            }

        });
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = new Accelerometer(sensorManager);

            springDumper = new SpringDumper(accelerometer);
            springDumper.setListener(this);
            modelList.add(springDumper);

            hiddenMarkovModel = new HiddenMarkovModel(accelerometer);
            hiddenMarkovModel.setListener(this);
            modelList.add(hiddenMarkovModel);
        }
    }

    private boolean showCoverImage() {
        try {
            EpubReader epubReader = new EpubReader();
            Book book = epubReader.readEpub(new FileInputStream(bookUri));
            if (book.getCoverImage() == null || book.getCoverImage().getData() == null){
                return false;
            }
            byte[] array = book.getCoverImage().getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
            coverImageView.setImageBitmap(bitmap);
            if (textView.getVisibility() == View.VISIBLE){
                textView.setVisibility(View.GONE);
                coverImageView.setVisibility(View.VISIBLE);
            }
        }
        catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        for(UnshakyModel model : modelList) {
            if (model.getTag().equals(modelType)) {
                model.enable();
            } else {
                model.disable();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        for(UnshakyModel model : modelList) {
            model.disable();
        }
    }


    private void initViews() {
        View layoutRoot = findViewById(R.id.reading_view_layout);

        layoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                springDumper.reset();
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
        if (page > 0) {
            page--;
            readPage(page);
        }
        else{
            showCoverImage();
        }

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
                if (coverImageView.getVisibility() == View.VISIBLE){
                    coverImageView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }
                page = pageToRead;
            } catch (ReadingException e) {
                e.printStackTrace();
            } catch (OutOfPagesException e) {
                e.printStackTrace();
            }
        }

    }
}
