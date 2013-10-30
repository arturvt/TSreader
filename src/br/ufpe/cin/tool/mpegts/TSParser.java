package br.ufpe.cin.tool.mpegts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class TSParser {
	
	public static final int SYNC_BYTE = 0x47;
	public static final int TS_PACKET_SIZE = 188;
	public static final int TMCC_SIZE = 16;

	private byte[] buffer;
	private int packetSize;
	
	private int transport_error_indicator, // 1 bit
	current_pid; // 13 bits Packet ID - one for each ES

	private long byteCount = 0, packetCount = 0, errorCount = 0, bigPackets = 0;
	
	private String pids = new String();
	
	private int pidArray[] = new int[8192]; // 8192 = 2^13 the maximum of bits
										// in a PID
	
	private boolean changed = false;
	
	private ParsePacket parser = null;

	
	private FileInputStream fileInputStream = null;

	public TSParser(File file, int packetSize) throws FileNotFoundException {
		super();
		this.fileInputStream = new FileInputStream(file);
		this.parser = new ParsePacket();
		this.packetSize = packetSize;
		buffer = new byte[packetSize];
	}

	public ArrayList<EPGValues> getEITList() {
		return this.parser.getEitlList();
	}

	public boolean startReadTS() {
		try {
			if (fileInputStream == null) {
				return false;
			}

			packetCount = 0;
			byteCount = 0;

			try {
				readTS();
				if (this.parser.getEitlList() != null && this.parser.getEitlList().size() > 0 ) {
//					ResourceManager.getInstance().saveCurrentEitTable(this.parser.getEitlList());
				}
				fileInputStream.close();
				printValues();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (RuntimeException e) {
			System.out.println("RuntimeException!Msg");
		}

		return true;
	}
	
	
	private void printValues() {
		System.out.println("PrintValues");
		for (int j = 0; j < this.pidArray.length; j++) {
			setPids(getPids() + String.format("+%d ", j));

		}
		System.out.println(" PacketCounter:" + packetCount);
		System.out.println(" ErrorCounter:" + errorCount);
		System.out.println("BigPackets: "+bigPackets);

	}
	
	public void setPids(String pids) {
		this.pids = pids;
	}

	public String getPids() {
		return pids;
	}
	private void readTS() throws IOException {
		while (fileInputStream.read(buffer) != -1) {
			parsePacket(buffer);
			byteCount+=buffer.length;
		}
	}
	
	private void colectPids(byte[] content) {
		transport_error_indicator = BitHandler.convertBits(content[0], 8, 1);
		current_pid = ((content[1] << 8) & 0x1f00) | (content[2] & 0xff);

		if (transport_error_indicator == 0) { // Check if the actual
			this.pidArray[current_pid] = 1;
		}
	}
	
	public void parsePacket(byte[] buffer) throws IOException {
		packetCount++;
		if (buffer[0] != SYNC_BYTE) {
			errorCount++;
		} else {
			colectPids(buffer);
			parser.parsePacket(buffer, changed);
		}

	}

	
}
