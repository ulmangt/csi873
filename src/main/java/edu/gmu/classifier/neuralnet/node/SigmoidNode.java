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
	@Override
	public void calculateError( )
	{
		double sum = 0.0;
		
		for ( Link outputLink : outputLinks )
		{
			sum += outputLink.getWeight( ) * outputLink.getOutputNode( ).getError( );
		}
		
		error = output * ( 1 - output ) * sum;
	}

	// assumes output is up to date
	@Override
	public void calculateError( double truth )
	{
		error = output * ( 1 - output ) * ( truth - output );
	}
	
	@Override
	public void updateInputWeights( double learningRate )
	{
		for ( Link inputLink : getInputLinks( ) )
		{
			double weight = inputLink.getWeight( );
			double x = inputLink.getInputNode( ).getOutput( );
			double error = getError( );
			
			double delta_weight = learningRate * error * x;
		
			inputLink.setWeight( weight + delta_weight );
		}
	}
}