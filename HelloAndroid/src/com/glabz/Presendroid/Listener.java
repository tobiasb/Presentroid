package com.glabz.Presendroid;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.util.Log;
 
public class Listener implements Runnable {

    private String newline = System.getProperty("line.separator");
    private Presendroid parentActivity;
    
    public Listener(Presendroid parent){
    	this.parentActivity = parent;
    }
    
    public void run() {
        try {
            /* Retrieve the ServerName */
            //InetAddress serverAddr = InetAddress.getByName(SERVERIP);
 
            Log.d("UDP", "S: Connecting...");
            /* Create new UDP-Socket */
            DatagramSocket socket = new DatagramSocket(Settings.getLocalPort(parentActivity.getApplicationContext()));

            byte[] buffer = new byte[204800];

            /* Prepare a UDP-Packet that can
             * contain the data we want to receive */
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
 
            while(true){
                /* Receive the UDP-Packet */
            	socket.receive(packet);
            	                
                if(packet.getLength() < 50) {
                    String msg = new String(buffer, 0, packet.getLength());
                    
	                parentActivity.stringToShow = "Received " + msg + newline;
	            	parentActivity.mHandler.post(parentActivity.mUpdateResults);
                } else {
	                parentActivity.imageToShow = packet.getData();
	            	parentActivity.mHandler.post(parentActivity.mUpdateImage);
                }
                
                packet.setLength(buffer.length);
            }
        } 
        catch (Exception e) {
            parentActivity.stringToShow = "Error " + e.getMessage() + newline;
        	parentActivity.mHandler.post(parentActivity.mUpdateResults);
            
        	Log.e("UDP", "S: Error", e);
        }
    }
}
