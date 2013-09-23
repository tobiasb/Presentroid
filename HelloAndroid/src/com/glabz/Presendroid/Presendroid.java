package com.glabz.Presendroid;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class Presendroid extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	String LOG_TAG = "Presendroid";
    String newline = System.getProperty("line.separator");

    public static Settings settings = new Settings();
    

    public String stringToShow = "";
    
    private void AppendText(String text) {
    	TextView tv = (TextView)this.findViewById(R.id.text);
		tv.append(text + newline);
    }
    
    public byte[] imageToShow;
    private void ShowImage(byte[] imageData) {

        ImageView jpgView = (ImageView)findViewById(R.id.ImageView01);
        
        Drawable d = ImageOperations(imageData);
        
        if(d == null) {
        	AppendText("d = null");
        }
       
        jpgView.setImageDrawable(d);
    }
    
    // Need handler for callbacks to the UI thread
    public final Handler mHandler = new Handler();

    // Create runnable for posting
    public final Runnable mUpdateResults = new Runnable() {
        public void run() {
        	AppendText(stringToShow);
        }
    };
    // Create runnable for posting
    public final Runnable mUpdateImage = new Runnable() {
        public void run() {
    		ShowImage(imageToShow);
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
      
        Log.e("UDP", "TEST");

		AppendText("Connecting..." + newline);
		
		new Thread(new Listener(this)).start();
		
		

		AppendText("Subscribe..." + newline);
		

		String localIpAddress = getLocalIpAddress();
		AppendText("IP Address is " + localIpAddress + newline);
		Send("subscribe " + localIpAddress + " " + Settings.getLocalPort(getApplicationContext()));
		//redir add udp:4712:4712
		AppendText("Done..." + newline);
			
		
		View nextButton = this.findViewById(R.id.next_button);
		nextButton.setOnClickListener(this);
		View prevButton = this.findViewById(R.id.prev_button);
		prevButton.setOnClickListener(this);
		View exitButton = this.findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);
		View tmp = this.findViewById(R.id.Button01);
		tmp.setOnClickListener(this);

//        ImageView jpgView = (ImageView)findViewById(R.id.ImageView01);
//		Drawable image = ImageOperations("http://www.spiegel.de/images/image-145843-thumbbiga-tydu.jpg");
//		jpgView.setImageDrawable(image);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()){
    	case R.id.settings:
    		startActivity(new Intent(this, Settings.class));
    		return true;
    	}
    	
    	return false;
    }
    
    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(LOG_TAG, ex.toString());
        }
        return null;
    }
    
    public void onClick(View v){
    	
    	switch(v.getId()){
    	case R.id.next_button:
			Send("next");
    		break;
    	case R.id.prev_button:
			Send("prev");
    		break;
    	case R.id.exit_button:
			finish();
    		break;
    	case R.id.Button01:
			Send("slidesnum");
    		break;
    	}
    }
    
    private void Send(String text){
    	try {
			InetAddress serverAddr = InetAddress.getByName(Settings.getTargetIp(getApplicationContext()));
			
			byte[] buf = text.getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, Settings.getTargetPort(getApplicationContext()));
	
			DatagramSocket socket = new DatagramSocket();
			
			socket.send(packet);

	        TextView tv = (TextView)this.findViewById(R.id.text);
			tv.append("Sent [" + text + "] to " + serverAddr.toString() + ":" + Settings.getTargetPort(getApplicationContext()) + newline);

            Log.e("UDP", "Sent [" + text + "]");
		} catch (Exception e) {
			e.printStackTrace();
			text = text.concat(e.getMessage() + newline);
            Log.e("UDP", "Error", e);
		}
    }
    
//    private Drawable ImageOperations(String url) {
//		try {
//			InputStream is = (InputStream) this.fetch(url);
//			Drawable d = Drawable.createFromStream(is, "src");
//			return d;
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//			return null;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
    
    private Drawable ImageOperations(byte[] data) {
		ByteArrayInputStream is = new ByteArrayInputStream(data);
		Drawable d = Drawable.createFromStream(is, "src");
		return d;
	}

	public Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}
}