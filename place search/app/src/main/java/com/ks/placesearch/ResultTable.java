package com.ks.placesearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ResultTable extends AppCompatActivity implements View.OnClickListener {


    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private Button previous, next;
    private List<result> results;
    private final String TAG = "ResultTable";
    private Stack<List<result>> previousResultsStack;
    private Stack<String> nextTokenStack;
    private TextView noResults;
    private SharedPreferenceManager sharedPreferenceManager;
//    private GlobalVaries favList = (GlobalVaries) this.getApplication();
    private String nextToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_table);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext());
        previous = (Button) findViewById(R.id.previous);
        previous.setOnClickListener(this);
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);
        previousResultsStack = new Stack<>();
        nextTokenStack = new Stack<>();
        noResults = (TextView)findViewById(R.id.noResults);


        if (getIntent().getExtras() != null) {
            Bundle data = getIntent().getExtras();
            String keyword = data.getString("keyword");
            String category = data.getString("category");
            String distance = data.getString("distance");
            String address = data.getString("address");
            String position = data.getString("position");

            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            results = new ArrayList<>();
            loadRecyclerViewData(keyword, category, distance, address, position);
        } else {
            Log.d("ojbk",  "dsadsada");
            Log.d("ojbk",  nextToken);
            Log.d("ojbk",  "dsadsausc");
//            for (int i = 0; i < results.size(); i++) {
//                Log.d("ojbk",  results.get(i).toString());
//            }
            Log.d("ojbk",  "dsadsausc");

//            displayResult();
        }
    }

    @Override
    protected void onResume() {
        if (results.size() > 0) {
            displayResult();
        } else {

        }

        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previous:
                results = previousResultsStack.pop();
                nextToken = nextTokenStack.pop();
                displayResult();
                ((RecyclerView.Adapter) (recyclerView.getAdapter())).notifyDataSetChanged();
                break;
            case R.id.next:
                // maintain stack
                previousResultsStack.add(results);
                nextTokenStack.add(nextToken);
                // load next token view
                loadNextTokenView();
                displayResult();
                ((RecyclerView.Adapter) (recyclerView.getAdapter())).notifyDataSetChanged();
                break;
        }
    }

    private void loadNextTokenView() {
        final ProgressDialog fetchNextDialog = new ProgressDialog(this);
        fetchNextDialog.setMessage("Fetching next page");
        fetchNextDialog.show();
        String rest = "nextToken=" + nextToken;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                AppURLs.NEXTTOKENINFO + rest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fetchNextDialog.dismiss();
                        Log.d(TAG, "here3");
                        handleResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        fetchNextDialog.dismiss();
                        toast("error request");
                    }
                }

        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void loadRecyclerViewData(String keyword, String category, String distance, String address, String position) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching results");
        progressDialog.show();
        String rest = "keyword=" + keyword + "&category=" + reviseCategory(category) + "&distance=" + distance +
                "&address=" + address + "&position=" + position;


        Log.d(TAG, rest);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                AppURLs.NEARBYSEARCH + rest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
//                        Log.d(TAG, "here3");
                        handleResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        toast("error request");
                    }
                }

        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void handleResponse(String response) {
        LinearLayout pagination = (LinearLayout) findViewById(R.id.pagination);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int error = jsonObject.getInt("error");
            Log.d(TAG, "here1");
            jsonObject = new JSONObject(jsonObject.getString("data"));
            if (error == 0 && jsonObject.getJSONArray("results").length() > 0) {

                Log.d(TAG, "here2");

                if (jsonObject.has("next_page_token")) {
                    nextToken = jsonObject.getString("next_page_token");
                    Log.d(TAG, nextToken);
                } else {
                    nextToken = "";
                }
                JSONArray array = jsonObject.getJSONArray("results");
                results = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.getJSONObject(i);

                    JSONObject location = item.getJSONObject("geometry").getJSONObject("location");
                    String position = location.getString("lat") + "," + location.getString("lng");

                    result rs = new result(
                            item.getString("name"),
                            item.getString("vicinity"),
                            item.getString("icon"),
                            item.getString("place_id"),
                            position);
                    results.add(rs);
                }
                displayResult();

            } else {
                pagination.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noResults.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void btnShow() {
        if (nextToken.equals("")) {
            next.setEnabled(false);
        } else {
            next.setEnabled(true);
        }

        Log.d(TAG, nextToken);

        if (previousResultsStack.empty()) {
            previous.setEnabled(false);
        } else {
            previous.setEnabled(true);
        }
    }

    private String reviseCategory (String s) {
        s = s.toLowerCase();
        s = s.replace(" ", "_");
        return s;
    }

    private String encode (String s) {
        try {
            s = s.toLowerCase();
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            return s;
        }
    }

    private void displayResult() {
        adapter = new resultAdapter(results, getApplicationContext());
        recyclerView.setAdapter(adapter);
        ((RecyclerView.Adapter) (recyclerView.getAdapter())).notifyDataSetChanged();
        btnShow();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(ResultTable.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toast(String s){
        Toast.makeText(getApplication(),s,Toast.LENGTH_SHORT).show();
    }

}
