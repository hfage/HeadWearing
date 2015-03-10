package com.example.headwearing;


import java.util.ArrayList;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.XLabels.XLabelPosition;
import com.github.mikephil.charting.utils.YLabels;
import com.github.mikephil.charting.utils.YLabels.YLabelPosition;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

public class HeadWear extends Activity {
	public static boolean DEBUG = true;
	public static String DRAW_BARCHART = "DRAW BAR CHART";
	public static boolean viewAcceleration = true;
	public static String TAG = "testHeadWear";
	public static boolean mBLEDeviceConnected = false;
	public static boolean mBLEDeviceConnecting = true;
	
	
	private String mDeviceName = "";
	private String mDeviceAddress = "";
	
	private BluetoothLeService mBluetoothLeService = null;
	private DataHandlerService mDataHandlerService = null;
	private BarChart mBarChart;
	private ArrayList<String> xVals = new ArrayList<String>();
	private LineChart mLineChart1;
	private LineChart mLineChart2;
	private LineChart mLineChart3;
	private ArrayList<String> xLineChartVals = new ArrayList<String>();
	private int xLineChartLen = 250;
	public static float YRANGE_MIN = 0.95f;
	public static float YRANGE_MAX = 1f;
	private ArrayList<Entry> yLineChartVals1 = new ArrayList<Entry>();
	private ArrayList<Entry> yLineChartVals2 = new ArrayList<Entry>();
	private ArrayList<Entry> yLineChartVals3 = new ArrayList<Entry>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_head_wear);
		registerReceiver(mBLEDateUpdateReciver,makeBLEIntentFilter());
		mBarChart = (BarChart) findViewById(R.id.barchart);
		if(viewAcceleration){
			mBarChart.setDrawYValues(false);
			mBarChart.setDescription("");
			mBarChart.setMaxVisibleValueCount(5);
			mBarChart.set3DEnabled(false);
			mBarChart.setPinchZoom(false);
			mBarChart.setUnit(" du");
			mBarChart.setDrawGridBackground(true);
			mBarChart.setDrawHorizontalGrid(true);
			mBarChart.setDrawVerticalGrid(false);
			mBarChart.setValueTextSize(10f);
			mBarChart.setDrawBorder(false);
			mBarChart.setYRange(YRANGE_MIN, YRANGE_MAX, false);
			xVals.add("X");
			xVals.add("Y");
			xVals.add("Z");
			
			mLineChart1 = (LineChart) findViewById(R.id.linechart1);
			mLineChart1.setUnit(" du");
			mLineChart1.setDrawUnitsInChart(true);
			mLineChart1.setYRange(YRANGE_MIN, YRANGE_MAX, false);
			mLineChart1.setDrawYValues(false);
			mLineChart1.setDescription("");
			mLineChart1.setTouchEnabled(true);
			mLineChart1.setDragEnabled(true);

			mLineChart2 = (LineChart) findViewById(R.id.linechart2);
			mLineChart2.setUnit(" du");
			mLineChart2.setDrawUnitsInChart(true);
			mLineChart2.setYRange(YRANGE_MIN, YRANGE_MAX, false);
			mLineChart2.setDrawYValues(false);
			mLineChart2.setDescription("");
			mLineChart2.setTouchEnabled(true);
			mLineChart2.setDragEnabled(true);
			
			mLineChart3 = (LineChart) findViewById(R.id.linechart3);
			mLineChart3.setUnit(" du");
			mLineChart3.setDrawUnitsInChart(true);
			mLineChart3.setYRange(YRANGE_MIN, YRANGE_MAX, false);
			mLineChart3.setDrawYValues(false);
			mLineChart3.setDescription("");
			mLineChart3.setTouchEnabled(true);
			mLineChart3.setDragEnabled(true);
			
			int i = 0;
			while(i < xLineChartLen){
				xLineChartVals.add("" + i);
				yLineChartVals1.add(new Entry(110,i));
				yLineChartVals2.add(new Entry(20,i));
				yLineChartVals3.add(new Entry(30,i));
				i++;
			}

			setBarChartData(0,0,0);
		}else{
			mBarChart.setVisibility(View.GONE);
		}
