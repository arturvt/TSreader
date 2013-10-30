package br.ufpe.cin.tool.db.dao;

// Generated Oct 29, 2013 11:09:50 AM by Hibernate Tools 4.0.0

import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.criterion.Restrictions;

import br.ufpe.cin.tool.db.DatabaseManager;

/**
 * Home object for domain model class Program.
 * @see br.ufpe.cin.tool.db.dao.Program
 * @author Hibernate Tools
 */
public class ProgramHome {

	private static final Log log = LogFactory.getLog(ProgramHome.class);

	private DatabaseManager sessionFactory = getSessionFactory();

	protected DatabaseManager getSessionFactory() {
		if (sessionFactory == null) {
			sessionFactory = DatabaseManager.getInstance();
		}
		return sessionFactory;	}

	public void persist(Program transientInstance) {
		log.debug("persisting Program instance");
		try {
			getSessionFactory().getSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Program instance) {
		log.debug("attaching dirty Program instance");
		try {
			getSessionFactory().getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Program instance) {
		log.debug("attaching clean Program instance");
		try {
			getSessionFactory().getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Program persistentInstance) {
		log.debug("deleting Program instance");
		try {
			getSessionFactory().getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Program merge(Program detachedInstance) {
		log.debug("merging Program instance");
		try {
			Program result = (Program) getSessionFactory().getSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Program findById(int id) {
		log.debug("getting Program instance with id: " + id);
		try {
			Program instance = (Program) getSessionFactory().getSession().get("br.ufpe.cin.tool.db.dao.Program", id);
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Program> findByExample(Program instance) {
		log.debug("finding Program instance by example");
		try {
			List<Program> results = (List<Program>) getSessionFactory().getSession()
					.createCriteria("br.ufpe.cin.tool.db.dao.Program")
					.add(create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public Program findByName(String name) {
		Criteria criteria = getSessionFactory().getSession().createCriteria(Program.class).add(Restrictions.eq("name", name));

		if (criteria.list().size() > 0) {
			return (Program) criteria.list().get(0);
		}
		return null;
	}
}
