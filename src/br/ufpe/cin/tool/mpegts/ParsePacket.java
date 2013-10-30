package br.ufpe.cin.tool.mpegts;


import java.util.ArrayList;
import java.util.Hashtable;


public class ParsePacket {

	private class PSITable {
		public int pid;
		public final String name;
		public int offset = 0;
		public byte[] fullTable = new byte[MAX_SECTION_LENGTH
				+ PACKET_HEADER_SIZE + 1 /* pointer_field */];
		public ArrayList<byte[]> headersList = new ArrayList<byte[]>();
		public int tableSpecificHeaderValue;
		public int sectionLength;
		public int sectionEnd;
		public int remaining = 0;
		public boolean crcCheck = false;
		private int dataStart;
		private int i = 0;
		private int qntOfPackets = 0;

		public PSITable(int pid, final String name) {
			this.pid = pid;
			this.name = name;
		}
	}


	public ParsePacket() {
		CRC32.makeTable();
	}
	
	private PSITable pat = new PSITable(0x00, "PAT");
	private PSITable pm1 = new PSITable(0x1fc8, "PMT");
	private PSITable pm2 = new PSITable(0x1fc9, "PMT 2");
	private PSITable pm3 = new PSITable(0x1fca, "PMT 3");
	private PSITable pm4 = new PSITable(0x1fcb, "PMT 4");
	private PSITable nit = new PSITable(0x10, "NIT");
	private PSITable sdt = new PSITable(0x11, "SDT");
	private PSITable eitH = new PSITable(0x12, "EIT"); // full seg
	private PSITable eitM = new PSITable(0x26, "EIT"); // full seg
	private PSITable eitL = new PSITable(0x27, "EIT"); // one seg
	private PSITable aitOneSeg = new PSITable(0x01fc, "AIT-O"); // 508
	private PSITable aitFullSeg = new PSITable(0x01f4, "AIT-F"); // 500
	
	private final static int DSMCC_ONE_SEG = 0x38c;	// 908
	private final static int DSMCC_FULL_SEG = 0x384;// 900
	
	private boolean isBigPacket = false;
	private ArrayList<EPGValues> eitlList = new ArrayList<EPGValues>();
	
	private Hashtable<Integer, Integer> PIDs = new Hashtable<Integer, Integer>();
	private Hashtable<Integer, String> PIDsDescriptions = new Hashtable<Integer, String>();
	private Hashtable<Integer, String> services = new Hashtable<Integer, String>();

	private boolean currentPacketIsTable = false;
	private byte[] bufferTemp;

	private PSITable[] allTables = new PSITable[] { pat, pm1, pm2, pm3, pm4,
			nit, sdt, eitH, eitL, eitM, aitOneSeg, aitFullSeg };

	private static final int PACKET_SIZE = 188;
//	private static final int MAX_SECTION_LENGTH = 0x3FD;// 1021
	 private static final int MAX_SECTION_LENGTH = 0xFFD;// 4096 -> Arib
	// STD-B10
	// Pag82
	private static final int PACKET_HEADER_SIZE = 4;
	private String networkName;
	
	private PSITable table = null;

	public boolean parsePacket(byte[] packet, boolean changed) { // returns 0  if OK, 1 if it is a payload and -1 if there was an error.
		bufferTemp = packet;
		int pid = ((packet[1] << 8) & 0x1f00) | (packet[2] & 0xff);
		boolean payloadUnitStart = (packet[1] & 0x40) > 0;
		int pointerField = 0;
		int dataStart = PACKET_HEADER_SIZE;

		table = null;
		
		if (payloadUnitStart) {
			pointerField = packet[PACKET_HEADER_SIZE] & 0xff;
			dataStart += 1 + pointerField;
		}
		
		for (int i = 0; i < allTables.length; i++) {
			if (pid == allTables[i].pid && !allTables[i].crcCheck) {
				table = allTables[i];
				break;
			}
		}
		if (table != null) {
			currentPacketIsTable = true;
			
			int length;
			int src_offset;
			if (payloadUnitStart) {
				table.dataStart = dataStart;
				table.i = table.dataStart;
				table.i++; // table_id
				table.sectionLength = ((packet[table.i++] << 8) & 0xf00)
						| (packet[table.i++] & 0xff);
				if (table.sectionLength > MAX_SECTION_LENGTH) {
					return false;
				}
				length = PACKET_SIZE;
				src_offset = 0;
				if (table.sectionLength + dataStart > PACKET_SIZE) {
					table.remaining = table.sectionLength - length - table.i;
				}
				table.offset = 0;
				table.qntOfPackets = 1;
				table.headersList.clear();
				if (table.remaining > 0) {
					setBigPacket(true);
				} else {
					setBigPacket(false);	
				}
			} else {
				setBigPacket(true);
				table.qntOfPackets++;
//				System.out.println("PacketNumber: "+packetCounter + " at "+table.qntOfPackets);
				length = PACKET_SIZE - PACKET_HEADER_SIZE;
				src_offset = PACKET_HEADER_SIZE;
				table.remaining -= length;
				
				byte [] header = new byte[PACKET_HEADER_SIZE];
				System.arraycopy(packet, 0, header, 0,PACKET_HEADER_SIZE);
				table.headersList.add(header);

			}
			try {
				System.arraycopy(packet, src_offset, table.fullTable, table.offset,length);
				
			} catch (ArrayIndexOutOfBoundsException e) {
				return false;
			}
			
			if (table.offset == 0 && src_offset != 0) {
				return false;
			}
			table.offset += length;
			if (table.remaining <= 0) {
				
				table.tableSpecificHeaderValue = ((table.fullTable[table.i++] << 8) & 0xff00)
						| (table.fullTable[table.i++] & 0xff);
				table.i++; /* Reserved, version_number, current_next_indicator */
				table.i++; /* section_number */
				table.i++; /* last_section_number */
				table.sectionLength -= 5 /*
										 * transport_stream_id, Reserved,
										 * version_number,
										 * current_next_indicator,
										 * section_number, last_section_number
										 */;
				table.sectionLength -= 4 /* crc */;
				table.sectionEnd = table.i + table.sectionLength;
				int previousCRC = CRC32.calc(table.fullTable, table.dataStart, table.sectionEnd - table.dataStart);
				table.crcCheck = CRC32.calc(table.fullTable, table.dataStart,
						table.sectionEnd - table.dataStart + 4) == 0;
				if (table.crcCheck) {
					if (pid == pat.pid) {
						parsePAT(table);
					} else if (pid == pm1.pid || pid == pm2.pid
							|| pid == pm3.pid || pid == pm4.pid) {
						parsePMT(table);
					} else if (pid == nit.pid) {
						parseNIT(table);
					} else if (pid == sdt.pid) {
						parseSDT(table);
					} else if (pid == aitOneSeg.pid || pid == aitFullSeg.pid) {
						parseAIT(table);
					} else if (pid == eitL.pid || pid == eitH.pid
							|| pid == eitM.pid) {
						parseEIT(table);
						table.crcCheck = false;
						table.offset = 0;
					}
					
					if (changed) {
						// Compares the previous CRC (before the changes) and the new one. If they are different, then make a change
						int crc32_Atual = CRC32.calc(table.fullTable, table.dataStart, table.sectionEnd - table.dataStart);
						if (crc32_Atual != previousCRC) {
							this.setNewCRC32(crc32_Atual, table.i);
						}
					}
				}
			}
		} else {
			currentPacketIsTable = false;
		}

		Integer oPID = new Integer(pid);
		Integer rPID = PIDs.get(oPID);
		if (rPID == null) {
			PIDs.put(oPID, 1);
		} else {
			PIDs.put(oPID, rPID + 1);
		}
		return true;
	}

