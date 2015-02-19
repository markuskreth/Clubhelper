package de.kreth.clubhelper;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here

// KEEP INCLUDES END

/**
 * Entity mapped to table CONTACT.
 */
public class Contact {

    private Long id;
    private String type;
    private String value;
    private long personId;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Contact() {
    }

    public Contact(Long id) {
        this.id = id;
    }

    public Contact(Long id, String type, String value, long personId) {
        this.id = id;
        this.type = type;
        this.value = value;
        this.personId = personId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    // KEEP METHODS - put your custom methods here

    @Override
    public String toString() {
        return type + "=" + value;
    }

    // KEEP METHODS END

}
