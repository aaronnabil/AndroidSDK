package kr.neolab.sdk.pen.bluetooth;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import kr.neolab.sdk.broadcastreceiver.BTDuplicateRemoveBroadcasterReceiver;
import kr.neolab.sdk.pen.IPenAdt;
import kr.neolab.sdk.pen.bluetooth.comm.CommProcessor;
import kr.neolab.sdk.pen.filter.Fdot;
import kr.neolab.sdk.pen.offline.OfflineFile;
import kr.neolab.sdk.pen.penmsg.IPenMsgListener;
import kr.neolab.sdk.pen.penmsg.PenMsg;
import kr.neolab.sdk.pen.penmsg.PenMsgType;
import kr.neolab.sdk.util.NLog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

/**
 * 
 * @author CHY
 * 
 */
public class BTAdt implements IPenAdt
{
	private static BTAdt myInstance = null;

	// Name for the SDP record when creating server socket
	public static final String NAME_PEN = "NWP-F";

	public static final String ALLOWED_MAC_PREFIX = "9C:7B:D2";
	public static final String DENIED_MAC_PREFIX = "9C:7B:D2:01";

	// Unique UUID for this application
	private static final UUID NeoOne_UUID = UUID.fromString( "00001101-0000-1000-8000-00805F9B34FB" );

	// Member fields
	private final BluetoothAdapter mAdapter;

	private ListenThread mListenThread;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectionThread;

	private IPenMsgListener listener = null;

	public static final int CONN_STATUS_IDLE = 0x01;
	public static final int CONN_STATUS_BINDED = 0x02;
	public static final int CONN_STATUS_ESTABLISHED = 0x03;

	private int status = CONN_STATUS_IDLE;

	private String penAddress = null;

	public static boolean allowOffline = true;

	private static final boolean USE_QUEUE = true;

	public static final int QUEUE_DOT = 1;
	public static final int QUEUE_MSG = 2;
	public static final int QUEUE_OFFLINE = 3;
	
	private Context context;

