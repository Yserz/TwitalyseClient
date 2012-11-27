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

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * 
 *
 * @author Michael Koppen <koppen@fh-brandenburg.de>
 */
@Named
@RequestScoped
public class WordCountBean implements Serializable {
	private final static Logger LOGGER = Logger.getLogger(WordCountBean.class.getName());
	
	
	private LazyDataModel<WordBean> wordList;
	private int numStati;
	private int numWords;
	private Jedis jedis;

	public WordCountBean() {
		LOGGER.setLevel(Level.ALL);
		jedis = new Jedis("ec2-54-247-30-175.eu-west-1.compute.amazonaws.com", 6379);
		jedis.getClient().setTimeout(9999);
		wordList = new LazyWordDataModel(jedis);
		wordList.setRowCount(Integer.valueOf("" + jedis.zcard("words")));
	}
	
	private void getAllWordsFromRedis(){
		
		
		
//		PushContext context = PushContextFactory.getDefault().getPushContext();
		
	}

	public LazyDataModel<WordBean> getWordList() {
		getAllWordsFromRedis();
		return wordList;
	}

	public void setWordList(LazyWordDataModel wordList) {
		this.wordList = wordList;
	}

	public int getNumStati() {
		try {
			numStati = Integer.valueOf(jedis.get("#stati"));
		} catch (JedisConnectionException e) {
			LOGGER.log(Level.SEVERE, "JedisConnectionTimeout");
		}
		
		return numStati;
	}

	public void setNumStati(int numStati) {
		this.numStati = numStati;
	}

	public int getNumWords() {
		try {
			numWords = Integer.valueOf(jedis.get("#words_filtered"));
		} catch (JedisConnectionException e) {
			LOGGER.log(Level.SEVERE, "JedisConnectionTimeout");
		}
		
		return numWords;
	}

	public void setNumWords(int numWords) {
		this.numWords = numWords;
	}
	
	
}
