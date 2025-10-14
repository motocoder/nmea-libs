/*
 * Sixbit.java
 * Copyright (C) 2015 Lázár József
 *
 * This file is part of Java Marine API.
 * <http://ktuukkan.github.io/marine-api/>
 *
 * Java Marine API is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Java Marine API is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Java Marine API. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.marineapi.ais.util;

/**
 * AIS characters are encoded as 6 bit values concatenated into a bit array.
 * This class implements the higher level access to this bit array, storing
 * and retrieveing characters, integers, etc.
 * 
 * @author Lázár József
 */
public class Sixbit {

	/** Number of bits per character */
	public static final int BITS_PER_CHAR = 6;

	private final String	fPayload;
	private BitVector		fBitVector;
	private final int	    fFillBits; // Number of padding bits at end

    /**
     * Constructor.
     *
     * @param payload 6-bit encoded String
     * @param fillBits Fill bits to be added
     */
	public Sixbit(String payload, int fillBits) {
        if (payload == null || payload.isEmpty()) {
            throw new IllegalArgumentException("Message payload cannot be null or empty");
        }
        if (fillBits < 0) {
            throw new IllegalArgumentException("Fill bits cannot be negative");
        }
		fPayload = payload;
		if (!isValidString(fPayload))
			throw new IllegalArgumentException("Invalid payload characters");

		fBitVector = new BitVector(fPayload.length() * BITS_PER_CHAR);
		for (int i = 0; i < fPayload.length(); i++) {
			char c = fPayload.charAt(i);
			int b = transportToBinary(c);
			convert(b, i * BITS_PER_CHAR, BITS_PER_CHAR);
		}
		fFillBits = fillBits;
	}

	private void convert(int value, int from, int length) {
		int index = from + BITS_PER_CHAR;
		while (value != 0L && length > 0) {
			if (value % 2L != 0) {
				fBitVector.set(index);
			}
			index--;
			value = value >>> 1;
			length--;
		}
	}

	/**
	 * Returns a {@link BitVector} for specified range.
	 *
	 * @param from Start index
	 * @param to End index
	 * @return BitVector for specified range.
	 */
	public BitVector get(int from, int to) {
		return fBitVector.get(from, to);
	}

	private boolean isValidCharacter(char ascii) {
		return ((ascii >= 0x30) && (ascii <= 0x77)) &&
				((ascii <= 0x57) || (ascii >= 0x60));
	}

	private boolean isValidString(String bits) {
		boolean valid = true;
		for (int i = 0; i < bits.length(); i++) {
			if (!isValidCharacter(bits.charAt(i))) {
				valid = false;
				break;
			}
		}
		return valid;
	}

	/**
	 * Returns the payload length.
	 *
	 * @return Number of payload bits.
	 */
	public int length() {
		return fPayload.length() * BITS_PER_CHAR - fFillBits;
	}

	/**
	 * Decode a transport character to a binary value.
	 * 
	 * @param ascii character to decode
	 * @return decoded value in 6-bit binary representation
	 */
	private int transportToBinary(char ascii) {
		if (!isValidCharacter(ascii))
			throw new IllegalArgumentException("Invalid transport character: " + ascii);
		int retval;
		if (ascii < 0x60)
			retval = (ascii - 0x30);
		else
			retval = (ascii - 0x38);
		return retval;
	}

	/** Decode a binary value to a content character.
	 * 
	 * @param value to be decoded
	 * @return corresponding ASCII value (0x20-0x5F)
	 *
	 * This function returns the content character as encoded in binary value. 
	 * See table 44 (page 100) of Rec. ITU-R M.1371-4.
	 * 
	 * This function is used to convert binary data to ASCII. This is 
	 * different from the 6-bit ASCII to binary conversion for VDM 
	 * messages; it is used for strings within the datastream itself.
	 * eg. Ship Name, Callsign and Destination.
	 */
	private char binaryToContent(int value) {
		if (value < 0x20)
			return (char)(value + 0x40);
		else
			return (char)value;
	}

	/**
	 * Return bit as boolean from the bit vector.
	 *
	 * @param index start index of bit
	 * @return Boolean value for specified index
	 */
	public boolean getBoolean(int index) {
		return fBitVector.getBoolean(index);
	}

	/**
	 * Returns the requested bits interpreted as an integer (MSB first) from the message.
	 * 
	 * @param from begin index (inclusive)
	 * @param to end index (inclusive)
	 * @return unsigned int value
	 */
	public int getInt(int from, int to) {
		return fBitVector.getUInt(from, to);
	}

	/**
	 * Get 8-bit integer value.
	 *
	 * @param from Start index
	 * @param to End index
	 * @return Integer value
	 */
	public int getAs8BitInt(int from, int to) {
		return fBitVector.getAs8BitInt(from, to);
	}

	/**
	 * Get 17-bit integer value.
	 *
	 * @param from Start index
	 * @param to End index
	 * @return Integer value
	 */
	public int getAs17BitInt(int from, int to) {
		return fBitVector.getAs17BitInt(from, to);
	}

	/**
	 * Get 18-bit integer value.
	 *
	 * @param from Start index
	 * @param to End index
	 * @return Integer value
	 */
	public int getAs18BitInt(int from, int to) {
		return fBitVector.getAs18BitInt(from, to);
	}

	/**
	 * Get 27-bit integer value.
	 *
	 * @param from Start index
	 * @param to End index
	 * @return Integer value
	 */
	public int getAs27BitInt(int from, int to) {
		return fBitVector.getAs27BitInt(from, to);
	}

	/**
	 * Get 28-bit integer value.
	 *
	 * @param from Start index
	 * @param to End index
	 * @return Integer value
	 */
	public int getAs28BitInt(int from, int to) {
		return fBitVector.getAs28BitInt(from, to);
	}

	/**
	 * Return string from bit vector
	 *
	 * @param fromIndex begin index (inclusive)
	 * @param toIndex end index (inclusive)
	 * @return String value
	 */
	public String getString(int fromIndex, int toIndex) {
		StringBuilder sb = new StringBuilder();
		for (int i = fromIndex; i < toIndex; i += BITS_PER_CHAR) {
			int value = getInt(i, i + BITS_PER_CHAR);
			sb.append(binaryToContent(value));
		}
		return stripAtSigns(sb.toString());
	}

	/**
	 * Strips the @ characters from specified String.
	 */
	private String stripAtSigns(String orig) {
		int end = orig.length() - 1;
		for (int i = orig.length() - 1; i >= 0; i--) {
			if (orig.charAt(i) != '@') {
				end = i;
				break;
			}
		}
		return orig.substring(0, end + 1);
	}

    /**
     * Returns the full message String in sixbit encoded format.
     *
     * @return Sixbit encoded String.
     */
    public String getPayload() {
        return fPayload;
    }
}
