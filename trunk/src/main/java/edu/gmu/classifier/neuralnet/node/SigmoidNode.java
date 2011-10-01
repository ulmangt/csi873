package edu.gmu.classifier.neuralnet.node;

import static java.lang.Math.*;

public class SigmoidNode extends AbstractNode
{
	@Override
	public double outputFunction( double net )
	{
		return 1.0 / ( 1.0 + exp( -net ) );
	}
}
