package it.lucadentella.bluetutorial_4;

import it.lucadentella.bluetutorial_1.R;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private BluetoothAdapter mBluetoothAdapter;
	private final int REQUEST_ENABLE_BT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		// get the bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		// check if the device has bluetooth capabilities
		// if not, display a toast message and close the app
		if (mBluetoothAdapter == null) {
			
			Toast.makeText(this, "This app requires a bluetooth capable phone", Toast.LENGTH_SHORT).show();
			finish();
		}		
	}	
		
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		
		// check if bluetooth is enabled
		// if not, ask the user to enable it using an Intent		
		if (!mBluetoothAdapter.isEnabled()) {
			
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		
		// if the bluetooth is enabled, display paired devices
		else listPairedDevices();
		
		super.onPostCreate(savedInstanceState);
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// check if the result comes from the request to enable bluetooth
		if(requestCode == REQUEST_ENABLE_BT)
			
			// the request was successful? if so, display paired devices
			if(resultCode == RESULT_OK) listPairedDevices();
		
			// if not, display a toast message and close the app
			else {
				Toast.makeText(this, "This app requires bluetooth", Toast.LENGTH_SHORT).show();
				finish();		
			}
		
		super.onActivityResult(requestCode, resultCode, data);
	}


	private void listPairedDevices() {
	
		// get the paired devices
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		
		// Get the ListView element
		ListView listView1 = (ListView)findViewById(R.id.listView1);
		
		// Add the paired devices to an ArrayList
		ArrayList<String> arrayList1 = new ArrayList<>();		
		for(BluetoothDevice pairedDevice : pairedDevices) 
			arrayList1.add(pairedDevice.getName());
		
		// Create an array adapter for the ListView
		ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(this, R.layout.paired_device_row, arrayList1);
		listView1.setAdapter(arrayAdapter1);
		
		// Add a click listener 
		listView1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// Get the element clicked and send Hello world to it
				String listElement = (String)parent.getItemAtPosition(position);
				sayHelloToDevice(listElement);
				
			}
		});
	}
	
	// Method that establish a serial connection to the device and send 
	// "Hello World!" string
	private void sayHelloToDevice(String deviceName) {
		
		UUID SPP_UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
		
		// Get the Bluetooth device with the given name
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		BluetoothDevice targetDevice = null;
		for(BluetoothDevice pairedDevice : pairedDevices) 
			if(pairedDevice.getName().equals(deviceName)) {
				targetDevice = pairedDevice;
				break;
			}
		
		// If the device was not found, toast an error and return
		if(targetDevice == null) {
			Toast.makeText(this, "Device not found", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Create a connection to the device with the SPP UUID
		BluetoothSocket btSocket = null;	
		try {
			btSocket = targetDevice.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
		} catch (IOException e) {
			Toast.makeText(this, "Unable to open a serial socket with the device", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Connect to the device
		try {
			btSocket.connect();
		} catch (IOException e) {
			Toast.makeText(this, "Unable to connect to the device", Toast.LENGTH_SHORT).show();
			return;
		}
		
		try {
			OutputStreamWriter writer = new OutputStreamWriter(btSocket.getOutputStream());		
			writer.write("Hello World!\r\n");
			writer.flush();
		} catch (IOException e) {
			Toast.makeText(this, "Unable to send message to the device", Toast.LENGTH_SHORT).show();
		}		
		
		try {
			btSocket.close();
			Toast.makeText(this, "Message successfully sent to the device", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(this, "Unable to close the connection to the device", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
