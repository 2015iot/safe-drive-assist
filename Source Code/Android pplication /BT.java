package com.example.accident;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.EditText;

public class BT {
	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    boolean stopWorker;
    Location l;
    File f;
    FileOutputStream fo;
    SmsManager sms=SmsManager.getDefault();
    
    void findBT()
	{
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("HC-05")) 
                {
                    mmDevice = device;
                    break;
                }
             }
        }
    }
    
    void openBT(EditText e, LocationManager l) throws Exception
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);        
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        
        beginListenForData(e,l);
    }
    
    void beginListenForData(final EditText e,final LocationManager lm)
    {
        final Handler handler = new Handler(); 
        final byte delimiter = 13; //This is the ASCII code for a newline character
        
        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {     
            	//myLabel.setText("Reading");
               while(!Thread.currentThread().isInterrupted() && !stopWorker)
               {
                    try 
                    {
                        int bytesAvailable = mmInputStream.available();                        
                        if(bytesAvailable > 0)
                        {
                        	//myLabel.setText("Got Byte");
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    //mmOutputStream.write(encodedBytes);
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    //myLabel.setText(data);
                                    readBufferPosition = 0;
                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            try{
                                            	mmOutputStream.write(data.getBytes());
                                            	if(data.contains("accident")){
                                            		l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                            		sms.sendTextMessage("+91"+e.getText().toString(), null, "Driver : "+data.charAt(0)+" Accident Detected at - http://www.google.com/maps/place/"+l.getLatitude()+","+l.getLongitude(), null, null);
                                            	}
                                            	else if(data.contains("drowsy")){
                                            		l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                            		f = new File(Environment.getExternalStorageDirectory().getPath()+"/Accident");
                                            		if(!f.exists())
                                            			f.mkdir();
                                            		f = new File(f,"Accident.txt");
                                            		fo = new FileOutputStream(f, true);
                                            		fo.write(("Driver : "+data.charAt(0)+" Drowsy Detected Latitude="+l.getLatitude()+" Longitude="+l.getLongitude()+" "+new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss\n").format(Calendar.getInstance().getTime())).getBytes());
                                            		fo.close();
                                            		new Thread(new Ftp(f)).start();
                                            	}
                                            }
                                            catch(Exception x){}
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } 
                    catch (Exception ex) 
                    {
                    	stopWorker = true;
                    }
               }
            }
        });

        workerThread.start();
    }
    
    void closeBT() throws Exception
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
    }

}