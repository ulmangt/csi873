package edu.gmu.classifier.database;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

public class UploadDataQuery
{
	protected int ixDataSet;
	protected String sCharacter;
	protected int iRows;
	protected int iCols;
	protected byte[] bData;
	
	protected int ixData;
	
	public UploadDataQuery( int ixDataSet, String sCharacter, int iRows, int iCols, byte[] bData )
	{
		this.ixDataSet = ixDataSet;
		this.sCharacter = sCharacter;
		this.iRows = iRows;
		this.iCols = iCols;
		this.bData = bData;
	}
	
	public void runQuery( )
	{
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try
		{
			connection = DatabaseManager.getInstance( ).getConnection( );
			statement = connection.prepareStatement( "INSERT INTO Handwriting.Data (ixDataSet, sCharacter, iRows, iCols, bData) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS );
			statement.setInt( 1, ixDataSet );
			statement.setString( 2, sCharacter );
			statement.setInt( 3, iRows );
			statement.setInt( 4, iCols );
			statement.setBlob( 5, new ByteArrayInputStream( bData ) );
			statement.execute( );
			
			ResultSet rs = statement.getGeneratedKeys( );
			if ( rs.next( ) )
				ixData = rs.getInt( 1 );

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
	
	public int getDataId( )
	{
		return ixData;
	}
}
