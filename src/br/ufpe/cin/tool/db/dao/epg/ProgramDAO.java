package br.ufpe.cin.tool.db.dao.epg;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.ufpe.cin.tool.db.DatabaseManager;
import br.ufpe.cin.tool.db.dao.DAO;
import br.ufpe.cin.tool.db.entities.Program;

public class ProgramDAO implements DAO<Program> {

	@Override
	public void saveOrUpdate(Program object) {
		DatabaseManager manager = DatabaseManager.getInstance();
		manager.getSession().saveOrUpdate(object);
	}

	@Override
	public Program search(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Program> searchAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Program seachByName(String name) {
		DatabaseManager manager = DatabaseManager.getInstance();
		Criteria criteria = manager.getSession().createCriteria(Program.class)
				.add(Restrictions.eq("name", name));

		if (criteria.list().size() > 0) {
			return (Program) criteria.list().get(0);
		}
		return null;
	}

}
