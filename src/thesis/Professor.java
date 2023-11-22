package thesis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/** @author gouvo
 * Η κλάση Professors χρησιμοποιείται για την αποθήκευση στοιχείων των καθηγητών.
 * @param profSurname Επώνυμο καθηγητή.
 * @param profFirstname Όνομα καθηγητή.
 * @param profField Ειδικότητα καθηγητή.
 * 
 */
public class Professor implements Serializable{
    
    private String profSurname;
    private String profFirstname;
    private String profField;
    private List<Availability> availability;
    
    Professor(String a, String b, String c){
        profSurname = a;
        profFirstname = b;
        profField = c;
        availability = new ArrayList<>();
    }
    
    public void setProfSurname(String x){
        this.profSurname = x;
    }
    public void setProfFirstname(String x){
        this.profFirstname = x;
    }
    public void setProfField(String x){
        this.profField = x;
    }
    
    public String getProfSurname(){
        return this.profSurname;
    }
    public String getProfFirstname(){
        return this.profFirstname;
    }
    public String getProfField(){
        return this.profField;
    }
    
    public void setAvailability(List<Availability> availability) {
        this.availability = availability;
    }
    
    /**
     * Η μέθοδος εκτυπώνει όλα τα στοιχεία ενός καθηγητή.
     */
    public void printText(){
        System.out.println(profSurname + " " + profFirstname + " " + profField);
    }
    
    public void prinAvailable(){
        for (Availability x : availability){
            System.out.println(this.getProfSurname() + " " + x.getDate() +  " " + x.getTimeSlot() + " " + x.getIsAvailable());
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Professor professor = (Professor) obj;
        return Objects.equals(profSurname, professor.profSurname) &&
               Objects.equals(profFirstname, professor.profFirstname) &&
               Objects.equals(profField, professor.profField);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profSurname, profFirstname, profField);
    }
}
