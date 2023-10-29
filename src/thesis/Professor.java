package thesis;

/**
 * Η κλάση Professors χρησιμοποιείται για την αποθήκευση στοιχείων των καθηγητών.
 * @param profSurname -> Επώνυμο καθηγητή
 * @param profFirstname -> Όνομα καθηγητή
 * @param profField -> Ειδικότητα καθηγητή
 * @author gouvo
 */
public class Professor {
    
    private String profSurname;
    private String profFirstname;
    private String profField;
    
    Professor(String a, String b, String c){
        profSurname = a;
        profFirstname = b;
        profField = c;
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
    
    /**
     * Η μέθοδος εκτυπώνει όλα τα στοιχεία ενός καθηγητή.
     */
    public void printText(){
        System.out.println(profSurname + " " + profFirstname + " " + profField);
    }
    
    
}
