package de.kreth.clubhelper.widgets;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import de.kreth.datecalc.DateDiff;
import de.kreth.datecalc.DateUnit;

/**
 * Created by markus on 21.12.14.
 */
public class PersonAdapter extends ArrayAdapter<Person> implements Filterable {

   final private DateFormat df = new SimpleDateFormat("yyyy");
   final private List<Person> objects;
   final private PersonAdapterFilter filter;
   private CharSequence lastConstraint = "";

   public PersonAdapter(Context context, List<Person> objects) {
      super(context, 0, objects);
      this.objects = new ArrayList<>(objects);
      filter = new PersonAdapterFilter(objects);
   }

   @Override
   public long getItemId(int position) {
      return getItem(position).getId();
   }

   @Override
   public boolean hasStableIds() {
      return true;
   }

   @Override
   public Person getItem(int position) {
      return objects.get(position);
   }

   @Override
   public void notifyDataSetChanged() {
      filter.filter(lastConstraint);
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent) {

      Person item = getItem(position);

      long age = DateDiff.calcDiff(item.getBirth(), new Date(), DateUnit.YEAR);
      String ageText = " (JG " + df.format(item.getBirth()) + ", Alter " + age + ")";
      TextView view;

      if (convertView == null) {
         view = new TextView(getContext());
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

      final private List<Person> persons;
      private boolean all = true;

      public PersonAdapterFilter(List<Person> persons) {
         this.persons = persons;
      }

      @Override
      protected FilterResults performFiltering(CharSequence constraint) {
         FilterResults results = new FilterResults();
         if (constraint == null || constraint.length() == 0) {
            all = true;
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
            all = false;
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
