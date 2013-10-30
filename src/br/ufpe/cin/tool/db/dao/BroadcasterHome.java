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
 * Home object for domain model class Broadcaster.
 * 
 * @see br.ufpe.cin.tool.db.dao.Broadcaster
 * @author Hibernate Tools
 */
public class BroadcasterHome {

	private static final Log log = LogFactory.getLog(BroadcasterHome.class);

	private DatabaseManager sessionFactory = null;

	protected DatabaseManager getSessionFactory() {
		if (sessionFactory == null) {
			sessionFactory = DatabaseManager.getInstance();
		}
		return sessionFactory;
	}

	public void persist(Broadcaster transientInstance) {
		log.debug("persisting Broadcaster instance");
		try {
			sessionFactory.getSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Broadcaster instance) {
		log.debug("attaching dirty Broadcaster instance");
		try {
			getSessionFactory().getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Broadcaster instance) {
		log.debug("attaching clean Broadcaster instance");
		try {
			sessionFactory.getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Broadcaster persistentInstance) {
		log.debug("deleting Broadcaster instance");
		try {
			sessionFactory.getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Broadcaster merge(Broadcaster detachedInstance) {
		log.debug("merging Broadcaster instance");
		try {
			Broadcaster result = (Broadcaster) sessionFactory.getSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Broadcaster findByName(String name) {
		Criteria criteria = getSessionFactory().getSession().createCriteria(Broadcaster.class).add(Restrictions.eq("name", name));

		if (criteria.list().size() > 0) {
			return (Broadcaster) criteria.list().get(0);
		}

		return null;
	}
	
	public Broadcaster findById(int id) {
		log.debug("getting Broadcaster instance with id: " + id);
		Broadcaster broadCaster = (Broadcaster) getSessionFactory()
				.getSession().get(Broadcaster.class, id);
		return broadCaster;
	}
	
	@SuppressWarnings("unchecked")
	public List<Broadcaster> findByExample (Broadcaster instance) {
		log.debug("finding Program instance by example");
		try {
			List<Broadcaster> results = (List<Broadcaster>) getSessionFactory().getSession()
					.createCriteria("br.ufpe.cin.tool.db.dao.Broadcaster")
					.add(create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List<Broadcaster> getALL() {

		Criteria criteria = getSessionFactory().getSession().createCriteria(Broadcaster.class)
				.add(Restrictions.isNotNull("name"));

		@SuppressWarnings("unchecked")
		List<Broadcaster> result = criteria.list();
		return result;
	}

}
