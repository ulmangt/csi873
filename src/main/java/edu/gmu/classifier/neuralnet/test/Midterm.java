package edu.gmu.classifier.neuralnet.test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.gmu.classifier.neuralnet.factory.NetFactory;
import edu.gmu.classifier.neuralnet.io.DataLoader;
import edu.gmu.classifier.neuralnet.net.Net;
import edu.gmu.classifier.neuralnet.node.NodeFunctions;
import edu.gmu.classifier.neuralnet.train.Backpropagation;
import edu.gmu.classifier.neuralnet.train.Backpropagation.TrainingExample;

/**
 * Main class which uses the data loading and neural net training classes defined elsewhere
 * in order to train neural networks using a variety of parameters to classify handwriting
 * images and report results.
 * 
 * @author ulman
 */
public class Midterm
{
	public static void main( String[] args ) throws IOException
	{
		// list the test data files
		File dataDirectory = new File( "/home/ulman/CSI873/midterm/data" );
		String[] testDataFiles = dataDirectory.list( new FilenameFilter( )
		{
			@Override
			public boolean accept( File dir, String name )
			{
				return name.startsWith( "test" );
			}
		});
		
		// list the training data files
		String[] trainingDataFiles = dataDirectory.list( new FilenameFilter( )
		{
			@Override
			public boolean accept( File dir, String name )
			{
				return name.startsWith( "train" );
			}
		});
		
		// load all test data examples
		List<TrainingExample> testDataList = new ArrayList<TrainingExample>( );
		for ( String fileName : testDataFiles )
		{
			testDataList.addAll( DataLoader.loadFile( new File( dataDirectory, fileName ) ) );
		}
		
		// load all training data examples
		List<TrainingExample> trainingDataList = new ArrayList<TrainingExample>( );
		for ( String fileName : trainingDataFiles )
		{
			trainingDataList.addAll( DataLoader.loadFile( new File( dataDirectory, fileName ) ) );
		}
		
		// create a network
		Net net = NetFactory.newNet( 64, 10, 10 );
		
		// randomize the network weights
		net.apply( NodeFunctions.setRandomWeights( -0.1, 0.1 ) );
		
		// run the backpropagation algorithm to train the network using the above training examples
		Backpropagation b = new Backpropagation( );
		b.train( net, trainingDataList, testDataList, 0.15, 0.3 );
	}
}
