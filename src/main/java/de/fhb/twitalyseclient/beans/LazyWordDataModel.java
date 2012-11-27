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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 *
 * @author Michael Koppen
 */
public class LazyWordDataModel extends LazyDataModel<WordBean> {

	private final static Logger LOGGER = Logger.getLogger(WordCountBean.class.getName());
	private Jedis jedis;
	
	public LazyWordDataModel(Jedis jedis) {
		this.jedis = jedis;
	}
	
	@Override
	public List<WordBean> load(int start, int end, String string, SortOrder so, Map<String, String> map) {
		Set<Tuple> words = null;
		try {
			words = jedis.zrangeWithScores("words", start, end);
		} catch (JedisConnectionException e) {
		}
		
		List<WordBean> tempList = new ArrayList<WordBean>();
		
		for (Tuple entry : words) {
			WordBean word = new WordBean();
			word.setWord(entry.getElement());
			word.setCount((int)entry.getScore());
			tempList.add(word);
		}
		return tempList;
	}
	
}
