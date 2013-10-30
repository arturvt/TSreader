package br.ufpe.cin.tool.core;

import java.util.List;

import br.ufpe.cin.tool.db.DataBaseFacade;
import br.ufpe.cin.tool.db.dao.Broadcaster;
import br.ufpe.cin.tool.db.dao.EpgEvent;
import br.ufpe.cin.tool.db.dao.Program;
import br.ufpe.cin.tool.mpegts.TSChecker;

public class Main {


	private static void createEpgEvent() {
		DataBaseFacade session = DataBaseFacade.getInstance();
		if (session.beginTransaction()) {
			System.out.println("Go on");
		} else {
			return;
		}

		Program prog = session.getProgram("Progsa");
		if (prog != null) {
			System.out.println("Program: " + prog.getName());

			EpgEvent event = new EpgEvent();
			event.setStartdate("30/10/2013");
			event.setStarttime("13:00:00");
			event.setDurationtime("00:45:00");
			event.setDescriptor("Programa diário do Ratinho");
			event.setProgram(prog);
			session.save(event);
			session.commit();
			session.closeSession();
		} else {
			System.out.println("Program not found!");
		}
	}
	
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
		tsChecker.loadTS("D:\\TSs\\TSS\\Globo-2013-10-28-23h29m49s-Tela Quente.ts","Globo");
		EPGConstructor.readEPGList("Globo", 13, tsChecker.getEPGList());

		tsChecker.loadTS("D:\\TSs\\TSS\\SBT-2013-10-28-22h40m44#Programa do Ratinho.ts","SBT");
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
