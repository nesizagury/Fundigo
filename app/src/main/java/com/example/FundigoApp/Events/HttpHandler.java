package com.example.FundigoApp.Events;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpHandler {

    // ----------------------------------------------------------------------------
    // -- HTTP GET

    public static String get(String address, String queryString) {

        HttpURLConnection connection = null;
        BufferedReader input = null;
        try {

            // the url
            URL url;
            if (queryString != null) {
                // with query string (separated by '?')
                url = new URL(address + "?" + queryString);
            } else {
                // no query string
                url = new URL(address);
            }

            // open and set up the connection:
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);

            // check HTTP response code:
            // codes 200-299 are HTTP OK, 300+ are HTTP errors
            if (connection.getResponseCode() >= 300) {
                return null;
            }

            // we can read the response from the connection's input stream:
            // we'll wrap it with a Input Stream Reader to read it as text
            // we'll wrap it with a Buffered Reader to be more efficient
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // we'll read the response from the input stream line by line
            // and collect all the lines to a String Builder
            // until we get null - the end of the response
            StringBuilder response = new StringBuilder();
            String line = "";
            while ((line = input.readLine()) != null) {
                response.append(line + "\n");
            }

            // return as string:
            return response.toString();

        } catch (MalformedURLException e) {
            // oops
            e.printStackTrace();
            return null;
        } catch (ProtocolException e) {
            // oops
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // oops
            e.printStackTrace();
            return null;
        } finally {

            // clean up
            // close streams and disconnect.

            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}
