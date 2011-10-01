package edu.gmu.classifier.neuralnet.net;

import java.util.ArrayList;
import java.util.List;

import edu.gmu.classifier.neuralnet.node.Node;
import edu.gmu.classifier.neuralnet.node.NodeFunction;
import edu.gmu.classifier.neuralnet.node.NodeFunctions;

public abstract class AbstractNet implements Net
{
	protected List<List<Node>> layerList;
	
	public AbstractNet( int... nodeCount )
	{
		int layerCount = nodeCount.length;
		
		layerList = new ArrayList<List<Node>>( layerCount );
		
		for ( int layerIndex = 0 ; layerIndex < layerCount ; layerIndex++ )
		{
			int count = nodeCount[layerIndex];
			
			List<Node> nodeList = new ArrayList<Node>( count );
			layerList.add( nodeList );
			
			for ( int nodeIndex = 0 ; nodeIndex < count ; nodeIndex++ )
			{
				List<Node> inputNodes = layerIndex == 0 ? new ArrayList<Node>( 0 ) : layerList.get( layerIndex-1 );
				nodeList.add( createNode( inputNodes ) );
			}
		}
		
		apply( NodeFunctions.setWeights( 0.0 ) );
	}
	
	public abstract Node createNode( List<Node> inputNodes );
	
	public int getLayerCount( )
	{
		return layerList.size( ) - 1;
	}

	public int getNodeCount( int layer )
	{
		return layerList.get( layer ).size( );
	}

	public Node getNode( int layer, int node )
	{
		return layerList.get( layer ).get( node );
	}

	public double[] classify( double[] input )
	{
		List<Node> inputNodes = layerList.get( 0 );
		for ( int i = 0 ; i < inputNodes.size( ) ; i++ )
		{
			Node node = inputNodes.get( i );
			node.setOutput( input[i] );
		}
		
		for ( int i = 1 ; i <= getLayerCount( ) ; i++ )
		{
			List<Node> layerNodes = layerList.get( i );
			for ( Node node : layerNodes )
			{
				node.setOutput( );
			}
		}
		
		int outputNodeCount = getNodeCount( getLayerCount( ) );
		List<Node> outputNodes = layerList.get( getLayerCount( ) );
		double[] output = new double[ outputNodeCount ];
		for ( int i = 0 ; i < outputNodeCount ; i++ )
		{
			output[i] = outputNodes.get( i ).getOutput( );
		}
		
		return output;
	}

	public void apply( NodeFunction function )
	{
		for ( List<Node> innerList : layerList )
		{
			for ( Node node : innerList )
			{
				function.run( node );
			}
		}
	}
}
