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
package de.fhb.twitalyseclient;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import redis.clients.jedis.Jedis;

/**
 *
 * @author MacYser
 */
public class PropertyLoaderTest {
	

//	@Test
	public void redisTest() throws IOException{
		PropertyLoader propLoader = new PropertyLoader();
		Properties redisProps =	propLoader.loadSystemProperty("redisProps.properties");
		String host = redisProps.getProperty("host");
		int port = Integer.valueOf(redisProps.getProperty("port"));
		assertFalse(host.isEmpty());
		assertNotNull(port);
		Jedis jedis = new Jedis(host, port);
		jedis.connect();
		jedis.disconnect();
	}
}