	private void parseSDT(PSITable table) {
		table.i++;
		table.i++; /* original_network_id */
		table.i++; /* reserved_future_use */
		while (table.i < table.sectionEnd) {
			int serviceId = ((table.fullTable[table.i++] & 0xff) << 8)
					| (table.fullTable[table.i++] & 0xff);
			table.i++; /*
						 * reserved_future_use, EIT_schedule_flag,
						 * EIT_present_following_flag
						 */
			int descriptorsLoopLength = ((table.fullTable[table.i++] & 0xf) << 8)
					| (table.fullTable[table.i++] & 0xff);
			int descriptorsLoopEnd = table.i + descriptorsLoopLength;
			if (descriptorsLoopEnd >= table.fullTable.length) {
				System.out.println("Wrong SDT: Skipping...");
				break;
			}
			while (table.i < descriptorsLoopEnd) {
				table.i += parseDescriptor(table.pid, serviceId, table.fullTable, table.i);
			}
		}
	}

	private void parseNIT(PSITable table) {
		int network_descriptors_length = ((table.fullTable[table.i++] << 8) & 0xf00)
				| (table.fullTable[table.i++] & 0xff);
		int networkDescriptorsEnd = table.i + network_descriptors_length;
		while (table.i < networkDescriptorsEnd) {
			table.i += parseDescriptor(table.pid,-1, table.fullTable, table.i);
		}
		if (table.i < table.sectionEnd) {
			int transport_stream_loop_length = ((table.fullTable[table.i++] << 8) & 0xf00)
					| (table.fullTable[table.i++] & 0xff);
			int transportStreamLoopEnd = table.i + transport_stream_loop_length;
			while (table.i < transportStreamLoopEnd) {
				table.i++;
				table.i++; // int transport_stream_id = (table.fullTable[i++] <<
							// 8) | (table.fullTable[i++] & 0xff);
				table.i++;
				table.i++; // int original_network_id = (table.fullTable[i++] <<
							// 8) | (table.fullTable[i++] & 0xff);
				int transport_descriptors_length = ((table.fullTable[table.i++] << 8) & 0xf00)
						| (table.fullTable[table.i++] & 0xff);
				int transportDescriptorsEnd = table.i
						+ transport_descriptors_length;
				while (table.i < transportDescriptorsEnd) {
					table.i += parseDescriptor(table.pid,-1, table.fullTable, table.i);
				}
			}
		}
	}

