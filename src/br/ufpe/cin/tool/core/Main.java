package br.ufpe.cin.tool.core;

import java.util.List;

import br.ufpe.cin.tool.db.DataBaseFacade;
import br.ufpe.cin.tool.db.dao.Broadcaster;
import br.ufpe.cin.tool.db.dao.Program;
import br.ufpe.cin.tool.mpegts.TSChecker;

public class Main {


	
	public static void listProgramsByBroadCaster() {
		DataBaseFacade session = DataBaseFacade.getInstance();
		if (session.beginTransaction()) {
			System.out.println("Go on");
		} else {
			return;
		}
		
		List<Broadcaster> list = session.getAllBroadCasters();
		for (Broadcaster bro:list) {
			System.out.println("Broadcaster: "+bro.getName());
			System.out.println(" --- Programs ----");
			for (Program prog:bro.getPrograms()) {
				System.out.println("-> "+prog.getName());
			}
		}
		
		session.closeSession();
	}

	public static void main(String[] args) {

		TSChecker tsChecker = TSChecker.getInstance();
		System.out.println("Globo");
		tsChecker.loadTS("D:\\TSs\\TSS\\vlc-record-2013-10-30-23h44m21s-dvb-t___frequency=605143000-Programa do Jo.ts","Globo");
		EPGConstructor.readEPGList("Globo", 13, tsChecker.getEPGList());

		tsChecker.loadTS("D:\\TSs\\TSS\\vlc-record-2013-10-30-23h49m55s-dvb-t___frequency=623142000-.ts","Record");
		EPGConstructor.readEPGList("Record", 9, tsChecker.getEPGList());

		tsChecker.loadTS("D:\\TSs\\TSS\\vlc-record-2013-10-31-00h11m58s-dvb-t___frequency=599142000-#Gabi Quase Proibida.ts","SBT");
		EPGConstructor.readEPGList("SBT", 2, tsChecker.getEPGList());

		listProgramsByBroadCaster();
		
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
