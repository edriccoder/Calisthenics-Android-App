package com.example.gymapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.ArrayList;
import java.util.List;

public class exfocus_adapter extends ArrayAdapter<focusExercise> {

    private Context mContext;
    private List<focusExercise> mExerciseList;
    private String username;

    public exfocus_adapter(Context context, List<focusExercise> exerciseList, String username) {
        super(context, 0, exerciseList);
        mContext = context;
        mExerciseList = exerciseList;
        this.username = username;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(mContext).inflate(R.layout.exlist_item, parent, false);
        }

        focusExercise currentExercise = mExerciseList.get(position);

        ImageView imageView = listItemView.findViewById(R.id.imageView);
        TextView exerciseNameTextView = listItemView.findViewById(R.id.exerciseNameTextView);
        TextView descriptionTextView = listItemView.findViewById(R.id.descriptionTextView);
        TextView difficultyTextView = listItemView.findViewById(R.id.difficultyTextView);
        TextView activityTextView = listItemView.findViewById(R.id.activityTextView);
        Button deleteButton = listItemView.findViewById(R.id.deleteButton);

        exerciseNameTextView.setText(currentExercise.getExerciseName());
        activityTextView.setText(currentExercise.getActivity());
        difficultyTextView.setText("Difficulty: " + currentExercise.getDifficulty());
        descriptionTextView.setText(currentExercise.getDescription());

        if (!currentExercise.getImageUrl().isEmpty()) {
            if (currentExercise.getImageUrl().endsWith(".gif")) {
                Glide.with(mContext).asGif().load(currentExercise.getImageUrl()).into(imageView);
            } else {
                Glide.with(mContext)
                        .load(currentExercise.getImageUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView);
            }
        } else {
            imageView.setImageResource(R.drawable.dumbell);
        }

        // Set OnClickListener to open details activity
        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, focusDetail.class);
                intent.putParcelableArrayListExtra("exerciseList", new ArrayList<>(mExerciseList));
                intent.putExtra("currentIndex", position);
                intent.putExtra("username", username);
                mContext.startActivity(intent);
            }
        });

        // Set OnClickListener for the delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeExercise(currentExercise);
            }
        });

        return listItemView;
    }

    @Override
    public int getCount() {
        return mExerciseList.size();
    }

    private void removeExercise(focusExercise exercise) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = {"exname", "username"};
                String[] data = {exercise.getExerciseName(), username};

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/deleteExercise.php", "POST", field, data);
                if (putData.startPut() && putData.onComplete()) {
                    String result = putData.getResult();
                    if (result.equals("Exercise Deleted")) {
                        mExerciseList.remove(exercise);
                        notifyDataSetChanged();
                        Toast.makeText(mContext, "Exercise deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Failed to delete exercise: " + result, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "Failed to complete request", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
