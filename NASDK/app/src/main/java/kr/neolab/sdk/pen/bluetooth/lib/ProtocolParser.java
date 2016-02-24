package kr.neolab.sdk.pen.bluetooth.lib;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.TimeZone;
import kr.neolab.sdk.util.NLog;

/**
 * @author CHY
 * 
 */
public class ProtocolParser
{
	private static final int PKT_START = 0xC0;
	private static final int PKT_END = 0xC1;
	private static final int PKT_EMPTY = 0x00;
	private static final int PKT_HEADER_LEN = 3;
	private static final int PKT_LENGTH_POS1 = 1;
	private static final int PKT_LENGTH_POS2 = 2;
	private static final int PKT_MAX_LEN = 8200;

	private int counter = 0;
	private int dataLength = 0;

	// length
	private byte[] lbuffer = new byte[2];

	private static int buffer_size = PKT_MAX_LEN + 1;

	private ByteBuffer nbuffer = ByteBuffer.allocate( buffer_size );

	private boolean isStart = true;

	private IParsedPacketListener listener = null;

	public ProtocolParser( IParsedPacketListener listener )
	{
		this.listener = listener;
	}

	public void parseByteData( byte data[], int size )
	{
		// StringBuffer sb = new StringBuffer();

		for ( int i = 0; i < size; i++ )
		{
			// int int_data = (int) (data[i] & 0xFF);
			//
			// sb.append(Integer.toHexString(int_data));
			// sb.append(", ");
			//
			// if ( int_data == 0xC1 )
			// {
			// NLog.d("[CommProcessor] parseByteData : " + sb.toString());
			// sb = new StringBuffer();
			// }

			parseOneByte( data[i], listener );
		}

		// NLog.d("[CommProcessor] parseByteData : " + sb.toString());
	}

	private void parseOneByte( byte data, IParsedPacketListener listener )
	{
		int int_data = (int) (data & 0xFF);

		if ( int_data == PKT_START && isStart )
		{
			counter = 0;
			isStart = false;
		}
		else if ( int_data == PKT_END && counter == dataLength + PKT_HEADER_LEN )
		{
			this.listener.onCreatePacket( new Packet( nbuffer.array() ) );

			dataLength = 0;
			counter = 10;
			nbuffer.clear();

			isStart = true;
		}
		else if ( counter > PKT_MAX_LEN )
		{
			counter = 10;
			dataLength = 0;

			isStart = true;
		}
		else
		{
			if ( counter == PKT_LENGTH_POS1 )
			{
				lbuffer[0] = data;
			}
			else if ( counter == PKT_LENGTH_POS2 )
			{
				lbuffer[1] = data;
				dataLength = ByteConverter.byteArrayToInt( lbuffer );
			}

			nbuffer.put( counter, data );

			counter++;
		}
	}

	// 0x02 CMD.P_PenOnResponse
	public static byte[] buildPenOnOffData( boolean status )
	{
		PacketBuilder builder = new PacketBuilder( 9 );

		builder.setCommand( CMD.P_PenOnResponse );

		byte[] buffer = ByteConverter.longTobyte( System.currentTimeMillis() );

		builder.write( buffer, 8 );

		if ( status )
		{
			builder.write( (byte) 0x00 );
		}
		else
		{
			builder.write( (byte) 0x01 );
		}

		return builder.getPacket();
	}

	public static int getLocalTimeOffset( long timetick )
	{
		return TimeZone.getDefault().getOffset( timetick );
	}

	// 0x03 CMD.P_RTCset
	public static byte[] buildSetCurrentTimeData()
	{
		PacketBuilder builder = new PacketBuilder( 12 );

		builder.setCommand( CMD.P_RTCset );

		long ts = System.currentTimeMillis();
		int offset = getLocalTimeOffset( ts );

		builder.write( ByteConverter.longTobyte( ts ), 8 );
		builder.write( ByteConverter.intTobyte( offset ), 4 );

		return builder.getPacket();
	}

	// 0x07 CMD.P_ForceCalibrate
	public static byte[] buildForceCalibrateData()
	{
		PacketBuilder builder = new PacketBuilder( 0 );
		builder.setCommand( CMD.P_ForceCalibrate );

		return builder.getPacket();
	}

	// 0x0A CMD.P_EchoResponse
	public static byte[] buildPenEchoResponse( byte echar )
	{
		PacketBuilder builder = new PacketBuilder( 1 );
		builder.setCommand( CMD.P_EchoResponse );
		builder.write( echar );

		return builder.getPacket();
	}

