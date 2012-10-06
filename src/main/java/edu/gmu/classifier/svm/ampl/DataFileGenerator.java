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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.gmu.classifier.database.ResultsUploader;
import edu.gmu.classifier.database.UploadResultQuery;
import edu.gmu.classifier.database.UploadRunQuery;
import edu.gmu.classifier.io.DataLoader;
import edu.gmu.classifier.io.TrainingExample;

public class DataFileGenerator
{
    // a functor interface which defines a function for calculating the
    // y (output) value for a given training example
    // this function should always return either 1 or -1
    public interface OutputGenerator
    {
        public double getOutput( TrainingExample data );
    }

    // a functor interface which defines a function that takes
    // two input vectors (two 64 length vectors containing 0 or 1
    // in each element representing a handwriting sample) and outputs
    // a scalar value.
    public interface Kernel
    {
        public double getValue( double[] x1, double[] x2 );
    }

    // the polynomial kernel
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
            for ( int i = 0; i < x1.length; i++ )
            {
                dot += x1[i] * x2[i];
            }

            return Math.pow( alpha * dot + beta, delta );
        }
    }

    // the radial basis kernel
    public static class Radial implements Kernel
    {
        double gamma;

        public Radial( double gamma )
        {
            this.gamma = gamma;
        }

        @Override
        public double getValue( double[] x1, double[] x2 )
        {
            double norm = 0.0;
            for ( int i = 0; i < x1.length; i++ )
            {
                norm += Math.pow( x1[i] - x2[i], 2.0 );
            }

            return Math.exp( -gamma * norm );
        }
    }

    // The output generator for one digit versus all others.
    // If the TrainingExample is an instance of the digit 
    // the result is 1.0 otherwise it is -1.0.
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

    // The output generator for the two class (one digit versus
    // one other digit) problem.
    // If the TrainingExample is an instance of digit1 the
    // result is a 1.0 otherwise it is -1.0.
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

            if ( digit == digit1 )
                return 1;
            else if ( digit == digit2 )
                return -1;
            else
            {
                throw new IllegalArgumentException( "Digit: " + digit );
            }
        }
    };

    // a routine for generating AMPL data files from the provided training example data files
    // this generates 11 data files (ten for the 10 digit classification problem and one for
    // the 2 versus 5 classification problem).
    public static void generateAllDataFiles( String inDirectoryString, String outDirectoryString ) throws IOException
    {
        generateDataFile( inDirectoryString, outDirectoryString, "classify_2-5", 2, 5 );

        for ( int i = 0; i < 10; i++ )
        {
            generateDataFile( inDirectoryString, outDirectoryString, String.format( "classify_%d", i ), i );
        }
    }

    // a helper routine which generates a single AMPL data file using
    // data from all the digits and classifying the given digit against all others
    public static void generateDataFile( String inFileName, String outDirectoryName, String outFilePrefix, int digit ) throws IOException
    {
        List<TrainingExample> dataList = DataLoader.loadDirectoryTrain( inFileName );

        File outDirectory = new File( outDirectoryName );
        File outFile = new File( outDirectory, outFilePrefix + ".dat" );
        outputDataFile( new FileOutputStream( outFile ), dataList, new OneVersusAll( digit ) );
    }

    // a helper routine which generates a single AMPL data file using
    // data from only the two provided digits
    public static void generateDataFile( String inFileName, String outDirectoryName, String outFilePrefix, int digit1, int digit2 ) throws IOException
    {
        List<TrainingExample> dataList = loadData( inFileName, false, digit1, digit2 );

        File outDirectory = new File( outDirectoryName );
        File outFile = new File( outDirectory, outFilePrefix + ".dat" );
        outputDataFile( new FileOutputStream( outFile ), dataList, new TwoClass( digit1, digit2 ) );
    }

    // loads the training examples corresponding to the two given digits from either the test or training data set
    public static List<TrainingExample> loadData( String inFileName, boolean test, int digit1, int digit2 ) throws IOException
    {
        List<TrainingExample> dataList = test ? DataLoader.loadDirectoryTest( inFileName ) : DataLoader.loadDirectoryTrain( inFileName );

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

    // generates an AMLP data file for the given dataList and output generator
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

        // Radial Kernel Parameters
        out.write( String.format( "param gamma := %f;%n", 0.0521 ) );

        // Polynomial Kernel Parameters
        //out.write( String.format( "param alpha := %f;%n", 0.0156 ) );

        //out.write( String.format( "param beta := %f;%n", 0.0 ) );

        //out.write( String.format( "param delta := %f;%n", 3.0 ) );

        out.write( String.format( "param y :=%n" ) );
        for ( int i = 0; i < l; i++ )
        {
            TrainingExample data = dataList.get( i );
            out.write( String.format( " %d %.1f%n", i + 1, gen.getOutput( data ) ) );
        }
        out.write( ";" );
        out.newLine( );

        out.write( String.format( "param x:" ) );
        for ( int i = 0; i < n; i++ )
        {
            out.write( String.format( " %d", i + 1 ) );
        }
        out.write( String.format( " :=%n" ) );
        for ( int i = 0; i < l; i++ )
        {
            TrainingExample data = dataList.get( i );
            double[] input = data.getInputs( );

            out.write( String.format( " %d", i + 1 ) );

            for ( int j = 0; j < n; j++ )
            {
                out.write( String.format( " %.1f", input[j] ) );
            }

            out.newLine( );
        }
        out.write( ";" );
        out.newLine( );

        out.close( );
    }

    // reads a NEOS AMPL output file and returns the calculated alpha values
    public static double[] read_a( String file ) throws IOException
    {
        BufferedReader stream = new BufferedReader( new InputStreamReader( new FileInputStream( file ) ) );
        try
        {
            return read_a( stream, "a" );
        }
        finally
        {
            stream.close( );
        }
    }

    // reads a NEOS AMPL output file and returns the calculated alpha values
    public static double[] read_a( BufferedReader in, String variable ) throws IOException
    {
        List<Double> list = new ArrayList<Double>( );
        String line = null;
        boolean parseMode = false;

        while ( ( line = in.readLine( ) ) != null )
        {
            if ( parseMode )
            {
                if ( line.isEmpty( ) ) break;
                
                String[] tokens = line.trim( ).split( "[\\s]+" );

                try
                {

                    for ( int i = 0; i < tokens.length / 2; i++ )
                    {
                        int index = Integer.parseInt( tokens[i * 2] ) - 1;
                        double value = Double.parseDouble( tokens[i * 2 + 1] );

                        ensureLength( index, list );

                        list.set( index, value );
                    }

                }
                catch ( NumberFormatException e )
                {
                    break;
                }
            }
            else if ( line.startsWith( String.format( "%s [*] :=", variable ) ) )
            {
                parseMode = true;
            }
        }

        double[] array = new double[list.size( )];
        for ( int i = 0; i < list.size( ); i++ )
            array[i] = list.get( i );

        return array;
    }

    // calculate b's for each a
    // only one is needed for an i s.t. 0 < a[i] < C, but this is a good check
    public static double[] calculate_b( List<TrainingExample> dataList, OutputGenerator out, Kernel kernel, double C, double[] a ) throws IOException
    {
        double[] b = new double[a.length];

        for ( int i = 0; i < b.length; i++ )
        {
            double[] x_i = dataList.get( i ).getInputs( );
            double y_i = out.getOutput( dataList.get( i ) );

            double sum = 0.0;
            for ( int j = 0; j < b.length; j++ )
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

    /**
     * Makes a classification decision for the 2 versus 5 case based on the AMPL solution.
     * 
     * @param dataListTest a list of data samples of classify
     * @param dataListTrain the training data list used to train the svm classifier
     * @param out a generator for calculating expected output values from the training data
     * @param kernel the kernel used in the AMPL model to calculate the alpha vector
     * @param a the alpha vector generated via AMPL
     * @param b the beta value calculated from the AMPL solution
     * @return a vector containing the predicted y values for each testing example
     */
    public static double[] calculate_y_predicted( List<TrainingExample> dataListTest, List<TrainingExample> dataListTrain, OutputGenerator out, Kernel kernel, double[] a, double b )
    {
        double[] y_predicted = new double[dataListTest.size( )];

        // iterate over the training examples
        for ( int i = 0; i < dataListTest.size( ); i++ )
        {
            TrainingExample x_i = dataListTest.get( i );

            // apply the formula from the svm slides to compute a y_predicted value
            // based on the alpha vector (solution to the dual problem)
            double sum = 0.0;
            for ( int j = 0; j < a.length; j++ )
            {
                TrainingExample x_j = dataListTrain.get( j );
                double y_j = out.getOutput( x_j );
                double a_j = a[j];

                sum += y_j * a_j * kernel.getValue( x_j.getInputs( ), x_i.getInputs( ) );
            }

            y_predicted[i] = sum - b;
        }

        return y_predicted;
    }

    // ensures that the length of the provided list is at least large enough to contain index
    private static void ensureLength( int index, List<Double> list )
    {
        if ( list.size( ) > index ) return;

        for ( int i = list.size( ); i <= index; i++ )
        {
            list.add( i, 0.0 );
        }
    }

    // uses calculate_y_predicted( ) to classify each testing example and compute an error rate
    public static int[] calculateErrorRate( List<TrainingExample> dataListTest, List<TrainingExample> dataListTrain, OutputGenerator out, Kernel kernel, double[] a, double b )
    {
        int[] digit = new int[dataListTest.size( )];

        double[] y = calculate_y_predicted( dataListTest, dataListTrain, out, kernel, a, b );

        double count = 0.0;
        for ( int i = 0; i < dataListTest.size( ); i++ )
        {
            double value = y[i];
            double predicted = y[i] > 0 ? 1.0 : -1.0;
            double actual = out.getOutput( dataListTest.get( i ) );

            digit[i] = predicted > 0 ? 3 : 6;

            if ( predicted == actual )
            {
                count += 1.0;
            }

            System.out.printf( "Value %.4f Predicted %.1f Actual %.1f%n", value, predicted, actual );
        }

        double errorRate = 1.0 - ( count / dataListTest.size( ) );
        double errorInterval = 1.96 * Math.sqrt( errorRate * ( 1 - errorRate ) / dataListTest.size( ) );

        System.out.printf( "Error Rate: %.3f Train Interval: (%.3f, %.3f)%n", errorRate, errorRate - errorInterval, errorRate + errorInterval );

        return digit;
    }

    // a database helper method for uploading results in the SQL data format
    // required by the CSI710 handwriting sample viewer (used for generating
    // confusion matrices and handwriting sample visualizations)
    public static void uploadResultsTest2_5( String description, List<TrainingExample> list, int[] predicted )
    {
        int first2id = 171257;
        int first5id = 171380;

        UploadRunQuery uploadRunQuery = new UploadRunQuery( description, System.currentTimeMillis( ) );
        uploadRunQuery.runQuery( );
        int ixRunId = uploadRunQuery.getRunId( );

        int count2 = 0;
        int count5 = 0;

        for ( int i = 0; i < list.size( ); i++ )
        {
            TrainingExample data = list.get( i );
            String sClassification = String.valueOf( predicted[i] );

            int index;
            if ( data.getDigit( ) == 2 )
            {
                index = first2id + count2;
                count2 += 1;
            }
            else
            {
                index = first5id + count5;
                count5 += 1;
            }

            UploadResultQuery uploadResultQuery = new UploadResultQuery( index, ixRunId, sClassification );
            uploadResultQuery.runQuery( );
        }
    }

    // helper method for generating AMPL model and data files
    public static void generateAmplDataFiles( ) throws IOException
    {
        String inputDirectory = "/home/ulman/CSI873/midterm/data";
        String outputDirectory = "/home/ulman/CSI873/midterm/repository/final/ampl";
        generateAllDataFiles( inputDirectory, outputDirectory );
    }

    public static void generateTestingResultsPolynomial_36( ) throws IOException
    {
        generateTestingResults( new Polynomial( 0.0156, 0.0, 3.0 ), "SVM 3vs6 run α = 0.0156, β = 0, d = 3" );
    }

    public static void generateTestingResultsRadial_36( ) throws IOException
    {
        generateTestingResults( new Radial( 0.0521 ), "SVM 3vs6 radial" );
    }
    
    // runs two digit 2-5 classification problem and calculates and displays results
    public static void generateTestingResults( Kernel kernel, String name ) throws IOException
    {
        List<TrainingExample> dataListTrain = loadData( "/home/ulman/workspace/csi747/midterm", false, 3, 6 );
        List<TrainingExample> dataListTest = loadData( "/home/ulman/workspace/csi747/midterm", true, 3, 6  );
        String outputDirectory = "/home/ulman/workspace/csi747/midterm";
        String temporaryOutput = String.format( "%s/%s", outputDirectory, "tmp.out" );

        double C = 100.0;
        OutputGenerator out = new TwoClass( 3, 6 );

        double[] a = read_a( temporaryOutput );
        double[] b = calculate_b( dataListTrain, out, kernel, C, a );

        double count = 0.0;
        double b_sum = 0.0;

        for ( int i = 0; i < a.length; i++ )
        {
            if ( a[i] < C && a[i] > 0.001 )
            {
                System.out.printf( "%.4f %.12f%n", a[i], b[i] );
                b_sum += b[i];
                count += 1.0;
            }
        }

        double b_avg = b_sum / count;

        System.out.println( "Error rate on Training Data." );
        calculateErrorRate( dataListTrain, dataListTrain, out, kernel, a, b_avg );

        System.out.println( "Error rate on Testing Data." );
        int[] testPreditions = calculateErrorRate( dataListTest, dataListTrain, out, kernel, a, b_avg );

        //uploadResultsTest2_5( name, dataListTest, testPreditions );
    }

    //////////////////////////////
    ///  Full 10 Digit Problem ///
    //////////////////////////////

    // a helper data structure for storing the alpha output values from AMPL
    // along with the calculated b value and an OutputGenerator
    public static class Model
    {
        double[] a;
        double b;
        OutputGenerator out;

        public Model( double[] a, double b, OutputGenerator out )
        {
            this.a = a;
            this.b = b;
            this.out = out;
        }
    }

    // runs ten digit classification problem and calculates and displays results
    public static void generateTestingResultsAll( ) throws IOException
    {
        List<TrainingExample> dataListTrain = DataLoader.loadDirectoryTrain( "/home/ulman/CSI873/midterm/data" );
        List<TrainingExample> dataListTest = DataLoader.loadDirectoryTest( "/home/ulman/CSI873/midterm/data" );

        Kernel kernel = new Polynomial( 0.0156, 0.0, 3.0 );

        Map<Integer, Model> map = new HashMap<Integer, Model>( );
        for ( int i = 0; i < 10; i++ )
        {
            String outputDirectory = "/home/ulman/CSI873/midterm/repository/final/ampl";
            String temporaryOutput = String.format( "%s/%s", outputDirectory, String.format( "out_%d.txt", i ) );

            double C = 100.0;
            OutputGenerator out = new OneVersusAll( i );

            double[] a = read_a( temporaryOutput );
            double[] b = calculate_b( dataListTrain, out, kernel, C, a );

            double count = 0.0;
            double b_sum = 0.0;

            for ( int j = 0; j < a.length; j++ )
            {
                if ( a[j] < C && a[j] > 0.001 )
                {
                    System.out.printf( "%.4f %.12f%n", a[j], b[j] );
                    b_sum += b[j];
                    count += 1.0;
                }
            }

            double b_avg = b_sum / count;

            map.put( i, new Model( a, b_avg, out ) );
        }

        System.out.println( "Error rate on Training Data." );
        int[] trainPreditions = calculateErrorRate( dataListTrain, dataListTrain, kernel, map );

        System.out.println( "Error rate on Testing Data." );
        int[] testPreditions = calculateErrorRate( dataListTest, dataListTrain, kernel, map );

        uploadResultsAllTrain( trainPreditions );
        uploadResultsAllTest( testPreditions );
    }

    // uses calculate_y_predicted( ) to classify each testing example and compute an error rate
    public static int[] calculateErrorRate( List<TrainingExample> dataListTest, List<TrainingExample> dataListTrain, Kernel kernel, Map<Integer, Model> map )
    {
        int[] predicted_digit = calculate_y_predicted( dataListTest, dataListTrain, kernel, map );

        double count = 0.0;
        for ( int i = 0; i < dataListTest.size( ); i++ )
        {
            TrainingExample data = dataListTest.get( i );

            if ( data.getDigit( ) == predicted_digit[i] )
            {
                count += 1.0;
            }
        }

        double errorRate = 1.0 - ( count / dataListTest.size( ) );
        double errorInterval = 1.96 * Math.sqrt( errorRate * ( 1 - errorRate ) / dataListTest.size( ) );

        System.out.printf( "Error Rate: %.3f Train Interval: (%.3f, %.3f)%n", errorRate, errorRate - errorInterval, errorRate + errorInterval );

        return predicted_digit;
    }

    /**
     * Makes a classification decision for the 10 digit classification problem based on the solutions
     * to the ten individual AMPL problems.
     * 
     * @param dataListTest a list of data samples of classify
     * @param dataListTrain the training data list used to train the svm classifier
     * @param out a generator for calculating expected output values from the training data
     * @param kernel the kernel used in the AMPL model to calculate the alpha vector
     * @param map the alpha and beta values generated via AMPL for each of the ten separate SVMs constructed (one for each digit)
     * @return a vector containing the predicted y values for each testing example
     */
    public static int[] calculate_y_predicted( List<TrainingExample> dataListTest, List<TrainingExample> dataListTrain, Kernel kernel, Map<Integer, Model> map )
    {
        int[] predicted_digit = new int[dataListTest.size( )];

        for ( int i = 0; i < dataListTest.size( ); i++ )
        {
            TrainingExample x_i = dataListTest.get( i );

            int best_digit = -1;
            double best_value = Double.NEGATIVE_INFINITY;

            for ( Entry<Integer, Model> entry : map.entrySet( ) )
            {
                int digit = entry.getKey( );
                Model model = entry.getValue( );
                double[] a = model.a;
                double b = model.b;
                OutputGenerator out = model.out;

                double sum = 0.0;
                for ( int j = 0; j < a.length; j++ )
                {
                    TrainingExample x_j = dataListTrain.get( j );
                    double y_j = out.getOutput( x_j );
                    double a_j = a[j];

                    sum += y_j * a_j * kernel.getValue( x_j.getInputs( ), x_i.getInputs( ) );
                }

                double value = sum - b;

                if ( value > best_value )
                {
                    best_value = value;
                    best_digit = digit;
                }
            }

            predicted_digit[i] = best_digit;
        }

        return predicted_digit;
    }

    public static void uploadResultsAllTest( int[] predicted_digit )
    {
        uploadResultsAll( "svm_testing_polynomial_all", ResultsUploader.IX_TEST_FIRST_INDEX, predicted_digit );
    }

    public static void uploadResultsAllTrain( int[] predicted_digit )
    {
        uploadResultsAll( "svm_training_polynomial_all", ResultsUploader.IX_TRAIN_FIRST_INDEX, predicted_digit );
    }

    public static void uploadResultsAll( String description, int firstDataId, int[] predicted_digit )
    {
        UploadRunQuery uploadRunQuery = new UploadRunQuery( description, System.currentTimeMillis( ) );
        uploadRunQuery.runQuery( );
        int ixRunId = uploadRunQuery.getRunId( );

        for ( int i = 0; i < predicted_digit.length; i++ )
        {
            String sClassification = String.valueOf( predicted_digit[i] );

            UploadResultQuery uploadResultQuery = new UploadResultQuery( firstDataId + i, ixRunId, sClassification );
            uploadResultQuery.runQuery( );
        }
    }

    /*
    public static void main( String[] args ) throws IOException
    {
    	generateTestingResultsAll( );
    }
    */
    
    /*
    public static void main( String[] args ) throws IOException
    {
        String inputDirectory = "/home/ulman/workspace/csi747/midterm/3-6";
        String outputDirectory = "/home/ulman/workspace/csi747/midterm/3-6";
        
        generateDataFile( inputDirectory, outputDirectory, "classify_3-6", 3, 6 );
    }
    */
    
    public static double[] calculate_y_predicted_primal( List<TrainingExample> dataListTest, List<TrainingExample> dataListTrain, OutputGenerator out, double[] w, double b )
    {
        double[] y_predicted = new double[dataListTest.size( )];

        // iterate over the training examples
        for ( int i = 0; i < dataListTest.size( ); i++ )
        {
            TrainingExample x_i = dataListTest.get( i );

            double sum = 0;
            double[] x = x_i.getInputs( );
            for ( int j = 0 ; j < x.length ; j++ )
            {
                sum += x[j] * w[j];
            }

            y_predicted[i] = sum - b;
        }

        return y_predicted;
    }
    
    public static int[] calculateErrorRatePrimal( List<TrainingExample> dataListTest, List<TrainingExample> dataListTrain, OutputGenerator out, double[] w, double b )
    {
        int[] digit = new int[dataListTest.size( )];

        double[] y = calculate_y_predicted_primal( dataListTest, dataListTrain, out, w, b );

        double count = 0.0;
        for ( int i = 0; i < dataListTest.size( ); i++ )
        {
            double value = y[i];
            double predicted = y[i] > 0 ? 1.0 : -1.0;
            double actual = out.getOutput( dataListTest.get( i ) );

            digit[i] = predicted > 0 ? 3 : 6;

            if ( predicted == actual )
            {
                count += 1.0;
            }

            System.out.printf( "Value %.4f Predicted %.1f Actual %.1f%n", value, predicted, actual );
        }

        double errorRate = 1.0 - ( count / dataListTest.size( ) );
        double errorInterval = 1.96 * Math.sqrt( errorRate * ( 1 - errorRate ) / dataListTest.size( ) );

        System.out.printf( "Error Rate: %.3f Train Interval: (%.3f, %.3f)%n", errorRate, errorRate - errorInterval, errorRate + errorInterval );

        return digit;
    }

    public static void generateTestingResultsPrimal( ) throws IOException
    {
        List<TrainingExample> dataListTrain = loadData( "/home/ulman/workspace/csi747/midterm", false, 3, 6 );
        List<TrainingExample> dataListTest = loadData( "/home/ulman/workspace/csi747/midterm", true, 3, 6  );
        
        BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( "/home/ulman/workspace/csi747/midterm/results_primal_3-6.txt" ) ) );
        try
        {
            double[] w = read_a( in, "w" );
            double[] xi = read_a( in, "xi" );
            double b = 0.489299; //XXX hard coded
            
            calculateErrorRatePrimal( dataListTrain, dataListTrain, new TwoClass( 3, 6 ), w, b );
            calculateErrorRatePrimal( dataListTest, dataListTrain, new TwoClass( 3, 6 ), w, b );
        }
        finally
        {
            in.close( );
        }
    }
   
    public static void main( String[] args ) throws IOException
    {
        generateTestingResultsPrimal( );
    }
}
