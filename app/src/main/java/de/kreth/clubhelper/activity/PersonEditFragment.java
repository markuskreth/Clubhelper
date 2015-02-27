package de.kreth.clubhelper.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.kreth.clubhelper.Adress;
import de.kreth.clubhelper.Contact;
import de.kreth.clubhelper.MainActivity;
import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.R;
import de.kreth.clubhelper.RelationType;
import de.kreth.clubhelper.Relative;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.datahelper.PersonRelationHelper;
import de.kreth.clubhelper.datahelper.SessionHolder;
import de.kreth.clubhelper.widgets.PersonAdapter;
import de.kreth.clubhelper.widgets.PersonSelectDialog;

/**
 * A placeholder fragment containing a simple view.
 */
public class PersonEditFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    public static final String TAG = PersonEditFragment.class.getName();

    private PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    private DaoSession session;
    private Person person;
    private EditText preName;
    private EditText surName;
    private TextView txtBirth;

    private TableLayout rootView;

    private List<View> contactViews = new ArrayList<>();
    private List<View> relationViews = new ArrayList<>();
    private List<View> adressViews = new ArrayList<>();
    private ToggleButton btnContacts;
    private ToggleButton btnRelations;
    private ToggleButton btnAdresses;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_addPerson) {
            addDetail();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDetail() {
        AlertDialog.Builder dlgBld = new AlertDialog.Builder(getActivity()).setTitle("Welche Art von Information soll ergänzt werden?").setItems(R.array.person_detail_items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        addContact();
                        break;
                    case 1:
                        addRelation();
                        break;
                }
            }
        });
        dlgBld.show();
    }

    private void addRelation() {
        PersonSelectDialog dlg = new PersonSelectDialog();
        dlg.addExcludePersonId(person.getId());
        dlg.setSession(session);
        dlg.setResultHandler(new PersonSelectDialog.DialogResultHandler() {
            @Override
            public void selectedPersons(Collection<Person> selected) {
                if (selected.size() > 0) {
                    final Person relative = selected.iterator().next();
                    final RelationType[] relationTypes = RelationType.values();

                    CharSequence[] relations = new CharSequence[relationTypes.length];
                    for (int i = 0; i < relationTypes.length; i++) {
                        relations[i] = relationTypes[i].toString(getResources());
                    }
                    AlertDialog.Builder bld = new AlertDialog.Builder(getActivity())
                            .setTitle("Welche Art von Beziehung")
                            .setItems(relations, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    RelationType toSecond = relationTypes[which];
                                    RelationType toFirst;
                                    switch (toSecond) {

                                        case PARENT:
                                            toFirst = RelationType.CHILD;
                                            break;
                                        case CHILD:
                                            toFirst = RelationType.PARENT;
                                            break;
                                        case RELATIONSHIP:
                                            toFirst = RelationType.RELATIONSHIP;
                                            break;
                                        default:
                                            toFirst = RelationType.RELATIONSHIP;
                                    }
                                    Relative rel = new Relative(null, person.getId(), relative.getId(), toSecond.name(), toFirst.name());
                                    session.getRelativeDao().insert(rel);
                                    refreshRelatives(person.getRelations());

                                    Log.i(getTag(), Relative.class.getSimpleName() + " created");
                                }
                            });
                    bld.show();
                }
            }
        });
        dlg.show(getFragmentManager(), PersonSelectDialog.TAG);
    }

    private void addContact() {
        final Spinner typeSpinner = new Spinner(getActivity());
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getActivity(), android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.contact_type_values));
        typeSpinner.setAdapter(typeAdapter);
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.addView(typeSpinner);

        final EditText input = new EditText(getActivity());
        layout.addView(input);

        AlertDialog.Builder bld = new AlertDialog.Builder(getActivity()).setView(layout).setPositiveButton(R.string.lblOK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String item = typeSpinner.getSelectedItem().toString();
                String value = null;
                try {
                    Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(input.getText().toString(), Locale.getDefault().getCountry());
                    if(phoneUtil.isValidNumber(phoneNumber)) {
                        value = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
                    }
                } catch (NumberParseException e) {
                    e.printStackTrace();
                }
                if(value == null)
                    value = input.getText().toString();

                Contact newContact = new Contact(null, item, value, person.getId());
                session.getContactDao().insert(newContact);
                person.getContactList().add(newContact);
                addContact(newContact);
                setVisibleContacts();
            }
        });
        bld.setNegativeButton(R.string.lblCancel, null);
        bld.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        session = ((SessionHolder) getActivity()).getSession();
        long personId = -1;

        personId = getArguments().getLong(MainActivity.PERSONID);

        person = session.getPersonDao().load(personId);
        rootView = (TableLayout) inflater.inflate(R.layout.fragment_person_edit, container, false);

        initViews();
        initTabs();

        return rootView;
    }

    private void initTabs() {
        btnContacts = (ToggleButton) rootView.findViewById(R.id.toggleButtonContacts);
        btnRelations = (ToggleButton) rootView.findViewById(R.id.toggleButtonRelations);
        btnAdresses = (ToggleButton) rootView.findViewById(R.id.toggleButtonAdresses);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.toggleButtonContacts:
                        btnContacts.setChecked(true);
                        btnRelations.setChecked(false);
                        btnAdresses.setChecked(false);

                        setVisibleContacts();
                        break;
                    case R.id.toggleButtonRelations:
                        btnAdresses.setChecked(false);
                        btnRelations.setChecked(true);
                        btnContacts.setChecked(false);

                        setVisibleRelations();
                        break;
                    case R.id.toggleButtonAdresses:
                        btnRelations.setChecked(false);
                        btnContacts.setChecked(false);
                        btnAdresses.setChecked(true);
                        setVisibleAdresses();
                        break;
                }
            }
        };
        btnContacts.setOnClickListener(onClickListener);
        btnRelations.setOnClickListener(onClickListener);
        btnAdresses.setOnClickListener(onClickListener);

        List<Contact> contactList = person.getContactList();
        for (Contact c : contactList) {
            addContact(c);
        }

        refreshRelatives(person.getRelations());

        List<Adress> adressList = person.getAdressList();
        for (Adress adr : adressList) {

            TableRow row = new TableRow(getActivity());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            lp.span = 2;
            row.setLayoutParams(lp);
            row.setVisibility(View.GONE);

            TextView value = new TextView(getActivity());
            value.setText(adr.toString());

            adressViews.add(row);
            rootView.addView(row, rootView.getChildCount());
        }

    }

    private void setVisibleAdresses() {
        for (View vi : contactViews)
            vi.setVisibility(View.GONE);
        for (View vi : relationViews)
            vi.setVisibility(View.GONE);
        for (View vi : adressViews)
            vi.setVisibility(View.VISIBLE);
    }

    private void setVisibleRelations() {

        for (View vi : contactViews)
            vi.setVisibility(View.GONE);
        for (View vi : adressViews)
            vi.setVisibility(View.GONE);
        for (View vi : relationViews)
            vi.setVisibility(View.VISIBLE);
    }

    private void setVisibleContacts() {

        for (View vi : contactViews)
            vi.setVisibility(View.VISIBLE);
        for (View vi : relationViews)
            vi.setVisibility(View.GONE);
        for (View vi : adressViews)
            vi.setVisibility(View.GONE);
    }

    private void refreshRelatives(List<Person.RelativeType> relations) {

        for (View row : relationViews)
            rootView.removeView(row);
        relationViews.clear();

        for (Person.RelativeType r : relations) {
            addRelative(r);
        }

        if(btnRelations.isChecked()) {
            setVisibleRelations();
        }
    }

    private void addRelative(Person.RelativeType r) {

        TableRow row = new TableRow(getActivity());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        row.setVisibility(View.GONE);

        TextView label = new TextView(getActivity());
        label.setText(r.getType().toString(getResources()));
        row.addView(label);

        TextView value = new TextView(getActivity());
        value.setText(r.getRel().getPrename() + " " + r.getRel().getSurname());
        row.addView(value);
        relationViews.add(row);

        rootView.addView(row, rootView.getChildCount());
    }

    private void addContact(Contact c) {

        TableRow r = new TableRow(getActivity());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);

        r.setLayoutParams(lp);

        TextView label = new TextView(getActivity());

        label.setText(c.getType());
        r.addView(label);

        TextView value = new TextView(getActivity());
        String val = null;

        try {
            Phonenumber.PhoneNumber number = phoneUtil.parse(c.getValue(), Locale.getDefault().getCountry());
            if(phoneUtil.isValidNumber(number))
                val = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }

        if(val == null)
            val = c.getValue();

        value.setText(val);

        r.addView(value);
        contactViews.add(r);
        rootView.addView(r, rootView.getChildCount());
    }

    private void initViews() {
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
