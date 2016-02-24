package kr.neolab.sdk.pen.bluetooth.cmd;

import kr.neolab.sdk.pen.bluetooth.comm.CommProcessor;
import kr.neolab.sdk.pen.bluetooth.lib.ProtocolParser;
import kr.neolab.sdk.util.NLog;

/**
 * @author CHY
 *
 */
public class EstablishCommand extends Command 
{
    private boolean wait = true;
    private int count = 0;

    /**
     * @param key
     * @param comp
     */
    public EstablishCommand(int key, CommProcessor comp) 
    {
        super(key, comp);
    }

    public void run() 
    {
        NLog.d("[EstablishCommand] BEGIN Establish check Thread.");

        while ( wait ) 
        {
            count++;

            if ( comp.getConn().getIsEstablished() ) 
            {
                wait = false;
            }

            if ( wait && count > 7 ) 
            {
                NLog.e("[EstablishCommand] Connection can't established : " + comp.getConn().getMacAddress());
                
                wait = false;
                
                // init nack
                comp.write( ProtocolParser.buildPenOnOffData(false) );
                comp.getConn().unbind();
            }

            try 
            {
                Thread.sleep(1000);
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
        
        super.isAlive = false;
    }

    @Override
    protected void write() 
    {
        // TODO Auto-generated method stub
    }
}
