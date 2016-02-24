package kr.neolab.sdk.pen.bluetooth.cmd;

import kr.neolab.sdk.pen.bluetooth.comm.CommProcessor;
import kr.neolab.sdk.pen.bluetooth.lib.ProtocolParser;

/**
 * @author CHY
 *
 */
public class SetTimeCommand extends Command 
{
    public SetTimeCommand(int key, CommProcessor comp) 
    {
        super(key, comp);
    }
    
    protected void write() 
    {
        super.comp.write( ProtocolParser.buildSetCurrentTimeData() );
    }
}
