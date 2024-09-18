package com.example.gymapp;

import android.Manifest;
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
import android.location.LocationManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emg_bluetooth);

        bluetoothStatusText = findViewById(R.id.bluetoothStatusText);
        connectBluetoothButton = findViewById(R.id.connectBluetoothButton);
        emgLevelGauge = findViewById(R.id.emgLevelGauge);
        emgValueText = findViewById(R.id.emgValueText);

        boolean hideButton = getIntent().getBooleanExtra("hide_button", false);
        if (hideButton) {
            connectBluetoothButton.setVisibility(View.GONE); // Hide the button
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
                final int emgLevel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                Log.d(TAG, "onCharacteristicChanged triggered with EMG level: " + emgLevel);

                runOnUiThread(() -> {
                    Log.d(TAG, "Updating UI with EMG level: " + emgLevel);
                    String emgLevelText;
                    int progress;

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

                    emgValueText.setText("EMG Level: " + emgLevelText);
                    emgLevelGauge.setProgress(progress);
                });
            }
        }
    };
}
