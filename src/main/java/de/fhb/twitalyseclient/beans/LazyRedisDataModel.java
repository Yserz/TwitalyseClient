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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisException;

/**
 *
 * @author Michael Koppen
 */
public abstract class LazyRedisDataModel<T> extends LazyDataModel<T> {

	private final static Logger LOGGER = Logger.getLogger(LazyRedisDataModel.class.getName());
	private Jedis jedis;
	private String key;

	public LazyRedisDataModel(Jedis jedis, String key) {
		this.key = key;
		this.jedis = jedis;
	}

	@Override
	public List<T> load(int first, int pageSize, String sortField, SortOrder so, Map<String, String> map) {
		Set<Tuple> words = null;
		try {
			LOGGER.log(Level.INFO, "START: {0}, END: {1}", new Object[]{first, first+(pageSize-1)});
			words = jedis.zrangeWithScores(key, first, first+(pageSize-1));
		} catch (JedisException e) {
			LOGGER.log(Level.SEVERE, "JedisException{0}", e);
		}
		
		return fillList(words);
	}
	
	protected abstract List<T> fillList(Set<Tuple> returnedSet);
	
	@Override
	public void setRowIndex(final int rowIndex) {		
		if (rowIndex == -1 || getPageSize() == 0) {
			super.setRowIndex(-1);
		} else {
			super.setRowIndex(rowIndex % getPageSize());
		}
	}
}
