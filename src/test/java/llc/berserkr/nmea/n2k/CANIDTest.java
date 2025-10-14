package llc.berserkr.nmea.n2k;


import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CANIDTest {

    @Test
    public void testCanID() {

        {

            byte bite1 = (byte) 0xFF;
            byte bite2 = (byte) 0xE0;

            CANID canid1 = new CANID(Arrays.copyOf(new byte [] {bite1}, 100));
            CANID canid2 = new CANID(Arrays.copyOf(new byte [] {bite2}, 100));

            assertEquals(canid1.getPriority(), canid2.getPriority());

        }

        byte bite1 = (byte) 0x1F;

    }
}