	private void parsePMT(PSITable table) {
		int pcrPID = ((table.fullTable[table.i++] << 8) & 0x1f00)
				| (table.fullTable[table.i++] & 0xff);
		PIDsDescriptions.put(new Integer(pcrPID),
				"PCR - Program Clock Reference");
		int programInfoLength = ((table.fullTable[table.i++] << 8) & 0xf00)
				| (table.fullTable[table.i++] & 0xff);
		int programInfoEnd = table.i + programInfoLength;
		while (table.i < programInfoEnd) {
			table.i += parseDescriptor(table.pid,-1, table.fullTable, table.i);
		}
		while (table.i < table.sectionEnd) {
			byte streamType = table.fullTable[table.i++];
			int elementaryPID = ((table.fullTable[table.i++] << 8) & 0xf00)
					| (table.fullTable[table.i++] & 0xff);
			int ES_info_length = ((table.fullTable[table.i++] << 8) & 0xf00)
					| (table.fullTable[table.i++] & 0xff);
			String description = "";
			switch (streamType) {
			case 0x01:
				description = "Vídeo conforme ISO/IEC 11172-2";
				break;
			case 0x02:
				description = "Vídeo conforme ITU Recommendation H.262";
				break;
			case 0x03:
				description = "??udio conforme ISO/IEC 11172-3";
				break;
			case 0x04:
				description = "??udio conforme ISO/IEC 13818-3";
				break;
			case 0x05:
				description = "ITU-T Rec. H.222.0|ISO/IEC 13818-1 private_sections";
				break;
			case 0x06:
				description = "Pacote PES (legendas/closed-caption)";
				// closed_caption_pid = elementaryPID;
				break;
			case 0x07:
				description = "MHEG conforme ISO/IEC 13522-1";
				break;
			case 0x08:
				description = "Conforme ITU Recommendation H.222.0:2006, Anexo DSM-CC";
				break;
			case 0x09:
				description = "Conforme ITU Recommendation H.222.1";
				break;
			case 0x0A:
				description = "Conforme tipo A descrito na ISO/IEC 13818-6";
				break;
			case 0x0B:
				description = "Conforme tipo B descrito na ISO/IEC 13818-6 (DSM-CC)";
				break;
			case 0x0C:
				description = "Conforme tipo C descrito na ISO/IEC 13818-6";
				break;
			case 0x0D:
				description = "Conforme tipo D descrito na ISO/IEC 13818-6";
				break;
			case 0x0E:
				description = "Dados auxiliares conforme ITU Recomendation H222.0";
				break;
			case 0x0F:
				description = "??udio com sintaxe de transporte ADTS conforme ISO/IEC 13818-7";
				break;
			case 0x10:
				description = "Vídeo conforme ISO/IEC 14496-2";
				break;
			case 0x11:
				description = "??udio conforme ISO/IEC 14496-3";
				break;
			case 0x12:
				description = "Fluxo de pacotes SL ou fluxo FlexMux transportada nos pacotes de PES conforme ISO/IEC 14496-1";
				break;
			case 0x13:
				description = "Fluxo de pacotes SL ou fluxo FlexMux transportada em seções conforme ISO/IEC 14496-1";
				break;
			case 0x14:
				description = "Protocolo de sincronização de download conforme ISO/IEC 13818-6";
				break;
			case 0x15:
				description = "Metadados transportados por um pacote PES";
				break;
			case 0x16:
				description = "Metadados transportados por uma metadata_sections";
				break;
			case 0x17:
				description = "Metadados transportados pelo carrossel de dados conforme a ISO/IEC 13818-6";
				break;
			case 0x18:
				description = "Metadados transportados pelo carrossel de objetos conforme a ISO/IEC 13818-6";
				break;
			case 0x19:
				description = "Metadados transportados por um protocolo de download sincronizado ISO/IEC 13818-6";
				break;
			case 0x1A:
				description = "IPMP stream conforme a ISO/IEC 13818-11";
				break;
			case 0x1B:
				description = "Video H.264 / ISO 14496-10";
				break;
			case 0x7E:
				description = "Data pipe";
				break;
			case 0x7F:
				description = "IPMP stream";
				break;
			default:
				description = "Unknown";
			}
			PIDsDescriptions.put(new Integer(elementaryPID), description);
			int ESInfoEnd = table.i + ES_info_length;
			while (table.i < ESInfoEnd) {
				table.i += parseDescriptor(elementaryPID,-1, table.fullTable, table.i);
			}
		}
	}
	
	private void parseAIT(PSITable table) {
		int initialValue = table.dataStart + 3; // SectionSyntaxIndicator, ReservedFutureUse, Reserved, SectionLength
		int applicationType  = ((table.fullTable[initialValue++] & 0xff) << 8) | (table.fullTable[initialValue++] & 0xff);
		switch (applicationType) {
		case 0x00:
			System.out.println("Application type: Reservated");
			break;
		case 0x01:
			System.out.println("Application type: DVB-J/Ginga-J");
			break;
		case 0x02:
			System.out.println("Application type: DVB-HTML");
			break;
		case 0x06:
			System.out.println("Application type: ACAP-J");
			break;
		case 0x07:
			System.out.println("Application type: ARIB-BML");
			break;
		case 0x08:
			System.out.println("Application type: Ginga-Bridge");
			break;
		case 0x09:
			System.out.println("Application type: Ginga-NCL");
			break;
		default:
			System.out.println("Application type: Not registered");
			break;
		}
		initialValue++; /* Reserved, version_number, current_next_indicator */
		initialValue++; /* section_number */
		initialValue++; /* last_section_number */
		int commonDescriptorsLength = ((table.fullTable[table.i++] & 0xf) << 8) | (table.fullTable[table.i++] & 0xff);
		table.i+=commonDescriptorsLength;
		
		int applicationLoopLength = ((table.fullTable[table.i++] & 0xf) << 8) | (table.fullTable[table.i++] & 0xff);
		
		for (int i = table.i; i < table.i+applicationLoopLength; i++) {
			i+=6; // Application identifier size 32 bits for Organization ID and 16 for Application ID
			int applicationControlCode = (table.fullTable[i] & 0xff);
			i++;
			int applicationDescriptorLoopLength = ((table.fullTable[i++] & 0xf) << 8) | (table.fullTable[i++] & 0xff);
			i+=applicationDescriptorLoopLength;
		}
		table.i+=applicationLoopLength;
		//The last four is the crc32
	}

