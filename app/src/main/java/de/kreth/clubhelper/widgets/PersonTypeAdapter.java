package de.kreth.clubhelper.widgets;

import android.content.res.Resources;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.kreth.clubhelper.R;

/**
 * Created by markus on 03.04.15.
 */
public class PersonTypeAdapter implements SpinnerAdapter {

    private List<PersonType> values;

    public PersonTypeAdapter(Resources resources) {
        values = new ArrayList<>();
        values.add(new PersonType(de.kreth.clubhelper.data.PersonType.ACTIVE, resources.getString(R.string.ACTIVE)));
        values.add(new PersonType(de.kreth.clubhelper.data.PersonType.STAFF, resources.getString(R.string.STAFF)));
        values.add(new PersonType(de.kreth.clubhelper.data.PersonType.RELATIVE, resources.getString(R.string.RELATIVE)));
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return values.size();
    }

    public int getPosition(de.kreth.clubhelper.data.PersonType type) {
        for (int i = 0; i < values.size(); i++) {
            if(values.get(i).getType() == type)
                return i;
        }
        return -1;
    }

    @Override
    public de.kreth.clubhelper.data.PersonType getItem(int position) {
        return values.get(position).getType();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view;
        if(convertView != null)
            view = (TextView) convertView;
        else
            view = new TextView(parent.getContext());
        view.setText(values.get(position).getName());
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    private class PersonType {
        private de.kreth.clubhelper.data.PersonType type;
        private String name;

        public PersonType(de.kreth.clubhelper.data.PersonType type, String name) {
            this.type = type;
            this.name = name;
        }

        public de.kreth.clubhelper.data.PersonType getType() {
            return type;
        }

        public String getName() {
            return name;
        }
    }
}
