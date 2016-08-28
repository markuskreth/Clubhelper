package de.kreth.clubhelper.daogenerator;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Index;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class ClubDaoGenerator {

    private static final int DATABASE_VERSION = 7;
    private Schema schema;
    private Entity person;
    private Entity contact;
    private Entity attendance;
    private Entity adress;
    private Entity relative;
    private Entity group;
    private Entity personGroup;
    private Entity synchronization;
    private Entity deletedEntries;
    private Property personId;

    public void generate() throws Exception {

        schema = new Schema(DATABASE_VERSION, "de.kreth.clubhelper.data");
        schema.setDefaultJavaPackageTest("de.kreth.clubhelper.data");
        schema.setDefaultJavaPackageDao("de.kreth.clubhelper.dao");
        schema.enableKeepSectionsByDefault();

        setupSchema(true);

        DaoGenerator daoGenerator = new DaoGenerator();
        File f = new File(".");
        System.out.println("Current Dir:");
        System.out.println(f.getAbsolutePath());

        daoGenerator.generateAll(schema, "app/src/main/java");

        File[] files = new File("app/src/main/java/de/kreth/clubhelper/data").listFiles(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return !f.isDirectory() && f.getName().endsWith(".java") && !f.getName().equals("SyncStatus.java") && !f.getName().equals("Synchronization.java");
            }
        });

        for (File toCopy: files) {
            File destination = new File("../../workspace_ee/ClubHelperBackend/src/main/java/de/kreth/clubhelperbackend/pojo/", toCopy.getName());
            Files.copy(toCopy.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

            cleanGeneratedFileFromDaoCode(destination);
        }

    }

    private void setupSchema(boolean clientSide) {

        createPerson();
        createContact();
        createAttendance();
        createAdress();
        createRelatives();
        createGroup();
        createSynchronization();
        createDeleted();

        addGeneralProperties(person, clientSide);
        addGeneralProperties(contact, clientSide);
        addGeneralProperties(attendance, clientSide);
        addGeneralProperties(adress, clientSide);
        addGeneralProperties(relative, clientSide);
        addGeneralProperties(group, clientSide);
        addGeneralProperties(personGroup, clientSide);
        addGeneralProperties(deletedEntries, clientSide);

    }

    private void createDeleted() {
        deletedEntries = schema.addEntity("DeletedEntries");
        deletedEntries.addIdProperty();
        deletedEntries.addStringProperty("tablename");
        deletedEntries.addLongProperty("entryId");

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

    private void cleanGeneratedFileFromDaoCode(File destination) {

        if(destination.exists()) {

            try {

                Charset charset = Charset.defaultCharset();

                Path toPath = destination.toPath();
                List<String> fileLines = Files.readAllLines(toPath, charset);

                destination.delete();

                List<String> lines = filterLines(fileLines);
                lines.remove(0);
                lines.add(0, "package de.kreth.clubhelperbackend.pojo;");

                Files.write(toPath, lines, charset, StandardOpenOption.CREATE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public List<String> filterLines(List<String> fileLines) {
        List<String> lines = new ArrayList<>();

        boolean lastLineEmpty = false;
        int levelCount = 0;

        for(String str : fileLines) {

            str = cleanConstructorOfSyncStatus(str);
            if(levelCount == 0
                    && !isDoubleEmptyLine(lastLineEmpty, str)
                    && isValidLine(str)) {
                lines.add(str);

                lastLineEmpty = str.length() == 0;
            } else {
                if(levelCount>0) {
                    levelCount += str.split("\\{").length - 1;
                    if(str.endsWith("{"))
                        levelCount++;
                    levelCount -= str.split("\\}").length - 1;
                    if(str.endsWith("}"))
                        levelCount--;
                } else {
                    if(isFunctionStart(str) && !isValidLine(str))
                        levelCount++;
                }
            }

        }

        return lines;
    }

    private String cleanConstructorOfSyncStatus(String str) {
        if(str.matches("\\s*(public|protected|private) [A-Z][\\w<>]+\\([\\w,\\. ]*\\)\\s*\\{\\s*")) {    // Is Constructor?
            if(str.contains("SyncStatus")) {
                str = str.replaceAll(",? *SyncStatus \\w+", "");
            }
        }
        return str;
    }

    private boolean isFunctionStart(String str) {
        return str.matches("\\s*(public|protected|private)\\s+[\\w<>]+ .*\\(.*\\)\\s*\\{\\s*")
                || str.matches("\\s*if.+type.+\\.\\w+\\(\"tele\"\\).+type.+\\.\\w+\\(\"mobile\"\\)\\s*\\{\\s*");        // In Contact alles mit PhoneNumberUtil entfernen.
    }

    private boolean isDoubleEmptyLine(boolean lastLineEmpty, String str) {
        return lastLineEmpty && str.length() == 0;
    }

    private boolean isValidLine(String str) {
        return !str.matches(".*([D|d]ao).*")
//                    && !str.matches(".*KEEP.*")
                && !str.matches(".*List.*")
                && !str.matches(".*android.*")
                && !str.matches(".*KEEP.+END.*")
                && !str.matches("\\s*public void (delete|update|refresh)\\(\\) \\{")
                && !str.matches("\\s*this\\.(delete|update|refresh)\\(\\);")
                && !str.matches("\\s*// KEEP.+custom.+here.*")
                && !str.matches("\\s*/\\*.*\\*/")
                && !str.matches(".*SyncStatus .*")
                && !str.matches(".*this.syncStatus = syncStatus;.*")
                && !str.matches("\\s*if.+type.+\\.\\w+\\(\"tele\"\\).+type.+\\.\\w+\\(\"mobile\"\\)\\s*\\{\\s*")
                && !str.matches("\\s*//.*GENERATED BY.*");
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
            e.addIntProperty("syncStatus").customType("de.kreth.clubhelper.data.SyncStatus", "de.kreth.clubhelper.data.SyncStatus.SyncStatusConverter");

        e.implementsInterface("Data");
//        e.implementsSerializable();
    }

    public static void main(String[] args) throws Exception {
        new ClubDaoGenerator().generate();
    }
}
