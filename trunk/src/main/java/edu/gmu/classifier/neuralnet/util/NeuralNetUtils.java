package edu.gmu.classifier.neuralnet.util;

import java.util.Arrays;
import java.util.List;

import edu.gmu.classifier.io.TrainingExample;
import edu.gmu.classifier.neuralnet.net.Net;
import edu.gmu.classifier.neuralnet.node.Link;
import edu.gmu.classifier.neuralnet.node.Node;
import edu.gmu.classifier.neuralnet.node.NodeFunction;

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
	
	/**
	 * Prints the iteration number and training example information and the current weights of the network.
	 * 
	 * @param iteration
	 * @param net
	 * @param data
	 */
	public static void printWeights( int iteration, final Net net, TrainingExample data )
	{
		System.out.printf( "Iteration %d Training Example %s %s%n", iteration, Arrays.toString( data.getInputs( ) ), Arrays.toString( data.getOutputs( ) ) );
		printWeights( net );
	}
	
	/**
	 * Prints the current weights of each link in the current network.
	 * 
	 * @param net
	 */
	public static void printWeights( final Net net )
	{
		net.apply( new NodeFunction( )
		{
			@Override
			public void run( Node node )
			{
				for ( Link link : node.getInputLinks( ) )
				{
					System.out.printf( "%s,%s,%.5f%n", link.getInputNode( ), link.getOutputNode( ), link.getWeight( ) );
				}
			}
		});
	}
}
