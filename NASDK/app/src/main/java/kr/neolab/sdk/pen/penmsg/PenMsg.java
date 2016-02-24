package kr.neolab.sdk.pen.penmsg;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The structure for sent message from pen
 * 
 * @author CHY
 * 
 */
public class PenMsg
{
	public int penMsgType;

	public String content;

	public PenMsg( int penMsgType )
	{
		this.penMsgType = penMsgType;
	}

//	public PenMsg( int penMsgType, String content )
//	{
//		this.penMsgType = penMsgType;
//		this.content = content;
//	}

	public PenMsg( int penMsgType, JSONObject job )
	{
		this.penMsgType = penMsgType;
		
		try
		{
			this.content = toJSONString(job);
		}
		catch ( JSONException e )
		{
			e.printStackTrace();
		}
	}

	public PenMsg( int penMsgType, JSONArray jarr )
	{
		this.penMsgType = penMsgType;
		
		try
		{
			this.content = toJSONString(jarr);
		}
		catch ( JSONException e )
		{
			e.printStackTrace();
		}
	}
	
	public PenMsg( int penMsgType, String name, String value )
	{
		this.penMsgType = penMsgType;
		
		try
		{
			this.content = toJSONString( name, value );
		}
		catch ( JSONException e )
		{
			e.printStackTrace();
		}
	}
	
	public PenMsg( int penMsgType, String[] names, String[] values )
	{
		this.penMsgType = penMsgType;
		
		try
		{
			this.content = toJSONString(names, values);
		}
		catch ( JSONException e )
		{
			e.printStackTrace();
		}
	}
	
	public int getPenMsgType()
	{
		return penMsgType;
	}
	
	public String getContent()
	{
		return content;
	}
	
	public JSONObject getContentByJSONObject()
	{
		return getContentByJSONObject(content);
	}
	
	public JSONArray getContentByJSONArray()
	{
		return getContentByJSONArray(content);
	}
	
	public static JSONObject getContentByJSONObject(String content)
	{
		JSONObject result = null;
		
		try
		{
			result = new JSONObject(content);
		}
		catch ( JSONException e )
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static JSONArray getContentByJSONArray(String content)
	{
		JSONArray result = null;
		
		try
		{
			result = new JSONArray(content);
		}
		catch ( JSONException e )
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	public String toJSONString(String name, String value) throws JSONException
	{
		JSONObject job = new JSONObject();
		
		job.put( name, value );
		
		return job.toString();
	}
	
	public String toJSONString(String[] names, String[] values) throws JSONException
	{
		if ( names.length != values.length )
		{
			return null;
		}
		
		JSONObject job = new JSONObject();
		
		for (int i=0; i<names.length; i++)
		{
			job.put( names[i], values[i] );
		}
		
		return job.toString();
	}
	
	public String toJSONString( LinkedHashMap<String, String> map ) throws JSONException
	{
		JSONObject job = new JSONObject();
		
		Set<Entry<String, String>> entrys = map.entrySet();
		
		for (Entry<String, String> item : entrys)
		{
			job.put( item.getKey(), item.getValue() );
		}
		
		return job.toString();
	}
	
	public String toJSONString( JSONArray jarr ) throws JSONException
	{
		return jarr.toString();
	}
	
	public String toJSONString( JSONObject job ) throws JSONException
	{
		return job.toString();
	}
}
