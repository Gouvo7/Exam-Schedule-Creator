/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package thesis;

/**
 *
 * @author dbuser1
 */
public class Logs{
        private String log;
        private int index;
        Logs(){
            log = "";
            index = 0;
        }
        
        public String getLoggerTxt(){
            return log;
        }
        public void appendLogger(String x){
            log = log + x + "\n";
        }
        
        public int getIndex(){
            index = index + 1;
            return index;
        }
        public String getIndexString(){
            index = index + 1;
            return index+") ";
        }
        
        public void loggerClear(){
            log = "";
        }
    }
