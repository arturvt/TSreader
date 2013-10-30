package br.ufpe.cin.tool.db;

import java.util.List;

import org.hibernate.Session;

import br.ufpe.cin.tool.db.dao.Broadcaster;
import br.ufpe.cin.tool.db.dao.BroadcasterHome;
import br.ufpe.cin.tool.db.dao.EpgEvent;
import br.ufpe.cin.tool.db.dao.EpgEventHome;
import br.ufpe.cin.tool.db.dao.Program;
import br.ufpe.cin.tool.db.dao.ProgramHome;
import br.ufpe.cin.tool.db.dao.Terms;
import br.ufpe.cin.tool.db.dao.TermsHome;

public class DataBaseFacade {

	private DatabaseManager dataBaseManager = null;
	
	// should put here the dao
	private BroadcasterHome broadCasterDAO = null;
	private TermsHome termsDAO = null;
	private ProgramHome programsDAO = null;
	private EpgEventHome epgEventDAO = null;
	
	public DataBaseFacade() {
		dataBaseManager = DatabaseManager.getInstance();
		broadCasterDAO = new BroadcasterHome();
		termsDAO = new TermsHome();
		programsDAO = new ProgramHome();
		epgEventDAO = new EpgEventHome();
	}
	
	private static DataBaseFacade instance = null;

	public synchronized static DataBaseFacade getInstance() {
		if (instance == null) {
			instance = new DataBaseFacade();
		}

		return instance;
	}

	// *********************
	// Transaction methods
	// *********************
	public boolean beginTransaction() {
		return dataBaseManager.beginTransaction();
	}
	
	public Session getSession() {
		return dataBaseManager.getSession();
	}

	public void commit() {
		DatabaseManager.getInstance().commit();
	}

	public void rollback() {
		DatabaseManager.getInstance().rollback();
	}

	public void closeSession() {
		DatabaseManager.getInstance().closeSession();
	}

	public Object merge(Object obj) {
		return DatabaseManager.getInstance().merge(obj);
	}

	public void flush() {
		DatabaseManager.getInstance().flush();
	}

	// *********************
	// Save Methods
	// *********************
	public void save (EpgEvent event) {
		epgEventDAO.attachDirty(event);
	}
 	public void save(Broadcaster broadCaster) {
		broadCasterDAO.attachDirty(broadCaster);
	}
	
	public void save(Terms term) {
		termsDAO.attachDirty(term);
	}
	
	public void save(Program prog) {
		programsDAO.attachDirty(prog);
	}
	
	// *********************
	// Get by All itens from something
	// *********************
		
	public List<Broadcaster> getAllBroadCasters() {
		return broadCasterDAO.getALL();
	}
	
	// *********************
	// Get by Name methods
	// *********************
	public Broadcaster getBroadCaster(String name) {
		return broadCasterDAO.findByName(name);
	}
	
	public Program getProgram(String name) {
		return programsDAO.findByName(name);
	}
	
	// *********************
	// List Methods
	// *********************
	public void listBroadcasters() {
		for (Broadcaster bro : broadCasterDAO.getALL()) {
			System.out.println("Name: "+bro.getName());
		}
	}

	public EpgEvent getEPGEvent(String startDate, String startTime,
			String operador) {
		return epgEventDAO.findByDateTimeOperator(startDate, startTime, operador);
	}


	

}
