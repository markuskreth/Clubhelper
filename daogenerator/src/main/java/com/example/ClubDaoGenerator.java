package com.example;

import java.io.File;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Index;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class ClubDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "de.kreth.clubhelper");
        schema.setDefaultJavaPackageTest("de.kreth.clubhelper.test");
        schema.setDefaultJavaPackageDao("de.kreth.clubhelper.dao");
        schema.enableKeepSectionsByDefault();

        Entity person = schema.addEntity("Person");
        person.addIdProperty();

        Index index = new Index();
        index.setName("idx_name");
        Property.PropertyBuilder property = person.addStringProperty("prename");
        index.addProperty(property.getProperty());
        property = person.addStringProperty("surname");
        index.addProperty(property.getProperty());
        person.addIndex(index);
        person.addStringProperty("type").index();
        person.addDateProperty("birth");

        person.implementsSerializable();

        Entity contact = schema.addEntity("Contact");
        contact.addIdProperty();
        contact.addStringProperty("type");
        contact.addStringProperty("value");
        Property personId = contact.addLongProperty("personId").getProperty();
        contact.addToMany(person, personId);

        Index idxAtt = new Index();
        idxAtt.setName("idxAttendance");

        Entity attendance = schema.addEntity("Attendance");
        idxAtt.addProperty(attendance.addDateProperty("onDate").getProperty());
        personId = attendance.addLongProperty("personId").getProperty();
        idxAtt.addProperty(personId);
        contact.addToMany(attendance, personId);

        DaoGenerator daoGenerator = new DaoGenerator();
        File f = new File(".");
        System.out.println("Current Dir:");
        System.out.println(f.getAbsolutePath());
        daoGenerator.generateAll(schema, "app/src/main/java", "app/src/androidTest/java");
    }
}
