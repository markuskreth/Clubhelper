package de.kreth.clubhelper.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.kreth.clubhelper.data.Contact;
import de.kreth.clubhelper.data.Person;
import de.kreth.clubhelper.data.PersonType;
import de.kreth.clubhelper.R;
import de.kreth.clubhelper.data.SyncStatus;
import de.kreth.clubhelper.widgets.PersonTypeAdapter;

/**
 * Created by markus on 21.12.14.
 */
public class PersonDialog implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private final ViewGroup viewGroup;
    private final java.text.DateFormat df;
    private final List<Contact> contactList;

    final private Person person;
    private TextView txtPrename;
    private TextView txtSurname;
    private TextView birthday;
    private Spinner personType;
    private Calendar birth;
    private PersonTypeAdapter personTypeAdapter;

    public PersonDialog(ViewGroup view, Person person) {

        viewGroup = view;
        this.person = person;
        this.contactList = new ArrayList<>();
        if (person.getId() != null)
            this.contactList.addAll(person.getContactList());
        df = DateFormat.getDateFormat(view.getContext());
        initComponents();
        initPersonView();
    }

    private void initPersonView() {
        txtPrename.setText(person.getPrename());
        txtSurname.setText(person.getSurname());
        birth = new GregorianCalendar(2000,1,1);

        if (person.getBirth() != null) {
            birth.setTime(person.getBirth());
            birthday.setText(df.format(birth.getTime()));
        } else
            birthday.setText("");

        if(person.getType() != null)
            personType.setSelection(personTypeAdapter.getPosition(person.getPersonType()));
        initContacts();
    }

    private void initContacts() {
        for (Contact c : this.contactList) {
            addContactToDialog(c);
        }
    }

    private void addContactToDialog(final Contact contact) {

        TableRow row = new TableRow(viewGroup.getContext());
        row.setTag(contact);
        Spinner lbl = new Spinner(viewGroup.getContext());
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(viewGroup.getContext(),
                R.array.contact_type_values,
                android.R.layout.simple_spinner_item);
        lbl.setAdapter(typeAdapter);
        lbl.setSelection(typeAdapter.getPosition(contact.getType()));
        lbl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                contact.setType(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        row.addView(lbl);
        EditText editText = new EditText(viewGroup.getContext());
        editText.setText(contact.getValue());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contact.setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        row.addView(editText);

        viewGroup.addView(row);
    }

    private void initComponents() {

        InputMethodManager imm = (InputMethodManager) viewGroup.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        txtPrename = (TextView) viewGroup.findViewById(R.id.textPreName);
        imm.showSoftInput(txtPrename, InputMethodManager.SHOW_IMPLICIT);
        txtSurname = (TextView) viewGroup.findViewById(R.id.textSurName);
        imm.showSoftInput(txtSurname, InputMethodManager.SHOW_IMPLICIT);

        this.personTypeAdapter = new PersonTypeAdapter(viewGroup.getContext().getResources());
        personType = (Spinner) viewGroup.findViewById(R.id.person_type);
        personType.setAdapter(personTypeAdapter);

        birthday = (TextView) viewGroup.findViewById(R.id.textBirth);
        birthday.setFocusable(false);
        birthday.setOnClickListener(this);
        viewGroup.findViewById(R.id.lblBirthday).setOnClickListener(this);

        viewGroup.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(viewGroup.getContext()).setItems(R.array.person_edit_options,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dlg,
                                    int which) {
                                Date now = new Date();

                                switch (which) {
                                    case 0:
                                        new AlertDialog.Builder(
                                                viewGroup.getContext()).setMessage(
                                                "Kontakte bearbeiten").setNeutralButton(
                                                R.string.lblOK,
                                                null).show();
                                        Contact nc = new Contact(
                                                null,
                                                "Mobile",
                                                "0174-2521286",
                                                person.getId(), now, now, SyncStatus.NEW);
                                        addContactToDialog(nc);
                                        break;
                                    case 1:
                                        new AlertDialog.Builder(
                                                viewGroup.getContext()).setMessage(
                                                "Beziehungen bearbeiten").setNeutralButton(
                                                R.string.lblOK,
                                                null).show();
                                        break;
                                }
                            }
                        }).setNeutralButton(
                        R.string.lblCancel, null).show();
                return true;
            }
        });
    }

    public List<Contact> getContactList() {
        return contactList;
    }

    public CharSequence getPrename() {
        return txtPrename.getText();
    }

    public PersonType getPersonType() {
        return personTypeAdapter.getItem((int) personType.getSelectedItemId());
    }

    public CharSequence getTxtSurname() {
        return txtSurname.getText();
    }

    public Calendar getBirthday() {
        if(birthday.getText().length()>0)
            return birth;
        else
            return null;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        birth.set(year, month, day);
        birthday.setText(df.format(birth.getTime()));
    }

    @Override
    public void onClick(View view) {
        int year = birth.get(Calendar.YEAR);
        int month = birth.get(Calendar.MONTH);
        int day = birth.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dlg = new DatePickerDialog(view.getContext(), this, year, month, day);
        dlg.show();
    }


}
