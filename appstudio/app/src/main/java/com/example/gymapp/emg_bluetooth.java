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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
    private int stressThreshold = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emg_bluetooth);

        bluetoothStatusText = findViewById(R.id.bluetoothStatusText);
        connectBluetoothButton = findViewById(R.id.connectBluetoothButton);
        emgLevelGauge = findViewById(R.id.emgLevelGauge);
        emgValueText = findViewById(R.id.emgValueText);
        ImageView bodyFocusImage = findViewById(R.id.imageView6);

        boolean hideButton = getIntent().getBooleanExtra("hide_button", false);
        if (hideButton) {
            connectBluetoothButton.setVisibility(View.GONE); // Hide the button
        }

        boolean hideimage = getIntent().getBooleanExtra("hide_image", false);
        if (hideimage) {
            bodyFocusImage.setVisibility(View.GONE); // Hide the image
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

        connectBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasBluetoothPermissions()) {
                    startScan();
                } else {
                    requestBluetoothPermissions();
                }
            }
        });

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

        emgLineChart = findViewById(R.id.emgLineChart);
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
        // Ensure the value is valid (between 0 and 4095, the 12-bit range from Arduino)
        if (emgValue < 0 || emgValue > 4095) {
            Log.e("ChartError", "Invalid EMG value: " + emgValue);
            return;  // Skip invalid values
        }

        // Add the new value to the chart
        emgEntries.add(new Entry(timeIndex++, emgValue));

        // Limit the number of entries to avoid overwhelming the chart
        if (emgEntries.size() > 100) {
            emgEntries.remove(0);  // Remove the oldest entry
        }

        // Notify the chart about the updated data
        lineDataSet.notifyDataSetChanged();
        lineData.notifyDataChanged();
        emgLineChart.notifyDataSetChanged();

        // Ensure that we only scroll if the number of entries is greater than a threshold
        if (emgEntries.size() > 100) {
            emgLineChart.moveViewToX(lineData.getEntryCount() - 100);  // Keep the last 100 entries visible
        }

        // Refresh the chart to show updated data
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
        if (hasBluetoothPermissions()) {
            try {
                 bluetoothLeScanner.startScan(scanCallback);
                bluetoothStatusText.setText("Scanning for devices...");
            } catch (SecurityException e) {
                Log.e(TAG, "Bluetooth scan permission not granted", e);
            }
        } else {
            requestBluetoothPermissions();
        }
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
            bluetoothStatusText.setText("Scan failed. Try again.");
        }
    };

    private void connectToDevice(BluetoothDevice device) {
        if (hasBluetoothPermissions()) {
            try {
                bluetoothGatt = device.connectGatt(this, false, gattCallback);
                if (bluetoothGatt == null) {
                    Log.e(TAG, "Failed to connect to GATT server");
                }
            } catch (SecurityException e) {
                Log.e(TAG, "Bluetooth connect failed", e);
            }
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (status == BluetoothGatt.GATT_FAILURE) {
                Log.e(TAG, "GATT connection failed");
                return;
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread(() -> bluetoothStatusText.setText("Bluetooth Status: Connected"));
                bluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                runOnUiThread(() -> bluetoothStatusText.setText("Bluetooth Status: Disconnected"));
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService emgService = gatt.getService(EMG_SERVICE_UUID);
                if (emgService != null) {
                    emgValueCharacteristic = emgService.getCharacteristic(EMG_VALUE_CHARACTERISTIC_UUID);
                    if (emgValueCharacteristic != null) {
                        enableNotifications(emgValueCharacteristic);
                    } else {
                        Log.e(TAG, "EMG Value Characteristic not found");
                    }
                } else {
                    Log.e(TAG, "EMG Service not found");
                }
            } else {
                Log.e(TAG, "Service discovery failed: " + status);
            }
        }

        private void enableNotifications(BluetoothGattCharacteristic characteristic) {
            if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CCCD_UUID);
                if (descriptor != null) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    try {
                        bluetoothGatt.writeDescriptor(descriptor);
                        bluetoothGatt.setCharacteristicNotification(characteristic, true);
                    } catch (SecurityException e) {
                        Log.e(TAG, "Failed to enable notifications", e);
                    }
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

                        // Check if values are in expected range
                        if (emgVal >= 0 && emgVal <= 4095 && emgLevel >= 0 && emgLevel <= 3) {
                            runOnUiThread(() -> {
                                // Update the UI based on the parsed EMG value and level
                                updateUI(emgVal, emgLevel);
                            });
                        } else {
                            Log.e("EMGDataError", "Invalid EMG values: EMGVal = " + emgVal + ", EMGLevel = " + emgLevel);
                        }
                    } catch (NumberFormatException e) {
                        Log.e("EMGDataError", "Error parsing EMG data: " + emgData, e);
                    }
                } else {
                    Log.e("EMGDataError", "Invalid EMG data format: " + emgData);
                }
            }
        }

        private void updateUI(int emgVal, int emgLevel) {
            String emgLevelText;
            int progress;

            // Determine text and progress for the gauge based on EMG level
            switch (emgLevel) {
                case 0:
                    emgLevelText = "Below Easy";
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

            // Update the text and gauge UI components
            emgValueText.setText("Stress Level: " + emgLevelText);
            emgLevelGauge.setProgress(progress);

            // Update the chart with the new EMG value
            updateChart(emgVal);

            stressScore += emgLevel;

            // Check if the stress score exceeds the threshold
            if (stressScore >= stressThreshold) {
                showWarningDialog();
                stressScore = 0;  // Reset the stress score after showing the dialog
            }
        }

        private void showWarningDialog() {
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


    };
}
