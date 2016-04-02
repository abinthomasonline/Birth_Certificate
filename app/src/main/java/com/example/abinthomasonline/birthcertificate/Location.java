package com.example.abinthomasonline.birthcertificate;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.os.StrictMode;
import android.widget.RadioGroup.OnCheckedChangeListener;

import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;
import java.util.List;
import java.util.Map;
import java.net.HttpCookie;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import java.net.URISyntaxException;
import java.net.URI;
import android.text.TextUtils;

public class Location extends AppCompatActivity {

    private ProgressDialog progress;
    static TextView dateFormat;
    static final String COOKIES_HEADER = "Set-Cookie";
    static java.net.CookieManager msCookieManager = new java.net.CookieManager();
    static String params[]=new String[10];


    private class PopulateClass {
        private ArrayList<String> spinnerList = new ArrayList<String>();
        private ArrayList<Integer> spinnerListValues = new ArrayList<Integer>();

        public void parse(String inputString) {


            int l = inputString.length();
            char ch;
            String s = "";
            for (int i = 0; i < l; ++i) {

                ch = inputString.charAt(i);
                if (ch == ',') {
                    if (s.charAt(0) - 48 >= 0 && s.charAt(0) - 48 <= 9)
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



    public void sendPostRequest() {

        progress = ProgressDialog.show(this, "Retrieving Data",
                "Please wait", true);

        try {

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

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

            while ((line = br.readLine()) != null) {
                responseOutput.append(line);
            }
            br.close();

            requestResponse = responseOutput.toString();


            progress.dismiss();


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String startSession(){

        try {
            url = new URL("http://cr.lsgkerala.gov.in/Pages/sevanaQckSrch.php");

            HttpURLConnection startSession = (HttpURLConnection)url.openConnection();
            startSession.setRequestMethod("GET");
            startSession.connect();

            Map<String, List<String>> headerFields = startSession.getHeaderFields();
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

            if(cookiesHeader != null)
            {
                for (String cookie : cookiesHeader)
                {
                    msCookieManager.getCookieStore().add(null,HttpCookie.parse(cookie).get(0));
                    System.out.println(cookie);
                }
            }


        } catch (Exception e){

        }

        return TextUtils.join(";", msCookieManager.getCookieStore().getCookies());
    }


    public static class DateOfBirthSelector extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            params[3] = "" + day;
            params[4] = "" + month;
            params[5] = "" + year;

            dateFormat.setText("" + day + "/" + month + "/" + year);

        }
    }

    public void dobSelector(View view) {
        dateFormat = (TextView) findViewById(R.id.date_format);
        DialogFragment newFragment = new DateOfBirthSelector();
        newFragment.show(getSupportFragmentManager(), "datePicker");

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        CookieHandler.setDefault( new java.net.CookieManager( null, CookiePolicy.ACCEPT_ALL ) );



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
                    params[0]=""+(districtPosition+1);

                    sendPostRequest();
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
                    public void onItemSelected(AdapterView<?> parent, View view,int localBodyTypePosition, long id) {
                        PopulateClass LB = new PopulateClass();
                        // POST Request to populate localbodytype

                        try {

                            url = new URL("http://cr.lsgkerala.gov.in/Pages/PopulateFn.php");
                            urlParameters = "cmbdist=" + (districtPosition + 1) + "&cmbLBType=" + LBType.spinnerListValues.get(localBodyTypePosition) + "&FType=LB";
                            params[1]=""+localBodyTypePosition;
                            sendPostRequest();
                            LB.parse(requestResponse);

                        } catch (Exception e) {
                        }


                        Spinner localBodySpinner = (Spinner) findViewById(R.id.local_body_spinner);
                        ArrayAdapter<String> localBodyAdapter = new ArrayAdapter<String>(Location.this,
                                android.R.layout.simple_spinner_item, LB.spinnerList);
                        localBodyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        localBodySpinner.setAdapter(localBodyAdapter);

                        localBodySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,int localBodyPosition, long id)
                            {
                              params[2]=""+localBodyPosition;
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

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//gender

        RadioGroup gender = (RadioGroup)findViewById(R.id.gender_grp);

        gender.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                params[6]= "" +(checkedId-2131558489);


            }
        });




// mother name
        EditText nameOfMother = (EditText)findViewById(R.id.name_of_mother_Edittext);
        final String name=nameOfMother.getText().toString();




        // captcha

        String cookieString;


        cookieString = startSession() + "; Domain=cr.lsgkerala.gov.in";

        final CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this);
        final CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();

        cookieManager.setCookie("http://cr.lsgkerala.gov.in/Capcha/CaptchaSecurityImages.php", cookieString);

        cookieSyncManager.sync();

        WebView captchaImage = (WebView)findViewById(R.id.captcha_image);
        captchaImage.loadUrl("http://cr.lsgkerala.gov.in/Capcha/CaptchaSecurityImages.php");



//submit button
        Button submitButton = (Button)findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    url = new URL("http://cr.lsgkerala.gov.in/Pages/SaveCertsrchdetail.php");
                    urlParameters="cmbTypeQck=QckSel&cmbType=1&cboDist="+ params[0] +"&cboLBType="+ params[1] +"&cboLB="+ params[2] +"&TxtDay=" + params[3]+ "&TxtMonth="+params[4]+"&TxtYear="+params[5]+"&rdGender="+params[6]+"&TxtMother="+name;

                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);


                        int responseCode = connection.getResponseCode();


                } catch (Exception e) {
                }





            }
        });



    }

}