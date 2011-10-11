package edu.gmu.classifier.neuralnet.node;

import java.util.LinkedList;
import java.util.List;

/**
 * A basic implementation of the Node interface which does not
 * define the output/activation function for the node or how
 * to calculate the node's error.
 * 
 * @author ulman
 *
 */
public abstract class AbstractNode implements Node
{
	protected String name;
	protected double error;
	protected double output;
	protected double net;
	protected List<Link> inputLinks;
	protected List<Link> outputLinks;
	
	/**
	 * Construct a node based on a set of input and output links.
	 * @param inputLinks
	 * @param outputLinks
	 */
	public AbstractNode( List<Link> inputLinks, List<Link> outputLinks )
	{
		this.inputLinks = inputLinks;
		this.outputLinks = outputLinks;
	}
	
	public AbstractNode( )
	{
		this( new LinkedList<Link>( ), new LinkedList<Link>( ) );
	}
	
	/**
	 * Given the weighted sum of the node's input values, calculates
	 * an output value based upon the node's activation function
	 * (step, sigmoid, tanh, etc...)
	 */
	public abstract double outputFunction( double net );
	
	/**
	 * Sets the stored net (sum of weighted inputs) and output
	 * values for this node based on the current input values of
	 * this node's input nodes.
	 */
	@Override
	public void calculateOutput( )
	{
		if ( inputLinks.isEmpty( ) )
			return;
		
		double sum = 0.0;
		
		for ( Link link : inputLinks )
		{
			sum += link.getWeight( ) * link.getInputNode( ).getOutput( );
		}
		
		net = sum;
		output = outputFunction( net );
	}
	
	@Override
	public String getName( )
	{
		return name;
	}
	
	@Override
	public void setName( String name )
	{
		this.name = name;
	}
	 	
	@Override
	public void setError( double error )
	{
		this.error = error;
	}
	
	@Override
	public double getError( )
	{
		return error;
	}

	@Override
	public void setOutput( double value )
	{
		output = value;
	}

	@Override
	public double getOutput( )
	{
		return output;
	}

	@Override
	public double getNet( )
	{
		return net;
	}
	
	@Override
	public List<Link> getInputLinks( )
	{
		return this.inputLinks;
	}
	
	@Override
	public List<Link> getOutputLinks( )
	{
		return this.outputLinks;
	}
	
	@Override
	public void addInputLink( Link link )
	{
		this.inputLinks.add( link );
	}
	
	@Override
	public void addOutputLink( Link link )
	{
		this.outputLinks.add( link );
	}
	
	@Override
	public String toString( )
	{
		return getName( );
	}
}
