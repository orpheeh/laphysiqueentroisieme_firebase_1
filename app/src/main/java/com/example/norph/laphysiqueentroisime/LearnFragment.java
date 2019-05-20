package com.example.norph.laphysiqueentroisime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.norph.laphysiqueentroisime.adapter.PartAdapter;
import com.example.norph.laphysiqueentroisime.model.Plan;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LearnFragment extends Fragment {

    private List<Plan> mList;

    private DatabaseReference mDatabaseReference;

    private RecyclerView mRecyclerView;
    private TextView mErrorView;
    private ProgressDialog mProgressDialog;

    private static final String LOG_TAG = "LearnFragment";

    public LearnFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_learn, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mErrorView = view.findViewById(R.id.error_view);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Patientez");
        mProgressDialog.show();

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList = createList(dataSnapshot);
                createRecyclerView();
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, "Database Error !");
                mErrorView.setText("Connectez vous à internet");
            }
        });

        return view;
    }

    private Plan getPlan(DataSnapshot dataSnapshot, String sectionId){
        String dsc = (String)dataSnapshot.child("plan").child(sectionId).child("desc").getValue();
        String n = (String)dataSnapshot.child("plan").child(sectionId).child("name").getValue();
        Plan p = new Plan(sectionId, n, dsc);
        return p;
    }

    private List<Plan> createList(DataSnapshot dataSnapshot){
        List<Plan> list = new ArrayList<>();
        list.add(getPlan(dataSnapshot, "chimie"));
        list.add(getPlan(dataSnapshot, "mecanique"));
        list.add(getPlan(dataSnapshot, "electricite"));
        list.add(getPlan(dataSnapshot, "optique"));
        return list;
    }

    private void createRecyclerView(){
        mRecyclerView.setAdapter(new PartAdapter(mList, new PartAdapter.OnItemClick() {
            @Override
            public void clicked(Plan plan) {
                Intent intent = new Intent(getActivity(), ChapterListActivity.class);
                intent.putExtra(ChapterListActivity.INTENT_KEY_SECTION_ID, plan.id);
                getActivity().startActivity(intent);
            }
        }));
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }
}
