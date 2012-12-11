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

	private Set<String> getAllKeys() {
		return jedis.keys("coordswords_*");
	}

	private void buildMenuModel() {
		model = new DefaultMenuModel();

		//Global submenu
		Submenu submenu = new Submenu();
		submenu.setLabel("All");

		MenuItem item = new MenuItem();
		item.setValue("All Words");
		item.setUrl("/");
		submenu.getChildren().add(item);

		model.addSubmenu(submenu);

		//Daily submenu
		submenu = new Submenu();
		submenu.setLabel("Daily");


		String name;
		for (String key : getAllKeys()) {
			
			name = "Words of "+key.split("_")[1]+"."+key.split("_")[2]+"."+key.split("_")[3];
			item = new MenuItem();
			item.setAjax(false);
			item.setValue(name);
			item.getAttributes().put("key", key);
			item.addActionListener(new NaviActionListener());
			submenu.getChildren().add(item);
		}

		model.addSubmenu(submenu);
	}

	public MenuModel getModel() {
		buildMenuModel();
		return model;
	}
}
