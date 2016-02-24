package kr.neolab.sdk.pen.bluetooth.cmd;

import kr.neolab.sdk.pen.bluetooth.comm.CommProcessor;
import kr.neolab.sdk.pen.bluetooth.lib.ProtocolParser;



/**
 * @author CHY
 *
 */
public class PenStatusCommand extends Command 
{
    public PenStatusCommand(int key, CommProcessor comp) 
    {
        super(key, comp);
    }
    
    protected void write() 
    {
        comp.write( ProtocolParser.buildPenStatusData() );        
    }
}
