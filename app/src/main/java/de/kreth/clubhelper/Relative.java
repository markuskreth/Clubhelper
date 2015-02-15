package de.kreth.clubhelper;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

/**
 * Entity mapped to table RELATIVE.
 */
public class Relative {

   private Long id;
   private long person1;
   private long person2;
   private String toPerson2Relation;
   private String toPerson1Relation;

   // KEEP FIELDS - put your custom fields here
   // KEEP FIELDS END

   public Relative() {
   }

   public Relative(Long id) {
      this.id = id;
   }

   public Relative(Long id, long person1, long person2, String toPerson2Relation,
                   String toPerson1Relation) {
      this.id = id;
      this.person1 = person1;
      this.person2 = person2;
      this.toPerson2Relation = toPerson2Relation;
      this.toPerson1Relation = toPerson1Relation;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public long getPerson1() {
      return person1;
   }

   public void setPerson1(long person1) {
      this.person1 = person1;
   }

   public long getPerson2() {
      return person2;
   }

   public void setPerson2(long person2) {
      this.person2 = person2;
   }

   public String getToPerson2Relation() {
      return toPerson2Relation;
   }

   public void setToPerson2Relation(String toPerson2Relation) {
      this.toPerson2Relation = toPerson2Relation;
   }

   public String getToPerson1Relation() {
      return toPerson1Relation;
   }

   public void setToPerson1Relation(String toPerson1Relation) {
      this.toPerson1Relation = toPerson1Relation;
   }

   // KEEP METHODS - put your custom methods here
   // KEEP METHODS END

}
