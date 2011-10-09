package edu.gmu.classifier.neuralnet.train;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.gmu.classifier.neuralnet.net.Net;
import edu.gmu.classifier.neuralnet.node.Link;
import edu.gmu.classifier.neuralnet.node.Node;
import edu.gmu.classifier.neuralnet.node.NodeFunction;
import edu.gmu.classifier.neuralnet.node.NodeFunctions;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

import com.google.common.collect.ArrayListMultimap;

public class Backpropagation
{	
	public static class TrainingExample
	{
		protected double[] inputs;
		protected double[] outputs;
		
		public TrainingExample( double[] inputs, double[] outputs )
		{
			this.inputs = inputs;
			this.outputs = outputs;
		}
		
		public double[] getInputs( )
		{
			return inputs;
		}
		
		public void setInputs( double[] inputs )
		{
			this.inputs = inputs;
		}
		
		public double[] getOutputs( )
		{
			return outputs;
		}
		
		public void setOutputs( double[] outputs )
		{
			this.outputs = outputs;
		}
	}
	
	int counter = 0;
	
	public void train( Net net, List<TrainingExample> trainData, List<TrainingExample> testData, double learningRate, double momentum )
	{
		// populate map to save weights
		
		final ArrayListMultimap<Link,Double> weightMap = ArrayListMultimap.create( );
		final ArrayList<Double> testErrorList = new ArrayList<Double>( );
		final ArrayList<Double> trainErrorList = new ArrayList<Double>( );
		
		counter = 0;
		
		while ( !stop( net ) )
		{
			System.out.println( counter );
			
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
			
			double testError = calculateError( net, testData );
			double trainError = calculateError( net, trainData );
			
			testErrorList.add( testError );
			trainErrorList.add( trainError );
		}
		
		printWeights( net );
		
		// create jfreechart dataset for plotting purposes
		double[][] testSeriesData = new double[2][testErrorList.size( )];
		double[][] trainSeriesData = new double[2][trainErrorList.size( )];
		DefaultXYDataset dataset = new DefaultXYDataset( );
		
		for( int i = 0 ; i < testErrorList.size( ) ; i++ )
		{
			testSeriesData[0][i] = i;
			testSeriesData[1][i] = testErrorList.get( i );
			
			dataset.addSeries( "Test Error", testSeriesData );
		}
		
		for( int i = 0 ; i < trainErrorList.size( ) ; i++ )
		{
			trainSeriesData[0][i] = i;
			trainSeriesData[1][i] = trainErrorList.get( i );
			
			dataset.addSeries( "Train Error", trainSeriesData );
		}
		
		JFreeChart chart = ChartFactory.createXYLineChart( "Test and Training Error", "Iteration", "Error", dataset, PlotOrientation.VERTICAL, true, false, false );
		ChartPanel chartPanel = new ChartPanel( chart );
		JFrame frame = new JFrame( );
		frame.setSize( 1000, 1000 );
		frame.add( chartPanel );
		frame.setVisible( true );
		
		// create jfreechart dataset for plotting purposes
		DefaultXYDataset dataset2 = new DefaultXYDataset( );
		for( Link link : weightMap.keySet( ) )
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
		
		JFreeChart chart2 = ChartFactory.createXYLineChart( "Network Weights", "Iteration", "Weight", dataset2, PlotOrientation.VERTICAL, false, false, false );
		ChartPanel chartPanel2 = new ChartPanel( chart2 );
		JFrame frame2 = new JFrame( );
		frame2.setSize( 1000, 1000 );
		frame2.add( chartPanel2 );
		frame2.setVisible( true );
	}
	
	protected double calculateError( Net net, List<TrainingExample> testData )
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
	
	protected int getLargestIndex( double[] array )
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
	
	protected void printWeights( int iteration, final Net net, TrainingExample data )
	{
		System.out.printf( "Iteration %d Training Example %s %s%n", iteration, Arrays.toString( data.getInputs( ) ), Arrays.toString( data.getOutputs( ) ) );
		printWeights( net );
	}
	
	protected void printWeights( final Net net )
	{
		net.apply( new NodeFunction( )
		{
			@Override
			public void run( Node node )
			{
				for ( Link link : node.getInputLinks( ) )
				{
					System.out.printf( "%s (%.5f)%n", link, link.getWeight( ) );
				}
			}
		});
	}
	
	public boolean stop( Net net )
	{
		return counter++ > 1000;
	}
}
