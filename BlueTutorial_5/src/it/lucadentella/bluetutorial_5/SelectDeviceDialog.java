package it.lucadentella.bluetutorial_5;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class SelectDeviceDialog extends DialogFragment {
	
	SelectDeviceDialogListener selectDeviceDialogListener;

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
		
		// Save the activity to perform callbacks
		selectDeviceDialogListener = (SelectDeviceDialogListener)activity;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Retrieve the list of paired devices from the bundle
		final String[] pairedDevices = getArguments().getStringArray("pairedDevices");
		
		// Configure and create the dialog
		return new AlertDialog.Builder(getActivity())
		.setTitle("Choose device")
		.setItems(pairedDevices, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				selectDeviceDialogListener.onChoosingPairedDevice(pairedDevices[which]);		
			}
		})
				
		.create();
	}
	
	
	// Static method to create a dialog with arguments
	public static SelectDeviceDialog newInstance(String[] pairedDevices) {
		
		SelectDeviceDialog dialog = new SelectDeviceDialog();
        
		// Save the parameters in a bundle
		Bundle args = new Bundle();
		args.putStringArray("pairedDevices", pairedDevices);
        dialog.setArguments(args);
        return dialog;
    }

}
