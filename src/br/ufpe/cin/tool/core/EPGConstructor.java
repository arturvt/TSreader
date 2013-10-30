package br.ufpe.cin.tool.core;

import java.util.ArrayList;
import java.util.HashMap;

import br.ufpe.cin.tool.db.DataBaseFacade;
import br.ufpe.cin.tool.db.dao.Broadcaster;
import br.ufpe.cin.tool.db.dao.EpgEvent;
import br.ufpe.cin.tool.db.dao.Program;
import br.ufpe.cin.tool.mpegts.EPGValues;

public class EPGConstructor {
	
	static HashMap<String, Integer> qntOFProgram = new HashMap<String, Integer>();

	
	public static void readEPGList(String operador, int channelNumber, ArrayList<EPGValues> list) {
		Broadcaster broadCaster = null;
		DataBaseFacade session = DataBaseFacade.getInstance();
		if (session.beginTransaction()) {
			System.out.println("Got access");
		} else {
			System.out.println("Could not access the session.");
			return;
		}
		broadCaster = session.getBroadCaster(operador);
		
		for (EPGValues item:list) {
			if (broadCaster == null) {
				broadCaster = new Broadcaster(operador, channelNumber, item.getContryCode(), "port", null);
				session.save(broadCaster);
			}
			Program program = session.getProgram(item.getName());
			if (program == null) {
				program = new Program(broadCaster, item.getName());
				session.save(program);
			}
			
			EpgEvent event = session.getEPGEvent(item.getStartDate(), item.getStartTime(), operador);
			if (event == null) {
				event = new EpgEvent(program,item.getShortDescrition(),item.getStartDate(), item.getStartTime(), item.getDurationTime());
			} else {
				System.out.println("Already contains");
			}
			session.save(event);
		}
		session.commit();
		session.closeSession();
	}
}
