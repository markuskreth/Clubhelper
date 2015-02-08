package de.kreth.clubhelper.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.R;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.dao.PersonDao;
import de.kreth.clubhelper.dialogs.PersonDialog;
import de.kreth.clubhelper.widgets.PersonAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private List<Person> persons;
    private PersonAdapter adapter;
    private DaoSession session;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final PersonDao personDao = session.getPersonDao();
        persons = personDao.loadAll();

        adapter = new PersonAdapter(getActivity(), persons);

        ListView listView = (ListView)rootView.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final Person person = persons.get(position);
                StringBuilder txt = new StringBuilder(person.toString());
                List<Person.RelativeType> relations = person.getRelations();
                for(Person.RelativeType r: relations) {
                    txt.append("\n");
                    switch (r.getType()){

                        case MOTHER:
                            txt.append("Mutter: ");
                            break;
                        case FATHER:
                            txt.append("Vater: ");
                            break;
                        case CHILD:
                            txt.append("Kind: ");
                            break;
                        case RELATIONSHIP:
                            txt.append("Freund(-in): ");
                            break;
                    }
                    txt.append(r.getRel().getId()).append(": ").append(r.getRel().getPrename()).append(" ").append(r.getRel().getSurname());
                }
                Toast.makeText(getActivity(), txt.toString(), Toast.LENGTH_LONG).show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                final Person person = persons.get(position);

                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());

                ViewGroup view1 = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.person_complete, null);
                dlg.setView(view1);
                dlg.setNegativeButton(R.string.lblCancel, null);

                final PersonDialog p = new PersonDialog(view1, person);

                dlg.setPositiveButton(R.string.lblSave, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        person.setPrename(p.getPrename().toString());
                        person.setSurname(p.getTxtSurname().toString());
                        person.getBirth().setTime(p.getBirthday().getTimeInMillis());
                        personDao.update(person);
                        adapter.notifyDataSetChanged();
                    }
                });

                dlg.show();

                return true;
            }
        });
        return rootView;
    }

    public void createNewPerson() {

        final Person person = new Person();
        person.setBirth(new GregorianCalendar(2000, Calendar.JANUARY, 1).getTime());
        AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());

        ViewGroup view1 = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.person_complete, null);
        dlg.setView(view1);
        dlg.setNegativeButton(R.string.lblCancel, null);

        final PersonDialog p = new PersonDialog(view1, person);

        dlg.setPositiveButton(R.string.lblSave, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                person.setPrename(p.getPrename().toString());
                person.setSurname(p.getTxtSurname().toString());
                person.getBirth().setTime(p.getBirthday().getTimeInMillis());
                PersonDao personDao = session.getPersonDao();
                personDao.insertOrReplace(person);
                persons.clear();
                persons.addAll(personDao.loadAll());
                adapter.notifyDataSetChanged();
            }
        });

        dlg.show();
    }

    public MainFragment setSession(DaoSession session) {
        this.session = session;
        return this;
    }

}
