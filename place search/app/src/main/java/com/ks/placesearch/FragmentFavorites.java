package com.ks.placesearch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentFavorites.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentFavorites#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFavorites extends Fragment {

    private SharedPreferenceManager sharedPreferenceManager;
    private final String TAG = "FragmentFavorites";
    private List<result> results = new ArrayList<>();
    private TextView noFavorites;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    public static FragmentFavorites newInstance(String param1, String param2) {
        FragmentFavorites fragment = new FragmentFavorites();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedPreferenceManager = new SharedPreferenceManager(getActivity().getApplicationContext());
        noFavorites = (TextView)getActivity().findViewById(R.id.noFavorites);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.favRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        generateFavoriteList();
    }

    public void generateFavoriteList() {
        results = new ArrayList<>();
        Map<String, ?> favList = sharedPreferenceManager.getAll();
        for (Map.Entry<String, ?> entry : favList.entrySet()) {
            String key = entry.getKey();
            if (entry.getValue() instanceof String) {
                String value = (String) entry.getValue();
                Gson gson = new Gson();
                result result = gson.fromJson(value, result.class);
                results.add(result);
            }
        }
        if (results.size() > 0) {
            adapter = new resultAdapter(results, getActivity().getApplicationContext(), this);
            recyclerView.setAdapter(adapter);
            ((RecyclerView.Adapter) (recyclerView.getAdapter())).notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            noFavorites.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            noFavorites.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferenceManager = new SharedPreferenceManager(getActivity().getApplicationContext());
    }

    @Override
    public void onResume() {
        generateFavoriteList();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_favorites, container, false);
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

    private OnFragmentInteractionListener mListener;
    //    private GlobalVaries favList = (GlobalVaries) getActivity().getApplication();
    public FragmentFavorites() {
        // Required empty public constructor
    }

    public interface IMethodCaller{
        void generateFavoriteList();
    }
}
