import simulip.net.*;
import java.net.BindException;
import java.util.*;
import java.math.*;

public class MyP2Pserver extends Application {
	
	/* Dirty User Database */
	Vector<String> addresses= new Vector<String>();
	Vector<byte[]> ports = new Vector<byte[]>();
	Vector<String> names=new Vector<String>();

	byte[] data = new byte[29];
	simulip.net.DatagramSocket d;
	simulip.net.DatagramPacket p;
	

	/* function to register users into the database */
    private void write_mode(){
					system.out.println("recording request");
					NetworkAddress add = p.getAddress();					
					system.out.println("from " + add.getStrAddress());
					byte[] pnum = new byte[3];
					pnum[0] = 0;
					pnum[1] = data[1];
					pnum[2] = data[2];
					BigInteger pbi = new BigInteger(pnum);
					system.out.println("chat at port " + pbi.toString());
					
					byte[] blname = new byte[1];
					blname[0] = data[3];
					BigInteger it = new BigInteger(blname);
					byte[] bname = new byte[it.intValue()];
					for(int i = 0; i < bname.length; i++)
						bname[i] = data[i + 4];
					String name = new String(bname);
					system.out.println("name : " + name);
					byte[] twobyteport = new byte[2];
					twobyteport[0] = pnum[1];
					twobyteport[1] = pnum[2];
					addresses.add(add.getStrAddress());
                                        ports.add(twobyteport);
                                        names.add(name);
					byte[] ack = new byte[1];
					ack[0] = 'y';
					p.setData(ack);
    }

    /* function to find a user registered in the database */
    private void read_mode(){

	/* cursors */
	Enumeration<String> ernames = names.elements();
    Enumeration<String> eraddr = addresses.elements();
    Enumeration<byte[]> erport = ports.elements();
    
	system.out.println("contacting request");
	
	/* reading requested name */
	byte[] blname = new byte[1];
	blname[0] = data[1];
	BigInteger it = new BigInteger(blname);
	byte[] bname = new byte[it.intValue()];
	for(int i = 0; i < bname.length; i++)
	 bname[i] = data[i + 2];
	String name = new String(bname);
	system.out.println("ask for : " + name);

	/* looking for the name among registered users */
	String nam = new String();
    String addr = new String();
    byte [] prt = new byte[2];
	boolean found = false;
	while(!found && ernames.hasMoreElements() && eraddr.hasMoreElements() && erport.hasMoreElements()){
	     nam = ernames.nextElement();
         addr = eraddr.nextElement();
         prt = erport.nextElement();
		found = nam.equals(name);
	}
	/* sending answer of error message */
	if(!found){
		byte[] nack = new byte[1];
		nack[0] = 'n';
		p.setData(nack);
	} else{
		byte[] resp = new byte[7];
		try{
			/* type ('y' or 'n') */
			resp[0] = 'y';
			/* address of the user found in the database */
			byte[] add = NetworkAddress.toBytes((new NetworkAddress(addr)).getBits());
			resp[1] = add[0];
			resp[2] = add[1];
			resp[3] = add[2];
			resp[4] = add[3];
			/* port recorded for this user in the database */
			resp[5] = prt[0];
			resp[6] = prt[1];
			/* writing data into the UDP packet */
			p.setData(resp);	
	    } catch(NetworkAddressFormatException nafe){
	    	system.out.println(nafe.getMessage());
	    }
	}
	}


	public void run(){
		/* Network init */
		try {
	    	d = new simulip.net.DatagramSocket(this,530);
		    p= new simulip.net.DatagramPacket(data,13);
	    }catch(BindException b){
	    	system.out.println(b.getMessage());
	    }
		
	    while(true){
				d.receive(p);
				data = p.getData();
				if(data[0] == 'r')
					write_mode(); // a recording request was received
				else if (data[0] == 'c') {
                    read_mode(); // a lookup request was received
                } else {
					byte[] nack = new byte[1];
					nack[0] = 'n';
					p.setData(nack);
					system.out.println("unknow request ");
				}
				/* send answer */
				p.setAddress(p.getAddress());
				p.setPort(p.getPort());
				d.send(p);
			}
	
		}


}
