package br.ufpe.cin.tool.db.entity;

public class Program {
	
	// Attributes
	private Long id;
    private String name;
    private String twitterUser;
    private String hashTag;
	private ChannelOperator chanOperator;
        
    
    public Program(Long id, String name, String twitterUser,
			String hashTag, ChannelOperator chanOperator) {
		super();
		this.id = id;
		this.name = name;
		this.twitterUser = twitterUser;
		this.hashTag = hashTag;
		this.chanOperator = chanOperator;
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
	public String getTwitterUser() {
		return twitterUser;
	}
	public void setTwitterUser(String twitterUser) {
		this.twitterUser = twitterUser;
	}
	public String getHashTag() {
		return hashTag;
	}
	public void setHashTag(String hashTag) {
		this.hashTag = hashTag;
	}
	public ChannelOperator getChanOperator() {
		return chanOperator;
	}
	public void setChanOperator(ChannelOperator chanOperator) {
		this.chanOperator = chanOperator;
	}

}
