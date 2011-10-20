package edu.gmu.classifier.neuralnet.util;

import java.util.List;

import edu.gmu.classifier.neuralnet.net.Net;
import edu.gmu.classifier.neuralnet.train.Backpropagation.TrainingExample;

public class NeuralNetUtils
{
	/**
	 * @param net the network to use to classify the training examples
	 * @param testData 
	 * @return the percentage of correctly classified examples for the provided list of data
	 */
	public static double calculateError( Net net, List<TrainingExample> testData )
	{
		int totalCount = testData.size( );
		int correctCount = 0;
		
		for ( TrainingExample data : testData )
		{
			double[] output = net.calculateOutput( data.getInputs( ) );
		
			if ( getLargestIndex( output ) == getLargestIndex( data.getOutputs( ) ) )
			{
				correctCount++;
			}
		}
		
		return 1.0 - ( (double) correctCount / (double) totalCount );
	}
	
	/**
	 * @param array
	 * @return the index of the largest entry in the array
	 */
	public static int getLargestIndex( double[] array )
	{
		double max = Double.NEGATIVE_INFINITY;
		int index = 0;
		
		for ( int i = 0 ; i < array.length ; i++ )
		{
			double data = array[i];
			
			if ( data > max )
			{
				max = data;
				index = i;
			}
		}
		
		return index;
	}
}
