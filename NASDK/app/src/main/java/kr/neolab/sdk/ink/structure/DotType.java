package kr.neolab.sdk.ink.structure;

/**
 * Separated by a dot enumeration type
 * 
 * @author CHY
 *
 */
public enum DotType 
{
	PEN_ACTION_DOWN(1), PEN_ACTION_MOVE(2), PEN_ACTION_UP(4), PEN_ACTION_HOVER(8), PEN_TYPE_INK(16), PEN_TYPE_ERASER(32);
	
	private final int value;
	
	DotType(int i)
	{
		this.value = i;
	}
	
	public int getValue() 
	{
		return this.value;
	}
	
	public static int toIntInkDot(DotType type)
	{
		return PEN_TYPE_INK.getValue() | type.getValue();
	}
	
	public static int toIntEraserDot(DotType type)
	{
		return PEN_TYPE_ERASER.getValue() | type.getValue();
	}
	
	public static DotType getPenType(int type)
	{
		if ( isEraserType(type) )
		{
			return PEN_TYPE_ERASER;
		}
		
		return PEN_TYPE_INK;
	}
	
	public static DotType getPenAction(int type)
	{
		if ( isPenActionDown(type) )
		{
			return PEN_ACTION_DOWN;
		}
		else if ( isPenActionHover(type) )
		{
			return PEN_ACTION_HOVER;
		}	
		else if ( isPenActionUp(type) )
		{
			return PEN_ACTION_UP;
		}

		return PEN_ACTION_MOVE;
	}
	
	public static boolean isEraserType(int type)
	{
		return (type & PEN_TYPE_ERASER.getValue()) > 0 ? true : false;
	}
	
	public static boolean isInkType(int type)
	{
		return (type & PEN_TYPE_INK.getValue()) > 0 ? true : false;
	}
	
	public static boolean isPenActionUp(int type)
	{
		return (type & PEN_ACTION_UP.getValue()) > 0 ? true : false;
	}
	
	public static boolean isPenActionDown(int type)
	{
		return (type & PEN_ACTION_DOWN.getValue()) > 0 ? true : false;
	}
	
	public static boolean isPenActionMove(int type)
	{
		return (type & PEN_ACTION_MOVE.getValue()) > 0 ? true : false;
	}
	
	public static boolean isPenActionHover(int type)
	{
		return (type & PEN_ACTION_HOVER.getValue()) > 0 ? true : false;
	}
};