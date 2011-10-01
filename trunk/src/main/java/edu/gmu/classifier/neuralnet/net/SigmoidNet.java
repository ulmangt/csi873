package edu.gmu.classifier.neuralnet.net;

import java.util.List;

import edu.gmu.classifier.neuralnet.node.Node;
import edu.gmu.classifier.neuralnet.node.SigmoidNode;

public class SigmoidNet extends AbstractNet
{
	public SigmoidNet( int... nodeCount )
	{
		super( nodeCount );
	}

	@Override
	public Node createNode( List<Node> inputNodes )
	{
		return new SigmoidNode( inputNodes.toArray( new Node[inputNodes.size( )] ) );
	}

}
