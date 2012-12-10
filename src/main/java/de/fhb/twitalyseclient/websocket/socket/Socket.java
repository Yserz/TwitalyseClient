package de.fhb.twitalyseclient.websocket.socket;

import com.sun.grizzly.websockets.DefaultWebSocket;
import com.sun.grizzly.websockets.ProtocolHandler;
import com.sun.grizzly.websockets.WebSocketListener;

/**
 * This class represents a chat socket associated with a client
 *
 * @author Michael Koppen
 *
 */
public class Socket extends DefaultWebSocket {


	public Socket(ProtocolHandler handler, WebSocketListener... listeners) {
		super(handler, listeners);
	}

}
