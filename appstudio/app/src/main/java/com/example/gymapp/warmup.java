package com.example.gymapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class warmup extends AppCompatActivity {

    private ListView listView;
    private Warmup_adapter adapter;
    private List<Exercise_Warm> exerciseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warmup);

        listView = findViewById(R.id.listView);
        exerciseList = new ArrayList<>();
        adapter = new Warmup_adapter(this, exerciseList);
        listView.setAdapter(adapter);
        String focusbody = getIntent().getStringExtra("TITLE_EXTRA");

        fetchExercises(focusbody);
    }

    private void fetchExercises(String focusbody) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = {"focusbody"};
                String[] data = {focusbody};

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/getWarmUp.php", "POST", field, data);

                if (putData.startPut() && putData.onComplete()) {
                    String result = putData.getResult();

                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String exname = jsonObject.getString("exname");
                            String exdesc = jsonObject.getString("exdesc");
                            String eximg = jsonObject.getString("eximg");
                            String lossWeight = jsonObject.getString("Loss Weight");

                            exerciseList.add(new Exercise_Warm(exname, exdesc, eximg, lossWeight));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(warmup.this, "Error parsing JSON data.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(warmup.this, "Failed to complete request", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
