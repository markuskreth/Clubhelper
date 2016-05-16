package de.kreth.clubhelper.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;

import de.kreth.clubhelper.Contact;
import de.kreth.clubhelper.R;

/**
 * Created by markus on 03.04.15.
 */
public class ContactEditDialog {

    private final Contact contactToEdit;
    private final ContactEditDialogResult resultHandler;
    private PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    private Context context;

    public ContactEditDialog(Context context, final Contact contactToEdit, ContactEditDialogResult resultHandler) {
        this.context = context;
        this.contactToEdit = contactToEdit;
        this.resultHandler = resultHandler;
    }

    public void show() {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL|Gravity.LEFT;

        final Spinner typeSpinner = new Spinner(context);
        typeSpinner.setLayoutParams(params);
        ContactTypeAdapter typeAdapter = new ContactTypeAdapter(context, context.getResources().getStringArray(R.array.contact_type_values));
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setSelection(typeAdapter.getPosition(contactToEdit.getType()));

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.addView(typeSpinner);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1);
        params.gravity = Gravity.CENTER_VERTICAL|Gravity.RIGHT;

        final EditText input = new EditText(context);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
        input.setLayoutParams(params);
        input.setText(contactToEdit.getValue());
        layout.addView(input);

        connectSpinnerWithEditText(typeSpinner, input);

        AlertDialog.Builder bld = new AlertDialog.Builder(context)
                .setView(layout)
                .setPositiveButton(R.string.lblOK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = typeSpinner.getSelectedItem().toString();
                        String value = null;
                        try {
                            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(input.getText().toString(), Locale.getDefault().getCountry());
                            if (phoneUtil.isValidNumber(phoneNumber)) {
                                value = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
                            }
                        } catch (NumberParseException e) {
                            e.printStackTrace();
                        }
                        if (value == null)
                            value = input.getText().toString();

                        contactToEdit.setType(item);
                        contactToEdit.setValue(value);
                        resultHandler.contactToStore(contactToEdit);
                    }
                })

                .setNegativeButton(R.string.lblCancel, null);
        bld.show();
    }

    private void connectSpinnerWithEditText(Spinner typeSpinner, final EditText input) {
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position < 2)
                    input.setInputType(InputType.TYPE_CLASS_PHONE);
                else
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                input.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });
    }

    public interface ContactEditDialogResult {
        /**
         * Contact to be stored as result of this Dialog. Is the same object as supplied in the Constructor.
         * @param contact
         */
        void contactToStore(Contact contact);
    }
}
