package edu.gmu.classifier.neuralnet.test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

import edu.gmu.classifier.neuralnet.factory.NetFactory;
import edu.gmu.classifier.neuralnet.io.DataLoader;
import edu.gmu.classifier.neuralnet.net.Net;
import edu.gmu.classifier.neuralnet.node.Link;
import edu.gmu.classifier.neuralnet.node.Node;
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
		
		Backpropagation b = new Backpropagation( );
		
		/* 32% error
		Net net = NetFactory.newNet( 64, 40, 10 );
		net.apply( NodeFunctions.setRandomWeights( -0.1, 0.1 ) );
		b.train( net, trainingDataList, testDataList, 0.3, 0.0 );
		*/
		
		/* 40% error (!!!) -- no hidden layers actually does better than a few hidden layers
		Net net = NetFactory.newNet( 64, 10 );
		net.apply( NodeFunctions.setRandomWeights( -0.1, 0.1 ) );
		b.train( net, trainingDataList, testDataList, 0.3, 0.0 );
		*/
		
		/*
		Net net = NetFactory.newNet( 64, 20, 10 );
		net.apply( NodeFunctions.setRandomWeights( -0.1, 0.1 ) );
		b.train( net, trainingDataList, testDataList, 0.3, 0.0 );
		*/
		
		/*
		Net net = NetFactory.newNet( 64, 10, 10 );
		net.apply( NodeFunctions.setRandomWeights( -0.1, 0.1 ) );
		b.train( net, trainingDataList, testDataList, 0.3, 0.0 );
		*/
		
		/*
		Net net = NetFactory.newNet( 64, 2, 10 );
		net.apply( NodeFunctions.setRandomWeights( -0.1, 0.1 ) );
		b.train( net, trainingDataList, testDataList, 0.3, 0.0 );
		*/
		
		/*
		Net net = NetFactory.newNet( 64, 4, 10 );
		net.apply( NodeFunctions.setRandomWeights( -0.1, 0.1 ) );
		b.train( net, trainingDataList, testDataList, 0.3, 0.0 );
		*/
		
		/*
		Net net = NetFactory.newNet( 64, 3, 10 );
		net.apply( NodeFunctions.setRandomWeights( -0.1, 0.1 ) );
		b.train( net, trainingDataList, testDataList, 0.3, 0.0 );
		*/
		
		// create jfreechart dataset for plotting purposes
		DefaultXYDataset dataset2 = new DefaultXYDataset( );
		
		int steps = 30;
		double stepd = 0.9 / steps;
		
		double[][] seriesData = new double[2][steps];
		
		for ( int i = 0 ; i < steps ; i++ )
		{
			Net net = NetFactory.newNet( 64, 4, 10 );
			net.apply( NodeFunctions.setRandomWeights( -0.1, 0.1 ) );
			b.train( net, trainingDataList, testDataList, 1.0 - i * stepd, 0.3 );
			
				seriesData[0][i] = 1.0 - stepd * i;
				seriesData[1][i] = b.calculateError( net, testDataList );
		}
		
		dataset2.addSeries( "Learning Rate", seriesData );
		
		JFreeChart chart2 = ChartFactory.createXYLineChart( "Test Error For Varying Learning Rate Parameters", "Learning Rate", "Error", dataset2, PlotOrientation.VERTICAL, false, false, false );
		ChartPanel chartPanel2 = new ChartPanel( chart2 );
		JFrame frame2 = new JFrame( );
		frame2.setSize( 1000, 1000 );
		frame2.add( chartPanel2 );
		frame2.setVisible( true );
		
	}
}