	public static byte[] buildPenUpRespnse( long ts )
	{
		PacketBuilder builder = new PacketBuilder( 8 );
		builder.setCommand( CMD.P_DotUpDownResponse );
		builder.write( ByteConverter.longTobyte( ts ), 8 );

		return builder.getPacket();
	}

	// 0x21 CMD.P_PenStatusRequest
	public static byte[] buildPenStatusData()
	{
		PacketBuilder builder = new PacketBuilder( 0 );
		builder.setCommand( CMD.P_PenStatusRequest );

		return builder.getPacket();
	}

	// 0x42 CMD.P_OfflineInfoResponse
	public static byte[] buildOfflineInfoResponse( boolean result )
	{
		PacketBuilder builder = new PacketBuilder( 2 );
		builder.setCommand( CMD.P_OfflineInfoResponse );
		builder.write( (byte) (result ? 0x01 : 0x00) );
		builder.write( (byte) (0x00) );
		return builder.getPacket();
	}

	// 0x44 CMD.P_OfflineChunkResponse
	public static byte[] buildOfflineChunkResponse( int index )
	{
		PacketBuilder builder = new PacketBuilder( 2 );
		builder.setCommand( CMD.P_OfflineChunkResponse );
		builder.write( ByteConverter.shortTobyte( (short) index ) );

		return builder.getPacket();
	}

	// 0x51 CMD.P_PenSWUpgradeCommand
	public static byte[] buildPenSwUpgrade( String filename, int filesize, short chunk_count, short chunk_size )
	{
		PacketBuilder builder = new PacketBuilder( 136 );

		builder.setCommand( CMD.P_PenSWUpgradeCommand );

		// FILE NAME
		ByteBuffer temp = ByteBuffer.wrap( filename.getBytes() );
		temp.order( ByteOrder.LITTLE_ENDIAN );
		byte[] bFilename = temp.array();

		builder.write( bFilename, 128 );

		// FILE SIZE
		byte[] bFilesize = ByteConverter.intTobyte( filesize );
		builder.write( bFilesize, 4 );

		// PACKET COUNT
		byte[] bChunkCount = ByteConverter.shortTobyte( chunk_count );
		builder.write( bChunkCount, 2 );

		// PACKET SIZE
		byte[] bChunkSize = ByteConverter.shortTobyte( chunk_size );
		builder.write( bChunkSize, 2 );

		// builder.showPacket();

		return builder.getPacket();
	}

	// 0x53 P_PenSWUpgradeResponse
	public static byte[] buildPenSwUpgradeResponse( int index, byte checksum, byte[] data )
	{
		int dataLength = data.length + 3;

		PacketBuilder sendbyte = new PacketBuilder( dataLength );
		sendbyte.setCommand( CMD.P_PenSWUpgradeResponse );
		sendbyte.write( ByteConverter.shortTobyte( (short) index ) );
		sendbyte.write( checksum );
		sendbyte.write( data );

		return sendbyte.getPacket();
	}

	// CMD 0x61 P_ShowText
	public static byte[] buildShowTextData( String showText )
	{
		// TODO Auto-generated method stub
		PacketBuilder builder = new PacketBuilder( 24 );
		builder.setCommand( CMD.P_ShowText );

		// showText
		ByteBuffer temp = ByteBuffer.wrap( showText.getBytes() );
		temp.order( ByteOrder.LITTLE_ENDIAN );
		byte[] bFilename = temp.array();

		NLog.d( "[ProtocolParser] showText : " + showText );
		builder.write( bFilename, 24 );
		return builder.getPacket();
	}

	public static final int USING_NOTE_TYPE_NOTE = 1;
	public static final int USING_NOTE_TYPE_SECTION_OWNER = 2;
	public static final int USING_NOTE_TYPE_ALL = 3;
	
	public static byte[] buildAddUsingNotes( int sectionId, int ownerId, int[] noteIds )
	{
		byte[] ownerByte = ByteConverter.intTobyte( ownerId );

		PacketBuilder sendbyte = new PacketBuilder( 42 );
		sendbyte.setCommand( CMD.P_UsingNoteNotify );
		sendbyte.write( (byte) USING_NOTE_TYPE_NOTE );
		sendbyte.write( (byte) noteIds.length );
		sendbyte.write( ownerByte[0] );
		sendbyte.write( ownerByte[1] );
		sendbyte.write( ownerByte[2] );
		sendbyte.write( (byte) sectionId );

		for ( int noteId : noteIds )
		{
			sendbyte.write( ByteConverter.intTobyte( noteId ) );
		}
		
		sendbyte.write( new byte[ (9-noteIds.length) * 4 ] );
		
		return sendbyte.getPacket();
	}
	
