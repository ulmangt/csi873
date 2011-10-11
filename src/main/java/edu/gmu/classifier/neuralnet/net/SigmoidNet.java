package edu.gmu.classifier.neuralnet.net;

import edu.gmu.classifier.neuralnet.node.Node;
import edu.gmu.classifier.neuralnet.node.SigmoidNode;

/**
 * A neural network implementation which uses sigmoid units for the nodes.
 * @author ulman
 */
public class SigmoidNet extends AbstractNet
{
	public SigmoidNet( int... nodeCount )
	{
		super( nodeCount );
	}

	@Override
	public Node createNode( )
	{
		return new SigmoidNode( );
	}

}
