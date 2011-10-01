package edu.gmu.classifier.neuralnet.factory;

import edu.gmu.classifier.neuralnet.net.Net;
import edu.gmu.classifier.neuralnet.net.SigmoidNet;

public class NetFactory
{
	private NetFactory( )
	{
		
	}
	
	/**
	 * Creates a 
	 * @param nodeCount
	 */
	public static Net newNet( int... nodeCount )
	{
		return new SigmoidNet( nodeCount );
	}
}