	public static byte[] buildAddUsingNotes( int sectionId, int ownerId )
	{
		byte[] ownerByte = ByteConverter.intTobyte( ownerId );

		PacketBuilder sendbyte = new PacketBuilder( 42 );
		sendbyte.setCommand( CMD.P_UsingNoteNotify );
		sendbyte.write( (byte) USING_NOTE_TYPE_SECTION_OWNER );
		sendbyte.write( (byte) 1 );
		sendbyte.write( ownerByte[0] );
		sendbyte.write( ownerByte[1] );
		sendbyte.write( ownerByte[2] );
		sendbyte.write( (byte) sectionId );
		sendbyte.write( new byte[36] );
		
		return sendbyte.getPacket();
	}

	public static byte[] buildAddUsingAllNotes()
	{
		PacketBuilder sendbyte = new PacketBuilder( 42 );
		sendbyte.setCommand( CMD.P_UsingNoteNotify );
		sendbyte.write( (byte) USING_NOTE_TYPE_ALL );
		sendbyte.write( (byte) 0 );
		sendbyte.write( new byte[40] );

		return sendbyte.getPacket();
	}

	public static byte[] buildReqOfflineData( int sectionId, int ownerId, int noteId )
	{
		byte[] ownerByte = ByteConverter.intTobyte( ownerId );

		PacketBuilder sendbyte = new PacketBuilder( 45 );
		sendbyte.setCommand( CMD.P_OfflineDataRequest );
		sendbyte.write( ownerByte[0] );
		sendbyte.write( ownerByte[1] );
		sendbyte.write( ownerByte[2] );
		sendbyte.write( (byte) sectionId );
		sendbyte.write( (byte) 1 );
		sendbyte.write( ByteConverter.intTobyte( noteId ) );
		sendbyte.write( new byte[36] );

		return sendbyte.getPacket();
	}

	public static byte[] buildReqOfflineDataList()
	{
		PacketBuilder sendbyte = new PacketBuilder( 1 );
		sendbyte.setCommand( CMD.P_OfflineNoteList );
		sendbyte.write( (byte) 0x00 );

		return sendbyte.getPacket();
	}

	public static byte[] buildReqOfflineDataRemove( int sectionId, int ownerId )
	{
		byte[] ownerByte = ByteConverter.intTobyte( ownerId );

		PacketBuilder sendbyte = new PacketBuilder( 12 );
		sendbyte.setCommand( CMD.P_OfflineDataRemove );
		sendbyte.write( ownerByte[0] );
		sendbyte.write( ownerByte[1] );
		sendbyte.write( ownerByte[2] );
		sendbyte.write( (byte) sectionId );
		sendbyte.write( (byte) 0x01 );
		sendbyte.write( (byte) 0x02 );
		sendbyte.write( (byte) 0x03 );
		sendbyte.write( (byte) 0x04 );
		sendbyte.write( (byte) 0x05 );
		sendbyte.write( (byte) 0x06 );
		sendbyte.write( (byte) 0x07 );
		sendbyte.write( (byte) 0x08 );

		return sendbyte.getPacket();
	}

	public static byte[] buildPenAutoPowerSetup( boolean on )
	{
		PacketBuilder sendbyte = new PacketBuilder( 1 );
		sendbyte.setCommand( CMD.P_AutoPowerOnSet );
		sendbyte.write( (byte) (on ? 1 : 0) );

		return sendbyte.getPacket();
	}

	public static byte[] buildAutoShutdownTimeSetup( short shutdownTime )
	{
		PacketBuilder sendbyte = new PacketBuilder( 2 );
		sendbyte.setCommand( CMD.P_AutoShutdownTime );
		sendbyte.write( ByteConverter.shortTobyte( shutdownTime ), 2 );
		return sendbyte.getPacket();
	}

	public static byte[] buildPenSensitivitySetup( short sensitivity )
	{
		PacketBuilder sendbyte = new PacketBuilder( 2 );
		sendbyte.setCommand( CMD.P_PenSensitivity );
		sendbyte.write( ByteConverter.shortTobyte( sensitivity ), 2 );
		return sendbyte.getPacket();
	}

