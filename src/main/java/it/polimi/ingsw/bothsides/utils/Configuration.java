package it.polimi.ingsw.bothsides.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;


public class Configuration {

    private InputStream inputStream;

    public int getPortAccept() throws IOException {

        int portAccept = 0;
        int standardAccept = 6700;

        try {

            Properties prop = new Properties();
            String propFileName = Global.CONFIGPROPERTIES;

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException(Global.PROPERTYFILE + propFileName + Global.NOTFOUNDINTHECLASSPATH);
            }

            String portAcceptString = prop.getProperty("portAccept");

            portAccept = Integer.parseInt(portAcceptString);


        } catch (Exception e) {
            System.out.println(Global.EXCEPTION + e);
            portAccept = standardAccept;
        } finally {

            if(inputStream != null) inputStream.close();

        }

        return portAccept;

    }

    public int getPortPingAndError() throws IOException {

        int portPingAndError = 0;
        int standardPingAndError = 6701;

        try {

            Properties prop = new Properties();
            String propFileName = Global.CONFIGPROPERTIES;

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException(Global.PROPERTYFILE + propFileName + Global.NOTFOUNDINTHECLASSPATH);
            }

            String portPingString = prop.getProperty("portPingAndError");

            portPingAndError = Integer.parseInt(portPingString);


        } catch (Exception e) {
            System.out.println(Global.EXCEPTION + e);
            portPingAndError = standardPingAndError;
        } finally {

            if(inputStream != null) inputStream.close();

        }

        return portPingAndError;
    }

    public String getServerLogPath() throws IOException {

        String serverLogPath = null;

        try {

            Properties prop = new Properties();
            String propFileName = Global.CONFIGPROPERTIES;

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException(Global.PROPERTYFILE + propFileName + Global.NOTFOUNDINTHECLASSPATH);
            }

            serverLogPath = prop.getProperty("serverLogPath");


        } catch (Exception e) {
            System.out.println(Global.EXCEPTION + e);
            return System.getProperty("user.home") + "/Desktop";
        } finally {
            if(inputStream != null) inputStream.close();
        }

        return serverLogPath;

    }

    public String getGenericStringFromConfig(String entryConfig) {

        String read = null;

        try {

            Properties prop = new Properties();
            String propFileName = Global.CONFIGPROPERTIES;

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException(Global.PROPERTYFILE + propFileName + Global.NOTFOUNDINTHECLASSPATH);
            }

            read = prop.getProperty(entryConfig);


        } catch (Exception e) {
            System.out.println(Global.EXCEPTION + e);
            return System.getProperty("user.home") + "/Desktop";
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LogPrinter.printOnLog("---FATAL ERROR IN CONFIGURATION");
                    Thread.currentThread().interrupt();
                }
            }
        }

        return read;




    }

    public int getGenericInt(String entryConfig) {

        int genericInt = 0;

        try {

            Properties prop = new Properties();
            String propFileName = Global.CONFIGPROPERTIES;

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException(Global.PROPERTYFILE + propFileName + Global.NOTFOUNDINTHECLASSPATH);
            }

            String portAcceptString = prop.getProperty(entryConfig);

            genericInt = Integer.parseInt(portAcceptString);


        } catch (Exception e) {
            System.out.println(Global.EXCEPTION + e);
        } finally {

            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LogPrinter.printOnLog("---FATAL ERROR IN CONFIGURATION");
                    System.exit(-1);
                }
            }

        }

        return genericInt;





    }

}



class ConfigurationTest {

    //test main
    public static void main(String[] args){


        int port1 = 0;
        int port2 = 0;
        String indirizzo = "";

        Configuration configuration = new Configuration();

        try {

            port1 = configuration.getPortAccept();
            port2 = configuration.getPortPingAndError();
            indirizzo = configuration.getServerLogPath();

            System.out.printf("%s porta accept %d, porta ping %d, indirizzo %s %s",ColorAnsi.RED, port1, port2, indirizzo, ColorAnsi.RESET);


            System.out.println(indirizzo +"Log_" +Calendar.getInstance().toString() +".txt");


        } catch (IOException e) {
            e.printStackTrace();
        }





    }



}