package com.example.gymapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class messege extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private EditText messageEditText;
    private ImageButton sendButton;
    private String admin = "Admin";
    private Handler handler = new Handler(); // Handler for periodic fetching
    private Runnable fetchRunnable;
    private String username;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messege);

        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        // Initialize UI components
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        // Set up RecyclerView
        messageAdapter = new MessageAdapter(new ArrayList<>());
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);
        recyclerViewMessages.scrollToPosition(messageAdapter.getItemCount() - 1);

        // Get username from global state or intent
        username = MainActivity.GlobalsLogin.username; // Ensure this is set correctly

        // Set send button listener
        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(username, message);
                messageEditText.setText("");
            } else {
                Toast.makeText(messege.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize fetch Runnable
        fetchRunnable = new Runnable() {
            @Override
            public void run() {
                fetchMessages(username);
                handler.postDelayed(this, 3000); // Repeat every 3 seconds
            }
        };

        // Start fetching messages
        handler.post(fetchRunnable);
    }

    private void sendMessage(String sender, String message) {
        String url = "https://calestechsync.dermocura.net/calestechsync/sendMessage.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Optionally handle the response
                    // For example, you can parse a success message
                },
                error -> {
                    Toast.makeText(messege.this, "Error sending message", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sender", sender);
                params.put("receiver", admin);
                params.put("message", message);
                return params;
            }
        };

        requestQueue.add(request);
    }

    private void fetchMessages(String username) {
        String url = "https://calestechsync.dermocura.net/calestechsync/getMessages.php?user=" + username;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<Message> messages = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject messageObject = response.getJSONObject(i);
                            String sender = messageObject.getString("sender");
                            String message = messageObject.getString("message");
                            String timestamp = messageObject.optString("timestamp"); // Assuming timestamp is returned

                            messages.add(new Message(sender, message, timestamp));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    messageAdapter.updateMessages(messages);
                    recyclerViewMessages.scrollToPosition(messageAdapter.getItemCount() - 1);
                },
                error -> {
                    Toast.makeText(messege.this, "Error fetching messages", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(fetchRunnable); // Stop fetching messages
        requestQueue.cancelAll(this); // Cancel all pending requests
    }

    // Message Model Class
    public static class Message {
        private String sender;
        private String message;
        private String timestamp;

        public Message(String sender, String message, String timestamp) {
            this.sender = sender;
            this.message = message;
            this.timestamp = timestamp;
        }

        // Getters
        public String getSender() { return sender; }
        public String getMessage() { return message; }
        public String getTimestamp() { return timestamp; }
    }

    // RecyclerView Adapter
    public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final List<Message> messages;
        private final int VIEW_TYPE_SENT = 1;
        private final int VIEW_TYPE_RECEIVED = 2;

        public MessageAdapter(List<Message> messages) {
            this.messages = messages;
        }

        public void updateMessages(List<Message> newMessages) {
            messages.clear();
            messages.addAll(newMessages);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            Message message = messages.get(position);
            if (message.getSender().equals(username)) {
                return VIEW_TYPE_SENT;
            } else {
                return VIEW_TYPE_RECEIVED;
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_SENT) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
                return new SentMessageViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
                return new ReceivedMessageViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Message message = messages.get(position);
            if (holder.getItemViewType() == VIEW_TYPE_SENT) {
                ((SentMessageViewHolder) holder).bind(message);
            } else {
                ((ReceivedMessageViewHolder) holder).bind(message);
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        // ViewHolder for Sent Messages
        class SentMessageViewHolder extends RecyclerView.ViewHolder {
            TextView textViewMessage, textViewTimestamp;

            SentMessageViewHolder(View itemView) {
                super(itemView);
                textViewMessage = itemView.findViewById(R.id.textViewMessage);
                textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
            }

            void bind(Message message) {
                textViewMessage.setText(message.getMessage());
                textViewTimestamp.setText(formatTimestamp(message.getTimestamp()));
            }
        }

        // ViewHolder for Received Messages
        class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
            TextView textViewSender, textViewMessage, textViewTimestamp;

            ReceivedMessageViewHolder(View itemView) {
                super(itemView);
                textViewSender = itemView.findViewById(R.id.textViewSender);
                textViewMessage = itemView.findViewById(R.id.textViewMessage);
                textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
            }

            void bind(Message message) {
                textViewSender.setText(message.getSender());
                textViewMessage.setText(message.getMessage());
                textViewTimestamp.setText(formatTimestamp(message.getTimestamp()));
            }
        }

        // Helper method to format timestamp
        private String formatTimestamp(String timestamp) {
            // Assuming timestamp is in ISO 8601 format, e.g., "2023-10-27T12:00:00Z"
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = inputFormat.parse(timestamp);

                SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                return outputFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    }
}
