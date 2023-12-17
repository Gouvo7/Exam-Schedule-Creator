package thesis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** 
 * Η κλάση Professors υλοποιεί την κλάση Serializable και είναι υπεύθυνη για την αποθήκευση στοιχείων των καθηγητών
 * 
 * @author Nektarios Gkouvousis
 * @author ice18390193
 * 
 * @param profSurname Επώνυμο καθηγητή
 * @param profFirstname Όνομα καθηγητή
 * @param profField Ειδικότητα καθηγητή
 * @param availability Λίστα με αντικείμενα της κλάσης Availability
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
     * Η μέθοδος επιστρέφει την διαθεσιμότητα ενός καθηγητή για συγκεκριμένη
     * ημερομηνία και χρονική περίοδο
     * @param date Η ημερομηνία της διαθεσιμότητας προς έλεγχο
     * @param timeslot Η χρονική περίοδος της διαθεσιμότητας προς έλεγχο
     * @return 0 όταν ο καθηγητής δεν είναι διαθέσιμος 
     *         1 όταν ο καθηγητής είναι διαθέσιμος
     *         2 όταν ο καθηγητής είναι διαθέσιμος αλλά έχει δεσμευθεί από άλλο μάθημα για εκείνη την ημερομηνία
     */
    public int isAvailable(String date, String timeslot){
        date.trim();
        timeslot.trim();
        for (Availability a : availability){
            if (a.getDate().equals(date) && a.getTimeSlot().equals(timeslot)){
                if(a.getIsAvailable() == 0){
                    System.out.println("Ο καθηγητής: " + this.getProfSurname() + " δεν είναι διαθέσιμος για την ημερομηνία: " + a.getDate() + " + ώρα: " + a.getTimeSlot());
                    return 0;
                }else if (a.getIsAvailable() == 1){
                    System.out.println("Ο καθηγητής: " + this.getProfSurname() + " είναι διαθέσιμος για την ημερομηνία: " + a.getDate() + " + ώρα: " + a.getTimeSlot());
                    return 1;
                }else if (a.getIsAvailable() == 2){
                    System.out.println("Ο καθηγητής: " + this.getProfSurname() + " έχει άλλο μάθημα για την ημερομηνία: " + a.getDate() + " + ώρα: " + a.getTimeSlot());
                    return 2;
                }
            }
        }
        return -1;
    }
    
    /**
     * Η μέθοδος αλλάζει την διαθεσιμότητα ενός καθηγητή για συγκεκριμένη
     * ημερομηνία και χρονική περίοδο
     * @param date Η ημερομηνία της διαθεσιμότητας προς αλλαγή
     * @param timeslot Η χρονική περίοδος της διαθεσιμότητας προς αλλαγή
     * @param res Η νέα τιμή διαθεσιμότητας που θα οριστεί (0,1,2)
     */
    public void changeSpecificAvailability(String date, String timeslot, int res){
        for (Availability a : availability){
            if (a.getDate().equals(date) && a.getTimeSlot().equals(timeslot)){
                a.setIsAvailable(res);
            }
        }
    }

    /**
     * Η μέθοδος εκτυπώνει ολόκληρο το φύλλο διαθεσιμότητας του καθηγητή
     */
    public void printProfessorAvailability(){
        System.out.println("Διαθεσιμότητα καθηγητή " + this.getProfSurname() + " " + this.getProfFirstname() + ":");
        String tmp = null;
        for (Availability x : availability){
            if (x.getIsAvailable() == 1){
                tmp = "Ναι";
            }else{
                tmp = "Όχι";
            }
            System.out.println(x.getDate() + " " + x.getTimeSlot() + " - " + tmp);
            tmp = "";
        }
    }

    /**
     * Η μέθοδος ελέγχει την ισότητα μεταξύ δύο αντικειμένων καθηγητών
     * @param obj Αντικείμενο
     * @return true ή false αντίστοιχα με το εάν είναι το ίδιο αντικείμενο καθηγητή ή όχι
     */
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
}
