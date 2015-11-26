package org.iii.smartcharge.module;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import org.iii.smartcharge.common.Logs;

public class CmpHandler
{
	static public final int	UNKNOW				= 0;
	static public final int	SUCCESS				= 1;
	static public final int	ERR_PACKET_LENGTH	= -1;
	static public final int	ERR_PACKET_SEQUENCE	= -2;
	static public final int	ERR_REQUEST_FAIL	= -3;
	static public final int	ERR_SOCKET_INVALID	= -4;
	static public final int	ERR_INVALID_PARAM	= -5;

	private final String					mstrCenterIP					= "140.92.142.158";
	private final int						mnCenterPort					= 6607;
	private OnPowerStateResponseListener	onPowerStateResponseListener	= null;

	public CmpHandler()
	{

	}

	/** Response callback **/
	public static interface OnPowerStateResponseListener
	{
		void onResponse(int nResult, String strData);
	}

	public void setOnPowerStateResponseListener(CmpHandler.OnPowerStateResponseListener listener)
	{
		if (null != listener)
		{
			onPowerStateResponseListener = listener;
		}
	}

	private int getSequence()
	{
		++CmpProtocol.msnSequence;
		if (0x7FFFFFFF <= CmpProtocol.msnSequence)
		{
			CmpProtocol.msnSequence = 0x00000001;
		}
		return CmpProtocol.msnSequence;
	}

	public void powerPortSet(String strController, String strWire, String strPort, String strState)
	{
		Thread t = new Thread(
				new sendCmpRunnable(CmpProtocol.power_port_request, strController, strWire, strPort, strState));
		t.start();
	}

	public void powerPortState(String strController, String strWire)
	{
		Thread t = new Thread(
				new sendCmpRunnable(CmpProtocol.power_port_state_request, strController, strWire, null, null));
		t.start();
	}

	class sendCmpRunnable implements Runnable
	{
		private int		mnCommand		= CmpProtocol.generic_nack;
		private String	mstrController	= null;
		private String	mstrWire		= null;
		private String	mstrPort		= null;
		private String	mstrState		= null;
		Socket			socket			= null;

		public sendCmpRunnable(int nCommand, String strController, String strWire, String strPort, String strState)
		{
			mnCommand = nCommand;
			mstrController = strController;
			mstrWire = strWire;
			mstrPort = strPort;
			mstrState = strState;
		}

		@Override
		public void run()
		{
			try
			{
				InetAddress addr = InetAddress.getByName(mstrCenterIP);
				socket = new Socket();
				socket.connect(new InetSocketAddress(addr, mnCenterPort), 3000);
				// socket = new Socket(mstrCenterIP, mnCenterPort);

				switch(mnCommand)
				{
				case CmpProtocol.power_port_request:
					powerPortSet();
					break;
				case CmpProtocol.power_port_state_request:
					powerPortState();
					break;
				}
				socket.close();
				socket = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				if (null != onPowerStateResponseListener)
				{
					onPowerStateResponseListener.onResponse(ERR_SOCKET_INVALID, null);
				}
			}
		}

		private int powerPortSet() throws IOException
		{
			int nRet = 0;
			final int nSequence = getSequence();
			OutputStream outSocket = socket.getOutputStream();
			InputStream inSocket = socket.getInputStream();

			int nTotalLen = CmpProtocol.CMP_HEADER_SIZE + 1 + 1 + 1 + mstrController.length() + 1;

			ByteBuffer buf = ByteBuffer.allocate(nTotalLen);

			/** header **/
			buf.putInt(nTotalLen);
			buf.putInt(CmpProtocol.power_port_request);
			buf.putInt(CmpProtocol.STATUS_ROK);
			buf.putInt(nSequence);

			/** body **/
			buf.put(mstrWire.getBytes("US-ASCII"));
			buf.put(mstrPort.getBytes("US-ASCII"));
			buf.put(mstrState.getBytes("US-ASCII"));
			buf.put(mstrController.getBytes("US-ASCII"));
			buf.put((byte) 0);

			/** send request **/
			buf.flip();
			outSocket.write(buf.array());
			buf.clear();

			buf = ByteBuffer.allocate(CmpProtocol.CMP_HEADER_SIZE);
			nTotalLen = inSocket.read(buf.array());
			buf.rewind();
			Logs.showTrace("CMP Response Size: " + String.valueOf(nTotalLen));
			if (CmpProtocol.CMP_HEADER_SIZE == nTotalLen)
			{
				nRet = checkResponse(buf, nSequence);
				buf.order(ByteOrder.BIG_ENDIAN);
			}
			else
			{
				nRet = ERR_PACKET_LENGTH;
			}
			buf.clear();
			buf = null;

			return nRet;
		}

