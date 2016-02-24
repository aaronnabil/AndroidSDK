package kr.neolab.sdk.pen.bluetooth.lib;

import java.util.Arrays;

/**
 * @author CHY
 *
 */
public class Packet 
{
	public int cmd;
	
	public int dataLength;
	
	public byte[] data;
	
    public int checkSum;
	
	public Packet() 
	{
	}
	
    public Packet(byte[] buffer) 
    {
        this.setValue(buffer);
    }
    
    private void setValue(byte[] buffer) 
    {
        this.cmd = ByteConverter.byteArrayToInt(new byte[]{ buffer[0] });
        this.dataLength = ByteConverter.byteArrayToInt(new byte[]{ buffer[1], buffer[2] }); 
        this.data = Packet.copyOfRange(buffer, 3, dataLength);
    }
    
    public int getCmd() 
    {
        return this.cmd;
    }
    
    public int getDataLength() 
    {
        return this.dataLength;
    }

    public int getDataRangeInt(int start, int size) 
    {
        byte[] range = Packet.copyOfRange(data, start, size);
        return ByteConverter.byteArrayToInt(range);
    }

    public short getDataRangeShort(int start, int size) 
    {
        byte[] range = Packet.copyOfRange(data, start, size);
        return ByteConverter.byteArrayToShort(range);
    }    
    
    public long getDataRangeLong(int start, int size) 
    {
        byte[] range = Packet.copyOfRange(data, start, size);
        return ByteConverter.byteArrayToLong(range);
    }
    
    public String getDataRangeString(int start, int size) 
    {
        byte[] range = Packet.copyOfRange(data, start, size);
        return new String(range);
    }   
    
    public byte[] getDataRange(int start, int size) 
    {
        return Packet.copyOfRange(data, start, size);
    }
    
    public static byte[] copyOfRange(byte[] buffer, int start, int size) 
    {
    	return Arrays.copyOfRange(buffer, start, start + size);
    }

    public byte[] getData() 
    {
        return this.data;
    }
}
