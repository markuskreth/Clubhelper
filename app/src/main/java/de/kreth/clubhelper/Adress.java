package de.kreth.clubhelper;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table ADRESS.
 */
public class Adress {

    private Long id;
    private String adress1;
    private String adress2;
    private String plz;
    private String city;
    private long personId;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Adress() {
    }

    public Adress(Long id) {
        this.id = id;
    }

    public Adress(Long id, String adress1, String adress2, String plz, String city, long personId) {
        this.id = id;
        this.adress1 = adress1;
        this.adress2 = adress2;
        this.plz = plz;
        this.city = city;
        this.personId = personId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdress1() {
        return adress1;
    }

    public void setAdress1(String adress1) {
        this.adress1 = adress1;
    }

    public String getAdress2() {
        return adress2;
    }

    public void setAdress2(String adress2) {
        this.adress2 = adress2;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
        StringBuilder bld = new StringBuilder();
        if(adress1 != null)
            bld.append(adress1);
        if(adress2 != null && adress2.trim().length() >0) {
            if(bld.length()>0)
                bld.append("\n");
            bld.append(adress2);
        }

        if(plz != null && plz.length()>0 && city != null && city.length()>0) {

            if(bld.length()>0)
                bld.append("\n");
            bld.append(plz).append(" ").append(city);
        }

        return bld.toString();
    }
    // KEEP METHODS END

}
