package kr.neolab.sdk.pen.bluetooth.cmd;

import kr.neolab.sdk.pen.bluetooth.comm.CommProcessor;
import kr.neolab.sdk.util.NLog;

/**
 * 
 * @author CHY
 *
 */
public abstract class Command implements ICommand, Runnable 
{
    private boolean retry = true;
    
    protected boolean isAlive = true;
    
    private int id;
    
    protected CommProcessor comp;
    
    private static final int WAIT_TIME = 2000;
    
    public Command(int key, CommProcessor comp) 
    {
        this.id  = key;
        this.comp = comp;
    }
    
    protected abstract void write();

    @Override
    public int getId() 
    {
        return this.id;
    }
    
    @Override
    public void excute() 
    {
        Thread td = new Thread(this);
        td.setDaemon(true);
        td.setName( "Command-Thread-" + id );
        td.start();
    }
    
    @Override
    public void run() 
    {
        for ( int i=1; i<4; i++ ) 
        {
            if ( this.retry ) 
            {
                NLog.d("[Command] " + this.getClass().getCanonicalName() + " Try " + i + "time");
                
                write();
                
                try 
                {
                    Thread.sleep(WAIT_TIME);
                } 
                catch (Exception e) 
                {
                    e.printStackTrace();
                }
            } 
            else 
            {
                break;
            }
        }
        
        this.isAlive = false;
    }
    
    @Override
    public void finish() 
    {
        this.retry = false;
    }
    
    @Override
    public boolean isAlive() 
    {
        return isAlive;
    }
}
