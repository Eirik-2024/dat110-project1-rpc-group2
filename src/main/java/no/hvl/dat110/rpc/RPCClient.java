package no.hvl.dat110.rpc;

import no.hvl.dat110.TODO;
import no.hvl.dat110.messaging.*;

import java.io.IOException;

public class RPCClient {

	// underlying messaging client used for RPC communication
	private MessagingClient msgclient;

	// underlying messaging connection used for RPC communication
	private MessageConnection connection;
	
	public RPCClient(String server, int port) {

		//skal retunere en message conection med en socket vi kan sende ting på
		msgclient = new MessagingClient(server, port);
	}
	
	public void connect() {
		
		// TODO - START
		// connect using the RPC client
		connection = msgclient.connect();

		// TODO - END
	}
	
	public void disconnect() {
		
		// TODO - START
		// disconnect by closing the underlying messaging connection
		if (connection != null) {
			connection.close();
		}
		// TODO - END
	}

	/*
	 Make a remote call om the method on the RPC server by sending an RPC request message and receive an RPC reply message

	 rpcid is the identifier on the server side of the method to be called
	 param is the marshalled parameter of the method to be called
	 */
	public byte[] call(byte rpcid, byte[] param) {
		
		byte[] returnval = null;
		// TODO - START
		if (connection == null) {
			throw new IllegalStateException("Client is not connected");
		}
		//encapsulater i forhold til rpc
		returnval = RPCUtils.encapsulate(rpcid, param);
		//Ligger det inn som et message format
		Message skalSendes = new Message(returnval);
		//Sender og encapsulater i forhold til message encapsulate
        try {
            connection.send(skalSendes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
		System.out.println("RPCClient: waiting for response...");
		/*
		The rpcid and param must be encapsulated according to the RPC message format
		The return value from the RPC call must be decapsulated according to the RPC message format
		*/
		Message tattImotMelding;
        tattImotMelding = connection.receive();
        System.out.println("RPCClient: received response");
		returnval = RPCUtils.decapsulate(tattImotMelding.getData());

        // TODO - END
		return returnval;
		
	}

}
