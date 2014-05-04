package it.lucadentella.bluetutorial_5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.widget.ScrollView;
import android.widget.TextView;

public class BtAsyncTask extends AsyncTask<Void, String, Void> {

	private Activity mainActivity;
	
	private BufferedReader reader;
	private BufferedWriter writer;
	
	
	public BtAsyncTask(Activity activity, BluetoothSocket socket) {
		
		// save the calling activity
		mainActivity = activity;
		
		// Prepare stream reader and writer
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		
		// start the main loop
		receiveLoop();
		return null;
	}
	
	@Override
	protected void onProgressUpdate(String... values) {

		TextView tvResponse = (TextView)mainActivity.findViewById(R.id.tvResponse);
		ScrollView scrollView = (ScrollView)mainActivity.findViewById(R.id.scrollView1);
		tvResponse.append(values[0] + "\n");
		scrollView.fullScroll(ScrollView.FOCUS_DOWN);
	}
	
	private void receiveLoop() {
	
		// loop until an error occurs or the Task is stopped
		while(true) {
			
			try {
				String inputLine = reader.readLine();
				publishProgress(inputLine);
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}			
		}
	}
	
	public void sendCommand(String command) {
		
		try {
			writer.write(command + "\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
