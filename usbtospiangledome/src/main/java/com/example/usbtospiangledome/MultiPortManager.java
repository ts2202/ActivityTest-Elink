package com.example.usbtospiangledome;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

public class MultiPortManager {
	public static final String TAG = "ch341par";
	private static final String Library = "Library Version 0.1";
	private static final String DevName = "CH341x";
	private static final String Description = "Used for EPP/MEM/IIC/SPI Mode";
	private UsbManager mUsbmanager;
	private PendingIntent mPendingIntent;
	private UsbDevice mUsbDevice;
	private UsbInterface mInterface;
	private UsbEndpoint mCtrlPoint;
	private UsbEndpoint mBulkInPoint;
	private UsbEndpoint mBulkOutPoint;
	private UsbDeviceConnection mDeviceConnection;
	private Context mContext;
	private String mString;
	private boolean BroadcastFlag = false;
	public boolean READ_ENABLE = false;
	public read_thread readThread;
	
	private byte [] readBuffer; /*circular buffer*/
	private byte [] usbdata;
	private int writeIndex;
	private int readIndex;
	private int readcount;
	private int totalBytes;
	
	private char READ_MODE;
	private char WRITE_MODE;
	public int ChipVersion;
	
	private ArrayList<String> DeviceNum = new ArrayList();
	protected final Object ReadQueueLock = new Object();
	protected final Object WriteQueueLock = new Object();
	private int DeviceCount;
	private int mBulkPacketSize;
	final int  maxnumbytes = 65536;
	private char StreamMode = 1;
	private static final int CH341_EPP_IO_MAX = 31;
	private static final int CH341_PACKET_LENGTH = 32;
	private static final int DEFAULT_BUFFER_LENGTH = 0x0400;
	private static final int MAX_BUFFER_LENGTH = 0x1000;
	
	public int WriteTimeOutMillis;
	public int ReadTimeOutMillis;
	private int DEFAULT_TIMEOUT = 500;
	
	
	private char ReadByte;
	private char Sector_Erase;
	private char Block_Erase;
	private char Chip_Erase;
	private char Byte_Program;
	private char AAI;
	private char RDSR;
	private char EWSR;
	private char WRSR;
	private char WREN;
	private char WRDI;
	private char ReadID;
	private char iChipselect = 0x80;
	
	//Used for high bit transform
	private	char mMsbTable[] = { 0x00, 0x80, 0x40, 0xC0, 0x20, 0xA0, 0x60, 0xE0, 
					0x10, 0x90, 0x50, 0xD0, 0x30, 0xB0, 0x70, 0xF0,		// 0XH
					0x08, 0x88, 0x48, 0xC8, 0x28, 0xA8, 0x68, 0xE8, 
					0x18, 0x98, 0x58, 0xD8, 0x38, 0xB8, 0x78, 0xF8,		// 1XH
				    0x04, 0x84, 0x44, 0xC4, 0x24, 0xA4, 0x64, 0xE4, 
					0x14, 0x94, 0x54, 0xD4, 0x34, 0xB4, 0x74, 0xF4,		// 2XH
					0x0C, 0x8C, 0x4C, 0xCC, 0x2C, 0xAC, 0x6C, 0xEC, 
					0x1C, 0x9C, 0x5C, 0xDC, 0x3C, 0xBC, 0x7C, 0xFC,		// 3XH
					0x02, 0x82, 0x42, 0xC2, 0x22, 0xA2, 0x62, 0xE2, 
					0x12, 0x92, 0x52, 0xD2, 0x32, 0xB2, 0x72, 0xF2,		// 4XH
					0x0A, 0x8A, 0x4A, 0xCA, 0x2A, 0xAA, 0x6A, 0xEA, 
					0x1A, 0x9A, 0x5A, 0xDA, 0x3A, 0xBA, 0x7A, 0xFA,		// 5XH
					0x06, 0x86, 0x46, 0xC6, 0x26, 0xA6, 0x66, 0xE6, 
					0x16, 0x96, 0x56, 0xD6, 0x36, 0xB6, 0x76, 0xF6,		// 6XH
					0x0E, 0x8E, 0x4E, 0xCE, 0x2E, 0xAE, 0x6E, 0xEE, 
					0x1E, 0x9E, 0x5E, 0xDE, 0x3E, 0xBE, 0x7E, 0xFE,		// 7XH
					0x01, 0x81, 0x41, 0xC1, 0x21, 0xA1, 0x61, 0xE1, 
					0x11, 0x91, 0x51, 0xD1, 0x31, 0xB1, 0x71, 0xF1,		// 8XH
					0x09, 0x89, 0x49, 0xC9, 0x29, 0xA9, 0x69, 0xE9, 
					0x19, 0x99, 0x59, 0xD9, 0x39, 0xB9, 0x79, 0xF9,		// 9XH
					0x05, 0x85, 0x45, 0xC5, 0x25, 0xA5, 0x65, 0xE5, 
					0x15, 0x95, 0x55, 0xD5, 0x35, 0xB5, 0x75, 0xF5,		// AXH
					0x0D, 0x8D, 0x4D, 0xCD, 0x2D, 0xAD, 0x6D, 0xED, 
					0x1D, 0x9D, 0x5D, 0xDD, 0x3D, 0xBD, 0x7D, 0xFD,		// BXH
					0x03, 0x83, 0x43, 0xC3, 0x23, 0xA3, 0x63, 0xE3, 
					0x13, 0x93, 0x53, 0xD3, 0x33, 0xB3, 0x73, 0xF3,		// CXH
					0x0B, 0x8B, 0x4B, 0xCB, 0x2B, 0xAB, 0x6B, 0xEB, 
					0x1B, 0x9B, 0x5B, 0xDB, 0x3B, 0xBB, 0x7B, 0xFB,		// DXH
					0x07, 0x87, 0x47, 0xC7, 0x27, 0xA7, 0x67, 0xE7, 
					0x17, 0x97, 0x57, 0xD7, 0x37, 0xB7, 0x77, 0xF7,		// EXH
					0x0F, 0x8F, 0x4F, 0xCF, 0x2F, 0xAF, 0x6F, 0xEF, 
					0x1F, 0x9F, 0x5F, 0xDF, 0x3F, 0xBF, 0x7F, 0xFF };	// FXH
	
	public MultiPortManager(UsbManager manager,Context context, String AppName) {
		super();
		readBuffer = new byte [maxnumbytes];
		usbdata = new byte[1024];
		writeIndex = 0;
		readIndex = 0;
		
		mUsbmanager = manager;
		mContext = context;
		mString = AppName;
		WriteTimeOutMillis = 1000;
		ReadTimeOutMillis = 10000;
			
		ArrayAddDevice("1a86:5512");
		ArrayAddDevice("1a86:5523");
	}
	

	private void ArrayAddDevice(String str)
	{
		DeviceNum.add(str);
		DeviceCount = DeviceNum.size();
	}

	public void SetChipSel(char mChar)
	{
		this.iChipselect = mChar;
	}


	public int[][] CH341GetPIDVID()
	{
		String mStr;
		int middle;
		int listSize = DeviceNum.size();
		int [][] arrayPIDVID = new int[2][listSize];
		for(int i = 0; i < listSize; i++) {
			mStr = DeviceNum.get(i);
			middle = mStr.indexOf(":");

			arrayPIDVID[0][i] = Integer.parseInt(mStr.substring(0, middle), 16);
			arrayPIDVID[1][i] = Integer.parseInt(mStr.substring(middle + 1, mStr.length()), 16);
		}
		return arrayPIDVID;
	}
	
	public int CH341GetBufSizeValue(){
		return maxnumbytes;
	}
	
	public String CH341GetDevName()
	{
		if(!isConnected())
			return null;
		return this.DevName;
	}
	
	public String CH341GetDescription()
	{
		if(!isConnected())
			return null;
		return this.Description;
	}
	
	public String CH341GetLibraryVendor()
	{
		if(!isConnected())
			return null;
		
		return this.Library;
	}
	
	public int CH341TransPackageSize() {
		return this.mBulkPacketSize;
	}
	
	public int CH341GetReadTimeOutValue() {
		return this.ReadTimeOutMillis;
	}
	
	public int CH341GetWriteTimeOutValue() {
		return this.WriteTimeOutMillis;
	}
	
	public boolean CH341SetTimeOut(int WriteTimeOut, int ReadTimeOut)
	{
		WriteTimeOutMillis = WriteTimeOut;
		ReadTimeOutMillis = ReadTimeOut;
		return true;
	}
	

	public synchronized void OpenUsbDevice(UsbDevice mDevice)
	{
		Object localObject;
		UsbInterface intf;
		if(mDevice == null)
			return;
		intf = getUsbInterface(mDevice);
		if((mDevice != null) && (intf != null)) {
			localObject = this.mUsbmanager.openDevice(mDevice);
			if(localObject != null) {
				if(((UsbDeviceConnection)localObject).claimInterface(intf, true)) {
					this.mUsbDevice = mDevice;
					this.mDeviceConnection = ((UsbDeviceConnection)localObject);
					this.mInterface = intf;
					if(!enumerateEndPoint(intf))
						return;
					//Toast.makeText(mContext, "Device Has Attached to Android", Toast.LENGTH_LONG).show();
					if(READ_ENABLE == false){
						READ_ENABLE = true;
//						readThread = new read_thread(mBulkInPoint, mDeviceConnection);
//						readThread.start();
					}
					return;
				}
			}
		}
		
	}
	
	public synchronized void CloseDevice()
	{
		try{Thread.sleep(10);}
		catch(Exception e){}
		
		if(this.mDeviceConnection != null)
		{
			if(this.mInterface != null) {
				this.mDeviceConnection.releaseInterface(this.mInterface);
				this.mInterface = null;
			}
			
			this.mDeviceConnection.close();
		}
		
		if(this.mUsbDevice != null) {
			this.mUsbDevice = null;
		}
		
		if(this.mUsbmanager != null) {
			this.mUsbmanager = null;
		}
		
		
		if(READ_ENABLE == true) {
			READ_ENABLE = false;
		}
		
		/*
		 * No need unregisterReceiver
		 */
		if(BroadcastFlag == true) {
			this.mContext.unregisterReceiver(mUsbReceiver);
			BroadcastFlag = false;
		}
		
//		System.exit(0);
	}
	
