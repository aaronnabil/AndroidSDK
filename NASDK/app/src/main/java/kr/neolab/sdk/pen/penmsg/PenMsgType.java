package kr.neolab.sdk.pen.penmsg;

/**
 * To identify messages that are sent to the pen.
 * 
 * @author CHY
 *
 */
public class PenMsgType
{
	/**
	 * Pen events that occur when you attempt to connect to
	 */
	public final static int PEN_CONNECTION_TRY			= 0x01;
	
	/**
	 * Pens when the connection is successful, the events that occur
	 */
	public final static int PEN_CONNECTION_SUCCESS		= 0x02;
	
	/**
	 * Pens when the connection fails, an event that occurs
	 */
	public final static int PEN_CONNECTION_FAILURE		= 0x03;
	
	
	/**
	 * Pen events that occur when a connection is released
	 */
	public final static int PEN_DISCONNECTED			= 0x04;
	
	/**
	 * Pens when the pen authorized, the events that occur
	 */
	public final static int PEN_AUTHORIZED			    = 0x05;
	
	/**
	 * The firmware version of pen
	 */
	public final static int PEN_FW_VERSION			    = 0x10;
	
	/**
	 * The status(battery, memory, ...) of pen
	 */
	public final static int PEN_STATUS                  = 0x11;
	
	public final static int PEN_SETUP_SUCCESS     		= 0x12;
	
	public final static int PEN_SETUP_FAILURE           = 0x13;
	
	public final static int PEN_SETUP_AUTO_SHUTDOWN_RESULT = 0x14;
	
	public final static int PEN_SETUP_SENSITIVITY_RESULT   = 0x15;
	
	public final static int PEN_SETUP_AUTO_POWER_ON_RESULT = 0x16;
	
	public final static int PEN_SETUP_BEEP_RESULT          = 0x17;
	
	public final static int PEN_SETUP_PEN_COLOR_RESULT     = 0x18;
	
	/**
	 * Events that occur when you start the pressure-adjusting
	 */
	public final static int PEN_CALIBRATION_START		= 0x20;
	
	/**
	 * Events that occur when you finish the pressure-adjusting
	 */
	public final static int PEN_CALIBRATION_FINISH		= 0x21;
	
	/**
	 * Message showing the status of the firmware upgrade pen
	 */
	public final static int PEN_FW_UPGRADE_STATUS		= 0x22;
	
	/**
	 * When the firmware upgrade is successful, the pen events that occur
	 */
	public final static int PEN_FW_UPGRADE_SUCCESS		= 0x23;
	
	/**
	 * When the firmware upgrade is fails, the pen events that occur
	 */
	public final static int PEN_FW_UPGRADE_FAILURE		= 0x24;

	/**
	 * When the firmware upgrade is suspended, the pen events that occur
	 */
	public final static int PEN_FW_UPGRADE_SUSPEND		= 0x25;
	
	/**
	 * Pen gesture detection events that occur when
	 */
	public final static int PEN_ACTION_GESTURE			= 0x40;
	
	/**
	 * Off-line data stored in the pen's 
	 */
	public final static int OFFLINE_DATA_NOTE_LIST	    = 0x30;
	
	public final static int OFFLINE_DATA_SEND_START		= 0x31;
	
	public final static int OFFLINE_DATA_SEND_STATUS	= 0x32;
	
	public final static int OFFLINE_DATA_SEND_SUCCESS	= 0x33;
	
	public final static int OFFLINE_DATA_SEND_FAILURE	= 0x34;
	
	public final static int OFFLINE_DATA_FILE_CREATED	= 0x35;
	
	public final static int OFFLINE_DATA_FILE_DELETED	= 0x36;
	
	
	public final static int PASSWORD_REQUEST			= 0x51;
	
	public final static int PASSWORD_SETUP_SUCCESS	    = 0x52;
	
	public final static int PASSWORD_SETUP_FAILURE		= 0x53;
	
	/**
	 * Pens when the connection fails cause duplicate BT connection, an event that occurs 
	 */
	public final static int PEN_CONNECTION_FAILURE_BTDUPLICATE	= 0x54;

}

