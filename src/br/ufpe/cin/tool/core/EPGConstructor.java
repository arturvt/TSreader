package br.ufpe.cin.tool.core;

import java.util.ArrayList;
import java.util.HashMap;

import br.ufpe.cin.tool.db.DatabaseFacade;
import br.ufpe.cin.tool.db.entities.AssociatedContext;
import br.ufpe.cin.tool.db.entities.Broadcaster;
import br.ufpe.cin.tool.db.entities.EpgEvent;
import br.ufpe.cin.tool.db.entities.Program;
import br.ufpe.cin.tool.mpegts.EPGValues;

public class EPGConstructor {

	static HashMap<String, Integer> qntOFProgram = new HashMap<String, Integer>();

	public static void readEPGList(String operador, int channelNumber,
			ArrayList<EPGValues> list) {
		if (list == null) {
			System.out.println("Skipping file...");
		}

		DatabaseFacade facade = DatabaseFacade.getInstance();

		int eventsSkippeds = 0;
		int programsSkippeds = 0;
		boolean newTransaction = false;
		for (EPGValues item : list) {
			try {
				if (!newTransaction) {
					newTransaction = facade.beginTransaction();
				}
				Broadcaster broadCaster = null;
				broadCaster = facade.getBroadCaster(operador);
				if (broadCaster == null) {
					broadCaster = new Broadcaster(0, operador, channelNumber,
							item.getContryCode(), "port", null);
					facade.saveOrUpdate(broadCaster);
				}
				Program program = facade.getProgram(item.getName());
				if (program == null) {
					program = new Program(0, broadCaster, item.getName());
					AssociatedContext associatedContext = new AssociatedContext(
							program);
					program.setAssociatedContext(associatedContext);
					facade.saveOrUpdate(program);
					facade.saveOrUpdate(associatedContext);
				} else {
					programsSkippeds++;
				}
	
				EpgEvent event = facade.getEPGEvent(item.getStartDate(),
						item.getStartTime(), operador);
				if (event == null) {
					event = new EpgEvent(program, item.getShortDescrition(),
							item.getStartDate(), item.getStartTime(),
							item.getDurationTime());
					facade.saveOrUpdate(event);
				} else {
					programsSkippeds++;
				}
				if (newTransaction) {
					facade.commit();
					System.out.println("Commited!");
					newTransaction = false;
				}
			} catch (Exception e) {
				if(newTransaction){
					facade.rollback();
				}
				e.printStackTrace();
			}
		}
		if (programsSkippeds != 0) {
			System.out.println("Skipped: " + programsSkippeds + " programs.");
		}
		if (eventsSkippeds != 0) {
			System.out.println("Skipped: " + eventsSkippeds + " events.");
		}
	}
}
