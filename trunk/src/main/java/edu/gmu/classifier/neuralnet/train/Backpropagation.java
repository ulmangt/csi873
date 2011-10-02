package edu.gmu.classifier.neuralnet.train;

import java.util.List;

import edu.gmu.classifier.neuralnet.net.Net;
import edu.gmu.classifier.neuralnet.node.Node;
import edu.gmu.classifier.neuralnet.node.NodeFunctions;

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
	
	public void train( Net net, List<TrainingExample> dataList, double learningRate, double momentum )
	{
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
			}
		}
	}
	
	public boolean stop( Net net )
	{
		return counter++ > 100;
	}
}