	public boolean UsbFeatureSupported()
	{
		boolean bool = this.mContext.getPackageManager().hasSystemFeature("android.hardware.usb.host");
		return bool;
	}
	
	public int ResumeUsbList()
	{
		mUsbmanager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
		mPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(mString), 0);
		HashMap<String, UsbDevice> deviceList = mUsbmanager.getDeviceList();
		if(deviceList.isEmpty()) {
			Toast.makeText(mContext, "No Device Or Device Not Match", Toast.LENGTH_LONG).show();
			return 2;
		}
		Iterator<UsbDevice> localIterator = deviceList.values().iterator();
		while(localIterator.hasNext()) {
			UsbDevice localUsbDevice = localIterator.next();
			for(int i = 0; i < DeviceCount; i++) {
				if(String.format("%04x:%04x", localUsbDevice.getVendorId(),
						localUsbDevice.getProductId()).equalsIgnoreCase(DeviceNum.get(i))) {
					IntentFilter filter = new IntentFilter(mString);
					filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
					mContext.registerReceiver(mUsbReceiver, filter);
					BroadcastFlag = true;
					if(mUsbmanager.hasPermission(localUsbDevice)) {
						Log.d(TAG, "hasPermission");
						OpenUsbDevice(localUsbDevice);
					} else {
						synchronized(mUsbReceiver) {
							mUsbmanager.requestPermission(localUsbDevice, mPendingIntent);
						}
						return -1;
					}
				} else {
					Log.d(TAG, "String.format not match");
				}
			}
		}
		
