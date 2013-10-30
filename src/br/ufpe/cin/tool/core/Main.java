package br.ufpe.cin.tool.core;

import br.ufpe.cin.tool.db.DataBaseFacade;
import br.ufpe.cin.tool.db.dao.Broadcaster;
import br.ufpe.cin.tool.db.dao.BroadcasterHome;
import br.ufpe.cin.tool.db.dao.Program;
import br.ufpe.cin.tool.db.dao.Terms;

public class Main {

	private static void createAndStoreEvent(String title) {
		DataBaseFacade session = DataBaseFacade.getInstance();
		if (session.beginTransaction()) {
			System.out.println("Go on");
		} else {
			return;
		}
		Broadcaster broad = new Broadcaster();
		broad.setChannel(12);
		broad.setCountry("BRA");
		broad.setLanguage("por");
		broad.setName(title);
		broad.setId(9);
		session.save(broad);

		Terms term = new Terms(46, "sada", 1);
		session.save(term);
		session.commit();
		session.closeSession();
	}

	private static void listBroadcasters() {
		DataBaseFacade session = DataBaseFacade.getInstance();
		if (session.beginTransaction()) {
			System.out.println("Go on");
		} else {
			return;
		}

		session.listBroadcasters();
		session.closeSession();
	}

	private static void getBroadCaster(int id) {
		DataBaseFacade session = DataBaseFacade.getInstance();
		if (session.beginTransaction()) {
			System.out.println("Go on");
		} else {
			return;
		}

		BroadcasterHome bro = new BroadcasterHome();
		Broadcaster foundBro = bro.findById(id);

		if (foundBro != null) {
			System.out.println("Bro: " + foundBro.getName());
		}

		session.closeSession();
	}

	private static void getByName(String name) {
		DataBaseFacade session = DataBaseFacade.getInstance();
		if (session.beginTransaction()) {
			System.out.println("Go on");
		} else {
			return;
		}

		Broadcaster bro = session.getBroadCaster(name);
		if ( bro != null) {
			System.out.println("Found! Listing attached programs...");
			for (Program programs:bro.getPrograms()) {
				System.out.println(programs.getName());
			}
		}
		session.closeSession();
	}

	private static void insertProgram(String broadcaster, String program) {
		DataBaseFacade session = DataBaseFacade.getInstance();
		if (session.beginTransaction()) {
			System.out.println("Go on");
		} else {
			return;
		}

		Program prog = new Program();
		Broadcaster tempBro = session.getBroadCaster(broadcaster);
		if (tempBro != null) {
			prog.setBroadcaster(tempBro);
			prog.setName(program);
			prog.setId(4);
			session.save(prog);
			session.commit();
		} else {
			System.out.println("No broadcaster found");
		}
		session.closeSession();
	}

	public static void main(String[] args) {
		// createAndStoreEvent("Manchete");
		String broadCasterName = "GLOBO";
		getByName(broadCasterName);
		listBroadcasters();
		insertProgram(broadCasterName, "Video Show");
		// DataBaseManager_working.startConnection();
		// TSChecker tsChecker = TSChecker.getInstance();
		// tsChecker.loadTS("D:\\TSs\\TSS\\Globo_SP-20100921_1sWithPAT.ts",
		// "Globo");
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
