package com.example.headwearing;

import java.util.ArrayList;

import android.util.Log;



class MyDatas{
	public static int LEN_OF_SIGNAL_DATA = 512;
	public static int HALF_OF_SIGNAL_DATA = LEN_OF_SIGNAL_DATA / 2;
	public static String TAG = "MyDatas ";
	public class SignalData{
		ArrayList<Float> data_x = new ArrayList<Float>();
		ArrayList<Float> data_y = new ArrayList<Float>();
		ArrayList<Float> data_z = new ArrayList<Float>();
		int len = 0;
		float total_x_value = 0f;
		float total_y_value = 0f;
		float total_z_value = 0f;
		float mean_x_value = 0f;
		float mean_y_value = 0f;
		float mean_z_value = 0f;
		float n_variance_x_value = 0f;
		float n_variance_y_value = 0f;
		float n_variance_z_value = 0f;
		float standard_deviation_x_value = 0f;
		float standard_deviation_y_value = 0f;
		float standard_deviation_z_value = 0f;
		public boolean used = false;
		public boolean enData(float x, float y, float z){
			if(len < LEN_OF_SIGNAL_DATA){
				len++;
				//if(len == 512)Log.e("test","512");
				//if(len == 513)Log.e("test","513");
				//if(len == 511)Log.e("test","511");
				data_x.add(x);
				data_y.add(y);
				data_z.add(z);
			}else{
				Log.e("test","else");
				data_x.remove(0);
				data_x.add(x);
				data_y.remove(0);
				data_y.add(y);
				data_z.remove(0);
				data_z.add(z);
			}
			Log.e("test","endata"+len);
			return true;
		}
		public boolean resetDatas(){
			Log.e(TAG,"reset");
			data_x.clear();
			data_y.clear();
			data_z.clear();
			len = 0;
			total_x_value = 0f;
			total_y_value = 0f;
			total_z_value = 0f;
			mean_x_value = 0f;
			mean_y_value = 0f;
			mean_z_value = 0f;
			n_variance_x_value = 0f;
			n_variance_y_value = 0f;
			n_variance_z_value = 0f;
			standard_deviation_x_value = 0f;
			standard_deviation_y_value = 0f;
			standard_deviation_z_value = 0f;
			return true;
		}
		public void calculate(){
			Log.e(TAG,"calculate");
			sum();
			meanValue();
			nVariance();
			standardDeviation();
			Log.e(TAG,"calculate over.");
		}
		public boolean standardDeviation(){
			Log.i(TAG,"standardDeviation");
			standard_deviation_x_value = (float) Math.sqrt(LEN_OF_SIGNAL_DATA * n_variance_x_value);
			standard_deviation_y_value = (float) Math.sqrt(LEN_OF_SIGNAL_DATA * n_variance_y_value);
			standard_deviation_z_value = (float) Math.sqrt(LEN_OF_SIGNAL_DATA * n_variance_z_value);
			if(HeadWear.DEBUG){
				Log.i(TAG + "standardDeviation","" + standard_deviation_x_value);
				Log.i(TAG + "standardDeviation","" + standard_deviation_y_value);
				Log.i(TAG + "standardDeviation","" + standard_deviation_z_value);
			}
			return true;
		}
		public boolean sum(){
			Log.e(TAG,"sum");
			if(len == LEN_OF_SIGNAL_DATA){
				for(int i = 0; i < LEN_OF_SIGNAL_DATA; i++){
					total_x_value += data_x.get(i);
					total_y_value += data_y.get(i);
					total_z_value += data_z.get(i);
				}
				if(HeadWear.DEBUG){
					Log.i(TAG + "sum","" + total_x_value);
					Log.i(TAG + "sum","" + total_y_value);
					Log.i(TAG + "sum","" + total_z_value);
				}
				return true;
			}else{
				return false;
			}
		}
		public boolean meanValue(){
			Log.e(TAG,"meanValue");
			if(len == LEN_OF_SIGNAL_DATA){
				mean_x_value = total_x_value / LEN_OF_SIGNAL_DATA;
				mean_y_value = total_y_value / LEN_OF_SIGNAL_DATA;
				mean_z_value = total_z_value / LEN_OF_SIGNAL_DATA;
				if(HeadWear.DEBUG){
					Log.i(TAG + "meanValue","" + mean_x_value);
					Log.i(TAG + "meanValue","" + mean_y_value);
					Log.i(TAG + "meanValue","" + mean_z_value);
				}
				return true;
			}else{
				return false;
			}
		}
		public boolean nVariance(){
			Log.e(TAG,"nVariance");
			for(int i = 0; i < LEN_OF_SIGNAL_DATA; i++){
				n_variance_x_value += Math.pow((data_x.get(i) - mean_x_value),2);
				n_variance_y_value += Math.pow((data_y.get(i) - mean_y_value),2);
				n_variance_z_value += Math.pow((data_z.get(i) - mean_z_value),2);
			}
			if(HeadWear.DEBUG){
				Log.i(TAG + "vVariance","" + n_variance_x_value);
				Log.i(TAG + "vVariance","" + n_variance_y_value);
				Log.i(TAG + "vVariance","" + n_variance_z_value);
			}
			return true;
		}
	}
}