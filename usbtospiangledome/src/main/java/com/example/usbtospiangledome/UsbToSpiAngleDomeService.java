package com.example.usbtospiangledome;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.ToggleButton;
import android.widget.Toast;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.hardware.usb.UsbManager;

public class UsbToSpiAngleDomeService extends Service {

	private String TAG = "ts";
    private Context context;
    private int pwm_thresh = 50;
	private float Angel_date;
	MultiPortManager multiPort;
	UsbManager usbManager;
    
    String action5 = "action_angle_get";
    String action5_report_action = "action_angle_report";
    String action5_extra = "action_angle_get_value";
	
    @Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		String action  = intent.getAction();
		if(action5.equals(action)){
            context=getApplicationContext();
			get_angle();
			float angle = Angel_date;
			Log.d(TAG,"angle ="+angle);
			Intent reportIntent = new Intent(action5_report_action);
			reportIntent.putExtra(action5_extra, angle);
			sendBroadcast(reportIntent);
		}
	}
	 void open(){
        if(multiPort.ResumeUsbList()!=0){
		   Log.d(TAG,"打开设备失败!");
            return;
        }
        initCH341();
    }
	void initCH341(){
        if(multiPort.CH341SystemInit()!=0){
            //Log.d(TAG,"CH341UsbToSpi芯片版本: 获取芯片版本失败!");
            //TvChipVersion.setText("CH341UsbToSpi芯片版本: 获取失败!");
			Log.d(TAG,"CH341UsbToSpi芯片版本: 获取失败!");
        }
        else
        {
            //TvChipVersion.setText("CH341UsbToSpi芯片版本: "+multiPort.ChipVersion);
			Log.d(TAG,"CH341UsbToSpi芯片版本: 获取成功!");
        }
    }
	void get_angle(){//获取角度传感器角度值
        int Int_Data;
        byte[] bytes = {0x00, 0x00};
        usbManager= (UsbManager)getSystemService(Context.USB_SERVICE);
        multiPort=new MultiPortManager(usbManager,this,"cn.wch.ch341par");
        open();
        if(!multiPort.CH341StreamSPI4((char) 0x80,bytes.length,bytes)){
            //Log.d(TAG,"CH341StreamSPI4失败");
            Log.d(TAG,"CH341StreamSPI4失败");
            return;
        }
        
        if (bytes.length == 2)
        {
            Int_Data = (int) ((0xFF & bytes[0]) << 8) | (0xFF & bytes[1]);
            //TvAngleRaw.setText("16位寄存器值:0x " + bytesToHexString(bytes,bytes.length) + "= " + Int_Data);
            Angel_date = (float) (Int_Data * 360) / 65536;
			Log.d(TAG,"Angel_date="+Angel_date);
            //TvAngle.setText("换算成角度值: " + Angel_date);
        }
        else
        {
			Log.d(TAG,"获取失败");
        }
    }	
}
