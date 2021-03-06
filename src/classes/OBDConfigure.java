package com.example.zanderieux.obd4me;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by zanderieux on 2/5/16.
 */
public class OBDConfigure {

    //  -----------
    // | Constants |
    //  -----------
    private static final int REQUEST_ENABLE_BT = 1;

    //  -----------------
    // | Private Members |
    //  -----------------
    private BroadcastReceiver receiver;
    private BluetoothAdapter adapter;

    public OBDConfigure() {
        super();
    }

    // Initialize all Bluetooth things.
    public void init(Context context, final BluetoothCallbackInterface bluetoothCallbackInterface) {

        // Obtain access to the BT adapter.
        this.adapter = BluetoothAdapter.getDefaultAdapter();

        // Ensure device supports bluetooth.
        if(adapter == null) {
            bluetoothCallbackInterface.bluetoothError("BT not supported on this device");
        }

        else {
            // Turn on the BT adapter if it is NOT already enabled.
            if ( !this.adapter.isEnabled() ) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity) context).startActivityForResult(enableBtIntent, this.REQUEST_ENABLE_BT);
            }

            // Register the broadcast receivers.
            this.createBroadcastReceiver(context, bluetoothCallbackInterface);

            // Create intent filter and add BT actions.
            IntentFilter btFilter = new IntentFilter();
            btFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            btFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            btFilter.addAction(BluetoothDevice.ACTION_FOUND);

            // Register the intent filter to the above broadcast receiver.
            context.registerReceiver(this.receiver, btFilter);
        }
    }


    // Setup Bluetooth BroadcastReceivers.
    private boolean createBroadcastReceiver(Context context, final BluetoothCallbackInterface bluetoothCallbackInterface) {

        try {

            //  ---------------------------------------
            // | Broadcast Receiver: Bluetooth Actions |
            //  ---------------------------------------
            this.receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    // Check for different actions.
                    //
                    // Get current action.
                    String action = intent.getAction();

                    if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                        // Call specific callback method.
                        bluetoothCallbackInterface.discoveryStarted();
                    }
                    else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        // Call specific callback method.
                        bluetoothCallbackInterface.discoveryFinished();
                    }

                    else if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                        // Cancel the discovery process.
                        adapter.cancelDiscovery();

                        // Call specific callback method.
                        bluetoothCallbackInterface.discoveryFound((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                    }
                }
            };

            return (true);

        } catch (Exception e) {
            return (false);
        }
    }

    // Unregisters all broadcast receivers.
    public boolean destroyBroadcastReceiver(Context context) {
        try {
            context.unregisterReceiver(this.receiver);
            return (true);
        } catch (Exception e) {
            return (false);
        }
    }

    // Scan for a bluetooth OBD device.
    public void startScan() {

        // Start the BT discovery process.
        this.adapter.startDiscovery();
    }

    // Scan for a bluetooth OBD device.
    public void stopScan() {

        // Start the BT discovery process.
        this.adapter.cancelDiscovery();
    }
}
