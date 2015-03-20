package de.kreth.clubhelper.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.kreth.clubhelper.Adress;
import de.kreth.clubhelper.Contact;
import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.R;
import de.kreth.clubhelper.RelationType;
import de.kreth.datecalc.DateDiff;
import de.kreth.datecalc.DateUnit;

/**
 * Created by markus on 12.03.15.
 */
public class PersonViewDialog extends DialogFragment {

    private Person person;

    private PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    private String[] types;
    private String[] actions;

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Resources resources = getActivity().getResources();
        types = resources.getStringArray(R.array.contact_type_values);
        actions = resources.getStringArray(R.array.mobile_actions);

        AlertDialog dlg = new AlertDialog.Builder(getActivity())
                .setNeutralButton(R.string.lblCancel, null)
                .setTitle(person.getPrename() + " " + person.getSurname())
                .create();

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.person_call_layout, null);

        TextView birth = (TextView) view.findViewById(R.id.textBirth);

        long age = DateDiff.calcDiff(person.getBirth(), new Date(), DateUnit.YEAR);
        String ageText = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(person.getBirth()) + ", Alter " + age;
        birth.setText(ageText);

        TableLayout table = (TableLayout) view.findViewById(R.id.table);

        for (Contact con : person.getContactList()) {
            addContactToTable(layoutInflater, table, con);
        }
        for (Person.RelativeType rel : person.getRelations()) {

            List<Contact> contactList = rel.getRel().getContactList();

            TextView text = new TextView(getActivity());
            String relText = rel.getType().toString(getActivity().getResources()) + ": " + rel.getRel().getPrename() + " " + rel.getRel().getSurname();
            text.setText(relText);

            TableRow.LayoutParams params = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            params.span = 3;
            params.column = 0;
            params.setMargins(30, 30, 0, 0);
            text.setLayoutParams(params);

            TableRow row = new TableRow(getActivity());
            row.addView(text);
            table.addView(row);

            for (Contact con : contactList) {
                addContactToTable(layoutInflater, table, con);
            }

        }
        List<Adress> adressList = person.getAdressList();
        for(Adress a: adressList) {
            addAdressToTable(table, a);
        }
        dlg.setView(view);
        return dlg;
    }

    private void addAdressToTable(TableLayout table, Adress a) {
        TableRow row = new TableRow(getActivity());
        TextView view = new TextView(getActivity());
        view.setSingleLine(false);
        view.setText(a.toString());
        row.addView(view);
        table.addView(row);
    }

    private void addContactToTable(LayoutInflater layoutInflater, TableLayout table, Contact con) {

        int index = 0;
        String conType = con.getType();
        for (int i = 0; i < types.length; i++) {
            if (types[i].matches(conType)) {
                index = i;
                break;
            }
        }
        int imgResource = android.R.drawable.sym_action_call;
        switch (index) {
            case 0:
            case 1:
                imgResource = android.R.drawable.sym_action_call;
                break;
            case 2:
                imgResource = android.R.drawable.sym_action_email;
        }
        ContactOnClickListener contactOnClickListener = new ContactOnClickListener(con);
        TableRow row = (TableRow) layoutInflater.inflate(R.layout.contact_call, table, false);

        TextView textType = (TextView) row.findViewById(R.id.textViewType);
        textType.setOnClickListener(contactOnClickListener);
        textType.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
        textType.setText(conType);

        TextView textValue = (TextView) row.findViewById(R.id.textViewValue);
        textValue.setOnClickListener(contactOnClickListener);

        textValue.setText(con.getValue());

        table.addView(row);
    }

    private class ContactOnClickListener implements View.OnClickListener {

        private Contact con;

        private ContactOnClickListener(Contact con) {
            this.con = con;
        }

        @Override
        public void onClick(View v) {

            int index = 0;
            for (int i = 0; i < types.length; i++) {
                if (types[i].matches(con.getType())) {
                    index = i;
                    break;
                }
            }
            if (index == 0) {
                new AlertDialog.Builder(getActivity()).setItems(actions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 1) {

                            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                            smsIntent.putExtra("sms_body", "");
                            smsIntent.putExtra("address", con.getValue());
                            smsIntent.setType("vnd.android-dir/mms-sms");

                            getActivity().startActivity(Intent.createChooser(smsIntent, getActivity().getText(R.string.title_connect_sms)));
                        } else {
                            String url = "tel:" + con.getValue();
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                            getActivity().startActivity(intent);
                        }
                    }
                }).setNeutralButton(R.string.lblCancel, null)
                        .show();
            } else if (index == 1) {   // Phone

                String url = "tel:" + con.getValue();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                getActivity().startActivity(intent);
            } else if (index == 2) {  // Email

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{con.getValue()});
                i.putExtra(Intent.EXTRA_SUBJECT, "");
                i.putExtra(Intent.EXTRA_TEXT, "\n\nMarkus Kreth\nTrainer Trampolinturnen - MTV Groß-Buchholz");
                try {
                    getActivity().startActivity(Intent.createChooser(i, "Sende Email..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "Keine Email Apps gefunden.", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}