	public static byte[] buildPenBeepSetup( boolean on )
	{
		PacketBuilder sendbyte = new PacketBuilder( 1 );
		sendbyte.setCommand( CMD.P_BeepSet );
		sendbyte.write( (byte) (on ? 1 : 0) );
		return sendbyte.getPacket();
	}

	public static byte[] buildPenTipColorSetup( int color )
	{
		byte[] cbyte = ByteConverter.intTobyte( color );

		byte[] nbyte = new byte[] { cbyte[0], cbyte[1], cbyte[2], (byte) 0x01 };

		PacketBuilder sendbyte = new PacketBuilder( 4 );
		sendbyte.setCommand( CMD.P_PenColorSet );
		sendbyte.write( nbyte, 4 );

		return sendbyte.getPacket();
	}

	public static byte[] buildPasswordInput( String password )
	{
		PacketBuilder sendbyte = new PacketBuilder( 16 );
		sendbyte.setCommand( CMD.P_PasswordResponse );
		sendbyte.write( ByteConverter.stringTobyte( password ), 16 );
		return sendbyte.getPacket();
	}

	public static byte[] buildPasswordSetup( String oldPassword, String newPassword )
	{
		PacketBuilder sendbyte = new PacketBuilder( 32 );
		sendbyte.setCommand( CMD.P_PasswordSet );

		sendbyte.write( ByteConverter.stringTobyte( oldPassword ), 16 );
		sendbyte.write( ByteConverter.stringTobyte( newPassword ), 16 );

		return sendbyte.getPacket();
	}

	public interface IParsedPacketListener
	{
		public void onCreatePacket( Packet packet );
	}

	public static class PacketBuilder
	{
		byte[] packet;
		int totalLength, dataLength;
		int position = 4;

		public PacketBuilder( int length )
		{
			allocate( length );
		}

		/**
		 * write command field
		 * 
		 * @param cmd
		 */
		public void setCommand( int cmd )
		{
			packet[1] = (byte) cmd;
		}

		/**
		 * buffer allocation and set packet frame
		 * 
		 * @param length
		 */
		public void allocate( int length )
		{
			totalLength = length + 5;

			dataLength = length;

			position = 4;

			packet = new byte[this.totalLength];

			Arrays.fill( packet, (byte) PKT_EMPTY );

			packet[0] = (byte) PKT_START;
			byte[] bLength = ByteConverter.shortTobyte( (short) length );

			packet[2] = bLength[0];
			packet[3] = bLength[1];

			packet[totalLength - 1] = (byte) PKT_END;
		}

		/**
		 * write data to data field
		 * 
		 * @param buffer
		 */
		public void write( byte[] buffer )
		{
			for ( int i = 0; i < buffer.length; i++ )
			{
				packet[position++] = buffer[i];
			}
		}

		/**
		 * write data to data field (resize)
		 * 
		 * @param buffer
		 * @param valid_size
		 */
		public void write( byte[] buffer, int valid_size )
		{
			buffer = ResizeByteArray( buffer, valid_size );
			this.write( buffer );
		}

		/**
		 * write single data to data field
		 * 
		 * @param data
		 */
		public void write( byte data )
		{
			packet[position++] = data;
		}

		public byte[] ResizeByteArray( byte[] bytes, int newsize )
		{
			byte[] result = new byte[newsize];
			Arrays.fill( result, (byte) 0x00 );

			int length = newsize > bytes.length ? bytes.length : newsize;

			for ( int i = 0; i < length; i++ )
			{
				result[i] = bytes[i];
			}

			return result;
		}

		public byte[] getPacket()
		{
			return packet;
		}

		public void showPacket()
		{
			StringBuffer buff = new StringBuffer();

			for ( byte item : packet )
			{
				int int_data = (int) (item & 0xFF);
				buff.append( Integer.toHexString( int_data ) + ", " );
			}

			NLog.d( "[PacketBuilder] showPacket : " + buff.toString() );

			buff = null;
		}

		public static void showPacket( byte[] bytes )
		{
			StringBuffer buff = new StringBuffer();

			for ( byte item : bytes )
			{
				int int_data = (int) (item & 0xFF);
				buff.append( Integer.toHexString( int_data ) + ", " );
			}

			NLog.d( "[PacketBuilder] showPacket : " + buff.toString() );

			buff = null;
		}
	}
}