		private int powerPortState() throws IOException
		{
			int nRet = 0;
			final int nSequence = getSequence();
			OutputStream outSocket = socket.getOutputStream();
			InputStream inSocket = socket.getInputStream();
			String strState = "";

			int nTotalLen = CmpProtocol.CMP_HEADER_SIZE + 1 + mstrController.length() + 1;

			ByteBuffer buf = ByteBuffer.allocate(nTotalLen);

			/** header **/
			buf.putInt(nTotalLen);
			buf.putInt(CmpProtocol.power_port_state_request);
			buf.putInt(CmpProtocol.STATUS_ROK);
			buf.putInt(nSequence);

			/** body **/
			buf.put(mstrWire.getBytes("US-ASCII"));
			buf.put(mstrController.getBytes("US-ASCII"));
			buf.put((byte) 0);

			/** send request **/
			buf.flip();
			outSocket.write(buf.array());
			buf.clear();

			/** receive response **/
			buf = ByteBuffer.allocate(CmpProtocol.MAX_DATA_LEN);
			nTotalLen = inSocket.read(buf.array());
			buf.rewind();
			Logs.showTrace("CMP Response Size: " + String.valueOf(nTotalLen));
			if (CmpProtocol.CMP_HEADER_SIZE <= nTotalLen)
			{
				nRet = checkResponse(buf, nSequence);
				buf.order(ByteOrder.BIG_ENDIAN);
				if (SUCCESS == nRet)
				{
					byte[] bytes = new byte[buf.getInt(0)];
					buf.get(bytes);
					String strTemp = new String(bytes, Charset.forName("UTF-8"));
					strState = strTemp.substring(16);
					Logs.showTrace("[CMP] Power port state response body:" + strState.trim());
					bytes = null;
				}
			}
			else
			{
				nRet = ERR_PACKET_LENGTH;
			}
			buf.clear();
			buf = null;

			if (0 >= strState.length())
				nRet = ERR_INVALID_PARAM;

			if (null != onPowerStateResponseListener)
			{
				onPowerStateResponseListener.onResponse(nRet, strState.trim());
			}

			return nRet;
		}

		private int checkResponse(ByteBuffer buf, int nSequence)
		{
			int nResult = UNKNOW;

			CmpProtocol.CMP_HEADER cmpResp = new CmpProtocol.CMP_HEADER();
			buf.order(ByteOrder.BIG_ENDIAN);
			cmpResp.nLength = buf.getInt(0); // offset
			cmpResp.nId = buf.getInt(4) & 0x00ffffff;
			cmpResp.nStatus = buf.getInt(8);
			cmpResp.nSequence = buf.getInt(12);

			Logs.showTrace("CMP Response:" + String.valueOf(cmpResp.nLength) + " " + String.valueOf(cmpResp.nId) + " "
					+ String.valueOf(cmpResp.nStatus) + " " + String.valueOf(cmpResp.nSequence));
			if (cmpResp.nSequence != nSequence)
			{
				nResult = ERR_PACKET_SEQUENCE;
			}
			else
			{
				if (CmpProtocol.STATUS_ROK == cmpResp.nStatus)
				{
					nResult = SUCCESS;
				}
				else
				{
					nResult = ERR_REQUEST_FAIL;
				}
			}
			cmpResp = null;
			return nResult;
		}
	}

}
