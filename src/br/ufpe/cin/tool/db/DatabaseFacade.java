package br.ufpe.cin.tool.db;

import java.util.List;

import br.ufpe.cin.tool.db.dao.epg.AssociatedContextDAO;
import br.ufpe.cin.tool.db.dao.epg.BroadcasterDAO;
import br.ufpe.cin.tool.db.dao.epg.EPGEventDAO;
import br.ufpe.cin.tool.db.dao.epg.HashtagsDAO;
import br.ufpe.cin.tool.db.dao.epg.ProgramDAO;
import br.ufpe.cin.tool.db.entities.AssociatedContext;
import br.ufpe.cin.tool.db.entities.Broadcaster;
import br.ufpe.cin.tool.db.entities.EpgEvent;
import br.ufpe.cin.tool.db.entities.Hashtags;
import br.ufpe.cin.tool.db.entities.Program;

public class DatabaseFacade {

	private AssociatedContextDAO associatedDAO = new AssociatedContextDAO();
	private BroadcasterDAO broadcasterDAO = new BroadcasterDAO();
	private EPGEventDAO epegEventDAO = new EPGEventDAO();
	private HashtagsDAO hashtagsDAO = new HashtagsDAO();
	private ProgramDAO programDAO = new ProgramDAO();

	private static DatabaseFacade instance = null;

	public DatabaseFacade() {
		super();
	}

	public synchronized static DatabaseFacade getInstance() {
		if (instance == null) {
			instance = new DatabaseFacade();
		}

		return instance;
	}

	// *********************
	// Transaction methods
	// *********************
	public boolean beginTransaction() {
		return DatabaseManager.getInstance().beginTransaction();
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

	/* --- AssociatedEvents Methods --- */
	public void saveOrUpdate(AssociatedContext object) {
		this.associatedDAO.saveOrUpdate(object);
	}

	/* --- BrodasCaster Methods --- */

	public void saveOrUpdate(Broadcaster toSave) {
		this.broadcasterDAO.saveOrUpdate(toSave);
	}

	public Broadcaster getBroadCaster(String operator) {
		return this.broadcasterDAO.seachByName(operator);
	}

	public List<Broadcaster> getAllBroadCasters() {
		return this.broadcasterDAO.searchAll();
	}

	/* --- EPGEvents Methods --- */

	public void saveOrUpdate(EpgEvent object) {
		this.epegEventDAO.saveOrUpdate(object);
	}

	public EpgEvent getEPGEvent(String startDate, String startTime,
			String operador) {
		return this.epegEventDAO.findByDateTimeOperator(startDate, startTime,
				operador);
	}

	/* --- HashTag Methods --- */

	public void saveOrUpdate(Hashtags object) {
		this.hashtagsDAO.saveOrUpdate(object);
	}

	public Hashtags getHashTag(String hashTag) {
		return this.hashtagsDAO.seachByName(hashTag);
	}

	/* --- Program Methods --- */
	public void saveOrUpdate(Program program) {
		this.programDAO.saveOrUpdate(program);
	}

	public Program getProgram(String name) {
		return this.programDAO.seachByName(name);
	}
}
