package com.example.abinthomasonline.birthcertificate;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.os.StrictMode;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class Location extends AppCompatActivity {

    private ProgressDialog progress;

    private class PopulateClass
    {
        private ArrayList<String> spinnerList = new ArrayList<String>();
        private ArrayList<Integer> spinnerListValues = new ArrayList<Integer>();

        public void parse(String inputString)
        {


            int l=inputString.length();
            char ch;
            String s="";
            for (int i=0;i<l;++i) {

                ch = inputString.charAt(i);
                if (ch == ',') {
                    if (s.charAt(0)-48>=0&&s.charAt(0)-48<=9)
                        spinnerListValues.add((int) s.charAt(0) - 48);
                    else
                        spinnerList.add(s);
                    s = "";
                } else
                    s += ch;
            }

        }

    }



    private URL url;
    private String urlParameters;
    private String requestResponse;



    public void sendPostRequest(View View) {

        progress = ProgressDialog.show(this, "Retrieving Data",
                "Please wait", true);

        try {

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("CONTENT-TYPE", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setDoOutput(true);

            DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
            dStream.writeBytes(urlParameters);
            dStream.flush();
            dStream.close();

            int responseCode = connection.getResponseCode();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            StringBuilder responseOutput = new StringBuilder();

            while((line = br.readLine()) != null ) {
                responseOutput.append(line);
            }
            br.close();

            requestResponse = responseOutput.toString();

            System.out.println(requestResponse);
            progress.dismiss();


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        // District Spinner
        Spinner districtSpinner = (Spinner) findViewById(R.id.district_spinner);
        ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(this,
                R.array.district_spinner, android.R.layout.simple_spinner_item);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(districtAdapter);


        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int districtPosition, long id) {
                final PopulateClass LBType = new PopulateClass();
                // POST Request to populate localbodytype

                try {

                    url = new URL("http://cr.lsgkerala.gov.in/Pages/PopulateFn.php");
                    urlParameters = "cmbdistLB=" + (districtPosition + 1) + "&FType=LBType";

                    sendPostRequest(view);
                    LBType.parse(requestResponse);

                } catch (Exception e) {
                }


                Spinner localBodyTypeSpinner = (Spinner) findViewById(R.id.local_body_type_spinner);
                ArrayAdapter<String> localBodyTypeAdapter = new ArrayAdapter<String>(Location.this,
                        android.R.layout.simple_spinner_item, LBType.spinnerList);
                localBodyTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                localBodyTypeSpinner.setAdapter(localBodyTypeAdapter);

                localBodyTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, final int localBodyTypePosition, long id) {
                        PopulateClass LB = new PopulateClass();
                        // POST Request to populate localbodytype

                        try {

                            url = new URL("http://cr.lsgkerala.gov.in/Pages/PopulateFn.php");
                            urlParameters = "cmbdist=" + (districtPosition + 1) + "&cmbLBType=" + LBType.spinnerListValues.get(localBodyTypePosition) + "&FType=LB";

                            sendPostRequest(view);
                            LB.parse(requestResponse);

                        } catch (Exception e) {
                        }


                        Spinner localBodySpinner = (Spinner) findViewById(R.id.local_body_spinner);
                        ArrayAdapter<String> localBodyAdapter = new ArrayAdapter<String>(Location.this,
                                android.R.layout.simple_spinner_item, LB.spinnerList);
                        localBodyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        localBodySpinner.setAdapter(localBodyAdapter);


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }





}
