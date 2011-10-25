package edu.gmu.classifier.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * Connects to an sql database and maintains a pool of open database connections.
 * These connections are used to interface with the CSI710 handwriting image
 * visualization tool.
 * 
 * @author ulman
 */
public class DatabaseManager
{
	private static final DatabaseManager instance = new DatabaseManager( );

	private static final String defaultHost = "localhost";
	private static final String defaultPort = "3306";
	private static final String defaultUser = "test";
	private static final String defaultPass = "test";
	
	public DatabaseManager( )
	{
		String host = defaultHost;
		String port = defaultPort;
		String user = defaultUser;
		String pass = defaultPass;
		
		try
		{
			// see example at http://svn.apache.org/viewvc/commons/proper/dbcp/trunk/doc/ManualPoolingDriverExample.java?view=markup
			ObjectPool connectionPool = new GenericObjectPool(null);
			ConnectionFactory connectionFactory = new DriverManagerConnectionFactory( String.format( "jdbc:mysql://%s:%s", host, port ), user, pass );
			new PoolableConnectionFactory(connectionFactory,connectionPool,null,null,false,true);
			Class.forName("org.apache.commons.dbcp.PoolingDriver");
			PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
			driver.registerPool("pool",connectionPool);
		}
		catch ( ClassNotFoundException e )
		{
			e.printStackTrace();
		}
		catch ( SQLException e )
		{
			e.printStackTrace();
		}
	}
	
	public Connection getConnection( ) throws SQLException
	{
		return DriverManager.getConnection( "jdbc:apache:commons:dbcp:pool" );
	}

	public static DatabaseManager getInstance( )
	{
		return instance;
	}
}
