package kr.neolab.sdk.pen.offline;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;

import kr.neolab.sdk.pen.bluetooth.lib.ByteConverter;
import kr.neolab.sdk.util.NLog;

public class OfflineFile
{
	public int appendCount;
	
	public int packetCount;

	private boolean isCompressed = false;

	private int sectionId = 0, ownerId = 0, noteId = 0, pageId = 0;
	
	private static String OFFLINE_FILE_PATH = getDefaultFilePath();
	
	public static String DEFAULT_FILE_FORMAT = "%d_%d_%d_%d_%d.%s";
	
	private File tempFile = null;
	
	private BufferedOutputStream buffer;
	
	public OfflineFile( String fileinfo, int packetCount, boolean isCompressed )
	{
		String[] arr = fileinfo.split( "\\\\" );

		int sectionOwner = Integer.parseInt( arr[2] );
		
		byte[] bso = ByteConverter.intTobyte( sectionOwner );
		
		sectionId = (int) (bso[3] & 0xFF);
		ownerId = ByteConverter.byteArrayToInt( new byte[] { bso[0], bso[1], bso[2], (byte) 0x00 } );
		
		noteId = Integer.parseInt( arr[3] );
		pageId = Integer.parseInt( arr[4] );

		this.packetCount = packetCount;
		this.appendCount = 0;
		this.isCompressed = isCompressed;
		
		openTempFile();
	}

	private void openTempFile()
	{
		File path = new File( OFFLINE_FILE_PATH );

		if ( !path.isDirectory() )
		{
			path.mkdirs();
		}

		try
		{
			tempFile = File.createTempFile( "_offline", ".tmp", path );
			
			buffer = new BufferedOutputStream( new FileOutputStream( tempFile ) );
		}
		catch ( IOException e )
		{
			NLog.e("[OfflineFile] openTempFile exception", e);
		}
	}
	
	public void clearTempFile()
	{
		File path = new File( OFFLINE_FILE_PATH );

		File[] files = path.listFiles();

		for ( File file : files )
		{
			if ( file.isFile() && file.getName().endsWith( ".tmp" ) )
			{
				file.delete();
			}
		}
	}
	
	public int getNoteId()
	{
		return noteId;
	}

	public int getPageId()
	{
		return pageId;
	}

	public int getSectionId()
	{
		return sectionId;
	}
	
	public int getOwnerId()
	{
		return ownerId;
	}
	
	public int getCount()
	{
		return appendCount;
	}
	
	public synchronized static boolean setOfflineFilePath( String newPath )
	{
		if ( !newPath.endsWith( "/" ) )
		{
			newPath = newPath + "/";
		}

		File newTarget = new File( newPath );

		if ( !newTarget.exists() || !newTarget.isDirectory() )
		{
			return false;
		}

		OFFLINE_FILE_PATH = newPath;

		return true;
	}

	public static String getDefaultFilePath()
	{
		return getExternalStoragePath() + "/neolab/offline/";
	}

	public static String getExternalStoragePath()
	{
		if ( Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) )
		{
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		else
		{
			return Environment.MEDIA_UNMOUNTED;
		}
	}

	public static String getOfflineFilePath()
	{
		return OFFLINE_FILE_PATH;
	}

	public void append( byte[] data, int index )
	{
		appendCount++;
		
		try
		{
			for ( int i = 0; i < data.length; i++ )
			{
				buffer.write( data, i, 1 );
			}
		}
		catch ( IOException e )
		{
			NLog.e("[OfflineFile] append exception", e);
		}
	}
	
	public String make()
	{
		if ( buffer != null )
		{
			try
			{
				buffer.close();
			}
			catch ( IOException e )
			{
				NLog.e("[OfflineFile] make exception", e);
			}
			
			buffer = null;
		}
		
		String filename = String.format( DEFAULT_FILE_FORMAT, sectionId, ownerId, noteId, pageId, System.currentTimeMillis(), isCompressed ? "zip" : "pen" );
		
		boolean result = tempFile.renameTo( new File(OFFLINE_FILE_PATH, filename) );
		
		NLog.d("[OfflineFile] result : " + result + ", filename : " + filename );
		
		return OFFLINE_FILE_PATH + filename;
	}
}
