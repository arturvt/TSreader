package br.ufpe.cin.tool.db;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.config.internal.ConfigurationServiceImpl;
import org.hibernate.service.config.spi.ConfigurationService;

public class DatabaseManager {

	private static SessionFactory sessionFactory;
	private static DatabaseManager instance = null;

	public DatabaseManager() {
		try {
			File file = new File("./hibernate.cfg.xml");
			// Create the SessionFactory from hibernate.cfg.xml
			Configuration cfg = new Configuration().configure(file);
			ConfigurationService configurationService = new ConfigurationServiceImpl(
					cfg.getProperties());
			ServiceRegistryBuilder serviceRegistry = new ServiceRegistryBuilder()
					.applySettings(configurationService.getSettings());
			ServiceRegistry registry = serviceRegistry.buildServiceRegistry();
			sessionFactory = cfg.buildSessionFactory(registry);
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static synchronized DatabaseManager getInstance() {
		if (instance == null) {
			instance = new DatabaseManager();
		}
		return instance;
	}

	// public static SessionFactory getSessionFactory() {
	// return sessionFactory;
	// }

	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public boolean beginTransaction() {
		try {
			getSession().beginTransaction();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void commit() {
		/*
		 * Transaction transaction = transactionThread.get(); if(transaction !=
		 * null && !transaction.wasCommitted() && !transaction.wasRolledBack()){
		 * transaction.commit(); transactionThread.set(null); }
		 */
		getSession().getTransaction().commit();
	}

	/**
	 * Rollback the current instance's transaction
	 */
	public void rollback() {
		/*
		 * Transaction transaction = transactionThread.get(); if(transaction !=
		 * null && !transaction.wasCommitted() && !transaction.wasRolledBack()){
		 * transaction.rollback(); transactionThread.set(null); }
		 */

		getSession().getTransaction().rollback();
	}

	public Object merge(Object obj) {
		/*
		 * Session session = sessionThread.get(); if(session != null &&
		 * session.isOpen()){ return session.merge(obj); }
		 * 
		 * return null;
		 */

		return getSession().merge(obj);
	}

	public void flush() {
		/*
		 * Session session = sessionThread.get(); if(session != null &&
		 * session.isOpen()){ session.flush(); }
		 */

		getSession().flush();
	}

	public void closeSession() {
		getSession().close();
	}

}
