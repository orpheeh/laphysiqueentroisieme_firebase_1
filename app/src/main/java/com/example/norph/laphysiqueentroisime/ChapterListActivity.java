package com.example.norph.laphysiqueentroisime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.norph.laphysiqueentroisime.adapter.ChapterAdapter;
import com.example.norph.laphysiqueentroisime.model.Chapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChapterListActivity extends AppCompatActivity {

    public static final String INTENT_KEY_SECTION_ID = "sectionid";

    private static final String LOG_TAG = "ChapterListActivity";

    ListView mListView;
    TextView mErrorView;

    List<Chapter> mList = new ArrayList<>();

    private DatabaseReference mDatabaseReference;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String sectionId = getIntent().getStringExtra(INTENT_KEY_SECTION_ID);

        mListView = findViewById(R.id.list_view);
        mErrorView = findViewById(R.id.error_view);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("plan").child(sectionId);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Patientez");
        mProgressDialog.show();

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                long chapterCount = dataSnapshot.getChildrenCount() - 2;
                Log.i(LOG_TAG, "Nombre de chapitre en " + sectionId + " : " + chapterCount);
                String sectionName = (String)dataSnapshot.child("name").getValue();
                getSupportActionBar().setTitle(sectionName);
                mList.clear();
                for(int i = 0; i < chapterCount; i++){
                    String title = (String)dataSnapshot.child(sectionId + i).child("title").getValue();
                    String url = (String)dataSnapshot.child(sectionId + i).child("illustration_url").getValue();
                    mList.add(new Chapter(sectionName, title, false, url));
                }

                final ChapterAdapter adapter = new ChapterAdapter(ChapterListActivity.this, mList);

                mListView.setAdapter(adapter);

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(ChapterListActivity.this, CourseActivity.class);
                        intent.putExtra(CourseActivity.INTENT_KEY_SECTION_ID, sectionId);
                        intent.putExtra(CourseActivity.INTENT_KEY_CHAPTER_ID, sectionId + position);
                        startActivity(intent);
                    }
                });
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, "Database read error");
                mErrorView.setText("Connectez vous à internet");
            }
        });
    }
}
