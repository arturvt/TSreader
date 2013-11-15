package br.ufpe.cin.tool.db.dao;

import java.util.List;

public interface DAO<T> {
	
	public void saveOrUpdate (T object);
	
	public T search (long id);
	
	public List<T> searchAll();
	
	public Object seachByName(String name);
	
}
