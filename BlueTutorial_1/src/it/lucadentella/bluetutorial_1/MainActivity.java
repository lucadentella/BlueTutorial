package it.lucadentella.bluetutorial_1;

import java.util.Set;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
		
		// list the paired devices in the TextView2
		TextView textView2 = (TextView)findViewById(R.id.textView2);
		for(BluetoothDevice pairedDevice : pairedDevices) {
			
			textView2.append(Html.fromHtml("<b>" + pairedDevice.getName() + "</b>"));
			textView2.append(" (" + pairedDevice.getAddress() + ")\n");
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
