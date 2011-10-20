package edu.gmu.classifier.io;

public class TrainingExample
{
	protected int trueDigit;
	protected double[] inputs;
	protected double[] outputs;
	
	public TrainingExample( double[] inputs, double[] outputs, int trueDigit )
	{
		this.trueDigit = trueDigit;
		this.inputs = inputs;
		this.outputs = outputs;
	}
	
	public double[] getInputs( )
	{
		return inputs;
	}
	
	public void setInputs( double[] inputs )
	{
		this.inputs = inputs;
	}
	
	public double[] getOutputs( )
	{
		return outputs;
	}
	
	public void setOutputs( double[] outputs )
	{
		this.outputs = outputs;
	}
	
	public int getDigit( )
	{
		return this.trueDigit;
	}
	
	public String getCharacter( )
	{
		return String.valueOf( this.trueDigit );
	}
}