	private int parsePAT(PSITable table) {
		int pmtIndex = 0;
		while (table.i < table.sectionEnd) {
			int programNumber = ((table.fullTable[table.i++] & 0xff) << 8)
					| (table.fullTable[table.i++] & 0xff);
			int programPID = ((table.fullTable[table.i++] << 8) & 0x1f00)
					| (table.fullTable[table.i++] & 0xff);
			if (programNumber == 0) {
				nit.pid = programPID;
				PIDsDescriptions
						.put(nit.pid, "NIT - Network Information Table");
			} else {
				switch (pmtIndex) {
				case 0:
					pm1.pid = programPID;
					break;
				case 1:
					pm2.pid = programPID;
					break;
				case 2:
					pm3.pid = programPID;
					break;
				case 3:
					pm4.pid = programPID;
					break;
				}
				pmtIndex++;
				PIDsDescriptions.put(programPID, String.format(
						"PMT - Program Map Table (number 0x%04x)",
						programNumber));
			}
		}
		return table.i;
	}

	private String convertMJDtoDate(byte[] buffer, int position) {
		String startDate = new String();

		// StartTime has 40 bits.
		// 16 for MJD
		// 24 for TIME(hh:mm:ss). Using BCD.
		// 24 for Duration(hh:mm:ss). Using BCD.
		// If has an emergency news all bits should be '1' in TIME or Duration

		long MJD = ((buffer[position] & 0xff) << 8) // 2 StartTime MJD
				| (buffer[position+1] & 0xff);

		int k = 0;

		int Y1 = (int) Math.floor((MJD - 15078.2) / 365.25);
		int M1 = (int) Math
				.floor((MJD - 14956.1 - Math.floor(Y1 * 365.25)) / 30.6001);
		int D = (int) (MJD - 14956 - Math.floor(Y1 * 365.25) - Math
				.floor(M1 * 30.6001));

		if (M1 == 14 || M1 == 15) {
			k = 1;
		}
		int Y = Y1 + k;
		int M = M1 - 1 - k * 12;

		startDate = String.format("%d/%d/%d", D, M, Y + 1900);
		
		return startDate;

		// Conversão de volta
		// MJD = 14956 + D + int [(Y - L) x 365,25] + int [(M + 1 + L x 12) x
		// 30,6001]
		// Para M = 1 ou M = 2, L = 1. Em outros casos, L = 0.
	}
	
	private int convertDatetoMJD(String date) {
		int value = -1;
		// Considering that the correctly format is DD/MM/AAAA
		int Y = -1;
		int M = -1;
		int D = -1;
		int L = 0;
		
		D = Integer.valueOf((date.substring(0, date.indexOf('/'))));
		date = date.substring(date.indexOf('/')+1,date.length());

		M = Integer.valueOf((date.substring(0, date.indexOf('/'))));
		date = date.substring(date.indexOf('/')+1,date.length());

		Y = Integer.valueOf(date) - 1900;
		
		if (M == 1 || M == 2) {
			L = 1;
		}
		
		value = (int) (14956 + D +  Math.floor((Y - L) * 365.25) + Math.floor((M + 1 + L * 12) * 30.6001));
		return value;
		
	}
	
	private String getTime(byte buffer[], int position) {
		StringBuffer date = new StringBuffer();
		date.append(buffer[position] >> 4); // H1
		date.append(buffer[position] & 0xf); // H2
		date.append(":");
		position++;
		date.append(buffer[position] >> 4); // M1
		date.append(buffer[position] & 0xf); // M2
		date.append(":");
		position++;
		date.append(buffer[position] >> 4); // S1
		date.append(buffer[position] & 0xf); // S2
		position++;
		return date.toString();
	}
	
	private int[] convertTimeToInt(String date) {
		//Format 12:35:00
		int[] arrayNumbers = new int[3]; // HH:MM:SS - 3 bytes
		
		String Htmp = date.substring(0,date.indexOf(':'));
		date = date.substring(date.indexOf(':')+1,date.length());
		String Mtmp = date.substring(0,date.indexOf(':'));
		date = date.substring(date.indexOf(':')+1,date.length());
		String Stmp = date.substring(0,date.length());
		
		int H = Integer.valueOf(Htmp, 16).intValue();
		int M = Integer.valueOf(Mtmp, 16).intValue();
		int S = Integer.valueOf(Stmp, 16).intValue();
		
		arrayNumbers[0] = H;
		arrayNumbers[1] =  M;
		arrayNumbers[2] = S;
		return arrayNumbers;
	}
	
	private void parseEIT(PSITable table) {
		int transportStreamID = ((table.fullTable[table.i++] & 0xff) << 8) // 2
																			// bytes
																			// uimsbf
				| (table.fullTable[table.i++] & 0xff);
		int originalNetworkID = ((table.fullTable[table.i++] & 0xff) << 8) // 2
																			// bytes
																			// uimsbf
				| (table.fullTable[table.i++] & 0xff);

		table.i++; /* segment_last_section_number */
		table.i++; /* last_table_id */
		while (table.i < table.sectionEnd) {
			int event_id = ((table.fullTable[table.i++] & 0xff) << 8)
					| (table.fullTable[table.i++] & 0xff);
			
//			if (table.pid == eitL.pid) {
				int startI = table.i;
				EPGValues epgTemp = new EPGValues(event_id, transportStreamID, originalNetworkID);
				epgTemp.setStartDate(convertMJDtoDate(table.fullTable, table.i));// StartTime - MJD
				table.i+=2;
				epgTemp.setStartTime(getTime(table.fullTable, table.i));//StartTime - Hour
				table.i+=3;
				epgTemp.setDurationTime(getTime(table.fullTable, table.i));//DurationTime
				table.i+=3;
				int running_status = ((table.fullTable[table.i] >> 5) & 0x7);// 3bits para running status
				epgTemp.setRunningStatus(running_status);
				
				addEPGToList(epgTemp);
				
//			} else {
//				table.i += 5; // StartTime
//				table.i += 3; // duration
//				System.out.println("NOt!");
//
//			}

			int descriptorsLoopLength = ((table.fullTable[table.i++] << 8) & 0xf00)
					| (table.fullTable[table.i++] & 0xff);
			int descriptorsLoopEnd = table.i + descriptorsLoopLength;
			while (table.i < descriptorsLoopEnd) {
				table.i += parseDescriptor(table.pid,event_id, table.fullTable, table.i);
			}
		}
	}

