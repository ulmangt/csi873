package edu.gmu.classifier.neuralnet.train;

import java.util.List;
import java.util.Map;

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
	
	Map<Link,List<Double>> weights;
	
	int counter = 0;
	
	public void train( Net net, List<TrainingExample> dataList, double learningRate, double momentum )
	{
		// populate map to save weights
		
		final ArrayListMultimap<Link,Double> weightMap = ArrayListMultimap.create( );
		
		counter = 0;
		
		while ( !stop( net ) )
		{
			for ( TrainingExample data : dataList )
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
				
				// print out weights
				//System.out.printf( "Iteration %d Training Example %s %s%n", counter, Arrays.toString( data.getInputs( ) ), Arrays.toString( data.getOutputs( ) ) );
				net.apply( new NodeFunction( )
				{
					@Override
					public void run( Node node )
					{
						for ( Link link : node.getInputLinks( ) )
						{
							weightMap.put( link, link.getWeight( ) );
							//System.out.printf( "%s (%.5f)%n", link, link.getWeight( ) );
						}
					}
				});
			}
		}
		
		// create jfreechart dataset for plotting purposes
		DefaultXYDataset dataset = new DefaultXYDataset( );
		for( Link link : weightMap.keySet( ) )
		{
			List<Double> list = weightMap.get( link );
			double[][] seriesData = new double[2][list.size( )];
			for ( int i = 0 ; i < list.size( ) ; i++ )
			{
				seriesData[0][i] = i;
				seriesData[1][i] = list.get( i );
			}
			dataset.addSeries( link.toString( ), seriesData );
		}
		
		JFreeChart chart = ChartFactory.createXYLineChart( "Network Weights", "Iteration", "Weight", dataset, PlotOrientation.VERTICAL, true, false, false );
		ChartPanel chartPanel = new ChartPanel( chart );
		JFrame frame = new JFrame( );
		frame.setSize( 400, 400 );
		frame.add( chartPanel );
		frame.setVisible( true );
	}
	
	public boolean stop( Net net )
	{
		return counter++ > 1000;
	}
}
