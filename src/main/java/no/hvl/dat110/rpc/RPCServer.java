package no.hvl.dat110.rpc;

import java.io.IOException;
import java.util.HashMap;

import no.hvl.dat110.TODO;
import no.hvl.dat110.messaging.MessageConnection;
import no.hvl.dat110.messaging.Message;
import no.hvl.dat110.messaging.MessageUtils;
import no.hvl.dat110.messaging.MessagingServer;

public class RPCServer {

	private MessagingServer msgserver;
	private MessageConnection connection;
	
	// hashmap to register RPC methods which are required to extend RPCRemoteImpl
	// the key in the hashmap is the RPC identifier of the method
	private HashMap<Byte,RPCRemoteImpl> services;
	
	public RPCServer(int port) {
		
		this.msgserver = new MessagingServer(port);
		this.services = new HashMap<Byte,RPCRemoteImpl>();
		
	}
	
	public void run() {
		
		// the stop RPC method is built into the server
		RPCRemoteImpl rpcstop = new RPCServerStopImpl(RPCCommon.RPIDSTOP,this);
		
		System.out.println("RPC SERVER RUN - Services: " + services.size());
			
		connection = msgserver.accept(); 
		
		System.out.println("RPC SERVER ACCEPTED");
		
		boolean stop = false;
		
		while (!stop) {
	    
		   byte rpcid = 0;
		   Message requestmsg, replymsg;
		   
		   // TODO - START
		   // - receive a Message containing an RPC request
            requestmsg = connection.receive();
            // - extract the identifier for the RPC method to be invoked from the RPC request
			byte[] melding = requestmsg.getData();
			rpcid = melding[0];
		   // - extract the method's parameter by decapsulating using the RPCUtils
			byte[] parameter = RPCUtils.decapsulate(requestmsg.getData());
		   // - lookup the method to be invoked
			RPCRemoteImpl service = services.get(rpcid);
			if(service == null){
				System.out.println("NO SERVICE FOR RPCID=" + rpcid);
			}
		   // - invoke the method and pass the param
			byte[] returnvalue = service.invoke(parameter);
		   // - encapsulate return value
			byte[] replydata = RPCUtils.encapsulate(rpcid, returnvalue);
			Message message = new Message(replydata);
		   // - send back the message containing the RPC reply
            try {
                connection.send(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

		   // TODO - END

			// stop the server if it was stop methods that was called
		   if (rpcid == RPCCommon.RPIDSTOP) {
			   stop = true;
		   }
		}
	
	}
	
	// used by server side method implementations to register themselves in the RPC server
	public void register(byte rpcid, RPCRemoteImpl impl) {
		services.put(rpcid, impl);
	}
	
	public void stop() {

		if (connection != null) {
			connection.close();
		} else {
			System.out.println("RPCServer.stop - connection was null");
		}
		
		if (msgserver != null) {
			msgserver.stop();
		} else {
			System.out.println("RPCServer.stop - msgserver was null");
		}
		
	}
}
