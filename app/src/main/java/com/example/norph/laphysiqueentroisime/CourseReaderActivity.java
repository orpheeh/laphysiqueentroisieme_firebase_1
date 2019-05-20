package com.example.norph.laphysiqueentroisime;

import android.content.Intent;
import android.net.Uri;
import android.support.design.button.MaterialButton;
import android.support.transition.Fade;
import android.support.transition.Scene;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.norph.laphysiqueentroisime.adapter.CourseReaderAdapter;
import com.example.norph.laphysiqueentroisime.model.CourseElement;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class CourseReaderActivity extends AppCompatActivity {

    public static final String INTENT_KEY_CHAPTER_ID = "chapter_id";
    public static final String INTENT_KEY_SECTION_ID = "section_id";
    public static final String INTENT_KEY_TITLE = "title";
    public static final String INTENT_KEY_PART_NUMBER = "part_number";

    private Scene mScene;
    private Transition mTransition;

    ListView mListView;
    TextView mTitleView;
    TextView mPartTitleView;
    MaterialButton mButtonNext;
    MaterialButton mButtonBack;

    List<CourseElement> mList;
    int mPartNumber;
    long mPartCount;
    int mSlideNumber = 0;
    String mChapterId;
    String mTitle;
    String mSectionId;

    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_reader);

        Intent intent = getIntent();
        mPartNumber = intent.getIntExtra(INTENT_KEY_PART_NUMBER, 0);
        mChapterId = intent.getStringExtra(INTENT_KEY_CHAPTER_ID);
        mTitle = intent.getStringExtra(INTENT_KEY_TITLE);
        mSectionId = intent.getStringExtra(INTENT_KEY_SECTION_ID);

        //Load Scene
        mScene = Scene.getSceneForLayout((FrameLayout)findViewById(R.id.scene_root), R.layout.layout_course_reader_slide, this);
        mTransition = new Fade();

        //Load View
        mTitleView = findViewById(R.id.chapter_title);
        mPartTitleView = findViewById(R.id.part_title);
        mButtonNext = findViewById(R.id.btn_next);
        mButtonBack = findViewById(R.id.btn_back);

        mTitleView.setText(mTitle);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("course").child(mChapterId);
        TransitionManager.go(mScene, mTransition);
        display();
    }

    private void display(){
        mListView = findViewById(R.id.list_view);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                String partTitle = (String)dataSnapshot.child(mPartNumber+"").child("title").getValue();
                mPartTitleView.setText(partTitle);
                mPartCount = dataSnapshot.getChildrenCount();
                mList = getCourseContent(dataSnapshot.child(mPartNumber+"").child("slide").child(mSlideNumber+""));
                mListView.setAdapter(new CourseReaderAdapter(CourseReaderActivity.this, mList));

                mButtonNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSlideNumber++;
                        if(mSlideNumber < dataSnapshot.child(mPartNumber+"").child("slide").getChildrenCount()) {
                            mList.clear();
                            loadSlide(dataSnapshot);
                        } else {
                            Intent intent = new Intent(CourseReaderActivity.this, CourseReadPauseActivity.class);
                            intent.putExtra(CourseReadPauseActivity.INTENT_KEY_CHAPTER_ID, mChapterId);
                            if(mPartNumber+1 < mPartCount) {
                                intent.putExtra(CourseReadPauseActivity.INTENT_KEY_PART_NUMBER, mPartNumber+1);
                                intent.putExtra(CourseReadPauseActivity.INTENT_KEY_PART_TITLE,
                                        (String)dataSnapshot.child((mPartNumber+1)+"").child("title").getValue());
                            }
                            intent.putExtra(CourseReadPauseActivity.INTENT_KEY_SECTION_ID, mSectionId);
                            intent.putExtra(CourseReadPauseActivity.INTENT_KEY_TITLE, mTitle);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

                mButtonBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSlideNumber--;
                        if(mSlideNumber >= 0) {
                            loadSlide(dataSnapshot);
                        } else {
                            onBackPressed();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("ERROR TAG", "Database error");
            }
        });
    }

    private void loadSlide(DataSnapshot dataSnapshot){
        mList.clear();
        ArrayAdapter<CourseElement> adapter = (ArrayAdapter<CourseElement>) mListView.getAdapter();
        adapter.clear();
        mList = getCourseContent(dataSnapshot.child(mPartNumber+"").child("slide").child(mSlideNumber+""));
        adapter.addAll(mList);
    }

    private List<CourseElement> getCourseContent(DataSnapshot dataSnapshot){
        final List<CourseElement> list = new ArrayList<>();

        list.add(new CourseElement((String)dataSnapshot.child("subtitle").getValue(), CourseElement.TYPE_TITLE, ""));

        for(int i = 0; i < dataSnapshot.child("content").getChildrenCount(); i++){
            String content = (String)dataSnapshot.child("content").child(i+"").child("content").getValue();
            String type = (String)dataSnapshot.child("content").child(i+"").child("type").getValue();

            if(type.equals(CourseElement.TYPE_IMAGE)){
                String legend = (String)dataSnapshot.child("content").child(i+"").child("legend").getValue();
                list.add(new CourseElement(content, type, legend));
            } else {
                list.add(new CourseElement(content, type, ""));
            }
        }

        return list;
    }

}
