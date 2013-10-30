package br.ufpe.cin.tool.db.dao;

// Generated Oct 29, 2013 11:09:50 AM by Hibernate Tools 4.0.0

import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;

import br.ufpe.cin.tool.db.DataBaseFacade;
import br.ufpe.cin.tool.db.DatabaseManager;

/**
 * Home object for domain model class Terms.
 * @see br.ufpe.cin.tool.db.dao.Terms
 * @author Hibernate Tools
 */
public class TermsHome {

	private static final Log log = LogFactory.getLog(TermsHome.class);

	private DatabaseManager sessionFactory = null;

	protected DatabaseManager getSessionFactory() {
		if (sessionFactory == null) {
			sessionFactory =  DatabaseManager.getInstance();
		}
		return sessionFactory;
	}

	public void persist(Terms transientInstance) {
		log.debug("persisting Terms instance");
		try {
			sessionFactory.getSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Terms instance) {
		log.debug("attaching dirty Terms instance");
		try {
			getSessionFactory().getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Terms instance) {
		log.debug("attaching clean Terms instance");
		try {
			sessionFactory.getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Terms persistentInstance) {
		log.debug("deleting Terms instance");
		try {
			sessionFactory.getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Terms merge(Terms detachedInstance) {
		log.debug("merging Terms instance");
		try {
			Terms result = (Terms) sessionFactory.getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Terms findById(int id) {
		log.debug("getting Terms instance with id: " + id);
		try {
			Terms instance = (Terms) sessionFactory.getSession().get(
					"br.ufpe.cin.tool.db.dao.Terms", id);
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

	public List<Terms> findByExample(Terms instance) {
		log.debug("finding Terms instance by example");
		try {
			List<Terms> results = (List<Terms>) sessionFactory
					.getSession()
					.createCriteria("br.ufpe.cin.tool.db.dao.Terms")
					.add(create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
