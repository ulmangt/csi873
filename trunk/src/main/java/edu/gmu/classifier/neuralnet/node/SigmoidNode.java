package edu.gmu.classifier.neuralnet.node;

import static java.lang.Math.*;

import java.util.List;

public class SigmoidNode extends AbstractNode
{
	public SigmoidNode( )
	{
		super( );
	}
	
	public SigmoidNode( List<Link> inputLinks, List<Link> outputLinks )
	{
		super( inputLinks, outputLinks );
	}
	
	@Override
	public double outputFunction( double net )
	{
		return 1.0 / ( 1.0 + exp( -net ) );
	}

	// assumes output and downstream errors are up to date
	public void calculateError( )
	{
		double sum = 0.0;
	}

	// assumes output is up to date
	public void calculateError( double truth )
	{
		this.error = this.output * ( 1 - this.output ) * ( truth - this.output );
	}
}
