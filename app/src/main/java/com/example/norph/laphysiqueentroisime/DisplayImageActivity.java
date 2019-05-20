package com.example.norph.laphysiqueentroisime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;


public class DisplayImageActivity extends AppCompatActivity {

    public static final String INTENT_KEY_PHOTO_URL = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        String url = getIntent().getStringExtra(INTENT_KEY_PHOTO_URL);

        ImageView imageView = findViewById(R.id.image);

        GlideApp.with(this)
                .load(url)
                .placeholder(R.drawable.image_loading)
                .into(imageView);

    }
}
