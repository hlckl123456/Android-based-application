package com.ks.placesearch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentFavorites.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentFavorites#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPhotos extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<String> photos;
    private String placeId;
    private OnFragmentInteractionListener mListener;
    private final String TAG = "FragmentPhotos";
    //    private GlobalVaries favList = (GlobalVaries) getActivity().getApplication();
    public FragmentPhotos() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentPhotos.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPhotos newInstance(String param1, String param2) {
        FragmentPhotos fragment = new FragmentPhotos();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActivity().setContentView(R.layout.fragment_photos);
        Bundle data = getActivity().getIntent().getExtras();
        placeId = data.getString("placeId");
        photos = new ArrayList<String>();
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadPhotos();
    }

    private void loadPhotos() {
        String rest = "placeid=" + placeId;
        Log.d(TAG, rest);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                AppURLs.PLACEDETAIL + rest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jsonObject = jsonObject.getJSONObject("result");
                            JSONArray photosarray = jsonObject.getJSONArray("photos");
                            for (int i = 0; i < photosarray.length(); i++) {
                                String ref = photosarray.getJSONObject(i).getString("photo_reference");
                                String photo = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=2000&photoreference="
                                            + ref + "&key=AIzaSyB9FQSZUWndH5dSqIQCLF-XrOW3kJ6Hiyc";
                                photos.add(photo);
                            }
                            recyclerView = (RecyclerView) getActivity().findViewById(R.id.photosRecyclerView);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            adapter = new photoAdapter(photos, getContext());
                            recyclerView.setAdapter(adapter);

                            //https://maps.googleapis.com/maps/api/place/photo?maxwidth=1000&photoreference=CmRaAAAALF9wFDUwikdFh_oA8C2aVmdEj-XRUXAU0JRHoX-PESCCg9Cn4wyXNQV7RFEpvScH6TLs1M35MbVtWj3tOhVOag3y8GDrr3U3D6Em0j-r3qe22KLPEXygbETwBCXg5rS1EhA0sIYDfiJVGqZivTwgN17UGhRZVPHCTZiSUOcEsmXNcZ1KUMGjNw&key=AIzaSyB9FQSZUWndH5dSqIQCLF-XrOW3kJ6Hiyc

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
        return inflater.inflate(R.layout.fragment_photos, container, false);
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
