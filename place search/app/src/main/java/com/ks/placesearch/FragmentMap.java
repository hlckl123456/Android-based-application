package com.ks.placesearch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentFavorites.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentFavorites#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMap extends Fragment implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private String placeId, address, position, mapurl;
    private MapView mapView;
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private AutoCompleteTextView autoCompleteLocation;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private LatLng origin, dest;
    private final String TAG = "FragmentMap";
    private static final LatLngBounds WORLD_VIEW = new LatLngBounds(
            new LatLng(28.70, -127.50), new LatLng(48.85, -55.90));

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getActivity().getIntent().getExtras();
        placeId = data.getString("placeId");
        address = data.getString("address");
        position = data.getString("position");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private OnFragmentInteractionListener mListener;
    //    private GlobalVaries favList = (GlobalVaries) getActivity().getApplication();
    public FragmentMap() {
        // Required empty public constructor
    }

    public static FragmentMap newInstance(String param1, String param2) {
        FragmentMap fragment = new FragmentMap();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        autoCompleteLocation = (AutoCompleteTextView) getActivity().findViewById(R.id.fromText);
        autoCompleteLocation.setThreshold(3);
        mPlaceArrayAdapter = new PlaceArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
                WORLD_VIEW, null);
        autoCompleteLocation.setAdapter(mPlaceArrayAdapter);
        autoCompleteLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String originText = autoCompleteLocation.getText().toString();
                getDirection(originText);
            }
        });

        mapView = (MapView) view.findViewById(R.id.mapTab);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

    }


    private LatLng getDirection(String address) {
        address = address.replace(",", "");
        address = address.replace(" ", "+");
        String rest = "address=" + address;
        Log.d(TAG, rest);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                AppURLs.GETPLACEBYADRRESS + rest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            jsonObject = jsonArray.getJSONObject(0);
                            JSONObject location = jsonObject.getJSONObject("geometry").getJSONObject("location");
                            String lat = location.getString("lat");
                            String lng  = location.getString("lng");
                            origin = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                            String[] aa = position.split(",");
                            dest = new LatLng(Double.parseDouble(aa[0]), Double.parseDouble(aa[1]));
                            Spinner travelMode = (Spinner) getActivity().findViewById(R.id.travelMode);
                            travelMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    drawLine();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

    //--------------------------------------------add google marker and zoom--------------------
                            drawLine();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }

        );

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

        return  null;
    }

    private void drawLine() {
        String url = getDirectionsUrl(origin, dest);
        Log.d(TAG, url);
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(origin).title(""));
        googleMap.addMarker(new MarkerOptions().position(dest).title(""));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(origin);
        builder.include(dest);
        LatLngBounds bounds = builder.build();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
        getDirectionData(url);
    }

    private void getDirectionData(String url) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            List<HashMap<String, String>> route = parseLegs(jsonObject);
                            ArrayList<LatLng> points = new ArrayList<LatLng>();
                            PolylineOptions lineOptions = new PolylineOptions();

                            for (int i = 0; i < route.size(); i++) {
                                HashMap<String, String> point = route.get(i);
                                double lat = Double.parseDouble(point.get("lat"));
                                double lng = Double.parseDouble(point.get("lng"));
                                LatLng position = new LatLng(lat, lng);
                                points.add(position);
                            }

                            lineOptions.addAll(points);
                            lineOptions.width(20);
                            lineOptions.color(Color.BLUE);
                            googleMap.addPolyline(lineOptions);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }

        );

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public List<HashMap<String, String>> parseLegs(JSONObject jObject) {
        List<HashMap<String, String>> legs = new ArrayList<HashMap<String, String>>();
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {
            jLegs = jObject.getJSONArray("routes");
            jLegs = ((JSONObject) jLegs.get(0)).getJSONArray("legs");
            for (int i = 0; i < jLegs.length(); i++) {
                jSteps = ((JSONObject) jLegs.get(i)).getJSONArray("steps");

                for (int j = 0; j < jSteps.length(); j++) {
                    String polyline = "";
                    polyline = (String) ((JSONObject) ((JSONObject) jSteps
                            .get(j)).get("polyline")).get("points");
                    List<LatLng> list = decodePoly(polyline);

                    for (int l = 0; l < list.size(); l++) {
                        HashMap<String, String> hm = new HashMap<String, String>();
                        hm.put("lat",
                                Double.toString(((LatLng) list.get(l)).latitude));
                        hm.put("lng",
                                Double.toString(((LatLng) list.get(l)).longitude));
                        legs.add(hm);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return legs;
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // String origin
        String str_origin = "origin=" + origin.latitude + ","
                + origin.longitude;
        // String destinatin
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Travelling Mode
        Spinner travelMode = (Spinner) getActivity().findViewById(R.id.travelMode);
        String mode = travelMode.getSelectedItem().toString().toLowerCase();
        mode = "mode=" + mode;

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&"
                + mode + "&" + "key=AIzaSyB9FQSZUWndH5dSqIQCLF-XrOW3kJ6Hiyc";


        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/json?"
                + parameters;

        return url;
    }



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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        String[] aa = position.split(",");
        LatLng current = new LatLng(Double.parseDouble(aa[0]), Double.parseDouble(aa[1]));
        map.addMarker(new MarkerOptions().position(current)
                .title(""));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
        googleMap = map;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("s", "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
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

    private void toast(String s){
        Toast.makeText(getActivity().getApplication(),s,Toast.LENGTH_SHORT).show();
    }
}
