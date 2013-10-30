package br.ufpe.cin.tool.db.dao;

// Generated Oct 30, 2013 5:03:56 PM by Hibernate Tools 4.0.0

import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.ufpe.cin.tool.db.DatabaseManager;

/**
 * Home object for domain model class EpgEvent.
 * @see br.ufpe.cin.tool.db.dao.EpgEvent
 * @author Hibernate Tools
 */
public class EpgEventHome {

	private static final Log log = LogFactory.getLog(EpgEventHome.class);

	private  DatabaseManager sessionFactory = getSessionFactory();

	protected DatabaseManager getSessionFactory() {
		if (sessionFactory == null) {
			sessionFactory = DatabaseManager.getInstance();
		}
		return sessionFactory;
	}

	public void persist(EpgEvent transientInstance) {
		log.debug("persisting EpgEvent instance");
		try {
			getSessionFactory().getSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(EpgEvent instance) {
		log.debug("attaching dirty EpgEvent instance");
		try {
			getSessionFactory().getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

//	public void attachClean(EpgEvent instance) {
//		log.debug("attaching clean EpgEvent instance");
//		try {
//			getSessionFactory().getSession().lock(instance, LockMode.NONE);
//			log.debug("attach successful");
//		} catch (RuntimeException re) {
//			log.error("attach failed", re);
//			throw re;
//		}
//	}

	public void delete(EpgEvent persistentInstance) {
		log.debug("deleting EpgEvent instance");
		try {
			getSessionFactory().getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public EpgEvent merge(EpgEvent detachedInstance) {
		log.debug("merging EpgEvent instance");
		try {
			EpgEvent result = (EpgEvent) getSessionFactory().getSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public EpgEvent findById(int id) {
		log.debug("getting EpgEvent instance with id: " + id);
		try {
			EpgEvent instance = (EpgEvent) getSessionFactory().getSession()
					.get("br.ufpe.cin.tool.db.dao.teste.EpgEvent", id);
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
	public List<EpgEvent> findByExample(EpgEvent instance) {
		log.debug("finding EpgEvent instance by example");
		try {
			List<EpgEvent> results = (List<EpgEvent>) getSessionFactory().getSession()
					.createCriteria("br.ufpe.cin.tool.db.dao.teste.EpgEvent")
					.add(create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	@SuppressWarnings("unchecked")
	public EpgEvent findByDateTimeOperator(String startDate, String startTime, String operador) {
		Criteria criteria = getSessionFactory().getSession().createCriteria(EpgEvent.class)
				.add(Restrictions.eq("startdate", startDate))
				.add(Restrictions.eq("starttime", startTime));
		
		List<EpgEvent> results = (List<EpgEvent>) criteria.list();
		
		for(EpgEvent event:results) {
			if(event.getProgram().getBroadcaster().getName().equals(operador)) {
				return event;
			}
		}
		
		return null;
	}
}
