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
package de.fhb.twitalyseclient.beans;

import de.fhb.twitalyseclient.connection.RedisConnection;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.primefaces.model.LazyDataModel;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

/**
 *
 * @author MacYser
 */
@Named
@RequestScoped
public class TableLazyDailyWordBean implements Serializable {

	private final static Logger LOGGER = Logger.getLogger(TableLazyDailyWordBean.class.getName());
	private LazyDataModel<WordBean> wordList;
	private Jedis jedis;

	/**
	 * Creates a new instance of TableLazyDailyWordBean
	 */
	public TableLazyDailyWordBean() {
		jedis = new RedisConnection().getConnection();

	}

	public LazyDataModel<WordBean> getWordList() {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		String key = (String)session.getAttribute("key");

		int rowCount = 0;
		try {
			rowCount = Integer.valueOf("" + jedis.zcard(key));
		} catch (JedisException e) {
			LOGGER.log(Level.SEVERE, "JedisException: {0}", e);
		}

		LOGGER.log(Level.INFO, key);
		LOGGER.log(Level.INFO, "{0}", rowCount);

		wordList = new LazyRedisWordDataModel(jedis, key, rowCount);
		return wordList;
	}

	public void setWordList(LazyRedisDataModel wordList) {
		this.wordList = wordList;
	}
}
