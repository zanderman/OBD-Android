package com.example.zanderieux.obd4me;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity implements BluetoothCallbackInterface {

    // Constants.
    private String keyName = "OBD";                     // Device name.
    private String keyAddress = "null";                 // MAC address.
    private static boolean isPaired = false;            // Pairing flag for BT devices.

    // Setup I/O objects.
    TextView textView;
    Button buttonScan;
    EditText editName;

    // Declare OBD configuration class.
    OBDConfigure obdConfigure;

    // Declare OBD adapter object.
    OBDAdapter obdAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // UI Elements.
        buttonScan = (Button) findViewById(R.id.buttonScan);
        textView = (TextView) findViewById(R.id.infoView);
        editName = (EditText) findViewById(R.id.editName);

        // Set button listeners.
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Change the defult key only if another name was specified.
                if ( !editName.getText().toString().equals(null) ) {
                    keyName = editName.getText().toString();
                }

                // Scan for the desired device name.
                obdConfigure.startScan();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Initialize all the things for BT and OBD.
        this.obdConfigure = new OBDConfigure();
        this.obdConfigure.init(this, this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unregister the broadcast receivers.
        obdConfigure.destroyBroadcastReceiver(this);
    }






    /*
        Bluetooth Callbacks
     */

    @Override
    public void discoveryStarted() {
        textView.setText("[!] Scanning for: " + keyName);

    }

    @Override
    public void discoveryFinished() {
        textView.setText("[!] Scanning complete!");
    }

    @Override
    public void discoveryFound(BluetoothDevice device) {
        if ( device.getName().contains(keyName) ) {
            // Cancel scanning.
            this.obdConfigure.stopScan();

            // Do something with the found device.
            textView.setText("[!] Found '" + device.getName() + "' @ " + device.getAddress() + "\n");

            // Create OBD object using device.
            this.obdAdapter = new OBDAdapter(device);
        }
        else {
            textView.setText("Invalid device found...");
        }
    }

    @Override
    public void bluetoothError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT);
    }
}
