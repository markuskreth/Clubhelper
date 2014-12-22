package de.kreth.clubhelper.widgets;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.kreth.clubhelper.Person;
import de.kreth.datecalc.DateDiff;
import de.kreth.datecalc.DateUnit;

/**
 * Created by markus on 21.12.14.
 */
public class PersonAdapter extends ArrayAdapter<Person> {

    private DateFormat df = new SimpleDateFormat("yyyy");
    public PersonAdapter(Context context,List<Person> objects) {
        super(context, 0, objects);
    }

    @Override
    public long getItemId(int position) {
        return super.getItem(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Person item = getItem(position);

        long age = DateDiff.calcDiff(item.getBirth(), new Date(), DateUnit.YEAR);
        String ageText = " (JG " + df.format(item.getBirth()) + ", Alter " + age + ")";
        TextView view;

        if(convertView == null) {
            view = new TextView(getContext());
            view.setTag(item.getId());
        } else
            view = (TextView) convertView;
        view.setText(item.getSurname() + ", " + item.getPrename() + ageText);
        return view;
    }
}
