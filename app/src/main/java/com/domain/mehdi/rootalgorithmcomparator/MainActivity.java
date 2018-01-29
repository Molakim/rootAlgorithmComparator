package com.domain.mehdi.rootalgorithmcomparator;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.domain.mehdi.rootalgorithmcomparator.Utilities.RootUtilities;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<BarEntry> barEntries;
    BarChart barChart ;
    EditText ed_number;
    EditText ed_degree ;

    TextView tv_bisection;
    TextView tv_newton;
    TextView tv_dekker;
    TextView tv_brent;


    ProgressBar loadingBar;

    Button button ;

    private EstimationTask mTask;

    private double precision = 0.000000001 ;

    public class EstimationTask extends AsyncTask<Void, Void, BarData>{

        int exponent;
        double value;

        EstimationTask(int exponent, double value){
            this.exponent = exponent;
            this.value = value;
        }

        @Override
        protected void onPreExecute() {
            loadingBar.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.INVISIBLE);
        }

        @Override
        protected BarData doInBackground(Void... voids) {
            return estimateData(exponent,value);
        }

        @Override
        protected void onPostExecute(BarData barData) {
            if (barData != null){
                barChart.setData(barData);
                barChart.invalidate();
                loadingBar.setVisibility(View.INVISIBLE);
                barChart.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barChart = findViewById(R.id.barChart);
        ed_number = findViewById(R.id.ed_number);
        ed_degree = findViewById(R.id.ed_degree);

        tv_bisection = findViewById(R.id.tv_BisectionResult);
        tv_newton = findViewById(R.id.tv_NewtonResult);
        tv_dekker = findViewById(R.id.tv_DekkerResult);
        tv_brent = findViewById(R.id.tv_BrentResult);

        loadingBar = findViewById(R.id.loadingBar);

        button = findViewById(R.id.button);

        barChart.setData(initialiazeData());
        barChart.invalidate();




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                double value  = Double.valueOf(ed_number.getText().toString());
                int exponent = Integer.valueOf(ed_degree.getText().toString());
                mTask = new EstimationTask(exponent, value);
                precision = updatePrecision(value,exponent);
                mTask.execute();

                tv_bisection.setText("Bisection : " + String.valueOf(RootUtilities.round(RootUtilities.rootBisection(exponent,value,precision),10)));
                tv_newton.setText("Newton : " + String.valueOf(RootUtilities.round(RootUtilities.rootNewton(exponent,value,precision),10)));
                tv_dekker.setText("Dekker : " + String.valueOf(RootUtilities.round(RootUtilities.rootDekker(exponent,value,precision),10)));
                tv_brent.setText("Brent : " +String.valueOf(RootUtilities.round(RootUtilities.rootBrent(exponent,value,precision),10)));

            }
        });




    }

    private double updatePrecision(double value, int exponent) {
        String s = String.valueOf(value);
        if (s.indexOf('E') != -1){
            String array[] = s.split("E");
            if (Integer.valueOf(array[1]) >= 10){
                int precisionPower = Integer.valueOf(array[1])%exponent > exponent/2 ? Integer.valueOf(array[1])/exponent + 1 : Integer.valueOf(array[1])/exponent ;
                return (double) RootUtilities.elevateTo(10,precisionPower);
            }
            else if (Integer.valueOf(array[1]) <= -4 ){
                double newPrecision = 0.1;
                for (int i = 0; i<RootUtilities.absolute(Integer.valueOf(array[1])) + 4 ; i++){
                    newPrecision *= 0.1;
                }
                return newPrecision;
            }
            return 0.0000001 ;
        }
        return 0.0000001;
    }

    private BarData initialiazeData(){
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0,0));
        barEntries.add(new BarEntry(0,1));
//        barEntries.add(new BarEntry(0,2));
        BarDataSet barDataSet = new BarDataSet(barEntries,"Execution time in ms for 10,000 iterations");
        ArrayList<String> theAlgorithms = new ArrayList<>();
        theAlgorithms.add("Bisection");
        theAlgorithms.add("Newton");
//        theAlgorithms.add("Dekker");
        BarData theData = new BarData(theAlgorithms,barDataSet);

        return theData;


    }

    private BarData estimateData(int exponent, double value) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(RootUtilities.estimateBisection(exponent,value,precision),0));
        barEntries.add(new BarEntry(RootUtilities.estimateNewton(exponent,value,precision),1));
        barEntries.add(new BarEntry(RootUtilities.estimateDekker(exponent,value,precision),2));
//        barEntries.add(new BarEntry(RootUtilities.estimateBrent(exponent,value,precision),3));
        BarDataSet barDataSet = new BarDataSet(barEntries,"Execution time in ms for 10,000 iterations");

        ArrayList<String> theAlgorithms = new ArrayList<>();
        theAlgorithms.add("Bisection");
        theAlgorithms.add("Newton");
        theAlgorithms.add("Dekker");
//        theAlgorithms.add("Brent");



        BarData theData = new BarData(theAlgorithms,barDataSet);

        return theData;
    }


}
