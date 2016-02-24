package kr.neolab.sdk.pen.bluetooth.lib;

/**
 * Protocol Command
 * A : to Activity
 * P : to Pen
 * 
 * @author CHY
 *
 */
public class CMD 
{
    public static final int A_PenOnState                 = 0x01;
    public static final int P_PenOnResponse              = 0x02;
    public static final int P_RTCset                     = 0x03;
    public static final int A_RTCsetResponse             = 0x04;
    public static final int P_Alarmset                   = 0x05;
    public static final int A_AlarmResponse              = 0x06;
    public static final int P_ForceCalibrate             = 0x07;
    public static final int A_ForceCalibrateResponse     = 0x08;
    
    public static final int P_AutoShutdownTime           = 0x09;
    public static final int A_AutoShutdownTimeResponse   = 0x0A;
    public static final int P_PenSensitivity             = 0x2C;
    public static final int A_PenSensitivityResponse     = 0x2D;
    public static final int P_PenColorSet                = 0x28;
    public static final int A_PenColorSetResponse        = 0x29;
    public static final int P_AutoPowerOnSet             = 0x2A;
    public static final int A_AutoPowerOnResponse        = 0x2B;
    public static final int P_BeepSet                    = 0x2E;
    public static final int A_BeepSetResponse            = 0x2F;
    
    public static final int P_UsingNoteNotify            = 0x0B;
    public static final int A_UsingNoteNotifyResponse    = 0x0C;

    public static final int A_PasswordRequest            = 0x0D;
    public static final int P_PasswordResponse           = 0x0E;
    public static final int P_PasswordSet                = 0x0F;
    public static final int A_PasswordSetResponse        = 0x10;
    
    public static final int A_Echo                       = 0x09;
    public static final int P_EchoResponse               = 0x0A;
    public static final int A_DotData                    = 0x11;
    public static final int A_DotIDChange                = 0x12;
    public static final int A_DotUpDownData              = 0x13;
    public static final int P_DotUpDownResponse          = 0x14;
    public static final int A_DotIDChange32              = 0x15;
    public static final int A_DotUpDownDataNew           = 0x16;
    public static final int P_PenStatusRequest           = 0x21;
    
    public static final int A_PenStatusResponse          = 0x25;
    public static final int P_PenStatusSetup             = 0x26;
    public static final int A_PenStatusSetupResponse     = 0x27;
    
    public static final int A_OfflineInfo                = 0x41;
    public static final int P_OfflineInfoResponse        = 0x42;
    public static final int A_OfflineChunk               = 0x43;
    public static final int P_OfflineChunkResponse       = 0x44;
    public static final int P_OfflineNoteList            = 0x45;
    public static final int A_OfflineNoteListResponse    = 0x46;
    public static final int P_OfflineDataRequest         = 0x47;
    public static final int A_OfflineResultResponse 	 = 0x48;
    public static final int A_OfflineDataInfo            = 0x49;
    public static final int P_OfflineDataRemove          = 0x4A;
    public static final int A_OfflineDataRemoveResponse  = 0x4B;
    
    public static final int P_PenSWUpgradeCommand        = 0x51;
    public static final int A_PenSWUpgradeRequest        = 0x52;
    public static final int P_PenSWUpgradeResponse       = 0x53;
    public static final int A_PenSWUpgradeStatus         = 0x54;
    public static final int A_PenDebug                   = 0xE5;
    public static final int P_ShowText                   = 0x61;
}
