/*
 * Copyright 2004-2014 ICEsoft Technologies Canada Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.icefaces.samples.showcase.example.ace.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

import org.icefaces.ace.component.menuitem.MenuItem;
import org.icefaces.ace.component.submenu.Submenu;
import org.icefaces.ace.model.DefaultMenuModel;
import org.icefaces.ace.model.MenuModel;

@ManagedBean(name= MenuDynamic.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class MenuDynamic implements Serializable {
    public static final String BEAN_NAME = "menuDynamic";
	public String getBeanName() { return BEAN_NAME; }
    
    private MenuModel menuModel;
    private int itemCount = 1;
    
    public MenuDynamic() 
    {
        menuModel = new DefaultMenuModel();
    }
    
    public MenuModel getMenuModel() { return menuModel; }
    
    public void setMenuModel(MenuModel menuModel) { this.menuModel = menuModel; }
    
    private Submenu generateItems(Submenu sub) {
        sub.getChildren().clear();
        
        MenuItem toAdd = null;
        for (int i = 0; i < itemCount; i++) {
            toAdd = new MenuItem();
            toAdd.setId(sub.getId() + "_mmi_" + (i+1));
            toAdd.setValue("Value " + (i+1));
            sub.getChildren().add(toAdd);
        }
        
        return sub;
    }
    
    private void refreshItems() {
        for (UIComponent currentMenu : menuModel.getSubmenus()) {
            generateItems((Submenu)currentMenu);
        }
    }
    
    public void addSubmenu(ActionEvent event) {
        int nextCount = menuModel.getSubmenus() != null ? menuModel.getSubmenus().size() : 0;
        
        Submenu newSubmenu = new Submenu();
        newSubmenu.setId("sm_" + (nextCount+1));
        newSubmenu.setLabel("Submenu " + (nextCount+1));
        generateItems(newSubmenu);
        
        menuModel.addSubmenu(newSubmenu);
    }
    
    public void removeSubmenu(ActionEvent event) {
        List<UIComponent> menus = menuModel.getMenus();
        
        if ((menus != null) && (menus.size() > 0)) {
			ArrayList<UIComponent> orderedMenus = new ArrayList<UIComponent>(menus);
            for (int i = orderedMenus.size() - 1 ; i >= 0; i--) {
				UIComponent menu = orderedMenus.get(i);
				if (menu instanceof Submenu) {
					menus.remove(menu);
					break;
				}
			}
        }
    }
    
    public void addItem(ActionEvent event) {
        itemCount++;
        
        refreshItems();
    }
    
    public void removeItem(ActionEvent event) {
        if (itemCount > 1) {
            itemCount--;
        }
        
        refreshItems();
    }
}
