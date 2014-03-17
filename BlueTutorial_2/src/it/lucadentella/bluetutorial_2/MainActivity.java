package it.lucadentella.bluetutorial_2;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private BluetoothAdapter mBluetoothAdapter;
	private BroadcastReceiver mReceiver;
	private final int REQUEST_ENABLE_BT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}		
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		
		// get the bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		// check if the device has bluetooth capabilities
		// if not, display a toast message and close the app
		if (mBluetoothAdapter == null) {
			
			Toast.makeText(this, "This app requires a bluetooth capable phone", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		// Create a broadcast receiver for receiving notifications when
		// - a new device is found
		// - the scanning process is finished
		mReceiver = new BroadcastReceiver() {
					
		    public void onReceive(Context context, Intent intent) {
		       
		    	String action = intent.getAction();
		    	
		    	// when a new device is discovered, add it to the TextArea
		        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		        			            
		            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		            TextView textView1 = (TextView)findViewById(R.id.textView1);
	    			textView1.append(Html.fromHtml("<b>" + device.getName() + "</b>"));
	    			textView1.append(" (" + device.getAddress() + ")\n");
		        }
		        
		        // if the scanning process has finished, enable the button
		        if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
		        
		        	final Button button1 = (Button)findViewById(R.id.button1);
		        	button1.setEnabled(true);
		        	button1.setText(R.string.scan);
		        }
		    }
		};
		
		// Register the BroadcastReceiver for two notifications
		IntentFilter deviceFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		IntentFilter discoveryFinishedfilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, deviceFoundFilter);
		registerReceiver(mReceiver, discoveryFinishedfilter);
		
		
		// check if bluetooth is enabled
		// if not, ask the user to enable it using an Intent		
		if (!mBluetoothAdapter.isEnabled()) {
			
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		
		// clickListener on the scan button
		final Button button1 = (Button)findViewById(R.id.button1);
		button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	// disable the button
            	button1.setEnabled(false);
            	
            	// change the text
            	button1.setText(R.string.scanning);
            	
            	// clear the textArea
            	((TextView)findViewById(R.id.textView1)).setText("");
            	
            	// start scanning
                mBluetoothAdapter.startDiscovery();
            }
        });
		
		super.onPostCreate(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// check if the result comes from the request to enable bluetooth
		if(requestCode == REQUEST_ENABLE_BT)
			
			// the request was successful? if so, display paired devices
			if(resultCode == RESULT_OK);
		
			// if not, display a toast message and close the app
			else {
				Toast.makeText(this, "This app requires bluetooth", Toast.LENGTH_SHORT).show();
				finish();		
			}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {

		// Unregister the broadcast receiver
		unregisterReceiver(mReceiver);
		super.onDestroy();
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
