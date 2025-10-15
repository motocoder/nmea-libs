package llc.berserkr.nmea.n2k;

import llc.berserkr.nmea.n2k.util.ByteUtils;

public class CANID {

    private static byte PRIORITY_AND =  ByteUtils.bitStringToUnsignedByteArray("11100000")[0];//(byte) 0xE0;
    private static byte RESERVED_AND = ByteUtils.bitStringToUnsignedByteArray("00010000")[0];//(byte) 0x10;
    private static byte DATAPAGE_AND = ByteUtils.bitStringToUnsignedByteArray("00001000")[0];//(byte) 0x08;
    private static byte PDU_SHIFT_AND = ByteUtils.bitStringToUnsignedByteArray("00011111")[0];//(byte) 0x1F;
    private final byte priority;
    private final boolean reserved;
    private final boolean datapage;
    private final byte pduFormat;
    private final byte sourceAddress;

    /*

    ------------- 3 bits for priority
    1/28
    2/27
    3/26
    ------------- 18 bits for PGN
    4/25 * Reserved ( 1 bit )
    5/24 * Data Page ( 1 bit )
    6/23 * PDU Format (PF) ( 8 bits ) (if PF < 240 message is PDU1 which means it's addressable message PS contains destination address )
    7/22 ( if PF is >= 240 then message is a PDU2 which means it is a broadcast message so the PS contains the group extension
    8/21
    9/20
    10/19
    11/18
    12/17
    13/16
    14/15 * PDU Specific (PS) ( 8 bit )
    15/14
    16/13
    17/12
    18/11
    19/10
    20/9
    21/8
    -------------
    22/7 - Source address
    23/6
    24/5
    25/4
    26/3
    27/2
    28/1
    ------------- data ( 0 - 64 bits )
    * bit startt 4 length 8 (Suspect Parameter Number (SPN) 1)
    * bit start 24 length 16 SPN2
    * SPN are where the indentifiers for the data are stored
     */

    public CANID(final byte[] rawPacket) {

        this.priority = (byte)(rawPacket[0] & PRIORITY_AND);
        this.reserved = (rawPacket[0] & RESERVED_AND) == 16;
        this.datapage = (rawPacket[0] & DATAPAGE_AND) == 8;

        {
            //after shift we are using the left 3 bits, everything else is zero
            int shifted = (rawPacket[0] << 5) & PRIORITY_AND; // & 0XE0 makes everything right of the 3rd bit zero

            int shiftedRight = (rawPacket[1] >> 3) & PDU_SHIFT_AND;

            //take left 3 bites from shifted, right 5 bits from shiftedRight and make the pdu value
            this.pduFormat = (byte)(shifted | shiftedRight);

        }

        {

            int shiftedLeft = (rawPacket[1] << 5) & PRIORITY_AND; //re-using priority shift

            int shiftedRight = (rawPacket[2] >>> 3) & PDU_SHIFT_AND; //re-using pdu shift

            this.sourceAddress = (byte)(shiftedLeft | shiftedRight);

        }


    }

    public boolean getReserved() {
        return reserved;
    }

    public boolean getDatapage() {
        return datapage;
    }

    public int getPduFormat() {
        return pduFormat;
    }

    public int getSourceAddress() {
        return sourceAddress;
    }
    public byte getPriority() {
        return priority;
    }
}
