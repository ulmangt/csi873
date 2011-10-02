package edu.gmu.classifier.neuralnet.node;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractNode implements Node
{
	protected String name;
	protected double error;
	protected double output;
	protected double net;
	protected List<Link> inputLinks;
	protected List<Link> outputLinks;
	
	public AbstractNode( List<Link> inputLinks, List<Link> outputLinks )
	{
		this.inputLinks = inputLinks;
		this.outputLinks = outputLinks;
	}
	
	public AbstractNode( )
	{
		this( new LinkedList<Link>( ), new LinkedList<Link>( ) );
	}
	
	public abstract double outputFunction( double net );
	
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
	
	/*
	@Override
	public String toString( )
	{
		StringBuilder b = new StringBuilder( );
		
		b.append( "[ " );
		
		
		for ( Link link : inputLinks )
		{
			b.append( String.format( "%.3f", link.getWeight( ) ) );
			b.append( " " );
		}
		
		b.append( "]" );
		
		return b.toString( );
	}
	*/
	
	@Override
	public String toString( )
	{
		return getName( );
	}
}
