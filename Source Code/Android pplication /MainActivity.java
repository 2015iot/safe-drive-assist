package com.example.accident;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;

public class MainActivity extends Activity implements LocationListener {
	BT bt = new BT();
	Button b,b1;
	EditText e;
	LocationManager lm;
	Location l;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if(bt.mBluetoothAdapter == null){
			Toast.makeText(getBaseContext(), "Bluetooth is not supported", Toast.LENGTH_SHORT).show();
			finish();
		}
		if(!bt.mBluetoothAdapter.isEnabled())
        	startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),1);
		lm = (LocationManager)getSystemService(LOCATION_SERVICE);
		b = (Button)findViewById(R.id.button1);
		b1 = (Button)findViewById(R.id.button2);
		e = (EditText)findViewById(R.id.editText1);
		SharedPreferences sp = getSharedPreferences("UserInfo", 0);
		e.setText(sp.getString("phno", ""));
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, this);
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try{
					if(e.getText().toString().length()!=10){
						Toast.makeText(getBaseContext(), "Enter a phone number", Toast.LENGTH_SHORT).show();
						return;
					}
					bt.findBT();
					bt.openBT(e,lm);
				}
				catch(Exception e){}
			}
		});
		b1.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try{
					bt.closeBT();
				}
				catch(Exception e){}
			}
		});
		
	}
	
	@Override
	public void onLocationChanged(Location lo) {
		// TODO Auto-generated method stub
		l = lo;
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(getBaseContext(), "GPS turned off", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(getBaseContext(), "GPS turned on", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != RESULT_OK){
			Toast.makeText(getBaseContext(), "Bluetooth is needed", Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		SharedPreferences sp = getSharedPreferences("UserInfo", 0);
		SharedPreferences.Editor ed = sp.edit();
		ed.putString("phno", e.getText().toString());
		ed.commit();
		super.onPause();
	}
}
