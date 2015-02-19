package de.kreth.clubhelper.activity;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.kreth.clubhelper.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PersonListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PersonListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public PersonListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment PersonListFragment.
     */
    public static PersonListFragment newInstance() {
        PersonListFragment fragment = new PersonListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_person_list, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            PersonListFragmentEvent ev = new PersonListFragmentEvent();
            ev.setUri(uri);
            mListener.onFragmentInteraction(ev);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public PersonListFragmentEvent createEvent() {
        return new PersonListFragmentEvent();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(PersonListFragmentEvent event);
    }

    private class PersonListFragmentEvent {
        private Uri uri;

        public Uri getUri() {
            return uri;
        }

        public void setUri(Uri uri) {
            this.uri = uri;
        }
    }
}
