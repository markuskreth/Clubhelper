package de.kreth.clubhelper.widgets;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.R;
import de.kreth.clubhelper.dao.DaoSession;

/**
 * Created by markus on 22.02.15.
 */
public class PersonSelectDialog extends DialogFragment {

    public static final String TAG = PersonSelectDialog.class.getName();

    private DaoSession session;
    private PersonAdapter adapter;
    private List<Person> selected = new ArrayList<>();
    private List<Long> excluded = null;
    private DialogResultHandler resultHandler;

    public PersonSelectDialog setSession(DaoSession session) {
        this.session = session;
        return this;
    }

    public void addExcludePersonId(long idToExclude) {
        if(excluded == null)
            excluded = new ArrayList<>();
        excluded.add(idToExclude);
    }

    public void clearExcludes() {
        excluded = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        adapter = new PersonAdapter(getActivity(), session.getPersonDao());

        if(excluded != null) {
            for(Long id : excluded)
                adapter.getFilter().addExcludePersonId(id);
            adapter.getFilter().filter("");
        }
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_person_select, null);

        final ListView listView = (ListView) rootView.findViewById(R.id.listView2);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected.add(adapter.getItem(position));
                PersonSelectDialog.this.dismiss();
            }
        });

        EditText edtText = (EditText) rootView.findViewById(R.id.editText);
        edtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Filter filter = adapter.getFilter();
                filter.filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(rootView);
        builder.setNegativeButton(R.string.lblCancel, null);

        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(resultHandler != null)
            resultHandler.selectedPersons(getSelected());
        super.onDismiss(dialog);
    }

    public void setResultHandler(DialogResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }

    private Collection<Person> getSelected() {
        return new ArrayList<>(selected);
    }

    public static interface DialogResultHandler {
        public void selectedPersons(Collection<Person> selected);
    }
}
