package kr.neolab.sdk.pen.bluetooth.cmd;

import kr.neolab.sdk.pen.bluetooth.comm.CommProcessor;
import kr.neolab.sdk.pen.bluetooth.lib.ProtocolParser;

/**
 * @author CHY
 *
 */
public class ResPenOnOffCommand extends Command 
{
    private boolean status;

    public ResPenOnOffCommand(int key, CommProcessor comp) 
    {
        super(key, comp);
    }
    
    public void setStatus(boolean status) 
    {
        this.status = status;
    }
    
    protected void write() 
    {
        super.comp.write( ProtocolParser.buildPenOnOffData(status) );        
    }
    
    public void run() 
    {
        this.write();
        super.isAlive = false;
    }
}
