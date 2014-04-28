package it.lucadentella.bluetutorial_3;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private BluetoothAdapter mBluetoothAdapter;
	private BroadcastReceiver mReceiver;

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
		
		// set the GUI on the actual state
		updateGUI(mBluetoothAdapter.getState());
		
		// Create a broadcast receiver for receiving notifications
		// when bluetooth changes status
		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				String action = intent.getAction();
				if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
					
					int actualState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
					updateGUI(actualState);
				}
			}			
		};	
		
		// Register the broadcast receiver for the ACTION_STATE_CHANGE event
		IntentFilter stateChangedfilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(mReceiver, stateChangedfilter);
		
		super.onPostCreate(savedInstanceState);
	}		
	
	@Override
	protected void onDestroy() {

		// Unregister the broadcast receiver
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	private void updateGUI(int actualState) {
	
		// get the GUI objects
		ImageView imageView1 = (ImageView)findViewById(R.id.imageView1);
		TextView textView2 = (TextView)findViewById(R.id.textView2);
		
		switch(actualState) {
		
			case BluetoothAdapter.STATE_ON:
				imageView1.setImageResource(R.drawable.bt_on_icon);
				textView2.setText(R.string.bt_on);
				break;
	
			case BluetoothAdapter.STATE_OFF:
				imageView1.setImageResource(R.drawable.bt_off_icon);
				textView2.setText(R.string.bt_off);
				break;
	
			case BluetoothAdapter.STATE_TURNING_ON:
				imageView1.setImageResource(R.drawable.bt_working_icon);
				textView2.setText(R.string.bt_turning_on);
				break;
	
			case BluetoothAdapter.STATE_TURNING_OFF:
				imageView1.setImageResource(R.drawable.bt_working_icon);
				textView2.setText(R.string.bt_turning_off);
				break;							
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