	private BTAdt()
	{
		mAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	public synchronized static BTAdt getInstance()
	{
		if ( myInstance == null )
		{
			if ( myInstance == null )
			{
				myInstance = new BTAdt();
			}
		}

		return myInstance;
	}

	@Override
	public void setContext(Context context) {
		this.context = context;

	}
	
	@Override
	public Context getContext()
	{
		return this.context;
	}

	public void setListener( IPenMsgListener listener )
	{
		this.listener = listener;
	}

	@Override
	public IPenMsgListener getListener()
	{
		return this.listener;
	}
	
	private BluetoothAdapter getBluetoothAdapter()
	{
		return myInstance.mAdapter;
	}

	@Override
	public synchronized void connect( String address )
	{
		responseMsg( new PenMsg( PenMsgType.PEN_CONNECTION_TRY ) );

		NLog.i( "[BTAdt] connect device : " + address );

		if ( !isAvailableDevice( address ) )
		{
			NLog.e( "[BTAdt] Your device is not allowed." );

			this.responseMsg( new PenMsg( PenMsgType.PEN_CONNECTION_FAILURE ) );
			return;
		}

		if ( status != CONN_STATUS_IDLE )
		{
			this.responseMsg( new PenMsg( PenMsgType.PEN_CONNECTION_FAILURE ) );
			return;
		}

		BluetoothDevice device = getBluetoothAdapter().getRemoteDevice( address );

		mConnectThread = new ConnectThread( device );
		mConnectThread.start();
	}

	public boolean isConnected()
	{
		return status == CONN_STATUS_ESTABLISHED;
	}

	@Override
	public synchronized void disconnect()
	{
		NLog.i( "[BTAdt] disconnect device" );

		if ( mConnectionThread != null )
		{
			mConnectionThread.unbind();
		}
	}

	@Override
	public synchronized void startListen()
	{
		NLog.i( "[BTAdt] start listen" );

		if ( myInstance.getBluetoothAdapter().isEnabled() && status == CONN_STATUS_IDLE )
		{
			if ( mListenThread != null )
			{
				mListenThread.cancel();
			}

			mListenThread = new ListenThread();
			mListenThread.start();
		}
	}

	private synchronized void stopListen()
	{
		NLog.i( "[BTAdt] stop listen" );

		if ( mListenThread != null )
		{
			mListenThread.cancel();
		}

		mListenThread = null;
	}

	public void endup()
	{
		NLog.i( "[BTAdt] endup" );

		if ( mConnectThread != null )
		{
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if ( mListenThread != null )
		{
			mListenThread.cancel();
			mListenThread = null;
		}
	}

	@Override
	public boolean isAvailableDevice( String mac )
	{
		return mac.startsWith( ALLOWED_MAC_PREFIX ) && !mac.startsWith( DENIED_MAC_PREFIX );
	}

	@Override
	public String getConnectedDevice()
	{
		return penAddress;
	}

	@Override
	public void inputPassword( String password )
	{
		if ( !isConnected() )
		{
			return;
		}

		mConnectionThread.getPacketProcessor().reqInputPassword( password );
	}

	@Override
	public void reqSetupPassword( String oldPassword, String newPassword )
	{
		if ( !isConnected() )
		{
			return;
		}

		mConnectionThread.getPacketProcessor().reqSetUpPassword( oldPassword, newPassword );
	}
	
	@Override
	public void reqPenStatus()
	{
		if ( !isConnected() )
		{
			return;
		}

		mConnectionThread.getPacketProcessor().reqPenStatus();
	}

	@Override
	public void reqFwUpgrade( File fwFile, String targetPath )
	{
		if ( !isConnected() )
		{
			return;
		}

		if ( !fwFile.exists() || !fwFile.canRead() )
		{
			responseMsg( new PenMsg( PenMsgType.PEN_FW_UPGRADE_FAILURE ) );
			return;
		}

		mConnectionThread.getPacketProcessor().reqPenSwUpgrade( fwFile, targetPath );
	}

	@Override
	public void reqSuspendFwUpgrade()
	{
		if ( !isConnected() )
		{
			return;
		}

		mConnectionThread.getPacketProcessor().reqSuspendPenSwUpgrade();
	}
	
	@Override
	public void reqForceCalibrate()
	{
		if ( !isConnected() )
		{
			return;
		}

		mConnectionThread.getPacketProcessor().reqForceCalibrate();
	}

	@Override
	public void reqDisplayShowString24( String showString24 )
	{
		if ( !isConnected() )
		{
			return;
		}

		mConnectionThread.getPacketProcessor().reqDisplayShowString24( showString24 );
	}

	/**
	 * Listen(Accept) Thread
	 * 
	 * @author CHY
	 * 
	 */
	private class ListenThread extends Thread
	{
		// The local server socket
		private BluetoothServerSocket mmServerSocket = null;

		public ListenThread()
		{
			NLog.i( "[BTAdt] ListenThread startup" );
			setNewServerSocket();
		}

		public void setNewServerSocket()
		{
			BluetoothServerSocket tmp = null;

			try
			{
				tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord( NAME_PEN, NeoOne_UUID );
				//tmp = mAdapter.listenUsingRfcommWithServiceRecord( NAME_PEN, NeoOne_UUID );
				NLog.d( "[BTAdt] ListenThread new BT ServerSocket assigned" );
			}
			catch ( IOException e )
			{
				NLog.e( "[BTAdt] ListenThread new BT ServerSocket assign fail", e );
			}

			mmServerSocket = tmp;
		}

		public void run()
		{
			NLog.i( "[BTAdt] ListenThread running" );

			setName( "ListenThread" );

			mAdapter.cancelDiscovery();

			BluetoothSocket socket = null;

			// Listen to the server socket if we're not connected
			while ( mmServerSocket != null )
			{
				try
				{
					NLog.d( "[BTAdt] ListenThread ready to connection" );

					if ( mmServerSocket != null )
					{
						NLog.d( "[BTAdt] Wait new connection" );
						socket = mmServerSocket.accept();
					}
				}
				catch ( IOException e )
				{
					NLog.e( "[BTAdt] ListenThread fail to listen socket", e );
					break;
				}

				if ( socket != null )
				{
					synchronized ( BTAdt.this )
					{
						if ( !isAvailableDevice( socket.getRemoteDevice().getAddress() ) )
						{
							NLog.e( "[BTAdt] Your device is not allowed." );
							continue;
						}

						NLog.d( "[BTAdt] ListenThread success to listen socket : " + socket.getRemoteDevice().getAddress() );

						bindConnection( socket );

						// socket binding 되면 listen 중지
						stopListen();
					}
				}
			}
		}

		public void cancel()
		{
			NLog.d( "[BTAdt] ListenThread cancel" );

			try
			{
				if ( mmServerSocket != null )
				{
					mmServerSocket.close();
				}

				mmServerSocket = null;
			}
			catch ( IOException e )
			{
				NLog.e( "[BTAdt] ListenThread fail to close server socket", e );
			}
		}
	}

	/**
	 * Connect Thread
	 * 
	 * @author CHY
	 * 
	 */
	private class ConnectThread extends Thread
	{
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread( BluetoothDevice device )
		{
			mmDevice = device;

			BluetoothSocket tmp = null;

			try
			{
				tmp = device.createInsecureRfcommSocketToServiceRecord( NeoOne_UUID );
				//tmp = device.createRfcommSocketToServiceRecord( NeoOne_UUID );
			}
			catch ( Exception e )
			{
				NLog.e( "[BTAdt/ConnectThread] Socket Type : create() failed", e );
			}

			mmSocket = tmp;
		}

		public void run()
		{
			NLog.d( "[BTAdt/ConnectThread] ConnectThread STARTED" );

			setName( "ConnectThread" );

			// Always cancel discovery because it will slow down a connection
			mAdapter.cancelDiscovery();

			try
			{
				// This is a blocking call and will only return on a successful
				// connection or an exception
				mmSocket.connect();
				NLog.d( "[BTAdt/ConnectThread] success to connect socket" );
			}
			catch ( IOException e )
			{
				NLog.e( "[BTAdt/ConnectThread] fail to connect socket.", e );

				try
				{
					mmSocket.close();
				}
				catch ( IOException e2 )
				{
					NLog.e( "[BTAdt/ConnectThread] fail to close socket", e2 );
				}

				responseMsg( new PenMsg( PenMsgType.PEN_CONNECTION_FAILURE ) );
				if(context != null)
				{
			    	Intent i = new Intent(BTDuplicateRemoveBroadcasterReceiver.ACTION_BT_REQ_CONNECT);
			    	i.putExtra(BTDuplicateRemoveBroadcasterReceiver.EXTRA_BT_CONNECT_PACKAGENAME, context.getPackageName());
			    	context.sendBroadcast(i);
				}

				return;
			}

			// Reset the ConnectThread because we're done
			synchronized ( BTAdt.this )
			{
				mConnectThread = null;
			}

			bindConnection( mmSocket );

			// stop listen
			stopListen();
		}

		public void cancel()
		{
			NLog.d( "[BTAdt/ConnectThread] cancel()" );

			try
			{
				mmSocket.close();
			}
			catch ( IOException e )
			{
				NLog.e( "[BTAdt/ConnectThread] fail to close socket", e );
			}
		}
	}

	public void setAllowOfflineData( boolean allow )
	{
		allowOffline = allow;
	}

	@Override
	public synchronized void setOfflineDataLocation( String path )
	{
		OfflineFile.setOfflineFilePath( path );
	}

	private void responseMsg( PenMsg msg )
	{
		if ( listener != null )
		{
			if ( USE_QUEUE )
			{
				mHandler.obtainMessage( QUEUE_MSG, msg ).sendToTarget();
			}
			else
			{
				listener.onReceiveMessage( msg );
			}
		}
	}

	private void responseDot( Fdot dot )
	{
		if ( listener != null )
		{
			if ( USE_QUEUE )
			{
				mHandler.obtainMessage( QUEUE_DOT, dot ).sendToTarget();
			}
			else
			{
				listener.onReceiveDot( dot.sectionId, dot.ownerId, dot.noteId, dot.pageId, dot.x, dot.y, dot.fx, dot.fy, dot.force, dot.timestamp, dot.dotType, dot.color );
			}
		}
	}

	/**
	 * Connection 바인드
	 * 
	 * @param socket
	 */
	private void bindConnection( BluetoothSocket socket )
	{
		NLog.i( "[BTAdt] bindConnection by BluetoothSocket : " + socket.getRemoteDevice().getAddress() );

		mConnectionThread = new ConnectedThread();

		if ( mConnectionThread.bind( socket ) )
		{
			mConnectionThread.start();
			this.onBinded( socket.getRemoteDevice().getAddress() );
		}
		else
		{
			mConnectionThread = null;
		}
	}

	private void onLostConnection()
	{
		status = CONN_STATUS_IDLE;
		penAddress = null;

		responseMsg( new PenMsg( PenMsgType.PEN_DISCONNECTED ) );

		this.startListen();
	}

	private void onBinded( String address )
	{
		status = CONN_STATUS_BINDED;
		penAddress = address;
	}

	private void onConnectionEstablished( String address )
	{
		status = CONN_STATUS_ESTABLISHED;
		penAddress = address;
	}

	/**
	 * Connection Object
	 * 
	 * @author CHY
	 * 
	 */
	public class ConnectedThread extends Thread
	{
		private BluetoothSocket mmSocket;
		private InputStream mmInStream;
		private OutputStream mmOutStream;

		private CommProcessor processor;

		private boolean isRunning = false;

		private String macAddress;

		public ConnectedThread()
		{
			processor = new CommProcessor( this );
		}

		public void stopRunning()
		{
			NLog.d( "[BTAdt/ConnectedThread] stopRunning()" );
			this.isRunning = false;
		}

		public boolean bind( BluetoothSocket socket )
		{
			mmSocket = socket;
			macAddress = mmSocket.getRemoteDevice().getAddress();

			// Get the BluetoothSocket input and output streams
			try
			{
				mmInStream = socket.getInputStream();
				mmOutStream = socket.getOutputStream();

				this.isRunning = true;

				return true;
			}
			catch ( IOException e )
			{
				NLog.e( "[BTAdt/ConnectedThread] temporary sockets is not created", e );
			}

			return false;
		}

		public void unbind()
		{
			NLog.d( "[BTAdt/ConnectedThread] unbind()" );

			processor = null;

			if ( mmSocket != null )
			{
				try
				{
					mmInStream.close();
					mmOutStream.close();
					mmSocket.close();
				}
				catch ( IOException e )
				{
					// TODO Auto-generated catch block
					NLog.e( "[BTAdt/ConnectedThread] socket closing fail at unbind time.", e );
				}
			}
			else
			{
				NLog.d( "[BTAdt/ConnectedThread] socket is null!!" );
			}

			this.stopRunning();
		}

		public String getMacAddress()
		{
			return macAddress;
		}

		public CommProcessor getPacketProcessor()
		{
			return processor;
		}

		public boolean getIsEstablished()
		{
			return status == CONN_STATUS_ESTABLISHED;
		}

		/**
		 * 접속후 연결되었을때 호출
		 */
		public void onEstablished()
		{
			onConnectionEstablished( macAddress );
		}

		public void run()
		{
			NLog.d( "[BTAdt/ConnectedThread] STARTED" );
			setName( "ConnectionThread" );

			if ( this.isRunning )
			{
				this.read();
			}
		}

		public void read()
		{
			byte[] buffer = new byte[512];
			int bytes;

			while ( this.isRunning )
			{
				try
				{
					bytes = mmInStream.read( buffer );

					if ( bytes > 0 )
					{
						processor.fill( buffer, bytes );
					}
					else if ( bytes == -1 )
					{
						this.stopRunning();
					}
				}
				catch ( IOException e )
				{
					NLog.e( "[BTAdt/ConnectedThread] ConnectedThread read IOException occured.", e );
					this.stopRunning();
					break;
				}
			}

			onLostConnection();
		}

		/**
		 * output stream 에 buffer 기록한다. (한번에 기록하는 단위를 정해서 펜의 read 속도와 맞춤)
		 * 
		 * @param buffer
		 */
		public void write( byte[] buffer )
		{
			try
			{
				mmOutStream.write( buffer );
				mmOutStream.flush();
			}
			catch ( IOException e )
			{
				NLog.e( "[BTAdt/ConnectedThread] IOException during write.", e );
				this.stopRunning();
			}
		}

		public void onCreateMsg( PenMsg msg )
		{
			responseMsg( msg );
		}

		public void onCreateDot( Fdot dot )
		{
			responseDot( dot );
		}
	}

	Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage( Message msg )
		{
			switch ( msg.what )
			{
				case QUEUE_DOT:
				{
					Fdot dot = (Fdot) msg.obj;
					listener.onReceiveDot( dot.sectionId, dot.ownerId, dot.noteId, dot.pageId, dot.x, dot.y, dot.fx, dot.fy, dot.force, dot.timestamp, dot.dotType, dot.color );
				}
					break;

				case QUEUE_MSG:
				{
					PenMsg pmsg = (PenMsg) msg.obj;
					listener.onReceiveMessage( pmsg );
				}
					break;
			}
		}
	};

	@Override
	public void reqAddUsingNote( int sectionId, int ownerId, int[] noteIds )
	{
		if ( !isConnected() )
		{
			return;
		}

		mConnectionThread.getPacketProcessor().reqAddUsingNote( sectionId, ownerId, noteIds );
	}

	@Override
	public void reqAddUsingNote( int sectionId, int ownerId )
	{
		if ( !isConnected() )
		{
			return;
		}

		mConnectionThread.getPacketProcessor().reqAddUsingNote( sectionId, ownerId );
	}

	@Override
	public void reqAddUsingNoteAll()
	{
		if ( !isConnected() )
		{
			return;
		}

		mConnectionThread.getPacketProcessor().reqAddUsingNoteAll();
	}

	@Override
	public void reqOfflineData( int sectionId, int ownerId, int noteId )
	{
		if ( !isConnected() )
		{
			return;
		}

		mConnectionThread.getPacketProcessor().reqOfflineData( sectionId, ownerId, noteId );
	}
	
	@Override
	public void reqOfflineDataList()
	{
		if ( !isConnected() || !allowOffline)
		{
			return;
		}

		mConnectionThread.getPacketProcessor().reqOfflineDataList();
	}

	@Override
	public void removeOfflineData( int sectionId, int ownerId )
	{
		if ( !isConnected() )
		{
			return;
		}

		mConnectionThread.getPacketProcessor().reqOfflineDataRemove( sectionId, ownerId );
	}
	
	@Override
	public void reqSetupAutoPowerOnOff( boolean setOn )
	{
		if ( !isConnected() )
		{
			return;
		}

		mConnectionThread.getPacketProcessor().reqAutoPowerSetupOnOff( setOn );
	}

	@Override
	public void reqSetupPenBeepOnOff( boolean setOn )
	{
		if ( !isConnected() )
		{
			return;
		}

		mConnectionThread.getPacketProcessor().reqPenBeepSetup( setOn );
	}

	@Override
	public void reqSetupPenTipColor( int color )
	{
		if ( !isConnected() )
		{
			return;
		}

		mConnectionThread.getPacketProcessor().reqSetupPenTipColor( color );
	}

	@Override
	public void reqSetupAutoShutdownTime( short minute )
	{
		if ( !isConnected() )
		{
			return;
		}

		mConnectionThread.getPacketProcessor().reqSetAutoShutdownTime( minute );
	}

	@Override
	public void reqSetupPenSensitivity( short level )
	{
		if ( !isConnected() )
		{
			return;
		}

		mConnectionThread.getPacketProcessor().reqSetPenSensitivity( level );
	}

}
