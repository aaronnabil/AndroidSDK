package kr.neolab.sdk.pen.bluetooth.cmd;

import kr.neolab.sdk.pen.bluetooth.comm.CommProcessor;
import kr.neolab.sdk.pen.bluetooth.lib.ProtocolParser;

/**
 * 
 * @author A
 *
 */
public class ShowTextCommand extends Command 
{
	private String status;
	
    public ShowTextCommand(int key, CommProcessor comp) 
    {
		super(key, comp);
	} 
    
    public void setStatus(String status) 
    {
        this.status = status;
    }
    
    protected void write() 
    {
        super.comp.write( ProtocolParser.buildShowTextData(status) );    
    }
}
