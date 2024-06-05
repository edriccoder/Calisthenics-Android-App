// Warmup_adapter.java
package com.example.gymapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

public class Warmup_adapter extends ArrayAdapter<Exercise_Warm> {

    private Context context;
    private List<Exercise_Warm> exerciseList;

    public Warmup_adapter(Context context, List<Exercise_Warm> exerciseList) {
        super(context, 0, exerciseList);
        this.context = context;
        this.exerciseList = exerciseList;
    }

    private static class ViewHolder {
        TextView exname;
        TextView exdesc;
        ImageView eximg;
        TextView lossWeight;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.warmup_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.exname = convertView.findViewById(R.id.exname);
            viewHolder.exdesc = convertView.findViewById(R.id.exdesc);
            viewHolder.eximg = convertView.findViewById(R.id.eximg);
            viewHolder.lossWeight = convertView.findViewById(R.id.lossWeight);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Exercise_Warm exercise = getItem(position);

        if (exercise != null) {
            viewHolder.exname.setText(exercise.getExname());
            viewHolder.exdesc.setText(exercise.getExdesc());
            viewHolder.lossWeight.setText(exercise.getLossWeight());

            if (!exercise.getEximg().isEmpty()) {
                if (exercise.getEximg().endsWith(".gif")) {
                    Glide.with(context).asGif().load(exercise.getEximg()).into(viewHolder.eximg);
                } else {
                    Glide.with(context)
                            .load(exercise.getEximg())
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(viewHolder.eximg);
                }
            } else {
                viewHolder.eximg.setImageResource(R.drawable.dumbell);
            }
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, warmup_details.class);
                intent.putExtra("exname", exercise.getExname());
                intent.putExtra("exdesc", exercise.getExdesc());
                intent.putExtra("eximg", exercise.getEximg());
                intent.putExtra("lossWeight", exercise.getLossWeight());
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
