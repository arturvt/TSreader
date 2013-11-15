package br.ufpe.cin.tool.db.dao.epg;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.ufpe.cin.tool.db.DatabaseManager;
import br.ufpe.cin.tool.db.dao.DAO;
import br.ufpe.cin.tool.db.entities.Hashtags;

public class HashtagsDAO implements DAO<Hashtags> {

	@Override
	public void saveOrUpdate(Hashtags object) {
		DatabaseManager manager = DatabaseManager.getInstance();
		manager.getSession().saveOrUpdate(object);
	}

	@Override
	public Hashtags search(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Hashtags> searchAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Hashtags seachByName(String name) {
		DatabaseManager manager = DatabaseManager.getInstance();
		Criteria criteria = manager.getSession().createCriteria(Hashtags.class)
				.add(Restrictions.eq("value", name));

		if (criteria.list().size() > 0) {
			return (Hashtags) criteria.list().get(0);
		}
		return null;
	}

}
