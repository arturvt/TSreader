package br.ufpe.cin.tool.mpegts;


public class EPGValues {
	private int eventID = 0;
	private int transportStreamID = 0;
	private int originalNetworkID = 0;
	private int runningStatus = 0;
	private String startDate;
	private String startTime;
	private String durationTime;
	private String name;
	private String shortDescrition;
	private int parentalRating = -1;
	private int parentalRatingDescription = -1;
	private int nibble_1 = -1;
	private int nibble_2 = -1;
	private int user_byte = -1;
	private int ratingHex = 0x0;
	private String contryCode;

	public EPGValues(int eventID, int transportStreamID, int originalNetworkID) {
		this.setEventID(eventID);
		this.setTransportStreamID(transportStreamID);
		this.setOriginalNetworkID(originalNetworkID);
	}

	public String getRunningStatusString(int runningStatusInt) {
		String runningStatusS;
		switch (runningStatusInt) {
		case 0:
			runningStatusS = "Indefinido";
			break;
		case 1:
			runningStatusS = "Desligado";
			break;
		case 2:
			runningStatusS = "Começa em alguns minutos ";
			break;
		case 3:
			runningStatusS = "Pausado";
			break;
		case 4:
			runningStatusS = "Executando";
			break;
		case 5:
		case 6:
		case 7:
			runningStatusS = "Reservado para uso futuro";
			break;
		default:
			runningStatusS = "Error!";
			break;
		}
		return runningStatusS;
	}

	public void setParentalRating(int parentalRating) {
		this.parentalRating = parentalRating;
	}

	public int getParentalRating() {
		return parentalRating;
	}

	public void setShortDescrition(String shortDescrition) {
		this.shortDescrition = shortDescrition;
	}

	public String getShortDescrition() {
		return shortDescrition;
	}

	public void setEventID(int eventID) {
		this.eventID = eventID;
	}

	public int getEventID() {
		return eventID;
	}

	public void setTransportStreamID(int transportStreamID) {
		this.transportStreamID = transportStreamID;
	}

	public int getTransportStreamID() {
		return transportStreamID;
	}

	public void setOriginalNetworkID(int originalNetworkID) {
		this.originalNetworkID = originalNetworkID;
	}

	public int getOriginalNetworkID() {
		return originalNetworkID;
	}

	public void setRunningStatus(int runningStatus) {
		this.runningStatus = runningStatus;
	}

	public int getRunningStatus() {
		return runningStatus;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setDurationTime(String durationTime) {
		this.durationTime = durationTime;
	}

	public String getDurationTime() {
		return durationTime;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setParentalRatingDescription(int parentalRatingDescription) {
		this.parentalRatingDescription = parentalRatingDescription;
	}

	public int getParentalRatingDescription() {
		return parentalRatingDescription;
	}

	public void setContryCode(String contryCode) {
		this.contryCode = contryCode;
	}

	public String getContryCode() {
		return contryCode;
	}

	public void setRatingHex(int ratingHex) {

		int conteudo = ratingHex >> 4;
		int idade = ratingHex & 0xf;
		setParentalRating(idade);
		setParentalRatingDescription(conteudo);
		
	}
	
	public int getRatingHex() {
		return ratingHex;
	}
	
	public void setUser(int userHex) {
		this.user_byte = userHex & 0xff;
	}

	public void setNibble(int nibbleHex) {
		int nibble_1 = nibbleHex >> 4;
		int nibble_2 = nibbleHex & 0xf;
		this.nibble_1 = nibble_1;
		this.nibble_2 = nibble_2;
		
	}

	public int getNibble_1() {
		return nibble_1;
	}

	public void setNibble_2(int nibble_2) {
		this.nibble_2 = nibble_2;
	}

	public int getNibble_2() {
		return nibble_2;
	}

	public int getUser_Byte() {
		return user_byte;
	}
	
	public void setUserByte(int user_byte) {
		this.user_byte = user_byte;
	}
}