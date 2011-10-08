package edu.gmu.classifier.neuralnet.io;

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

import edu.gmu.classifier.neuralnet.train.Backpropagation.TrainingExample;

public class DataLoader
{
	public static int INPUT_SIZE = 64;
	public static String filePatternString = "[\\S]*([\\d])\\.txt";
	public static Pattern filePattern = Pattern.compile( filePatternString );
	
	
	public static List<TrainingExample> loadFile( String filePath ) throws IOException
	{
		return loadFile( new File( filePath ) );
	}
	
	public static List<TrainingExample> loadFile( File file ) throws IOException
	{
		String fileName = file.getName( );
		int trueDigit = getTrueDigitFromFileName( fileName );
		
		return loadFile( new FileInputStream( file ), trueDigit );
	}
	
	public static List<TrainingExample> loadFile( InputStream stream, int trueDigit ) throws IOException
	{
		List<TrainingExample> trainingExamples = new ArrayList<TrainingExample>( );
		
		BufferedReader in = new BufferedReader( new InputStreamReader( stream ) );
		
		String line = null;
		while ( ( line = in.readLine( ) ) != null )
		{
			line = line.replaceAll( "[\\s]*", "" );
			
			if ( line.length( ) != INPUT_SIZE )
				throw new RuntimeException( String.format( "Unexpected line length: %d for input: %s", line.length( ), line ) );
			
			double[] inputData = new double[INPUT_SIZE];
			
			for ( int i = 0 ; i < INPUT_SIZE ; i++ )
			{
				char c = line.charAt( i );
			
				if ( c == '0' ) inputData[i] = 0.0;
				else if ( c == '1' ) inputData[i] = 1.0;
				else throw new RuntimeException( String.format( "Unexpected character %s for input: %s", c, line ) );
			}
			
			double[] outputData = createOutputArray( trueDigit );
			
			trainingExamples.add( new TrainingExample( inputData, outputData ) );
		}
		
		return trainingExamples;
	}
	
	protected static double[] createOutputArray( int trueDigit )
	{
		double[] outputArray = new double[10];
		outputArray[trueDigit] = 1.0;
		return outputArray;
	}
	
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
