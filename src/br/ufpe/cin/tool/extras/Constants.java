package br.ufpe.cin.tool.extras;

public class Constants {

	public static final String EPG_QUERY =  "INSERT INTO epg(country, datestart, timestart, duration, channel_name, program_name, descriptor) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?);";
	
}
