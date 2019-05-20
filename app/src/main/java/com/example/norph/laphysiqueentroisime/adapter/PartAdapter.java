package com.example.norph.laphysiqueentroisime.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.norph.laphysiqueentroisime.R;
import com.example.norph.laphysiqueentroisime.model.Plan;

import java.util.List;

public class PartAdapter extends RecyclerView.Adapter<PartAdapter.PartViewHolder>{

    List<Plan> mList;
    OnItemClick mListener;

    public PartAdapter(List<Plan> list, OnItemClick l){
        mList = list;
        mListener = l;
    }

    @NonNull
    @Override
    public PartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_course_plan_view, viewGroup, false);

        return new PartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PartViewHolder partViewHolder, int i) {
        final Plan p = mList.get(i);

        partViewHolder.nameView.setText(p.name);
        partViewHolder.descriptionView.setText(p.desc);

        partViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.clicked(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class PartViewHolder extends RecyclerView.ViewHolder {

        TextView nameView;

        TextView descriptionView;

        public PartViewHolder(@NonNull View itemView) {
            super(itemView);

            nameView = itemView.findViewById(R.id.name);

            descriptionView = itemView.findViewById(R.id.desc);
        }
    }

    public interface OnItemClick {
        public void clicked(Plan plan);
    }
}
