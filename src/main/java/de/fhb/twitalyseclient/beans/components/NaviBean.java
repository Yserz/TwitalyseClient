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
package de.fhb.twitalyseclient.beans.components;

import de.fhb.twitalyseclient.beans.common.NaviActionListener;
import de.fhb.twitalyseclient.connection.RedisConnection;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.submenu.Submenu;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;
import redis.clients.jedis.Jedis;

/**
 *
 * @author MacYser
 */
@Named
@RequestScoped
public class NaviBean {

	private MenuModel model;
	private Jedis jedis;

	/**
	 * Creates a new instance of NaviBean
	 */
	public NaviBean() {
		jedis = new RedisConnection().getConnection();
	}

	@PreDestroy
	private void preDestroy() {
		jedis.disconnect();
	}

	private Set<String> getKeyOfDailyWords() {
		return jedis.keys("words_*");
	}
	
	private Set<String> getKeyOfDailyWordsInEnviroment() {
		return jedis.keys("coordswords_*");
	}
	
	private Set<String> getKeyOfDailyHashtags() {
		return jedis.keys("hashtags_*");
	}
	
	private void buildMenuModel() {
		model = new DefaultMenuModel();
		
		String key;

		//Global submenu
		Submenu subAllStatistics = new Submenu();
		subAllStatistics.setLabel("Global Statistics");

		MenuItem item = new MenuItem();
		
		item.setValue("All Words");
		key = "words";
		item.setAjax(false);
		item.getAttributes().put("key", key);
		item.addActionListener(new NaviActionListener());
		subAllStatistics.getChildren().add(item);

		item = new MenuItem();
		item.setValue("All Words in Enviroment");
		key = "coordswords";
		item.setAjax(false);
		item.getAttributes().put("key", key);
		item.addActionListener(new NaviActionListener());
		subAllStatistics.getChildren().add(item);

		item = new MenuItem();
		item.setValue("All Languages");
		key = "languages";
		item.setAjax(false);
		item.getAttributes().put("key", key);
		item.addActionListener(new NaviActionListener());
		subAllStatistics.getChildren().add(item);


		item = new MenuItem();
		item.setValue("All Sources");
		key = "sources";
		item.setAjax(false);
		item.getAttributes().put("key", key);
		item.addActionListener(new NaviActionListener());
		subAllStatistics.getChildren().add(item);
		
		item = new MenuItem();
		item.setValue("All Hashtags");
		key = "hashtags";
		item.setAjax(false);
		item.getAttributes().put("key", key);
		item.addActionListener(new NaviActionListener());
		subAllStatistics.getChildren().add(item);



		model.addSubmenu(subAllStatistics);

		//Daily submenu
		Submenu subDailyStatistics = new Submenu();
		subDailyStatistics.setLabel("Daily Statistics");


		String name;
		
		for (String keyDaily : getKeyOfDailyWords()) {

			name = "All Words of " + keyDaily.split("_")[1] + "." + keyDaily.split("_")[2] + "." + keyDaily.split("_")[3];
			item = new MenuItem();
			item.setAjax(false);
			item.setValue(name);
			item.getAttributes().put("key", keyDaily);
			item.addActionListener(new NaviActionListener());
			subDailyStatistics.getChildren().add(item);
		}
		for (String keyDaily : getKeyOfDailyWordsInEnviroment()) {

			name = "All Words in Enviroment of " + keyDaily.split("_")[1] + "." + keyDaily.split("_")[2] + "." + keyDaily.split("_")[3];
			item = new MenuItem();
			item.setAjax(false);
			item.setValue(name);
			item.getAttributes().put("key", keyDaily);
			item.addActionListener(new NaviActionListener());
			subDailyStatistics.getChildren().add(item);
		}
		for (String keyDaily : getKeyOfDailyHashtags()) {

			name = "All Hashtags of " + keyDaily.split("_")[1] + "." + keyDaily.split("_")[2] + "." + keyDaily.split("_")[3];
			item = new MenuItem();
			item.setAjax(false);
			item.setValue(name);
			item.getAttributes().put("key", keyDaily);
			item.addActionListener(new NaviActionListener());
			subDailyStatistics.getChildren().add(item);
		}
		
		model.addSubmenu(subDailyStatistics);
	}

	public MenuModel getModel() {
		buildMenuModel();
		return model;
	}
}
