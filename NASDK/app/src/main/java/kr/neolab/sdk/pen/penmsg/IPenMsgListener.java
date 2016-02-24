package kr.neolab.sdk.pen.penmsg;

/**
 * Been sent from the pen to implement an event callback interface
 * 
 * @author CHY
 */
public interface IPenMsgListener 
{    
    /**
     * Fired when a receive dot successfully, override to handle in your own code
	 *
     * @param sectionId
     * @param ownerId
     * @param noteId
     * @param pageId
     * @param x
     * @param y
     * @param fx
     * @param fy
     * @param pressure
     * @param timestamp
     * @param type
     * @param color
     */
    public void onReceiveDot(int sectionId, int ownerId, int noteId, int pageId, int x, int y, int fx, int fy, int pressure, long timestamp, int type, int color);
    
    /**
     * Fired when a receive message from pen, override to handle in your own code
     * 
     * @param penMsg message object from pen
     */
    public void onReceiveMessage(PenMsg penMsg);
}
