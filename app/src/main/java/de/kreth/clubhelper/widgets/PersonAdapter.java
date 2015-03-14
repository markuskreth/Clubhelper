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

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.dao.PersonDao;
import de.kreth.datecalc.DateDiff;
import de.kreth.datecalc.DateUnit;

/**
 * Created by markus on 21.12.14.
 */
public class PersonAdapter extends BaseAdapter implements Filterable {

    final private DateFormat df = new SimpleDateFormat("yyyy", Locale.getDefault());
    final private PersonAdapterFilter filter;
    final private Context context;
    private CharSequence lastConstraint = "";
    final private List<Person> objects;
    final private PersonDao personDao;

    public PersonAdapter(Context context, PersonDao personDao) {
        this.personDao = personDao;
        Query<Person> personQuery = personDao.queryBuilder().orderAsc(PersonDao.Properties.Surname).orderAsc(PersonDao.Properties.Prename).build();
        objects = personQuery.list();
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

        objects.addAll(personDao.queryBuilder().orderAsc(PersonDao.Properties.Surname).orderAsc(PersonDao.Properties.Prename).list());
        if (lastConstraint != null && lastConstraint.length() > 0)
            filter.filter(lastConstraint);
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        objects.clear();
        objects.addAll(personDao.queryBuilder().orderAsc(PersonDao.Properties.Surname).orderAsc(PersonDao.Properties.Prename).list());
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
            view.setTextAppearance(context, android.R.style.TextAppearance_Large);
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

    public class PersonAdapterFilter extends Filter {

        private final PersonDao personDao;
        private List<Long> excludePersonIds = new ArrayList<>();

        public PersonAdapterFilter(PersonDao personDao) {
            this.personDao = personDao;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Person> persons;
            QueryBuilder<Person> personQueryBuilder = personDao.queryBuilder().orderAsc(PersonDao.Properties.Surname).orderAsc(PersonDao.Properties.Prename);
            if(!excludePersonIds.isEmpty())            {
                        WhereCondition cond =new WhereCondition.
                                PropertyCondition(PersonDao.Properties.Id , " not in (" + excludedAsStringList() + ")");
//                                new WhereCondition.AbstractCondition(){
//                    @Override
//                    public void appendTo(StringBuilder builder, String tableAlias) {
//                        builder.append("_id not in (" + excludedAsStringList() + ")");
//                    }
//                };

                personQueryBuilder.where(cond);
            }

            persons = personQueryBuilder.list();

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

        private String excludedAsStringList() {
            StringBuilder bld = new StringBuilder();
            for(Long id : excludePersonIds) {
                if(bld.length()>0)
                    bld.append(",");
                bld.append(id);
            }
            return bld.toString();
        }

        public void clearExcludes() {
            excludePersonIds.clear();
        }

        public void addExcludePersonId(long personIdToExclude) {
            excludePersonIds.add(personIdToExclude);
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
