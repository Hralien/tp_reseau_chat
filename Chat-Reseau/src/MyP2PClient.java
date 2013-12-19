import java.net.BindException;

import simulip.gui.views.simulation.Input;
import simulip.net.*;


public class MyP2PClient extends Application{

	String userName;
	int portUDP;
	
	simulip.net.DatagramSocket d;
	simulip.net.DatagramPacket p;
	public void run(){
		userName = system.in.read();
		do{
			portUDP = Integer.parseInt(system.in.read());
		}while(portUDP>65535);
		//tableau d'octet à envoyer
		byte[] tab = new byte[1+2+1+userName.length()];
		tab[0] = 'r';
		byte[] port = intToByteArray(portUDP);
		java.lang.System.out.println(port);
		tab[1]=port[0];
		tab[2]=port[1];
		tab[3]=new Integer(userName.length()).byteValue();
		for(int i = 0; i < userName.length(); i++){
			char currentChar = userName.charAt(i);
			tab[i+4] = (byte) currentChar;
		}
		try {
			Input l = system.in;
	    	d = new simulip.net.DatagramSocket(this,530);
		    p= new simulip.net.DatagramPacket(tab,13);
		    NetworkAddress serverIP = new NetworkAddress("2.0.0.1");
		    p.setAddress(serverIP);
		    p.setPort(530);
	    }catch(BindException b){
	    	system.out.println(b.getMessage());
	    }catch (NetworkAddressFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		d.send(p);
		
	}
	public static byte[] intToByteArray(int a)
	{
	    byte[] ret = new byte[2];
	    ret[1] = (byte) (a & 0xFF);   
	    ret[0] = (byte) ((a >> 8) & 0xFF);   
	    return ret;
	}
}
