package edu.gmu.classifier.neuralnet.train;

import static edu.gmu.classifier.neuralnet.util.NeuralNetUtils.calculateError;
import static edu.gmu.classifier.neuralnet.util.NeuralNetUtils.printWeights;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

import com.google.common.collect.ArrayListMultimap;

import edu.gmu.classifier.io.TrainingExample;
import edu.gmu.classifier.neuralnet.net.Net;
import edu.gmu.classifier.neuralnet.node.Link;
import edu.gmu.classifier.neuralnet.node.Node;
import edu.gmu.classifier.neuralnet.node.NodeFunction;
import edu.gmu.classifier.neuralnet.node.NodeFunctions;

/**
 * Implementation of the neural network backpropagation training algorithm. Takes a 
 * previously constructed edu.gmu.classifier.neuralnet.net.Net and TrainingExample
 * instances loaded using edu.gmu.classifier.neuralnet.io.DataLoader and adjusts the
 * weights of the network based on the training examples.
 * 
 * @author ulman
 */
public class Backpropagation
{
	int counter = 0;
	int improveCounter = 0;
	double bestError = 1.0;
	
	/**
	 * Trains the provided network based on the training examples.
	 * 
	 * @param net the network to train. The weights of the links in the network will be adjusted by this call
	 * @param trainData the data set to use to train the network
	 * @param validationData the data set used to verify the effectiveness of the network and determine when to halt training
	 * @param learningRate the step size of take at each iteration of the backpropagation algorithm
	 * @param momentum whether to adjust link weight updates based on the magnitude of the previous update
	 */
	public void train( Net net, List<TrainingExample> trainData, List<TrainingExample> validationData, double learningRate, double momentum )
	{
		// initialize data structures to store error and weight information at each iteration in order to generate plots
		final ArrayListMultimap<Link,Double> weightMap = ArrayListMultimap.create( );
		final ArrayList<Double> validationErrorList = new ArrayList<Double>( );
		final ArrayList<Double> trainErrorList = new ArrayList<Double>( );
		
		// initialize counters
		counter = 0;
		improveCounter = 0;
		bestError = 1.0;
		
		// loop until stopping criterion are met
		while ( !stop( net, validationData ) )
		{
			System.out.printf( "Iteration: %d Since Improvement: %d Best Error: %.3f%n", counter, improveCounter, bestError );
			
			// loop over each training example
			for ( TrainingExample data : trainData )
			{
				// calculate and store node outputs
				net.calculateOutput( data.getInputs( ) );
				
				// calculate error terms for output layer
				double[] truth = data.getOutputs( );
				List<Node> outputNodes = net.getOutputLayer( );
				for ( int i = 0 ; i < outputNodes.size( ) ; i++ )
				{
					Node node = outputNodes.get( i );
					node.calculateError( truth[i] );
				}
			
				// calculate error terms for each hidden layer moving
				// backwards from the output layer
				for ( int layer = net.getLayerCount( )-1 ; layer > 0 ; layer-- )
				{
					List<Node> layerNodes = net.getLayer( layer );
					for ( int i = 0 ; i < layerNodes.size( ) ; i++ )
					{
						Node node = layerNodes.get( i );
						node.calculateError( );
					}
				}
				
				// update weights for all nodes
				net.apply( NodeFunctions.updateWeights( learningRate, momentum ) );
			}
			
			// store weights
			net.apply( new NodeFunction( )
			{
				@Override
				public void run( Node node )
				{
					for ( Link link : node.getInputLinks( ) )
					{
						weightMap.put( link, link.getWeight( ) );
					}
				}
			});
			
			// calculate and store training and validation errors
			double validationError = calculateError( net, validationData );
			double trainError = calculateError( net, trainData );
			
			validationErrorList.add( validationError );
			trainErrorList.add( trainError );
		}
		
		//// print results and plots ////
		
		// print learning rate and momentum parameters
		System.out.printf( "============================================%n" );
		System.out.printf( "LR: %.2f M: %.2f%n", learningRate, momentum ); 
		
		// print network weights
		printWeights( net );
		
		// create jfreechart dataset for plotting purposes
		double[][] validationSeriesData = new double[2][validationErrorList.size( )];
		double[][] trainSeriesData = new double[2][trainErrorList.size( )];
		DefaultXYDataset dataset = new DefaultXYDataset( );
		
		// add data points to validation error vs training iteration plot
		for( int i = 0 ; i < validationErrorList.size( ) ; i++ )
		{
			validationSeriesData[0][i] = i * trainData.size( );
			validationSeriesData[1][i] = validationErrorList.get( i );
			
			dataset.addSeries( "Validation Error", validationSeriesData );
		}
		
		// add data points to training error vs training iteration plot
		for( int i = 0 ; i < trainErrorList.size( ) ; i++ )
		{
			trainSeriesData[0][i] = i * trainData.size( );
			trainSeriesData[1][i] = trainErrorList.get( i );
			
			dataset.addSeries( "Train Error", trainSeriesData );
		}
		
		// display plots
		JFreeChart chart = ChartFactory.createXYLineChart( String.format( "Validation and Training Error (LR: %.2f M: %.2f)", learningRate, momentum), "Iteration", "Error", dataset, PlotOrientation.VERTICAL, true, false, false );
		ChartPanel chartPanel = new ChartPanel( chart );
		JFrame frame = new JFrame( );
		frame.setSize( 1000, 1000 );
		frame.add( chartPanel );
		frame.setVisible( true );
		
		// create jfreechart dataset for plotting purposes
		DefaultXYDataset dataset2 = new DefaultXYDataset( );
		
		Node outputNode0 = net.getOutputLayer( ).get( 0 );
		for ( Link link : outputNode0.getInputLinks( ) )
		//for( Link link : weightMap.keySet( ) )
		{
			List<Double> list = weightMap.get( link );
			double[][] seriesData = new double[2][list.size( )];
			for ( int i = 0 ; i < list.size( ) ; i++ )
			{
				seriesData[0][i] = i;
				seriesData[1][i] = list.get( i );
			}
			dataset2.addSeries( link.toString( ), seriesData );
		}
		
		JFreeChart chart2 = ChartFactory.createXYLineChart( String.format( "Digit 0 Output Node Weights (LR: %.2f M: %.2f)", learningRate, momentum), "Iteration", "Weight", dataset2, PlotOrientation.VERTICAL, true, false, false );
		ChartPanel chartPanel2 = new ChartPanel( chart2 );
		JFrame frame2 = new JFrame( );
		frame2.setSize( 1000, 1000 );
		frame2.add( chartPanel2 );
		frame2.setVisible( true );
	}
	
	/**
	 * Stopping condition function. At each iteration, the network is tested against the validation data set. If
	 * the error has not improved for 50 iterations (where an iteration is one pass through all the training data)
	 * then the training is stopped.
	 * 
	 * @param net the network
	 * @param validationData the validation data set
	 * @return whether or not to stop the algorithm
	 */
	public boolean stop( Net net, List<TrainingExample> validationData )
	{
		counter++;
		
		double validationError = calculateError( net, validationData );
		
		if ( validationError < bestError )
		{
			bestError = validationError;
			improveCounter = 0;
			return false;
		}
		else
		{
			return improveCounter++ > 300;
		}
	}
}
