package kr.neolab.sdk.ink.structure;

/**
 * The class of information is stored to the dot
 * 
 * @author CHY
 *
 */
public class Dot 
{
	public float x, y;
	public int dotType;
	public int pressure; 
	public long timestamp;
	
	/**
	 * A constructor that constructs a Dot object
	 *
     * @param x x-coordinate of dot
     * @param y y-coordinate of dot
     * @param pressure level of pressure
     * @param dotType type of dot
     * @param timestamp generated time
	 */
	public Dot(float x, float y, int pressure, int dotType, long timestamp)
	{
		this.x = x;
		this.y = y;
		this.pressure = pressure;
		this.dotType = dotType;
		this.timestamp = timestamp;
	}
	
	/**
	 * A constructor that constructs a Dot object
	 *
     * @param x x-coordinate of dot
     * @param y y-coordinate of dot
     * @param fx below decimal point of x-coordinate
     * @param fy below decimal point of y-coordinate
     * @param pressure level of pressure
     * @param dotType type of dot
     * @param timestamp generated time
	 */
	public Dot(int x, int y, int fx, int fy, int pressure, int dotType, long timestamp)
	{
		this((x + (float) (fx * 0.01)), (y + (float) (fy * 0.01)), pressure, dotType, timestamp);

	}
	
	/**
	 * Returns the x coordinate of the dot
	 * 
	 * @return x
	 */
	public float getX() 
	{
		return x;
	}

	/**
	 * Returns the y coordinate of the dot
	 * 
	 * @return y
	 */
	public float getY() 
	{
		return y;
	}

	/**
	 * Returns the type of the dot
	 * 
	 * @return dotType
	 */
	public int getDotType() 
	{
		return dotType;
	}

	/**
	 * Returns the pressure of the dot
	 * 
	 * @return pressure
	 */
	public int getPressure() 
	{
		return pressure;
	}

	/**
	 * Returns the timestamp of the dot
	 * 
	 * @return timestamp
	 */
	public long getTimestamp() 
	{
		return timestamp;
	}
}
