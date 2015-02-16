package de.kreth.clubhelper.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kreth.clubhelper.Contact;
import de.kreth.clubhelper.MainActivity;
import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.R;
import de.kreth.clubhelper.RelationType;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.datahelper.SessionHolder;

/**
 * A placeholder fragment containing a simple view.
 */
public class PersonEditFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

   public static final String TAG = PersonEditFragment.class.getName();

   private DaoSession session;
   private Person person;
   private EditText preName;
   private EditText surName;
   private TextView txtBirth;

   private List<View> contactViews = new ArrayList<>();
   private List<View> relationViews = new ArrayList<>();

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      session = ((SessionHolder)getActivity()).getSession();
      long personId = getArguments().getLong(MainActivity.PERSONID);

      person = session.getPersonDao().load(personId);

      View rootView = inflater.inflate(R.layout.fragment_person_edit, container, false);
      initViews(rootView);

      initTabs(rootView);
      return rootView;
   }

   private void initTabs(View rootView) {
      final TableLayout table = (TableLayout) rootView;
      final ToggleButton btnContacts = (ToggleButton) rootView.findViewById(R.id.toggleButtonContacts);
      final ToggleButton btnRelations = (ToggleButton) rootView.findViewById(R.id.toggleButtonRelations);
      final ToggleButton btnOther = (ToggleButton) rootView.findViewById(R.id.toggleButton3);

      View.OnClickListener onClickListener = new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            switch (v.getId()) {
               case R.id.toggleButtonContacts:
                  btnContacts.setChecked(true);
                  btnRelations.setChecked(false);
                  btnOther.setChecked(false);
                  for(View vi : contactViews)
                     vi.setVisibility(View.VISIBLE);
                  for(View vi : relationViews)
                     vi.setVisibility(View.GONE);
                  break;
               case R.id.toggleButtonRelations:
                  btnOther.setChecked(false);
                  btnRelations.setChecked(true);
                  btnContacts.setChecked(false);
                  for(View vi : contactViews)
                     vi.setVisibility(View.GONE);
                  for(View vi : relationViews)
                     vi.setVisibility(View.VISIBLE);
                  break;
               case R.id.toggleButton3:
                  btnRelations.setChecked(false);
                  btnContacts.setChecked(false);
                  btnOther.setChecked(true);
                  for(View vi : contactViews)
                     vi.setVisibility(View.GONE);
                  for(View vi : relationViews)
                     vi.setVisibility(View.GONE);
                  break;
            }
         }
      };
      btnContacts.setOnClickListener(onClickListener);
      btnRelations.setOnClickListener(onClickListener);
      btnOther.setOnClickListener(onClickListener);

      List<Contact> contactList = person.getContactList();
      for (Contact c: contactList) {
         TableRow r = new TableRow(getActivity());
         TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);

         r.setLayoutParams(lp);

         TextView label = new TextView(getActivity());
         label.setText(c.getType());
         r.addView(label);

         TextView value = new TextView(getActivity());
         value.setText(c.getValue());
         r.addView(value);
         contactViews.add(r);
         table.addView(r, table.getChildCount());
      }

      List<Person.RelativeType> relations = person.getRelations();
      Map<String, Integer> relationtypeToString = new HashMap<>();
      relationtypeToString.put(RelationType.CHILD.name(), R.string.CHILD);
      relationtypeToString.put(RelationType.MOTHER.name(), R.string.MOTHER);
      relationtypeToString.put(RelationType.FATHER.name(), R.string.FATHER);
      relationtypeToString.put(RelationType.RELATIONSHIP.name(), R.string.RELATIONSHIP);

      for (Person.RelativeType r: relations) {

         TableRow row = new TableRow(getActivity());
         TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
         row.setLayoutParams(lp);
         row.setVisibility(View.GONE);


         TextView label = new TextView(getActivity());
         label.setText(relationtypeToString.get(r.getType().name()));
         row.addView(label);

         TextView value = new TextView(getActivity());
         value.setText(r.getRel().getPrename() + " " + r.getRel().getSurname());
         row.addView(value);
         relationViews.add(row);

         table.addView(row, table.getChildCount());
      }
   }

   private void initViews(View rootView) {
      this.preName = (EditText) rootView.findViewById(R.id.textPreName);
      this.preName.setText(person.getPrename());

      this.surName = (EditText) rootView.findViewById(R.id.textSurName);
      this.surName.setText(person.getSurname());

      this.txtBirth = (TextView) rootView.findViewById(R.id.textBirth);

      this.txtBirth.setText(SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(
              person.getBirth()));
      rootView.findViewById(R.id.imageButton).setOnClickListener(this);
   }

   @Override
   public void onDateSet(DatePicker datePicker, int year, int month, int day) {
      person.getBirth().setTime(new GregorianCalendar(year, month, day).getTimeInMillis());
      this.txtBirth.setText(SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(
              person.getBirth()));
      session.getPersonDao().update(person);
   }

   @Override
   public void onClick(View view) {
      Calendar birth = new GregorianCalendar();
      birth.setTime(person.getBirth());
      int year = birth.get(Calendar.YEAR);
      int month = birth.get(Calendar.MONTH);
      int day = birth.get(Calendar.DAY_OF_MONTH);
      DatePickerDialog dlg = new DatePickerDialog(getActivity(), this, year, month, day);
      dlg.show();
   }

}
