package com.zhm.util.serial;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;

/**
 * 数字类型序列化
 * 
 * @author zhouqi.zhm
 * 
 */
public class NumberSerial {

	
	public static int variableLengthSize(int value) {
		int size = 1;
		while ((value & (~0x7f)) != 0) {
			value >>>= 7;
			size++;
		}
		return size;
	}

	public static int variableLengthSize(long value) {
		int size = 1;
		while ((value & (~0x7f)) != 0) {
			value >>>= 7;
			size++;
		}
		return size;
	}

	public static void writeVariableLengthInt(int value, DataOutputStream sliceOutput)throws Exception {
		int highBitMask = 0x80;
		if (value < (1 << 7) && value >= 0) {
			sliceOutput.writeByte(value);
		} else if (value < (1 << 14) && value > 0) {
			sliceOutput.writeByte(value | highBitMask);
			sliceOutput.writeByte(value >>> 7);
		} else if (value < (1 << 21) && value > 0) {
			sliceOutput.writeByte(value | highBitMask);
			sliceOutput.writeByte((value >>> 7) | highBitMask);
			sliceOutput.writeByte(value >>> 14);
		} else if (value < (1 << 28) && value > 0) {
			sliceOutput.writeByte(value | highBitMask);
			sliceOutput.writeByte((value >>> 7) | highBitMask);
			sliceOutput.writeByte((value >>> 14) | highBitMask);
			sliceOutput.writeByte(value >>> 21);
		} else {
			sliceOutput.writeByte(value | highBitMask);
			sliceOutput.writeByte((value >>> 7) | highBitMask);
			sliceOutput.writeByte((value >>> 14) | highBitMask);
			sliceOutput.writeByte((value >>> 21) | highBitMask);
			sliceOutput.writeByte(value >>> 28);
		}
	}

	public static void writeVariableLengthLong(long value,
			DataOutputStream sliceOutput) throws Exception{
		// while value more than the first 7 bits set
		while ((value & (~0x7f)) != 0) {
			sliceOutput.writeByte((int) ((value & 0x7f) | 0x80));
			value >>>= 7;
		}
		sliceOutput.writeByte((int) value);
	}

	public static int readVariableLengthInt(DataInputStream sliceInput)throws Exception {
		int result = 0;
		for (int shift = 0; shift <= 28; shift += 7) {
			int b = sliceInput.readUnsignedByte();

			// add the lower 7 bits to the result
			result |= ((b & 0x7f) << shift);

			// if high bit is not set, this is the last byte in the number
			if ((b & 0x80) == 0) {
				return result;
			}
		}
		throw new NumberFormatException(
				"last byte of variable length int has high bit set");
	}

	public static int readVariableLengthInt(ByteBuffer sliceInput) {
		int result = 0;
		for (int shift = 0; shift <= 28; shift += 7) {
			int b = sliceInput.get();

			// add the lower 7 bits to the result
			result |= ((b & 0x7f) << shift);

			// if high bit is not set, this is the last byte in the number
			if ((b & 0x80) == 0) {
				return result;
			}
		}
		throw new NumberFormatException(
				"last byte of variable length int has high bit set");
	}

	public static long readVariableLengthLong(DataInputStream sliceInput) throws Exception{
		long result = 0;
		for (int shift = 0; shift <= 63; shift += 7) {
			long b = sliceInput.readUnsignedByte();

			// add the lower 7 bits to the result
			result |= ((b & 0x7f) << shift);

			// if high bit is not set, this is the last byte in the number
			if ((b & 0x80) == 0) {
				return result;
			}
		}
		throw new NumberFormatException(
				"last byte of variable length int has high bit set");
	}
	/**
	 * 定长序列化
	 * @param number
	 * @return
	 */
	public static  byte[] encodeUserId(long number){
		byte[] writeBuffer=new byte[8];
		writeBuffer[0] = (byte)(number >>> 56);
        writeBuffer[1] = (byte)(number >>> 48);
        writeBuffer[2] = (byte)(number >>> 40);
        writeBuffer[3] = (byte)(number >>> 32);
        writeBuffer[4] = (byte)(number >>> 24);
        writeBuffer[5] = (byte)(number >>> 16);
        writeBuffer[6] = (byte)(number >>>  8);
        writeBuffer[7] = (byte)(number >>>  0);						
		return writeBuffer;
	}
	
	public static  long decodeLong(byte[] bytes){
		if(bytes.length!=8)return -1;
		byte readBuffer[]=bytes;
		return (((long)readBuffer[0] << 56) +
                ((long)(readBuffer[1] & 255) << 48) +
		((long)(readBuffer[2] & 255) << 40) +
                ((long)(readBuffer[3] & 255) << 32) +
                ((long)(readBuffer[4] & 255) << 24) +
                ((readBuffer[5] & 255) << 16) +
                ((readBuffer[6] & 255) <<  8) +
                ((readBuffer[7] & 255) <<  0));
		
	}
}
