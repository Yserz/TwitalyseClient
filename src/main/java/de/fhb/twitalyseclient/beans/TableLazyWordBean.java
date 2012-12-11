/*
 * Copyright (C) 2012 Michael Koppen
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
package de.fhb.twitalyseclient.beans;

import de.fhb.twitalyseclient.connection.RedisConnection;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

/**
 * 
 *
 * @author Michael Koppen <koppen@fh-brandenburg.de>
 */
@Named
@RequestScoped
public class TableLazyWordBean implements Serializable {
	private final static Logger LOGGER = Logger.getLogger(TableLazyWordBean.class.getName());
	
	private LazyDataModel<WordBean> wordList;
	private Jedis jedis;

	public TableLazyWordBean() {
		jedis = new RedisConnection().getConnection();
		
	}

	public LazyDataModel<WordBean> getWordList() {
		String key = "coordswords";
		int rowCount = 0;
		try {
			rowCount = Integer.valueOf("" + jedis.zcard(key));
		} catch (JedisException e) {
			LOGGER.log(Level.SEVERE, "JedisException: {0}", e);
		}
		
		wordList = new LazyRedisWordDataModel(jedis, key, rowCount);
		return wordList;
	}

	public void setWordList(LazyRedisDataModel wordList) {
		this.wordList = wordList;
	}
}
