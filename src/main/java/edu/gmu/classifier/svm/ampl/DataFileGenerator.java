package edu.gmu.classifier.svm.ampl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import edu.gmu.classifier.io.DataLoader;
import edu.gmu.classifier.io.TrainingExample;

public class DataFileGenerator
{
	public interface OutputGenerator
	{
		public double getOutput( TrainingExample data );
	}
	
	public interface Kernel
	{
		public double getValue( double[] x1, double[] x2 );
	}
	
	public static class Polynomial implements Kernel
	{
		double alpha, beta, delta;
		
		public Polynomial( double alpha, double beta, double delta )
		{
			this.alpha = alpha;
			this.beta = beta;
			this.delta = delta;
		}

		@Override
		public double getValue( double[] x1, double[] x2 )
		{
			double dot = 0.0;
			for ( int i = 0 ; i < x1.length ; i++ )
			{
				dot += x1[i] * x2[i];
			}
			
			return Math.pow( alpha * dot + beta, delta ); 
		}
	}
	
	public static class OneVersusAll implements OutputGenerator
	{
		protected int digit;
		
		public OneVersusAll( int digit )
		{
			this.digit = digit;
		}
		
		@Override
		public double getOutput( TrainingExample data )
		{
			return data.getDigit( ) == digit ? 1 : -1;
		}
	};
	
	public static class TwoClass implements OutputGenerator
	{
		protected int digit1;
		protected int digit2;
		
		public TwoClass( int digit1, int digit2 )
		{
			this.digit1 = digit1;
			this.digit2 = digit2;
		}
		
		@Override
		public double getOutput( TrainingExample data )
		{
			int digit = data.getDigit( );
			
			if ( digit == digit1 ) return 1;
			else if ( digit == digit2 ) return -1;
			else return 0;
		}
	};
	
	
	public static void generateAllDataFiles( String inDirectoryString, String outDirectoryString ) throws IOException
	{
		generateDataFile( inDirectoryString, outDirectoryString,  "classify_2-5", 2, 5 );
		
		for ( int i = 0 ; i < 10 ; i++ )
		{
			generateDataFile( inDirectoryString, outDirectoryString, String.format( "classify_%d", i ), i );
		}
	}
	
	public static void generateDataFile( String inFileName, String outDirectoryName, String outFilePrefix, int digit ) throws IOException
	{
		List<TrainingExample> dataList = DataLoader.loadDirectory( inFileName );
		
		File outDirectory = new File( outDirectoryName );
		File outFile = new File( outDirectory, outFilePrefix + ".dat" );
		outputDataFile( new FileOutputStream( outFile ), dataList, new OneVersusAll( digit ) );
		
		File outCommandFile = new File( outDirectory, outFilePrefix + ".cmd" );
		outputCommandFile( new FileOutputStream( outCommandFile ), outFile.getName( ) );
	}
	
	public static List<TrainingExample> loadData( String inFileName, int digit1, int digit2 ) throws IOException
	{
		List<TrainingExample> dataList = DataLoader.loadDirectory( inFileName );
		
		List<TrainingExample> filteredList = new ArrayList<TrainingExample>( dataList.size( ) );
		for ( TrainingExample data : dataList )
		{
			if ( data.getDigit( ) == digit1 || data.getDigit( ) == digit2 )
			{
				filteredList.add( data );
			}
		}
		
		return filteredList;
	}
	
	public static void generateDataFile( String inFileName, String outDirectoryName, String outFilePrefix, int digit1, int digit2 ) throws IOException
	{
		List<TrainingExample> dataList = loadData( inFileName, digit1, digit2 );
		
		File outDirectory = new File( outDirectoryName );
		File outFile = new File( outDirectory, outFilePrefix + ".dat" );
		outputDataFile( new FileOutputStream( outFile ), dataList, new TwoClass( digit1, digit2 ) );
		
		File outCommandFile = new File( outDirectory, outFilePrefix + ".cmd" );
		outputCommandFile( new FileOutputStream( outCommandFile ), outFile.getName( ) );
	}
	
	public static void outputCommandFile( OutputStream stream, String dataFileName ) throws IOException
	{
		BufferedWriter out = new BufferedWriter( new OutputStreamWriter( stream ) );
		
		out.write( "reset;" );
		out.newLine( );

		out.write( "model classify.mod;" );
		out.newLine( );

		out.write( String.format( "data %s;%n", dataFileName ) );

		out.write( "solve;" );
		out.newLine( );

		out.write( "display a > tmp.out;" );
		out.newLine( );
		
		out.close( );
	}
	
	public static void outputDataFile( OutputStream stream, List<TrainingExample> dataList, OutputGenerator gen ) throws IOException
	{
		BufferedWriter out = new BufferedWriter( new OutputStreamWriter( stream ) );
		
		out.write( "data;" );
		out.newLine( );
		
		int l = dataList.size( );
		out.write( String.format( "param l := %d;%n", l ) );
		
		int n = dataList.get( 0 ).getInputs( ).length;
		out.write( String.format( "param n := %d;%n", n ) );
		
		out.write( String.format( "param C := %f;%n", 100.0 ) );
		
		out.write( String.format( "param alpha := %f;%n", 0.0156 ) );
		
		out.write( String.format( "param beta := %f;%n", 0.0 ) );
		
		out.write( String.format( "param delta := %f;%n", 3.0 ) );
		
		out.write( String.format( "param y :=%n" ) );
		for ( int i = 0 ; i < l ; i++ )
		{
			TrainingExample data = dataList.get( i );
			out.write( String.format( " %d %.1f%n", i+1, gen.getOutput( data ) ) );
		}
		out.write( ";" );
		out.newLine( );
		
		out.write( String.format( "param x:" ) );
		for ( int i = 0 ; i < n ; i++ )
		{
			out.write( String.format( " %d", i+1 ) ); 
		}
		out.write( String.format( " :=%n" ) );
		for ( int i = 0 ; i < l ; i++ )
		{
			TrainingExample data = dataList.get( i );
			double[] input = data.getInputs( );
			
			out.write( String.format( " %d", i+1 ) );
			
			for ( int j = 0 ; j < n ; j++ )
			{
				out.write( String.format( " %.1f", input[j] ) ); 
			}
			
			out.newLine( );
		}
		out.write( ";" );
		out.newLine( );
		
		out.close( );
	}
	
