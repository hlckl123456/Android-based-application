package com.ks.placesearch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentFavorites.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentFavorites#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentReviews extends Fragment {

    private RecyclerView recyclerView;
    private TextView noReviews;
    private RecyclerView.Adapter adapter;
    private List<Review> reviews, yelpReviews;
    private String yelpRest, placeId;
    private String TAG = "FragmentReviews";
    private Spinner typeBy, orderBy;

    private OnFragmentInteractionListener mListener;
    //    private GlobalVaries favList = (GlobalVaries) getActivity().getApplication();
    public FragmentReviews() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentReviews newInstance(String param1, String param2) {
        FragmentReviews fragment = new FragmentReviews();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getActivity().getIntent().getExtras();
        placeId = data.getString("placeId");
        reviews = new ArrayList<>();
        yelpReviews = new ArrayList<>();

        loadGoogleReviews();
    }

    private void reloadGoogleReviews() {
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.reviewsRecyclerView);
        noReviews = (TextView) getActivity().findViewById(R.id.noReviews);
        if (reviews != null || reviews.size() != 0) {
            recyclerView.setVisibility(View.VISIBLE);
            noReviews.setVisibility(View.GONE);
            adapter = new ReviewAdapter(reviews, getContext());
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            noReviews.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        typeBy = (Spinner) getActivity().findViewById(R.id.reviewTypeBy);
        orderBy = (Spinner) getActivity().findViewById(R.id.reviewOrderBy);

        typeBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = typeBy.getSelectedItem().toString();
                if (selected.equals("Google reviews")) {
                    reloadGoogleReviews();
                }
                if (selected.equals("Yelp reviews")) {

                    loadYelpReviews();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        orderBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortReviews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private void sortReviews() {
        String order = orderBy.getSelectedItem().toString();
        String type = typeBy.getSelectedItem().toString();
        List<Review> currentReview= new ArrayList<>();
        currentReview = new ArrayList<>(type.equals("Google reviews") ? reviews : yelpReviews);

        if (currentReview.size() > 0) {
            switch (order) {
                case "Default order":
                    break;
                case "Highest rating":
                    Collections.sort(currentReview, new Comparator<Review>() {
                        @Override
                        public int compare(Review o1, Review o2) {
                            return Integer.parseInt(o1.getRating()) < Integer.parseInt(o2.getRating()) ? 1 : -1;
                        }
                    });
                    break;
                case "Lowest rating":
                    Collections.sort(currentReview, new Comparator<Review>() {
                        @Override
                        public int compare(Review o1, Review o2) {
                            return Integer.parseInt(o1.getRating()) > Integer.parseInt(o2.getRating()) ? 1 : -1;
                        }
                    });
                    break;
                case "Most recent":
                    Collections.sort(currentReview, new Comparator<Review>() {
                        @Override
                        public int compare(Review o1, Review o2) {

                            return !compareTime(o1.getTime(), o2.getTime()) ? 1 : -1;
                        }
                    });
                    break;
                case "Least recent":
                    Collections.sort(currentReview, new Comparator<Review>() {
                        @Override
                        public int compare(Review o1, Review o2) {

                            return compareTime(o1.getTime(), o2.getTime()) ? 1 : -1;
                        }
                    });
                    break;
            }
            loadView(currentReview);
        }
    }

    private boolean compareTime (String t1, String t2) {
        t1 = t1.replace("-", "");
        t1 = t1.replace(" ", "");
        t1 = t1.replace(":", "");
        t2 = t2.replace("-", "");
        t2 = t2.replace(" ", "");
        t2 = t2.replace(":", "");
        Log.d(TAG, t1);
        Log.d(TAG, t2);
        return Long.parseLong(t1) > Long.parseLong(t2);
    }

    private void loadView(List<Review> orderedReviews) {
        adapter = new ReviewAdapter(orderedReviews, getContext());
        recyclerView.setAdapter(adapter);
    }

    private void loadYelpReviews() {
        yelpReviews = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                AppURLs.YELPREVIEW + yelpRest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("error").equals("0")) {
                                recyclerView.setVisibility(View.VISIBLE);
                                noReviews.setVisibility(View.GONE);
                                jsonObject = new JSONObject(jsonObject.getString("data"));
                                JSONArray jsonReviews = jsonObject.getJSONArray("reviews");
                                for (int i = 0; i < jsonReviews.length(); i++) {
                                    JSONObject component = (JSONObject) jsonReviews.get(i);
                                    JSONObject user = (JSONObject) component.getJSONObject("user");
                                    String photo = user.getString("image_url");
                                    String name = user.getString("name");
                                    String rating = component.getString("rating");
                                    String time = component.getString("time_created");
                                    String comment = component.getString("text");
                                    String url = component.getString("url");
//                                    Log.d(TAG, "photo:" + photo);
//                                    Log.d(TAG, "name:" + name);
//                                    Log.d(TAG, "rating:" + rating);
//                                    Log.d(TAG, "time:" + time);
//                                    Log.d(TAG, "comment:" + comment);
                                    yelpReviews.add(new Review(name, photo, rating, time, comment, url));
                                }
                                adapter = new ReviewAdapter(yelpReviews, getContext());
                                recyclerView.setAdapter(adapter);
                            } else {
                                recyclerView.setVisibility(View.GONE);
                                noReviews.setVisibility(View.VISIBLE);
                            }
                            //https://maps.googleapis.com/maps/api/place/photo?maxwidth=1000&photoreference=CmRaAAAALF9wFDUwikdFh_oA8C2aVmdEj-XRUXAU0JRHoX-PESCCg9Cn4wyXNQV7RFEpvScH6TLs1M35MbVtWj3tOhVOag3y8GDrr3U3D6Em0j-r3qe22KLPEXygbETwBCXg5rS1EhA0sIYDfiJVGqZivTwgN17UGhRZVPHCTZiSUOcEsmXNcZ1KUMGjNw&key=AIzaSyB9FQSZUWndH5dSqIQCLF-XrOW3kJ6Hiyc

                        } catch (JSONException e) {
                            recyclerView.setVisibility(View.GONE);
                            noReviews.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        recyclerView.setVisibility(View.GONE);
                        noReviews.setVisibility(View.VISIBLE);
                    }
                }

        );

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void loadGoogleReviews() {
        String rest = "placeid=" + placeId;
        Log.d(TAG, rest);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                AppURLs.PLACEDETAIL + rest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        noReviews = (TextView) getActivity().findViewById(R.id.noReviews);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jsonObject = jsonObject.getJSONObject("result");
                            yelpRest = getYelpRest(jsonObject);
                            JSONArray reviewsarray = jsonObject.getJSONArray("reviews");
                            for (int i = 0; i < reviewsarray.length(); i++) {
                                JSONObject review = reviewsarray.getJSONObject(i);
                                String photo = review.getString("profile_photo_url");
                                String name = review.getString("author_name");
                                String rating = review.getString("rating");
                                long t = (long) Long.parseLong(review.getString("time")) *1000;
                                Date d = new Date(t);
                                DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String time = f.format(d).toString();
                                String comment = review.getString("text");
                                String url = review.getString("author_url");

                                reviews.add(new Review(name, photo, rating, time, comment, url));
                            }
                            recyclerView = (RecyclerView) getActivity().findViewById(R.id.reviewsRecyclerView);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            adapter = new ReviewAdapter(reviews, getContext());
                            recyclerView.setAdapter(adapter);
                            recyclerView.setVisibility(View.VISIBLE);
                            noReviews.setVisibility(View.GONE);

                            //https://maps.googleapis.com/maps/api/place/photo?maxwidth=1000&photoreference=CmRaAAAALF9wFDUwikdFh_oA8C2aVmdEj-XRUXAU0JRHoX-PESCCg9Cn4wyXNQV7RFEpvScH6TLs1M35MbVtWj3tOhVOag3y8GDrr3U3D6Em0j-r3qe22KLPEXygbETwBCXg5rS1EhA0sIYDfiJVGqZivTwgN17UGhRZVPHCTZiSUOcEsmXNcZ1KUMGjNw&key=AIzaSyB9FQSZUWndH5dSqIQCLF-XrOW3kJ6Hiyc

                        } catch (JSONException e) {
                            recyclerView.setVisibility(View.GONE);
                            noReviews.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        recyclerView.setVisibility(View.GONE);
                        noReviews.setVisibility(View.VISIBLE);
                    }
                }

        );

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private String getYelpRest(JSONObject jsonObject) throws JSONException {
        //name=Papa John's Pizza&city=Los Angeles County&state=CA&country=US&address1=6520 Crenshaw Boulevard, Los Angeles
        try {
            String city = "";
            String state = "";
            String country = "";
            String name = jsonObject.getString("name");
            String address1 = jsonObject.getString("vicinity");
            JSONArray address_components = jsonObject.getJSONArray("address_components");
            for (int i = 0; i < address_components.length(); i++) {
                JSONObject component = (JSONObject)address_components.get(i);
                String type = ((JSONArray)component.get("types")).getString(0);
                if (type.equals("country")) {
                    country = component.getString("short_name");
                }
                if (type.equals("administrative_area_level_1")) {
                    state = component.getString("short_name");
                }
                if (type.equals("administrative_area_level_2")) {
                    city = component.getString("short_name");
                }
            }

            return "name="+ name +"&city="+ city +"&state="+ state +"&country="+ country
                    +"&address1="+address1;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reviews, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
}
