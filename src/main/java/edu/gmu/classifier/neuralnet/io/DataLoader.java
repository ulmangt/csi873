package edu.gmu.classifier.neuralnet.io;

import java.awt.Rectangle;
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
	public static int X_SIZE = 8;
	public static int Y_SIZE = 8;
	public static int INPUT_SIZE = X_SIZE * Y_SIZE;
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
		
		return loadFile( fileName, new FileInputStream( file ), trueDigit );
	}
	
	public static List<TrainingExample> loadFile( String name, InputStream stream, int trueDigit ) throws IOException
	{
		List<TrainingExample> trainingExamples = new ArrayList<TrainingExample>( );
		
		BufferedReader in = new BufferedReader( new InputStreamReader( stream ) );
		
		int lineNumber = 1;
		String line = null;
		while ( ( line = in.readLine( ) ) != null )
		{
			if ( line.isEmpty( ) )
				continue;
			
			line = line.replaceAll( "[\\s]*", "" );
			
			if ( line.length( ) != INPUT_SIZE )
				throw new RuntimeException( String.format( "Unexpected line length: %d for file: %s, line number %d: %s", line.length( ), name, lineNumber, line ) );
			
			double[] inputData = new double[INPUT_SIZE];
			
			for ( int i = 0 ; i < INPUT_SIZE ; i++ )
			{
				char c = line.charAt( i );
			
				if ( c == '0' ) inputData[i] = 0.0;
				else if ( c == '1' ) inputData[i] = 1.0;
				else throw new RuntimeException( String.format( "Unexpected character %s for file: %s, line number %d: %s", c, name, lineNumber, line ) );
			}
			
			double[] outputData = createOutputArray( trueDigit );
			
			trainingExamples.add( new TrainingExample( inputData, outputData ) );
			
			lineNumber++;
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
	
	protected static Rectangle findOffsets( double[] data )
	{
		int minX = X_SIZE + 1;
		int maxX = -1;
		
		int minY = Y_SIZE + 1;
		int maxY = -1;
		
		for ( int x = 0 ; x < X_SIZE ; x++ )
		{
			for ( int y = 0 ; y < Y_SIZE ; y++ )
			{
				int index = getIndex( x, y );
				double value = data[index];
			
				if ( value > 0.0 )
				{
					if ( x < minX ) minX = x;
					if ( x > maxX ) maxX = x;
					if ( y < minY ) minY = y;
					if ( y > maxY ) maxY = y;
				}
			}
		}
		
		return new Rectangle( minX, minY, maxX - minX, maxY - minY );
	}
	
	protected static double[] shift( double[] data )
	{
		Rectangle rect = findOffsets( data );
		return shift( -rect.x, -rect.y, data );
	}
	
	protected static double[] shift( int shiftX, int shiftY, double[] data )
	{
		double[] newData = new double[data.length];
		
		for ( int i = 0 ; i < data.length ; i++ )
		{
			int x = getX( i ) + shiftX;
			int y = getY( i ) + shiftY;
			int newi = getIndex( x, y );
			
			if ( newi < 0 || newi >= data.length )
				continue;
			
			newData[newi] = data[i];
		}
		
		return newData;
	}
	
	protected static int getIndex( int x, int y )
	{
		return y * X_SIZE + x;
	}
	
	protected static int getX( int index )
	{
		return index % X_SIZE;
	}
	
	protected static int getY( int index )
	{
		return index / X_SIZE;
	}
}
