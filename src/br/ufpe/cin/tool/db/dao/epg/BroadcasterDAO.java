package br.ufpe.cin.tool.db.dao.epg;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.ufpe.cin.tool.db.DatabaseManager;
import br.ufpe.cin.tool.db.dao.DAO;
import br.ufpe.cin.tool.db.entities.Broadcaster;

public class BroadcasterDAO implements DAO<Broadcaster>{

	@Override
	public void saveOrUpdate(Broadcaster object) {
		DatabaseManager manager = DatabaseManager.getInstance();

		manager.getSession().saveOrUpdate(object);		
	}

	@Override
	public Broadcaster search(long id) {
		DatabaseManager manager = DatabaseManager.getInstance();
		Criteria criteria = manager.getSession().createCriteria(Broadcaster.class).add(Restrictions.eq("id", id));

		if (criteria.list().size() > 0) {
			return (Broadcaster) criteria.list().get(0);
		}
		return null;
	}

	@Override
	public List<Broadcaster> searchAll() {
		DatabaseManager manager = DatabaseManager.getInstance();
		Criteria criteria = manager.getSession().createCriteria(Broadcaster.class)
				.add(Restrictions.isNotNull("name"));

		@SuppressWarnings("unchecked")
		List<Broadcaster> result = criteria.list();
		
		return result;
	}

	@Override
	public Broadcaster seachByName(String name) {
		DatabaseManager manager = DatabaseManager.getInstance();
		Criteria criteria = manager.getSession().createCriteria(Broadcaster.class).add(Restrictions.eq("name", name));

		if (criteria.list().size() > 0) {
			return (Broadcaster) criteria.list().get(0);
		}

		return null;
	}

}
