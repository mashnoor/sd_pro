package com.smartdsepro.android;
/*
 * This class is used for checking for if there is a active Internet connection 
 * Written By : Nowfel Mashnoor
 * From : Mashnoor Lab
 * 
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Active_net_checking {

	/*To use this class
	 * You just need to use Active_net_cheking.testInte()
	 * 
	 */

    public static boolean testInte(String site) throws IOException {

        Socket socket = new Socket();
        InetSocketAddress address = new InetSocketAddress(site, 80);
        try {
            socket.connect(address, 30000);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            socket.close();
        }

    }

}
