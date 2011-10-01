package edu.gmu.classifier.neuralnet.node;

import static java.lang.Math.*;

public class SigmoidNode extends AbstractNode
{
	public SigmoidNode( )
	{
		super( );
	}
	
	public SigmoidNode( Node[] inputs )
	{
		super( inputs );
	}
	
	public SigmoidNode( Node[] inputs, double[] weights )
	{
		super( inputs, weights );
	}
	
	@Override
	public double outputFunction( double net )
	{
		return 1.0 / ( 1.0 + exp( -net ) );
	}
}
