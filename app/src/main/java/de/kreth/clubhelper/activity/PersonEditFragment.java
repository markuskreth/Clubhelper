package de.kreth.clubhelper.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import de.kreth.clubhelper.Adress;
import de.kreth.clubhelper.Contact;
import de.kreth.clubhelper.MainActivity;
import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.PersonType;
import de.kreth.clubhelper.R;
import de.kreth.clubhelper.RelationType;
import de.kreth.clubhelper.Relative;
import de.kreth.clubhelper.dao.AdressDao;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.datahelper.SessionHolder;
import de.kreth.clubhelper.widgets.ContactEditDialog;
import de.kreth.clubhelper.widgets.ContactTypeAdapter;
import de.kreth.clubhelper.widgets.PersonSelectDialog;
import de.kreth.clubhelper.widgets.PersonTypeAdapter;

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
    private MainFragment.OnMainFragmentEventListener fragmentEventListener = null;
    private Spinner spinnerPersonType;
    private PersonTypeAdapter personTypeAdapter;

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
        } else if(item.getItemId() == R.id.action_edit) {
            editDetail();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editDetail() {

        AlertDialog.Builder dlgBld = new AlertDialog.Builder(getActivity()).setTitle(R.string.title_quest_which_info_toedit).setItems(R.array.person_detail_items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        editContact();
                        break;
                    case 1:
//                        editRelation();
                        break;
                    case 2:
                        editAdress();
                        break;
                }
            }

        });
        dlgBld.show();
    }

    private void editContact() {
        final List<Contact> contactList = person.getContactList();
        String[] items = new String[contactList.size()];

        for (int i = 0; i < contactList.size(); i++) {
            items[i] = contactList.get(i).getType() + ": " + contactList.get(i).getValue();
        }
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title_quest_which_info_toedit)
                .setNeutralButton(R.string.lblCancel, null)
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Contact c = contactList.get(which);

                        ContactEditDialog.ContactEditDialogResult result = new ContactEditDialog.ContactEditDialogResult() {
                            @Override
                            public void contactToStore(Contact contact) {
                                if (!(c.getType().matches(contact.getType()) || c.getValue().matches(contact.getValue()))) {
                                    contact.setChanged(new Date());
                                    session.getContactDao().update(contact);
                                }
                            }
                        };
                        Contact toChange = new Contact(c.getId(), c.getType(), c.getValue(), c.getPersonId(), c.getChanged(), c.getCreated());
                        new ContactEditDialog(getActivity(), toChange, result).show();
                    }
                })
                .show();
    }
    private void addDetail() {
        AlertDialog.Builder dlgBld = new AlertDialog.Builder(getActivity()).setTitle(R.string.title_quest_which_info_toadd).setItems(R.array.person_detail_items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        addContact();
                        break;
                    case 1:
                        addRelation();
                        break;
                    case 2:
                        addAdress();
                        break;
                }
            }
        });
        dlgBld.show();
    }

    private void editAdress() {
        final ViewGroup root = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.person_adress_layout, null);
        List<Adress> adressList = person.getAdressList();
        if(adressList.size()>0) {
            final Adress adress = adressList.get(0);
            final EditText edtAdr1 = (EditText) root.findViewById(R.id.editTextAdress1);
            edtAdr1.setText(adress.getAdress1());
            final EditText edtAdr2 = (EditText) root.findViewById(R.id.editTextAdress2);
            edtAdr2.setText(adress.getAdress2());
            final EditText edtZip = (EditText) root.findViewById(R.id.editTextAdressZip);
            edtZip.setText(adress.getPlz());
            final EditText edtCity = (EditText) root.findViewById(R.id.editTextAdressCity);
            edtCity.setText(adress.getCity());

            new AlertDialog.Builder(getActivity())
                    .setView(root)
                    .setPositiveButton(R.string.lblOK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String adr1 = edtAdr1.getText().toString();
                            String adr2 = edtAdr2.getText().toString();
                            String zip = edtZip.getText().toString();
                            String city = edtCity.getText().toString();
                            AdressDao adressDao = session.getAdressDao();
                            adress.setAdress1(adr1);
                            adress.setAdress2(adr2);
                            adress.setPlz(zip);
                            adress.setCity(city);
                            adress.setChanged(new Date());
                            adressDao.update(adress);
                            person.resetAdressList();
                            refreshAdresses(person.getAdressList());
                        }
                    })
                    .setNegativeButton(R.string.lblCancel, null)
                    .setNeutralButton(R.string.lblDelete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            session.getAdressDao().delete(adress);
                            person.resetAdressList();
                            refreshAdresses(person.getAdressList());
                        }
                    })
                    .show();
        }
    }

    private void addAdress() {
        final ViewGroup root = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.person_adress_layout, null);
        new AlertDialog.Builder(getActivity())
                .setView(root)
                .setPositiveButton(R.string.lblOK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String adr1 = ((EditText) root.findViewById(R.id.editTextAdress1)).getText().toString();
                        String adr2 = ((EditText) root.findViewById(R.id.editTextAdress2)).getText().toString();
                        String zip = ((EditText) root.findViewById(R.id.editTextAdressZip)).getText().toString();
                        String city = ((EditText) root.findViewById(R.id.editTextAdressCity)).getText().toString();

                        Date now = new Date();
                        Adress a = new Adress(null, adr1, adr2, zip, city, person.getId(), now, now);
                        AdressDao adressDao = session.getAdressDao();
                        adressDao.insert(a);
                        addAdress(a);
                        person.resetAdressList();
                        refreshAdresses(person.getAdressList());
                    }
                }).setNegativeButton(R.string.lblCancel, null)
                .show();
    }

    private void addAdress(Adress adr) {

        TableRow row = new TableRow(getActivity());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        lp.span = 2;
        row.setLayoutParams(lp);
        row.setVisibility(View.GONE);

        TextView value = new TextView(getActivity());
        value.setText(adr.toString());
        row.addView(value);

        adressViews.add(row);
        rootView.addView(row, rootView.getChildCount());
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
                            .setTitle(R.string.title_quest_kind_relation)
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
                                        default:
                                            toFirst = toSecond;
                                    }

                                    Date now = new Date();
                                    Relative rel = new Relative(null, person.getId(), relative.getId(), toSecond.name(), toFirst.name(), now, now);
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

        Date now = new Date();
        Contact newContact = new Contact(null, "", "" , person.getId(), now, now);
        ContactEditDialog.ContactEditDialogResult result = new ContactEditDialog.ContactEditDialogResult() {
            @Override
            public void contactToStore(Contact contact) {

                session.getContactDao().insert(contact);
                person.getContactList().add(contact);
                addContact(contact);
                setVisibleContacts();
            }
        };

        final ContactEditDialog dlg = new ContactEditDialog(getActivity(), newContact, result);
        dlg.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        long personId = -1;

        personId = getArguments().getLong(MainActivity.PERSONID);

        session = ((SessionHolder) getActivity()).getSession();
        if(session == null)
            return null;

        person = session.getPersonDao().load(personId);
        rootView = (TableLayout) inflater.inflate(R.layout.fragment_person_edit, container, false);
        if(getActivity() instanceof MainFragment.OnMainFragmentEventListener){
            this.fragmentEventListener = (MainFragment.OnMainFragmentEventListener) getActivity();
        }
        initViews();
        initTabs();

        return rootView;
    }

    private void initViews() {
        this.preName = (EditText) rootView.findViewById(R.id.textPreName);
        this.preName.setText(person.getPrename());

        this.surName = (EditText) rootView.findViewById(R.id.textSurName);
        this.surName.setText(person.getSurname());

        this.spinnerPersonType = (Spinner) rootView.findViewById(R.id.spinner_person_type);
        this.personTypeAdapter = new PersonTypeAdapter(getResources());
        this.spinnerPersonType.setAdapter(personTypeAdapter);

        if(person.getType() == null) {
            person.setPersonType(PersonType.ACTIVE);
            if(person.getId() != null)
                try {
                    person.update();
                } catch (RuntimeException e) {
                    Log.e(TAG, "Fehler bei update PersonType", e);
                }
        }

        int personType = personTypeAdapter.getPosition(person.getPersonType());
        spinnerPersonType.setSelection(personType);

        this.txtBirth = (TextView) rootView.findViewById(R.id.textBirth);

        this.txtBirth.setText(SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(
                person.getBirth()));

        this.txtBirth.setOnClickListener(this);
        rootView.findViewById(R.id.lblBirthday).setOnClickListener(this);
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
        refreshAdresses(person.getAdressList());

    }

    @Override
    public void onPause() {

        final String preNameString = preName.getText().toString();
        final String surnameString = surName.getText().toString();
        final PersonType type = (PersonType) spinnerPersonType.getSelectedItem();
        person.setPrename(preNameString);
        person.setSurname(surnameString);
        person.setType(type.name());
        person.update();

        super.onPause();
    }

    private void refreshAdresses(List<Adress> adressList) {
        for (View adrView : adressViews) {
            rootView.removeView(adrView);
        }
        adressViews.clear();

        for (Adress adr : adressList) {
            addAdress(adr);
        }
        if(btnAdresses.isChecked()) {
            setVisibleAdresses();
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
        if(this.fragmentEventListener != null) {
            row.setOnClickListener(new RelativeEditListener(r.getRel()));
        }

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

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Date time = new GregorianCalendar(year, month, day).getTime();
        person.setBirth(time);
        this.txtBirth.setText(SimpleDateFormat
                                    .getDateInstance(SimpleDateFormat.MEDIUM)
                                    .format(time));
        person.setChanged(new Date());
        session.getPersonDao().update(person);
    }

    @Override
    public void onClick(View view) {

        Calendar birth = new GregorianCalendar(2000,1,1);
        Date personBirth = person.getBirth();
        if(personBirth != null)
            birth.setTime(personBirth);

        int year = birth.get(Calendar.YEAR);
        int month = birth.get(Calendar.MONTH);
        int day = birth.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dlg = new DatePickerDialog(getActivity(), this, year, month, day);
        dlg.show();
    }

    private class RelativeEditListener implements View.OnClickListener {
        private Person personId;

        private RelativeEditListener(Person personId) {
            this.personId = personId;
        }

        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Bearbeiten?")
                    .setMessage(personId.toString() + " bearbeiten?")
                    .setNegativeButton(R.string.lblNo, null)
                    .setPositiveButton(R.string.lblYes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            fragmentEventListener.editPerson(personId.getId());
                        }
                    })
                    .show();
        }
    }
}