		return 0;
	}
	
	public boolean isConnected()
	{
		return (this.mUsbDevice != null) && (this.mInterface != null) && (this.mDeviceConnection != null);
	}
	
	protected UsbDevice getUsbDevice()
	{
		return this.mUsbDevice;
	}
	
	public boolean CH341GetInput(
			int[] iStatus )

	{
		char[] mBuffer = new char[CH341_PACKET_LENGTH];
		char[] outBuf = new char[CH341_PACKET_LENGTH];
		long mLength, mLen = 0;
		char CH341A_CMD_GET_INPUT = 0xA0;
		if(ChipVersion < 0x20) {
			return false;
		}
		mBuffer[0] = CH341A_CMD_GET_INPUT;
		mBuffer[1] = (char)min(0x3F, CH341_PACKET_LENGTH);
		mBuffer[5] = 1;
		mLength = 1 + 8;
		if(CH34xWriteRead(mLength, mBuffer, outBuf) != 0) {
			iStatus[0] = (int)(outBuf[0] | ( (long)outBuf[1] & 0xEF ) << 8 | ( (long)outBuf[2] & 0x80 ) << ( 23 - 7 ));
			return true;
		}
		return false;
	}
	
	public boolean CH341SetOutput(

				long			iEnable,

				long			iSetDirOut,
				long			iSetDataOut )

	{
		char[] mBuffer = new char[CH341_PACKET_LENGTH];
		long mLength;
		int mReqValue, mReqIndex;
		if(ChipVersion < 0x20) {
			if((iEnable & 0x03) != 0) {
				if((iEnable & 0x03) == 0x03) {
					mReqValue = 0x1606;
					mReqIndex = (int)(iSetDataOut & 0xFF | iSetDirOut << 8 | 0xFF00);
				} else if((iEnable & 0x02) != 0) {
					mReqValue = 0x1616;
					mReqIndex = (int)(iSetDirOut & 0xFF | iSetDirOut << 8 | 0xFF00);
				} else {
					mReqValue = 0x0606;
					mReqIndex = (int)(iSetDataOut & 0xFF | iSetDataOut << 8 | 0xFF00);
				}
				if(MultiPort_Control_Out(MultiCmd.VENDOR_WRITE, mReqValue, mReqIndex) < 0) {
					return false;
				}
			}
			if((iEnable & 0x0C) != 0) {
				if((iEnable & 0x0C) == 0x0C) {
					mReqValue = 0x1505;
					mReqIndex = (int)(iSetDataOut >> 8 & 0xEF | iSetDirOut & 0xEF00 | 0x1000);
				} else if((iEnable & 0x08) != 0) {
					mReqValue = 0x1515;
					mReqIndex = (int)(iSetDirOut >> 8 & 0xEF | iSetDirOut & 0xEF00 | 0x1010);
				} else {
					mReqValue = 0x0505;
					mReqIndex = (int)(iSetDataOut >> 8 & 0xEF | iSetDataOut & 0xEF00);
				}
				if(MultiPort_Control_Out(MultiCmd.VENDOR_WRITE, mReqValue, mReqIndex) < 0) {
					return false;
				}
			}
			if((iEnable & 0x10) != 0) {
				mReqValue = 0x0707;
				mReqIndex = (int)(iSetDataOut >> 16 & 0x0F | iSetDataOut >> 8 & 0x0F00);
				if(MultiPort_Control_Out(MultiCmd.VENDOR_WRITE, mReqValue, mReqIndex) < 0) {
					return false;
				}
			}
			return true;
		} else { //CH341A
			mBuffer[0] = MultiCmd.CH341A_CMD_SET_OUTPUT;
			mBuffer[1] = 0x6A;
			mBuffer[2] = (char)( iEnable & 0x1F );
			mBuffer[3] = (char)( iSetDataOut >> 8 & 0xEF );
			mBuffer[4] = (char)( iSetDirOut >> 8 & 0xEF | 0x10 );
			mBuffer[5] = (char)( iSetDataOut & 0xFF );
			mBuffer[6] = (char)( iSetDirOut & 0xFF );
			mBuffer[7] = (char)( iSetDataOut >> 16 & 0x0F );
			mBuffer[8] = 0;
			mBuffer[9] = 0;
			mBuffer[10] = 0;
			mLength = 11;
			long retval = WriteData(mBuffer, mLength);
			if(retval >= 8) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public boolean CH341Set_D5_D0(
				long			iSetDirOut,
				long			iSetDataOut )

	{
		char[] mBuffer = new char[CH341_PACKET_LENGTH];
		int mLength;
		int mReqValue, mReqIndex;
		if(ChipVersion < 0x20) {
			mReqValue = 0x1606;
			mReqIndex = (int)(iSetDataOut & 0x3F | iSetDirOut << 8 & 0x3F00);
			if(MultiPort_Control_Out(MultiCmd.VENDOR_WRITE, mReqValue, mReqIndex) < 0) {
				return false;
			} else {
				return true;
			}
		} else {
			mBuffer[0] = MultiCmd.CH341A_CMD_UIO_STREAM;
			mBuffer[1] = (char)(MultiCmd.CH341A_CMD_UIO_STM_OUT | iSetDataOut & 0x3F);
			mBuffer[2] = (char)(MultiCmd.CH341A_CMD_UIO_STM_DIR | iSetDirOut & 0x3F);
			mBuffer[3] = MultiCmd.CH341A_CMD_UIO_STM_END;
			mLength = 4;
			long retval = WriteData(mBuffer, mLength);
			if(retval >= 3) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
     * Performs a control transaction on endpoint zero for this device.
     * The direction of the transfer is determined by the request type.
     * If requestType & {@link UsbConstants#USB_ENDPOINT_DIR_MASK} is
     * {@link UsbConstants#USB_DIR_OUT}, then the transfer is a write,
     * and if it is {@link UsbConstants#USB_DIR_IN}, then the transfer
     * is a read.
     *

     * public int controlTransfer(int requestType, int request, int value,
     *       int index, byte[] buffer, int length, int timeout)
     */
	
	public int MultiPort_Control_Out(int request, int value, int index)
	{
		int retval = 0;
		retval = mDeviceConnection.controlTransfer(UsbType.USB_TYPE_VENDOR | UsbType.USB_RECIP_DEVICE | UsbType.USB_DIR_OUT,
				request, value, index, null, 0, DEFAULT_TIMEOUT);
		
		return retval;
	}
	
	public int MultiPort_Control_In(int request, int value, int index, byte[] buffer, int length)
	{
		int retval = 0;
		retval = mDeviceConnection.controlTransfer(UsbType.USB_TYPE_VENDOR | UsbType.USB_RECIP_DEVICE | UsbType.USB_DIR_IN,
					request, value, index, buffer, length, DEFAULT_TIMEOUT);
		return retval;
	}
	
	public int CH341SystemInit()
	{
		Log.d(TAG,"CH341SystemInit");
		int retval;
		if(!isConnected())
			return -1;
		retval = MultiPortChipVersion();
		if(retval == MultiState.Multi_GETVERSION_ERROR) {
			return -MultiState.Multi_GETVERSION_ERROR;
		}
		
		return 0;
	}
	
	private int MultiPortChipVersion()
	{
		int mVersion = 0;
		int retval = 0;
		byte [] buffer = new byte[2];
		retval = MultiPort_Control_In(MultiCmd.VENDOR_VERSION, 0x0000, 0x0000, buffer, 2);
		if(retval < 0) {
			Log.d(TAG,"MultiPort_Control_In error");
			return MultiState.Multi_GETVERSION_ERROR;
		} else {
			Log.d(TAG,"MultiPort_Control_In success");
			char VersionLow = (char)buffer[0];
			char VersionHigh = (char)buffer[1];
			mVersion |= VersionLow;
			mVersion |= (int)(VersionHigh << 8);
			Log.d(TAG,"ChipVersion: "+ChipVersion);
			this.ChipVersion = mVersion;
			return 0;
		}
	}
	
	public int CH341GetChipVersion()
	{
		return this.ChipVersion;
	}
	
	/*
	 * ********************************************************************
	 * FUNCTION : Set Stream Mode
	 * arg:
	 * Mode : Set Stream Mode
	 * -> bit0~1 : set I2C SCL rate
	 * 			   --> 00 :	Low Rate /20KHz
	 * 			   --> 01 : Default Rate /100KHz
	 * 			   --> 10 : Fast Rate /400KHz
	 * 			   --> 11 : Full Rate /750KHz
	 * -> bit2 : set spi mode
	 * 			   --> 0 : one in one out(D3 :clk/ D5 :out/ D7 :in)
	 * 			   --> 1 : two in two out(D3 :clk/ D4,D5 :out/ D6,D7 :in)
	 * -> bit7 : set spi data mode
	 * 			   --> 0 : low bit first
	 *       	   --> 1 : high bit first
	 * other bits must keep 0
	 * ********************************************************************
	 */
	public boolean CH341SetStream( long Mode )
	{
		byte[] mBuffer = new byte[CH341_PACKET_LENGTH];
		int mLength;
		int retval;
		if(!isConnected())
			return false;
		if(ChipVersion < 0x20)
			return false;
		StreamMode = (char)( Mode & 0x8F );
		mBuffer[0] = (byte)MultiCmd.CH341A_CMD_I2C_STREAM;
		//mBuffer[1] = (byte)MultiCmd.CH341A_CMD_I2C_STM_SET;
		mBuffer[1] = 0x61;
		mBuffer[2] = (byte)MultiCmd.CH341A_CMD_I2C_STM_END;
		mLength = 3;
		retval = ParallelWriteData(mBuffer, mLength);
		if( retval >= 2 )
			return true;
		
		return false;
	}
	
	/*
	 * ********************************************************************
	 * FUNCTION : Read EEPROM Data (For I2C)
	 * arg:
	 * id 		: EEPROM TYPE
	 * iAddr 	: the start addr for read
	 * iLen  	: should read the length of data
	 * oBuffer  : output buffer
	 * ********************************************************************
	 */
	public boolean CH341ReadEEPROM(int id, long iAddr, long iLen, byte[]oBuffer)
	{
		long mLen;
		int Readlen = 0;
		char [] mWrBuf = new char[4];
		if(id >= EEPROM_TYPE.ID_24C01 && id <= EEPROM_TYPE.ID_24C16) {
			while(iLen > 0)
			{
				mWrBuf[0] = (char)(0xA0 |(iAddr >> 7) & 0x0E);
				mWrBuf[1] = (char)iAddr;
				mLen = min(iLen, DEFAULT_BUFFER_LENGTH);
				char[] reabuf = new char[(int)mLen];
				if(!CH341StreamI2C(2, mWrBuf, mLen, reabuf)) {
					return false;
				}
				iAddr += mLen;
				iLen -= mLen;
				Readlen += mLen;
//				System.arraycopy(reabuf, 0, oBuffer, Readlen, (int)mLen);
				for(int i = 0; i < mLen; i++)
					oBuffer[i] = (byte)reabuf[i];
			}
		} else if(id >= EEPROM_TYPE.ID_24C32 && id <= EEPROM_TYPE.ID_24C4096) {
			while(iLen > 0)
			{
				mWrBuf[0] = (char)(0xA0 |(iAddr >> 15) & 0x0E);
				mWrBuf[1] = (char)(iAddr >> 8);
				mWrBuf[2] = (char)iAddr;
				mLen = min(iLen, DEFAULT_BUFFER_LENGTH);
				char[] reabuf = new char[(int)mLen];
				if(!CH341StreamI2C(3, mWrBuf, mLen, reabuf)) {
					return false;
				}
				iAddr += mLen;
				iLen -= mLen;
				Readlen += mLen;
//				System.arraycopy(reabuf, 0, oBuffer, Readlen, (int)mLen);
				for(int i = 0; i < mLen; i++)
					oBuffer[i] = (byte)reabuf[i];

			}
		} else {
			return false;
		}
		return true;
	}
	
	/*
	 * ********************************************************************
	 * FUNCTION : Write EEPROM Data (For I2C)
	 * arg:
	 * id 		: EEPROM TYPE
	 * iAddr 	: the start addr for read
	 * iLen  	: should write the length of data
	 * iBuffer  : Iutput buffer
	 * ********************************************************************
	 */
	public boolean CH341WriteEEPROM(int id, long iAddr, long iLen, char[]iBuffer)
	{
		long mLen;
		int WriteLen = 0;
		char [] mWrBuf = new char[256];
		if(id >= EEPROM_TYPE.ID_24C01 && id <= EEPROM_TYPE.ID_24C16) {
			while(iLen > 0)
			{
				mWrBuf[0] = (char)(0xA0 |(iAddr >> 7) & 0x0E);
				mWrBuf[1] = (char)iAddr;
				mLen = id >= EEPROM_TYPE.ID_24C04 ? 16 - ( iAddr & 15 ) : 8 - (iAddr & 7 );
				if(mLen > iLen) {
					mLen = iLen;
				}
//				System.arraycopy(iBuffer, WriteLen, mWrBuf, 2, (int)mLen);
				for(int i = 0; i < mLen; i++)
					mWrBuf[2+WriteLen+i] = iBuffer[i+WriteLen];
				if(!CH341StreamI2C(2 + mLen, mWrBuf, 0, null)) {
					return false;
				}
				iAddr += mLen;
				iLen -= mLen;
				WriteLen += mLen;
			}
		} else if(id >= EEPROM_TYPE.ID_24C32 && id <= EEPROM_TYPE.ID_24C4096) {
			while(iLen > 0)
			{
				mWrBuf[0] = (char)(0xA0 | ( iAddr >>15 ) & 0x0E);
				mWrBuf[1] = (char)(iAddr >> 8);
				mWrBuf[2] = (char)iAddr;
				mLen = id >= EEPROM_TYPE.ID_24C512 ? 128 - ( iAddr & 127) : (id >= EEPROM_TYPE.ID_24C128 ? 64 - ( iAddr & 63 ) : 32 - ( iAddr & 31));
				if(mLen > iLen) {
					mLen = iLen;
				}
//				System.arraycopy(iBuffer, WriteLen, mWrBuf, 3, (int)mLen);
				for(int i = 0; i < mLen; i++)
					mWrBuf[3+WriteLen+i] = iBuffer[i+WriteLen];
				
				if(!CH341StreamI2C(3 + mLen, mWrBuf, 0, null)) {
					return false;
				}
				iAddr += mLen;
				iLen -= mLen;
				WriteLen += mLen;
			
			}
		} else {
			return false;
		}
		return true;
	}
	
	/*
	 * ********************************************************************
	 * FUNCTION : Write/Read I2C Data Stream
	 * This function issue a set of packets of iWriteBuffer data
	 * arg:
	 * WriteLen : should write the length of data
	 * WriteBuffer : input buffer
	 * ReadLen  : should read the length of data
	 * ReadBuffer  : output buffer
	 * ********************************************************************
	 */
	public boolean CH341StreamI2C(long WriteLen, char[]WriteBuffer, long ReadLen, char[]ReadBuffer)
	{
		char[] mBuf = new char[this.MAX_BUFFER_LENGTH];
		char[] mWrBuf = new char[this.MAX_BUFFER_LENGTH];
		long i, j, mLen;
		/*if((char)this.ChipVersion < 0x20) {
			Log.d(TAG,"ChipVersion error: "+ChipVersion);
			return false;
		}*/
		mLen = max(WriteLen, ReadLen);
		if(mLen > MAX_BUFFER_LENGTH){
			Log.d(TAG,"mLen error");
			return false;
		}
		i = 0;
		mWrBuf[(int)i++] = MultiCmd.CH341A_CMD_I2C_STREAM;
		if((this.StreamMode & 0x03) == 0) {
			mWrBuf[(int)i++] = MultiCmd.CH341A_CMD_I2C_STM_US | 10;
			mWrBuf[(int)i++] = MultiCmd.CH341A_CMD_I2C_STM_US | 10;
		}
		mWrBuf[(int)i++] = MultiCmd.CH341A_CMD_I2C_STM_STA;
		if(WriteLen > 0)
		{
			for(j = 0; j < WriteLen;)
			{
				mLen = MultiPortManager.CH341_PACKET_LENGTH - i % MultiPortManager.CH341_PACKET_LENGTH;
				if(mLen <= 2)
				{
					while(mLen-- > 0) {
						mWrBuf[(int)i++] = MultiCmd.CH341A_CMD_I2C_STM_END;
					}
					mLen = MultiPortManager.CH341_PACKET_LENGTH;
				}
				if(mLen >= CH341_PACKET_LENGTH)
				{
					mWrBuf[(int)i++] = MultiCmd.CH341A_CMD_I2C_STREAM;
					mLen = CH341_PACKET_LENGTH - 1;
				}
				mLen--;
				mLen--;
				if(mLen > WriteLen - j) {
					mLen = WriteLen - j;
				}
				mWrBuf[(int)i++] = (char)(MultiCmd.CH341A_CMD_I2C_STM_OUT | mLen);
//				System.arraycopy(WriteBuffer, (int)j, mWrBuf, (int)i, (int)mLen);
				for(int a = 0; a < mLen; a++) {
					mWrBuf[(int)i++] = WriteBuffer[(int)j++];
				}
			}
		}
		if(ReadLen > 0) {
			mLen = MultiPortManager.CH341_PACKET_LENGTH - i % MultiPortManager.CH341_PACKET_LENGTH;
			if(mLen < 3) {
				while(mLen-- > 0) {
					mWrBuf[(int)i++] = MultiCmd.CH341A_CMD_I2C_STM_END;
				}
				mLen = MultiPortManager.CH341_PACKET_LENGTH;
			}
			if(mLen > CH341_PACKET_LENGTH)
				mWrBuf[(int)i++] = MultiCmd.CH341A_CMD_I2C_STREAM;
			if(WriteLen > 1) {
				mWrBuf[(int)i++] = MultiCmd.CH341A_CMD_I2C_STM_STA;
				mWrBuf[(int)i++] = (char)(MultiCmd.CH341A_CMD_I2C_STM_OUT | 1);
				mWrBuf[(int)i++] = (char)(WriteBuffer[0] | 0x01);
			} else if(WriteLen > 0) {
				i--;
				mWrBuf[(int)i++] = (char)(WriteBuffer[0] | 0x01);
			}
			for(j = 1; j < ReadLen;) {
				mLen = CH341_PACKET_LENGTH - i % CH341_PACKET_LENGTH;
				if(mLen <= 1) {
					if(mLen > 0)
						mWrBuf[(int)i++] = MultiCmd.CH341A_CMD_I2C_STM_END;
					mLen = CH341_PACKET_LENGTH;
				}
				if(mLen >= CH341_PACKET_LENGTH)
					mWrBuf[(int)i++] = MultiCmd.CH341A_CMD_I2C_STREAM;
				
				mLen = ReadLen - j >= CH341_PACKET_LENGTH ? CH341_PACKET_LENGTH : ReadLen - j;
				mWrBuf[(int)i++] = (char)(MultiCmd.CH341A_CMD_I2C_STM_IN | mLen);
				j += mLen;
				if(mLen >= CH341_PACKET_LENGTH) {
					mWrBuf[(int)i++] = MultiCmd.CH341A_CMD_I2C_STM_END;
					i += CH341_PACKET_LENGTH - i % CH341_PACKET_LENGTH;
				}
			}
			mLen = CH341_PACKET_LENGTH - i % CH341_PACKET_LENGTH;
			if(mLen <= 1) {
				if(mLen > 0)
					mWrBuf[(int)i++] = MultiCmd.CH341A_CMD_I2C_STM_END;
				mLen = CH341_PACKET_LENGTH;
			}
			if(mLen >= CH341_PACKET_LENGTH)
				mWrBuf[(int)i++] = MultiCmd.CH341A_CMD_I2C_STREAM;
			mWrBuf[(int)i++] = MultiCmd.CH341A_CMD_I2C_STM_IN;			
		}
		mLen = CH341_PACKET_LENGTH - i % CH341_PACKET_LENGTH;
		if(mLen <= 1) {
			if(mLen > 0)
				mWrBuf[(int)i++] = MultiCmd.CH341A_CMD_I2C_STM_END;
			mLen = CH341_PACKET_LENGTH;
		}
		if(mLen >= CH341_PACKET_LENGTH)
			mWrBuf[(int)i++] = MultiCmd.CH341A_CMD_I2C_STREAM;
		mWrBuf[(int)i++] = MultiCmd.CH341A_CMD_I2C_STM_STO;
		mWrBuf[(int)i++] = MultiCmd.CH341A_CMD_I2C_STM_END;
		mLen = 0;
		if(ReadLen > 0)
		{
			mWrBuf[(int)i] = CH341_PACKET_LENGTH;
			mWrBuf[(int)(i+4)] = (char)((ReadLen + CH341_PACKET_LENGTH -1) / CH341_PACKET_LENGTH);
			i = i + 8;
		}
		if(ReadLen > 0)
		{
			j = CH34xWriteRead(i, mWrBuf, ReadBuffer,ReadLen);
			/*if(j != ReadLen) {
				Log.d(TAG, "Return length is not equal to input length");
				return false;
			}*/
			if(j <0) {
				Log.d(TAG, "Return length is not equal to input length");
				return false;
			}
		} else {
			/*
			 * ********************************************************************
			 * FUNCTION : Write Data ( for i2c/flash )
			 * arg:
			 * iBuffer : should Input  data buffer
			 * ioLength : write length of data
			 * ********************************************************************
			 */
			j = WriteData(mWrBuf, i);
			if(j < 0) {
				Log.d(TAG,"WriteData error");
				return false;
			}
		}
		return true;
	}
	////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////2020/03/20/////////////////////////////////////////////////////
	///////////////////////////////////////////  //////////  ///////////////////////////////////////\
	///////////////////////////////////////////////////////////////////////////////////////////////
	boolean flag=false;
	Result result;

	boolean needReadFlag=false;
	int needRead=0;
	public interface Result{
		void onEnableButton();
		void onSuccess(byte[] data);
		void onError(String msg);
	}
	
	public void setResultListener(Result result){
		this.result=result;
	}
	
	public void startIICThread(){
		flag=true;
		new testThread().start();
	}
	
	public void cancelListener(){
		this.result=null;
	}
	
	public void stopIICThread(){
		needReadFlag=false;
		flag=false;
	}
	
	class testThread extends Thread{
		
		@Override
		public void run() {

			// TODO Auto-generated method stub
			while(flag){
				if(needReadFlag){
					byte[] data=readsomeData(needRead);
					needReadFlag=false;
					if(result!=null ){
						if(data.length==needRead){
							result.onSuccess(data);
						}else{
							result.onError("error");
						}
					}
					needRead=0;
				}
				
			}

		}
		
	}
	
	byte[] readsomeData(int count){

		byte[] buffer=new byte[count];
		byte[] temp = new byte[CH341_EPP_IO_MAX+1];
		int offset=0;
		int mLen=0;
		while(offset<count ){
			Arrays.fill(temp, (byte) 0x00);
			mLen = mDeviceConnection.bulkTransfer(mBulkInPoint, temp, CH341_EPP_IO_MAX+1, 10000);
			if(mLen<=0){
				continue;
				//return null;
			}else{
				System.arraycopy(temp, 0, buffer, offset, mLen);
				offset+=mLen;

			}
		}

		return buffer;
	}
	
	/*
	 * ********************************************************************
	 * FUNCTION : Write/Read Data ( for i2c/flash )
	 * arg:
	 * length : should write the length of data
	 * WriteBuf : input buffer
	 * ReadBuf  : output buffer
	 * return 	: the number of read.
	 * ********************************************************************
	 */
	private long CH34xWriteRead(long length, char[]WriteBuf, char[]ReadBuf,long wishRead)
	{	
		Log.d(TAG,"CH34xWriteRead "+"start"+" length-->"+length);
		long mLen = 0;
		int i;
		long totallen = 0;
		long mReadLen;
		long ReadStep, ReadTimes;
		byte[] oBuf = new byte[MAX_BUFFER_LENGTH];
		if(length < 8 || length > MAX_BUFFER_LENGTH) {
			Log.d(TAG, "The Length Input Error");
			return -MultiState.Multi_FRAME_ERROR;
		}
		length -= 8;
		ReadStep = WriteBuf[(int)length];
		ReadTimes = (WriteBuf[(int)(length + 4)] & 0xff);
		Log.d(TAG,"CH34xWriteRead caculate times"+" readTimes-->"+ReadTimes);
		mReadLen = ReadStep * ReadTimes;
		if(mReadLen == 0)
			return -MultiState.Multi_RECV_ERROR;
		/*
		 * First Write, then Read.
		 */
		if(result!=null){
			result.onEnableButton();
		}
		needRead=(int) wishRead;
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		needReadFlag=true;
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG,"CH34xWriteRead write"+"start");
		mLen = WriteData(WriteBuf, length);
		Log.d(TAG,"CH34xWriteRead write result "+mLen);
		if(mLen < 0) {
			return -MultiState.Multi_SEND_ERROR;
		}
		//mLen = 0;
		//Log.d(TAG,"CH34xWriteRead read"+"start"+" readTimes-->"+ReadTimes);
		/*for(i = 0; i < ReadTimes; i++) {
			synchronized (ReadQueueLock) {
				byte[] temp = new byte[CH341_EPP_IO_MAX+1];
				mLen = this.mDeviceConnection.bulkTransfer(this.mBulkInPoint, temp, CH341_EPP_IO_MAX+1, this.ReadTimeOutMillis);
				Log.d(TAG,"CH34xWriteRead bulkTransfer count: "+mLen);
				if(mLen >= 0) {
					totallen += mLen;
					Log.d(TAG,"CH34xWriteRead bulkTransfer totallen: "+totallen);
					System.arraycopy(temp, 0, oBuf, i * (CH341_EPP_IO_MAX+1), (CH341_EPP_IO_MAX+1));
				} else {
					Log.d(TAG, "ReadQueueLock");
				}
			}
		}
		for (i = 0; i < totallen; i++) {
			ReadBuf[i] = (char)oBuf[i];
		}
*/
		return mLen;
	}

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

	private long CH34xWriteRead(long length, char[]WriteBuf, char[]ReadBuf)
	{
		Log.d(TAG,"CH34xWriteRead "+"start"+" length-->"+length);

		long mLen = 0;
		int i;
		long totallen = 0;
		long mReadLen;
		long ReadStep, ReadTimes;
		byte[] oBuf = new byte[MAX_BUFFER_LENGTH];
		if(length < 8 || length > MAX_BUFFER_LENGTH) {
			Log.d(TAG, "The Length Input Error");
			return -MultiState.Multi_FRAME_ERROR;
		}
		length -= 8;
		ReadStep = WriteBuf[(int)length];
		ReadTimes = (WriteBuf[(int)(length + 4)] & 0xff);
		Log.d(TAG,"CH34xWriteRead caculate times"+" readTimes-->"+ReadTimes);
		mReadLen = ReadStep * ReadTimes;
		if(mReadLen == 0)
			return -MultiState.Multi_RECV_ERROR;
		/*
		 * First Write, then Read.
		 */
		//new testThread((int)wishRead).start();
		Log.d(TAG,"CH34xWriteRead write"+"start");
		mLen = WriteData(WriteBuf, length);
		Log.d(TAG,"CH34xWriteRead write result"+mLen);
		if(mLen < 0) {
			return -MultiState.Multi_SEND_ERROR;
		}
		mLen = 0;
		Log.d(TAG,"CH34xWriteRead read"+"start"+" readTimes-->"+ReadTimes);
		for(i = 0; i < ReadTimes; i++) {
			synchronized (ReadQueueLock) {
				byte[] temp = new byte[CH341_EPP_IO_MAX+1];
				mLen = this.mDeviceConnection.bulkTransfer(this.mBulkInPoint, temp, CH341_EPP_IO_MAX+1, this.ReadTimeOutMillis);
				Log.d(TAG,"CH34xWriteRead bulkTransfer count: "+mLen);
				if(mLen >= 0) {
					totallen += mLen;
					Log.d(TAG,"CH34xWriteRead bulkTransfer totallen: "+totallen);
					System.arraycopy(temp, 0, oBuf, i * (CH341_EPP_IO_MAX+1), (CH341_EPP_IO_MAX+1));
				} else {
					Log.d(TAG, "ReadQueueLock");
				}
			}
		}
		for (i = 0; i < totallen; i++) {
			ReadBuf[i] = (char)oBuf[i];
		}

		return totallen;
	}


	private boolean CH34xWriteSPI(long length, char[]WriteBuf)
	{
		Log.d(TAG,"CH34xWriteRead "+"start"+" length-->"+length);

		long mLen = 0;
		int i;
		long totallen = 0;
		long mReadLen;
		long ReadStep, ReadTimes;
		byte[] oBuf = new byte[MAX_BUFFER_LENGTH];
		if(length < 8 || length > MAX_BUFFER_LENGTH) {
			Log.d(TAG, "The Length Input Error");
			return false;
		}
		length -= 8;
		ReadStep = WriteBuf[(int)length];
		ReadTimes = (WriteBuf[(int)(length + 4)] & 0xff);
		Log.d(TAG,"CH34xWriteRead caculate times"+" readTimes-->"+ReadTimes);
		mReadLen = ReadStep * ReadTimes;
		if(mReadLen == 0)
			return false;
		/*
		 * First Write, then Read.
		 */
		//new testThread((int)wishRead).start();
		Log.d(TAG,"CH34xWriteRead write"+"start");
		mLen = WriteData(WriteBuf, length);
		Log.d(TAG,"CH34xWriteRead write result"+mLen);
		if(mLen < 0) {
			return false;
		}

		return mLen==length;
	}
	
	
	public void SetDeviceType(int mode)
	{
		switch(mode) {
		case 0:
			ReadByte = 0x03;		Sector_Erase = 0x20;
			Block_Erase = 0x52;		Chip_Erase = 0x60;
			Byte_Program = 0x02;	AAI = 0xAF;
			RDSR = 0x05;			EWSR = 0x50;
			WRSR = 0x01;			WREN = 0x06;
			WRDI = 0x04;			ReadID = 0x90;
			break;
		case 1:
			break;
		}
	}
	
	public int SelectDevType(String iic_devname)
	{
		int iic_Selection;
		if(iic_devname.equals("24C01"))
			iic_Selection = 0;
		else if(iic_devname.equals("24C02"))
			iic_Selection = 1;
		else if(iic_devname.equals("24C04"))
			iic_Selection = 2;
		else if(iic_devname.equals("24C08"))
			iic_Selection = 3;
		else if(iic_devname.equals("24C16"))
			iic_Selection = 4;
		else if(iic_devname.equals("24C32"))
			iic_Selection = 5;
		else if(iic_devname.equals("24C64"))
			iic_Selection = 6;
		else if(iic_devname.equals("24C128"))
			iic_Selection = 7;
		else if(iic_devname.equals("24C256"))
			iic_Selection = 8;
		else if(iic_devname.equals("24C512"))
			iic_Selection = 9;
		else if(iic_devname.equals("24C1024"))
			iic_Selection = 10;
		else if(iic_devname.equals("24C2048"))
			iic_Selection = 11;
		else if(iic_devname.equals("24C4096"))
			iic_Selection = 12;
		else
			iic_Selection = 0;
		
		return iic_Selection;
	}
	
	public boolean Ch34xWriteEnable()
	{
		long mLen;
		byte mWrBuf[] = new byte[2];
		mLen = 2;
		mWrBuf[0] = (byte)WREN;
		if(CH341StreamSPI4(iChipselect, mLen, mWrBuf) == false)
			return false;
		else
			return true;
	}
	
	public boolean Ch34xSectorErase(long StartAddr)
	{
		long mLen;
		byte mWrBuf[] = new byte[4];
		if(Ch34xWriteEnable() == false)
			return false;
		mWrBuf[0] = (byte)Sector_Erase;
		mWrBuf[1] = (byte)(StartAddr >> 16 & 0xff);
		mWrBuf[2] = (byte)(StartAddr >> 8 & 0xf0);
		mWrBuf[3] = 0x00;
		mLen = 4;
		if(CH341StreamSPI4(iChipselect, mLen, mWrBuf) == false)
			return false;
		if(Ch34xFalshWait() == false)
			return false;

		return true;
	}
	
	public boolean Ch34xFlashWrite(byte[]iBuffer, long iAddr, int iLength)
	{
		int i;
		byte mWrBuf[] = new byte[5];
		long mLen = 5;
		for(i = 0; i < iLength; i++) {
			Log.d(TAG, "iBuffer[" + i + "] " + iBuffer[i]);
		}
		Log.d(TAG, "iAddr is " + iAddr);
		for(i = 0; i < iLength; i++)
		{
			if(!Ch34xWriteEnable())
				return false;
			mWrBuf[0] = (byte)Byte_Program;
			mWrBuf[1] = (byte)(iAddr >> 16 & 0xff);
			mWrBuf[2] = (byte)(iAddr >> 8 & 0xff);
			mWrBuf[3] = (byte)(iAddr & 0xff);
			iAddr++;
			mWrBuf[4] = iBuffer[i];
			if(CH341StreamSPI4(iChipselect, mLen, mWrBuf) == false)
				return false;
			/*******************/
			for(int j = 0; j < 5; j++)
				mWrBuf[j] = 0x00;
			if(!Ch34xFalshWait())
				return false;
		}
		return true;
	}
	
	public boolean Ch34xFalshWait()
	{
		long mLen;
		char status;
		byte mWrBuf[] = new byte[3];
		int temp;
		mLen = 3;
		do {
			mWrBuf[0] = (byte)RDSR;
			if(CH341StreamSPI4(iChipselect, mLen, mWrBuf) == false)
				return false;
			status = (char)((mWrBuf[1] < 0)?((int)mWrBuf[1] + 256) : mWrBuf[1]);;
			temp = status & (char)0x01;
		}while(temp != 0);
		return true;
	}
	
	private byte[] ToByte(char[] src)
	{
		int strlength = src.length;
		int mLen = (strlength % 2 == 1) ? (strlength + 1):(strlength);
		char[] temp = new char[strlength];
		byte[] Bytes = new byte[strlength];
		int i, j = 0;
		
		for(i = 0; i < strlength; i++)
		{
			if(src[i] >= '0' && src[i] <= '9')
				temp[i] = (char)(src[i] - '0');
			else if(src[i] >= 'a' && src[i] <= 'z')
				temp[i] = (char)(src[i] - 'a' + 10);
			else if(src[i] >= 'A' && src[i] <= 'Z')
				temp[i] = (char)(src[i] - 'A' + 10);
		}
		if(mLen == strlength) {
			for(i = 0; i < strlength; i++) 
			{
				Bytes[j++] = (byte)(temp[i] * 16 + temp[++i]);
			}
		} else {
			for(i = 0; i < strlength - 1; i++) 
			{
				Bytes[j++] = (byte)(temp[i] * 16 + temp[++i]);
			}
			Bytes[j] = (byte)(temp[i] * 16 + 0);
		}
		
		return Bytes;
	}
	

	public boolean Ch341WriteSpi(long iAddr, long iLength, byte[] writebyte)
	{

		boolean flag;
		int i;
		int Cur_state = 0;

		
		flag = CH34xFlashReadStatus();
		if(flag == false)
		{
			Log.e(TAG, "CH34xFlashReadStatus Error");
			return false;
		}

//		iBuffer = Ch34xFlashReadBlock(iAddr, 4096);
		flag = Ch34xSectorErase(iAddr);
		if(flag == false)
		{
			Log.e(TAG, "Ch34xSectorErase Error");
			return false;
		}
//		Cur_state = (int)(iAddr % 4096);
//		for(i = 0; i < ((iLength < ioBuffer.length)?iLength:(ioBuffer.length)); i++) {
//				iBuffer[Cur_state + i] = ioBuffer[i];
//		}
		flag = Ch34xFlashWrite(writebyte, iAddr, (int)iLength);
		if(flag == false)
			return false;
	
		return true;
	}
	
	public boolean CH34xFlashReadStatus()
	{
		long mLen;
		byte status;
		byte mWrBuf[] = new byte[3];
		mLen = 3;
		mWrBuf[0] = (byte)RDSR;
		if(CH341StreamSPI4(iChipselect, mLen, mWrBuf) == false)
			return false;
		
		status = mWrBuf[1];
		if((status & 0x0c) != 0x00)
		{
			mLen = 1;
			for(int j = 0; j < mLen; j++)
				mWrBuf[j] = 0x00;
			mWrBuf[0] = (byte)EWSR;
			if(CH341StreamSPI4(iChipselect, mLen, mWrBuf) == false)
				return false;
			
			mLen = 2;
			for(int j = 0; j < mLen; j++)
				mWrBuf[j] = 0x00;
			mWrBuf[0] = (byte)WRSR;
			mWrBuf[1] = (byte)(status & (~0x0c));
			if(CH341StreamSPI4(iChipselect, mLen, mWrBuf) == false)
				return false;
		}
		for(int j = 0; j < mLen; j++)
			mWrBuf[j] = 0x00;
		
		mWrBuf[0] = (byte)RDSR;
		if(CH341StreamSPI4(iChipselect, mLen, mWrBuf) == false)
			return false;
		
		status = mWrBuf[1];
		return true;
	}
	

	public String Ch341FlashReadByte(long iAddr, long iLength)
	{
		long mLen;
		byte oBuffer[] = new byte[4096];
		byte mWrbuf[] = new byte[4096];
		String mStr = new String();
		char temp;
		int i;
		mLen = iLength + 4;
		oBuffer[0] = (byte)ReadByte;
		oBuffer[1] = (byte)(iAddr >> 16 & 0xff);
		oBuffer[2] = (byte)(iAddr >> 8 & 0xf0);
		oBuffer[3] = (byte)(iAddr & 0xff);
		if( CH341StreamSPI4( iChipselect, mLen, oBuffer ) == false ) {
	    	return null;
	    } else {
	    	System.arraycopy(oBuffer, 4, mWrbuf, 0, (int)iLength);
	    	for(i = 0; i < iLength; i++) {
				temp = (char)((mWrbuf[i] < 0)?((int)mWrbuf[i] + 256) : mWrbuf[i]);
				mStr += Integer.toHexString(temp);
				mStr += " ";
			}
	    	return mStr;
	    }
	}
	
	public byte[] Ch34xFlashReadBlock(long iAddr, long iLength)
	{
		long mLen;
		int i;
		byte mWrBuf[] = new byte[1028];
		byte oBuffer[] = new byte [4096];
		mLen = 1024;
		iAddr = iAddr - iAddr % 4096;
		for(i = 0; i < iLength;)
	    {
	    	mWrBuf[0] = (byte)ReadByte;
	    	mWrBuf[1] = (byte)(iAddr >> 16 & 0xff);
	    	mWrBuf[2] = (byte)(iAddr >> 8 & 0xff);
	    	mWrBuf[3] = (byte)(iAddr & 0xff);
	    	if( CH341StreamSPI4( iChipselect, mLen + 4, mWrBuf ) == false ) {
	    		return null;
	    	}
	    	
	    	System.arraycopy(mWrBuf, 4, oBuffer, i, 1024);
	    	i = i + 1024;
	    	iAddr += 1024;
	    }
		return oBuffer;		
	}
	
	/*
	 * ********************************************************************
	 * FUNCTION : Write/Read Flash Data (For one in one out SPI)
	 * arg:
	 * chipselect 	: Flash cs enable
	 * iLength 		: the length of data
	 * ioBuffer  	: one in one out buffer
	 * ********************************************************************
	 */
	// Note : StreamMode must set first
	public boolean CH341StreamSPI4( char chipSelect, long iLength, byte[] ioBuffer )
	{
		boolean retval;
		if(ChipVersion >= 0x25 && ChipVersion < 0x30)
			return false;
		StreamMode = 0x80;
		if((StreamMode & 0x04) != 0)
		{
			if( CH341SetStream(StreamMode & 0xFB) == false )
				return false;
		}	
		retval = CH34xStreamSPIx(chipSelect, iLength, ioBuffer, 1);
		
		return retval;		
	}


	/*
	 * ********************************************************************
	 * FUNCTION : Write/Read Flash Data (For SPI)
	 * arg:
	 * iChipselect 	: Flash cs enable
	 * iLength 		: the length of data
	 * ioBuffer  	: one in one out buffer
	 * ioBuffer2  	: two in two out buffer
	 * ********************************************************************
	 */
	private boolean CH34xStreamSPIx(char ChipSel, long iLength, byte[] ioBuffer, int ioBuffer2)
	{
		char[] mBuffer = new char[DEFAULT_BUFFER_LENGTH * 5];
		char[] temp = new char[DEFAULT_BUFFER_LENGTH * 5];
		int i, j, mSelect, mCount;
		long mReturn, mLength;
		char c1, c2;
		mReturn = iLength;
		if(ChipVersion < 0x20) {
			Log.d(TAG, "Not Support This Device");
			return false;
		}
		if(iLength <= DEFAULT_BUFFER_LENGTH / 2) {
			i = DEFAULT_BUFFER_LENGTH;
		} else {
			i = MAX_BUFFER_LENGTH;
		}
		char[] mWrBuf = new char[i];
		i = 0;
		if((ChipSel & 0x80) != 0x00) {
			mWrBuf[i++] = MultiCmd.CH341A_CMD_UIO_STREAM;
			switch(ChipSel & 0x03) {
			case 0x00:		//DCK/D3->0, D0 ->0
				mSelect = 0x36;
				break;
			case 0x01:		//DCK/D3->0, D1 ->0
				mSelect = 0x35;
				break;
			case 0x02:		//DCK/D3->0, D2 ->0
				mSelect = 0x33;
				break;
			default:
				mSelect = 0x27;
				break;
			}
			mWrBuf[i++] = (char)(MultiCmd.CH341A_CMD_UIO_STM_OUT | mSelect); //Output Data
			mWrBuf[i++] = (char)(MultiCmd.CH341A_CMD_UIO_STM_DIR | 0x3f); //set D5~D0 direct
			mWrBuf[i++] = (char)MultiCmd.CH341A_CMD_UIO_STM_END;
			i = CH341_PACKET_LENGTH;
		}
		if(iLength > 0) {
			if(ioBuffer2 < 4) {
				for(j = 0; j < iLength;) 
				{
					mLength = CH341_PACKET_LENGTH - 1;
					if(mLength > iLength -j)
						mLength = iLength - j;
					mWrBuf[i++] = MultiCmd.CH341A_CMD_SPI_STREAM;
					if((StreamMode & 0x81) == 0x80) {
						Log.d(TAG,"high bit convert");
						while(mLength-- > 0) {

							mWrBuf[i++] = mMsbTable[((ioBuffer[j] < 0)? ((ioBuffer[j] + 256)) : ioBuffer[j])];
							//Log.d(TAG, "this is " + (int)((ioBuffer[j] < 0)? ((ioBuffer[j] + 256)) : ioBuffer[j]));
							j++;
						}

					} else {
						Log.d(TAG,"no bit convert");
						while(mLength-- > 0)
							mWrBuf[i++] = (char)ioBuffer[j++];
					}
					if(ChipVersion == 0x20 && i % CH341_PACKET_LENGTH == 0) {
						mWrBuf[i] = mWrBuf[i+1] = 0;
						i += CH341_PACKET_LENGTH;
					}
				}
			} else {

				return false;
			}
		}
		
		mLength = 0;
		mWrBuf[i] = CH341_PACKET_LENGTH - 1;
		mWrBuf[i + 4] = (char)((iLength + CH341_PACKET_LENGTH - 1 - 1) /(CH341_PACKET_LENGTH - 1));
		i = i + 8;
		j = (int)CH34xWriteRead(i, mWrBuf, temp);

		if((ChipSel & 0x80) != 0x00)
		{
			mBuffer[0] = MultiCmd.CH341A_CMD_UIO_STREAM;
			mBuffer[1] = (char)(MultiCmd.CH341A_CMD_UIO_STM_OUT | 0x37);
			mBuffer[2] = MultiCmd.CH341A_CMD_UIO_STM_END;
			mLength = 3;
			
			mLength = WriteData(mBuffer, mLength);
			if(mLength < 2) {
				Log.d(TAG, "Error In Spi......");
				return false;
			}
		}
		if(j > 0) {
			if(ioBuffer2 < 4)
			{
				if((StreamMode & 0x80) != 0x00)
				{
					for(i = 0; i < iLength; i++) {
						ioBuffer[i] = (byte)mMsbTable[(((byte)temp[i] < 0)? ((int)((byte)temp[i] + 256)) : temp[i])];
					}
				}
			} else {

			}
			return true;
		} else {
			return false;
		}
	}






	/*
	 * EPP Read/Write
	 */

	public boolean MultiPortReadMode(int Mode)
	{
		switch(Mode) {
		case 0:
			READ_MODE = MultiCmd.CH34x_PARA_CMD_R0;
			break;
		case 1:
			READ_MODE = MultiCmd.CH34x_PARA_CMD_R1;
			break;
		default:
			READ_MODE = MultiCmd.CH34x_PARA_CMD_R0;
			break;
		}
		return true;
	}
	
	public boolean MultiPortWriteMode(int Mode)
	{
		switch (Mode) {
		case 0:
			WRITE_MODE = MultiCmd.CH34x_PARA_CMD_W0;
			break;
		case 1:
			WRITE_MODE = MultiCmd.CH34x_PARA_CMD_W1;
			break;
		default:
			WRITE_MODE = MultiCmd.CH34x_PARA_CMD_W0;
			break;
		}
		return true;
	}
	
	//Mode = 0 ------>EPP Mode
	//Mode = 1 ------>EPP Mode
	//Mode = 2 ------>MEM Mode
	/*
	 * ********************************************************************
	 * FUNCTION : Set Para Mode
	 * arg:
	 * Mode : set Para Mode( EPP or MEM )
	 * ********************************************************************
	 */
	public boolean SetParaMode(char mode)
	{
		int retval = 0;
		retval = MultiPort_Control_Out(MultiCmd.VENDOR_WRITE, 0x2525, ((int)(mode << 8) | mode));
		if(retval < 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/*
	 * Init Parallel Mode
	 */
	public boolean CH341InitParallel(char mode)
	{
		int retval;
		int request = MultiCmd.CH34x_PARA_INIT;
		int value = (int)(mode << 8) | (mode < 0x00000100 ? 0x02 : 0x00);
		int index = 0;
		retval = MultiPort_Control_Out(request, value, index);
		if(retval < 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean CH341InitEpp()
	{
		boolean retval;
		char mode;
		mode = 0x00;
		if(!isConnected())
			return false;
		retval = SetParaMode(mode);
		if(!retval) {
			Log.d(TAG, "Init Epp Error -->1");
			return false;
		}
		retval = CH341InitParallel(mode);
		if(!retval) {
			Log.d(TAG, "Init Epp Error -->2");
			return false;
		}
		
		return true;
	}
	
	/*
	 * ********************************************************************
	 * FUNCTION : EPP READ
	 * arg:
	 * Buffer : Output buffer
	 * length : should read the length of Data/Addr
	 * mode   : Read Pipe
	 * 		   	 -->0  Read Pipe0 Data
	 * 			 -->1  Read Pipe1 Addr
	 * ********************************************************************
	 */
	private int CH34xEppRead(byte[]Buffer, long length, int mode)
	{
		int mReturnLen = 0;

		MultiPortReadMode(mode);
	
		mReturnLen = ParallelRead(Buffer, length);
		
		return mReturnLen;
	}
	
	private int ParallelRead(byte[]Buf, long length)
	{
		long bytes_read, mNewlen;
		long bytes;
		int totallen = 0;
		int retval, i;
		byte[] mBuffer = new byte[4];
		byte[] temp = new byte[2];
		
		if(length == 0 || length > MAX_BUFFER_LENGTH)
		{
			return (int)length;
		}

		bytes_read = (long)((this.ChipVersion > 0x20) ? (0xff - (CH341_PACKET_LENGTH - 1)) : 0xff);

		mNewlen = length / bytes_read;
		temp[0] = mBuffer[0] = mBuffer[2] = (byte)READ_MODE;
		mBuffer[1] = (byte)bytes_read;
		mBuffer[3] = (byte)(length - mNewlen * bytes_read);
		
		if(mBuffer[3] != 0)
			mNewlen++;
		
		for(i = 0; i < mNewlen; i++)
		{
			if((i + 1) == mNewlen && mBuffer[3] != 0) {
				temp[1] = mBuffer[3];
				bytes = mBuffer[3];
			} else {
				temp[1] = mBuffer[1];
				bytes = bytes_read;
			}
			byte[] bulk_in = new byte[(int)bytes];
			retval = ParallelWriteData(temp, 2);
			if(retval < 0)
			{
				Log.d(TAG, "Usb bulk out error");
				return retval;
			}
			retval = this.mDeviceConnection.bulkTransfer(this.mBulkInPoint, bulk_in, (int)bytes, ReadTimeOutMillis);
			if(retval < 0) {
				Log.d(TAG, "Usb Bulk In Error");
				return totallen;
			} else {
				totallen += retval;
				for(int j = 0; j < totallen; j++) {
					Buf[j] = bulk_in[j];
				}
			}
		}
		return totallen;
	}
	
	//PipeMode = 1 ------>Read from Pipe1
	//PipeMode = 0 ------>Read from Pipe0
	/*
	 * ********************************************************************
	 * FUNCTION : EPP Write
	 * arg:
	 * iBuffer : Iutput buffer
	 * ioLength: should Write the length of Data/Addr
	 * PipeMode: Write Pipe
	 * 		   	 -->0  Read Pipe0 Data
	 * 			 -->1  Read Pipe1 Addr
	 * ********************************************************************
	 */
	private int CH34xEppWrite(byte[] Buffer, long length, int mode)
	{
		int mLen = 0;
		MultiPortWriteMode(mode);
		
		mLen = ParallelWrite(Buffer, length);
		
		return mLen;
	}
	
	private int ParallelWrite(byte[]Buf, long length)
	{
		int retval = 0;
		int total = 0;
		int i, mReturnLen = 0;
		long mLen, mNewlen;
		int write_size;
		if(length == 0 || length > MAX_BUFFER_LENGTH)
		{
			Log.d(TAG, "Data OverFlow");
			return (int)length;
		}
		
		mNewlen = length / CH341_EPP_IO_MAX;
		mLen = length - mNewlen * CH341_EPP_IO_MAX;
		mNewlen *= CH341_PACKET_LENGTH;
		byte[] tempBuf = new byte[1024];
		for(i = 0; i < mNewlen; i += CH341_PACKET_LENGTH)
		{
			tempBuf[i] = (byte)WRITE_MODE;
			System.arraycopy(Buf, mReturnLen, tempBuf, (int)(i + 1), CH341_EPP_IO_MAX);
			mReturnLen += CH341_EPP_IO_MAX;
		}
		if(mLen > 0) {
			Log.d(TAG, "i " + i);
			tempBuf[i] = (byte)WRITE_MODE;
			System.arraycopy(Buf, mReturnLen, tempBuf, (int)(i + 1), (int)mLen);
			mReturnLen += mLen;
			mNewlen += mLen + 1;
		}

		if(mNewlen > MAX_BUFFER_LENGTH) {
			write_size = MAX_BUFFER_LENGTH;
			byte[]writeBuf = new byte[MAX_BUFFER_LENGTH];
			System.arraycopy(tempBuf, 0, writeBuf, 0, write_size);
			retval = ParallelWriteData(tempBuf, write_size);
			if(retval < 0) {
				Log.d(TAG, "Usb Bulk Out Error---> 1");
				return retval;
			}
			total += retval;
			
			mLen = mNewlen - MAX_BUFFER_LENGTH;
		} else {
			mLen = mNewlen;
		}
		write_size = (int)mLen;
		byte[] lastBuf = new byte[write_size];
		System.arraycopy(tempBuf, (int)(mNewlen - mLen), lastBuf, 0, (int)mLen);
		retval = ParallelWriteData(lastBuf, write_size);
		if(retval < 0) {
			Log.d(TAG, "Usb Bulk Out Error---> 2");
			return retval;
		}
		total += retval;
		return total;
	}
	
	/*
	 * ********************************************************************
	 * FUNCTION : EPP Write Data
	 * arg:
	 * iBuffer : Output buffer
	 * ioLength: should Write the length of Data
	 * ********************************************************************
	 */
	public int CH341EppWriteData( byte[] iBuffer, long ioLength )
	{
		int retval;
		retval = CH34xEppWrite(iBuffer, ioLength, 0);
		if(retval < 0 || retval != ioLength) {
			Log.d(TAG, "CH34xEppWriteData retval -> " + retval);
			return -1;
		}
		return retval;
	}
	
	/*
	 * ********************************************************************
	 * FUNCTION : EPP Read Data
	 * arg:
	 * oBuffer : Iutput buffer
	 * ioLength: should read the length of Data
	 * ********************************************************************
	 */
	public int CH341EppReadData(byte[] oBuffer, long ioLength)
	{
		int retval;
		retval = CH34xEppRead( oBuffer, ioLength, 0 );	// set Pipe0,then Read Data from pipe0
		if(retval < 0 || retval != ioLength) {
			Log.d(TAG, "CH34xEppReadData retval -> " + retval);
			return -1;
		}
		return retval;
	}
	
	/*
	 * ********************************************************************
	 * FUNCTION : EPP Write Addr
	 * arg:
	 * iBuffer : Output buffer Data
	 * ioLength: should write the length of Addr
	 * ********************************************************************
	 */
	public int CH341EppWriteAddr(byte[] iBuffer, long ioLength)
	{
	        int retval;
	        retval = CH34xEppWrite(iBuffer, ioLength, 1); // set Pipe1,then Write Data from pipe1
	        if(retval < 0) {
				Log.d(TAG, "CH34xEppWriteAddr retval -> " + retval);
				return -1;
			}
			return retval;
	}
	
	/*
	 * ********************************************************************
	 * FUNCTION : EPP Read Addr
	 * arg:
	 * oBuffer : Iutput buffer Data
	 * ioLength: should read the length of Addr
	 * ********************************************************************
	 */
	public int CH341EppReadAddr(byte[] oBuffer, long ioLength)
	{
	        int retval;
	        retval = CH34xEppRead( oBuffer, ioLength, 1 ); // set Pipe1,then Read Data from pipe1
	        if(retval < 0 || retval != ioLength) {
				Log.d(TAG, "CH34xEppReadAddr retval -> " + retval);
				return -1;
			}
			return retval;
	}
	
	/*
	 * ********************************************************************
	 * FUNCTION : EPP Set Addr
	 * arg:
	 * iAddr: Need set the iAddr
	 * ********************************************************************
	 */
	public boolean CH34xEppSetAddr(long iAddr)
	{
		int retval;
		byte[] iBuffer = new byte[4];
		long mLength;
		iBuffer[0] = (byte)iAddr;
		mLength = 1;
		retval = CH341EppWriteAddr(iBuffer, mLength);
		if(retval == -1)
			return false;

		return true;
	}
	
	/*
	 * FUNCTION:	Init MEM
	 */
	public boolean CH341InitMem()
	{
		boolean retval;
		char mode;
		mode = 0x02;
		if(!isConnected())
			return false;
		retval = SetParaMode(mode);
		if(!retval) {
			Log.d(TAG, "Init MEM Error -->1");
			return false;
		}
		retval = CH341InitParallel(mode);
		if(!retval) {
			Log.d(TAG, "Init MEM Error -->2");
			return false;
		}
		
		return true;
	}
	
	/*
	 * ********************************************************************
	 * FUNCTION : MEM Read
	 * arg:
	 * oBuffer : Output buffer
	 * ioLength: should Read the length of Data
	 * PipeMode: Write Pipe
	 * 		   	 -->0  Read Pipe0 Data
	 * 			 -->1  Read Pipe1 Data
	 * ********************************************************************
	 */
	public int CH341MEMReadData(byte[] oBuffer, long ioLength, int PipeMode)
	{
		int retval = 0;
		MultiPortReadMode(PipeMode);
		
		retval = ParallelRead(oBuffer, ioLength);
		if(retval < 0)
			return -1;
		return retval;
	}
	
	/*
	 * ********************************************************************
	 * FUNCTION : MEM Write
	 * arg:
	 * iBuffer : Iutput buffer
	 * ioLength: should Write the length of Data
	 * PipeMode: Write Pipe
	 * 		   	 -->0  Read Pipe0 Data
	 * 			 -->1  Read Pipe1 Data
	 * ********************************************************************
	 */
	public int CH341MEMWriteData(byte[] iBuffer, long ioLength, int PipeMode)
	{
		int mLen;
		MultiPortWriteMode(PipeMode);
		
		mLen = ParallelWrite(iBuffer, ioLength);
		if(mLen < 0)
			return -1;
		
		return mLen;
	}
	
	private long max(long Len1, long Len2) {
		// TODO Auto-generated method stub
		if(Len1 >= Len2)
			return Len1;
		else
			return Len2;
	}
	
	private long min(long Len1, long Len2)
	{
		return (Len1 >= Len2 ? Len2 : Len1);
	}

	public int ReadData(byte[]data, int length)
	{
		int mLen;

		/*should be at least one byte to read*/
		if((length < 1) || (totalBytes == 0)){
			mLen = 0;
			return mLen;
		}

		/*check for max limit*/
		if(length > totalBytes)
			length = totalBytes;

		/*update the number of bytes available*/
		totalBytes -= length;

		mLen = length;	

		/*copy to the user buffer*/	
		for(int count = 0; count < length; count++)
		{
			data[count] = readBuffer[readIndex];
			readIndex++;
			/*shouldnt read more than what is there in the buffer,
			 * 	so no need to check the overflow
			 */
			readIndex %= maxnumbytes;
		}
		return mLen;
	}
	
	public int ReadData(byte[]data, int length, int timeoutMillis) throws IOException
	{
		int totalBytesRead;
		if(this.mBulkInPoint == null)
			return -1;
		synchronized(this.ReadQueueLock) {
			int readAmt = Math.min(length, this.mBulkPacketSize);
			totalBytesRead = this.mDeviceConnection.bulkTransfer(this.mBulkInPoint, data, readAmt, timeoutMillis);
		}
		
		if(totalBytesRead < 0) {
			throw new IOException("Expected Over 0 Byte");
		}
		
		return totalBytesRead;
	}
	

	private int ParallelWriteData(byte[] buf, int length)
	{
		int mLen = 0;
		mLen = mDeviceConnection.bulkTransfer(this.mBulkOutPoint, buf, length, this.WriteTimeOutMillis);
		if(mLen < 0)
			Log.d(TAG, "Write Data Error");
		return mLen;
	}
	
	public long WriteData2(char[] buf, long length)
	{
		Log.d(TAG, "Write Data start length: "+length);
		int mLen = (int)length;
		byte[] mWrbuf = new byte[mLen];
		for(int i = 0; i < length; i++) {
			mWrbuf[i] = (byte)buf[i];
		}
		Log.d(TAG, "Write Data start mWrbuf: "+mWrbuf.length);
		Log.d(TAG, "Write Data : "+ MyOperator.bytesToHexString(mWrbuf, mWrbuf.length));
		mLen = mDeviceConnection.bulkTransfer(this.mBulkOutPoint, mWrbuf, mLen, this.WriteTimeOutMillis);
		Log.d(TAG, "Write Data result: "+mLen);
		if(mLen < 0)
			Log.d(TAG, "Write Data Error");
		return mLen;	
	}
	
	
	
	public long WriteData(char[] buf, long length)
	{
		Log.d(TAG, "Write Data start length: "+length);
		int mLen = (int)length;
		byte[] mWrbuf = new byte[mLen];
		for(int i = 0; i < length; i++) {
			mWrbuf[i] = (byte)buf[i];
		}
		Log.d(TAG, "Write Data start mWrbuf: "+mWrbuf.length);
		Log.d(TAG, "Write Data mWrbuf : "+MyOperator.bytesToHexString(mWrbuf, mWrbuf.length));
		byte[] buffer=new byte[32];
		int count=mLen/32+1;
		int ret=0;
		int offet=0;
		for(int i=0;i<count;i++){
			Arrays.fill(buffer, (byte) 0);
			int length1=Math.min(32, mLen-i*32);
			System.arraycopy(mWrbuf, i*32, buffer, 0, length1);
			Log.d(TAG, "Write Data length: "+length1);
			Log.d(TAG, "Write Data : "+MyOperator.bytesToHexString(buffer, length1));
			ret=mDeviceConnection.bulkTransfer(this.mBulkOutPoint, buffer, length1, this.WriteTimeOutMillis);
			Log.d(TAG, "Write Data result: "+ret);
			if(ret < 0){
				Log.d(TAG, "Write Data Error");
			}
			offet+=ret;
		}
		if(offet!=length){
			return -1;
		}
		return offet;	
	}
	
	public int WriteData(byte[] buf, int length) throws IOException
	{
		int mLen = 0;
		mLen = WriteData(buf, length, this.WriteTimeOutMillis);
		if(mLen < 0)
		{
			throw new IOException("Expected Write Actual Bytes");
		}
		return mLen;	
	}
	
	public int WriteData(byte[] buf, int length, int timeoutMillis)
	{
		int offset = 0;
		int HasWritten = 0;
		int odd_len = length;
		if(this.mBulkOutPoint == null)
			return -1;
		while(offset < length)
		{
			synchronized(this.WriteQueueLock) {
				int mLen = Math.min(odd_len, this.mBulkPacketSize);
				byte[] arrayOfByte = new byte[4096];
				if(offset == 0) {
					System.arraycopy(buf, 0, arrayOfByte, 0, mLen);
				} else {
					System.arraycopy(buf, offset, arrayOfByte, 0, mLen);
				}
				HasWritten = this.mDeviceConnection.bulkTransfer(this.mBulkOutPoint, arrayOfByte, mLen, timeoutMillis);
				if(HasWritten < 0) {
					return -2;
				} else {
					offset += HasWritten;
					odd_len -= HasWritten;
				}
			}
		}
		return offset;
	}
	
	private boolean enumerateEndPoint(UsbInterface sInterface)
	{
		if(sInterface == null)
			return false;
		for(int i = 0; i < sInterface.getEndpointCount(); ++i) {
			UsbEndpoint endPoint = sInterface.getEndpoint(i);
			if(endPoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK && endPoint.getMaxPacketSize() == 0x20) {
				if(endPoint.getDirection() == UsbConstants.USB_DIR_IN) {
					mBulkInPoint = endPoint;
//					Log.d(TAG, "mBulkInPoint");
				} else {
					mBulkOutPoint = endPoint;
//					Log.d(TAG, "mBulkOutPoint");
				}
				this.mBulkPacketSize = endPoint.getMaxPacketSize();
			} else if(endPoint.getType() == UsbConstants.USB_ENDPOINT_XFER_CONTROL) {
					mCtrlPoint = endPoint;
			}
		}
		return true;
	}
	
	private UsbInterface getUsbInterface(UsbDevice paramUsbDevice) 
	{
		if(this.mDeviceConnection != null) {
			if(this.mInterface != null) {
				this.mDeviceConnection.releaseInterface(this.mInterface);
				this.mInterface = null;
			}
			this.mDeviceConnection.close();
			this.mUsbDevice = null;
			this.mInterface = null;
		}
		if(paramUsbDevice == null)
			return null;
		
		for (int i = 0; i < paramUsbDevice.getInterfaceCount(); i++) {
			UsbInterface intf = paramUsbDevice.getInterface(i);
			if (intf.getInterfaceClass() == 0xff
					&& intf.getInterfaceSubclass() == 0x01
					&& intf.getInterfaceProtocol() == 0x02) {
				return intf;
			}
		}
		return null;
	}
	
	/***********USB broadcast receiver*******************************************/
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			String action = intent.getAction();
			if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action))
				return;
			Log.d(TAG, "BroadcastReceiver");
			if(mString.equals(action))
			{
				synchronized(this) 
				{
					UsbDevice localUsbDevice = (UsbDevice)intent.getParcelableExtra("device");
					if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
					{
						//OpenUsbDevice(localUsbDevice);
					} else {
						Toast.makeText(MultiPortManager.this.mContext, "Deny USB Permission", Toast.LENGTH_SHORT).show();
						Log.d(TAG, "permission denied");
					}
				}
			} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				Toast.makeText(MultiPortManager.this.mContext, "Disconnect", Toast.LENGTH_SHORT).show();
				CloseDevice();
			} else {
				Log.d(TAG, "......");
			}
		}	
	};
	
	/*usb input data handler*/
	private class read_thread  extends Thread 
	{
		UsbEndpoint endpoint;
		UsbDeviceConnection mConn;

		read_thread(UsbEndpoint point, UsbDeviceConnection con){
			endpoint = point;
			mConn = con;
			this.setPriority(Thread.MAX_PRIORITY);
		}

		public void run()
		{		
			while(READ_ENABLE == true)
			{
				while(totalBytes > (maxnumbytes - 63))
				{
					try 
					{
						Thread.sleep(5);
					}
					catch (InterruptedException e) {e.printStackTrace();}
				}

				synchronized(ReadQueueLock)
				{
					if(endpoint != null)
					{	
						readcount = mConn.bulkTransfer(endpoint, usbdata, 64, ReadTimeOutMillis);
						if(readcount > 0)
						{
							for(int count = 0; count< readcount; count++)
							{					    			
								readBuffer[writeIndex] = usbdata[count];
								writeIndex++;
								writeIndex %= maxnumbytes;
							}

							if(writeIndex >= readIndex)
								totalBytes = writeIndex-readIndex;
							else
								totalBytes = (maxnumbytes-readIndex)+writeIndex;

						}
					}
				}
			}
		}
	}
	
	public final class UsbType {
		public static final int USB_TYPE_VENDOR = (0x02 << 5);
		public static final int USB_RECIP_DEVICE = 0x00;
		public static final int USB_DIR_OUT = 0x00;	/*to device*/
		public static final int USB_DIR_IN = 0x80;  /*to host*/
	}
	
	public final class MultiCmd {
		public static final int VENDOR_READ	= 0x95;	/*read two regs*/
		public static final int VENDOR_WRITE = 0x9A;	/*write two regs*/
		public static final int VENDOR_SERIAL_INIT = 0xA1;
		public static final int VENDOR_MODEM_OUT = 0xA4;
		public static final int VENDOR_VERSION = 0x5F;
		public static final int CH341A_CMD_SET_OUTPUT = 0xA1;
		public static final int CH34x_PARA_INIT = 0xB1;
		public static final int CH34x_PARA_CMD_R0 = 0xAC;	//read data0 from Para
		public static final int CH34x_PARA_CMD_R1 = 0xAD;	//read data1 from Para
		public static final int CH34x_PARA_CMD_W0 = 0xA6;	//write data0 to Para
		public static final int CH34x_PARA_CMD_W1 = 0xA7;	//write data1 to Para
		public static final int CH341A_CMD_I2C_STM_SET = 0x60;		//I2C Stream Set Mode
		public static final int CH341A_CMD_I2C_STM_STA = 0x74;		//I2C Stream Start Command
		public static final int CH341A_CMD_I2C_STM_STO = 0x75;		//I2C Stream Stop byte Command
		public static final int CH341A_CMD_I2C_STM_OUT = 0x80;		//I2C Stream Out Command
		public static final int CH341A_CMD_I2C_STM_IN = 0xC0;		//I2C Stream In Command
		public static final int CH341A_CMD_I2C_STREAM = 0xAA;		// I2C Interface Command
		public static final int CH341A_CMD_UIO_STREAM = 0xAB;		// UIO Interface Command
		public static final int CH341A_CMD_UIO_STM_IN = 0x00;	 // UIO Interface In ( D0 ~ D7 )
		public static final int CH341A_CMD_UIO_STM_DIR = 0x40;	 // UIO interface Dir( set dir of D0~D5 )
		public static final int CH341A_CMD_UIO_STM_OUT = 0x80;	 //	UIO Interface Output(D0~D5)
		public static final int CH341A_CMD_UIO_STM_US = 0xC0;	 // UIO Interface Delay Command( us )
		public static final int CH341A_CMD_UIO_STM_END = 0x20;	 // UIO Interface End Command
		public static final int CH341A_CMD_PIO_STREAM = 0xAE;		// PIO Interface Command
		public static final int CH341A_CMD_I2C_STM_US = 0x40;		//I2C Stream Delay(us)
		public static final int CH341A_CMD_I2C_STM_MS = 0x50;		//I2C Stream Delay(ms)
		public static final int CH341A_CMD_I2C_STM_DLY = 0x0F;		//I2C Stream Set Max Delay
		public static final int CH341A_CMD_I2C_STM_END = 0x00;		//I2C Stream End Command
		public static final int CH341A_CMD_SPI_STREAM = 0xA8;		//SPI Interface Command
	}
	
	public final class MultiState {
		public static final int Multi_STATE = 0x00;
		public static final int Multi_OVERRUN_ERROR = 0x01;
		public static final int Multi_GETVERSION_ERROR = 0x02;
		public static final int Multi_RECV_ERROR = 0x03;
		public static final int Multi_SEND_ERROR = 0x04;
		public static final int Multi_FRAME_ERROR = 0x06;
		public static final int Multi_STATE_TRANSIENT_MASK = 0x07;
	}
	
	public final class EEPROM_TYPE {
		public static final int ID_24C01 = 0;
		public static final int ID_24C02 = 1;
		public static final int ID_24C04 = 2;
		public static final int ID_24C08 = 3;
		public static final int ID_24C16 = 4;
		public static final int ID_24C32 = 5;
		public static final int ID_24C64 = 6;
		public static final int ID_24C128 = 7;
		public static final int ID_24C256 = 8;
		public static final int ID_24C512 = 9;
		public static final int ID_24C1024 = 10;
		public static final int ID_24C2048 = 11;
		public static final int ID_24C4096 = 12;
	}
}