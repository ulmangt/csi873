package edu.gmu.classifier.database;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.gmu.classifier.io.DataLoader;
import edu.gmu.classifier.io.TrainingExample;

/**
 * A utility class which can take the handwriting data files loaded by
 * edu.gmu.classifier.io.DataLoader and place them into an sql database
 * of the format required by the CSI710 handwriting viewer application.
 * 
 * @author ulman
 */
public class DatabaseUploader
{
	public static final int IX_TRAIN_DATA_SET = 4;
	public static final int IX_TEST_DATA_SET = 5;
	
	public static void uploadData( List<TrainingExample> dataList, int ixDataSet )
	{
		for ( TrainingExample data : dataList )
		{
			byte[] input = convertInputArray( data.getInputs( ) );
			UploadDataQuery query = new UploadDataQuery( ixDataSet, data.getCharacter( ), DataLoader.X_SIZE, DataLoader.Y_SIZE, input );
			query.runQuery( );
		}
	}
	
	protected static byte[] convertInputArray( double[] input )
	{
		byte[] returnArray = new byte[ input.length ];
		
		for ( int i = 0 ; i < input.length ; i++ )
		{
			if ( input[i] == 0.0 )
			{
				returnArray[i] = (byte) 0;
			}
			else if ( input[i] == 1.0 )
			{
				returnArray[i] = (byte) -1;
			}
		}
		
		return returnArray;
	}
	
	public static void main( String[] args ) throws IOException
	{
		// list the test data files
		File dataDirectory = new File( "/home/ulman/CSI873/midterm/data" );
		String[] testDataFiles = dataDirectory.list( new FilenameFilter( )
		{
			@Override
			public boolean accept( File dir, String name )
			{
				return name.startsWith( "test" );
			}
		});

		// sort the testDataFiles
		Arrays.sort( testDataFiles );
		
		// list the training data files
		String[] trainingDataFiles = dataDirectory.list( new FilenameFilter( )
		{
			@Override
			public boolean accept( File dir, String name )
			{
				return name.startsWith( "train" );
			}
		});
		
		// sort the trainingDataFiles
		Arrays.sort( trainingDataFiles );
		
		// load all test data examples
		List<TrainingExample> testDataList = new ArrayList<TrainingExample>( );
		for ( String fileName : testDataFiles )
		{
			testDataList.addAll( DataLoader.loadFile( new File( dataDirectory, fileName ) ) );
		}
		
		// load all training data examples
		List<TrainingExample> trainingDataList = new ArrayList<TrainingExample>( );
		for ( String fileName : trainingDataFiles )
		{
			trainingDataList.addAll( DataLoader.loadFile( new File( dataDirectory, fileName ) ) );
		}
		
		uploadData( testDataList, IX_TEST_DATA_SET );
		uploadData( trainingDataList, IX_TRAIN_DATA_SET );
	}
}
