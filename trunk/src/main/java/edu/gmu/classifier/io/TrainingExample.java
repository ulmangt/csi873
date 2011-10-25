package edu.gmu.classifier.io;

/**
 * A data structure for storing a single handwriting sample. Input arrays suitable for
 * passing into a edu.gmu.classifier.neuralnet.net.Net instance are stored. Similarly,
 * output arrays of the same form as those provided by the Net class are stored.
 * 
 * @author ulman
 */
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