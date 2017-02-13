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
 * Code Modification 2: [ADD BRIEF DESCRIPTION HERE]
 * Contributors: ______________________
 * Contributors: ______________________
 */
package org.icefaces.ace.util;

import org.icefaces.component.Focusable;
import org.icefaces.impl.util.CoreUtils;
import org.icefaces.ace.api.ButtonGroupMember;
import org.icefaces.ace.component.buttongroup.ButtonGroup;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.ProjectStage;
import javax.faces.component.*;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.faces.application.FacesMessage;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComponentUtils {

    private final static Logger logger = Logger.getLogger(ComponentUtils.class.getName());

    /**
     * Algorithm works as follows;
     * - If it's an input component, submitted value is checked first since it'd be the value to be used in case validation errors
     * terminates jsf lifecycle
     * - Finally the value of the component is retrieved from backing bean and if there's a converter, converted value is returned
     * <p/>
     * - If the component is not a value holder, toString of component is used to support Facelets UIInstructions.
     *
     * @param facesContext FacesContext instance
     * @param component    UIComponent instance whose value will be returned
     * @return End text
     */
    public static String getStringValueToRender(FacesContext facesContext, UIComponent component) {
        if (component instanceof ValueHolder) {

            if (component instanceof EditableValueHolder) {
                Object submittedValue = ((EditableValueHolder) component).getSubmittedValue();
                if (submittedValue != null) {
                    return submittedValue.toString();
                }
            }

            ValueHolder valueHolder = (ValueHolder) component;
            Object value = valueHolder.getValue();
            if (value == null)
                return "";

            //first ask the converter
            if (valueHolder.getConverter() != null) {
                return valueHolder.getConverter().getAsString(facesContext, component, value);
            }
            //Try to guess
            else {
                ValueExpression expr = component.getValueExpression("value");
                if (expr != null) {
                    Class<?> valueType = expr.getType(facesContext.getELContext());
                    if (valueType != null) {
                        Converter converterForType = facesContext.getApplication().createConverter(valueType);

                        if (converterForType != null)
                            return converterForType.getAsString(facesContext, component, value);
                    }
                }
            }

            //No converter found just return the value as string
            return value.toString();
        } else {
            //This would get the plain texts on UIInstructions when using Facelets
            String value = component.toString();

            if (value != null)
                return value.trim();
            else
                return "";
        }
    }


    /**
     * Attempts to return the property accessed in a simple EL expression of the form
     * ${someObject.property}.
     *
     * @param expression An EL expression to parse the property from.
     * @return The property name of the EL expression.
     */
    public static String resolveField(ValueExpression expression) {
        String expressionString = expression.getExpressionString();
        return expressionString.substring(expressionString.indexOf(".") + 1, expressionString.length() - 1);
    }

    public static UIComponent findParentForm(FacesContext context, UIComponent component) {
        UIComponent parent = component;
        while (parent != null)
            if (parent instanceof UIForm) break;
            else parent = parent.getParent();

        return parent;
    }

    public static String findClientIds(FacesContext context, UIComponent component, String list) {
        if (list == null) return "@none";

        String[] ids = list.split("[,\\s]+");
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < ids.length; i++) {
            if (i != 0) buffer.append(" ");
            String id = ids[i].trim();
            //System.out.println("ComponentUtils.findClientIds()    ["+i+"]  id: " + id);

            if (id.equals("@all") || id.equals("@none")) {
                //System.out.println("ComponentUtils.findClientIds()    ["+i+"]  " + id);
                buffer.append(id);
            } else if (id.equals("@this")) {
                //System.out.println("ComponentUtils.findClientIds()    ["+i+"]  @this  : " + component.getClientId(context));
                buffer.append(component.getClientId(context));
            } else if (id.equals("@parent")) {
                //System.out.println("ComponentUtils.findClientIds()    ["+i+"]  @parent: " + component.getParent().getClientId(context));
                buffer.append(component.getParent().getClientId(context));
            } else if (id.equals("@form")) {
                UIComponent form = ComponentUtils.findParentForm(context, component);
                if (form == null)
                    throw new FacesException("Component " + component.getClientId(context) + " needs to be enclosed in a form");

                buffer.append(form.getClientId(context));
            } else {
                UIComponent comp = component.findComponent(id);

                //For portlets, if the standard search doesn't work, it may be necessary to do an absolute search
                //which requires including the portlet's namespace. So the resulting encoded id looks something
                //like portletNamespace:container:componentId.  We make the search absolute by pre-pending
                //a leading colon (:).
                if (comp == null) {
                    String encodedId = encodeNameSpace(context, id);
                    if (!encodedId.startsWith(":")) {
                        encodedId = ":" + encodedId;
                    }
                    comp = component.findComponent(encodedId);
//                    System.out.println("ComponentUtils.findClientIds()   ["+i+"]  comp   : " + (comp == null ? "null" : comp.getClientId(context)) + "  id: " + encodedId);
                }

                if (comp != null) {
                    buffer.append(comp.getClientId(context));
                } else {
                    if (context.getApplication().getProjectStage().equals(ProjectStage.Development)) {
                        logger.log(Level.INFO, "Cannot find component with identifier \"{0}\" in view.", id);
                    }
                    buffer.append(id);
                }
            }
        }

        return buffer.toString();
    }

    /**
     * Environments like portlets need to namespace the components in order to uniquely identify them
     * on the page in case there are multiple instances of the same portlet or different portlets that use
     * the same ids.  This method will prepend the namespace properly taking care to ensure that the
     * namespace is not added twice and that a colon (:) is added if necessary.
     *
     * @param fc The current FacesContext instance
     * @param id The id to encode
     * @return The namespace encoded id
     */
    public static String encodeNameSpace(FacesContext fc, String id) {

        if (id == null || id.trim().length() == 0) {
            return id;
        }

        String tempId = id;
        ExternalContext ec = fc.getExternalContext();
        String encodedId = ec.encodeNamespace(tempId);

        //If no namespace was applied, we're done.
        if (encodedId.equals(id)) {
            return id;
        }

        //Extract the actual namespace.
        int idStart = encodedId.indexOf(id);
        String ns = encodedId.substring(0, idStart);

        //Check if the id already had the namespace.  If so, we're done.
        if (id.startsWith(ns)) {
            return id;
        }

        //If necessary, add the separator character before including the namespace.
        String sep = String.valueOf(UINamingContainer.getSeparatorChar(fc));
        if (!id.startsWith(sep)) {
            id = ":" + id;
        }

        //Add the namespace.
        id = ns + id;
        return id;
    }

    public static UIComponent findComponent(UIComponent base, String id) {
        return CoreUtils.findComponentById(base, id);
    }

    public static void enableOnElementUpdateNotify(ResponseWriter writer, String id) throws IOException {
        CoreUtils.enableOnElementUpdateNotify(writer, id);
    }

    /**
     * used by GrowlMessagesRenderer and MessagesRenderer for option @inView
     *
     * @param context
     * @return list of ids in view
     */
    public static List<String> findIdsInView(FacesContext context) {
        List<String> idList = new ArrayList<String>();
        UIViewRoot root = context.getViewRoot();
        for (UIComponent uic : root.getChildren()) {
            getChildIds(uic, idList, context);
        }
        return idList;
    }

    /**
     * @param uic
     * @param idList
     * @param context
     */
    private static void getChildIds(UIComponent uic, List<String> idList, FacesContext context) {
        idList.add(uic.getClientId(context));
        if (uic.getChildren().size() > 0) {
            Iterator<UIComponent> iter = uic.getFacetsAndChildren();
            while (iter.hasNext()) {
                UIComponent child = iter.next();
                //   if (child instanceof UIInput){ do we want to just include UIINput or EditableValueHolders?
                getChildIds(child, idList, context);
                //  }
            }
        }
    }

    /**
     * used by GrowlMessagesRenderer and MessagesRenderer for option @inView
     *
     * @param context
     * @param idsInView
     * @return messages in view iterator
     */
    public static Iterator<FacesMessage> getMessagesInView(FacesContext context, List<String> idsInView) {
        List<FacesMessage> compiledList = new ArrayList<FacesMessage>();
        for (String id : idsInView) {
            if (context.getMessages(id).hasNext()) {
                Iterator msgIt = context.getMessages(id);
                while (msgIt.hasNext()) {
                    FacesMessage fm = (FacesMessage) msgIt.next();
                    //    System.out.println(" adding message="+fm.getSummary() +" for id="+id);
                    compiledList.add(fm);
                }
            }
        }
        return compiledList.iterator();
    }

    public static String getFocusedElementId(UIComponent component) {
        if (component instanceof Focusable) {
            Focusable f = (Focusable) component;
            return f.getFocusedElementId();
        } else {
            return "";
        }
    }

    /**
     * A buttonGroup will put itself in the context while its children are rendering and
     * automatically the groupId will set itself.  Any radioButton/checkboxButton that is a descendant of a
     * buttonGroup component inherits it as a groupId.
     *
     * @param button
     * @param context
     * @return the buttonGroup id
     */
    public static String findInHeirarchy(ButtonGroupMember button, FacesContext context) {
        String groupId = "";
        Object oId = context.getAttributes().get(ButtonGroup.GROUP_PARENT_ID);
        if (oId != null) {
            groupId = String.valueOf(oId);
        }
        return groupId;
    }

    /**
     * This method will look in Context attributes for the buttonGroups registered in the view
     *
     * @param button
     * @param fc
     * @return the groupList
     */
    public static List<String> findInFacesContext(ButtonGroupMember button, FacesContext fc) {
        List<String> groupList = new ArrayList<String>();
        Object olist = fc.getAttributes().get(ButtonGroupMember.GROUP_LIST_KEY);
        if (olist != null) {
            if (olist instanceof List) {
                groupList = (List<String>) fc.getAttributes().get(ButtonGroupMember.GROUP_LIST_KEY);
            }
        }
        return groupList;
    }

    /**
     * Render pass-through attributes for the given component.
     *
     * @param writer
     * @param component
     * @param attrs
     * @throws IOException
     */
    public static void renderPassThroughAttributes(ResponseWriter writer, UIComponent component, String[] attrs) throws IOException {
        for (String attribute : attrs) {
            Object value = component.getAttributes().get(attribute);

            if (shouldRenderAttribute(value)) {
                writer.writeAttribute(attribute, value.toString(), attribute);
            }
        }
    }

    /**
     * Render pass-through attribute for the given component.
     * @param writer
     * @param component
     * @param attribute
     * @param initialValue
     * @throws IOException
     */
    public static void renderPassThroughAttribute(ResponseWriter writer, UIComponent component, String attribute, String initialValue) throws IOException {
        final Object value = component.getAttributes().get(attribute);
        final Object finalValue;
        if (initialValue == null || "".equals(initialValue)) {
            finalValue = value;
        } else {
            if (value == null) {
                finalValue = initialValue;
            }else {
                finalValue = initialValue + value;
            }
        }
        if (shouldRenderAttribute(finalValue)) {
            writer.writeAttribute(attribute, finalValue, attribute);
        }
    }

    /**
     * Render pass-through attribute for the given component.
     * @param writer
     * @param component
     * @param attribute
     * @throws IOException
     */
    public static void renderPassThroughAttribute(ResponseWriter writer, UIComponent component, String attribute) throws IOException {
        renderPassThroughAttribute(writer, component, attribute, "");
    }


    private static boolean shouldRenderAttribute(Object value) {
        if (value == null)
            return false;

        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        } else if (value instanceof Number) {
            Number number = (Number) value;

            if (value instanceof Integer)
                return number.intValue() != Integer.MIN_VALUE;
            else if (value instanceof Double)
                return number.doubleValue() != Double.MIN_VALUE;
            else if (value instanceof Long)
                return number.longValue() != Long.MIN_VALUE;
            else if (value instanceof Byte)
                return number.byteValue() != Byte.MIN_VALUE;
            else if (value instanceof Float)
                return number.floatValue() != Float.MIN_VALUE;
            else if (value instanceof Short)
                return number.shortValue() != Short.MIN_VALUE;
        } else if (value instanceof String) {
            return ((String) value).length() > 0;
        }

        return true;
    }


    /**
       * Not all text for components are easy to make attributes for components,
       * especially those for accessibility.  Default values are made available
       * within the ace jar messages properties files.  Users may override the
       * key if they prefer their own, or their locale is not available.
       * @return reference to resource bundle to get localised text for rendering
       */
    public static ResourceBundle getComponentResourceBundle(FacesContext context, String ACE_MESSAGES_BUNDLE){
          Locale locale = context.getViewRoot().getLocale();
          ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
          String bundleName = context.getApplication().getMessageBundle();
          if (classLoader == null) {
              classLoader = bundleName.getClass().getClassLoader();
          }
          if (bundleName == null) {
              bundleName = ACE_MESSAGES_BUNDLE;
          }
        return ResourceBundle.getBundle(bundleName, locale, classLoader);
    }

      /**
       *
       * @param bundle
       * @param MESSAGE_KEY_PREFIX
       * @param key
       * @param defaultValue
       * @return localized or internationalized String value from message bundle
       */
    public static String getLocalisedMessageFromBundle(ResourceBundle bundle,
             String MESSAGE_KEY_PREFIX,
             String key,
             String defaultValue){
        if (null == bundle) {
            return defaultValue;
        }
        String label=defaultValue;
        try {
            label = bundle.getString(MESSAGE_KEY_PREFIX + key);
        } catch(MissingResourceException mre){
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(" BUNDLE missing property : "+key+" defaultValue used : "+defaultValue);
            }
        }
        return label;
    }
}