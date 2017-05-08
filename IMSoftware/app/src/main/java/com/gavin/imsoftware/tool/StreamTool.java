package com.gavin.imsoftware.tool;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
public class StreamTool {
	public static byte[] readStream(DataInputStream inStream) throws IOException{
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			//获取要从流中读取的数据长度       网络流数据格式   length+data
			int totalCount = inStream.readInt();
			byte[] buffer = new byte[totalCount];
			int len= -1;
			int readCount = 0;
			while(readCount < totalCount){
				len = inStream.read(buffer, 0, totalCount - readCount);
				readCount+= len;
				outStream.write(buffer, 0, len);
			}
			byte[] datas = outStream.toByteArray();
			outStream.close();
			return datas;
	}
}
