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

import de.fhb.twitalyseclient.PropertyLoader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.inject.Singleton;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

/**
 * This class loads connection data and opens a connection to a redis server.
 * 
 * @author Michael Koppen
 */
public class RedisConnection {
	private final static Logger LOGGER = Logger.getLogger(RedisConnection.class.getName());
	private String redisHost;
	private Integer redisPort;
	private Jedis jedis;

	public RedisConnection() {
		initRedis();
	}
	
	@PreDestroy
	public void preDestroy(){
		if (jedis != null) {
			jedis.disconnect();
		}
	}
	
	private void initRedis(){
		Properties redisProps = null;
		try {

			PropertyLoader propLoader = new PropertyLoader();
			redisProps = propLoader.loadSystemProperty("redisProps.properties");
		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
		}
		redisHost = redisProps.getProperty("host");
		redisPort = Integer.valueOf(redisProps.getProperty("port"));

		try {
			jedis = new Jedis(redisHost, redisPort);
			jedis.getClient().setTimeout(9999);
		} catch (JedisException e) {
			LOGGER.log(Level.SEVERE, null, e);
		}
	}
	
	public Jedis getConnection(){
		return jedis;
	}
}
