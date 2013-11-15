package br.ufpe.cin.tool.db.dao.epg;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.ufpe.cin.tool.db.DatabaseManager;
import br.ufpe.cin.tool.db.dao.DAO;
import br.ufpe.cin.tool.db.entities.AssociatedContext;

public class AssociatedContextDAO implements DAO<AssociatedContext> {

	@Override
	public void saveOrUpdate(AssociatedContext object) {
		DatabaseManager manager = DatabaseManager.getInstance();

		manager.getSession().saveOrUpdate(object);
	}

	@Override
	public AssociatedContext search(long id) {
		DatabaseManager manager = DatabaseManager.getInstance();

		AssociatedContext associatedContext = (AssociatedContext) manager.getSession().get(AssociatedContext.class, id);

		return associatedContext;
	}

	@Override
	public List<AssociatedContext> searchAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AssociatedContext seachByName(String name) {
		DatabaseManager manager = DatabaseManager.getInstance();
		
		Criteria criteria = manager.getSession().createCriteria(AssociatedContext.class);
		criteria.add(Restrictions.eq("name", name));

		@SuppressWarnings("unchecked")
		List<AssociatedContext> result = criteria.list();
				
		if(result.isEmpty()){
			return null;
		}
		
		return result.get(0);
	}

	
	
}
