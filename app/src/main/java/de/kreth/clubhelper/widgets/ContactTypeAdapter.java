package de.kreth.clubhelper.widgets;

import android.support.v4.app.FragmentActivity;
import android.widget.ArrayAdapter;

/**
 * Created by markus on 19.03.15.
 */
public class ContactTypeAdapter extends ArrayAdapter<String> {

    public ContactTypeAdapter(FragmentActivity activity, String[] stringArray) {
        super(activity,  android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item, stringArray);
    }

}
