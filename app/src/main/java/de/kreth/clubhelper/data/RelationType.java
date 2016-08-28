package de.kreth.clubhelper.data;

import android.content.res.Resources;

import de.kreth.clubhelper.R;

/**
 * Created by markus on 22.12.14.
 */
public enum RelationType {
    PARENT,
    CHILD,
    RELATIONSHIP,
    SIBLINGS;

    public String toString(Resources bundle) {

        switch (this) {
            case PARENT:
                return bundle.getString(R.string.PARENT);
            case CHILD:
                return bundle.getString(R.string.CHILD);
            case RELATIONSHIP:
                return bundle.getString(R.string.RELATIONSHIP);
            case SIBLINGS:
                return bundle.getString(R.string.SIBLINGS);
        }
        return name();
    }

}
