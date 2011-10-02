package edu.gmu.classifier.neuralnet.train;

import java.util.List;

import edu.gmu.classifier.neuralnet.net.Net;

public class Backpropagation
{
	public static class TrainingExample
	{
		protected double[] inputs;
		protected double output;
		
		public TrainingExample( double[] inputs, double output )
		{
			this.inputs = inputs;
			this.output = output;
		}
		
		public double[] getInputs( )
		{
			return inputs;
		}
		
		public void setInputs( double[] inputs )
		{
			this.inputs = inputs;
		}
		
		public double getOutput( )
		{
			return output;
		}
		
		public void setOutput( double output )
		{
			this.output = output;
		}
	}
	
	public void train( Net net, List<TrainingExample> dataList )
	{
		while ( !stop( net ) )
		{
			for ( TrainingExample data : dataList )
			{
				// store outputs
				net.classify( data.inputs );
				
				
			}
		}
	}
	
	public boolean stop( Net net )
	{
		return false;
	}
}
