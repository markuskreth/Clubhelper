package de.kreth.clubhelper.backup;

import java.util.ArrayList;
import java.util.List;

import de.kreth.clubhelper.data.Adress;
import de.kreth.clubhelper.data.Attendance;
import de.kreth.clubhelper.data.Contact;
import de.kreth.clubhelper.data.Group;
import de.kreth.clubhelper.data.Person;
import de.kreth.clubhelper.data.PersonGroup;
import de.kreth.clubhelper.data.Relative;
import de.kreth.clubhelper.data.Synchronization;

/**
 * Created by markus on 29.03.15.
 */
public class DataExportClass {
    private List<Person> persons;
    private List<Contact> contacts;
    private List<Relative> relatives;
    private List<Adress> adresses;
    private List<Attendance> attendances;
    private List<Group> groups;
    private List<PersonGroup> personGroups;
    private List<Synchronization> synchronizations;

    public DataExportClass() {
        this.persons = new ArrayList<>();
        this.contacts = new ArrayList<>();
        this.relatives = new ArrayList<>();
        this.adresses = new ArrayList<>();
        this.attendances = new ArrayList<>();
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

    public void setAdresss(List<Adress> adresses) {
        this.adresses = adresses;
    }

    public void setAttendances(List<Attendance> attendances) {
        this.attendances = attendances;
    }

    public List<Attendance> getAttendances() {
        return attendances;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setPersonGroups(List<PersonGroup> personGroups) {
        this.personGroups = personGroups;
    }

    public List<PersonGroup> getPersonGroups() {
        return personGroups;
    }

    public void setSynchronizations(List<Synchronization> synchronizations) {
        this.synchronizations = synchronizations;
    }

    public List<Synchronization> getSynchronizations() {
        return synchronizations;
    }
}
