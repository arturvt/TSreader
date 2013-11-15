package br.ufpe.cin.tool.db.dao.epg;

import java.util.List;

import br.ufpe.cin.tool.db.DatabaseManager;
import br.ufpe.cin.tool.db.dao.DAO;
import br.ufpe.cin.tool.db.entities.Terms;

public class TermsDAO implements DAO<Terms>{

	@Override
	public void saveOrUpdate(Terms object) {
		DatabaseManager manager = DatabaseManager.getInstance();
		manager.getSession().saveOrUpdate(object);		
	}

	@Override
	public Terms search(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Terms> searchAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object seachByName(String name) {
//		DatabaseManager manager = DatabaseManager.getInstance();
		return null;
	}

}
