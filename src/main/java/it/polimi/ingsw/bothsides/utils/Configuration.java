package it.polimi.ingsw.bothsides.utils;

import it.polimi.ingsw.server.ServerMain;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Properties;


public class Configuration {

    private static InputStream inputStream;


    public static int getPortAccept() throws IOException {

        int portAccept = 0;
        int standardAccept = 6700;

        try {

            Properties prop = new Properties();
            String propFileName = Global.CONFIGPROPERTIES;

            inputStream = Configuration.class.getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException(Global.PROPERTYFILE + propFileName + Global.NOTFOUNDINTHECLASSPATH);
            }

            String portAcceptString = prop.getProperty("portAccept");

            portAccept = Integer.parseInt(portAcceptString);


        } catch (Exception e) {
            LogPrinter.printOnLog(Global.ERRORCONFIG);
            LogPrinter.printOnLog(e.toString());
            portAccept = standardAccept;
        } finally {

            if(inputStream != null) inputStream.close();

        }

        return portAccept;

    }

    public static int getPortPingAndError() throws IOException {

        int portPingAndError = 0;
        int standardPingAndError = 6701;

        try {

            Properties prop = new Properties();
            String propFileName = Global.CONFIGPROPERTIES;

            inputStream = Configuration.class.getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException(Global.PROPERTYFILE + propFileName + Global.NOTFOUNDINTHECLASSPATH);
            }

            String portPingString = prop.getProperty("portPingAndError");

            portPingAndError = Integer.parseInt(portPingString);


        } catch (Exception e) {
            LogPrinter.printOnLog(Global.ERRORCONFIG);
            LogPrinter.printOnLog(e.toString());
            portPingAndError = standardPingAndError;
        } finally {

            if(inputStream != null) inputStream.close();

        }

        return portPingAndError;
    }

    public static String getGenericStringFromConfig(String entryConfig) {

        String read = null;

        try {

            Properties prop = new Properties();
            String propFileName = Global.CONFIGPROPERTIES;

            inputStream = Configuration.class.getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException(Global.PROPERTYFILE + propFileName + Global.NOTFOUNDINTHECLASSPATH);
            }

            read = prop.getProperty(entryConfig);


        } catch (Exception e) {
            LogPrinter.printOnLog(Global.ERRORCONFIG);
            LogPrinter.printOnLog(e.toString());
            return System.getProperty("user.home") + "/Desktop";
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LogPrinter.printOnLog(Global.ERRORCONFIG);
                    LogPrinter.printOnLog(e.toString());

                    Thread.currentThread().interrupt();
                }
            }
        }

        return read;




    }

    public static int getGenericIntFromConfig(String entryConfig) {

        int genericInt = 0;

        try {

            Properties prop = new Properties();
            String propFileName = Global.CONFIGPROPERTIES;

            inputStream = Configuration.class.getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException(Global.PROPERTYFILE + propFileName + Global.NOTFOUNDINTHECLASSPATH);
            }

            String portAcceptString = prop.getProperty(entryConfig);

            genericInt = Integer.parseInt(portAcceptString);


        } catch (Exception e) {

            LogPrinter.printOnLog(Global.ERRORCONFIG);
            LogPrinter.printOnLog(Arrays.toString(e.getStackTrace()));
            System.err.printf("\n%s\n", "FATAL ERROR IN CONFIGURATION");

        } finally {

            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LogPrinter.printOnLog(Global.ERRORCONFIG);
                    LogPrinter.printOnLog(e.toString());
                    System.exit(-1);
                }
            }

        }

        return genericInt;





    }

}
