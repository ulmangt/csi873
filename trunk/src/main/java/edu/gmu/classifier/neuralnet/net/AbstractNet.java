package edu.gmu.classifier.neuralnet.net;

import java.util.List;

import edu.gmu.classifier.neuralnet.node.Node;
import edu.gmu.classifier.neuralnet.node.NodeFunction;

public abstract class AbstractNet implements Net
{
	protected List<List<Node>> nodeList;
	
	public abstract Node createNode( );
	
	public int getLayerCount( )
	{
		return nodeList.size( ) - 1;
	}

	public int getNodeCount( int layer )
	{
		return nodeList.get( layer ).size( );
	}

	public Node getNode( int layer, int node )
	{
		return nodeList.get( layer ).get( node );
	}

	public double[] classify( double[] input )
	{
		List<Node> inputNodes = nodeList.get( 0 );
		for ( int i = 0 ; i < inputNodes.size( ) ; i++ )
		{
			Node node = inputNodes.get( i );
			node.setOutput( input[i] );
		}
		
		for ( int i = 1 ; i <= getLayerCount( ) ; i++ )
		{
			List<Node> layerNodes = nodeList.get( i );
			for ( Node node : layerNodes )
			{
				node.setOutput( );
			}
		}
		
		int outputNodeCount = getNodeCount( getLayerCount( ) );
		List<Node> outputNodes = nodeList.get( getLayerCount( ) );
		double[] output = new double[ outputNodeCount ];
		for ( int i = 0 ; i < outputNodeCount ; i++ )
		{
			output[i] = outputNodes.get( i ).getOutput( );
		}
		
		return output;
	}

	public void apply( NodeFunction function )
	{
		for ( List<Node> innerList : nodeList )
		{
			for ( Node node : innerList )
			{
				function.run( node );
			}
		}
	}
}
