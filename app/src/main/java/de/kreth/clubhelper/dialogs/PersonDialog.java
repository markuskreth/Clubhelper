package de.kreth.clubhelper.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import de.kreth.clubhelper.Contact;
import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.R;

/**
 * Created by markus on 21.12.14.
 */
public class PersonDialog implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private final ViewGroup dialog;
    private final java.text.DateFormat df;
    private final List<Contact> contactList;

    final private Person person;
    private TextView txtPrename;
    private TextView txtSurname;
    private TextView birthday;
    private Calendar birth;

    public PersonDialog(ViewGroup view, Person person) {

        dialog = view;
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
        birth = new GregorianCalendar();
        birth.setTime(person.getBirth());
        birthday.setText(df.format(person.getBirth()));
        initContacts();
    }

    private void initContacts() {
        for (Contact c : this.contactList) {
            addContactToDialog(c);
        }
    }

    private void addContactToDialog(final Contact contact) {

        TableRow row = new TableRow(dialog.getContext());
        row.setTag(contact);
        Spinner lbl = new Spinner(dialog.getContext());
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(dialog.getContext(),
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
        EditText editText = new EditText(dialog.getContext());
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

        dialog.addView(row);
    }

    private void initComponents() {
        txtPrename = (TextView) dialog.findViewById(R.id.textPreName);
        txtSurname = (TextView) dialog.findViewById(R.id.textSurName);
        birthday = (TextView) dialog.findViewById(R.id.textBirth);
        birthday.setFocusable(false);
        birthday.setOnClickListener(this);

        ImageButton btn = (ImageButton) dialog.findViewById(R.id.imageButton);
        btn.setOnClickListener(this);
        dialog.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(dialog.getContext()).setItems(R.array.person_edit_options,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dlg,
                                    int which) {
                                switch (which) {
                                    case 0:
                                        new AlertDialog.Builder(
                                                dialog.getContext()).setMessage(
                                                "Kontakte bearbeiten").setNeutralButton(
                                                R.string.lblOK,
                                                null).show();
                                        Contact nc = new Contact(
                                                null,
                                                "Mobile",
                                                "0174-2521286",
                                                person.getId());
                                        addContactToDialog(
                                                nc);
                                        break;
                                    case 1:
                                        new AlertDialog.Builder(
                                                dialog.getContext()).setMessage(
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

    public CharSequence getTxtSurname() {
        return txtSurname.getText();
    }

    public Calendar getBirthday() {
        return birth;
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
