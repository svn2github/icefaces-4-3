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

package org.icefaces.ace.component.drawerpanel;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.FacesException;

import org.icefaces.ace.renderkit.CoreRenderer;
import org.icefaces.ace.util.ComponentUtils;
import org.icefaces.ace.util.JSONBuilder;
import org.icefaces.component.Focusable;
import org.icefaces.impl.component.FocusManager;
import org.icefaces.render.MandatoryResourceComponent;
import org.icefaces.util.EnvUtils;
import org.icefaces.util.CoreComponentUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@MandatoryResourceComponent(tagName="drawerPanel", value="org.icefaces.ace.component.drawerpanel.DrawerPanel")
public class DrawerPanelRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        super.decodeBehaviors(context, component);
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        DrawerPanel drawerPanel = (DrawerPanel) component;
        String clientId = drawerPanel.getClientId(context);

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, null);
        ComponentUtils.enableOnElementUpdateNotify(writer, clientId);

        String baseclass = drawerPanel.HIDE_CLASS;
        if (drawerPanel.isVisible()){
            baseclass = drawerPanel.SHOW_CLASS;
        }
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_main", null);
        if (drawerPanel.getStyleClass() !=null && drawerPanel.getStyleClass().trim().length()>0){
            baseclass += drawerPanel.getStyleClass();
        }
        //writer.writeAttribute("class", baseclass, null);
	}

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        DrawerPanel drawerPanel = (DrawerPanel) component;

        writer.endElement("div");

        encodeScript(context, drawerPanel);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, DrawerPanel drawerPanel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = drawerPanel.getClientId(context);
        boolean ariaEnabled = EnvUtils.isAriaEnabled(context);

        String setFocusID = "";
            //set focus on the specified component
        String focusFor = drawerPanel.getSetFocus();
        if ("none".equalsIgnoreCase(focusFor)) {
            setFocusID = "**none";
        } else if (focusFor != null && !"".equals(focusFor)) {
            UIComponent c = findComponent(drawerPanel, focusFor);
            if (c instanceof UIInput) {
                setFocusID = c.getClientId(context);
            }
            if (c instanceof Focusable) {
                setFocusID = ((Focusable) c).getFocusedElementId();
            }
        }

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        JSONBuilder jb = JSONBuilder.create();
        jb.beginFunction("ice.ace.lazy")
          .item("DrawerPanel")
          .beginArray()
          .item(clientId)
          .beginMap()
          .entry("isVisible", drawerPanel.isVisible())
          .entry("setFocus", setFocusID);

        String baseclass = drawerPanel.HIDE_CLASS;
        if (drawerPanel.isVisible()){
            baseclass = drawerPanel.SHOW_CLASS;
        }
        String styleClass = baseclass;
		String style = drawerPanel.getStyle();
        String showEffect = drawerPanel.getShowEffect();
        String hideEffect = drawerPanel.getHideEffect();
        String headerText = drawerPanel.getHeader();
        String position = drawerPanel.getPosition();
        int width = drawerPanel.getWidth();
        int zIndex = drawerPanel.getZindex();
        String onShow = drawerPanel.getOnShow();
        String onHide = drawerPanel.getOnHide();

        if (styleClass != null) jb.entry("dialogClass", styleClass);
		if (style != null) jb.entry("dialogStyle", style);
        if (width > 0) jb.entry("width", width);
        if (drawerPanel.isModal()) jb.entry("modal", true);
        if (zIndex != 1000) jb.entry("zIndex", zIndex);
        if (showEffect != null) jb.entry("show", showEffect);
        if (hideEffect != null) jb.entry("hide", hideEffect);
        if (!drawerPanel.isCloseOnEscape()) jb.entry("closeOnEscape", false);
        if (!drawerPanel.isShowHeader()) jb.entry("showHeader", false);
        if (onShow != null) jb.entry("onShow", "function(event, ui) {" + onShow + "}", true);
        if (onHide != null) jb.entry("onHide", "function(event, ui) {" + onHide + "}", true);

        //Position
        if (position != null) {
            if (position.contains(",")) {
				jb.entry("position", "[" + position + "]", true);
            } else {
                jb.entry("position", position);
            }
        }

        jb.entryNonNullValue("title", headerText);
		jb.entry("ariaEnabled", ariaEnabled);

        //Behaviors
        encodeClientBehaviors(context, drawerPanel, jb);

        jb.endMap().endArray();
		jb.item(clientId); // root id
		jb.endFunction();
		writer.write("ice.ace.lazy.registry['"+clientId+"'] = function(){ return "+jb.toString()+"};");
		if (drawerPanel.isVisible()) writer.write("ice.ace.instance('"+clientId+"').show();");

        writer.endElement("script");
    }

    private static UIComponent findComponent(UIComponent base, String id) {
        if (id.equals(base.getId()))
            return base;

        UIComponent kid;
        UIComponent result = null;
        Iterator<UIComponent> kids = base.getFacetsAndChildren();
        while (kids.hasNext() && (result == null)) {
            kid = kids.next();
            if (id.equals(kid.getId())) {
                result = kid;
                break;
            }
            result = findComponent(kid, id);
            if (result != null) {
                break;
            }
        }
        return result;
    }
}