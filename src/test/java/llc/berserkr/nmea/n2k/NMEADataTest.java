package llc.berserkr.nmea.n2k;

import llc.berserkr.nmea.n2k.util.ByteUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NMEADataTest {

    @Test
    public void testDecode() throws Exception {
        NMEAData data = new NMEAData();

        final byte[] bytesIn = ByteUtils.bitStringToUnsignedByteArray(
            //canid
            "00000000" +
            "00000000" +
            "00000000" +
            "00000" +
            //Data
            "111" +
            "11111111" +
            "11111111" +
            "11111111" +
            "11111111" +
            "11111111" +
            "11111111" +
            "11111111" +
            "11111" + "000"
        );

        final byte[] seperated = NMEAData.seperateData(bytesIn);

        for(int i = 0; i < seperated.length; i++) {
            assertEquals("11111111", ByteUtils.byteAsBitString(seperated[i]));
        }

    }
}
