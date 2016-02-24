package kr.neolab.sdk.pen.filter;

import android.graphics.Color;
import kr.neolab.sdk.ink.structure.Dot;

public class Fdot 
{
    public int dotType;
    public int sectionId, ownerId, noteId, pageId, x, y, fx, fy, force, color = Color.BLACK;
    public long timestamp;
    
    public void setDot(int dotType, int sectionId, int ownerId, int noteId, int pageId, int x, int y, long time, int fx, int fy, int force, int color)
    {
    	this.dotType = dotType;
        this.sectionId = sectionId;
        this.ownerId = ownerId;    	
        this.noteId = noteId;
        this.pageId = pageId;
        this.x = x;
        this.y = y;
        this.timestamp = time;
        this.fx = fx;
        this.fy = fy;
        this.force = force;
        this.color = color;
    }
    
    public String ToString()
    {
        String tempstring = "dotType:" + dotType + " sectionId: " + sectionId + " ownerId: " + ownerId + " noteId: " + noteId + " pageId: " + pageId + " x: " + x + " y: " + y + " fx= " + fx + " fy: " + fy + " force: " + force+" Time:"+timestamp;
        return tempstring;
    }

    public Dot toDot()
    {
    	return new Dot( x, y, fx, fy, force, dotType, timestamp);
    }
}