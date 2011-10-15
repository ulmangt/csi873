package edu.gmu.classifier.neuralnet.node;

import static java.lang.Math.*;

import java.util.List;

/**
 * A node implementation which uses a sigmoid as its activation function.
 * @author ulman
 */
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
	
	/**
	 * Given a learning rate and a momentum parameter, calculates new values for the weights
	 * associated with the input links connecting to this node. This calculation is dependent
	 * on the error term calculated for this node, as well as the output values for all the
	 * links feeding into this node and the learning rate and momentum.
	 */
	@Override
	public void updateInputWeights( double learningRate, double momentum )
	{
		// loop over each input link
		for ( Link inputLink : getInputLinks( ) )
		{
			double x = inputLink.getInputNode( ).getOutput( );
			double error = getError( );
			
			// calculate the amount to change the link's weight by
			double delta_weight = learningRate * error * x;
		
			// update the link's weight, possibly adjusting based on the momentum parameter
			inputLink.deltaWeight( delta_weight, momentum );
		}
	}
}
