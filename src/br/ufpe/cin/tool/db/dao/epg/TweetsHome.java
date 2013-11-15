package br.ufpe.cin.tool.db.dao.epg;

// Generated Oct 29, 2013 11:09:50 AM by Hibernate Tools 4.0.0

import java.util.List;

import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;

import br.ufpe.cin.tool.db.entities.Tweets;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class Tweets.
 * @see br.ufpe.cin.tool.db.entities.Tweets
 * @author Hibernate Tools
 */
public class TweetsHome {

	private static final Log log = LogFactory.getLog(TweetsHome.class);

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

	public void persist(Tweets transientInstance) {
		log.debug("persisting Tweets instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Tweets instance) {
		log.debug("attaching dirty Tweets instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Tweets instance) {
		log.debug("attaching clean Tweets instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Tweets persistentInstance) {
		log.debug("deleting Tweets instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Tweets merge(Tweets detachedInstance) {
		log.debug("merging Tweets instance");
		try {
			Tweets result = (Tweets) sessionFactory.getCurrentSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Tweets findById(int id) {
		log.debug("getting Tweets instance with id: " + id);
		try {
			Tweets instance = (Tweets) sessionFactory.getCurrentSession().get(
					"br.ufpe.cin.tool.db.dao.Tweets", id);
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

	public List<Tweets> findByExample(Tweets instance) {
		log.debug("finding Tweets instance by example");
		try {
			List<Tweets> results = (List<Tweets>) sessionFactory
					.getCurrentSession()
					.createCriteria("br.ufpe.cin.tool.db.dao.Tweets")
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
