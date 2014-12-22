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
    private Entity adress;
    private Entity relative;
    private Property personId;

    public void generate() throws Exception {

        schema = new Schema(3, "de.kreth.clubhelper");
        schema.setDefaultJavaPackageTest("de.kreth.clubhelper.test");
        schema.setDefaultJavaPackageDao("de.kreth.clubhelper.dao");
        schema.enableKeepSectionsByDefault();

        createPerson();
        createContact();
        createAttendance();
        createAdress();
        createRelatives();

        DaoGenerator daoGenerator = new DaoGenerator();
        File f = new File(".");
        System.out.println("Current Dir:");
        System.out.println(f.getAbsolutePath());
        daoGenerator.generateAll(schema, "app/src/main/java", "app/src/androidTest/java");
    }

    private void createRelatives() {
        relative = schema.addEntity("Relative");
        relative.addIdProperty();

        Property person1 = relative.addLongProperty("person1").notNull().getProperty();
        Property person2 = relative.addLongProperty("person2").notNull().getProperty();

        Index unique = new Index();
        unique.makeUnique();
        unique.addProperty(person1);
        unique.addProperty(person2);
        relative.addIndex(unique);

        relative.addStringProperty("toPerson2Relation");
        relative.addStringProperty("toPerson1Relation");

    }

    private void createAdress() {
        adress = schema.addEntity("Adress");
        adress.addIdProperty().columnName("_id");
        adress.addStringProperty("adress1");
        adress.addStringProperty("adress2");
        adress.addStringProperty("plz");
        adress.addStringProperty("city");
        Property personIdAdress = adress.addLongProperty("personId").notNull().getProperty();

        person.addToMany(adress, personIdAdress);
    }

    private void createAttendance() {
        Index idxAtt = new Index();
        idxAtt.setName("idxAttendance");

        attendance = schema.addEntity("Attendance");
        attendance.addIdProperty().columnName("_id");
        idxAtt.addProperty(attendance.addDateProperty("onDate").getProperty());

        Property personIdAttend = attendance.addLongProperty("personId").notNull().getProperty();
        idxAtt.addProperty(personIdAttend);
        // contact.addToOne(attendance, personIdAttend);
        attendance.addIndex(idxAtt);

        person.addToMany(attendance, personIdAttend);
    }

    private void createContact() {

        contact = schema.addEntity("Contact");
        contact.addIdProperty().columnName("_id");
        contact.addStringProperty("type");
        contact.addStringProperty("value");
        Property personIdContact = contact.addLongProperty("personId").notNull().getProperty();
        person.addToMany(contact, personIdContact);
    }

    private void createPerson() {

        person = schema.addEntity("Person");
        person.implementsSerializable();
        personId = person.addIdProperty().columnName("_id").getProperty();

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
