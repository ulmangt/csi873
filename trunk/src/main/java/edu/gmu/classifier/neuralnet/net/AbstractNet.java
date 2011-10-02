package edu.gmu.classifier.neuralnet.net;

import java.util.ArrayList;
import java.util.List;

import edu.gmu.classifier.neuralnet.node.Link;
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
				nodeList.add( createNode( ) );
			}
		}
		
		// connect each node in the current layer to each node in the previous layer
		for ( int layerIndex = 1 ; layerIndex < layerCount ; layerIndex++ )
		{
			List<Node> currentList = layerList.get( layerIndex );
			List<Node> previousList = layerList.get( layerIndex-1 );
		
			for ( Node currentNode : currentList )
			{
				for ( Node previousNode : previousList )
				{
					Link link = new Link( previousNode, currentNode );
					previousNode.addOutputLink( link );
					currentNode.addInputLink( link );
				}
			}
		}
		
		// add threshold nodes to each layer which are not connected to any previous
		// layers, set their output to 1 (which should never be changed)
		for ( int layerIndex = 1 ; layerIndex < layerCount ; layerIndex++ )
		{
			List<Node> currentList = layerList.get( layerIndex );
			
			Node thresholdNode = createNode( );
			thresholdNode.setOutput( 1.0 );
			
			for ( Node node : currentList )
			{
				Link link = new Link( thresholdNode, node );
				thresholdNode.addOutputLink( link );
				node.addInputLink( link );
			}
			
			List<Node> previousList = layerList.get( layerIndex-1 );
			previousList.add( thresholdNode );
		}
		
		apply( NodeFunctions.setWeights( 0.0 ) );
	}
	
	public abstract Node createNode( );
	
	public List<Node> getLayer( int layer )
	{
		return layerList.get( layer );
	}
	
	public List<Node> getInputLayer( )
	{
		return getLayer( 0 );
	}
	
	public List<Node> getOutputLayer( )
	{
		return getLayer( getLayerCount( ) );
	}
	
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

	public double[] calculateOutput( double... input )
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
				node.calculateOutput( );
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
