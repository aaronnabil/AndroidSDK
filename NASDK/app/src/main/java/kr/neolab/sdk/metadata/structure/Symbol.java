package kr.neolab.sdk.metadata.structure;

import android.graphics.RectF;

public class Symbol extends RectF
{
	public int noteId, pageId;
	public String id, previousId, nextId; 
	public Symbol previous, next;
	public String name, action, param;
	
	public Symbol(int noteId, int pageId, String name, String action, String param, float left, float top, float right, float bottom)
	{
		super(left, top, right, bottom);
		
		this.noteId = noteId;
		this.pageId = pageId;
		this.name   = name;
		this.action = action;
		this.param  = param;
	}
	
	/**
	 * @return the id
	 */
	public String getId() 
	{
		return id;
	}

	/**
	 * @return the previous
	 */
	public Symbol getPrevious() 
	{
		return previous;
	}

	/**
	 * @return the next
	 */
	public Symbol getNext() 
	{
		return next;
	}
	
	/**
	 * @return the name
	 */
	public String getName() 
	{
		return name;
	}

	/**
	 * @return the action
	 */
	public String getAction() 
	{
		return action;
	}

	/**
	 * @return the param
	 */
	public String getParam() 
	{
		return param;
	}

	public float getX()
	{
		return this.left;
	}
	
	public float getY()
	{
		return this.top;
	}
	
	public float getWidth()
	{
		return this.width();
	}
	
	public float getHeight()
	{
		return this.height();
	}
	
	public String toString()
	{
		return "Symbol => noteId : " + noteId + ", pageId : " + pageId + ", RectF (" + left + "," + top + "," + width() + "," + height() + "), param : " + param;
	}
}