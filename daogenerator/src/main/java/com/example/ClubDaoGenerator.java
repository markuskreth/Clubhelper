package com.example;

import java.io.File;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Index;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class ClubDaoGenerator {

    private Schema schema;
    private Entity person;
    private Entity contact;
    private Entity attendance;
    private Property personIdAttend;
    private Property personIdContact;

    public void generate() throws Exception {

        schema = new Schema(1, "de.kreth.clubhelper");
        schema.setDefaultJavaPackageTest("de.kreth.clubhelper.test");
        schema.setDefaultJavaPackageDao("de.kreth.clubhelper.dao");
        schema.enableKeepSectionsByDefault();

        createPerson();
        createContact();
        createAttendance();
        connectPersonWithContactAndAttendance();

        DaoGenerator daoGenerator = new DaoGenerator();
        File f = new File(".");
        System.out.println("Current Dir:");
        System.out.println(f.getAbsolutePath());
        daoGenerator.generateAll(schema, "app/src/main/java", "app/src/androidTest/java");
    }

    private void connectPersonWithContactAndAttendance() {
        person.addToMany(contact, personIdContact);
        person.addToMany(attendance, personIdAttend);
    }

    private void createAttendance() {
        Index idxAtt = new Index();
        idxAtt.setName("idxAttendance");

        attendance = schema.addEntity("Attendance");
        attendance.addIdProperty().columnName("_id");
        idxAtt.addProperty(attendance.addDateProperty("onDate").getProperty());

        personIdAttend = attendance.addLongProperty("personId").notNull().getProperty();
        idxAtt.addProperty(personIdAttend);
        // contact.addToOne(attendance, personIdAttend);
        attendance.addIndex(idxAtt);
    }

    private void createContact() {

        contact = schema.addEntity("Contact");
        contact.addIdProperty().columnName("_id");
        contact.addStringProperty("type");
        contact.addStringProperty("value");
        personIdContact = contact.addLongProperty("personId").notNull().getProperty();
        // contact.addToOne(person, personIdContact);
    }

    private void createPerson() {

        person = schema.addEntity("Person");
        person.implementsSerializable();
        person.addIdProperty().columnName("_id");

        Index index = new Index();
        index.setName("idx_name");

        Property.PropertyBuilder property = person.addStringProperty("prename");
        index.addProperty(property.getProperty());

        property = person.addStringProperty("surname");
        index.addProperty(property.getProperty());

        person.addIndex(index);
        person.addStringProperty("type").index();
        person.addDateProperty("birth");

    }

    public static void main(String[] args) throws Exception {
        new ClubDaoGenerator().generate();
    }
}
