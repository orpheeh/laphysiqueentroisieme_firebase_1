package com.example.norph.laphysiqueentroisime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.button.MaterialButton;
import android.support.transition.Fade;
import android.support.transition.Scene;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.norph.laphysiqueentroisime.model.QCM;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QCMActivity extends AppCompatActivity {

    public static final String INTENT_KEY_CHAPTER_ID = "chapterid";
    public static final String INTENT_KEY_PART_ID = "partid";
    public static final String INTENT_KEY_SECTION_ID = "secionid";

    private Scene mAnswerScene;
    private Scene mResponseScene;
    private Transition mTransition;
    private ViewGroup mRootScene;

    private int mQuestionCount;
    private int mCurrentQCM;
    private int mCorrectCount;
    private int mIncorrectCount;
    private int mCurrentResponse;
    private List<QCM> qcmList;

    private final int QCM_COUNT = 5;

    private String mChapterId;
    private String mSectionId;
    private int mPartId;

    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qcm);

        mChapterId = getIntent().getStringExtra(INTENT_KEY_CHAPTER_ID);
        mSectionId = getIntent().getStringExtra(INTENT_KEY_SECTION_ID);
        mPartId = getIntent().getIntExtra(INTENT_KEY_PART_ID, -1);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mRootScene = findViewById(R.id.scene_root);
        mAnswerScene = Scene.getSceneForLayout(mRootScene, R.layout.layout_scene_qcm_question, QCMActivity.this);
        mResponseScene = Scene.getSceneForLayout(mRootScene, R.layout.layout_scene_qcm_response, QCMActivity.this);
        mTransition = new Fade();
        TransitionManager.go(mAnswerScene, mTransition);


        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(mPartId < 0){
                    qcmList = getAllQCM(dataSnapshot);
                } else {
                    String qcmId = (String) dataSnapshot.child("course")
                            .child(mChapterId)
                            .child(mPartId + "")
                            .child("qcm").getValue();

                    qcmList = createList(dataSnapshot.child("qcm").child(qcmId), QCM_COUNT);
                }

                mQuestionCount = qcmList.size();

                TextView questionCountView = findViewById(R.id.total);
                questionCountView.setText(mQuestionCount+"");

                TransitionManager.go(mAnswerScene, mTransition);
                displayAnswer();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private List<QCM> createList(DataSnapshot dataSnapshot, int limite){
        List<QCM> list = new ArrayList<>();

        for(int i = 0; i < dataSnapshot.getChildrenCount(); i++){
            String question = (String)dataSnapshot.child(i+"").child("question").getValue();
            long response = (long)dataSnapshot.child(i+"").child("response").getValue();

            final long SUGG_COUNT = dataSnapshot.child(i+"").child("sugg").getChildrenCount();
            String[] arr = new String[(int)SUGG_COUNT];
            for(int j = 0; j < SUGG_COUNT; j++){
                arr[j] = (String)dataSnapshot.child(i+"").child("sugg").child(j+"").getValue();
            }

            list.add(new QCM(question, (int)response, arr));
        }

        Collections.shuffle(list);

        return list.subList(0, limite < 0 ? list.size() : limite);
    }

    private List<QCM> getAllQCM(DataSnapshot dataSnapshot){
        List<QCM> list = new ArrayList<>();

        for(int i = 0; i < dataSnapshot.child("course").child(mChapterId).getChildrenCount(); i++){
            String qcmId = (String)dataSnapshot.child("course")
                    .child(mChapterId)
                    .child(i+"")
                    .child("qcm").getValue();
            list.addAll(createList(dataSnapshot.child("qcm").child(qcmId), -1));
        }

        Collections.shuffle(list);

        return list;
    }

    private void displayAnswer(){
        QCM currentQCM = qcmList.get(mCurrentQCM);
        updateResultView();

        TextView questionView = findViewById(R.id.question);
        LinearLayout proprositionView = findViewById(R.id.button_root);

        questionView.setText(Html.fromHtml(currentQCM.question));

        if(currentQCM.propositions.length < 4){
            proprositionView.removeViews(currentQCM.propositions.length - 1,
                    4 - currentQCM.propositions.length);
        }

        for(int i = 0; i < currentQCM.propositions.length; i++) {
            MaterialButton button = (MaterialButton) proprositionView.getChildAt(i);
            button.setText(Html.fromHtml(currentQCM.propositions[i]));
            final int index = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentResponse = index;
                    TransitionManager.go(mResponseScene, mTransition);
                    displayResponse();
                }
            });
        }
    }

    private  void displayResponse(){
        TextView mainFeedBackView = findViewById(R.id.feedback);
        TextView responseView = findViewById(R.id.text1);
        TextView yourResponseView = findViewById(R.id.text2);
        LinearLayout yourResponseBackgroundView = findViewById(R.id.your_proposition);
        ImageView feedbackView = findViewById(R.id.feedback2);
        MaterialButton buttonNext = findViewById(R.id.btn_next);

        String mainfeedBack = "Incorrecte";
        int colorId = R.color.colorIncorrectDark;
        int backgroundId = R.color.colorIncorrectLigth;
        int feedbackId = R.drawable.ic_clear_black_24dp;
        int response = qcmList.get(mCurrentQCM).response;
        if(mCurrentResponse == response){
            colorId = R.color.colorCorrectDark;
            feedbackId = R.drawable.ic_done_black_24dp;
            mainfeedBack = "Correcte";
            backgroundId = R.color.colorCorrectLigth;
            mCorrectCount++;
        } else {
            mIncorrectCount++;
        }
        mainFeedBackView.setText(mainfeedBack);
        mainFeedBackView.setTextColor(getResources().getColor(colorId));
        yourResponseBackgroundView.setBackgroundColor(getResources().getColor(backgroundId));
        feedbackView.setImageDrawable(getResources().getDrawable(feedbackId));
        responseView.setText(Html.fromHtml(qcmList.get(mCurrentQCM).propositions[response]));
        yourResponseView.setText(Html.fromHtml(qcmList.get(mCurrentQCM).propositions[mCurrentResponse]));
        mCurrentQCM++;
        updateResultView();
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentQCM < qcmList.size()){
                    TransitionManager.go(mAnswerScene, mTransition);
                    displayAnswer();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(QCMActivity.this);

                    View view = QCMActivity.this.getLayoutInflater().inflate(R.layout.layout_qcm_finish, null);
                    TextView mention = view.findViewById(R.id.mention);
                    TextView note = view.findViewById(R.id.note);
                    TextView total = view.findViewById(R.id.total);

                    note.setText(mCorrectCount + "");
                    total.setText("/" + mQuestionCount);

                    final String GOOD = "Bravo! Vous avez bien compris le cours";
                    final String BAD = "Votre note est en dessous de la moyenne, nous vous conseillons de relir le cour puis refaire le teste.";

                    mention.setText((mCorrectCount > mQuestionCount / 2 ) ? GOOD : BAD);

                    builder.setView(view)
                            .setPositiveButton("Quitter", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onBackPressed();
                                }
                            })
                            .create()
                            .show();

                    if(mPartId < 0) {
                        String key = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
                        DatabaseReference data = FirebaseDatabase.getInstance().getReference();
                        data.child("user").child(key).child("lastNote").setValue(mCorrectCount + "/" + mQuestionCount);
                    }
                }
            }
        });
    }

    private void updateResultView(){
        TextView correctCountView = findViewById(R.id.correct);
        TextView incorrectCountView = findViewById(R.id.incorrect);
        TextView currentQuestionView = findViewById(R.id.current_question);

        correctCountView.setText(mCorrectCount+"");
        incorrectCountView.setText(mIncorrectCount+"");
        currentQuestionView.setText(mCurrentQCM +"");
    }
}
