package kr.neolab.sdk.pen;

import java.io.File;

import android.content.Context;
import kr.neolab.sdk.pen.penmsg.IPenMsgListener;

/**
 * The interface that provides the functionality of a pen
 * 
 * @author CHY
 *
 */
public interface IPenCtrl 
{
	/**
	 * Set up listener of message from pen
	 * 
	 * @param listener callback interface
	 */
	public void setListener(IPenMsgListener listener);

	/**
	 * get up listener of message from pen
	 * @return IPenMsgListener
	 */
	public IPenMsgListener getListener();

	/**
	 * Ready to use function
	 */
	public void startup();
	
    /**
     * Attempts to connect to the pen.
     * 
     * @param address MAC address of pen
     */
    public void connect(String address);

    /**
     * And disconnect the connection with pen
     */
    public void disconnect();

    /**
     * Connected to the pen's current information.
     * 
     * @return
     */
    public String getConnectedDevice();

	/**
	 * Confirm whether or not the MAC address to connect
	 * 
	 * @param mac
	 * @return true if can use, otherwise false
	 */
    public boolean isAvailableDevice( String mac );
    
    /**
     * When pen requested password, you can response password by this method. 
	 *
     * @param password
     */
    public void inputPassword( String password );
    
    /**
     * Change the password of pen.
     * 
     * @param oldPassword current password
     * @param newPassword new password
     */
    public void reqSetupPassword( String oldPassword, String newPassword );
    
    /**
     * Pen to the display displays up to 24 characters.(Only English is supported.)
     * 
     * @param showString24 Character you want appears on the display
     */
    public void reqDisplayShowString24(String showString24);
    
    /**
     * Specify whether you want to get the data off-line.
     * 
     * @param allow if allow receive offline data, set true
     */
    public void setAllowOfflineData(boolean allow);
    
    /**
     * Specify where to store the offline data. (Unless otherwise specified, is stored in the default external storage)
     * 
     * @param path Be stored in the directory
     */
    public void setOfflineDataLocation(String path);
    
    /**
     * Adjust the pressure-sensor to the pen.
     */
    public void calibratePen();
    
    /**
     * To upgrade the firmware of the pen.
     *
     * @param fwFile object of firmware
     * @param targetPath The file path to be stored in the pen
     */
    public void upgradePen(File fwFile, String targetPath);

    /**
     * To upgrade the firmware of the pen.
     * 
     * @param fwFile object of firmware
     */
	public void upgradePen(File fwFile);

	
	/**
	 * To suspend Upgrading task.
	 */
	public void suspendPenUpgrade();
	
    /**
     * Notes for use in applications specified.
     * 
     * @param sectionId section id of note
     * @param ownerId owner id of note
     * @param noteIds array of note id 
     */
    public void reqAddUsingNote(int sectionId, int ownerId, int[] noteIds);
 
    /**
     * Notes for use in applications specified.
     * 
     * @param sectionId section id of note
     * @param ownerId owner id of note
     */
    public void reqAddUsingNote(int sectionId, int ownerId);
    
    
    /**
     * Specifies that all of the available notes.
     */
    public void reqAddUsingNoteAll();
    
    /**
     * The pen is stored in an offline transfer of data requested.
     * (Please note that this function is not synchronized. If multiple threads concurrently try to run this function, explicit synchronization must be done externally.)
     * 
     * @param sectionId section id of note
     * @param ownerId owner id of note
     * @param noteId of note
     */
    public void reqOfflineData(int sectionId, int ownerId, int noteId);
    		
    /**
     * The offline data is stored in the pen to request information.
     */
    public void reqOfflineDataList();
    
    /**
     * To Delete offline data of pen
     * 
     * @param sectionId section id of note
     * @param ownerId owner id of note
     */
    public void removeOfflineData(int sectionId, int ownerId);
    
    /**
     * Connected to the current state of the pen provided.
     */
    public void reqPenStatus();

    /**
     * Disable or enable Auto Power function
     *  
     * @param setOn
     */
    public void reqSetupAutoPowerOnOff( boolean setOn );
    
    /**
     * Disable or enable sound of pen
     *  
     * @param setOn
     */
    public void reqSetupPenBeepOnOff( boolean setOn );
    
    /**
     * Setup color of pen
     * 
     * @param color
     */
    public void reqSetupPenTipColor( int color );
    
    
    /**
     * Setup auto shutdown time of pen
     *  
     * @param minute shutdown wait time of pen
     */
    public void reqSetupAutoShutdownTime( short minute );
    
    /**
     * Setup Sensitivity level of pen
     *  
     * @param level sensitivity level (0~4)
     */
    public void reqSetupPenSensitivity( short level );
    
    /**
     * register Broadcast for remove BT Duplicate connect.
     */
    public void registerBroadcastBTDuplicate();
    
    
    /**
     * unregister Broadcast for remove BT Duplicate connect.
     */
    public void unregisterBroadcastBTDuplicate();
    
    /**
     * set Context
     * @param context
     */
    public void setContext( Context context );
    
    /**
     * get SDK version
     * @return
     */
    public String getVersion();

}
