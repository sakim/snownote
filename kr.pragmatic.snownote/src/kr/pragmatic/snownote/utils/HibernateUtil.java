package kr.pragmatic.snownote.utils;

import java.io.File;
import java.sql.SQLException;

import org.hibernate.*;
import org.hibernate.cfg.*;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * Startup Hibernate and provide access to the singleton SessionFactory
 */
public class HibernateUtil {

	private static SessionFactory sessionFactory;

	public static void init(String path, String database) throws SQLException {
		Configuration cfg = new Configuration().configure();
		cfg.setProperty("hibernate.connection.url", "jdbc:hsqldb:file:" + path
				+ File.separator + "data" + File.separator + database
				+ ";shutdown=true");

		sessionFactory = cfg.buildSessionFactory();
		Session session = sessionFactory.openSession();

		// Generate schema
		try {
			session.createQuery("select count(a) from SnowPage a").list();
		} catch (SQLGrammarException e) {
			SchemaExport schemaExport = new SchemaExport(cfg);
			schemaExport.create(false, true);
		}

		// HSQL WRITE DELAY OFF
		SQLQuery query = session.createSQLQuery("SET WRITE_DELAY FALSE");
		query.executeUpdate();
	}

	public static SessionFactory getSessionFactory() {
		// Alternatively, we could look up in JNDI here
		return sessionFactory;
	}

	public static void shutdown() {
		// Close caches and connection pools
		getSessionFactory().close();
		// Clear static variables
		sessionFactory = null;
	}
}
