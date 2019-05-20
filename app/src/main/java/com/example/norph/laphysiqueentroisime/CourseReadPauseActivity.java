package com.example.norph.laphysiqueentroisime;

import android.content.Intent;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class CourseReadPauseActivity extends AppCompatActivity {

    public static final String INTENT_KEY_CHAPTER_ID = "chapter_id";
    public static final String INTENT_KEY_SECTION_ID = "section_id";
    public static final String INTENT_KEY_TITLE = "title";
    public static final String INTENT_KEY_PART_NUMBER = "part_number";
    public static final String INTENT_KEY_PART_TITLE = "part_title";

    int mPartNumber;
    String mChapterId;
    String mTitle;
    String mPartTitle;
    String mSectionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_read_pause);

        Intent intent = getIntent();
        mPartNumber = intent.getIntExtra(INTENT_KEY_PART_NUMBER, -1);
        mPartTitle = intent.getStringExtra(INTENT_KEY_PART_TITLE);
        mChapterId = intent.getStringExtra(INTENT_KEY_CHAPTER_ID);
        mTitle = intent.getStringExtra(INTENT_KEY_TITLE);
        mSectionId = intent.getStringExtra(INTENT_KEY_SECTION_ID);

        LinearLayout root = findViewById(R.id.button_root);

        MaterialButton QCMButton = findViewById(R.id.qcm);
        MaterialButton nextPartButton = findViewById(R.id.gotonextpart);
        MaterialButton QuitButton = findViewById(R.id.quit);

        if(mPartNumber < 0){
            root.removeView(nextPartButton);
        } else {
            nextPartButton.setText(mPartTitle);
            nextPartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(CourseReadPauseActivity.this, CourseReaderActivity.class);
                    i.putExtra(CourseReaderActivity.INTENT_KEY_PART_NUMBER, mPartNumber);
                    i.putExtra(CourseReaderActivity.INTENT_KEY_TITLE, mTitle);
                    i.putExtra(CourseReaderActivity.INTENT_KEY_CHAPTER_ID, mChapterId);
                    i.putExtra(CourseReaderActivity.INTENT_KEY_SECTION_ID, mSectionId);
                    startActivity(i);
                    finish();
                }
            });
        }

        QCMButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseReadPauseActivity.this, QCMActivity.class);
                intent.putExtra(QCMActivity.INTENT_KEY_PART_ID, mPartNumber);
                intent.putExtra(QCMActivity.INTENT_KEY_SECTION_ID, mSectionId);
                intent.putExtra(QCMActivity.INTENT_KEY_CHAPTER_ID, mChapterId);
                startActivity(intent);
            }
        });

        QuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseReadPauseActivity.this, CourseActivity.class);
                intent.putExtra(CourseActivity.INTENT_KEY_CHAPTER_ID, mChapterId);
                intent.putExtra(CourseActivity.INTENT_KEY_SECTION_ID, mSectionId);
                startActivity(intent);
            }
        });


    }
}
