package com.example.gymapp;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class emg_bluetooth extends AppCompatActivity {

    private static final String TAG = "EMGBluetoothActivity";
    private static final String DEVICE_NAME = "EMG_Sensor";

    private static final UUID EMG_SERVICE_UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb");
    private static final UUID EMG_VALUE_CHARACTERISTIC_UUID = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb");
    private static final UUID CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic emgValueCharacteristic;

    private TextView bluetoothStatusText;
    private TextView emgValueText;
    private Button connectBluetoothButton;
    private ProgressBar emgLevelGauge;

    private LineChart emgLineChart;
    private LineData lineData;
    private LineDataSet lineDataSet;
    private ArrayList<Entry> emgEntries = new ArrayList<>();
    private int timeIndex = 0;
    private int stressScore = 0;
    private int stressThreshold = 210;

    private TextView timerTextView;
    private Button startTimerButton;
    private Button skipTimerButton;
    private ImageView question;
    private CountDownTimer countDownTimer;
    private CountDownTimer restCountDownTimer;
    private boolean isTimerRunning = false;
    private long remainingTime = 60000; // Initialize remaining time to 60 seconds
    private int easySeconds = 0;
    private int mediumSeconds = 0;
    private int hardSeconds = 0;
    private int belowEasySeconds = 0;
    private int previousEmgLevel = -1; // To track changes in EMG level
    private int currentEmgLevel = -1; // Initialize with an invalid level
    private long lastEmgChangeTime = 0; // To track the last change timestamp

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable timeTrackerRunnable;


    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emg_bluetooth);

        bluetoothStatusText = findViewById(R.id.bluetoothStatusText);
        connectBluetoothButton = findViewById(R.id.connectBluetoothButton);
        emgLevelGauge = findViewById(R.id.emgLevelGauge);
        emgValueText = findViewById(R.id.emgValueText);
        emgLineChart = findViewById(R.id.emgLineChart);
        ImageView bodyFocusImage = findViewById(R.id.imageView6);
        question = findViewById(R.id.question);

        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(emg_bluetooth.this, guide_pressure.class);
                startActivity(intent1);
            }
        });

        // UI setup and Bluetooth initialization
        boolean hideChart = getIntent().getBooleanExtra("hide_chart", false);
        if (hideChart) {
            emgLineChart.setVisibility(View.GONE);
        }

        boolean hideimage = getIntent().getBooleanExtra("hide_image", false);
        if (hideimage) {
            bodyFocusImage.setVisibility(View.GONE);
        }

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            bluetoothStatusText.setText("Bluetooth is not enabled");
            requestBluetoothEnable();
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        connectBluetoothButton.setOnClickListener(v -> {
            if (hasBluetoothPermissions()) {
                startScan();
            } else {
                requestBluetoothPermissions();
            }
        });

        // Load body focus image based on passed intent data
        String focus = getIntent().getStringExtra("focus");
        if (focus != null) {
            switch (focus) {
                case "Arms":
                    bodyFocusImage.setImageResource(R.drawable.arm);
                    break;
                case "Chest":
                    bodyFocusImage.setImageResource(R.drawable.arm);
                    break;
                case "Abs":
                    bodyFocusImage.setImageResource(R.drawable.abs);
                    break;
                case "Legs":
                    bodyFocusImage.setImageResource(R.drawable.legs);
                    break;
                case "Back":
                    bodyFocusImage.setImageResource(R.drawable.arm);
                    break;
                default:
                    bodyFocusImage.setImageResource(R.drawable.backbody);
                    break;
            }
        }

        setupChart();

        timerTextView = findViewById(R.id.timerTextView);
        startTimerButton = findViewById(R.id.startTimerButton);
        skipTimerButton = findViewById(R.id.skipTimerButton);

        startTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTimerRunning) {
                    startTimer();
                }
            }
        });

        skipTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimerRunning) {
                    skipTimer();  // Call the skip function
                }
            }
        });

        initializeTimeTracker();
    }

    private void skipTimer() {
        if (isTimerRunning) {
            countDownTimer.cancel(); // Cancel the running timer
            isTimerRunning = false; // Mark the timer as not running
            insertDurationToDatabase((int) (remainingTime / 1000));
            remainingTime = 60000;
            showRestDialog(); // Show the rest dialog immediately
        }
    }

    private void startTimer() {
        isTimerRunning = true;
        startTimerButton.setEnabled(false); // Disable while timer is running

        countDownTimer = new CountDownTimer(remainingTime, 1000) { // Use remainingTime which is reset to 60 seconds
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = millisUntilFinished; // Update remaining time
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                timerTextView.setText(String.valueOf(secondsRemaining));
            }

            @Override
            public void onFinish() {
                timerTextView.setText("0");
                insertDurationToDatabase(60);
                showRestDialog(); // Show rest dialog when timer finishes
                isTimerRunning = false; // Reset running state
            }
        }.start();
    }

    private void showRestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rest Time");
        builder.setMessage("You need to rest for 30 seconds.");

        // Create a custom view for the rest timer within a FrameLayout to center the text
        final FrameLayout frameLayout = new FrameLayout(this);
        final TextView restTimerTextView = new TextView(this);

        // Style the timer text view
        restTimerTextView.setText("30");
        restTimerTextView.setTextSize(48); // Larger text size for emphasis
        restTimerTextView.setPadding(50, 50, 50, 50); // Add padding around the text
        restTimerTextView.setGravity(Gravity.CENTER); // Center text within the view
        restTimerTextView.setTextColor(Color.BLACK); // Set text color

        // Set background color and padding for FrameLayout to center TextView within dialog
        frameLayout.setPadding(20, 40, 20, 40); // Add some padding around the timer
        frameLayout.setBackgroundColor(Color.LTGRAY); // Light gray background
        frameLayout.addView(restTimerTextView);

        builder.setView(frameLayout);

        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing, we want the dialog to stay open during the rest period
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        restCountDownTimer = new CountDownTimer(30000, 1000) { // 30 seconds rest timer
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                restTimerTextView.setText(String.valueOf(secondsRemaining));
            }

            @Override
            public void onFinish() {
                restTimerTextView.setText("0");
                dialog.dismiss();
                Toast.makeText(emg_bluetooth.this, "Rest period over, starting the next set.", Toast.LENGTH_SHORT).show();

                // Reset the timer back to 60 seconds after the rest
                remainingTime = 60000; // Reset remaining time to 60 seconds
                startTimer(); // Start the timer again with the full 60 seconds
            }
        }.start();
    }

    private void insertDurationToDatabase(int remainingTime) {
        String username = MainActivity.GlobalsLogin.username;
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Create data to send to PHP script
        HashMap<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("time_seconds", String.valueOf(remainingTime));
        data.put("date", date);

        // Convert HashMap to arrays
        String[] keys = data.keySet().toArray(new String[0]);
        String[] values = data.values().toArray(new String[0]);

        PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/insertDuration.php", "POST", keys, values);
        if (putData.startPut()) {
            if (putData.onComplete()) {
                String result = putData.getResult();
                if (result.equals("Success")) {
                    Toast.makeText(this, "Duration inserted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error inserting duration", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void resetTimer() {
        isTimerRunning = false;
        startTimerButton.setEnabled(true); // Enable the start button again
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (restCountDownTimer != null) {
            restCountDownTimer.cancel();
        }
        // Remove handler callbacks
        handler.removeCallbacks(timeTrackerRunnable);

        // Close Bluetooth GATT connection
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
            bluetoothGatt = null;
        }

        // Shutdown the executor service
        executorService.shutdownNow();
    }

    private void setupChart() {

        lineDataSet = new LineDataSet(emgEntries, "EMG Values");
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Smooth curve
        lineData = new LineData(lineDataSet);
        emgLineChart.setData(lineData);
        emgLineChart.getDescription().setEnabled(false);
        emgLineChart.getLegend().setEnabled(false);
        emgLineChart.setTouchEnabled(false);
        emgLineChart.setDragEnabled(false);
        emgLineChart.setScaleEnabled(false);
    }

    private void updateChart(float emgValue) {
        if (emgValue < 0 || emgValue > 4095) {
            Log.e("ChartError", "Invalid EMG value: " + emgValue);
            return;
        }

        emgEntries.add(new Entry(timeIndex++, emgValue));

        if (emgEntries.size() > 100) {
            emgEntries.remove(0);
        }

        lineDataSet.notifyDataSetChanged();
        lineData.notifyDataChanged();
        emgLineChart.notifyDataSetChanged();

        if (emgEntries.size() > 100) {
            emgLineChart.moveViewToX(lineData.getEntryCount() - 100);
        }

        emgLineChart.invalidate();
        Log.d(TAG, "Updating chart with EMG Value: " + emgValue);
    }

    private void requestBluetoothEnable() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, 1);
    }

    private boolean hasBluetoothPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestBluetoothPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, 1);
    }

    private void startScan() {
        executorService.execute(() -> {
            if (hasBluetoothPermissions()) {
                try {
                    bluetoothLeScanner.startScan(scanCallback);
                    runOnUiThread(() -> bluetoothStatusText.setText("Scanning for devices..."));
                } catch (SecurityException e) {
                    Log.e(TAG, "Bluetooth scan permission not granted", e);
                }
            } else {
                requestBluetoothPermissions();
            }
        });
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            Log.d(TAG, "Found device: " + device.getName() + " [" + device.getAddress() + "]");

            List<ParcelUuid> uuids = result.getScanRecord().getServiceUuids();
            if (uuids != null && uuids.contains(ParcelUuid.fromString(EMG_SERVICE_UUID.toString()))) {
                bluetoothLeScanner.stopScan(this);
                connectToDevice(device);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "Bluetooth scan failed with error: " + errorCode);
        }
    };

    private void connectToDevice(BluetoothDevice device) {
        executorService.execute(() -> {
            if (hasBluetoothPermissions()) {
                try {
                    bluetoothGatt = device.connectGatt(emg_bluetooth.this, false, gattCallback);
                    runOnUiThread(() -> bluetoothStatusText.setText("Connecting to " + device.getName() + "..."));
                } catch (SecurityException e) {
                    Log.e(TAG, "Bluetooth connect permission not granted", e);
                }
            } else {
                requestBluetoothPermissions();
            }
        });
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread(() -> bluetoothStatusText.setText("Connected to device"));
                bluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                runOnUiThread(() -> bluetoothStatusText.setText("Disconnected"));
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService emgService = gatt.getService(EMG_SERVICE_UUID);
                if (emgService != null) {
                    emgValueCharacteristic = emgService.getCharacteristic(EMG_VALUE_CHARACTERISTIC_UUID);
                    gatt.setCharacteristicNotification(emgValueCharacteristic, true);
                    BluetoothGattDescriptor descriptor = emgValueCharacteristic.getDescriptor(CCCD_UUID);
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(descriptor);
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (EMG_VALUE_CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
                String emgData = characteristic.getStringValue(0);  // Data format: "EMGVal,EMGLevel"
                String[] parts = emgData.split(",");

                if (parts.length == 2) {
                    try {
                        int emgVal = Integer.parseInt(parts[0]);  // Raw EMG value
                        int emgLevel = Integer.parseInt(parts[1]);  // EMG level (0-3)

                        runOnUiThread(() -> updateUI(emgVal, emgLevel));
                    } catch (NumberFormatException e) {
                        Log.e("EMGDataError", "Error parsing EMG data: " + emgData, e);
                    }
                }
            }
        }
    };

    private void updateUI(int emgVal, int emgLevel) {
        runOnUiThread(() -> {
            String emgLevelText;
            int progress;
            long currentTime = System.currentTimeMillis();

            if (emgLevel != currentEmgLevel) {
                // EMG level has changed
                if (currentEmgLevel != -1 && lastEmgChangeTime != 0) {
                    long elapsedTime = (currentTime - lastEmgChangeTime) / 1000; // Convert to seconds
                    if (elapsedTime > 0) {
                        switch (currentEmgLevel) {
                            case 0:
                                belowEasySeconds += elapsedTime;
                                break;
                            case 1:
                                easySeconds += elapsedTime;
                                break;
                            case 2:
                                mediumSeconds += elapsedTime;
                                break;
                            case 3:
                                hardSeconds += elapsedTime;
                                break;
                            default:
                                break;
                        }
                    }
                }
                // Update the current level and timestamp
                currentEmgLevel = emgLevel;
                lastEmgChangeTime = currentTime;
            }

            // Set the EMG level gauge and chart updates
            switch (emgLevel) {
                case 0:
                    emgLevelText = "Minimal Effort";
                    progress = 0;
                    break;
                case 1:
                    emgLevelText = "Easy";
                    progress = 33;
                    break;
                case 2:
                    emgLevelText = "Medium";
                    progress = 66;
                    break;
                case 3:
                    emgLevelText = "Hard";
                    progress = 100;
                    break;
                default:
                    emgLevelText = "Unknown";
                    progress = 0;
            }

            emgValueText.setText("Stress Level: " + emgLevelText);
            emgLevelGauge.setProgress(progress);
            updateChart(emgVal);

            // Log for debugging
            Log.d(TAG, "Time - Below Easy: " + belowEasySeconds + "s, Easy: " + easySeconds + "s, Medium: " + mediumSeconds + "s, Hard: " + hardSeconds + "s");

            stressScore += emgLevel;

            // Check if the stress score exceeds the threshold
            if (stressScore >= stressThreshold) {
                if (!isFinishing() && !isDestroyed()) {
                    showWarningDialog();
                }
                stressScore = 0;  // Reset the stress score after showing the dialog
            }

        });
    }


    private void initializeTimeTracker() {
        timeTrackerRunnable = new Runnable() {
            private long lastUploadTime = 0;
            private final long uploadInterval = 10000; // Reduce to 10 seconds for more frequent uploads

            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();

                // Check if EMG level has changed and update elapsed time
                if (currentEmgLevel != -1 && lastEmgChangeTime != 0) {
                    long elapsedTime = (currentTime - lastEmgChangeTime) / 1000; // Convert to seconds
                    if (elapsedTime > 0) {
                        switch (currentEmgLevel) {
                            case 0:
                                belowEasySeconds += elapsedTime;
                                break;
                            case 1:
                                easySeconds += elapsedTime;
                                break;
                            case 2:
                                mediumSeconds += elapsedTime;
                                break;
                            case 3:
                                hardSeconds += elapsedTime;
                                break;
                            default:
                                break;
                        }
                        lastEmgChangeTime = currentTime;
                    }
                }

                // Insert the data more frequently, here every 10 seconds
                if (currentTime - lastUploadTime >= uploadInterval) {
                    insertTimeTrackerData();
                    lastUploadTime = currentTime;
                }

                handler.postDelayed(this, 1000); // Continue running every second
            }
        };
        handler.post(timeTrackerRunnable); // Start the time tracker
    }

    private void insertTimeTrackerData() {
        String username = MainActivity.GlobalsLogin.username;
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Prepare the data
        HashMap<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("date", date);
        data.put("below_easy_seconds", String.valueOf(belowEasySeconds));
        data.put("easy_seconds", String.valueOf(easySeconds));
        data.put("medium_seconds", String.valueOf(mediumSeconds));
        data.put("hard_seconds", String.valueOf(hardSeconds));

        // Log the data
        Log.d(TAG, "Inserting data: " + data.toString());

        // Convert HashMap to arrays
        String[] keys = data.keySet().toArray(new String[0]);
        String[] values = data.values().toArray(new String[0]);

        PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/insertDurationEMG.php", "POST", keys, values);

        // Start the PutData task
        if (putData.startPut()) {
            if (putData.onComplete()) {
                String result = putData.getResult();
                Log.d(TAG, "Insert result: " + result);
                if (result.equals("Success")) {
                    Toast.makeText(this, "Time Tracker data inserted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error inserting time tracker data: " + result, Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "PutData onComplete failed");
            }
        } else {
            Log.e(TAG, "PutData startPut failed");
        }
    }

    private void showWarningDialog() {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(emg_bluetooth.this);
        LayoutInflater inflater = getLayoutInflater();

        // Inflate the custom layout for the dialog
        View dialogLayout = inflater.inflate(R.layout.dialog_warning, null);
        builder.setView(dialogLayout);

        // Find the ImageView and set a warning image
        ImageView warningImage = dialogLayout.findViewById(R.id.warningImage);
        warningImage.setImageResource(R.drawable.warning_icon);  // Replace with your warning image resource

        // Set dialog title and message
        builder.setTitle("Warning: High Stress");
        builder.setMessage("Muscle stress is too high. Please take a rest for at least 5 minutes.");

        // Add an "OK" button to dismiss the dialog
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
