package de.kreth.clubhelper.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.kreth.clubhelper.MainActivity;
import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.R;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.datahelper.SessionHolder;

/**
 * A placeholder fragment containing a simple view.
 */
public class PersonEditFragment extends Fragment {

   private DaoSession session;
   private Person person;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      session = ((SessionHolder)getActivity()).getSession();
      long personId = getArguments().getLong(MainActivity.PERSONID);

      person = session.getPersonDao().load(personId);

      View rootView = inflater.inflate(R.layout.fragment_person_edit, container, false);
      return rootView;
   }
}
