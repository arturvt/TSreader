package br.ufpe.cin.tool.db.dao;

// Generated Oct 29, 2013 11:09:50 AM by Hibernate Tools 4.0.0

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class Epg.
 * @see br.ufpe.cin.tool.db.dao.Epg
 * @author Hibernate Tools
 */
public class EpgHome {

	private static final Log log = LogFactory.getLog(EpgHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext()
					.lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException(
					"Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(Epg transientInstance) {
		log.debug("persisting Epg instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Epg instance) {
		log.debug("attaching dirty Epg instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Epg instance) {
		log.debug("attaching clean Epg instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Epg persistentInstance) {
		log.debug("deleting Epg instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Epg merge(Epg detachedInstance) {
		log.debug("merging Epg instance");
		try {
			Epg result = (Epg) sessionFactory.getCurrentSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Epg findById(int id) {
		log.debug("getting Epg instance with id: " + id);
		try {
			Epg instance = (Epg) sessionFactory.getCurrentSession().get(
					"br.ufpe.cin.tool.db.dao.Epg", id);
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

	public List<Epg> findByExample(Epg instance) {
		log.debug("finding Epg instance by example");
		try {
			List<Epg> results = (List<Epg>) sessionFactory.getCurrentSession()
					.createCriteria("br.ufpe.cin.tool.db.dao.Epg")
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
