/*
 * Original Code Copyright Prime Technology.
 * Subsequent Code Modifications Copyright 2011-2014 ICEsoft Technologies Canada Corp. (c)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * NOTE THIS CODE HAS BEEN MODIFIED FROM ORIGINAL FORM
 *
 * Subsequent Code Modifications have been made and contributed by ICEsoft Technologies Canada Corp. (c).
 *
 * Code Modification 1: Integrated with ICEfaces Advanced Component Environment.
 * Contributors: ICEsoft Technologies Canada Corp. (c)
 *
 * Code Modification 2: (ICE-6978) Used JSONBuilder to add the functionality of escaping JS output.
 * Contributors: ICEsoft Technologies Canada Corp. (c)
 */
package org.icefaces.ace.component.tooltip;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitHint;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.el.ValueExpression;

import org.icefaces.ace.renderkit.CoreRenderer;
import org.icefaces.ace.util.ComponentUtils;
import org.icefaces.ace.util.JSONBuilder;
import org.icefaces.render.MandatoryResourceComponent;
import org.icefaces.ace.component.delegate.Delegate;
import org.icefaces.util.EnvUtils;
import org.icefaces.util.CoreComponentUtils;
import org.icefaces.util.JavaScriptRunner;
import org.icefaces.ace.event.TooltipDelegateDisplayEvent;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.EnumSet;

@MandatoryResourceComponent(tagName="tooltip", value="org.icefaces.ace.component.tooltip.Tooltip")
public class TooltipRenderer extends CoreRenderer {

	private static final Map<String, String> positionsMap = new HashMap<String, String>();
	
	static {
		positionsMap.put("topleft", "top left");
		positionsMap.put("topmiddle", "top center");
		positionsMap.put("topright", "top right");
		positionsMap.put("righttop", "right top");
		positionsMap.put("rightmiddle", "right center");
		positionsMap.put("rightbottom", "right bottom");
		positionsMap.put("bottomright", "bottom right");
		positionsMap.put("bottommiddle", "bottom center");
		positionsMap.put("bottomleft", "bottom left");
		positionsMap.put("leftbottom", "left bottom");
		positionsMap.put("leftmiddle", "left center");
		positionsMap.put("lefttop", "left top");
		positionsMap.put("center", "center");
	}

    @Override
    public void decode(FacesContext facesContext, UIComponent component) {
        Tooltip tooltip = (Tooltip) component;
        String clientId = tooltip.getClientId(facesContext);
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();

		tooltip.setStore(null);
		String delegateId = tooltip.getForDelegate();
		if (delegateId != null) {
			UIComponent delegateComponent = CoreComponentUtils.findComponentInView(facesContext.getViewRoot(), delegateId);
			if (delegateComponent != null) {
				if (params.containsKey(clientId + "_activeComponent")) {
					String activeComponentId = params.get(clientId + "_activeComponent");
					if (activeComponentId != null && !"".equals(activeComponentId)) {
						ValueExpression fetch = tooltip.getValueExpression("fetch");
						if (fetch != null) {
							String expression = fetch.getExpressionString();
							Object data = retrieveData(facesContext, delegateComponent, activeComponentId, expression);
							tooltip.setStore(data);
						}
					}
				}
			}
		}
        if (params.containsKey(clientId + "_displayListener")) {
			ActionEvent event = new ActionEvent(tooltip);
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			tooltip.queueEvent(event);
			TooltipDelegateDisplayEvent delegateEvent = new TooltipDelegateDisplayEvent(tooltip);
			delegateEvent.setPhaseId(PhaseId.INVOKE_APPLICATION);
			tooltip.queueEvent(delegateEvent);
        }
        decodeBehaviors(facesContext, tooltip);
    }

    @Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Tooltip tooltip = (Tooltip) component;
		
