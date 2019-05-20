package com.example.norph.laphysiqueentroisime;

import android.content.Intent;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CourseActivity extends AppCompatActivity {

    public static final String INTENT_KEY_SECTION_ID = "sectionid";

    public static final String INTENT_KEY_CHAPTER_ID = "chapterid";

    private String mSectionId;
    private String mChapterId;

    private DatabaseReference mDatabaseRefenrence;

    private TextView mTitleView;
    private TextView mSummuryView;
    private TextView mQCMNote;

    private FloatingActionButton mReadButton;
    private MaterialButton mTestButton;
    private MaterialButton mTrainingButton;
    private ImageView mIllustrationView;

    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        mSectionId = getIntent().getStringExtra(INTENT_KEY_SECTION_ID);
        mChapterId = getIntent().getStringExtra(INTENT_KEY_CHAPTER_ID);

        mTitleView = findViewById(R.id.title);
        mSummuryView = findViewById(R.id.summury);
        mReadButton = findViewById(R.id.btn_read);
        mTestButton = findViewById(R.id.btn_test);
        mTrainingButton = findViewById(R.id.btn_training);
        mIllustrationView = findViewById(R.id.ill);

        mDatabaseRefenrence = FirebaseDatabase.getInstance().getReference();

        mDatabaseRefenrence.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {

                DataSnapshot dataSnapshot = dataSnapshot1
                        .child("plan")
                        .child(mSectionId)
                        .child(mChapterId);

                String title = (String)dataSnapshot.child("title").getValue();
                String url = (String)dataSnapshot.child("illustration_url").getValue();
                String summury = (String)dataSnapshot.child("summury").getValue();

                mTitleView.setText(title);
                mTitle = title;
                mQCMNote = findViewById(R.id.note);
                mSummuryView.setText(Html.fromHtml(summury));

                String key = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
                String note = (String)dataSnapshot1.child("user").child(key).child("lastNote").getValue();
                mQCMNote.setText(note);

                GlideApp.with(CourseActivity.this).load(url)
                        .placeholder(R.drawable.image_loading)
                        .error(R.drawable.image_not_available)
                        .into(mIllustrationView);

                mReadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(CourseActivity.this, CourseReaderActivity.class);
                        i.putExtra(CourseReaderActivity.INTENT_KEY_PART_NUMBER, 0);
                        i.putExtra(CourseReaderActivity.INTENT_KEY_TITLE, mTitle);
                        i.putExtra(CourseReaderActivity.INTENT_KEY_SECTION_ID, mSectionId);
                        i.putExtra(CourseReaderActivity.INTENT_KEY_CHAPTER_ID, mChapterId);
                        startActivity(i);
                    }
                });

                mTestButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(CourseActivity.this, QCMActivity.class);
                        i.putExtra(QCMActivity.INTENT_KEY_CHAPTER_ID, mChapterId);
                        i.putExtra(QCMActivity.INTENT_KEY_SECTION_ID, mSectionId);
                        startActivity(i);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
