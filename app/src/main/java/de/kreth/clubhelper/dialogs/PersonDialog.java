package de.kreth.clubhelper.dialogs;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
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
public class PersonDialog implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private final ViewGroup dialog;
    private final java.text.DateFormat df;

    private TextView txtPrename;
    private TextView txtSurname;
    private TextView birthday;
    private Person person;
    private Calendar birth;

    public PersonDialog(ViewGroup view, Person person) {

        dialog = view;
        this.person = person;
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
    }

    private void initComponents() {
        txtPrename = (TextView) dialog.findViewById(R.id.textPreName);
        txtSurname = (TextView) dialog.findViewById(R.id.textSurName);
        birthday = (TextView) dialog.findViewById(R.id.textBirth);
        birthday.setFocusable(false);
        birthday.setOnClickListener(this);

        ImageButton btn = (ImageButton) dialog.findViewById(R.id.imageButton);
        btn.setOnClickListener(this);
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
        DatePickerDialog dlg = new DatePickerDialog(view.getContext(),this, year, month, day);
        dlg.show();
    }

}