	private int parseDescriptor(int pid, int id, byte[] packet, int position) {
		// http://www.arib.or.jp/english/html/overview/doc/6-STD-B10v4_6-E2.pdf
		// ABNTNBR15603_2D1_2007Vc2_2008.pdf
		int i = position;
		int descriptor_tag = packet[i++] & 0xff;
		int descriptor_length = packet[i++] & 0xff;
//		TSChecker instance = TSChecker.getInstance();
		int initialPosition = i;
		switch (descriptor_tag) {
		case 0x09: // Conditional access descriptor (descritor de acesso
					// condicional) Ver Figura 19
			break;
		case 0x0D: // Copyright descriptor (descritor de direitos autorais) Ver
					// Figura 40
			break;
		case 0x13: // Carousel ID descriptor (descritor identificador de
					// carrossel) Ver ISO/IEC 13818-6
			//Carousel ID = 32 bits, 4 bytes
			int carouselID = ((packet[i++] & 0xff) << 32) | (packet[i++] & 0xff) << 16 |
				((packet[i++] & 0xff) << 8) | (packet[i++] & 0xff);
			break;
		case 0x14: // Association tag descriptor (descritor de associação de
					// tag) Ver ISO/IEC 13818-6
			int associationTag = ((packet[i++] & 0xff) << 8) | (packet[i++] & 0xff);
			if (pid == DSMCC_FULL_SEG) {
				System.out.println("AssociationTagFullSeg: " + associationTag);
			} 
			if (pid == DSMCC_ONE_SEG) {
				System.out.println("AssociationTagOneSeg: " + associationTag);
			}
			break;
		case 0x15: // Deferred association tags descriptor (descritor de
					// informação de associação estendida) Ver ISO/IEC 13818-6
			break;
		case 0x28: // AVC vídeo descriptor (descritor de vídeo AVC) Ver Figura
					// 72
			break;
		case 0x2A: // AVC timing and HRD descriptor (descritor de sincronismo de
					// AVC e do decodificador hipotético de referência) Ver
					// Figura 73
			break;
		case 0x40: // Network name descriptor (descritor do nome da rede) Ver
					// Figura 20
			if (descriptor_length > 20) {
				System.out.println("Wrong network name descriptor");
			}
			networkName = bufferToString(packet, i, descriptor_length);
			break;
		case 0x41: // Service list descriptor (descritor da lista de serviços)
					// Ver Figura 21
			int descriptor_end = i + descriptor_length;
			while (i < descriptor_end) {
				int service_id = ((packet[i++] & 0xff) << 8)
						| (packet[i++] & 0xff);
				int service_type = packet[i++] & 0xff;
				Integer serviceID = new Integer(service_id);
				if (!this.services.containsKey(serviceID)) {
					this.services.put(serviceID, String.format(
							"service_id: 0x%02x | service_type: 0x%02x",
							service_id, service_type));
				}
			}
			break;
		case 0x42: // Stuffing descriptor (descritor de preenchimento) Ver
					// Figura 22
			break;
		case 0x47: // Bouquet name descriptor (descritor do nome do buquê) Ver
					// Figura 23
			break;
		case 0x48: // "Service descriptor (descritor de serviços)  Ver Figura 24";
			String d = "Desconhecido";
			int service_type = packet[i++] & 0xff;
			switch (service_type) {
			case 0x00:
				d = "Reservado para uso futuro";
				break;
			case 0x01:
				d = "Serviço de televisão digital";
				break;
			case 0x02:
				d = "Serviço de áudio digital";
				break;
			case 0x03:
				d = "Serviço de teletexto";
				break;
			case 0x04:
				d = "Serviço de referência NVOD";
				break;
			case 0x05:
				d = "Serviço time-shifted NVOD";
				break;
			case 0x06:
				d = "Serviço de mosaico";
				break;
			case 0x0A:
				d = "Codificação avançada para serviço de rádio digital";
				break;
			case 0x0B:
				d = "Codificação avançada para serviço de mosaico";
				break;
			case 0x0C:
				d = "Serviço de transmissão de dados";
				break;
			case 0x0D:
				d = "Reservado para interface de uso comum (ver EN 50221)";
				break;
			case 0x0E:
				d = "RCS Map (ver EN 301 790)";
				break;
			case 0x0F:
				d = "RCS FLS (ver EN 301 790)";
				break;
			case 0x10:
				d = "Serviço DVB MHP";
				break;
			case 0x11:
				d = "Serviço de televisão digital MPEG-2 HD";
				break;
			case 0x16:
				d = "Codificação avançada de serviço de televisão digital SD";
				break;
			case 0x17:
				d = "Codificação avançada de serviço de NVOD SD time-shifted";
				break;
			case 0x18:
				d = "Codificação avançada de serviço de referência NVOD SD";
				break;
			case 0x19:
				d = "Codificação avançada de serviço de televisão digital HD";
				break;
			case 0x1A:
				d = "Codificação avançada de serviço de NVOD HD time-shifted";
				break;
			case 0x1B:
				d = "Codificação avançada de serviço de referência NVOD HD";
				break;
			case 0xA1:
				d = "Serviço especial de vídeo";
				break;
			case 0xA2:
				d = "Serviço especial de áudio";
				break;
			case 0xA3:
				d = "Serviço especial de dados";
				break;
			case 0xA4:
				d = "Serviço de engenharia (software update)";
				break;
			case 0xA5:
				d = "Serviço promocional de vídeo";
				break;
			case 0xA6:
				d = "Serviço promocional de áudio";
				break;
			case 0xA7:
				d = "Serviço promocional de dados";
				break;
			case 0xA8:
				d = "Serviço de dados para armazenamento antecipado";
				break;
			case 0xA9:
				d = "Serviço de dados exclusivo para armazenamento";
				break;
			case 0xAA:
				d = "Lista de serviços de bookmark";
				break;
			case 0xAB:
				d = "Serviço simultâneo do tipo servidor";
				break;
			case 0xAC:
				d = "Serviço independente de arquivos";
				break;
			case 0xC0:
				d = "Serviço de dados";
				break;
			}
			int service_provider_name_length = packet[i++] & 0xff;
			if (service_provider_name_length > 20) {
				System.out.println("Wrong service descriptor");
			}
			String service_provider_name = bufferToString(packet, i,
					service_provider_name_length);
			i += service_provider_name_length;
			int service_name_length = packet[i++] & 0xff;
			if (service_name_length > 20) {
				System.out.println("Wrong service descriptor");
			}
			String service_name = bufferToString(packet, i, service_name_length);
			i += service_name_length;
			Integer serviceID = new Integer(id);
			this.services
					.put(serviceID,
							String.format(
									"service_id: 0x%02x | service_provider_name: %-16s | service_name: %-16s | service_type: 0x%02x %s",
									id, service_provider_name, service_name,
									service_type, d));
			break;
		case 0x49: // Country availability descriptor (descritor de
					// disponibilidade de país) Ver Figura 25
			System.out.println("Country Availability: "
					+ (((packet[i++] & 0xff) << 8) | (packet[i++] & 0xff)));
			break;
		case 0x4A: // Linkage descriptor (descritor de ligações) Ver Figura 26
			// EPG!!
			System.out.println("Linkage Descriptor");
			System.out.println("DesciptorTag: " + descriptor_tag);
			break;
		case 0x4B: // NVOD reference descriptor (descritor de referência NVOD)
					// Ver Figura 27
			break;
		case 0x4C: // Time shifted service descriptor (descritor de horário de
					// mudança de serviço) Ver Figura 28
			break;
		case 0x4D: // "Short event descriptor (descritor de eventos curtos)  Ver Figura 29";
			i++;
			i++;
			i++; // ISO 639 language code
			int event_name_length = packet[i++] & 0xff;
			if (event_name_length > 96) {
				// System.out.println("Wrong short event descriptor");
			}
			int statingPosition = i;
			String event_name = bufferToString(packet, i, event_name_length);
			getEPGByID(id).setName(event_name);
			i += event_name_length;
			
			int text_length = packet[i++] & 0xff;
			statingPosition = i;
			if (text_length > 192) {
//				System.out.println("Wrong short event descriptor");
				getEPGByID(id).setShortDescrition("Error!");	
			} else {
				String text = bufferToString(packet, i, text_length);
				getEPGByID(id).setShortDescrition(text);
				
			}
			
			i += text_length;
		
			break;
		case 0x4E: // Extended event descriptor (descritor de eventos
					// estendidos) Ver Figura 30
			System.out.println("OPA, aqui tem o POR!");
			break;
		case 0x4F: // Time shifted event descriptor (descritor de horário de
					// mudança de evento) Ver Figura 31
			break;
		case 0x50: // "Component descriptor (descritor de componentes)  Ver Figura 32"
			i++; // Reservado, Stream content
			i++; // Component type, Component tag
			i++; // ?
			// String iso_639_language_code = new String(packet, i, 3);
			i += 3;
			int component_text_length = descriptor_length - 6;
			if (component_text_length > 16) {
				System.out.println("Wrong component descriptor");
			}
			break;
		case 0x51: // Mosaic descriptor (descritor de mosaicos) Ver Figura 33
			break;
		case 0x52: // Stream identifier descriptor (descritor de identificação)
					// Ver Figura 34
			break;
		case 0x53: // CA identifier descriptor (descritor identificador de CA)
					// Ver Figura 35
			break;
		case 0x54: // Content descriptor (descritor de conteúdo) Ver Figura 36
			// Ver ANEXO C da ABNT NBR 15603-2:2007
			for (int j = i; j < i + descriptor_length;j++) {
				
				int nibble_1 = packet[j] >> 4;
				int nibble_2 = packet[j] & 0xf;
				int currentValue = 0;
				int  newValue = 0;
				try {
					currentValue = getNewDoubleIntValueAsHex(nibble_1, nibble_2);	
				} catch (NumberFormatException e) {
					// Do nothing, defaul value is 0 in this case
				}
				
				getEPGByID(id).setNibble(packet[j]);
				
				j++;
				
				int user_byte_1 = packet[j] >> 4;
				int user_byte_2= packet[j] & 0xf;
				currentValue = 0;
				newValue = 0;
				try {
					currentValue = getNewDoubleIntValueAsHex(user_byte_1, user_byte_2);	
				} catch (NumberFormatException e) {
					// Do nothing, defaul value is 0 in this case
				}
				getEPGByID(id).setUser(packet[j]);
			}
			i+=descriptor_length;
			
			break;
		case 0x55: // Parental rating descriptor (descritor de classificação
					// indicativa) Ver Figura 37
			 int n = descriptor_length/4;
			 for (int j = 0; j < n; j++) {
				 int indexOfContryCode = i;
				 getEPGByID(id).setContryCode(bufferToString(packet, i, 3)); // Country Code 24 Bits or 3 bytes
				 i+=3;
	
				 int conteudo = packet[i] >> 4;
				 int idade = packet[i] & 0xf;
				 int currentValue = getNewDoubleIntValueAsHex(conteudo, idade);
				 
				 getEPGByID(id).setRatingHex(packet[i]);
				 
				 i++;
			}
			break;
		case 0x58: // Local time offset descriptor (descritor de diferença de
					// fuso horário) Ver Figura 44
			break;
		case 0x63: // Partial transport stream descriptor (descritor do fluxo de
					// transporte parcial) Ver Figura 75
			break;
		case 0x7C: // AAC Audio (não documentado na norma)
			break;
		case 0xC0: // Hierarchical transmission descriptor (descritor de
					// transmissão hierárquica) Ver Figura 41
			break;
		case 0xC1: // Digital copy control descriptor (descritor de controle de
					// cópias digitais) Ver Figura 42
			break;
		case 0xC2: // Network identifier descriptor (descritor de identificação
					// de rede) Ver Figura 76
			break;
		case 0xC3: // Partial transport stream time descriptor (descritor de
					// hora do fluxo de transporte parcial)
			break;
		case 0xC4: // Audio component descriptor (descritor de componentes de
					// áudio) Ver Figura 45
			break;
		case 0xC5: // Hyperlink descriptor (descritor de hyperlinkI) Ver Figura
					// 46
			break;
		case 0xC6: // Target area descriptor (descritor de região-alvo) Ver
					// Figura 47
			break;
		case 0xC7: // Data contents descriptor (descritor de conteúdo de dados)
					// Ver Figura 48
			break;
		case 0xC8: // Video decode control descriptor (descritor de controle de
					// decodificação de vídeo) Ver Figura 49
			break;
		case 0xC9: // Download content descriptor (descritor de conteúdo de
					// atualização de receptores) Ver Figura 78
			break;
		case 0xCD: // TS information descriptor (descritor de informação do TS)
					// Ver Figura 66
			i++; // remote control key identification
			int length_ts_name = (packet[i++] & 0xfc) >> 2;
			if (length_ts_name > 20) {
				System.out.println("Wrong ts name descriptor");
			}
			// tsName = bufferToString(packet, i, length_ts_name);
			break;
		case 0xCE: // Extended broadcaster descriptor (descritor estendido de
					// radiodifusão) Ver Figura 67
			break;
		case 0xCF: // Logo transmission descriptor (descritor de transmissão de
					// logotipos) Ver Figura 68
			break;
		case 0xD0: // Basic local event descriptor (descritor de evento locais
					// básicos) Ver Figura 50
			break;
		case 0xD1: // Reference descriptor (descritor de referência) Ver Figura
					// 51
			break;
		case 0xD2: // Node relation descriptor (descritor de relação de nós) Ver
					// Figura 52
			break;
		case 0xD3: // Short node information descriptor (descrição curta de
					// informações de nó) Ver Figura 53
			break;
		case 0xD4: // STC (system time clock) reference descriptor (descritor
					// para a referência do relógio do sistema) Ver Figura 54
			break;
		case 0xD5: // Series descriptor (descritor de séries) Ver Figura 57
//			System.out.println("Series!");
			break;
		case 0xD6: // Event group descriptor (descritor de grupo de eventos) Ver
					// Figura 58
			break;
		case 0xD7: // SI parameter descriptor (descritor de parâmetros de SI)
					// Ver Figura 59
			break;
		case 0xD8: // Broadcaster name descriptor (descritor de nome do
					// radiodifusor) Ver Figura 60
			System.out.println("Contains broadcaster");
			break;
		case 0xD9: // Component group descriptor (descritor de grupo de
					// componentes) Ver Figura 61
			break;
		case 0xDA: // SI prime TS descriptor (descritor do principal TS do SI)
					// Ver Figura 62
			break;
		case 0xDB: // Board information descriptor (descritor da informação
					// embarcada) Ver Figura 63
			break;
		case 0xDC: // LDT linkage descriptor (descritor de ligação da LDT) Ver
					// Figura 64
			break;
		case 0xDD: // Connected transmission descriptor (descritor de
					// transmissões conectadas de áudio) Ver Figura 65
			break;
		case 0xDE: // Content availability descriptor (descritor de
					// disponibilidade de conteúdo) Ver Figura 69
			break;
		case 0xE0: // Service group descriptor (descritor de grupo de serviço)
					// Ver Figura 74
			break;
		case 0xF7: // Carousel compatible composite descriptor (descritor de
					// composição do carrossel de dados) Ver Figura 70
			break;
		case 0xF8: // Conditional playback descriptor (descritor de reexibição
					// condicional) Ver Figura 71
			break;
		case 0xFA: // Terrestrial delivery system descriptor (descritor de
					// sistema de distribuição terrestre) Ver Figura 55
			break;
		case 0xFB: // Partial reception descriptor (descritor de recepção
					// parcial) Ver Figura 56
			break;
		case 0xFC: // Emergency information descriptor (descritor de informação
					// de emergência) Ver Figura 43
			break;
		case 0xFD: // Data component descriptor (descritor de componente de
					// dados) Ver Figura 38
			break;
		case 0xFE: // System management descriptor (descritor de gerenciamento
					// de sistema) Ver Figura 39
			i++;
			i++; /* system_management_id */
			int additional_identifier_info_size = descriptor_length - 2;
			if (additional_identifier_info_size > 0) {
				System.out.println("additional_identifier_info="
						+ bufferToString(packet, i,
								additional_identifier_info_size));
			}
			break;
		default:
			// System.out.println(String.format("Unknown descriptor tag 0x%x",
			// descriptor_tag));
		}
		return descriptor_length + 2;
	}

