package com.ks.placesearch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentSearch.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSearch extends Fragment implements TextWatcher, View.OnTouchListener,
        OnClickListener, View.OnFocusChangeListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String TAG = "FragmentSearch";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText inputKeyword, inputDistance;
    private TextInputLayout inputLayoutKeyword, inputLayoutDistance, inputLayoutLocation;
    private Button btnSearch, btnClear;
    private AutoCompleteTextView autoCompleteLocation;
    private GoogleApiClient mGoogleApiClient;
    private OnFragmentInteractionListener mListener;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private Spinner inputCategory;
    private RadioGroup radioGroup;
    private RadioButton current, otherLocation;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private String latlon = "";
    private static final LatLngBounds WORLD_VIEW = new LatLngBounds(
            new LatLng(28.70, -127.50), new LatLng(48.85, -55.90));

    public FragmentSearch() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSearch.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSearch newInstance(String param1, String param2) {
        FragmentSearch fragment = new FragmentSearch();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        askPermission();
    }

    private void askPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                getLocalLonlat();
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            getLocalLonlat();
        }
    }

    private void getLocalLonlat() {
        String url = "http://ip-api.com/json";

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String lat = jsonObject.getString("lat");
                            String lon = jsonObject.getString("lon");
                            latlon = lat + "," + lon;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toast("fail to get here position");
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        inputKeyword = (EditText) getActivity().findViewById(R.id.keyword_text);
        inputKeyword.setOnFocusChangeListener(this);
        inputKeyword.setOnTouchListener(this);
        inputDistance = (EditText) getActivity().findViewById(R.id.distance_text);
        autoCompleteLocation = (AutoCompleteTextView) getActivity().findViewById(R.id.location_text);
        autoCompleteLocation.setThreshold(3);
        autoCompleteLocation.setOnClickListener(this);
        inputLayoutKeyword = (TextInputLayout) getActivity().findViewById(R.id.keyword_holder);
        inputLayoutDistance = (TextInputLayout) getActivity().findViewById(R.id.distance_holder);
        inputLayoutLocation = (TextInputLayout) getActivity().findViewById(R.id.location_holder);
        mPlaceArrayAdapter = new PlaceArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
                WORLD_VIEW, null);
        autoCompleteLocation.setAdapter(mPlaceArrayAdapter);

        btnSearch = (Button) getActivity().findViewById(R.id.search_btn);
        btnSearch.setOnClickListener(this);
        btnClear = (Button) getActivity().findViewById(R.id.clear_btn);
        btnClear.setOnClickListener(this);

        inputCategory = (Spinner) getActivity().findViewById(R.id.category);
        radioGroup = (RadioGroup) getActivity().findViewById(R.id.radioGroup1);
        current = (RadioButton) getActivity().findViewById(R.id.radio0);
        current.setOnClickListener(this);
        current.setChecked(true);
        otherLocation = (RadioButton) getActivity().findViewById(R.id.radio1);
        otherLocation.setOnClickListener(this);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i("s", "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i("s", "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e("s", "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public void onClick(View v) {
        TextView keywordError = (TextView) getActivity().findViewById(R.id.keywordError);
        TextView locationError = (TextView) getActivity().findViewById(R.id.locationError);
        switch (v.getId()) {
            case R.id.clear_btn:
                locationError.setVisibility(View.GONE);
                keywordError.setVisibility(View.GONE);
                autoCompleteLocation.setText("");
                autoCompleteLocation.setEnabled(false);
                inputDistance.setText("");
                ((RadioButton)radioGroup.getChildAt(0)).setChecked(true);
                break;
            case R.id.search_btn:
                RadioButton radioButton = (RadioButton) getActivity().findViewById(radioGroup.getCheckedRadioButtonId());
                String keyword = inputKeyword.getText().toString();
                String category = inputCategory.getSelectedItem().toString();
                String distanceString = inputDistance.getText().toString();
                int distance = 160930;
                if (distanceString != null && !distanceString.equals("")) {
                    distance = 16093 * Integer.parseInt(distanceString);
                }
                String from = radioButton.getText().toString();
                Log.d(TAG, from);
                String otherLocation = "";
                if (!from.equals("Current location")) {
                    otherLocation = autoCompleteLocation.getText().toString();
                }

                Intent intent = new Intent(getContext(),  ResultTable.class);
                intent.putExtra("keyword", keyword);
                intent.putExtra("category", category);
                intent.putExtra("distance", Integer.toString(distance));
                intent.putExtra("address", otherLocation);
                intent.putExtra("position", latlon);

                boolean keywordCheck = !keyword.trim().equals("");
                boolean otherLocationCheck = from.equals("Current location")
                        || (!from.equals("Current location") && !otherLocation.trim().equals(""));
                Log.d(TAG, String.valueOf(otherLocationCheck));
                if (latlon != "" && keywordCheck && otherLocationCheck) {
                    startActivity(intent);
                } else {
                    toast("Please fix all fields with errors");
                    if (!keywordCheck) {
                        keywordError.setVisibility(View.VISIBLE);
                    } else {
                        keywordError.setVisibility(View.GONE);
                    }
                    if (!otherLocationCheck) {
                        locationError.setVisibility(View.VISIBLE);
                    } else {
                        locationError.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.radio0:
                autoCompleteLocation.setText("");
                autoCompleteLocation.setEnabled(false);
                break;
            case R.id.radio1:
                autoCompleteLocation.setEnabled(true);
                break;
//            case R.id.clear_btn:
//                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        switch (v.getId()) {
//            case R.id.keyword_text:
//                TextView keywordError = (TextView) getActivity().findViewById(R.id.keywordError);
//                if (inputKeyword.getText().toString().equals("")) {
//                    keywordError.setVisibility(View.VISIBLE);
//                } else {
//                    keywordError.setVisibility(View.GONE);
//                }
//                break;
//            case R.id.location_text:
//                TextView locationError = (TextView) getActivity().findViewById(R.id.locationError);
//                if (inputLocation.getText().toString().equals("")) {
//                    locationError.setVisibility(View.VISIBLE);
//                } else {
//                    locationError.setVisibility(View.GONE);
//                }
//                break;
//        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString();
        if (text == inputKeyword.getText().toString()) {

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("s", "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());
        toast("Google Places API connection failed with error code:" +
                connectionResult.getErrorCode());

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i("s", "Google Places API connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e("s", "Google Places API connection suspended.");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }



    private void toast(String s){
        Toast toast = Toast.makeText(getActivity().getApplication(),s,Toast.LENGTH_SHORT);

        TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
        toastMessage.setTextColor(getResources().getColor(R.color.grey));
        toast.show();

    }
}
