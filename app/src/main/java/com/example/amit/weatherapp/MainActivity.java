package com.example.amit.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mResultTextView;
    private Button mResultButton;
    private EditText mCityEditText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultTextView=findViewById(R.id.textView2);
        mResultButton=findViewById(R.id.button);
        mCityEditText=findViewById(R.id.editText);


    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String result="";
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }catch(Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("fetched json:",s);
            try{
                JSONObject jsonObject=new JSONObject(s);
                String weatherinfo=jsonObject.getString("weather");
                String tempinfo=jsonObject.getString("main");
                Log.i("weather:::",weatherinfo);
                JSONArray jsonArray=new JSONArray(weatherinfo);
                JSONObject tempOject=new JSONObject(tempinfo);


                String message="";
                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonPart= jsonArray.getJSONObject(i);
                    String main=jsonPart.getString("main");
                    String description=jsonPart.getString("description");
                    if(!main.equals("")&&!description.equals(""))
                    {
                        message+=main+":"+description+"\n\r";
                    }
                    Log.i("main::",jsonPart.getString("main"));
                    Log.i("temp::",tempOject.getString("temp"));
                }
                message+="Tempreture:"+tempOject.getString("temp")+"\r\n";
                message+="Pressure:"+tempOject.getString("pressure")+"\r\n";
                message+="Humidity:"+tempOject.getString("humidity")+"\r\n";
                if(!message.equals(""))
                {
                    mResultTextView.setText(message);
                }

            }catch(Exception e)
            {   Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
    public void getweather(View view)
    {   if(mResultTextView.getText()!=null)
       {
          mResultTextView.setText("");
       }
        DownloadTask downloadTask=new DownloadTask();
        downloadTask.execute("http://openweathermap.org/data/2.5/weather?q="+mCityEditText.getText().toString()+"&appid=b6907d289e10d714a6e88b30761fae22");
        //hide keyboard on click
        InputMethodManager manager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(mCityEditText.getWindowToken(),0);

    }

}
