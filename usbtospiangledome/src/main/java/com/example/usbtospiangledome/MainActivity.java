package com.example.usbtospiangledome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {
    final String TAG="ch341par";
    MultiPortManager multiPort;
    UsbManager usbManager;

    Button ButtonSpiCall;
    Button ButtonOpen;
    Button Button_Factory_Dfefault;
    Button ButtonGetAngle;

    TextView TvChipVersion;
    TextView TvSpiReceive;
    TextView TvAngleRaw;
    TextView TvAngle;

    EditText EtSpiSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    void init(){
        ButtonSpiCall=findViewById(R.id.btn_spi_call);
        ButtonOpen=findViewById(R.id.btn_open);
        Button_Factory_Dfefault=findViewById(R.id.btn_factory_dfefault);
        ButtonGetAngle=findViewById(R.id.btn_get_angle);

        TvChipVersion=findViewById(R.id.tv_chipversion);
        TvSpiReceive=findViewById(R.id.tv_spi_receive);
        TvAngleRaw=findViewById(R.id.tv_angle_raw);
        TvAngle=findViewById(R.id.tv_angle);

        EtSpiSend=findViewById(R.id.et_spi_send);
        ButtonGetAngle.setEnabled(true);
        ButtonSpiCall.setEnabled(true);
        ButtonSpiCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpiCall();
            }
        });
        /*ButtonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open();
            }
        });*/
        /*Button_Factory_Dfefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_dactory_dfefault();
            }
        });*/
        usbManager= (UsbManager) getSystemService(Context.USB_SERVICE);
        multiPort=new MultiPortManager(usbManager,this,"cn.wch.ch341par");
        ButtonGetAngle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_angle();
            }
        });
    }

    void open(){
        if(multiPort.ResumeUsbList()!=0){
           Toast.makeText(this,"打开设备失败",Toast.LENGTH_SHORT).show();
            return;
        }
        //ButtonSpiCall.setEnabled(true);
        //ButtonGetAngle.setEnabled(true);
		//Button_Factory_Dfefault.setEnabled(true);

        initCH341();
    }
    
    void initCH341(){
        if(multiPort.CH341SystemInit()!=0){
            //Log.d(TAG,"CH341UsbToSpi芯片版本: 获取芯片版本失败!");
            TvChipVersion.setText("CH341UsbToSpi芯片版本: 获取失败!");
        }
        else
        {
            TvChipVersion.setText("CH341UsbToSpi芯片版本: "+multiPort.ChipVersion);
        }
    }

    /*void set_dactory_dfefault(){
        int i;
        byte byte_date[] = new byte[2];
        byte rd_bytes[] = new byte[2];
        char[] ch_bytes = {0x80, 0x00, 
                            0x81, 0x00,
                            0x82, 0x00,
                            0x83, 0x00, 
                            0x84, 0xC0, 
                            0x85, 0x3F, 
                            0x86, 0x1C, 
                            0x89, 0x00,
                            };

        for(i = 0; i < ch_bytes.length; i += 2)
        {
            
            byte_date[0] = (byte) ch_bytes[i];
            byte_date[1] = (byte) ch_bytes[i + 1];
            if(!multiPort.CH341StreamSPI4((char) 0x80,byte_date.length,byte_date))
            {
                //Log.d(TAG,"CH341StreamSPI4失败!");
                Toast.makeText(this,"初始化失败, 请重试!",Toast.LENGTH_SHORT).show();
                break;
            }
            
            try
            {
                Thread.sleep(25);
            } catch (Exception e) {
                
            }

            rd_bytes[0] = 0x00;
            rd_bytes[1] = 0x00;
            if((!multiPort.CH341StreamSPI4((char) 0x80,rd_bytes.length,rd_bytes))
                || (rd_bytes[0] != ((byte) ch_bytes[i + 1])))
            {
                Toast.makeText(this,"初始化失败, 请重试!",Toast.LENGTH_SHORT).show();
                break;
            }
        }*/

        /*if (i == ch_bytes.length)
        {
            //Log.d(TAG,"初始化成功!");
            Toast.makeText(this,"初始化成功!",Toast.LENGTH_SHORT).show();
        }
    }*/

    void SpiCall(){
        open();
        Log.d(TAG,"开始CH341StreamSPI4测试");
        //获取控件hex值
        String s = EtSpiSend.getText().toString();
        if(s==null || "".equals(s)){
            Toast.makeText(this,"发送内容为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!s.matches("([0-9|a-f|A-F]{2})*")){
            Toast.makeText(this,"发送内容不符合HEX规范",Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] bytes = hexStringToBytes(s);

        if(!multiPort.CH341StreamSPI4((char) 0x80,bytes.length,bytes)){
            //Log.d(TAG,"CH341StreamSPI4失败");
            TvSpiReceive.setText("通信失败!");
            return;
        }
        Log.d(TAG,bytesToHexString(bytes,bytes.length));
        TvSpiReceive.setText("读取到的数据: "+bytesToHexString(bytes,bytes.length));

    }

    void get_angle(){//获取角度传感器角度值
        open();
        int Int_Data;
        byte[] bytes = {0x00, 0x00};

        if(!multiPort.CH341StreamSPI4((char) 0x80,bytes.length,bytes)){
            //Log.d(TAG,"CH341StreamSPI4失败");
            TvAngleRaw.setText("通信失败!");
            TvAngle.setText("通信失败!");
            return;
        }
        
        if (bytes.length == 2)
        {
            Int_Data = (int) ((0xFF & bytes[0]) << 8) | (0xFF & bytes[1]);
            String TT="0x " + bytesToHexString(bytes,bytes.length) + "= " + Int_Data;
            TvAngleRaw.setText(TT);
            float Angel_date = (float) (Int_Data * 360) / 65536;
            TvAngle.setText("换算成角度值: " + Angel_date);
        }
        else
        {
            TvAngleRaw.setText("获取失败!");
            TvAngle.setText("获取失败!");
        }
    }

    @Override
    protected void onDestroy() {
        if(multiPort != null) {
            if(multiPort.isConnected()) {
                multiPort.CloseDevice();
            }
            multiPort = null;
        }
        super.onDestroy();
    }


    /*public static byte[] getBytes(char[] chars,int len) {
        Charset cs = Charset.forName("UTF-8");
        int l=Math.min(chars.length,len);
        byte[] buff=new byte[l];
        for(int i=0;i<l;i++){
            buff[i]=(byte)chars[i];
        }
       /* Buffer cb = CharBuffer.allocate(l);
        for(int i=0;i<l;i++){
        	cb.put(chars[i]);
        }
        cb.flip();
        ByteBuffer bb = cs.encode(cb);*/
        //return buff;
    //}*/

    public static String bytesToHexString(byte[] bArr,int mLen) {
        if (bArr == null || bArr.length==0)
            return "";
        int len=Math.min(mLen, bArr.length);

        StringBuffer sb = new StringBuffer(bArr.length);
        String sTmp;
        for (int i = 0; i < len; i++) {
            sTmp = Integer.toHexString(0xFF & bArr[i]);
            if (sTmp.length() < 2)
                sb.append(0);
            sb.append(sTmp.toUpperCase() + " ");
        }
        return sb.toString();
    }

    public static byte[] hexStringToBytes(@NonNull final String arg) {
        if (arg != null) {
            // 1.先去除String中的' '，然后将String转换为char数组
            char[] NewArray = new char[1000];
            char[] array = arg.toCharArray();
            int length = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i] != ' ') {
                    NewArray[length] = array[i];
                    length++;
                }
            }
             //将char数组中的值转成一个实际的十进制数组
            int EvenLength = (length % 2 == 0) ? length : length + 1;
            if (EvenLength != 0) {
                int[] data = new int[EvenLength];
                data[EvenLength - 1] = 0;
                for (int i = 0; i < length; i++) {
                    if (NewArray[i] >= '0' && NewArray[i] <= '9') {
                        data[i] = NewArray[i] - '0';
                    } else if (NewArray[i] >= 'a' && NewArray[i] <= 'f') {
                        data[i] = NewArray[i] - 'a' + 10;
                    } else if (NewArray[i] >= 'A' && NewArray[i] <= 'F') {
                        data[i] = NewArray[i] - 'A' + 10;
                    }
                }
                 //将 每个char的值每两个组成一个16进制数据
                byte[] byteArray = new byte[EvenLength / 2];
                for (int i = 0; i < EvenLength / 2; i++) {
                    byteArray[i] = (byte) (data[i * 2] * 16 + data[i * 2 + 1]);
                }
                return byteArray;
            }
        }
        return new byte[] {};
    }
}