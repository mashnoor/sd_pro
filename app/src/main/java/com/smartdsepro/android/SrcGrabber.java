package com.smartdsepro.android;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;


/*
 * This class is used for grabbing the source code of a webpage
 */

public class SrcGrabber
{




    public SrcGrabber()
    {

    }

    public static String grabSource(String url) throws IOException, URISyntaxException
    {

        URL robotURL = new URL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(robotURL.openStream()));
        String line = null;
        StringBuilder builder = new StringBuilder();
        while((line = in.readLine()) != null) {

            builder.append(line);
        }
        in.close();

        return builder.toString();

    }







}