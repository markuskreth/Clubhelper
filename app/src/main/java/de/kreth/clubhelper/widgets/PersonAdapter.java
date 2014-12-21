package de.kreth.clubhelper.widgets;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.kreth.clubhelper.Person;

/**
 * Created by markus on 21.12.14.
 */
public class PersonAdapter extends ArrayAdapter<Person> {

    public PersonAdapter(Context context,List<Person> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Person item = getItem(position);
        TextView view;

        if(convertView == null) {
            view = new TextView(getContext());
        } else
            view = (TextView) convertView;
        view.setText(item.getSurname() + ", " + item.getPrename());
        return view;
    }
}
