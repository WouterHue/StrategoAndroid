package com.example.GitHub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.Android.R;

/**
 * Created by wouter on 21/02/14.
 */
public class ImageAdapter extends BaseAdapter {
    private Context context;
    private final String[] imageTitles;

    public ImageAdapter(Context context, String[] imageTitles) {
        this.context = context;
        this.imageTitles = imageTitles;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {
            gridView = new View(context);

            // get layout from achievement_image.xml
            gridView = inflater.inflate(R.layout.achievement_image, null);

            // set value into textview
            TextView textView = (TextView) gridView
                    .findViewById(R.id.grid_item_label);
            textView.setText(imageTitles[position]);

            // set image based on selected text
            ImageView imageView = (ImageView) gridView
                    .findViewById(R.id.grid_item_image);

            String title = imageTitles[position];

            if (title.equalsIgnoreCase("like a boss")) {
                imageView.setImageResource(R.drawable.acheivements_1);
            }
            else if(title.equalsIgnoreCase("good start")){
                imageView.setImageResource(R.drawable.achievements_2);
            }
        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return imageTitles.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
