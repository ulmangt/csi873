package edu.gmu.classifier.neuralnet.net;

import java.util.ArrayList;
import java.util.List;

import edu.gmu.classifier.neuralnet.node.Link;
import edu.gmu.classifier.neuralnet.node.Node;
import edu.gmu.classifier.neuralnet.node.NodeFunction;
import edu.gmu.classifier.neuralnet.node.NodeFunctions;

/**
 * An abstract neural network implementation which provides routines for
 * constructing and accessing a graph representation of a neural network.
 * 
 * @see edu.gmu.classifier.neuralnet.net.SigmoidNet provides a concrete
 *      implemention using preceptrons with a sigmoid activation function
 * @author ulman
 */
public abstract class AbstractNet implements Net
{
	// the layered network structure is stored as a list of lists
	// each outer list entry contains a list of nodes for a single
	// layer
	protected List<List<Node>> layerList;
	
	/**
	 * Constructs a neural network with the provide number of nodes
	 * in each layer.
	 * 
	 * @param nodeCount
	 */
	public AbstractNet( int... nodeCount )
	{
		int layerCount = nodeCount.length;
		
		layerList = new ArrayList<List<Node>>( layerCount );
	
		// construct nodes for each layer
		for ( int layerIndex = 0 ; layerIndex < layerCount ; layerIndex++ )
		{
			// determine the number of nodes in the current layer
			int count = nodeCount[layerIndex];
			
			// build the node list for the current layer
			List<Node> nodeList = new ArrayList<Node>( count );
			layerList.add( nodeList );
			
			// build count nodes and add them to nodeList
			for ( int nodeIndex = 0 ; nodeIndex < count ; nodeIndex++ )
			{
				Node node = createNode( );
				node.setName( layerIndex + "-" + (nodeIndex+1) );
				nodeList.add( node );
			}
		}
		
		// connect each node in the current layer to each node in the previous layer
		for ( int layerIndex = 1 ; layerIndex < layerCount ; layerIndex++ )
		{
			// retrieve the node lists for the current and previous layers
			List<Node> currentList = layerList.get( layerIndex );
			List<Node> previousList = layerList.get( layerIndex-1 );
		
			// create a link for each pair of nodes in the current and previous lists
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
			// get the node list for the current layer
			List<Node> currentList = layerList.get( layerIndex );
			
			// create a threshold node to add to the current layer
			Node thresholdNode = createNode( );
			thresholdNode.setName( (layerIndex-1) + "-" + "0" );
			thresholdNode.setOutput( 1.0 );
			
			// add links between the threshold node and each node in the current layer
			for ( Node node : currentList )
			{
				Link link = new Link( thresholdNode, node );
				thresholdNode.addOutputLink( link );
				node.addInputLink( link );
			}
			
			// add the threshold node to the previous layer
			List<Node> previousList = layerList.get( layerIndex-1 );
			previousList.add( thresholdNode );
		}
		
		// set all the node weights to 0
		apply( NodeFunctions.setWeights( 0.0 ) );
	}
	
	/**
	 * @return a new node
	 */
	public abstract Node createNode( );
	
	@Override
	public List<Node> getLayer( int layer )
	{
		return layerList.get( layer );
	}
	
	@Override
	public List<Node> getInputLayer( )
	{
		return getLayer( 0 );
	}
	
	@Override
	public List<Node> getOutputLayer( )
	{
		return getLayer( getLayerCount( ) );
	}
	
	@Override
	public int getLayerCount( )
	{
		return layerList.size( ) - 1;
	}

	@Override
	public int getNodeCount( int layer )
	{
		return layerList.get( layer ).size( );
	}

	@Override
	public Node getNode( int layer, int node )
	{
		return layerList.get( layer ).get( node );
	}

	/**
	 * Given a set of values for the input nodes, provides
	 * a set of corresponding values for the network's output nodes.<p>
	 * 
	 * The size of the input array should be equal to getInputLayer().size()-1
	 * since the input layer will contain one threshold node (the node at index
	 * getNodeCount( 0 )-1 ). This threshold node always has output value 1.
	 */
	public double[] calculateOutput( double... input )
	{
		// get the node list for the input layer
		List<Node> inputNodes = getInputLayer( );
		
		// the last node in the list is the threshold node which is not provided an input
		assert( input.length == inputNodes.size( )-1 );
		
		// set the output value of the input nodes to be the provided input
		for ( int i = 0 ; i < inputNodes.size( )-1 ; i++ )
		{
			Node node = inputNodes.get( i );
			node.setOutput( input[i] );
		}
		
		// calculate the output values for each layer in order
		for ( int i = 1 ; i <= getLayerCount( ) ; i++ )
		{
			List<Node> layerNodes = layerList.get( i );
			for ( Node node : layerNodes )
			{
				node.calculateOutput( );
			}
		}
		
		// get the calculated output values from the output layer and return them
		List<Node> outputNodes = getOutputLayer( );
		int outputNodeCount = outputNodes.size( );
		double[] output = new double[ outputNodeCount ];
		for ( int i = 0 ; i < outputNodeCount ; i++ )
		{
			output[i] = outputNodes.get( i ).getOutput( );
		}
		
		return output;
	}

	/**
	 * Apply a function to each node in the network.
	 */
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
