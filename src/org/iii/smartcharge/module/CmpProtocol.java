package org.iii.smartcharge.module;

public abstract class CmpProtocol
{
	/** CMP Command **/
	static public int		msnSequence		= 0x00000000;
	static public final int	CMP_HEADER_SIZE	= 16;
	static public final int	MAX_DATA_LEN	= 2048;

	/*
	 * CMP Command set
	 */
	static public final int	generic_nack				= 0x80000000;
	static public final int	bind_request				= 0x00000001;
	static public final int	bind_response				= 0x80000001;
	static public final int	authentication_request		= 0x00000002;
	static public final int	authentication_response		= 0x80000002;
	static public final int	access_log_request			= 0x00000003;
	static public final int	access_log_response			= 0x80000003;
	static public final int	enquire_link_request		= 0x00000015;
	static public final int	enquire_link_response		= 0x80000015;
	static public final int	unbind_request				= 0x00000006;
	static public final int	unbind_response				= 0x80000006;
	static public final int	update_request				= 0x00000007;
	static public final int	update_response				= 0x80000007;
	static public final int	reboot_request				= 0x00000010;
	static public final int	reboot_response				= 0x80000010;
	static public final int	config_request				= 0x00000011;
	static public final int	config_response				= 0x80000011;
	static public final int	power_port_request			= 0x00000012;
	static public final int	power_port_response			= 0x80000012;
	static public final int	power_port_state_request	= 0x00000013;
	static public final int	power_port_state_response	= 0x00000013;

	/*
	 * CMP status set
	 */
	static public final int	STATUS_ROK			= 0x00000000;
	static public final int	STATUS_RINVMSGLEN	= 0x00000001;
	static public final int	STATUS_RINVCMDLEN	= 0x00000002;
	static public final int	STATUS_RINVCMDID	= 0x00000003;
	static public final int	STATUS_RINVBNDSTS	= 0x00000004;
	static public final int	STATUS_RALYBND		= 0x00000005;
	static public final int	STATUS_RSYSERR		= 0x00000008;
	static public final int	STATUS_RBINDFAIL	= 0x00000010;
	static public final int	STATUS_RPPSFAIL		= 0x00000011;
	static public final int	STATUS_RINVBODY		= 0x00000040;
	static public final int	STATUS_RINVCTRLID	= 0x00000041;

	static public class CMP_HEADER
	{
		int	nLength;
		int	nId;
		int	nStatus;
		int	nSequence;

		void clean()
		{
			nLength = 0;
			nId = 0;
			nStatus = 0;
			nSequence = 0;
		}
	}

}