	private String bufferToString(byte[] buffer, int offset, int length) {
		StringBuffer result = new StringBuffer();
		for (int i = offset; i < offset + length; i++) {
			char c = (char) (buffer[i] & 0xff);
			result.append(Character.toString(c));
		}
		return result.toString();
	}
	
	public void setNewValueInAByte(int position, int value) {
		this.table.fullTable[position] = (byte) value;
	}
	
	public void setNewValueInTwoBytesAtFullTable(PSITable table,int value) {
		this.table.fullTable[table.i] = (byte) ((value >> 8) - 256);
		this.table.fullTable[table.i+1] = (byte) ((value & 0xff) - 256);
	}
	
	public void setNewValueInTwoBytes(int position, int value) {
		this.table.fullTable[position] = (byte) ((value >> 8) - 256);
		this.table.fullTable[position+1] = (byte) ((value & 0xff) - 256);
	}
	
	public void setValueInAFourBytes(int position, int value) {
		this.table.fullTable[position+0] = (byte) ((value >> 32) - 256);
		this.table.fullTable[position+1] = (byte) ((value >> 16) - 256);
		this.table.fullTable[position+2] = (byte) ((value >> 8) - 256);
		this.table.fullTable[position+3] = (byte) ((value & 0xff) - 256);
				
	}

