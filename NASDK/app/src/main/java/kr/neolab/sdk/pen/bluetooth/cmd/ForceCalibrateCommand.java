package kr.neolab.sdk.pen.bluetooth.cmd;

import kr.neolab.sdk.pen.bluetooth.comm.CommProcessor;
import kr.neolab.sdk.pen.bluetooth.lib.ProtocolParser;

/**
 * 
 * @author CHY
 *
 */
public class ForceCalibrateCommand extends Command 
{    
    public ForceCalibrateCommand(int key, CommProcessor comp) 
    {
        super(key, comp);
    }

    protected void write() 
    {
        super.comp.write( ProtocolParser.buildForceCalibrateData() );        
    }
    
    public void run() 
    {
        // TODO Auto-generated method stub
        this.write();
        super.isAlive = false;
    }
}
