package edu.gmu.classifier.database;

import java.util.List;

import edu.gmu.classifier.neuralnet.net.Net;
import edu.gmu.classifier.neuralnet.train.Backpropagation.TrainingExample;
import edu.gmu.classifier.neuralnet.util.NeuralNetUtils;

public class ResultsUploader
{
	public static final int IX_TRAIN_FIRST_INDEX = 171585;
	public static final int IX_TEST_FIRST_INDEX = 171175;
	
	public void uploadResults( Net net, String description, List<TrainingExample> list, int firstDataId )
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
}
