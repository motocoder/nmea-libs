package llc.berserkr.nmea.n2k;

import llc.berserkr.nmea.n2k.util.ByteUtils;

import java.util.BitSet;

public class NMEAData {


    /*

    ------------- data ( 0 - 64 bits )
    * bit start 4 length 8 (Suspect Parameter Number (SPN) 1)
            * bit start 24 length 16 SPN2
    * SPN are where the indentifiers for the data are stored

     */

    /**
     * Seperates the data from the PGN first 29 bits
     *
     * @param bytesIn
     * @return
     */
    public static byte []  seperateData(final byte[] bytesIn) {

        //TODO this will need to be converted to bitwise stuff for production
        //using inefficient string methods for prototyping right now.
        if(bytesIn.length != 12) {
            throw new IllegalArgumentException("input array must be 12 in length");
        }

        for(int i = 0; i < bytesIn.length; i++) {
            bytesIn[i] = ByteUtils.reverse(bytesIn[i]);
        }

        final BitSet bits = BitSet.valueOf(bytesIn);

        final StringBuilder builder = new StringBuilder();

        for(int i = 29; i < 93; i++) {
            if(bits.length() > i) {
                builder.append(bits.get(i) ? "1" : "0");
            }
            else {
                builder.append("0");
            }
        }

        return ByteUtils.bitStringToUnsignedByteArray(builder.toString());

    }

}