		encodeScript(facesContext, tooltip);
		if(tooltip.getValue() == null) {
			ResponseWriter writer = facesContext.getResponseWriter();
			String clientId = tooltip.getClientId(facesContext);

			writer.startElement("div", null);
			writer.writeAttribute("id", clientId + "_content", null);
			writer.writeAttribute("style", "display:none;", null);
			
			renderChildren(facesContext, tooltip);

			writer.endElement("div");
		}
	}

	protected void encodeScript(FacesContext facesContext, Tooltip tooltip) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		boolean global = tooltip.isGlobal();
		Object owner = null;
		String clientId = tooltip.getClientId(facesContext);

        writer.startElement("span", tooltip);
        writer.writeAttribute("id", clientId, null);

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

        String delegateId = tooltip.getForDelegate();
        JSONBuilder jb = JSONBuilder.create();
		jb.beginFunction("ice.ace.Tooltip.activate")
	      .beginMap()
          .entry("global", global)
          .entry("id", clientId)
          .entry("displayListener", (tooltip.getDisplayListener() != null || tooltip.getDelegateDisplayListener() != null));

		if (tooltip.isSpeechBubble()) jb.entry("speechBubble", true);
		//Position
		jb.beginMap("position");
		jb.entry("at", mapPosition(tooltip.getTargetPosition()))
				.entry("my", mapPosition(tooltip.getPosition()))
				.endMap();

		encodeClientBehaviors(facesContext, tooltip, jb);
		jb.entry("ariaEnabled", EnvUtils.isAriaEnabled(facesContext));
		String style = tooltip.getStyle();
		if (style != null) jb.entry("inlineStyle", style);
		String styleClass = tooltip.getStyleClass();
		if (styleClass != null) jb.entry("styleClass",styleClass);

		if(!global && delegateId == null) {
			owner = getTarget(facesContext, tooltip);
			if (owner instanceof ArrayList) {
				jb.beginArray("forComponents");
				ArrayList<String> clientIds = (ArrayList<String>) owner;
				int size = clientIds.size();
				for (int i = 0; i < size; i++) {
					jb.item(clientIds.get(i));
				}
				jb.endArray();
			} else {
				jb.entry("forComponent", (String) owner);
			}
		} else if (delegateId != null) {
			UIComponent delegateComponent = CoreComponentUtils.findComponentInView(facesContext.getViewRoot(), delegateId);
			if (delegateComponent != null && delegateComponent instanceof Delegate) {
				jb.entry("forDelegate", delegateComponent.getClientId(facesContext));
				String _for = tooltip.getFor();
				if (_for != null) {
					if (_for.indexOf(",") > -1) {
						jb.beginArray("forComponents");
						String[] forIds = _for.split(",");
						for (int i = 0; i < forIds.length; i++) {
							String forId = forIds[i].trim();
							jb.item(forId);
						}
						jb.endArray();
					} else {
						jb.entry("forComponent", _for);
					}
				} else {
					throw new FacesException("No 'for' attribute was specified for ace:tooltip component with ID \"" + tooltip.getId() + "\".");
				}
			} else {
				throw new FacesException("Cannot find delegate component \"" + delegateId + "\" in view or it is not an instance of <ace:delegate>.");
			}
		}
		jb.endMap();

		if(tooltip.getValue() == null)
			jb.item("");
		else {
			jb.item(ComponentUtils.getStringValueToRender(facesContext, tooltip).replaceAll("'", "\\\\'"));
		}

		//Events
		jb.beginArray()
				.item(tooltip.getShowEvent())
				.item(tooltip.getShowDelay())
				.item(createShowEffectFunction(tooltip.getShowEffect()))
				.item(tooltip.getShowEffectLength())
				.endArray();

		jb.beginArray()
				.item(tooltip.getHideEvent())
				.item(tooltip.getHideDelay())
				.item(createHideEffectFunction(tooltip.getHideEffect()))
				.item(tooltip.getHideEffectLength())
				.endArray();

		jb.endFunction();
		writer.write(jb.toString());

        writer.endElement("script");
        writer.endElement("span");

		// determine if display should be cancelled
		if (tooltip.isCancelDisplay()) {
			JavaScriptRunner.runScript(facesContext, "ice.ace.Tooltip.instances['"+clientId+"'].cancelDisplay();");
			tooltip.setCancelDisplay(false);
		}
	}

	protected Object getTarget(FacesContext facesContext, Tooltip tooltip) {
		if(tooltip.isGlobal())
			return null;
		else {
			String _for = tooltip.getFor();

			String forElement = tooltip.getForElement();
			if(_for != null) {
				if (_for.indexOf(",") > -1) { // multiple component ID's
					ArrayList<String> clientIds = new ArrayList<String>();
					String[] forIds = _for.split(",");
					for (int i = 0; i < forIds.length; i++) {
						String forId = forIds[i].trim();
						UIComponent forComponent = tooltip.findComponent(forId);
						if(forComponent == null) {
							String containerId = tooltip.getForContainer();
							UIComponent container = null;
							if (containerId != null) {
								container = CoreComponentUtils.findComponentInView(facesContext.getViewRoot(), containerId);
							}
							if (container != null) {
								clientIds.addAll(collectClientIds(facesContext, container, forId));
							} else {
								throw new FacesException("Cannot find component \"" + forId + "\" in view.");
							}
						} else {
							clientIds.add(forComponent.getClientId(facesContext));
						}
					}
					return clientIds;
				} else { // single component ID
					UIComponent forComponent = tooltip.findComponent(_for);
					if(forComponent == null) {
						String containerId = tooltip.getForContainer();
						UIComponent container = null;
						if (containerId != null) {
							container = CoreComponentUtils.findComponentInView(facesContext.getViewRoot(), containerId);
						}
						if (container != null) {
							return collectClientIds(facesContext, container, _for);
						} else {
							throw new FacesException("Cannot find component \"" + _for + "\" in view.");
						}
					} else {
						return forComponent.getClientId(facesContext);
					}
				}

			} else if(forElement != null) {
				return forElement;
			} else {
				return tooltip.getParent().getClientId(facesContext);
			}
		}
	}

	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Rendering happens on encodeEnd
	}

	public boolean getRendersChildren() {
		return true;
	}
	
	private String mapPosition(String position) {
		if (position != null) {
			position = position.toLowerCase();
			String result = positionsMap.get(position);
			if (result != null)
				return result;
			else
				return "";
		} else {
			return "";
		}
	}
	
	private String createShowEffectFunction(String effect) {
		if ("fade".equalsIgnoreCase(effect)) 
			return "fadeIn";
		if ("slide".equalsIgnoreCase(effect))
			return "slideDown";
		if ("grow".equalsIgnoreCase(effect))
			return "show";
		return "show";
	}
	
	private String createHideEffectFunction(String effect) {
		if ("fade".equalsIgnoreCase(effect)) 
			return "fadeOut";
		if ("slide".equalsIgnoreCase(effect))
			return "slideUp";
		if ("grow".equalsIgnoreCase(effect))
			return "hide";
		return "hide";
	}
		
	private ArrayList<String> collectClientIds(FacesContext context, UIComponent container, String id) {
	
		ArrayList<String> clientIds = new ArrayList<String>();
		container.visitTree(VisitContext.createVisitContext(context, null, EnumSet.of(VisitHint.SKIP_TRANSIENT, VisitHint.SKIP_UNRENDERED)),
			new IdCollectionVisitCallback(clientIds, id));
		return clientIds;
	}
	
	private class IdCollectionVisitCallback implements VisitCallback {
	
		private ArrayList<String> clientIds;
		private String id;
		
		private IdCollectionVisitCallback(ArrayList<String> clientIds, String id) {
			this.clientIds = clientIds;
			this.id = id;
		}
	
		public VisitResult visit(VisitContext context, UIComponent target) {
		
			if (this.id.equals(target.getId())) {
				this.clientIds.add(target.getClientId());
			}
			
			return VisitResult.ACCEPT;
		}
	}
	
	private Object retrieveData(FacesContext context, UIComponent delegate, String activeComponentId, String expression) {
		DataRetrievalVisitCallback callback = new DataRetrievalVisitCallback(activeComponentId, expression);
		delegate.visitTree(VisitContext.createVisitContext(context, null, EnumSet.of(VisitHint.SKIP_TRANSIENT, VisitHint.SKIP_UNRENDERED)), callback);		
		return callback.data;
	}
	
	private static class DataRetrievalVisitCallback implements VisitCallback {
		private String clientId;
		private String expression;
		private Object data;
		
		private DataRetrievalVisitCallback(String clientId, String expression) {
			this.clientId = clientId;
			this.expression = expression;
			this.data = null;
		}
	
		public VisitResult visit(VisitContext context, UIComponent target) {
		
			if (this.clientId.equals(target.getClientId())) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				this.data = facesContext.getApplication().evaluateExpressionGet(facesContext, expression, Object.class);
				return VisitResult.COMPLETE;
			}
			return VisitResult.ACCEPT;	
		}
	}
}