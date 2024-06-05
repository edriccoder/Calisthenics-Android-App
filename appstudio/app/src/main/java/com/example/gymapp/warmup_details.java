package com.example.gymapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class warmup_details extends AppCompatActivity {

    private TextView timerTextView;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private boolean timerRunning = false;

    private TextView repsCountTextView;
    private int repsCount = 0;

    private Button startButton;
    private Button stopButton;
    private Button increaseRepsButton;
    private Button decreaseRepsButton;
    private Button done_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warmup_details);

        ImageView detailsImg = findViewById(R.id.details_img);
        TextView detailsName = findViewById(R.id.details_name);
        TextView detailsDesc = findViewById(R.id.details_desc);
        TextView detailsLossWeight = findViewById(R.id.details_lossWeight);
        timerTextView = findViewById(R.id.timer_text_view);

        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);
        increaseRepsButton = findViewById(R.id.increase_reps_button);
        decreaseRepsButton = findViewById(R.id.decrease_reps_button);
        repsCountTextView = findViewById(R.id.reps_count_text);

        done_button = findViewById(R.id.done_button);
        String username = MainActivity.GlobalsLogin.username;

        Intent intent = getIntent();
        String exname = intent.getStringExtra("exname");
        String exdesc = intent.getStringExtra("exdesc");
        String eximg = intent.getStringExtra("eximg");
        String lossWeight = intent.getStringExtra("lossWeight");

        detailsName.setText(exname);
        detailsDesc.setText(exdesc);
        detailsLossWeight.setText(lossWeight);

        if (eximg != null && !eximg.isEmpty()) {
            if (eximg.endsWith(".gif")) {
                Glide.with(this).asGif().load(eximg).into(detailsImg); // Load gif image
            } else {
                Glide.with(this)
                        .load(eximg)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(detailsImg); // Load normal image
            }
        } else {
            detailsImg.setImageResource(R.drawable.abs); // Set default image if imageUrl is empty
        }

        // Set the timer duration in milliseconds (e.g., 10 minutes)
        timeLeftInMillis = 10 * 60 * 1000;
        updateCountDownText();

        showChoiceDialog();

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] bmiField = {"username", "exname", "eximg"};
                String[] bmiData = {username, exname, eximg};

                PutData putBmiData = new PutData("https://calestechsync.dermocura.net/calestechsync/trackingExercise.php", "POST", bmiField, bmiData);
                putBmiData.startPut();
                if (putBmiData.onComplete()) {
                    String bmiResult = putBmiData.getResult();
                    Toast.makeText(warmup_details.this, bmiResult, Toast.LENGTH_SHORT).show();
                    if (bmiResult.equals("Tracking exercise created Successfully")) {
                    }
                }

            }
        });

    }


    private void showChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Exercise Mode")
                .setMessage("Do you want to exercise based on Timer or Reps?")
                .setPositiveButton("Timer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showTimerMode();
                    }
                })
                .setNegativeButton("Reps", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showRepsMode();
                    }
                });
        builder.create().show();
    }

    private void showTimerMode() {
        timerTextView.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);

        repsCountTextView.setVisibility(View.GONE);
        increaseRepsButton.setVisibility(View.GONE);
        decreaseRepsButton.setVisibility(View.GONE);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timerRunning) {
                    startTimer();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerRunning) {
                    stopTimer();
                }
            }
        });
    }

    private void showRepsMode() {
        done_button.setVisibility(View.VISIBLE);
        timerTextView.setVisibility(View.GONE);
        startButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.GONE);

        repsCountTextView.setVisibility(View.VISIBLE);
        increaseRepsButton.setVisibility(View.VISIBLE);
        decreaseRepsButton.setVisibility(View.VISIBLE);

        increaseRepsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseReps();
            }
        });

        decreaseRepsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseReps();
            }
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                // Handle timer finish event
                timerRunning = false;
                timerTextView.setText("Exercise completed!");
            }
        }.start();
        done_button.setVisibility(View.VISIBLE);

        timerRunning = true;
    }

    private void stopTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        updateCountDownText();
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(timeLeftFormatted);
    }

    private void increaseReps() {
        repsCount++;
        repsCountTextView.setText("Reps: " + repsCount);
    }

    private void decreaseReps() {
        if (repsCount > 0) {
            repsCount--;
            repsCountTextView.setText("Reps: " + repsCount);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

}
