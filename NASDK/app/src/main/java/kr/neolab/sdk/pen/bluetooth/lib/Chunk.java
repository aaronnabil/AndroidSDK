/**
 * 
 */
package kr.neolab.sdk.pen.bluetooth.lib;

import java.io.IOException;
import java.io.InputStream;

/**
 * binary 파일을 Chunk로 분리
 * 
 * @author CHY
 *
 */
public class Chunk 
{
    private InputStream istream = null;
    
    // chunk size is 0.5k
    private int size = 512;
    private int rows;
    
    private byte[] rBuffer;
    private byte[][] tBuffer;
    private boolean[] status;
    
    public Chunk(InputStream is, long filesize) 
    {
        istream = is;
        rows = (int) Math.ceil(filesize / size) + 1;
        rBuffer = new byte[size];
        tBuffer = new byte[rows][size];
        status  = new boolean[rows];
    }
  
    public void load() 
    {
        int i = 0;
        
        while (true)
        {
            rBuffer = new byte[size];
            
            try 
            {
                int number = istream.read(rBuffer);
                if (number <= -1) 
                    break;
            } 
            catch (IOException e) 
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                break;
            }
            
            tBuffer[i++] = rBuffer;
        }
    }
    
    public byte[] getChunk(int number) 
    {
        return tBuffer.length > number ? tBuffer[number] : null;
    }
        
    public int getChunkLength() 
    {
        return rows;
    }
    
    public int getChunksize() 
    {
        return size;
    }
    
    public byte getChecksum(int number) 
    {
        return tBuffer.length > number ? calcChecksum(tBuffer[number]) : null;
    }
    
    public static byte calcChecksum(byte[] bytes) 
    {
        int CheckSum = 0;
        
        for( int i = 0; i < bytes.length; i++)
        {
             CheckSum += (int)(bytes[i] & 0xFF);
        }

        return (byte)CheckSum;
    }
    
    public int getStatus() 
    {
        int result = 0;
        
        for (boolean st : status) 
        {
            result += st ? 1 : 0;
        }
        
        return result;
    }
    
    public int getStatusPercent() 
    {         
        double status = (double)((double)getStatus() / (double)rows) * 100;

        return (int)status;
    }
    
    public void setStatus(int index, boolean status) 
    {
        this.status[index] = status;
    }
}
