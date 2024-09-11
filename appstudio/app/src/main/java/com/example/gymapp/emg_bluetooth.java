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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.UUID;

public class emg_bluetooth extends AppCompatActivity {

    private static final String TAG = "EMGBluetoothActivity";
    private static final String DEVICE_NAME = "EMG_Sensor";

    private static final UUID EMG_SERVICE_UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb"); // EMG Service UUID
    private static final UUID EMG_VALUE_CHARACTERISTIC_UUID = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb"); // EMG Value Characteristic UUID
    private static final UUID CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"); // Client Characteristic Configuration Descriptor UUID

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

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

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

    private boolean hasBluetoothPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestBluetoothPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
        }, 1);
    }

    private void startScan() {
        if (hasBluetoothPermissions()) {
            try {
                bluetoothLeScanner.startScan(scanCallback);
            } catch (SecurityException e) {
                Log.e(TAG, "Bluetooth scan permission not granted");
            }
        } else {
            requestBluetoothPermissions();
        }
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (hasBluetoothPermissions()) {
                if (DEVICE_NAME.equals(device.getName())) {
                    try {
                        bluetoothLeScanner.stopScan(this);
                        connectToDevice(device);
                    } catch (SecurityException e) {
                        Log.e(TAG, "Bluetooth connect permission not granted");
                    }
                }
            }
        }
    };

    private void connectToDevice(BluetoothDevice device) {
        if (hasBluetoothPermissions()) {
            try {
                bluetoothGatt = device.connectGatt(this, false, gattCallback);
            } catch (SecurityException e) {
                Log.e(TAG, "Bluetooth connect permission not granted");
            }
        } else {
            Log.e(TAG, "Bluetooth connect permission not granted");
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread(() -> bluetoothStatusText.setText("Bluetooth Status: Connected"));
                if (hasBluetoothPermissions()) {
                    try {
                        bluetoothGatt.discoverServices();
                    } catch (SecurityException e) {
                        Log.e(TAG, "Bluetooth service discovery permission not granted");
                    }
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                runOnUiThread(() -> {
                    bluetoothStatusText.setText("Bluetooth Status: Disconnected");
                    emgValueText.setText("EMG Level: Disconnected");
                    emgLevelGauge.setProgress(0);
                });
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
                        if (hasBluetoothPermissions()) {
                            bluetoothGatt.setCharacteristicNotification(characteristic, true);
                        }
                    } catch (SecurityException e) {
                        Log.e(TAG, "Bluetooth notification permission not granted");
                    }
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (EMG_VALUE_CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
                final int emgLevel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0); // Assuming 8-bit value
                runOnUiThread(() -> {
                    String emgLevelText;
                    int progress;

                    switch (emgLevel) {
                        case 0: // Below Easy
                            emgLevelText = "Below Easy";
                            progress = 0;
                            break;
                        case 1: // Easy
                            emgLevelText = "Easy";
                            progress = 33;
                            break;
                        case 2: // Medium
                            emgLevelText = "Medium";
                            progress = 66;
                            break;
                        case 3: // Hard
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothGatt != null) {
            try {
                bluetoothGatt.close();
                bluetoothGatt = null;
            } catch (SecurityException e) {
                Log.e(TAG, "Bluetooth disconnect permission not granted");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();
            } else {
                bluetoothStatusText.setText("Bluetooth permissions required.");
            }
        }
    }
}