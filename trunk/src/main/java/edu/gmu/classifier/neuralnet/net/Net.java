package edu.gmu.classifier.neuralnet.net;

import java.util.List;

import edu.gmu.classifier.neuralnet.node.Node;
import edu.gmu.classifier.neuralnet.node.NodeFunction;

/**
 * A collection of Nodes combined to create a neural network learner.
 * Assumes nodes are organized into layers such that nodes in layer n
 * provide outputs only to layer n+1 and receive inputs only from layer
 * n-1. Although this is not strictly a requirement, this assumption
 * simplifies the backpropagation algorithm.
 * @author ulman
 */
public interface Net
{	
	/**
	 * @return the number of layers in the network (not including the input layer)
	 */
	public int getLayerCount( );
	
	/**
	 * @param layer the layer to get the node count for
	 * @return the number of nodes in the specified layer (the input layer is 0)
	 */
	public int getNodeCount( int layer );
	
	/**
	 * @param layer
	 * @param node
	 * @return a particular Node in the network
	 */
	public Node getNode( int layer, int node );
	
	/**
	 * @param layer
	 * @return all the nodes in the specified layer
	 */
	public List<Node> getLayer( int layer );
	
	/**
	 * Returns all the nodes in the input layer. Should be equivalent
	 * to calling getLayer( 0 ).
	 * 
	 * @return all the nodes in the input layer
	 */
	public List<Node> getInputLayer( );
	
	/**
	 * Returns all the nodes in the output layer. Should be equivalent
	 * to calling getLayer( getLayerCount( ) ).
	 * 
	 * @return all the nodes in the input layer
	 */
	public List<Node> getOutputLayer( );
	
	/**
	 * Applies the provided array of inputs to the net and produces an output array.
	 * @param input an input array of size getNodeCount( 0 )
	 * @return an output array of size getNodeCount( getLayerCount( ) )
	 */
	public double[] calculateOutput( double... input );
	
	/**
	 * Applies the provided function to all nodes in the network.
	 * @param function
	 */
	public void apply( NodeFunction function );
}
