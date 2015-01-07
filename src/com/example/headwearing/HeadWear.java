package com.example.headwearing;


import java.util.ArrayList;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class HeadWear extends Activity {
	public static boolean DEBUG = true;
	public static String TAG = "testHeadWear";
	public static boolean mBLEDeviceConnected = false;
	public static boolean mBLEDeviceConnecting = true;
	
	private String mDeviceName = "";
	private String mDeviceAddress = "";
	
	private BluetoothLeService mBluetoothLeService = null;
	private DataHandlerService mDataHandlerService = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_head_wear);
		registerReceiver(mBLEDateUpdateReciver,makeBLEIntentFilter());
		LineChart chart = (LineChart) findViewById(R.id.chart);
		chart.setDescription("hello!");
		//chart.setDrawYValues(true);
		
		ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
	    ArrayList<Entry> valsComp2 = new ArrayList<Entry>();
	    Entry c1e1 = new Entry(100.000f, 0); // 0 == quarter 1
	    valsComp1.add(c1e1);
	    Entry c1e2 = new Entry(50.000f, 1); // 1 == quarter 2 ...
	    valsComp1.add(c1e2);
	    // and so on ...

	    Entry c2e1 = new Entry(120.000f, 0); // 0 == quarter 1
	    valsComp2.add(c2e1);
	    Entry c2e2 = new Entry(110.000f, 1); // 1 == quarter 2 ...
	    valsComp2.add(c2e2);
	    LineDataSet setComp1 = new LineDataSet(valsComp1, "Company 1");
	    LineDataSet setComp2 = new LineDataSet(valsComp2, "Company 2");
	    ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
	    dataSets.add(setComp1);
	    dataSets.add(setComp2);

	    ArrayList<String> xVals = new ArrayList<String>();
	    xVals.add("1.Q"); xVals.add("2.Q"); xVals.add("3.Q"); xVals.add("4.Q"); 

	    LineData data = new LineData(xVals, dataSets);
	    chart.setData(data);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(mBLEDeviceConnected){
			menu.findItem(R.id.menu_scan).setVisible(false);
			menu.findItem(R.id.menu_stop).setVisible(true);
		}
		else{
			menu.findItem(R.id.menu_scan).setVisible(true);
			menu.findItem(R.id.menu_stop).setVisible(false);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
			case R.id.menu_scan:{
				if(!mBLEDeviceConnected){
					Intent intent = new Intent(this,BLEDevice.class);
					startActivity(intent);
				}
				break;
			}
			case R.id.menu_stop:{
				if(mBLEDeviceConnected){
					
				}
				break;
			}
		}
		invalidateOptionsMenu();
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this)
                    // .setIcon(R.drawable.services)
                    .setTitle("退出程序？")
                    .setMessage("真的要退出吗？")
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int which) {
                                	
                                }
                            })
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    finish();
                                }
                            }).show();
            return true;
        }else{

        	return super.onKeyDown(keyCode, event);
        }
    }
	
	private final ServiceConnection mBLEServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
        	mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            Log.i("test","HeadWear onServiceConnected");
            if(!mBluetoothLeService.connect(mDeviceAddress)){
            	if(DEBUG){
            		Log.e(TAG,"Can not connect to the device: " + mDeviceAddress );
            	}
            	unbindService(mBLEServiceConnection);
            	Toast.makeText(HeadWear.this, "Can not connect to the device: " + mDeviceAddress ,Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        	mBluetoothLeService = null;
        }
    };
    
    private final ServiceConnection mDataHandlerServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
        	mDataHandlerService = ((DataHandlerService.LocalBinder) service).getService();
            Log.i("test","HeadWear onServiceConnected mDataHandlerServiceConnection");
            if(!mDataHandlerService.init()){
            	unbindService(mBLEServiceConnection);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        	mDataHandlerService = null;
        }
    };
	
	private final BroadcastReceiver mBLEDateUpdateReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(DEBUG){
            	Log.i(TAG,"onReceive : " + action);
            }
            if(BLEDevice.BLE_CONNECT_DEVICE.equals(action)){
            	mDeviceName = intent.getStringExtra(BLEDevice.BLE_DEVICE_NAME);
            	mDeviceAddress = intent.getStringExtra(BLEDevice.BLE_DEVICE_ADDRESS);
            	Intent gattServiceIntent = new Intent(HeadWear.this, BluetoothLeService.class);
                if(bindService(gattServiceIntent, mBLEServiceConnection, BIND_AUTO_CREATE)){
                	Log.i(TAG,"bindsuccessfully");
                	
                }
                else{
                	Log.i(TAG,"bind un successfully");
                }
                if(DEBUG){
                	Log.i(TAG,"bindService");
                }
                Intent dataHandlerServiceIntent = new Intent(HeadWear.this, DataHandlerService.class);
                bindService(dataHandlerServiceIntent, mDataHandlerServiceConnection, BIND_AUTO_CREATE);
            }
        }
	};
	
	private static IntentFilter makeBLEIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLEDevice.BLE_CONNECT_DEVICE);
        return intentFilter;
    }
}
