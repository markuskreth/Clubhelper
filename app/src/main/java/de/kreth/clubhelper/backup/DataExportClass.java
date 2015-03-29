package de.kreth.clubhelper.backup;

import java.util.ArrayList;
import java.util.List;

import de.kreth.clubhelper.Adress;
import de.kreth.clubhelper.Contact;
import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.Relative;

/**
 * Created by markus on 29.03.15.
 */
public class DataExportClass {
    private List<Person> persons;
    private List<Contact> contacts;
    private List<Relative> relatives;
    private List<Adress> adresses;

    public DataExportClass() {
        this.persons = new ArrayList<>();
        this.contacts = new ArrayList<>();
        this.relatives = new ArrayList<>();
        this.adresses = new ArrayList<>();
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<Relative> getRelatives() {
        return relatives;
    }

    public void setRelatives(List<Relative> relatives) {
        this.relatives = relatives;
    }

    public List<Adress> getAdresses() {
        return adresses;
    }

    public void setAdresses(List<Adress> adresses) {
        this.adresses = adresses;
    }
}
