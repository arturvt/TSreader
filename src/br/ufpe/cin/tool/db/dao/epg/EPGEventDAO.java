package br.ufpe.cin.tool.db.dao.epg;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.ufpe.cin.tool.db.DatabaseManager;
import br.ufpe.cin.tool.db.dao.DAO;
import br.ufpe.cin.tool.db.entities.EpgEvent;

public class EPGEventDAO implements DAO<EpgEvent> {

	@Override
	public void saveOrUpdate(EpgEvent object) {
		DatabaseManager manager = DatabaseManager.getInstance();
		manager.getSession().saveOrUpdate(object);
	}

	@Override
	public EpgEvent search(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EpgEvent> searchAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object seachByName(String name) {
		return null;
	}

	@SuppressWarnings("unchecked")
	public EpgEvent findByDateTimeOperator(String startDate, String startTime,
			String operador) {
		DatabaseManager manager = DatabaseManager.getInstance();
		Criteria criteria = manager.getSession().createCriteria(EpgEvent.class)
				.add(Restrictions.eq("startdate", startDate))
				.add(Restrictions.eq("starttime", startTime));

		List<EpgEvent> results = (List<EpgEvent>) criteria.list();

		for (EpgEvent event : results) {
			if (event.getProgram().getBroadcaster().getName().equals(operador)) {
				return event;
			}
		}

		return null;
	}

}
