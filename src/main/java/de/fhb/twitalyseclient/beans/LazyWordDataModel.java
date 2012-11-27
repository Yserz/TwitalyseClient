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
import java.util.logging.Level;
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

	private final static Logger LOGGER = Logger.getLogger(LazyWordDataModel.class.getName());
	private Jedis jedis;
	private List<WordBean> wordList;

	public LazyWordDataModel(Jedis jedis) {
		this.jedis = jedis;
		LOGGER.setLevel(Level.ALL);
		LOGGER.log(Level.INFO, "LazyWordDataModel init");
	}

	@Override
	public List<WordBean> load(int first, int pageSize, String sortField, SortOrder so, Map<String, String> map) {
		LOGGER.log(Level.INFO, "LazyWordDataModel load()");
		wordList = new ArrayList<WordBean>();
		Set<Tuple> words = null;
		try {
			LOGGER.log(Level.INFO, "START, END: {0}, {1}", new Object[]{first, first+(pageSize-1)});
			words = jedis.zrangeWithScores("words", first, first+(pageSize-1));
			for (Tuple entry : words) {
				LOGGER.log(Level.INFO, "Word {0} with Value {1}", new Object[]{entry.getElement(), entry.getScore()});
			}
		} catch (JedisConnectionException e) {
			LOGGER.log(Level.SEVERE, "JedisConnectionTimeout");
		}

		for (Tuple entry : words) {
			WordBean word = new WordBean();
			word.setWord(entry.getElement());
			word.setCount((int) entry.getScore());
			wordList.add(word);
			LOGGER.log(Level.INFO, "Word {0} with Value {1}", new Object[]{word.getWord(), word.getCount()});

		}
		return wordList;
	}

//	@Override
//	public WordBean getRowData(String rowKey) {
//		LOGGER.log(Level.INFO, "LazyWordDataModel getRowData()");
//		for (WordBean word : wordList) {
//			if (word.getWord().equals(rowKey)) {
//				return word;
//			}
//		}
//		return null;
//	}
//
//	@Override
//	public Object getRowKey(WordBean word) {
//		LOGGER.log(Level.INFO, "LazyWordDataModel getRowKey()");
//		return word.getWord();
//	}
//
//	@Override
//	public int getRowCount() {
//		LOGGER.log(Level.INFO, "LazyWordDataModel getRowCount()");
//		try {
//			LOGGER.log(Level.INFO, "RowCount: {0}", jedis.zcard("words"));
//
//			return Integer.valueOf("" + jedis.zcard("words"));
//		} catch (JedisConnectionException e) {
//			LOGGER.log(Level.SEVERE, "JedisConnectionTimeout");
//		}
//		return 0;
//	}
//
	@Override
	public void setRowIndex(final int rowIndex) {
		LOGGER.log(Level.INFO, "LazyWordDataModel getRowCount()");
		LOGGER.log(Level.INFO, "RowIndex: {0}", rowIndex);
		LOGGER.log(Level.INFO, "PageSize: {0}", getPageSize());
		
		
		if (rowIndex == -1 || getPageSize() == 0) {
			super.setRowIndex(-1);
		} else {
			super.setRowIndex(rowIndex % getPageSize());
		}
	}
}
