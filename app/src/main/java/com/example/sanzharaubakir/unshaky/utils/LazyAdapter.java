package com.example.sanzharaubakir.unshaky.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sanzharaubakir.unshaky.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;


public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<String> bookUris;
    private static LayoutInflater inflater=null;

    public LazyAdapter(Activity activity, ArrayList<String> bookUris) {
        this.activity = activity;
        this.bookUris = bookUris;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return bookUris.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.recent_books_list_row, null);

        TextView title = (TextView)vi.findViewById(R.id.book_title); // title
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
        try {
            String bookUri = bookUris.get(position);
            EpubReader epubReader = new EpubReader();
            Book book = epubReader.readEpub(new FileInputStream(bookUri));
            List<String> titles = book.getMetadata().getTitles();
            String bookTitle = (titles.isEmpty() ? "No title" : titles.get(0));
            title.setText(bookTitle);
            byte [] array = book.getCoverImage().getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
            thumb_image.setImageBitmap(bitmap);
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return vi;
    }
}