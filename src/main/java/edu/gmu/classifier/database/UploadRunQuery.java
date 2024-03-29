package edu.gmu.classifier.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.mysql.jdbc.Statement;

/**
 * A query which uploads metadata information about a classifier.
 * 
 * @author ulman
 */
public class UploadRunQuery
{
	protected int ixRun;
	protected String sDescription;
	protected long time;

	public UploadRunQuery( String sDescription, long time )
	{
		this.sDescription = sDescription;
		this.time = time;
	}

	public void runQuery( )
	{
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try
		{
			connection = DatabaseManager.getInstance( ).getConnection( );
			statement = connection.prepareStatement( "INSERT INTO Handwriting.Run (sDescription, dtRunDate) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS );
			statement.setString( 1, sDescription );
			statement.setTimestamp( 2, new Timestamp( time ) );
			statement.execute( );

			ResultSet rs = statement.getGeneratedKeys( );
			if ( rs.next( ) ) ixRun = rs.getInt( 1 );

		}
		catch ( SQLException e )
		{
			e.printStackTrace( );
		}
		finally
		{
			if ( resultSet != null ) try
			{
				resultSet.close( );
			}
			catch ( SQLException e )
			{
			}
			if ( statement != null ) try
			{
				statement.close( );
			}
			catch ( SQLException e )
			{
			}
			if ( connection != null ) try
			{
				connection.close( );
			}
			catch ( SQLException e )
			{
			}
		}
	}

	public int getRunId( )
	{
		return ixRun;
	}
}
