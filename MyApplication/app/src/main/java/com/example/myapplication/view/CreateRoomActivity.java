package com.example.myapplication.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateRoomActivity extends AppCompatActivity {

    EditText playerNameInput;
    EditText cityNameInput;
    EditText mafiaAmountInput;
    EditText civilAmountInput;
    TextView textView;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    String name;

    Button enterData;

    private StringRequest mStringRequest;
    List<String> playerNicks = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textView = findViewById(R.id.textView5);
        mafiaAmountInput = findViewById(R.id.MafiaAmountInput);
        civilAmountInput = findViewById(R.id.CivilAmountInput);
        playerNameInput = findViewById(R.id.PlayerNameInput);
        cityNameInput = findViewById(R.id.CityNameInput);
        enterData = findViewById(R.id.CreateRoomButton);

        enterData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendAndRequestResponse();
                //requestPlayerData();
                //sendWorkPostRequest();
               HttpPOSTRequestWithParameters();

            }
        });
    }

    public void HttpPOSTRequestWithParameters() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.0.2.2:63439/api/CreateNewRoom";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR","error => "+error.toString());
                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                //..add other headers
                return params;
            }


            // this is the relevant method
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("name", cityNameInput.getText().toString());
                // volley will escape this for you
                //params.put("randomFieldFilledWithAwkwardCharacters", "{{%stuffToBe Escaped/");
                params.put("playerName", playerNameInput.getText().toString());
                params.put("civilAmount", civilAmountInput.getText().toString());
                params.put("mafiaAmount", mafiaAmountInput.getText().toString());

                return params;
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError response) {
                try {

                    String json = new String(response.networkResponse.data, HttpHeaderParser.parseCharset(response.networkResponse.headers));
                    Log.e("tag", "reponse error = " + json);
                }catch (Exception e){}
                return super.parseNetworkError(response);
            }


        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

        //gets players nicks and then adds them to arraylist
    public void requestPlayerData() {
        String url = "http://10.0.2.2:63439/api/GetPlayers";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    if (response != null) {
                        for(int i = 0; i < response.length(); i++){
                            JSONObject obj = response.getJSONObject(i);
                            name = obj.getString("name");
                            if(!playerNicks.contains(name)) {
                                playerNicks.add(name);
                            }
                        }
                        Toast.makeText(CreateRoomActivity.this, response+name+"", Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOG", error.toString());
            }
        });
        queue.add(jsonArrayRequest);
    }
}

