package com.example.gymapp;

import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.gymapp.ExerciseFocus;
import com.example.gymapp.R;
import com.squareup.picasso.Picasso;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.List;

public class FocusAdapter extends ArrayAdapter<ExerciseFocus> {

    private List<ExerciseFocus> exerciseList;
    private Context context;
    private String focusbody;

    public FocusAdapter(Context context, List<ExerciseFocus> exerciseList,String focusbody) {
        super(context, R.layout.list_item_focus, exerciseList);
        this.context = context;
        this.exerciseList = exerciseList;
        this.focusbody = focusbody;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_focus, null);
        }

        ExerciseFocus exercise = exerciseList.get(position);

        if (exercise != null) {
            TextView nameTextView = view.findViewById(R.id.exercise_name);
            TextView descriptionTextView = view.findViewById(R.id.exercise_description);
            TextView difficultyTextView = view.findViewById(R.id.exercise_difficulty);
            ImageView imageView = view.findViewById(R.id.image_view);
            Button addButton = view.findViewById(R.id.add_button);

            nameTextView.setText(exercise.getName());
            descriptionTextView.setText(exercise.getDescription());
            difficultyTextView.setText("Difficulty: " + exercise.getDifficulty());

            if (!exercise.getImageUrl().isEmpty()) {

                if (exercise.getImageUrl().endsWith(".gif")) {

                    Glide.with(getContext()).asGif().load(exercise.getImageUrl()).into(imageView);
                } else {

                    Glide.with(getContext())
                            .load(exercise.getImageUrl())
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(imageView);
                }
            } else {

                imageView.setImageResource(R.drawable.dumbell);
            }

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    insertExercise(exercise, focusbody);
                }
            });
        }

        return view;
    }

    private void insertExercise(ExerciseFocus exercise, String focusbody) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String username = MainActivity.GlobalsLogin.username;
                String[] field = {"username", "exname", "exdesc", "eximg", "exdifficulty", "focusbody"};
                String[] data = {username, exercise.getName(), exercise.getDescription(), exercise.getImageUrl(), exercise.getDifficulty(), focusbody};

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/insertExercise.php", "POST", field, data);
                if (putData.startPut() && putData.onComplete()) {
                    String result = putData.getResult();
                    if (result.equals("Exercise Inserted")) {
                        exerciseList.add(exercise);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Exercise inserted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Exercise inserted successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Failed to complete request", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
