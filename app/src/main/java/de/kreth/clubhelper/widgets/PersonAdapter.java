package de.kreth.clubhelper.widgets;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.dao.PersonDao;
import de.kreth.datecalc.DateDiff;
import de.kreth.datecalc.DateUnit;

/**
 * Created by markus on 21.12.14.
 */
public class PersonAdapter extends BaseAdapter implements Filterable {

    final private DateFormat df = new SimpleDateFormat("yyyy");
    final private PersonAdapterFilter filter;
    final private Context context;
    private CharSequence lastConstraint = "";
    final private List<Person> objects;
    final private PersonDao personDao;

    public PersonAdapter(Context context, PersonDao personDao) {
        objects = personDao.loadAll();
        this.personDao = personDao;
        this.context = context;
        filter = new PersonAdapterFilter(personDao);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Person getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return objects.get(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void notifyDataSetChanged() {
        objects.clear();
        objects.addAll(personDao.loadAll());
        if (lastConstraint != null && lastConstraint.length() > 0)
            filter.filter(lastConstraint);
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        objects.clear();
        objects.addAll(personDao.loadAll());
        super.notifyDataSetInvalidated();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Person item = objects.get(position);

        long age = DateDiff.calcDiff(item.getBirth(), new Date(), DateUnit.YEAR);
        String ageText = " (JG " + df.format(item.getBirth()) + ", Alter " + age + ")";
        TextView view;

        if (convertView == null) {
            view = new TextView(context);
            view.setTag(item.getId());
        } else
            view = (TextView) convertView;
        view.setText(item.getSurname() + ", " + item.getPrename() + ageText);
        return view;
    }

    @Override
    public PersonAdapterFilter getFilter() {
        return filter;
    }

    private class PersonAdapterFilter extends Filter {

        private final PersonDao personDao;

        public PersonAdapterFilter(PersonDao personDao) {
            this.personDao = personDao;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Person> persons = personDao.loadAll();

            if (constraint == null || constraint.length() == 0) {
                results.values = persons;
                results.count = persons.size();
            } else {
                List<Person> filtered = new ArrayList<>();
                String lowerConstraint = constraint.toString().toLowerCase(Locale.GERMANY);
                for (Person p : persons) {
                    if (p.getPrename().toLowerCase(Locale.GERMANY).contains(
                            lowerConstraint) || p.getSurname().toLowerCase(Locale.GERMANY).contains(lowerConstraint))
                        filtered.add(p);
                }

                results.values = filtered;
                results.count = filtered.size();
            }

            lastConstraint = constraint;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                objects.clear();
                @SuppressWarnings("unchecked")
                List<Person> values = (List<Person>) results.values;
                objects.addAll(values);
                PersonAdapter.super.notifyDataSetChanged();
            }
        }
    }

}
