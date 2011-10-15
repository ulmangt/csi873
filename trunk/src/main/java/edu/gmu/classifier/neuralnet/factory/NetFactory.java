package edu.gmu.classifier.neuralnet.factory;

import edu.gmu.classifier.neuralnet.net.Net;
import edu.gmu.classifier.neuralnet.net.SigmoidNet;

public class NetFactory
{
	private NetFactory( )
	{
		
	}
	
	/**
	 * Creates a neural network with the given number of nodes at each layer.
	 * @param nodeCount
	 */
	public static Net newNet( int... nodeCount )
	{
		return new SigmoidNet( nodeCount );
	}
}
