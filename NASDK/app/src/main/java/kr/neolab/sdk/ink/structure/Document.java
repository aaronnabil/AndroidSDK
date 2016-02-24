package kr.neolab.sdk.ink.structure;

import java.util.ArrayList;
import android.graphics.Canvas;
import android.graphics.RectF;


/**
 * A collection of Stroke 
 * (One page, a page number of days and the number of days in a certain area.)
 * 
 * @author CHY
 *
 */
public class Document extends ArrayList<Stroke>
{
	/**
	 * serial ID
	 */
	private static final long serialVersionUID = 2L;

	public int sectionId, ownerId, noteId, pageId;
	
	public long timeEnd=0l, timeStart=0l;
	
	public boolean isCropped = false;
	
	public RectF rectArea = null;
	
	public Document(int sectionId, int ownerId, int noteId, int pageId)
	{
		super();
		this.sectionId = sectionId;
		this.ownerId = ownerId;
		this.noteId = noteId;
		this.pageId = pageId;
	}
	
	/**
	 * add stroke to document
	 * 
	 * @param str Add a Stroke object
	 */
	public boolean add(Stroke str)
	{
		if ( timeEnd == 0 )
		{
			this.timeEnd = str.timeStampStart;
		}

		this.timeStart = str.timeStampEnd;
		
		return super.add(str);
	}
	
	/**
	 * change scale all the Strokes
	 * 
	 * @param scale scale To zoom in or out of scale for stroke
	 */
	public void changeScale(float scale)
	{
		for (Stroke s: this)
		{
			s.changeScale(scale);
		}

		this.rectArea = new RectF(this.rectArea.left * scale, this.rectArea.top * scale, this.rectArea.right * scale, this.rectArea.bottom * scale);
	}

	/**
	 * Moves all the strokes.
	 * 
	 * @param dx Moving coordinate-x
	 * @param dy Moving coordinate-y
	 */
	public void changePos(float dx, float dy)
	{
		for (Stroke s: this)
		{
			s.changePos(dx, dy);
		}
		
		this.rectArea = new RectF(this.rectArea.left + dx, this.rectArea.top + dy, this.rectArea.right + dx, this.rectArea.bottom + dy);
	}
	
	public Document crop(RectF area)
	{
		Document odoc = copy();
		Document ndoc = new Document(this.sectionId, this.ownerId, this.noteId, this.pageId);

		for (Stroke s: odoc)
		{
			if ( s.intersect(area) && s.contains(area) )
			{
				s.changePos(-area.left, -area.top);
				ndoc.add(s);
			}
		}
		
		ndoc.isCropped = true;
		ndoc.rectArea = area;
		
		odoc.clear();
		odoc = null;
		
		return ndoc;
	}

	/**
	 * Draw all line on the canvas object
	 * 
	 * @param canvas Canvas object to draw stroke
	 * @param scale To zoom in or out of scale for stroke
	 * @param offset_x coordinates by moving the draw stroke
	 * @param offset_y coordinates by moving the draw stroke
	 */
	public void drawToCanvas( Canvas canvas, float scale, float offset_x, float offset_y) 
	{
		int length = this.size();
		
		for (int i=0; i<length;i++)
		{
			this.get(i).drawToCanvas(canvas, scale, offset_x, offset_y);
		}
	}
	
	/**
	 * Draw all line on the canvas object
	 * 
	 * @param canvas Canvas object to draw stroke
	 * @param scale To zoom in or out of scale for stroke
	 * @param offset_x coordinates by moving the draw stroke
	 * @param offset_y coordinates by moving the draw stroke
	 * @param width width of stroke
	 * @param color color of stroke
	 */
	public void drawToCanvas( Canvas canvas, float scale, float offset_x, float offset_y, int width, int color) 
	{
		int length = this.size();
		
		for (int i=0; i<length;i++)
		{
			this.get(i).drawToCanvas(canvas, scale, offset_x, offset_y, width, color);
		}
	}

	/**
	 * Draw all line on the canvas object
	 * 
	 * @param canvas Canvas object to draw stroke
	 * @param scale To zoom in or out of scale for stroke
	 * @param offset_x coordinates by moving the draw stroke
	 * @param offset_y coordinates by moving the draw stroke
	 * @param width1 width of complete stroke
	 * @param color1 color of complete stroke
	 * @param width2 width of interim stroke
	 * @param color2 color of interim stroke
	 */
	public void drawToCanvas( Canvas canvas, float scale, float offset_x, float offset_y, int width1, int color1, int width2, int color2) 
	{
		int length = this.size();
		
		for (int i=0; i<length;i++)
		{
			this.get(i).drawToCanvas(canvas, scale, offset_x, offset_y, width1, color1, width2, color2);
		}
	}

	private Document copy()
	{
		Document result = (Document) this.clone();
		return result; 
	}
}