	public void setNewCRC32(int crc32, int statingPoint) {
		this.table.fullTable[statingPoint+0] = (byte)((crc32 & 0xff000000) >> 24);
		this.table.fullTable[statingPoint+1] = (byte)((crc32 & 0x00ff0000) >> 16);
		this.table.fullTable[statingPoint+2] = (byte)((crc32 & 0x0000ff00) >> 8);
		this.table.fullTable[statingPoint+3] = (byte)((crc32 & 0x000000ff) >> 0);
	}
	

	public void setBigPacket(boolean isBigPacket) {
		this.isBigPacket = isBigPacket;
	}

	public boolean isBigPacket() {
		return isBigPacket;
	}
	
	// EIT LIST Handler
	
	public void addEPGToList(EPGValues epg) {
		if (this.eitlList.size() !=0 ) {
			for (int i = 0; i < this.eitlList.size(); i++) {
				if (this.eitlList.get(i).getEventID() == epg.getEventID()) {
					return;
				}
			}
		}
		this.eitlList.add(epg);
	}
	
	public void setEitlList(ArrayList<EPGValues> eitlList) {
		this.eitlList = eitlList;
	}

	public EPGValues getEPGByID(int id) {
		EPGValues value = null;
		for (int i = 0; i < this.eitlList.size(); i++) {
			if (this.eitlList.get(i).getEventID() == id) {
				value = this.eitlList.get(i);
			}
		}
		return value;
	}
	