//		WebView mWebView = (WebView) findViewById(R.id.wv);
//		mWebView.getSettings().setJavaScriptEnabled(true);
//		//requestFocus();
//		mWebView.requestFocus();
//		mWebView.loadUrl("http://www.baidu.com");
	}
	
	public void setBarChartData(float x, float y, float z){
		Log.i("","" + x + y + z);
		ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
		yVals.add(new BarEntry(x, 0));
		yVals.add(new BarEntry(y, 1));
		yVals.add(new BarEntry(z, 2));
		BarDataSet bardataset = new BarDataSet(yVals, "Acceleration");
		bardataset.setBarSpacePercent(30f);
		ArrayList<BarDataSet> arraybardataset = new ArrayList<BarDataSet>();
		arraybardataset.add(bardataset);
		BarData data = new BarData(xVals, arraybardataset);
		mBarChart.setData(data);
		mBarChart.invalidate();
		setLineChartData(x,y,z);
	}
	
	public void setLineChartData(float x, float y, float z){
		yLineChartVals1.remove(0);
		yLineChartVals2.remove(0);
		yLineChartVals3.remove(0);
		int i = 0;
		for(i = 0; i < xLineChartLen - 1; i++){
			yLineChartVals1.get(i).setXIndex(i);
			yLineChartVals2.get(i).setXIndex(i);
			yLineChartVals3.get(i).setXIndex(i);
		}
		yLineChartVals1.add(new Entry(x,xLineChartLen - 1));
		yLineChartVals2.add(new Entry(y,xLineChartLen - 1));
		yLineChartVals3.add(new Entry(z,xLineChartLen - 1));
		LineDataSet set; 
		ArrayList<LineDataSet> linedataset;
		LineData data;
		
		set = new LineDataSet(yLineChartVals1, "X");
		set.setColor(Color.BLUE);
		set.disableDashedLine();
		set.setDrawCircles(false);
		linedataset = new ArrayList<LineDataSet>();
		linedataset.add(set);
		data = new LineData(xLineChartVals, linedataset);
		mLineChart1.setData(data);
		mLineChart1.invalidate();
		
		set = new LineDataSet(yLineChartVals2, "Y");
		set.setColor(Color.BLUE);
		set.disableDashedLine();
		set.setDrawCircles(false);
		linedataset = new ArrayList<LineDataSet>();
		linedataset.add(set);
		data = new LineData(xLineChartVals, linedataset);
		mLineChart2.setData(data);
		mLineChart2.invalidate();
		
		set = new LineDataSet(yLineChartVals3, "Z");
		set.setColor(Color.BLUE);
		set.disableDashedLine();
		set.setDrawCircles(false);
		linedataset = new ArrayList<LineDataSet>();
		linedataset.add(set);
		data = new LineData(xLineChartVals, linedataset);
		mLineChart3.setData(data);
		mLineChart3.invalidate();
		
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
					//Intent intent = new Intent(this,BLEDevice.class);
					//startActivity(intent);
					Intent sendIntent = new Intent(BLEDevice.BLE_CONNECT_DEVICE);
					sendIntent.putExtra(BLEDevice.BLE_DEVICE_NAME, "a");
					sendIntent.putExtra(BLEDevice.BLE_DEVICE_ADDRESS, "a");
					sendBroadcast(sendIntent);
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
                                	if(mBluetoothLeService != null){
                                		unbindService(mBLEServiceConnection);
                                	}
                                	if(mDataHandlerService != null){
                                		unbindService(mDataHandlerServiceConnection);
                                	}
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
            	unbindService(mDataHandlerServiceConnection);
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
            }else if(DRAW_BARCHART.equals(action)){
            	if(viewAcceleration){
            		float[] x = new float[DataHandlerService.LEN_OF_RECEIVED_DATA];
            		float[] y = new float[DataHandlerService.LEN_OF_RECEIVED_DATA];
            		float[] z = new float[DataHandlerService.LEN_OF_RECEIVED_DATA];
	            	
	            	x = intent.getFloatArrayExtra("X");
	            	//x = 128 * (float) Math.sin(x / 100);
	            	y = intent.getFloatArrayExtra("Y");
	            	//y = 128 * (float) Math.cos(y / 100);
	            	z = intent.getFloatArrayExtra("Z");
	            	//z = 128 * (float) Math.tan(z / 100);
	            	for(int i = 0; i < DataHandlerService.LEN_OF_RECEIVED_DATA; i++){
	            		setBarChartData(x[i],y[i],z[i]);
	            	}
            	}
            }
        }
	};
	
	private static IntentFilter makeBLEIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLEDevice.BLE_CONNECT_DEVICE);
        intentFilter.addAction(DRAW_BARCHART);
        return intentFilter;
    }
}
