package br.ufpe.cin.tool.db;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.config.internal.ConfigurationServiceImpl;
import org.hibernate.service.config.spi.ConfigurationService;

	/**
	 * Controls the session and the transactions. Thread safe.
	 */
	public class DatabaseManager {

		private static DatabaseManager db = null;
		private Configuration cfg = null;
		
		private static SessionFactory sessionFactory = null;
		//private static final ThreadLocal<Session> sessionThread = new ThreadLocal<Session>();
		//private static final ThreadLocal<Transaction> transactionThread = new ThreadLocal<Transaction>();
		
		public static synchronized DatabaseManager getInstance(){
			if(db == null){
				db = new DatabaseManager();
			}
			
			return db;
		}
		
		/**
		 * Initializes the session factory
		 */
		private DatabaseManager() {
			try {
				File file = new File("./hibernate.cfg.xml");
				if (file.exists()) {
					cfg = new Configuration().configure(file);  
					ConfigurationService configurationService = new ConfigurationServiceImpl(cfg.getProperties());  
					ServiceRegistryBuilder serviceRegistry = new ServiceRegistryBuilder().applySettings(configurationService.getSettings());  
					ServiceRegistry registry = serviceRegistry.buildServiceRegistry(); 
					sessionFactory = cfg.buildSessionFactory(registry); 
				} else {
					System.out.println("File not found.");
				}
			} catch (Throwable ex) {
				System.out.println("Initial SessionFactory creation failed. " + ex.getMessage());
				throw new ExceptionInInitializerError(ex);
			}
		}
		
		/**
		 * Get the current session of an instance
		 * @return
		 * 		current Session
		 */
		public Session getSession(){
			return sessionFactory.getCurrentSession();
		}

		/**
		 * Closes the instance's session
		 */
		public void closeSession(){
			getSession().close();
		}
		
		/**
		 * Begin a transaction for the current instance
		 * @return 
		 */
		public boolean beginTransaction(){
			try{
				getSession().beginTransaction();
				return true;
			}catch(Exception e){
				return false;
			}
		}
		
		/**
		 * Commit the current instance's transaction
		 */
		public void commit(){
			getSession().getTransaction().commit();
		}
		
		/**
		 * Rollback the current instance's transaction
		 */
		public void rollback(){
			getSession().getTransaction().rollback();
		}
		
		public Object merge(Object obj){
			return getSession().merge(obj);
		}

		public void flush() {
			getSession().flush();
		}
		
	}

