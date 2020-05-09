package it.polimi.ingsw.server.utils;

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
            String propFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            String portAcceptString = prop.getProperty("portAccept");

            portAccept = Integer.parseInt(portAcceptString);


        } catch (Exception e) {
            System.out.println("Exception: " + e);
            portAccept = standardAccept;
        } finally {
            inputStream.close();
            return portAccept;
        }

    }

    public int getPortPingAndError() throws IOException {

        int portPingAndError = 0;
        int standardPingAndError = 6701;

        try {

            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            String portPingString = prop.getProperty("portPingAndError");

            portPingAndError = Integer.parseInt(portPingString);


        } catch (Exception e) {
            System.out.println("Exception: " + e);
            portPingAndError = standardPingAndError;
        } finally {
            inputStream.close();
            return portPingAndError;
        }

    }

    public String getServerLogPath() throws IOException {

        String serverLogPath = null;

        try {

            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            serverLogPath = prop.getProperty("serverLogPath");


        } catch (Exception e) {
            System.out.println("Exception: " + e);
            return System.getProperty("user.home") + "/Desktop";
        } finally {
            inputStream.close();
        }

        return serverLogPath;

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


            int orario = Calendar.getInstance().getTime().getMinutes();

            System.out.println(indirizzo +"Log_" +orario +".txt");


        } catch (IOException e) {
            e.printStackTrace();
        }





    }



}