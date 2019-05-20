package com.example.norph.laphysiqueentroisime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.norph.laphysiqueentroisime.GlideApp;
import com.example.norph.laphysiqueentroisime.R;
import com.example.norph.laphysiqueentroisime.model.Chapter;

import java.util.List;

public class ChapterAdapter extends ArrayAdapter<Chapter> {


    public ChapterAdapter(Context context, List<Chapter> list) {
        super(context, 0, list);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.layout_chapter, parent, false);
        }

        ImageView illustrationView = view.findViewById(R.id.illustration);
        TextView titleView = view.findViewById(R.id.title);
        TextView sectionView = view.findViewById(R.id.section);

        //TODO loadImage
        GlideApp.with(parent.getContext())
                .load(getItem(position).url)
                .placeholder(R.drawable.image_loading)
                .error(R.drawable.image_not_available)
                .into(illustrationView);

        titleView.setText(getItem(position).title);
        sectionView.setText(getItem(position).sectionName);

        return view;
    }
}
