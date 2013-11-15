package br.ufpe.cin.tool.extras;

public class Constants {

	public enum Channels {
		GLOBO("GLOBO", 13),
		SBT("SBT", 2),
		BAND("BAND", 4),
		RECORD("RECORD",9),
		REDETV("REDETV", 6);
		
		private String key;
		private int number;
		
		Channels(String key, int number) {
			this.key = key;
			this.number = number;
		}
		
		public int getNumber() {
			return number;
		}
		
		public String toString() {
			return key;
		}
	}
}
