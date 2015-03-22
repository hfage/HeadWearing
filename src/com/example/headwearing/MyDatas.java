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
		float skewness_x_value = 0f;
		float skewness_y_value = 0f;
		float skewness_z_value = 0f;
		float kurtosis_x_value = 0f;
		float kurtosis_y_value = 0f;
		float kurtosis_z_value = 0f;
		float correlation_x_y_value = 0f;
		float correlation_y_z_value = 0f;
		float correlation_z_x_value = 0f;
		public boolean used = false;
		public boolean using = false;
		public int error_time = 5;
		public boolean enData(float x, float y, float z){
			if(!using){
				if(len < LEN_OF_SIGNAL_DATA){
					len++;
					data_x.add(x);
					data_y.add(y);
					data_z.add(z);
				}else{
					Log.w("test","else");
					data_x.remove(0);
					data_x.add(x);
					data_y.remove(0);
					data_y.add(y);
					data_z.remove(0);
					data_z.add(z);
				}
				//Log.w("test","endata"+len);
				return true;
			}else{
				error_time--;
				if(error_time == 0){
					resetDatas();
				}
				return false;
			}
			
		}
		public boolean resetDatas(){
			Log.w(TAG,"reset");
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
			kurtosis_x_value = 0f;
			kurtosis_y_value = 0f;
			kurtosis_z_value = 0f;
			correlation_x_y_value = 0f;
			correlation_y_z_value = 0f;
			correlation_z_x_value = 0f;
			used = false;
			using = false;
			error_time =  5;
			return true;
		}
		public void calculate(){
			Log.w(TAG,"calculate");
			using = true;
			sum();
			meanValue();
			nVariance();
			standardDeviation();
			kurtosis();
			correlation();
			using = false;
			Log.w(TAG,"calculate over.");
			if(HeadWear.DEBUG){
				Log.w(TAG + "meanValue:", "" + mean_x_value);
				Log.w(TAG + "meanValue:", "" + mean_y_value);
				Log.w(TAG + "meanValue:", "" + mean_z_value);
				Log.w(TAG + "n_variance:Value", "" + n_variance_x_value);
				Log.w(TAG + "n_varianceValue:", "" + n_variance_y_value);
				Log.w(TAG + "n_varianceValue:", "" + n_variance_z_value);
				Log.w(TAG + "standar_deviationValue:", "" + standard_deviation_x_value);
				Log.w(TAG + "standar_deviationValue:", "" + standard_deviation_y_value);
				Log.w(TAG + "standar_deviationValue:", "" + standard_deviation_z_value);
				Log.w(TAG + "kurtosisValue:", "" + kurtosis_x_value);
				Log.w(TAG + "kurtosisValue:", "" + kurtosis_y_value);
				Log.w(TAG + "kurtosisValue:", "" + kurtosis_z_value);
				Log.w(TAG + "correlationValue:", "" + correlation_x_y_value);
				Log.w(TAG + "correlationValue:", "" + correlation_y_z_value);
				Log.w(TAG + "correlationValue:", "" + correlation_z_x_value);
			}
		}
		
		public boolean correlation(){
			if(HeadWear.DEBUG){
				Log.w(TAG,"method: correlation");
			}
			float d_x = 0f;
			float d_y = 0f;
			float d_z = 0f;
			for(int i = 0; i < LEN_OF_SIGNAL_DATA; i++){
				d_x = data_x.get(i) - mean_x_value;
				d_y = data_y.get(i) - mean_y_value;
				d_z = data_z.get(i) - mean_z_value;
				correlation_x_y_value += d_x * d_y;
				correlation_y_z_value += d_y * d_z;
				correlation_z_x_value += d_z * d_x;
			}
			correlation_x_y_value = (float) (correlation_x_y_value / (Math.sqrt(n_variance_x_value * n_variance_y_value)));
			correlation_y_z_value = (float) (correlation_y_z_value / (Math.sqrt(n_variance_y_value * n_variance_z_value)));
			correlation_z_x_value = (float) (correlation_z_x_value / (Math.sqrt(n_variance_z_value * n_variance_x_value)));
			return true;
		}
		
		public boolean kurtosis(){
			if(HeadWear.DEBUG){
				Log.w(TAG,"kurtosis");
			}
			for(int i = 0; i < LEN_OF_SIGNAL_DATA; i++){
				kurtosis_x_value += Math.pow((data_x.get(i) - mean_x_value),4);
				kurtosis_y_value += Math.pow((data_x.get(i) - mean_y_value),4);
				kurtosis_z_value += Math.pow((data_x.get(i) - mean_z_value),4);
			}
			kurtosis_x_value = (float) (kurtosis_x_value / (LEN_OF_SIGNAL_DATA * Math.pow(standard_deviation_x_value, 4)));
			kurtosis_y_value = (float) (kurtosis_y_value / (LEN_OF_SIGNAL_DATA * Math.pow(standard_deviation_y_value, 4)));
			kurtosis_z_value = (float) (kurtosis_z_value / (LEN_OF_SIGNAL_DATA * Math.pow(standard_deviation_z_value, 4)));
			return true;
		}
		
		public boolean skewness(){
			if(HeadWear.DEBUG){
				Log.w(TAG,"skewness");
			}
			for(int i = 0; i < LEN_OF_SIGNAL_DATA; i++){
				skewness_x_value += Math.pow((data_x.get(i) - mean_x_value),3);
				skewness_y_value += Math.pow((data_y.get(i) - mean_y_value),3);
				skewness_z_value += Math.pow((data_z.get(i) - mean_z_value),3);
			}
			skewness_x_value = (float) ((LEN_OF_SIGNAL_DATA * skewness_x_value) / ((LEN_OF_SIGNAL_DATA - 1) * (LEN_OF_SIGNAL_DATA - 2) * Math.pow(standard_deviation_x_value, 3)));
			skewness_y_value = (float) ((LEN_OF_SIGNAL_DATA * skewness_y_value) / ((LEN_OF_SIGNAL_DATA - 1) * (LEN_OF_SIGNAL_DATA - 2) * Math.pow(standard_deviation_y_value, 3)));
			skewness_z_value = (float) ((LEN_OF_SIGNAL_DATA * skewness_z_value) / ((LEN_OF_SIGNAL_DATA - 1) * (LEN_OF_SIGNAL_DATA - 2) * Math.pow(standard_deviation_z_value, 3)));
			return true;
		}
		
		public boolean standardDeviation(){
			Log.w(TAG,"standardDeviation");
			standard_deviation_x_value = (float) Math.sqrt(n_variance_x_value / LEN_OF_SIGNAL_DATA);
			standard_deviation_y_value = (float) Math.sqrt(n_variance_y_value / LEN_OF_SIGNAL_DATA);
			standard_deviation_z_value = (float) Math.sqrt(n_variance_z_value / LEN_OF_SIGNAL_DATA);
			return true;
		}
		public boolean sum(){
			Log.w(TAG,"sum");
			if(len == LEN_OF_SIGNAL_DATA){
				for(int i = 0; i < LEN_OF_SIGNAL_DATA; i++){
					total_x_value += data_x.get(i);
					total_y_value += data_y.get(i);
					total_z_value += data_z.get(i);
				}
				return true;
			}else{
				return false;
			}
		}
		public boolean meanValue(){
			Log.w(TAG,"meanValue");
			if(len == LEN_OF_SIGNAL_DATA){
				mean_x_value = total_x_value / LEN_OF_SIGNAL_DATA;
				mean_y_value = total_y_value / LEN_OF_SIGNAL_DATA;
				mean_z_value = total_z_value / LEN_OF_SIGNAL_DATA;
				return true;
			}else{
				return false;
			}
		}
		public boolean nVariance(){
			Log.w(TAG,"nVariance");
			for(int i = 0; i < LEN_OF_SIGNAL_DATA; i++){
				n_variance_x_value += Math.pow((data_x.get(i) - mean_x_value),2);
				n_variance_y_value += Math.pow((data_y.get(i) - mean_y_value),2);
				n_variance_z_value += Math.pow((data_z.get(i) - mean_z_value),2);
			}
			return true;
		}
	}
}