package llc.berserkr.nmea.n2k;


import llc.berserkr.nmea.n2k.util.ByteUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CANIDTest {

    @Test
    public void testCanID() {

        {

            //when priority bits are 1 and everything else isn't or is make sure it comes out -32
            byte bite1 = (byte) 0xFF;
            byte bite2 = (byte) 0xE0;

            CANID canid1 = new CANID(Arrays.copyOf(new byte[]{bite1}, 100));
            CANID canid2 = new CANID(Arrays.copyOf(new byte[]{bite2}, 100));

            assertEquals(canid1.getPriority(), canid2.getPriority());
            assertEquals(-32, canid1.getPriority());

        }

        {

            //when priority bits are zero and everything else isn't or is make sure it comes out zero
            byte bite1 = (byte) 0x00;
            byte bite2 = (byte) 0x1F;

            CANID canid1 = new CANID(Arrays.copyOf(new byte[]{bite1}, 100));
            CANID canid2 = new CANID(Arrays.copyOf(new byte[]{bite2}, 100));

            assertEquals(canid1.getPriority(), canid2.getPriority());
            assertEquals(0, canid1.getPriority());

        }

        {
            byte bite1 = (byte) 0xFF; //everything 1
            byte bite2 = (byte) 0x10; //only reserved 1

            CANID canid1 = new CANID(Arrays.copyOf(new byte[]{bite1}, 100));
            CANID canid2 = new CANID(Arrays.copyOf(new byte[]{bite2}, 100));

            assertEquals(canid1.getReserved(), canid2.getReserved());
            assertEquals(true, canid1.getReserved());
        }

        {
            byte bite1 = (byte) 0x00; //everything 0
            byte bite2 = (byte) 0xEF; //only reserved 0

            CANID canid1 = new CANID(Arrays.copyOf(new byte[]{bite1}, 100));
            CANID canid2 = new CANID(Arrays.copyOf(new byte[]{bite2}, 100));

            assertEquals(canid1.getReserved(), canid2.getReserved());
            assertEquals(false, canid1.getReserved());

        }

        {
            byte bite1 = (byte) 0xFF; //everything 1
            byte bite2 = (byte) 0x08; //only datapage 1

            CANID canid1 = new CANID(Arrays.copyOf(new byte[]{bite1}, 100));
            CANID canid2 = new CANID(Arrays.copyOf(new byte[]{bite2}, 100));

            assertEquals(canid1.getDatapage(), canid2.getDatapage());
            assertEquals(true, canid1.getDatapage());
        }

        {
            byte bite1 = (byte) 0x00; //everything 0
            byte bite2 = (byte) 0xF7; //only datapage 0

            CANID canid1 = new CANID(Arrays.copyOf(new byte[]{bite1}, 100));
            CANID canid2 = new CANID(Arrays.copyOf(new byte[]{bite2}, 100));

            assertEquals(canid1.getDatapage(), canid2.getDatapage());
            assertEquals(false, canid1.getDatapage());

        }

        {

            byte[] pduPacket = ByteUtils.bitStringToUnsignedByteArray("00000111"+ "11111000" + "00000000"); //only pdu is 1s

            final CANID canid = new CANID(pduPacket);
            assertEquals(false, canid.getDatapage());
            assertEquals(false, canid.getReserved());
            assertEquals((byte)0x00, canid.getPriority());
            assertEquals((byte)0xFF, canid.getPduFormat());
            assertEquals((byte)0x00, canid.getSourceAddress());

        }

        {

            byte[] pduPacket = ByteUtils.bitStringToUnsignedByteArray("00000000"+ "00000000" + "00000000"); //only pdu is 1s

            final CANID canid = new CANID(pduPacket);

            assertEquals(false, canid.getDatapage());
            assertEquals(false, canid.getReserved());
            assertEquals(0, canid.getPriority());
            assertEquals((byte)0x00, canid.getPduFormat());
              assertEquals((byte)0x00, canid.getSourceAddress());

        }

        {

            byte[] sourcePacket = ByteUtils.bitStringToUnsignedByteArray("00000000"+ "00000111" + "11111000"); //only pdu is 1s

            final CANID canid = new CANID(sourcePacket);

            assertEquals(false, canid.getDatapage());
            assertEquals(false, canid.getReserved());
            assertEquals(0, canid.getPriority());
            assertEquals((byte)0x00, canid.getPduFormat());
            assertEquals((byte)0xFF, canid.getSourceAddress());

        }

        {

            byte[] sourcePacket = ByteUtils.bitStringToUnsignedByteArray("00000000"+ "00000000" + "00000000"); //only pdu is 1s

            final CANID canid = new CANID(sourcePacket);

            assertEquals(false, canid.getDatapage());
            assertEquals(false, canid.getReserved());
            assertEquals(0, canid.getPriority());
            assertEquals((byte)0x00, canid.getPduFormat());
            assertEquals((byte)0x00, canid.getSourceAddress());

        }


    }
}
