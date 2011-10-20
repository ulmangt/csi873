package edu.gmu.classifier.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility functions for loading image data files.
 *  
 * @author ulman
 */
public class DataLoader
{
	// dimensions of handwriting image samples
	public static int X_SIZE = 8;
	public static int Y_SIZE = 8;
	public static int INPUT_SIZE = X_SIZE * Y_SIZE;
	
	// regular expressions for parsing file names
	public static String filePatternString = "[\\S]*([\\d])\\.txt";
	public static Pattern filePattern = Pattern.compile( filePatternString );
	
	/**
	 * @param filePath the file to load
	 * @return a list of training examples consisting of input and expected output data
	 * @throws IOException
	 */
	public static List<TrainingExample> loadFile( String filePath ) throws IOException
	{
		return loadFile( new File( filePath ) );
	}
	
	/**
	 * @see edu.gmu.classifier.neuralnet.io.DataLoader.loadFile( String ) 
	 */
	public static List<TrainingExample> loadFile( File file ) throws IOException
	{
		String fileName = file.getName( );
		int trueDigit = getTrueDigitFromFileName( fileName );
		
		return loadFile( fileName, new FileInputStream( file ), trueDigit );
	}
	
	/**
	 * @param name the name of the file to load
	 * @param stream an input stream to load data from
	 * @param trueDigit the true digit represented by the training examples
	 * @return a list of training examples consisting of input and expected output data
	 * @throws IOException
	 */
	public static List<TrainingExample> loadFile( String name, InputStream stream, int trueDigit ) throws IOException
	{
		// create a list to hold loaded training examples from
		List<TrainingExample> trainingExamples = new ArrayList<TrainingExample>( );
		
		// wrap the input stream in a BufferedReader to read the stream as ASCII characters
		BufferedReader in = new BufferedReader( new InputStreamReader( stream ) );
		
		// loop through lines in the input stream
		int lineNumber = 1;
		String line = null;
		while ( ( line = in.readLine( ) ) != null )
		{
			// ignore empty lines
			if ( line.isEmpty( ) )
				continue;
			
			// remove all whitespace from each line
			line = line.replaceAll( "[\\s]*", "" );
			
			// check that the line has the expected number of characters
			if ( line.length( ) != INPUT_SIZE )
				throw new RuntimeException( String.format( "Unexpected line length: %d for file: %s, line number %d: %s", line.length( ), name, lineNumber, line ) );
			
			// initialize an array to hold the input data
			double[] inputData = new double[INPUT_SIZE];
			
			// fill in the array with 0s or 1s from the file data
			for ( int i = 0 ; i < INPUT_SIZE ; i++ )
			{
				char c = line.charAt( i );
			
				if ( c == '0' ) inputData[i] = 0.0;
				else if ( c == '1' ) inputData[i] = 1.0;
				else throw new RuntimeException( String.format( "Unexpected character %s for file: %s, line number %d: %s", c, name, lineNumber, line ) );
			}
			
			// create the output data (a length 10 array with a single 1 entry for the true digit represented by the input data)
			double[] outputData = createOutputArray( trueDigit );
			
			// add  the training example to the return list
			trainingExamples.add( new TrainingExample( inputData, outputData, trueDigit ) );
			
			// update the line number count
			lineNumber++;
		}
		
		return trainingExamples;
	}
	
	/**
	 * Creates an expected output value array given an input digit from 0 to 9. The output array will
	 * contain all 0s except for a 1 in the index specified by trueDigit.
	 */
	protected static double[] createOutputArray( int trueDigit )
	{
		double[] outputArray = new double[10];
		
		for ( int i = 0 ; i < 10 ; i++ )
		{
			if ( i == trueDigit )
			{
				outputArray[i] = 0.9;
			}
			else
			{
				outputArray[i] = 0.1;
			}
		}
		
		return outputArray;
	}
	
	/**
	 * Parses the given file name to determine the digit which the file represents.
	 */
	protected static int getTrueDigitFromFileName( String fileName )
	{
		Matcher m = filePattern.matcher( fileName );
		if ( m.matches( ) )
		{
			return Integer.parseInt( m.group( 1 ) );
		}
		else
		{
			throw new RuntimeException( String.format( "Cannot determine digit from file name: %s", fileName ) );
		}
	}
}
