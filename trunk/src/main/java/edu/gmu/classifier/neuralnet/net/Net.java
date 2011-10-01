package edu.gmu.classifier.neuralnet.net;

import edu.gmu.classifier.neuralnet.node.Node;
import edu.gmu.classifier.neuralnet.node.NodeFunction;

/**
 * A collection of Nodes combined to create a neural network learner.
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
	 * Applies the provided array of inputs to the net and produces an output array.
	 * @param input an input array of size getNodeCount( 0 )
	 * @return an output array of size getNodeCount( getLayerCount( ) )
	 */
	public double[] classify( double[] input );
	
	/**
	 * Applies the provided function to all nodes in the network.
	 * @param function
	 */
	public void apply( NodeFunction function );
}
