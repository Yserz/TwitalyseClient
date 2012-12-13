package de.fhb.twitalyseclient.websocket.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import com.sun.grizzly.websockets.WebSocketEngine;
import de.fhb.twitalyseclient.beans.components.PushGlobalInformation;
import java.util.logging.Logger;

/**
 * This class represents a servlet starting a webSocket application
 *
 * @author Michael Koppen
 *
 */
public class WebSocketServlet extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = 2454863708244770534L;
	
	public static final Logger LOGGER = Logger.getLogger(WebSocketEngine.WEBSOCKET);
	private final PushGlobalInformation app = new PushGlobalInformation();

	@Override
	public void init(ServletConfig config) throws ServletException {
		System.out.println("Register App...");
		WebSocketEngine.getEngine().register(app);
	}
	@Override
    public void destroy() {
		System.out.println("Unregister App...");
        WebSocketEngine.getEngine().unregister(app);
    }
}
