package com.example.headwearing;

import java.util.BitSet;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


public class DataHandlerService extends Service{
	public boolean DEBUG = true;
	public boolean simulation = true;
	private final static String TAG = "testDataHandlerSerivce";
	public final static String DATA_SIMULATION = "DATA SIMULATION";
	public final static String DATA_RECEIVE = "DATA RECEIVE";
	
	public boolean init(){
		registerReceiver(mReceiver, makeIntentFilter());
		if(simulation){
			new Thread(new Runnable() {                    
				@Override
				public void run() {
					dataSimulation();
				}
			}).start();
		}
		
		return true;
	}
	
	public void dataSimulation(){
		int n = 100;
		while(n != 100){
			Log.i(TAG,"dataSimulation n : " + n);
			Intent intent = new Intent(DATA_SIMULATION);
			intent.putExtra("data", "" + n);
			sendBroadcast(intent);
			n--;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(int i = 0; i < SampleGattAttributes.standing_acc[0].length/100; i++){
			Intent intent = new Intent(DATA_SIMULATION);
			intent.putExtra("data", ""+SampleGattAttributes.standing_acc[0][i]);
			sendBroadcast(intent);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void dataHandler(String data){
		if(DEBUG)Log.i(TAG,"dataHandler data: " + data);
		BitSet bit = new BitSet(100);
		bit.set(1);
		float x,y,z;
		x = (float)Double.parseDouble(data);
		y = x;
		z = x;
		if(HeadWear.viewAcceleration){
			Intent intent = new Intent(HeadWear.DRAW_BARCHART);
			intent.putExtra("X", x);
			intent.putExtra("Y", y);
			intent.putExtra("Z", z);
			sendBroadcast(intent);
		}
	}
	 
	public class LocalBinder extends Binder {
        DataHandlerService getService() {
            return DataHandlerService.this;
        }
    }
	
	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		if(DEBUG)Log.i(TAG,"onBind");
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if(action.equals(DATA_SIMULATION)){
				final String data = intent.getStringExtra("data");
				
				new Thread(new Runnable() {                    
					@Override
					public void run() {
						dataHandler(data);
					}
				}).start();
			}
		}
	};
	
	private static IntentFilter makeIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DATA_SIMULATION);
		return intentFilter;
	}
	
}