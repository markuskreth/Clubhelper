package de.kreth.clubhelper.datahelper;

import java.util.List;

import de.kreth.clubhelper.Person;

/**
 * Created by markus on 08.02.15.
 */
public class PersonRelationHelper {
   public String relationsAsString(Person p) {

      StringBuilder txt = new StringBuilder(p.toString());
      List<Person.RelativeType> relations = p.getRelations();
      for (Person.RelativeType r : relations) {
         txt.append("\n");
         switch (r.getType()) {

            case MOTHER:
               txt.append("Mutter: ");
               break;
            case FATHER:
               txt.append("Vater: ");
               break;
            case CHILD:
               txt.append("Kind: ");
               break;
            case RELATIONSHIP:
               txt.append("Freund(-in): ");
               break;
         }
         txt.append(r.getRel().getId()).append(": ").append(r.getRel().getPrename()).append(
                 " ").append(r.getRel().getSurname());
      }
      return txt.toString();
   }
}
