package com.ks.placesearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentFavorites.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentFavorites#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentInfo extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView addressText, phoneNumberText, priceLevelText, googlePageText, websiteText;
    private RatingBar ratingBar;
    private String address, phoneNumber, priceLevel, rating, googlePage, website, geo;
    private LinearLayout addressLayout, phoneNumberLayout, priceLevelLayout, ratingLayout, googlePageLayout, websiteLayout;
    private ImageView twitterShare;
    private String placeId, name;
    private String TAG = "FragmentInfo";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    //    private GlobalVaries favList = (GlobalVaries) getActivity().getApplication();
    public FragmentInfo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentInfo.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentInfo newInstance(String param1, String param2) {
        FragmentInfo fragment = new FragmentInfo();
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



        Bundle data = getActivity().getIntent().getExtras();
        placeId = data.getString("placeId");
        name = data.getString("name");

        setInfoTable();

    }

    private void setTwitterBtn() {
        twitterShare = (ImageView) getActivity().findViewById(R.id.twitterShare);
        twitterShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //%20located%20at%20Los%20Angeles,%20CA%2090007,%20USA.%20Website:%20&url=http://usc.edu/
                String u = "https://twitter.com/intent/tweet?text="
                        + URLEncoder.encode("Check out " + name + " located at " + address)
                        + "%20%0aWebsite:"
                        + website;
                Uri uri = Uri.parse(u);
                Intent intent_twitter = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent_twitter);
            }
        });
    }

    public void setAddressText() {
        if (address != null) {
            addressText = (TextView) getActivity().findViewById(R.id.addressText);
            addressText.setText(address);
        } else {
            addressLayout = (LinearLayout) getActivity().findViewById(R.id.addressLayout);
            addressLayout.setVisibility(View.GONE);
        }
    }

    public void setPhoneNumberText() {
        if (phoneNumber != null) {
            phoneNumberText = (TextView) getActivity().findViewById(R.id.phoneNumberText);
            phoneNumberText.setText(phoneNumber);
            phoneNumberText.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            phoneNumberText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri telUri = Uri.parse("tel:" + phoneNumber);
                    Intent intent = new Intent(Intent.ACTION_DIAL, telUri);
                    startActivity(intent);
                }
            });
        } else {
            phoneNumberLayout = (LinearLayout) getActivity().findViewById(R.id.phoneNumberLayout);
            phoneNumberLayout.setVisibility(View.GONE);
        }
    }
    private void setPriceLevelText() {
        if (priceLevel != null) {
            priceLevelText = (TextView) getActivity().findViewById(R.id.priceLevelText);
            String symbol = "";
            for (int i = 0; i < Integer.parseInt(priceLevel); i++) {
                symbol += "$";
            }
            priceLevelText.setText(symbol);
        } else {
            priceLevelLayout = (LinearLayout) getActivity().findViewById(R.id.priceLevelLayout);
            priceLevelLayout.setVisibility(View.GONE);
        }
    }
    private void setRatingText() {
        if (rating != null) {
            ratingBar = (RatingBar) getActivity().findViewById(R.id.ratingBar);
            ratingBar.setRating(Float.parseFloat(rating));
        } else {
            ratingLayout = (LinearLayout) getActivity().findViewById(R.id.ratingLayout);
            ratingLayout.setVisibility(View.GONE);
        }
    }
    private void setGooglePageText() {
        if (googlePage != null) {
            googlePageText = (TextView) getActivity().findViewById(R.id.googlePageText);
            googlePageText.setText(googlePage);
            googlePageText.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            googlePageText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri mapUri = Uri.parse("geo:" + geo);
                    Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
                    startActivity(intent);
                }
            });
        } else {
            googlePageLayout = (LinearLayout) getActivity().findViewById(R.id.googlePageLayout);
            googlePageLayout.setVisibility(View.GONE);
        }
    }
    private void setWebsiteText() {
        if (website != null) {
            websiteText = (TextView) getActivity().findViewById(R.id.websiteText);
            websiteText.setText(website);
            websiteText.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            websiteText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri webUri = Uri.parse(website);
                    Intent intent = new Intent(Intent.ACTION_VIEW, webUri);
                    startActivity(intent);
                }
            });
        } else {
            websiteLayout = (LinearLayout) getActivity().findViewById(R.id.websiteLayout);
            websiteLayout.setVisibility(View.GONE);
        }
    }


    private void setInfoTable() {
        String rest = "placeid=" + placeId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                AppURLs.PLACEDETAIL + rest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "here3");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jsonObject = jsonObject.getJSONObject("result");
                            if (jsonObject.has("formatted_address")) {
                                address = jsonObject.getString("formatted_address");
                            }
                            if (jsonObject.has("formatted_phone_number")) {
                                phoneNumber = jsonObject.getString("formatted_phone_number");
                            }
                            if (jsonObject.has("price_level")) {
                                priceLevel = jsonObject.getString("price_level");
                            }
                            if (jsonObject.has("rating")) {
                                rating = jsonObject.getString("rating");
                            }
                            if (jsonObject.has("url")) {
                                googlePage = jsonObject.getString("url");
                            }
                            if (jsonObject.has("website")) {
                                website = jsonObject.getString("website");
                            }
                            JSONObject location = jsonObject.getJSONObject("geometry").getJSONObject("location");
                            geo = location.getString("lat") + "," + location.getString("lng");
                            Log.d(TAG, geo);

                            setAddressText();
                            setPhoneNumberText();
                            setPriceLevelText();
                            setRatingText();
                            setGooglePageText();
                            setWebsiteText();
                            setTwitterBtn();

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false);
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
