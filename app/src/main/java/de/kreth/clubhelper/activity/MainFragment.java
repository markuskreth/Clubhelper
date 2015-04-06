package de.kreth.clubhelper.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.kreth.clubhelper.Contact;
import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.R;
import de.kreth.clubhelper.dao.ContactDao;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.dao.PersonDao;
import de.kreth.clubhelper.datahelper.PersonRelationHelper;
import de.kreth.clubhelper.datahelper.SessionHolder;
import de.kreth.clubhelper.dialogs.PersonDialog;
import de.kreth.clubhelper.dialogs.PersonViewDialog;
import de.kreth.clubhelper.widgets.PersonAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemLongClickListener {

    public static final String TAG = MainFragment.class.getName();
    private PersonAdapter adapter;
    private DaoSession session;
    private OnMainFragmentEventListener listener;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnMainFragmentEventListener)
            listener = (OnMainFragmentEventListener) activity;
        else
            throw new ClassCastException(activity.getClass().getName() + " must implement " + OnMainFragmentEventListener.class.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        session = ((SessionHolder) getActivity()).getSession();

        adapter = new PersonAdapter(getActivity(), session.getPersonDao());
        setupListView(rootView);
        setupSearch(rootView);
        return rootView;
    }

    private void setupSearch(View rootView) {

        final EditText edtText = (EditText) rootView.findViewById(R.id.searchInput);
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
        rootView.findViewById(R.id.imageViewDeleteInput).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edtText.setText("");
                    }
                });
    }

    private void setupListView(View rootView) {

        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setTextFilterEnabled(true);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final Person person = adapter.getItem(position);
                PersonViewDialog dlg = new PersonViewDialog();
                dlg.setNeutralButton(getText(R.string.lbl_edit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.editPerson(person.getId());
                    }
                });
                dlg.setPerson(person);
                dlg.show(getFragmentManager(), PersonViewDialog.class.getName());

//                String txt = new PersonRelationHelper(getResources()).relationsAsString(person);
//                Toast.makeText(getActivity(), txt, Toast.LENGTH_LONG).show();
            }
        });
        listView.setOnItemLongClickListener(this);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position,
                                   long id) {

        final Person person = adapter.getItem(position);
        listener.editPerson(person.getId());
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_edit).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_addPerson) {
            createNewPerson();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

   private void createNewPerson() {

        final Person person = new Person();
        AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());

        ViewGroup view1 = (ViewGroup) getActivity().getLayoutInflater().inflate(
                R.layout.person_complete, null);
        dlg.setView(view1);
        dlg.setNegativeButton(R.string.lblCancel, null);

        final PersonDialog p = new PersonDialog(view1, person);

        dlg.setPositiveButton(R.string.lblSave, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ContactDao contactDao = session.getContactDao();
                for (Contact c : p.getContactList()) {
                    if (c.getId() == null)
                        contactDao.insertOrReplace(c);
                    else
                        contactDao.update(c);
                }

                person.setPrename(p.getPrename().toString());
                person.setSurname(p.getTxtSurname().toString());
                if(p.getBirthday() != null)
                    person.setBirth(p.getBirthday().getTime());
                else
                    person.setBirth(null);

                person.setChanged(new Date());
                person.setCreated(new Date());
                PersonDao personDao = session.getPersonDao();
                personDao.insertOrReplace(person);

                adapter.notifyDataSetChanged();
            }
        });

        dlg.show();
    }

    public interface OnMainFragmentEventListener {
        void editPerson(long personId);
    }
}
