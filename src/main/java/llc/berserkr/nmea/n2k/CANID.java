package llc.berserkr.nmea.n2k;

public class CANID {

    private static byte PRIORITY_AND = (byte) 0xE0;
    private final int priority;

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

        this.priority = rawPacket[0] & PRIORITY_AND;
    }

    public static byte[] hexStringToByteArray(String s) {

        int len = s.length();

        if(len % 2 != 0) {
            throw new RuntimeException("s must be an even-length string.");
        }

        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static void printByteAsBits(byte b) {
        StringBuilder binaryString = new StringBuilder();
        // Iterate from the most significant bit (7) down to the least significant bit (0)
        for (int i = 7; i >= 0; i--) {
            // Use bitwise AND with a mask to check if the i-th bit is set
            // The mask is 1 shifted left by i positions
            if ((b & (1 << i)) != 0) {
                binaryString.append("1");
            } else {
                binaryString.append("0");
            }
        }
        System.out.println("Byte " + b + " in binary: " + binaryString.toString());
    }

    public int getPriority() {
        return priority;
    }
}
