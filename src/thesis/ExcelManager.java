package thesis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gmele.general.sheets.XlsxSheet;
import org.gmele.general.sheets.exception.SheetExc;

/**
 *
 * @author gouvo
 * 
 * � ����� ����� �������� ��� ��� ���������� ��� ���������� ������������ ���� ��� ���
 * ��������/���������� ������� .xlsx
 * @param - myJFrame - �� �������� ��� �� �����.
 * @param - fileName - �� ����� ��� ������� ���� ��������.
 */

public class ExcelManager {
    
    
    private static String fileName;
    private static String sheet1, sheet2,sheet3, sheet4, sheet5, sheet6;
    private static JFrame myJFrame;
    private static Logs logger;
    private SavedPaths paths;

    private List<Professor> profs;
    private List<Course> courses;
    private List<Classroom> classrooms;
    private List<String> timeslots;
    private List<String> dates;
    
    public List<Professor> getProfs() {
        return profs;
    }

    public void setProfs(List<Professor> profs) {
        this.profs = profs;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<Classroom> getClassrooms() {
        return classrooms;
    }

    public void setClassrooms(List<Classroom> classrooms) {
        this.classrooms = classrooms;
    }

    public List<String> getTimeslots() {
        return timeslots;
    }

    public void setTimeslots(List<String> timeslots) {
        this.timeslots = timeslots;
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }
    
    // ������ ��� �������� ��������� ��� ������ ��� excel ���� �����������.
    
    ExcelManager(JFrame x, String y){
        myJFrame = x;
        fileName = y;
        logger = new Logs();
        paths = new SavedPaths();
        fileName = paths.getImportFilePath();
        sheet1 = paths.getSheet1();
        sheet2 = paths.getSheet2();
        sheet3 = paths.getSheet3();
        sheet4 = paths.getSheet4();
        sheet5 = paths.getSheet5();
        sheet6 = paths.getSheet6();
    }
    
    /**
     * 1) �������� �� ������ ��� �� �������� ��� ��������� � ������� ��� ��� ��������.
     * 2) ���������� �� �������� �� ����������� ���������� �����
     * 3) ������� ��� ��� ������ ���� �� 1� ������������� ��� ���� ��������� ��� 
     * ����� ��� ������������� ���� ��� ��� ����������� ��� �����������. ��� ���� 
     * �������� ��������� ��� �����, �� ������� ��� ��� ����������� ��� �� ������� 
     * ���������� ������ ��� ���� ����������� ��� �������������. �� 2� ������
     * ������������� ��� ��� �������� �������� ��� ������������ ����������� ���
     * ����� ��� ������������� ��� �������� ��� ��� ����������� ��� �����������.
     * 
     * �� ��������� ��� ������ ��� �� ����� ��� ����� ������������ �� ������� �����������
     * � ����, �� ��������� ��� Exception ��� � ���������� �� ��������.
     */
    public void createExcels(){
        try {
            boolean excel1, excel2, excel3, excel4, excel5 = false;
            
            profs = new ArrayList<>();
            profs = readProfs(fileName);
            excel1 = true;
            if (profs == null) {
                throw new Exception();
            }

            timeslots = new ArrayList<>();
            timeslots = readTimeslots(fileName);
            if (timeslots == null) {
                throw new Exception();
            }
            excel2 = true;

            dates = new ArrayList<>();
            dates = readDates(fileName);
            if (dates == null) {
                throw new Exception();
            }
            excel3 = true;
            Collections.sort(dates, new DateComparator());
            
            classrooms = new ArrayList<>();
            classrooms = readClassrooms(fileName);
            if (classrooms == null) {
                throw new Exception();
            }
            excel4 = true;

            courses = new ArrayList<>();
            courses = readCourses(fileName, profs);
            if (courses == null) {
                throw new Exception();
            }
            excel5 = true;
            addProfsToCourses(profs, courses, fileName);
            
            if (excel1 && excel2 && excel3 && excel4 && excel5) {
                createTemplate(profs, timeslots, dates, classrooms);
            }
            saveObjects();

        } catch (Exception e) {
            return;
        }
    }

    /**
     * � ������� �������� �� ����� excel ��� ���������� ����������� ��� ���� ���������,
     * �� ��������, ��� ������� ���� �.�.������, ����� 2 �������� ��� ������������
     * ��� ����������� ��������� ��� �������� ��� ������������� ���� �� ���� �� ������������
     * template.
     * 
     * @return 
     * @throws org.gmele.general.sheets.exception.SheetExc
     */
    public boolean readTemplates() throws SheetExc{
        try {
            boolean outcome = readObjects();
            addProfessorsAvailability(profs, timeslots.size(), paths.getImportFilePath1());
            addClassroomsAvailability(classrooms, timeslots.size(), paths.getImportFilePath2());
            System.out.println("Fox");
            return true;
        }catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(myJFrame, "������ ���� ��� �������� ��� ���������. ",
            " ������ ������", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    /**
     * ����������� ��� ��� ��� ����� ��� ���������, ��� ������������� ���� ��� ��
     * ������������ ����� ��� ��������� ��� �� ��������� �� ����������� ����. 
     * @param professors � ����� �� ���� ��������� ��� �� ��������� � ������������� ����.
     * @param lastColumn �� ������� ��� ������ timeslots � �� ������ ��� ������������
     * �������� ��������.
     * @param filename �� ����� ��� ������� ��� �� ����� �� ���������� ��� ����������.
     */    
    public void addProfessorsAvailability(List<Professor> professors,int lastColumn, String filename) throws SheetExc{
        try{
            FileInputStream file = new FileInputStream(new File(filename));
            //XSSFWorkbook workbook = new XSSFWorkbook(f);
            XlsxSheet s = new XlsxSheet(filename);
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEEE dd/MM/yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (Professor professor : professors){
                String sheetName = professor.getProfSurname() + " " + professor.getProfFirstname();
                s.SelectSheet(sheetName);
                int lastRow = s.GetLastRow();
                List<Availability> availabilityList = new ArrayList<>();
                for (int rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                    String cellDate = s.GetCellString(rowIndex, 0);
                    Date date = inputFormat.parse(cellDate);
                    cellDate = outputFormat.format(date);
                    for (int colIndex = 1; colIndex < lastColumn; colIndex++){
                        String timeslot = s.GetCellString(0,colIndex);
                        String curCell = s.GetCellString(rowIndex, colIndex).trim();
                        if (curCell.equals("+")){
                            Availability tmp = new Availability(cellDate, timeslot, 1);
                            availabilityList.add(tmp);
                        }else if (curCell.equals("-")){
                            Availability tmp = new Availability(cellDate, timeslot, 0);
                            availabilityList.add(tmp);
                        }else{
                            throw new Exception();
                        }
                    }
                }

                if (!availabilityList.isEmpty()){
                    professor.setAvailability(availabilityList);
                }
            }
            file.close();
            return ;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "�� ������ '" + filename + "' ��� �������.",
               "������ ������", JOptionPane.ERROR_MESSAGE);
            return ;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(myJFrame, "�� ������ '" + filename + "' ��� �������.",
               "������ ������", JOptionPane.ERROR_MESSAGE);
        } catch (SheetExc ex) {
            JOptionPane.showMessageDialog(myJFrame, "�������� �� �� ������������ ������ �������������� ��������.",
               "������ ������", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex){
            JOptionPane.showMessageDialog(myJFrame, "�������� �� �� ������������  ������ �������������� ���������.",
               "������ ������", JOptionPane.ERROR_MESSAGE);
        }
        
        return ;
    }
    
    /**
     * ����������� ��� ��� ��� ����� ��� ��������, ��� ������������� ���� ��� ��
     * ������������ ����� ��� ��������� ��� �� ��������� �� ����������� ����. 
     * @param classrooms � ����� �� ��� �������� ��� �� ��������� � ������������� ����.
     * @param lastColumn �� ������� ��� ������ timeslots � �� ������ ��� ������������
     * �������� ��������.
     * @param filename �� ����� ��� ������� ��� �� ����� �� ���������� ��� ����������.
     */
    public void addClassroomsAvailability(List<Classroom> classrooms,int lastColumn, String filename){
        try{
            FileInputStream file = new FileInputStream(new File(filename));
            //XSSFWorkbook workbook = new XSSFWorkbook(f);
            XlsxSheet s = new XlsxSheet(filename);
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEEE dd/MM/yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (Classroom classroom : classrooms){
                String sheetName = classroom.getClassroomName();
                s.SelectSheet(sheetName);
                int lastRow = s.GetLastRow();
                List<Availability> availabilityList = new ArrayList<>();
                for (int rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                    String cellDate = s.GetCellString(rowIndex, 0);
                    Date date = inputFormat.parse(cellDate);
                    cellDate = outputFormat.format(date);
                    for (int colIndex = 1; colIndex < lastColumn; colIndex++){
                        String timeslot = s.GetCellString(0,colIndex);
                        String curCell = s.GetCellString(rowIndex, colIndex).trim();
                        if (curCell.equals("+")){
                            Availability tmp = new Availability(cellDate, timeslot, 1);
                            availabilityList.add(tmp);
                        }else if (curCell.equals("-")){
                            Availability tmp = new Availability(cellDate, timeslot, 0);
                            availabilityList.add(tmp);
                        }else{
                            throw new Exception();
                        }
                    }
                }
                if (!availabilityList.isEmpty()){
                    classroom.setAvailability(availabilityList);
                }
                //classroom.prinAvailable();
            }
            file.close();
            return ;
            
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "�� ������ '" + filename + "' ��� �������.",
               "������ ������", JOptionPane.ERROR_MESSAGE);
            return ;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(myJFrame, "�� ������ '" + filename + "' ��� �������.",
               "������ ������", JOptionPane.ERROR_MESSAGE);
        } catch (SheetExc ex) {
            JOptionPane.showMessageDialog(myJFrame, "�������� �� �� ������������ ������ �������������� ��������.",
               "������ ������", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex){
            JOptionPane.showMessageDialog(myJFrame, "�������� �� �� ������������ ������ �������������� ��������.",
               "������ ������", JOptionPane.ERROR_MESSAGE);
        }
        return ;
    }
    
    /**
     * ��������� ��� ������ ��� ��������� ��� ��������� �� ������ �� �������� ��� �����
     * ������������ Course.
     * @param profs � ����� �� ���� ���������.
     * @param courses � ����� �� �� ��������.
     * @param filename �� ����� ��� ������� ��� �� ����� �� ���������� ��� ����������.
     * @throws SheetExc 
     */
    public void addProfsToCourses(List<Professor> profs, List<Course> courses, String filename) throws SheetExc{
        try{
            FileInputStream file = new FileInputStream(new File(filename));
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet6);
            int rowIndex = 0;
            int lastRow = s.GetLastRow();
            List<String> uniqueCourses = new ArrayList<>();
            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    String course = s.GetCellString(rowIndex, 0).trim();
                    if (uniqueCourses.contains(course)){
                        JOptionPane.showMessageDialog(myJFrame, "������� �� ���� ������"
                                + " �������� ��� ��� ���� ��� ����� " + sheet6 + ". �������� ������� �� ��������"
                                + "  ��� ��������� ����.","������ ������", JOptionPane.ERROR_MESSAGE);
                        return ;
                    }else{
                        uniqueCourses.add(course);
                    }
                }
                rowIndex++;
            }
            rowIndex = 0;
            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    String course = s.GetCellString(rowIndex, 0).trim();
                    String profA = s.GetCellString(rowIndex, 1).trim();
                    String profB = s.GetCellString(rowIndex, 2).trim();
                    String profC = s.GetCellString(rowIndex, 3).trim();
                    String profD = s.GetCellString(rowIndex, 4).trim();
                    boolean exists = false;
                    for (Course tmpCourse : courses){
                        if (tmpCourse.getCourseName().equals(course)){
                            exists = true;
                            if (checkIfValid(course)){
                                for (Professor prof : profs){
                                    if (prof.getProfSurname().equals(profA) || prof.getProfSurname().equals(profB) ||
                                        prof.getProfSurname().equals(profC) || prof.getProfSurname().equals(profD) ){
                                        if (!tmpCourse.getExaminers().contains(prof)){
                                            //tmpCourse.getExaminers().add(prof);
                                            tmpCourse.addExaminer(prof);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!exists){
                        throw new Exception();
                    }
                }
                rowIndex++;
            }
            file.close();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "�� ������ '" + filename + "' ��� �������.",
               "������ ������", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex){
            JOptionPane.showMessageDialog(myJFrame, "������ ���� �� �������"
                    + " ��� ������� '" + filename + "'.","������ ������", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex){
            JOptionPane.showMessageDialog(myJFrame, "������� ������ ��� ����� '" + sheet6 + "' ��� ��� �������"
                                + " ��� ����� ��� ���������.","������ ������", JOptionPane.ERROR_MESSAGE);
        }
        return ;
    }
    
    /**
     * ���������� ���� ��� ��������� ��� ����� ����������� ��� ������ ������.
     * @param filename �� ����� ��� ������� ��� �� ����� �� ���������� ��� ����������.
     * @param profs � ����� �� ���� ���������.
     * @return ��� ����� �� ����������� ����� Course.
     * @throws SheetExc 
     */
    public List<Course> readCourses(String filename, List<Professor> profs) throws SheetExc{
        try{
            FileInputStream file = new FileInputStream(new File(filename));
            //XSSFWorkbook workbook = new XSSFWorkbook(f);
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet5);
            int rowIndex = 0;
            int lastRow = s.GetLastRow();
            List<Course> courses = new ArrayList<>();
            
            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    String cellA = s.GetCellString(rowIndex, 0).trim();
                    String cellB = s.GetCellString(rowIndex, 1).trim();
                    String cellC = s.GetCellString(rowIndex, 2).trim();
                    String cellD = s.GetCellString(rowIndex, 3).trim();
                    
                    if (checkIfValid(cellA) && checkIfValid(cellB) && checkIfValid(cellC) && checkIfValid(cellD) ){
                        boolean dupliicate = false;
                        if (courses.isEmpty()){
                            dupliicate = false;
                        }else{
                            for (Course tmp : courses){
                                if (checkDuplicateCourse(tmp,cellA)){
                                    dupliicate = true;
                                    break;
                                }
                            }
                        }
                        if (!dupliicate){
                            if (cellD.equals("+")){
                                Course tmp = new Course(cellA,cellB,cellC,true);
                                courses.add(tmp);
                            }else{
                                Course tmp = new Course(cellA,cellB,cellC,false);
                                courses.add(tmp);
                            }
                        }
                    }
                    else{
                        throw new Exception();
                    }
                }
                rowIndex++;
            }
            file.close();
            return courses;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "�� ������ '" + filename + "' ��� �������.",
               "������ ������", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex){
            JOptionPane.showMessageDialog(myJFrame, "������ ���� �� �������"
                    + " ��� ������� '" + filename + "'.","������ ������", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex){
            JOptionPane.showMessageDialog(myJFrame, "�������� �� �� �������� ��� ������: '"
            + sheet5 + "'. ����������� ��� ��� �������� ���� ����� � ���������� ��������.",
            "������ ������", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    /**
     * ���������� ���� ��� �������� ��� ����� ����������� ��� ������ ������.
     * @param filename �� ����� ��� ������� ��� �� ����� �� ���������� ��� ����������.
     * @return ��� ����� �� ����������� ����� Classroom.
     * @throws SheetExc 
     */
    public List<Classroom> readClassrooms(String filename) throws SheetExc{
        try{
            FileInputStream file = new FileInputStream(new File(filename));
            //XSSFWorkbook workbook = new XSSFWorkbook(f);
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet4);
            int rowIndex = 0;
            int lastRow = s.GetLastRow();
            List<Classroom> classrooms = new ArrayList<>();
            
            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    String cellA = s.GetCellString(rowIndex, 0);
                    String cellB = s.GetCellString(rowIndex, 1);
                    int cellC = (int) s.GetCellNumeric(rowIndex, 2);
                    String cellD = s.GetCellString(rowIndex, 3);
                    cellA = cellA.trim();
                    cellB = cellB.trim();
                    cellD = cellD.trim();
                    if (cellA != null && cellB != null && cellC != 0 && cellD != null){
                        Classroom tmp = new Classroom(cellA, cellB, cellC, cellD);
                        classrooms.add(tmp);
                    }
                    else{
                        throw new Exception();
                    }
                }
                rowIndex++;
            }
            file.close();
            return classrooms;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "�� ������ '" + filename + "' ��� �������.",
               "������ ������", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex){
            JOptionPane.showMessageDialog(myJFrame, "������ ���� �� �������"
                    + " ��� ������� '" + filename + "'.","������ ������", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex){
            JOptionPane.showMessageDialog(myJFrame, "�������� �� �� �������� ��� ������: '"
            + sheet4 + "'. ����������� ��� ��� �������� ���� ����� � ���������� ��������.",
            "������ ������", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    /**
     * ���������� ���� ��� ����������� ��� ����������� ��� ����� ����������� ��� ������ ������.
     * @param filename �� ����� ��� ������� ��� �� ����� �� ���������� ��� ����������.
     * @return ��� HashMap �� �������� ������ - �����������.
     * @throws SheetExc 
     */
    public List<String> readDates(String filename) throws SheetExc{
        try {
            FileInputStream file = new FileInputStream(new File(filename));
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet3);
            List<String> dates = new ArrayList<>();
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            
            int rowIndex = 0;
            int lastRow = s.GetLastRow();
            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    Date date;
                    try {
                        date = inputDateFormat.parse(s.GetCellDate(rowIndex, 0).toString());
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(myJFrame, "����� ����� "
                                + "��������� ��� ����� '" + sheet3 + "'.","������ ������", JOptionPane.ERROR_MESSAGE);
                        file.close();
                        return null;
                    }
                    String cellA = outputDateFormat.format(date).trim();
                    if (checkIfValid(cellA) && !dates.contains(cellA)){
                        dates.add(cellA);
                    }else{
                        throw new Exception();
                    }
                }
                rowIndex++;
            }
            Collections.sort(dates);
            file.close();
            return dates;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "�� ������ '" + filename + "' ��� �������.",
               "������ ������", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex){
            JOptionPane.showMessageDialog(myJFrame, "������ ���� �� �������"
                    + " ��� ������� '" + filename + "'.","������ ������", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex){
            JOptionPane.showMessageDialog(myJFrame, "�������� �� �� �������� ��� ������: '"
            + sheet3 + "'. ����������� ��� ��� �������� ���� ����� � ���������� ��������.",
            "������ ������", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    /**
     * ���������� ���� ��� ����������� �������� ��� ����� ����������� ��� ������ ������.
     * @param filename �� ����� ��� ������� ��� �� ����� �� ���������� ��� ����������.
     * @return ��� ����� �� Strings.
     * @throws SheetExc 
     */
    public List<String> readTimeslots(String filename) throws SheetExc{
        try {
            FileInputStream file = new FileInputStream(new File(filename));
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet2);
            //Iterate through each rows one by one
            List<String> timeslots = new ArrayList<>();
            int rowIndex = 0;
            int lastRow = s.GetLastRow();
            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    String cellA = s.GetCellString(rowIndex, 0);
                    if(checkIfValid(cellA) && !timeslots.contains(cellA)){
                        timeslots.add(cellA);
                    }else{
                        throw new Exception();
                    }
                }
                rowIndex++;
            }
            file.close();
            return timeslots;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "�� ������ '" +filename + "' ��� �������.",
               "������ ������", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex){
            JOptionPane.showMessageDialog(myJFrame, "������ ���� �� �������"
                    + " ��� ������� '" + filename + "'.","������ ������", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e){
            JOptionPane.showMessageDialog(myJFrame, "�������� �� �� �������� ��� ������: '"
            + sheet3 + "'. ����������� ��� ��� �������� ���� ����� � ���������� ��������.",
            "������ ������", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    /**
     * ���������� ���� ��� ��������� ��� ����� ����������� ��� ������ ������.
     * @param filename �� ����� ��� ������� ��� �� ����� �� ���������� ��� ����������.
     * @return ��� ����� �� ����������� ����� Professor.
     * @throws SheetExc 
     */
    public List<Professor> readProfs(String filename) throws SheetExc{
        try{
            FileInputStream file = new FileInputStream(new File(filename));
            //XSSFWorkbook workbook = new XSSFWorkbook(f);
            XlsxSheet s = new XlsxSheet(filename);
            s.SelectSheet(sheet1);
            int rowIndex = 0;
            int lastRow = s.GetLastRow();
            List<Professor> profs = new ArrayList<>();

            while (rowIndex <= lastRow){
                if (rowIndex != 0){
                    String cellA = s.GetCellString(rowIndex, 0).trim();
                    String cellB = s.GetCellString(rowIndex, 1).trim();
                    String cellC = s.GetCellString(rowIndex, 2).trim();
                    if (cellA != null && cellB != null && cellC != null){
                        boolean duplicate = false;
                        if (profs.isEmpty()){
                            duplicate = false;
                        }else{
                            for (Professor tmp : profs){
                                if (checkDuplicateProfessor(tmp,cellA,cellB,cellC)){
                                    duplicate = true;
                                    break;
                                }
                            }
                        }
                        if (!duplicate){
                            Professor prof = new Professor(cellA, cellB, cellC);
                            profs.add(prof);
                        }else{
                            throw new Exception();
                        }
                    }
                    else{
                        throw new Exception();
                    }
                }
                rowIndex++;
            }
            file.close();
            
            return profs;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "�� ������ '" + filename + "' ��� �������.",
               "������ ������", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex){
            JOptionPane.showMessageDialog(myJFrame, "������ ���� �� �������"
                    + " ��� ������� '" + filename + "'.","������ ������", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e){
            JOptionPane.showMessageDialog(myJFrame, "�������� �� �� �������� ��� ������: '"
            + sheet1 + "'. ����������� ��� ��� �������� ���� ����� � ���������� ��������.",
            "������ ������", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    /**
     * ������� ��� ������������ ��������.
     * @param prof ����������� ��������.
     * @param cellA ���������� ��� 1� �����.
     * @param cellB ���������� ��� 2� �����.
     * @param cellC ���������� ��� 3� �����.
     * @return ��� ������� ��� � ���.
     */
    public boolean checkDuplicateProfessor(Professor prof, String cellA, String cellB, String cellC){
        if(prof.getProfSurname().equals(cellA) && prof.getProfFirstname().equals(cellB) && prof.getProfField().equals(cellC) ){
            return true;
        }
        return false;
    }
    
    /**
     * ������� ��� ������������ ���������.
     * @param course ����������� ��������.
     * @param cellA ���������� ��� 1� �����.
     * @return ��� ������� ��� � ���.
     */
    public boolean checkDuplicateCourse(Course course, String cellA){
        if(course.getCourseName().equals(cellA)){
            return true;
        }
        return false;
    }
    
    /**
     * ������� ��� ������ ����������.
     * @param s �� string ���� ������.
     * @return ��� ����� ������ � ���.
     */
    public boolean checkIfValid(String s){
        if(s != null && !s.equals("") && !s.equals(" ")){
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * ���������� ��� template ������� ���� ����������. 
     * 1 ������ ����� ���� ��������� ��� ��� ������������� ���� ��� ��� ����������� 
     * ��� �����������. ���� ����� �� ������������� ��� ��� ���� �������� ���������.
     * 2 ������ ����� ��� �������� ��� ��� ������������� ���� ��� ��� �����������
     * ��� �����������. �� ����� �������������� ��� ��� �������� ��� �� ���������.
     * @param uniqueProfs ����� ������������ ��������� (Professor).
     * @param timeslots ����� strings �� �� ���������� ��������. 
     * @param dates ����� strings �� �� �������� ������ - �����������.
     * @param classrooms ����� ������������ �������� (Course).
     */
    public void createTemplate(List<Professor> uniqueProfs, List<String> timeslots, List<String> dates,List<Classroom> classrooms){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            CellStyle style = getStyle(workbook);

            for (Professor professor : uniqueProfs) {
                // ���������� ������ (sheet) ��� ���� ��������
                String sheetName = professor.getProfSurname()+ " " + professor.getProfFirstname();
                    XSSFSheet sheet = workbook.createSheet(sheetName);
                
                
                    // �������� ��� �������� ����������� (timeslots) �� headers ���� ������
                    Row timeslotRow = sheet.createRow(0);
                    for (int i = 0; i < timeslots.size(); i++) {
                        Cell cell = timeslotRow.createCell(i+1);
                        cell.setCellValue(timeslots.get(i));
                        sheet.autoSizeColumn(i+1);
                    }

                    // �������� ��� ����������� ���� ������� ��� 1�� ������
                    int rowIndex = 1;
                    for (String tmp : dates) {
                        Row row = sheet.createRow(rowIndex++);
                        Cell dateCell = row.createCell(0);
                        LocalDate date = LocalDate.parse(tmp, dateFormatter);
                        String greekDayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("el-GR"));
                        dateCell.setCellValue(greekDayName + " " + tmp);
                    }
                    for (Row row : sheet) {
                        for (Cell cell : row) {
                            cell.setCellStyle(style);
                        }
                    }
                    sheet.autoSizeColumn(0);
            }

            // ���������� ������� ���� ���������� ��� ���� ���������
        try (FileOutputStream outputStream = new FileOutputStream("C:\\Users\\gouvo\\OneDrive\\Documents\\��������\\prof.xlsx")) {
                workbook.write(outputStream);
            }
            logger.appendLogger("� ���������� ��� template ��� ���� ��������� ������������ ��������.");
        }catch (Exception e){
            logger.appendLogger("� ���������� ��� template ��� ���� ��������� �������.");
        }
        
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            CellStyle style = getStyle(workbook);

            for (Classroom classroom : classrooms) {
                // ���������� ���� ������ (sheet) ��� ���� �������
                String sheetName = classroom.getClassroomName();
                XSSFSheet sheet = workbook.createSheet(sheetName);

                // �������� ��� �������� ����������� (timeslots) �� headers ���� ������
                Row timeslotRow = sheet.createRow(0);
                for (int i = 0; i < timeslots.size(); i++) {
                    Cell cell = timeslotRow.createCell(i + 1);
                    cell.setCellValue(timeslots.get(i));
                    sheet.autoSizeColumn(i+1);
                }
                // �������� ��� ����������� ���� ������� ��� 1�� ������
                int rowIndex = 1;
                for (String tmp : dates) {
                        Row row = sheet.createRow(rowIndex++);
                        Cell dateCell = row.createCell(0);
                        LocalDate date = LocalDate.parse(tmp, dateFormatter);
                        String greekDayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("el-GR"));
                        dateCell.setCellValue(greekDayName + " " + tmp);
                }
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        cell.setCellStyle(style);
                    }
                }
                sheet.autoSizeColumn(0);
            }
            // ���������� ������� ���� ���������� ��� ��� ��������
            try (FileOutputStream outputStream1 = new FileOutputStream("C:\\Users\\gouvo\\OneDrive\\Documents\\��������\\class.xlsx")) {
                workbook.write(outputStream1);
            }
            logger.appendLogger("� ���������� ��� template ��� ��� �������� ������������ ��������.");
        }catch (Exception e){
            logger.appendLogger("� ���������� ��� template ��� ��� �������� �������.");
        }
        JOptionPane.showMessageDialog(myJFrame,logger.getLoggerTxt(),
               "������ ���������", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * ������� ��� ���������� ��� ����������� CellStyle �� ������ �������� ��������������.
     * @param workbook ����������� ����� XSSFWorkbook ��� �� ����� ��� �����������.
     * @return style �� ����������� ����� CellStyle.
     */
    
    private CellStyle getStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        style.setFont(font);
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    public boolean readObjects(){
        try {
            FileInputStream fi = new FileInputStream(new File("myObjects.dat"));
            ObjectInputStream oi = new ObjectInputStream(fi);
            profs = (List<Professor>) oi.readObject();
            courses = (List<Course>) oi.readObject();
            classrooms = (List<Classroom>) oi.readObject();
            timeslots = (List<String>) oi.readObject();
            dates = (List<String>) oi.readObject();
            oi.close();
            fi.close();
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(myJFrame, "������ ���� ��� �������� ��� ������������� �������."
            + "�� �������� ��� ������������� ������� ��� ����� �����." + 
            " �������� ������� �� �������� ��� ��� ��������� ����.", "������ ������", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "�������� ���������", "������ ������", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    public void saveObjects(){
        FileOutputStream f = null;
        try {
            f = new FileOutputStream(new File("myObjects.dat"));
            ObjectOutputStream o = new ObjectOutputStream(f);
            if (profs == null || courses == null || classrooms == null || timeslots == null || dates == null ){
                JOptionPane.showMessageDialog(myJFrame, "�� �������� ��� ������������� ������� ��� ����� �����." + 
                    " �������� ������� �� �������� ��� ��� ��������� ����.", "������ ������", JOptionPane.ERROR_MESSAGE);
                return;
            }
            o.writeObject(profs);
            o.writeObject(courses);
            o.writeObject(classrooms);
            o.writeObject(timeslots);
            o.writeObject(dates);
            o.close();
            f.close();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(myJFrame, "�������� ���������� " + 
                    "��� ������������� ������� ��� ������������.", "������ ������", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(myJFrame, "�������� �� �� ������������ " + 
                    "��������. �������� ������� �� �������� ��� ��� ��������� ����.", "������ ������", JOptionPane.ERROR_MESSAGE);
        }
    }
}