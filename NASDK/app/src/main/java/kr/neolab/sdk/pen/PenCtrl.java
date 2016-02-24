package kr.neolab.sdk.pen;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import kr.neolab.sdk.broadcastreceiver.BTDuplicateRemoveBroadcasterReceiver;
import kr.neolab.sdk.pen.bluetooth.BTAdt;
import kr.neolab.sdk.pen.penmsg.IPenMsgListener;
import kr.neolab.sdk.util.NLog;
import kr.neolab.sdk.util.SDKVersion;

/**
 * The class that provides the functionality of a pen
 * 
 * @author CHY
 *
 */
public class PenCtrl implements IPenCtrl 
{
	private IPenAdt adt = null;
    private static PenCtrl myInstance = null;
    private static BTDuplicateRemoveBroadcasterReceiver mReceiver;

    private PenCtrl() 
    {
        adt = BTAdt.getInstance();
    }
    
    /**
     * Gets an instance of the Pen Controller
     * 
     * @return
     */
	public synchronized static PenCtrl getInstance() 
	{
		if (myInstance == null) 
		{
			if ( myInstance == null )
			{
				myInstance = new PenCtrl();
			}
		}
		if(mReceiver == null)
		{
			mReceiver = new BTDuplicateRemoveBroadcasterReceiver();
		}
		
		return myInstance;
	}
	
	@Override
	public void setContext(Context context) {
		// TODO Auto-generated method stub
		adt.setContext(context);
	}
    
    @Override
    public void setListener(IPenMsgListener listener)
    {
    	adt.setListener(listener);
    }
    
	@Override
	public IPenMsgListener getListener() {
		// TODO Auto-generated method stub
		return adt.getListener();
	}

    
	@Override
	public void startup() 
	{
        NLog.i("[BTCtrl] startup");
        adt.startListen();
	}

    @Override
    public void connect(String address) 
    {
    	adt.connect(address);
    }
    
    @Override
    public void disconnect() 
    {
        NLog.i("[BTCtrl] disconnect all pen");
        adt.disconnect();
    }
    
    @Override
	public boolean isAvailableDevice( String mac )
	{
    	return adt.isAvailableDevice( mac );
	}
	
    @Override
    public String getConnectedDevice() 
    {
        return adt.getConnectedDevice();
    }   
    
	@Override
	public void inputPassword( String password )
	{
		adt.inputPassword(password);
	}

	@Override
	public void reqSetupPassword( String oldPassword, String newPassword )
	{
		adt.reqSetupPassword(oldPassword,newPassword);
	}
	
    @Override
	public void reqDisplayShowString24(String showString24) 
    {
    	adt.reqDisplayShowString24(showString24);		
	}
    	
	@Override
	public void setAllowOfflineData(boolean allow) 
	{	
		adt.setAllowOfflineData(allow);
	}
	
	@Override
	public synchronized void setOfflineDataLocation(String path) 
	{
		adt.setOfflineDataLocation(path);
	}
	
	@Override
	public void calibratePen() 
	{	
		adt.reqForceCalibrate();
	}
	
	@Override
	public void upgradePen(File fwFile) 
	{   
		String target = "\\NEO1.zip";
		adt.reqFwUpgrade(fwFile, target);
	}
	
	@Override
	public void upgradePen(File fwFile, String targetPath) 
	{    
		adt.reqFwUpgrade(fwFile, targetPath);
	}
	
	@Override
	public void suspendPenUpgrade()
	{
		adt.reqSuspendFwUpgrade();
	}
	
	@Override
	public void reqAddUsingNote(int sectionId, int ownerId, int[] noteIds) 
	{
		adt.reqAddUsingNote(sectionId, ownerId, noteIds);
	}

	@Override
	public void reqAddUsingNote( int sectionId, int ownerId )
	{
		adt.reqAddUsingNote(sectionId, ownerId);
	}

	@Override
	public void reqAddUsingNoteAll()
	{
		adt.reqAddUsingNoteAll();
	}

	@Override
	public void reqOfflineData(int sectionId, int ownerId, int noteId) 
	{
		adt.reqOfflineData(sectionId, ownerId, noteId);
	}
	
	@Override
	public void reqOfflineDataList() 
	{
		adt.reqOfflineDataList();
	}

	@Override
	public void removeOfflineData( int sectionId, int ownerId )
	{
		adt.removeOfflineData(sectionId, ownerId);
	}
	
	@Override
	public void reqPenStatus() 
	{
		adt.reqPenStatus();
	}
	
	@Override
	public void reqSetupAutoPowerOnOff(boolean setOn) 
	{
		adt.reqSetupAutoPowerOnOff( setOn );
	}
	
	@Override
    public void reqSetupPenBeepOnOff( boolean setOn )
    {
		adt.reqSetupPenBeepOnOff( setOn );
    }

	@Override
	public void reqSetupPenTipColor( int color )
	{
		adt.reqSetupPenTipColor( color );
	}

	@Override
	public void reqSetupAutoShutdownTime( short minute )
	{
		adt.reqSetupAutoShutdownTime( minute );
	}

	@Override
	public void reqSetupPenSensitivity( short level )
	{
		adt.reqSetupPenSensitivity( level );
	}

	@Override
	public void registerBroadcastBTDuplicate() {
		try {
			IntentFilter filter = new IntentFilter();
			filter.addAction(BTDuplicateRemoveBroadcasterReceiver.ACTION_BT_REQ_CONNECT);
			filter.addAction(BTDuplicateRemoveBroadcasterReceiver.ACTION_BT_RESPONSE_CONNECTED);
			adt.getContext().registerReceiver(mReceiver, filter);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void unregisterBroadcastBTDuplicate() {
		try {
			adt.getContext().unregisterReceiver(mReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getVersion() {
		return SDKVersion.version;
	}


	
}


