package br.ufpe.cin.tool.db.entity;

public class ChannelOperator {

	private long id = -1;
	private String name;
	private String language;
	private String countryCode;
	
	
	public ChannelOperator(long id, String name, String language,
			String countryCode) {
		super();
		this.id = id;
		this.name = name;
		this.language = language;
		this.countryCode = countryCode;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
}
