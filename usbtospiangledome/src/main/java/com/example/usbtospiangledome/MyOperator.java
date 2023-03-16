package com.example.usbtospiangledome;

public class MyOperator {

	public byte[] ToByte(char[] src)
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
	
	public String tostringBuffer(byte[] readBuffer, int mLen)
	{
		String mStr = "";
		char temp;
		int i;
		
		for(i = 0; i < mLen; i++) {
			temp = (char)((readBuffer[i] < 0)?((int)readBuffer[i] + 256) : readBuffer[i]);
			mStr += Integer.toHexString(temp);
			mStr += " ";
		}
		
		return mStr;
	}
	
	public String tostringCharBuffer(char[] readBuffer, int mLen)
	{
		String mStr = "";
		int i;
		for(i = 0; i < mLen; i++) {
			mStr += Integer.toString((byte)readBuffer[i], 16);
			mStr += " ";
		}
		
		return mStr;
	}
	
}
