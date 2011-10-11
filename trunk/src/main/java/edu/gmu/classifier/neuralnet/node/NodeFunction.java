package edu.gmu.classifier.neuralnet.node;

/**
 * A functor used for passing operations to be performed on the nodes
 * of a edu.gmu.classifier.neuralnet.net.Net.
 * 
 * @author ulman
 */
public interface NodeFunction
{
	public void run( Node node );
}
