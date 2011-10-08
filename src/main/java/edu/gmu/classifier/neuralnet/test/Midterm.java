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

public class Midterm
{
	public static void main( String[] args ) throws IOException
	{
		File dataDirectory = new File( "/home/ulman/CSI873/midterm/data" );
		String[] testDataFiles = dataDirectory.list( new FilenameFilter( )
		{
			@Override
			public boolean accept( File dir, String name )
			{
				return name.startsWith( "test" );
			}
		});
		
		String[] trainingDataFiles = dataDirectory.list( new FilenameFilter( )
		{
			@Override
			public boolean accept( File dir, String name )
			{
				return name.startsWith( "train" );
			}
		});
		
		List<TrainingExample> testDataList = new ArrayList<TrainingExample>( );
		List<TrainingExample> trainingDataList = new ArrayList<TrainingExample>( );
		
		for ( String fileName : testDataFiles )
		{
			testDataList.addAll( DataLoader.loadFile( new File( dataDirectory, fileName ) ) );
		}
		
		for ( String fileName : trainingDataFiles )
		{
			trainingDataList.addAll( DataLoader.loadFile( new File( dataDirectory, fileName ) ) );
		}
		
		Net net = NetFactory.newNet( 64, 3, 10 );
		
		net.apply( NodeFunctions.setWeights( 0.0 ) );
		
		Backpropagation b = new Backpropagation( );
		
		b.train( net, trainingDataList, 0.3, 0.9 );
	}
}
