package de.kreth.clubhelper.daogenerator;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Index;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class ClubDaoGenerator {

    private static final int DATABASE_VERSION = 6;
    private Schema schema;
    private Entity person;
    private Entity contact;
    private Entity attendance;
    private Entity adress;
    private Entity relative;
    private Entity group;
    private Entity personGroup;
    private Entity synchronization;
    private Property personId;

    public void generate() throws Exception {

        schema = new Schema(DATABASE_VERSION, "de.kreth.clubhelper");
        schema.setDefaultJavaPackageTest("de.kreth.clubhelper.dao");
        schema.setDefaultJavaPackageDao("de.kreth.clubhelper.dao");
        schema.enableKeepSectionsByDefault();

        createPerson();
        createContact();
        createAttendance();
        createAdress();
        createRelatives();
        createGroup();
        createSynchronization();

        addGeneralProperties(person, true);
        addGeneralProperties(contact, true);
        addGeneralProperties(attendance, true);
        addGeneralProperties(adress, true);
        addGeneralProperties(relative, true);
        addGeneralProperties(group, true);
        addGeneralProperties(personGroup, true);

        DaoGenerator daoGenerator = new DaoGenerator();
        File f = new File(".");
        System.out.println("Current Dir:");
        System.out.println(f.getAbsolutePath());

        daoGenerator.generateAll(schema, "app/src/main/java");

        schema = new Schema(DATABASE_VERSION, "de.kreth.clubhelperbackend.pojo");
        schema.setDefaultJavaPackageDao("de.kreth.clubhelperbackend.pojo.dao");
        schema.enableKeepSectionsByDefault();

        createPerson();
        createContact();
        createAttendance();
        createAdress();
        createRelatives();
        createGroup();
        createSynchronization();

        addGeneralProperties(person, false);
        addGeneralProperties(contact, false);
        addGeneralProperties(attendance, false);
        addGeneralProperties(adress, false);
        addGeneralProperties(relative, false);
        addGeneralProperties(group, false);
        addGeneralProperties(personGroup, false);

        File backend = new File("../../workspace_ee/ClubHelperBackend/src/main/java");
        daoGenerator.generateAll(schema, backend.getAbsolutePath());
        backend = new File("../../workspace_ee/ClubHelperBackend/src/main/java/de/kreth/clubhelperbackend/pojo/");
        File[] daos = backend.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() && pathname.getName().matches("dao");
            }
        });
        if(daos.length>0) {
            for(File d: daos) {
                deleteDir(d);
            }
        }

        clearPojoFromGreenDaoCode(person);
        clearPojoFromGreenDaoCode(contact);
        clearPojoFromGreenDaoCode(attendance);
        clearPojoFromGreenDaoCode(adress);
        clearPojoFromGreenDaoCode(relative);
        clearPojoFromGreenDaoCode(group);
        clearPojoFromGreenDaoCode(personGroup);
    }

    private void createSynchronization() {
        synchronization = schema.addEntity("Synchronization");
        synchronization.addIdProperty();
        synchronization.addStringProperty("table_name");
        synchronization.addDateProperty("upload_successful");
        synchronization.addDateProperty("download_successful");
    }

    private void createGroup() {
        group = schema.addEntity("Group");
        final Property id = group.addIdProperty().columnName("_id").getProperty();
        group.addStringProperty("name").unique().notNull();

        personGroup = schema.addEntity("PersonGroup");
        personGroup.addIdProperty();
        final Property personId = personGroup.addLongProperty("personId").notNull().getProperty();
        final Property groupId = personGroup.addLongProperty("groupId").notNull().getProperty();

        person.addToMany(personGroup, personId);
        group.addToMany(personGroup, groupId);

        personGroup.addToMany(group, id);
        personGroup.addToMany(person, this.personId);

        Index unique = new Index();
        unique.makeUnique().setName("PersonGroupUnique");
        unique.addProperty(personId);
        unique.addProperty(groupId);
        personGroup.addIndex(unique);

    }

    private void clearPojoFromGreenDaoCode(Entity entity) {
        File f = new File("../../workspace_ee/ClubHelperBackend/src/main/java/de/kreth/clubhelperbackend/pojo/" + entity.getClassName() + ".java");
        File backup = new File(f.getAbsolutePath() + ".old");
        f.renameTo(backup);

        if(backup.exists()) {
            try {
                List<String> lines = new ArrayList<>();

                Charset charset = Charset.defaultCharset();
                for(String str : Files.readAllLines(backup.toPath(), charset)) {
                    if(!str.matches(".*([D|d]ao).*") && !str.matches(".*KEEP.*") && !str.matches(".*List.*"))
                        lines.add(str);
                }

                Files.write(f.toPath(), lines, charset, StandardOpenOption.CREATE_NEW);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean deleteDir(File dir) {
        boolean deleted = true;
        if(dir.isDirectory()) {
            for(File sub: dir.listFiles()) {
                deleted &= deleteDir(sub);
            }
        }

        return deleted && dir.delete();
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

    private void addGeneralProperties(Entity e, boolean clientSide) {
        e.addDateProperty("changed").notNull();
        e.addDateProperty("created").notNull();

        if(clientSide)
            e.addIntProperty("syncStatus").customType("de.kreth.clubhelper.SyncStatus", "de.kreth.clubhelper.SyncStatus.SyncStatusConverter");

        e.implementsInterface("Data");
        e.implementsSerializable();
    }

    public static void main(String[] args) throws Exception {
        new ClubDaoGenerator().generate();
    }
}
