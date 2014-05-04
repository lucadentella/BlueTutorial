package it.lucadentella.bluetutorial_5;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SelectDeviceDialogListener {

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket btSocket;
	private BtAsyncTask btAsyncTask;
	
	private boolean connected;
	
	private Button bt1;
	private Button bt2;
	private Menu actionsMenu;
	
	private final UUID SPP_UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private final int REQUEST_ENABLE_BT = 1;

	
	/**
	 * APP INITIALIZATION
	 */
	
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

			Toast.makeText(this, "This app requires a bluetooth capable phone",
					Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {

		// check if bluetooth is enabled
		// if not, ask the user to enable it using an Intent
		if (!mBluetoothAdapter.isEnabled()) {

			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

		// init variables and GUI controls
		connected = false;
		bt1 = (Button)findViewById(R.id.bt1);
		bt2 = (Button)findViewById(R.id.bt2);		
		
		bt1.setEnabled(false);
		bt2.setEnabled(false);

		super.onPostCreate(savedInstanceState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Save the menu variable
		actionsMenu = menu;
		
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
				
		return super.onCreateOptionsMenu(menu);
	}

	
	/**
	 * INTENTS
	 */
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// check if the result comes from the request to enable bluetooth
		if (requestCode == REQUEST_ENABLE_BT)

			// the request was not successful? display a toast message and close
			// the app
			if (resultCode != RESULT_OK) {
				Toast.makeText(this, "This app requires bluetooth",
						Toast.LENGTH_SHORT).show();
				finish();
			}

		super.onActivityResult(requestCode, resultCode, data);
	}

	
	/**
	 * GUI EVENTS
	 */
	
	// Respond to click on BtOnOff button
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		switch (item.getItemId()) {
			
		case R.id.action_btonoff:
			
			// If we're not connected, create and show the dialog with the paired devices
			if(!connected) {
			
				Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

				String[] pairedDeviceNames = new String[pairedDevices.size()];
				int i = 0;
				for(BluetoothDevice pairedDevice : pairedDevices) {
					pairedDeviceNames[i] = pairedDevice.getName();
					i++;
				}

				SelectDeviceDialog selectDeviceDialog = SelectDeviceDialog.newInstance(pairedDeviceNames);
				selectDeviceDialog.show(getFragmentManager(), "selectDeviceDialog");
			}
			
			// if we're connected, disconnect
			else {
				disconnectFromDevice();
			}
            return true;
            
		default:
            return super.onOptionsItemSelected(item);
		}
	}
	
	// Click on BT1 or BT2
	public void btClick(View view) {
		
		switch (view.getId()) {
			
		case R.id.bt1:
			btAsyncTask.sendCommand("BUTTON1");
			break;

		case R.id.bt2:
			btAsyncTask.sendCommand("BUTTON2");
			break;
		}
	}
	
	@Override
	public void onChoosingPairedDevice(String deviceName) {
		
		connectToDevice(deviceName);
	}
	
	
	/**
	 * LOGIC
	 */
	
	// Bluetooth connection
	private void connectToDevice(String deviceName) {
		
		Log.d("MainActivity", "Enter connectToDevice()");
		
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		BluetoothDevice targetDevice = null;
		for(BluetoothDevice pairedDevice : pairedDevices) 
			if(pairedDevice.getName().equals(deviceName)) {
				targetDevice = pairedDevice;
				break;
			}

		// If the device was not found, toast an error and return
		if(targetDevice == null) {
			Log.d("MainActivity", "No device found with name " + deviceName);
			Toast.makeText(this, "Device not found", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Create a connection to the device with the SPP UUID
		try {
			btSocket = targetDevice.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
			Log.d("MainActivity", "InsecureRfCommSocket created");
		} catch (IOException e) {
			Log.d("MainActivity", "Unable to create InsecureRfCommSocket: " + e.getMessage());
			Toast.makeText(this, "Unable to open a serial socket with the device", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Connect to the device
		try {
			btSocket.connect();
			Log.d("MainActivity", "Socket connected");
		} catch (IOException e) {
			Log.d("MainActivity", "Unable to connect the socket: " + e.getMessage());
			Toast.makeText(this, "Unable to connect to the device", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Connection successful, start the async task
		btAsyncTask = new BtAsyncTask(this, btSocket);
		btAsyncTask.execute();
		Log.d("MainActivity", "AsyncTask executed");
		
		// update GUI
		connected = true;
		bt1.setEnabled(true);
		bt2.setEnabled(true);		
		actionsMenu.findItem(R.id.action_btonoff).setIcon(R.drawable.button_off);
		Log.d("MainActivity", "GUI updated");
		
		Log.d("MainActivity", "Exit connectToDevice()");
	}
	
	private void disconnectFromDevice() {
	
		Log.d("MainActivity", "Enter disconnectFromDevice()");
		
		// stop the async task
		btAsyncTask.cancel(true);
		Log.d("MainActivity", "AsyncTask stopped");
		
		// close the socket
		try {
			btSocket.close();
			Log.d("MainActivity", "Socket closed");
		} catch (IOException e) {
			Log.d("MainActivity", "Unable to close socket: " + e.getMessage());
			Toast.makeText(this, "Unable to disconnect from the device", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Disconnection successful, update GUI
		connected = false;
		bt1.setEnabled(false);
		bt2.setEnabled(false);
		actionsMenu.findItem(R.id.action_btonoff).setIcon(R.drawable.button_on);
		((TextView)findViewById(R.id.tvResponse)).setText("");
		Log.d("MainActivity", "GUI updated");
		
		Log.d("MainActivity", "Exit disconnectFromDevice()");
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
