package llc.berserkr.nmea.n2k.util;

import java.util.ArrayList;
import java.util.List;

public class ByteUtils {

    public static void copyBitsIntoByteArray(
        final byte[] targetArray,
        final int targetBitIndex,
        final int sourceValue,
        final int numBitsToCopy
    ) {

        if (targetArray == null || numBitsToCopy < 0 || numBitsToCopy > Integer.SIZE || targetBitIndex < 0) {
            throw new IllegalArgumentException("Invalid input parameters.");
        }

        for (int i = 0; i < numBitsToCopy; i++) {
            int currentSourceBitIndex = i;
            int currentTargetBitIndex = targetBitIndex + i;

            // Determine the byte and bit within that byte in the target array
            int targetByteIndex = currentTargetBitIndex / 8;
            int bitInTargetByte = currentTargetBitIndex % 8;

            if (targetByteIndex >= targetArray.length) {
                throw new IndexOutOfBoundsException("Target array is too small to accommodate all bits.");
            }

            // Get the value of the current bit from the source
            boolean sourceBitValue = ((sourceValue >> currentSourceBitIndex) & 1) == 1;

            // Set or clear the corresponding bit in the target byte array
            if (sourceBitValue) {
                targetArray[targetByteIndex] |= (1 << bitInTargetByte); // Set the bit
            } else {
                targetArray[targetByteIndex] &= ~(1 << bitInTargetByte); // Clear the bit
            }
        }
    }

    public static int reverseBytesManually(int value) {
        return ((value & 0xFF) << 24) |
                ((value >> 8) & 0xFF) << 16 |
                ((value >> 16) & 0xFF) << 8 |
                ((value >> 24) & 0xFF);
    }

    public static byte[] bitStringToUnsignedByteArray(String bitString) {
        if (bitString == null || bitString.length() % 8 != 0) {
            throw new IllegalArgumentException("Bit string must not be null and its length must be a multiple of 8.");
        }

        List<Byte> byteList = new ArrayList<>();
        for (int i = 0; i < bitString.length(); i += 8) {
            String byteSegment = bitString.substring(i, i + 8);
            // Parse the 8-bit segment as a binary number and cast to byte
            byte b = (byte) Integer.parseInt(byteSegment, 2);
            byteList.add(b);
        }

        // Convert the List<Byte> to a byte[]
        byte[] byteArray = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            byteArray[i] = byteList.get(i);
        }
        return byteArray;
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

    public static String byteAsBitString(byte b) {
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

        return binaryString.toString();
    }

    public static void printByteAsBits(byte b) {
        System.out.println("Byte " + b + " in binary: " + byteAsBitString(b));
    }

    public static byte[] reverse(byte [] data) {
        byte [] bytes = data.clone();

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (Integer.reverse(bytes[i]) >>> 24);
        }

        return bytes;
    }

    public static byte reverse(byte data) {
        return (byte) (Integer.reverse(data) >>> 24);
    }

}
