package com.example.norph.laphysiqueentroisime.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.norph.laphysiqueentroisime.CourseReaderActivity;
import com.example.norph.laphysiqueentroisime.DisplayImageActivity;
import com.example.norph.laphysiqueentroisime.GlideApp;
import com.example.norph.laphysiqueentroisime.R;
import com.example.norph.laphysiqueentroisime.model.CourseElement;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CourseReaderAdapter extends ArrayAdapter<CourseElement> {

    StorageReference mStorageReference;

    public CourseReaderAdapter(Context context, List<CourseElement> list) {
        super(context, 0, list);

        mStorageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CourseElement element = getItem(position);
        View view;
        if (element.type.equals(CourseElement.TYPE_TEXT)) {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.layout_course_reader_paragraph, parent, false);
            createParagraph(view, element);
        } else if (element.type.equals(CourseElement.TYPE_IMAGE)) {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.layout_course_reader_figure, parent, false);
            createFigure(view, element);
        } else {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.layout_course_reader_subtitle, parent, false);
            ((TextView) view).setText(Html.fromHtml(element.text));
        }

        return view;
    }

    private void createParagraph(View view, CourseElement element) {
        TextView paragraph = (TextView) view;
        paragraph.setText(Html.fromHtml(element.text));
    }

    private void createFigure(final View view, CourseElement element) {
        final ImageView imageView = view.findViewById(R.id.image);
        TextView descriptionView = view.findViewById(R.id.description);

        descriptionView.setText(Html.fromHtml(element.text2));

        mStorageReference.child(element.text).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                final String url = uri.toString();
                GlideApp.with(view)
                        .load(url)
                        .placeholder(R.drawable.image_loading)
                        .error(R.drawable.image_not_available)
                        .into(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), DisplayImageActivity.class);
                        i.putExtra(DisplayImageActivity.INTENT_KEY_PHOTO_URL, url);
                        getContext().startActivity(i);
                    }
                });
            }
        });
    }
}