	public static double[] read_a( String file ) throws IOException
	{
		FileInputStream stream = new FileInputStream( file );
		try
		{
			return read_a( stream );
		}
		finally
		{
			stream.close( );
		}
	}
	
	public static double[] read_a( InputStream stream ) throws IOException
	{
		List<Double> list = new ArrayList<Double>( );
		
		BufferedReader in = new BufferedReader( new InputStreamReader( stream ) );
		String line = null;
		boolean parseMode = false;
		
		while ( ( line = in.readLine( ) ) != null )
		{
			if ( parseMode )
			{
				String[] tokens = line.trim( ).split( "[\\s]+" );
				
				try
				{
				
					for ( int i = 0 ; i < tokens.length / 2 ; i++ )
					{
						int index = Integer.parseInt( tokens[i*2] ) - 1;
						double value = Double.parseDouble( tokens[i*2+1] );
						
						ensureLength( index, list );
						
						list.set( index, value );
					}
				
				}
				catch ( NumberFormatException e )
				{
					parseMode = false;
				}
			}
			else if ( line.startsWith( "a [*] :=" ) )
			{
				parseMode = true;
			}
		}
		
		double[] array = new double[list.size( )];
		for ( int i = 0 ; i < list.size( ) ; i++ )
			array[i] = list.get( i );
		
		return array;
	}
	
	// calculate b's for each a
	// only one is needed for an i s.t. 0 < a[i] < C, but this is a good check
	public static double[] calculate_b( List<TrainingExample> dataList, OutputGenerator out, Kernel kernel, double C, double[] a ) throws IOException
	{
		double[] b = new double[ a.length ];
		
		for ( int i = 0 ; i < b.length ; i++ )
		{
			double[] x_i = dataList.get( i ).getInputs( );
			double y_i = out.getOutput( dataList.get( i ) );
			
			double sum = 0.0;
			for ( int j = 0 ; j < b.length ; j++ )
			{
				TrainingExample x = dataList.get( j );
				double[] x_j = x.getInputs( );
				double y_j = out.getOutput( x );
				
				sum += y_j * a[j] * kernel.getValue( x_j, x_i );
			}
			
			b[i] = sum - y_i;
		}
		
		return b;
	}
	
	public static double[] calculate_y_predicted( List<TrainingExample> dataList, OutputGenerator out, Kernel kernel, double[] a, double b ) throws IOException
	{
		double[] y_predicted = new double[ a.length ];
		
		for ( int i = 0 ; i < a.length ; i++ )
		{
			TrainingExample x_i = dataList.get( i );
			
			double sum = 0.0;
			for ( int j = 0 ; j < a.length ; j++ )
			{
				TrainingExample x_j = dataList.get( j );
				double y_j = out.getOutput( x_j );
				double a_j = a[j];
				
				sum += y_j * a_j * kernel.getValue( x_j.getInputs( ), x_i.getInputs( ) );
			}
			
			y_predicted[i] = sum - b;
		}
		
		return y_predicted;
	}
	
	private static void ensureLength( int index, List<Double> list )
	{
		if ( list.size( ) > index ) return;
		
		for ( int i = list.size( ) ; i <= index ; i++ )
		{
			list.add( i, 0.0 );
		}
	}
	
	public static void main( String[] args ) throws IOException
	{
//		String inputDirectory = "/home/ulman/CSI873/midterm/data";
//		String outputDirectory = "/home/ulman/CSI873/midterm/repository/final/ampl";
//		generateAllDataFiles( inputDirectory, outputDirectory );
		
		List<TrainingExample> dataList = loadData( "/home/ulman/CSI873/midterm/data", 2, 5 );
		String outputDirectory = "/home/ulman/CSI873/midterm/repository/final/ampl";
		String temporaryOutput = String.format( "%s/%s", outputDirectory, "out.tmp" );
		
		double C = 100.0;
		OutputGenerator out =new TwoClass( 2, 5 );
		Kernel kernel = new Polynomial( 0.0156, 0.0, 3.0 );
		
		double[] a = read_a( temporaryOutput );
		double[] b = calculate_b( dataList, out, kernel, C, a );
		
		double count = 0.0;
		double b_sum = 0.0;
		
		for ( int i = 0 ; i < a.length ; i++ )
		{
			if ( a[i] < C && a[i] > 0.001 )
			{
				System.out.printf( "%.4f %.12f%n", a[i], b[i] );
				b_sum += b[i];
				count += 1.0;
			}
		}
		
		double b_avg = b_sum / count;
		
		double[] y = calculate_y_predicted( dataList, out, kernel, a, b_avg );
		
		count = 0.0;
		for ( int i = 0 ; i < a.length ; i++ )
		{
			double value = y[i];
			double predicted = y[i] > 0 ? 1.0 : -1.0;
			double actual = out.getOutput( dataList.get( i ) );
			
			if ( predicted == actual )
			{
				count += 1.0;
			}
			
			System.out.printf( "Value %.4f Predicted %.1f Actual %.1f%n", value, predicted, actual );
		}
		
		System.out.printf( "Error Rate: %.3f%n", 1.0 - ( count / a.length ) );
		
	}
}
