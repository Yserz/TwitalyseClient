/*
 * Copyright (C) 2012 MacYser
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fhb.twitalyseclient.connection;

import com.sun.grizzly.tcp.Request;
import com.sun.grizzly.websockets.ProtocolHandler;
import com.sun.grizzly.websockets.WebSocket;
import com.sun.grizzly.websockets.WebSocketApplication;
import com.sun.grizzly.websockets.WebSocketException;
import com.sun.grizzly.websockets.WebSocketListener;
import de.fhb.twitalyseclient.websocket.servlet.WebSocketServlet;
import de.fhb.twitalyseclient.websocket.socket.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import redis.clients.jedis.exceptions.JedisException;

/**
 * This class represents the websocket connection of the application.
 * 
 * @author Michael Koppen
 */
public abstract class WebSocketConnection extends WebSocketApplication {
	
	@Override
	public WebSocket createWebSocket(ProtocolHandler protocolHandler, WebSocketListener... listeners) {
		return new Socket(protocolHandler, listeners);
	}

	@Override
	public abstract boolean isApplicationRequest(Request request);

//	@Override
//	public void onMessage(WebSocket socket, String text) {
//		if (text.startsWith("login:")) {
////            login((Socket) socket, text);
//		} else {
//			broadcast(/*((Socket) socket).getUser() + " : " +*/text);
//		}
//	}
	public void broadcast(String text) {
		WebSocketServlet.LOGGER.log(Level.INFO, "Broadcasting : {0}", text);
		for (WebSocket webSocket : getWebSockets()) {
			if (!webSocket.isConnected()) {
				continue;
			}
			try {
				send((Socket) webSocket, text);
			} catch (WebSocketException e) {
				e.printStackTrace();
				WebSocketServlet.LOGGER.log(Level.INFO, "Removing chat client: {0}", e.getMessage());
				webSocket.close();
			}
		}
	}

//    private void login(Socket socket, String frame) {
//        if (socket.getUser() == null) {
//            WebSocketServlet.LOGGER.info("ChatApplication.login");
//            socket.setUser(frame.split(":")[1].trim());
//            broadcast(socket.getUser() + " has joined the chat.");
//        }
//    }
	private void send(Socket webSocket, String data) {
		webSocket.send(data);
	}
}
