package de.kreth.clubhelper.dialogs;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.zip.Inflater;

import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.R;
import de.kreth.clubhelper.dao.PersonDao;

/**
 * Created by markus on 21.12.14.
 */
public class PersonDialog implements DatePicker.OnDateChangedListener {
    private final ViewGroup dialog;

    private TextView txtPrename;
    private TextView txtSurname;
    private DatePicker birthday;
    private Person person;
    private Calendar birth;

    public PersonDialog(ViewGroup view, Person person) {

        dialog = view;
        this.person = person;
        initComponents();
        initPersonView();
    }

    private void initPersonView() {
        txtPrename.setText(person.getPrename());
        txtSurname.setText(person.getSurname());
        birth = new GregorianCalendar();
        birth.setTime(person.getBirth());

        int year=birth.get(Calendar.YEAR);
        int month=birth.get(Calendar.MONTH);
        int day=birth.get(Calendar.DAY_OF_MONTH);
        birthday.init(year, month, day, this);
    }

    private void initComponents() {
        txtPrename = (TextView) dialog.findViewById(R.id.textPreName);
        txtSurname = (TextView) dialog.findViewById(R.id.textSurName);
        birthday = (DatePicker) dialog.findViewById(R.id.datePicker);
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
    public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
        birth.set(year, month, day);
    }
}
