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
package de.fhb.twitalyseclient.beans.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

/**
 *
 * @author Michael Koppen
 */
public class LazyRedisWordDataModel extends LazyRedisDataModel<WordBean> {

	private final static Logger LOGGER = Logger.getLogger(LazyRedisWordDataModel.class.getName());
	
	
	public LazyRedisWordDataModel(Jedis jedis, String key, int rowCount) {
		super(jedis, key);
		this.setRowCount(rowCount);
	}

	@Override
	public List<WordBean> load(int first, int pageSize, String sortField, SortOrder so, Map<String, String> map) {
		return super.load(first, pageSize, sortField, so, map);
	}

	@Override
	protected List<WordBean> fillList(Set<Tuple> returnedSet) {
		ArrayList<WordBean> list = new ArrayList<WordBean>();
		for (Tuple entry : returnedSet) {
			WordBean word = new WordBean();
			word.setWord(entry.getElement());
			word.setCount((int) entry.getScore());
			list.add(word);
			LOGGER.log(Level.INFO, "Word {0} with Value {1}", new Object[]{word.getWord(), word.getCount()});
		}
		return list;
	}
	
}
