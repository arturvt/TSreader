package br.ufpe.cin.tool.core;

import java.io.File;
import java.util.List;

import br.ufpe.cin.tool.db.DatabaseFacade;
import br.ufpe.cin.tool.db.entities.AssociatedContext;
import br.ufpe.cin.tool.db.entities.Broadcaster;
import br.ufpe.cin.tool.db.entities.Hashtags;
import br.ufpe.cin.tool.db.entities.Program;
import br.ufpe.cin.tool.extras.Constants.Channels;
import br.ufpe.cin.tool.mpegts.TSChecker;

public class Main {

	public static void listProgramsByBroadCaster() {
		DatabaseFacade facade = DatabaseFacade.getInstance();
		boolean newTransaction = false;
		newTransaction = facade.beginTransaction();
		if (!newTransaction) {
			System.out.println("Error starting transaction.");
			return;
		}

		List<Broadcaster> list = facade.getAllBroadCasters();
		for (Broadcaster bro : list) {
			System.out.println("Broadcaster: " + bro.getName());
			System.out.println(" --- Programs ----");
			for (Program prog : bro.getPrograms()) {
				System.out.println("-> " + prog.getName());
			}
		}

		facade.commit();
	}

	public static void insertHashTags(String program, String hashTags) {
		DatabaseFacade facade = DatabaseFacade.getInstance();
		boolean newTransaction = false;
		newTransaction = facade.beginTransaction();
		if (!newTransaction) {
			System.out.println("Error starting transaction.");
			return;
		}
		Program prg = facade.getProgram(program);
		if (prg != null) {
			System.out.println("Program found: " + program);
			AssociatedContext associatedContext = prg.getAssociatedContext();
			if (associatedContext != null) {
				String hashs[] = hashTags.split(",");
				for (String hashTag : hashs) {
					hashTag = hashTag.replaceAll("\\s+","");
					Hashtags hash = facade.getHashTag(hashTag);
					if (hash == null) {
						hash = new Hashtags(0, associatedContext, hashTag);
						facade.saveOrUpdate(hash);
						System.out.println("Added. Hashtag: " + hashTag);
					} else {
						System.out.println("Already contains. Hashtag: "
								+ hashTag);
					}
				}
			}
		} else {
			System.out.println("Program not found - " + program);
		}
		facade.commit();
	}

	public static void readTS(String folder) throws InterruptedException {
		File tsFolderLocation = new File(folder);
		if (tsFolderLocation.exists()) {
			if (tsFolderLocation.isDirectory()) {
				for (Channels channel : Channels.values()) {
					File tsFolderWithChannel = new File(folder + File.separator
							+ channel.toString());
					if (tsFolderWithChannel.isDirectory()) {
						File[] listOfTS = tsFolderWithChannel.listFiles();
						for (File file : listOfTS) {
							System.out.println("Reading file: "
									+ file.getAbsolutePath()
									+ " from channel: " + channel.toString());
							TSChecker checker = new TSChecker();
							if (checker.loadTS(file)) {
								EPGConstructor.readEPGList(channel.toString(),
										channel.getNumber(),
										checker.getEPGList());
								System.out.println("Waiting...");
								Thread.sleep(3000);
							}
						}
					}
				}
			} else {
				System.out.println("Is this really a folder?" + folder);
			}
		} else {
			System.out.println("Could not locate: " + folder);
		}
	}

	private static void fillData(String places[]) {
		for (String place:places) {
			System.out.println("Reading " + place);
			try {
				readTS("D:\\TSs\\TSS" + File.separator + place);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		listProgramsByBroadCaster();
		 
//		String places[] = {"Sorocaba", "Rio de Janeiro", "Recife"};
//		fillData(places);
		
		 insertHashTags("MAIS VOCÊ", "#MaisVoce, #AnaMariaBraga, #AnaMaria, #LoiroJose" );
		 insertHashTags("ENCONTRO COM FÁTIMA BERNARDES","#encontro, #encontrofatima, #fatimabernardes");
		// insertHashTags("BEM ESTAR", "#bemestar");
		// fillAssociatedEvents();
		// TSChecker tsChecker = TSChecker.getInstance();

		// tsChecker.loadTS("D:\\TSs\\TSS\\Globo-2013-10-28-23h29m49s-Tela Quente.ts","Globo");
		// EPGConstructor.readEPGList("Globo", 13, tsChecker.getEPGList());
		// tsChecker.clearEIT();
		// Thread.sleep(5000);
		// tsChecker.loadTS("D:\\TSs\\TSS\\vlc-record-2013-10-30-23h44m21s-dvb-t___frequency=605143000-Programa do Jo.ts","Globo");
		// EPGConstructor.readEPGList("Globo", 13, tsChecker.getEPGList());
		// tsChecker.clearEIT();
		// Thread.sleep(5000);
		// tsChecker.loadTS("D:\\TSs\\TSS\\vlc-record-2013-10-30-23h49m55s-dvb-t___frequency=623142000-.ts","Record");
		// EPGConstructor.readEPGList("Record", 9, tsChecker.getEPGList());
		// tsChecker.clearEIT();
		// Thread.sleep(5000);
		// tsChecker.loadTS("D:\\TSs\\TSS\\SBT-2013-10-28-22h40m44#Programa do Ratinho.ts","SBT");
		// EPGConstructor.readEPGList("SBT", 2, tsChecker.getEPGList());
		// tsChecker.clearEIT();

		// tsChecker.loadTS("D:\\TSs\\TSS\\vlc-record-2013-10-31-00h11m58s-dvb-t___frequency=599142000-#Gabi Quase Proibida.ts","SBT");
		// EPGConstructor.readEPGList("SBT", 2, tsChecker.getEPGList());
		// tsChecker.clearEIT();

		// listProgramsByBroadCaster();

		// tsChecker.loadTS("D:\\TSs\\TSS\\vlc-record-2011-10-21-10h46m09s-dvb-t___frequency=701000000-Onde Deus Chora.ts",
		// "RedeVida");
		// tsChecker.loadTS("D:\\TSs\\TSS\\vlc-record-2011-10-21-10h30m52s-dvb-t___frequency=605000000-Bem Estar.ts",
		// "Globo");
		// tsChecker.insertValuuesInDB();
		// tsChecker.loadTS("D:\\TSs\\TSS\\vlc-record-2013-Record-.ts",
		// "RECORD");
		// tsChecker.loadTS("D:\\TSs\\TSS\\vlc-record-2011-10-21-10h39m16s-dvb-t___frequency=599000000-HORA DA ALEGRIA.ts",
		// "SBT");

		// EPGConstructor.readEPGList(tsChecker.getEPGList());

		// tsChecker.insertValuuesInDB();
		// DataBaseManager_working.closeConnection();
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// tsChecker.loadTS("D:\\TSs\\TSS\\05-30_21-48-18_RBS TV HD_JORNAL DA GLOBO.ts");

	}

}
