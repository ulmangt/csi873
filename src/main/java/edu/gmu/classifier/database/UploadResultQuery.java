package edu.gmu.classifier.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UploadResultQuery
{
	protected int ixData;
	protected int ixRun;
	protected String sClassification;
	
	public UploadResultQuery( int ixData, int ixRun, String sClassification )
	{
		this.ixData = ixData;
		this.ixRun = ixRun;
		this.sClassification = sClassification;
	}
	
	public void runQuery( )
	{
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try
		{
			connection = DatabaseManager.getInstance( ).getConnection( );
			statement = connection.prepareStatement( "INSERT INTO Handwriting.Result (ixData, ixRun, sClassification) VALUES (?,?,?)" );
			statement.setInt( 1, ixData );
			statement.setInt( 2, ixRun );
			statement.setString( 3, sClassification );
			statement.execute( );

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
}
