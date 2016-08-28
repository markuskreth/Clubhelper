package de.kreth.clubhelper.datahelper;

import android.content.res.Resources;

import java.util.List;

import de.kreth.clubhelper.data.Contact;
import de.kreth.clubhelper.data.Person;

/**
 * Created by markus on 08.02.15.
 */
public class PersonRelationHelper {
    Resources bundle;

    public PersonRelationHelper(Resources bundle) {
        this.bundle = bundle;
    }

    public String relationsAsString(Person p) {

        StringBuilder txt = new StringBuilder(p.toString());
        for (Contact contact : p.getContactList()) {

            txt.append("\n");
            txt.append(contact.toString());
        }
        ;
        List<Person.RelativeType> relations = p.getRelations();

        for (Person.RelativeType r : relations) {
            txt.append("\n");

            txt.append(r.getType().toString(bundle)).append(": ");
            txt.append(r.getRel().getId()).append(": ").append(r.getRel().getPrename()).append(
                    " ").append(r.getRel().getSurname());
        }
        return txt.toString();
    }
}
