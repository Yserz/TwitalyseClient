package de.fhb.twitalyseclient.beans.components;

import com.sun.grizzly.tcp.Request;
import com.sun.grizzly.websockets.WebSocket;
import de.fhb.twitalyseclient.connection.RedisConnection;
import de.fhb.twitalyseclient.connection.WebSocketConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

/**
 * This class represents a web socket application; it creates new sockets and
 * passes messages to all connected users
 *
 * To run this app on glassfish run this command on shell: asadmin set
 * configs.config.server-config.network-config.protocols.protocol.http-listener-1.http.websockets-support-enabled=true
 *
 * @author Michael Koppen
 *
 */
@Singleton
public class PushGlobalInformation extends WebSocketConnection {

	private final static Logger LOGGER = Logger.getLogger(PushGlobalInformation.class.getName());
	private boolean active;
	private ScheduledExecutorService executor;
	private Jedis jedis;
	private Long numStati;
	private Long numWords;

	public PushGlobalInformation() {
		jedis = new RedisConnection().getConnection();
	}

	@Override
	public boolean isApplicationRequest(Request request) {
		final String uri = request.requestURI().toString();
		return uri.endsWith("/globalInfo");
	}

	@Override
	public void onConnect(WebSocket socket) {
		super.onConnect(socket);
		run();
	}

	private void run() {

		if (!active && !getWebSockets().isEmpty()) {
			executor = Executors.newSingleThreadScheduledExecutor();
			executor.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					String globalInfoJson;
					try {
						StringBuilder jsonBuilder = new StringBuilder("{");
						try {
							jsonBuilder.append("\"allStati\":")
									.append(getNumStati())
									.append(",");
							jsonBuilder.append("\"allWordsFilteredFound\":")
									.append(getNumWords());
						} catch (JedisException e) {
							LOGGER.log(Level.SEVERE, "JedisException {0}", e);
						}
						jsonBuilder.append("}");


						globalInfoJson = jsonBuilder.toString();

						broadcast(globalInfoJson);
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, "Exception: {0}", e);
					}

					if (getWebSockets().isEmpty()) {
						active = false;
						executor.shutdownNow();
					}
//					jedis.incr("#stati");

				}
			}, 0, 1, TimeUnit.SECONDS);
			active = true;

		}
	}

	private String escape(String orig) {
		StringBuilder buffer = new StringBuilder(orig.length());

		for (int i = 0; i < orig.length(); i++) {
			char c = orig.charAt(i);
			switch (c) {
				case '\b':
					buffer.append("\\b");
					break;
				case '\f':
					buffer.append("\\f");
					break;
				case '\n':
					buffer.append("<br />");
					break;
				case '\r':
					// ignore
					break;
				case '\t':
					buffer.append("\\t");
					break;
				case '\'':
					buffer.append("\\'");
					break;
				case '\"':
					buffer.append("\\\"");
					break;
				case '\\':
					buffer.append("\\\\");
					break;
				case '<':
					buffer.append("&lt;");
					break;
				case '>':
					buffer.append("&gt;");
					break;
				case '&':
					buffer.append("&amp;");
					break;
				default:
					buffer.append(c);
			}
		}

		return buffer.toString();
	}

	public long getNumStati() {
		try {
			String temp = jedis.get("#stati");
			if (temp == null) {
				numStati = 0l;
			}else {
				numStati = Long.valueOf(temp);
			}
		} catch (JedisException e) {
			LOGGER.log(Level.SEVERE, "JedisException {0}", e);
		}

		return numStati;
	}

	public void setNumStati(Long numStati) {
		this.numStati = numStati;
	}

	public long getNumWords() {
		try {
			String temp = jedis.get("#words_filtered");
			if (temp == null) {
				numWords = 0l;
			}else {
				numWords = Long.valueOf(temp);
			}
		} catch (JedisException e) {
			LOGGER.log(Level.SEVERE, "JedisException {0}", e);
		}

		return numWords;
	}

	public void setNumWords(Long numWords) {
		this.numWords = numWords;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
