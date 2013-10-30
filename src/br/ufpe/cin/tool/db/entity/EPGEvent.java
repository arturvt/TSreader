package br.ufpe.cin.tool.db.entity;


public class EPGEvent {
	
	private Long id;
    private Program listOfPrograms;
    private String date;
    private String time;
    private String descriptor;
    
    public EPGEvent(){}
    
    
	public EPGEvent(long id, Program listOfPrograms, String date,
			String time, String descriptor) {
		super();
		this.id = id;
		this.listOfPrograms = listOfPrograms;
		this.date = date;
		this.time = time;
		this.descriptor = descriptor;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Program getListOfPrograms() {
		return listOfPrograms;
	}
	public void setListOfPrograms(Program listOfPrograms) {
		this.listOfPrograms = listOfPrograms;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getDescriptor() {
		return descriptor;
	}
	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}
    
}