	public ArrayList<EPGValues> getEitlList() {
		return eitlList;
	}
	
	private byte[] fullPacketsFromABigPacket() {
		byte[] temp = new byte[PACKET_SIZE*(table.headersList.size()+1)]; // The size is: 1 (the first) + the number of headers collected
		
		System.arraycopy(table.fullTable, 0, temp, 0, PACKET_SIZE); // First packet

		for (int i = 0; i < table.headersList.size(); i++) {
			byte[] tempCompletePacket = new byte[PACKET_SIZE]; // algoritmo perfeito
			byte[] header = table.headersList.get(i);
			System.arraycopy(header, 0, tempCompletePacket, 0, PACKET_HEADER_SIZE);
			
			// The count is: The first packet(full) plus the other packets without the headers.
			int srcPos = PACKET_SIZE + ((PACKET_SIZE - PACKET_HEADER_SIZE)*(i)); // OK
			try {
			System.arraycopy(table.fullTable, srcPos, tempCompletePacket, PACKET_HEADER_SIZE, PACKET_SIZE - PACKET_HEADER_SIZE);
			} catch (Exception e) {
				System.out.println("Message");
			}
			int targetPos = PACKET_SIZE*(1+i);
			System.arraycopy(tempCompletePacket,0,temp, targetPos, PACKET_SIZE);
		}
		return temp;
	}
	
	/**
	 * This class is responsible to give back the packet for be written
	 */
	public byte[] getPacket() {
		if (currentPacketIsTable) {

			if (isBigPacket) {
				if (table.remaining <= 0) {
					return fullPacketsFromABigPacket();
				} else {
					return null; // Does not give back the packet part of the table until all table be completed.
				}
			} else {
				byte[] singlePacket = new byte[PACKET_SIZE];
				System.arraycopy(this.table.fullTable, 0, singlePacket, 0, PACKET_SIZE);
				return singlePacket;
			}
		}
		// if it isn't, just return the packet.
		return this.bufferTemp;
		
	}
	
	public int getNewDoubleIntValueAsHex(int int1, int int2) {
		int value = -1;
		
		String temp1 = Integer.toHexString(int1);
		String temp2 = Integer.toHexString(int2);
		
		value = Integer.parseInt(temp1+temp2, 16);  
		
		return value;
	}
}
