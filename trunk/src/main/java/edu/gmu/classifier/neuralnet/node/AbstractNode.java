package edu.gmu.classifier.neuralnet.node;

public abstract class AbstractNode implements Node
{
	protected double output;
	protected double net;
	protected Node[] inputs; 
	protected double[] weights;
	
	public AbstractNode( )
	{
		this.inputs = new Node[0];
		this.weights = new double[0];
	}
	
	public AbstractNode( Node[] inputs )
	{
		this.weights = new double[ inputs.length ];
	}
	
	public AbstractNode( Node[] inputs, double[] weights )
	{
		if ( inputs.length != weights.length )
		{
			throw new RuntimeException( "Input and weight arrays must be the same length." );
		}
		
		this.inputs = inputs;
		this.weights = weights;
	}
	
	public abstract double outputFunction( double net );
	
	public void setOutput( )
	{
		double sum = 0.0;
		
		for ( int i = 0 ; i < inputs.length ; i++ )
		{
			sum += inputs[i].getOutput( ) * weights[i];
		}
		
		net = sum;
		output = outputFunction( net );
	}

	public void setOutput( double value )
	{
		output = value;
	}

	public double getOutput( )
	{
		return output;
	}

	public double getNet( )
	{
		return net;
	}

	public int getInputCount( )
	{
		return inputs.length;
	}

	public double getWeight( int i )
	{
		return weights[i];
	}

	public void setWeight( int i, double weight )
	{
		weights[i] = weight;
	}

	public Node getInput( int i )
	{
		return inputs[i];
	}
}
