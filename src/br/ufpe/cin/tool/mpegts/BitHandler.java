/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufpe.cin.tool.mpegts;


public class BitHandler {

	private byte[] buffer = null;
	
	private final int MAX_LOOP = 30;

	private int bufSize		 = 0,
				readPtr		 = 0,
				readCount	 = 0,
				actualLoop	 = 0,
				remainingBits = 0, 
				lastByte 	 = 0;

	public BitHandler(byte[] newBuffer) {
		buffer = newBuffer;
		if (newBuffer != null)
			bufSize = buffer.length;
	}
	
	public byte[] getBuffer() {
		return this.buffer;
	}
	
	public BitHandler (BitHandler bitwise) {
		
		this.buffer = bitwise.buffer;
		this.bufSize = bitwise.bufSize;
		this.lastByte = bitwise.lastByte;
		this.actualLoop = bitwise.actualLoop;
		this.readCount = bitwise.readCount;
		this.readPtr = bitwise.readPtr;
		this.remainingBits = bitwise.remainingBits;
	}
	
	public void setOffset(int offset) {
		readPtr = offset;
	}
	
    public static int convertBits(int bufferNumber, int offset, int count) {
        if (bufferNumber < 0)
                bufferNumber += 256;
        int mask = (1 << count) - 1;
        if (offset >= count) {
                mask = mask << (offset - count);
                return (bufferNumber & mask) >>> (offset - count);
        }
        return -1;
}

	public static int toInt(byte b) {
		int i = b;
		if (i < 0)
			i += 256;	// Integer can't be negative. -256 is the lowest possible value
		return i;
	}

	public int pop() {
		int i = toInt(buffer[readPtr]);

		if (readPtr < bufSize - 1) {
			readPtr++;
		} else {
			loopCounter();
		}
		return i;
	}

	private void loopCounter() {
		actualLoop++;
		if (actualLoop == MAX_LOOP)
			throw (new RuntimeException("Infinite loop err:"));
	}

	public int pop32Bits() {
		return (pop16Bits() << 16) | pop16Bits();
	}

	public int pop16Bits() {
		return (pop() << 8) | pop();
	}

	public String toHex(int i) {
		String value = Integer.toHexString(i);
		if (value.length() % 2 == 1)
			return "0x0" + value;
		return "0x" + value;
	}

	public int hexToInt(String hex) {
		hex = hex.substring(hex.indexOf("x")+1);
		int value = Integer.valueOf(hex);
		return value;
	}
	
	public int pop(int bytes) {
		int i = toInt(buffer[readPtr]);

		if (readPtr <= bufSize - bytes) {
			readPtr += bytes;
		} else {
			loopCounter();
		}

		return i;
	}


	public void mark() {
		readCount = readPtr;
	}
	
	
	/* --------------------
	 * Getters and Setters
	 * --------------------*/

	public int getBufferSize() {
		return bufSize;
	}

	public int getAvailableSize() {
		return bufSize - readPtr - 1;
	}

	public void setBufferSize(int size) {
		bufSize = size;
		if (size > buffer.length)
			bufSize = buffer.length;
	}

	public int getByteCount() {
		return readPtr - readCount;
	}

	public BitHandler getCopy(int size) {
		return getCopy(readPtr, size);
	}

	public int getAbsolutePosition() {
		return readPtr;
	}

	public BitHandler getCopy(int sourceIndx, int size) {
		if (sourceIndx > bufSize)
			sourceIndx = bufSize;
		if (sourceIndx + size > bufSize)
			size = bufSize - sourceIndx;
		byte[] ba = new byte[size];
		System.arraycopy(buffer, sourceIndx, ba, 0, size);
		return new BitHandler(ba);
	}
}
