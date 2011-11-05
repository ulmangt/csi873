package edu.gmu.classifier.database;

import java.util.List;
import java.util.Map;

import edu.gmu.classifier.io.TrainingExample;
import edu.gmu.classifier.naivebayes.Homework7;
import edu.gmu.classifier.naivebayes.P;
import edu.gmu.classifier.neuralnet.net.Net;
import edu.gmu.classifier.neuralnet.util.NeuralNetUtils;

/**
 * Uploads classification run results to the database.
 * 
 * @author ulman
 */
public class ResultsUploader
{
	public static final int IX_TRAIN_FIRST_INDEX = 171585;
	public static final int IX_TEST_FIRST_INDEX = 171175;
	
	public static void uploadTrainingResults( Net net, List<TrainingExample> list, String description )
	{
		uploadResults( net, list, description, IX_TRAIN_FIRST_INDEX );
	}
	
	public static void uploadTestingResults( Net net, List<TrainingExample> list, String description )
	{
		uploadResults( net, list, description, IX_TEST_FIRST_INDEX );
	}
	
	public static void uploadResults( Net net, List<TrainingExample> list, String description, int firstDataId )
	{
		UploadRunQuery uploadRunQuery = new UploadRunQuery( description, System.currentTimeMillis( ) );
		uploadRunQuery.runQuery( );
		int ixRunId = uploadRunQuery.getRunId( );
		
		for ( int i = 0 ; i < list.size( ); i++ )
		{
			TrainingExample data = list.get( i );
			
			double[] output = net.calculateOutput( data.getInputs( ) );
			String sClassification = String.valueOf( NeuralNetUtils.getLargestIndex( output ) );
			
			UploadResultQuery uploadResultQuery = new UploadResultQuery( firstDataId + i, ixRunId, sClassification );
			uploadResultQuery.runQuery( );
		}
	}
	
	public static void uploadTrainingResults( Map<P, Double> p0map, List<TrainingExample> list, String description )
	{
		uploadResults( p0map, list, description, IX_TRAIN_FIRST_INDEX );
	}
	
	public static void uploadTestingResults( Map<P, Double> p0map, List<TrainingExample> list, String description )
	{
		uploadResults( p0map, list, description, IX_TEST_FIRST_INDEX );
	}
	
	public static void uploadResults( Map<P, Double> p0map, List<TrainingExample> list, String description, int firstDataId )
	{
		UploadRunQuery uploadRunQuery = new UploadRunQuery( description, System.currentTimeMillis( ) );
		uploadRunQuery.runQuery( );
		int ixRunId = uploadRunQuery.getRunId( );
		
		for ( int i = 0 ; i < list.size( ); i++ )
		{
			TrainingExample data = list.get( i );
			
			double[] output = Homework7.calculateOutputLikelihoods( p0map, data );
			String sClassification = String.valueOf( NeuralNetUtils.getLargestIndex( output ) );
			
			UploadResultQuery uploadResultQuery = new UploadResultQuery( firstDataId + i, ixRunId, sClassification );
			uploadResultQuery.runQuery( );
		}
	}
}
