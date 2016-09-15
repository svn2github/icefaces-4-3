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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
     * @param facesContext   FacesContext instance
     * @param component UIComponent instance whose value will be returned
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

    /**
     * Resolves the end text to render by using a specified value
     *
     * @param facesContext   FacesContext instance
     * @param component UIComponent instance whose value will be returned
     * @return End text
     */
    public static String getStringValueToRender(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null) return null;

        ValueHolder valueHolder = (ValueHolder) component;

        Converter converter = valueHolder.getConverter();
        if (converter != null) {
            return converter.getAsString(facesContext, component, value);
        } else {
            ValueExpression expr = component.getValueExpression("value");
            if (expr != null) {
                Class<?> valueType = expr.getType(facesContext.getELContext());
                Converter converterForType = facesContext.getApplication().createConverter(valueType);

                if (converterForType != null)
                    return converterForType.getAsString(facesContext, component, value);
            }
        }

        return value.toString();
    }

    public static UIComponent findParentForm(FacesContext context, UIComponent component) {
        UIComponent parent = component;
        while (parent != null)
            if (parent instanceof UIForm) break;
            else parent = parent.getParent();

        return parent;
    }

    public static void decorateAttribute(UIComponent component, String attribute, String value) {
        String attributeValue = (String) component.getAttributes().get(attribute);

        if (attributeValue != null) {
            if (attributeValue.indexOf(value) == -1) {
                String decoratedValue = attributeValue + ";" + value;

                component.getAttributes().put(attribute, decoratedValue);
            } else {
                component.getAttributes().put(attribute, attributeValue);
            }
        } else {
            component.getAttributes().put(attribute, value);
        }
    }

    public static List<SelectItem> createSelectItems(UIComponent component) {
        List<SelectItem> items = new ArrayList<SelectItem>();
        Iterator<UIComponent> children = component.getChildren().iterator();

        while (children.hasNext()) {
            UIComponent child = children.next();

            if (child instanceof UISelectItem) {
                UISelectItem selectItem = (UISelectItem) child;

                items.add(new SelectItem(selectItem.getItemValue(), selectItem.getItemLabel()));
            } else if (child instanceof UISelectItems) {
                Object selectItems = ((UISelectItems) child).getValue();

                if (selectItems instanceof SelectItem[]) {
                    SelectItem[] itemsArray = (SelectItem[]) selectItems;

                    for (SelectItem item : itemsArray)
                        items.add(new SelectItem(item.getValue(), item.getLabel()));

                } else if (selectItems instanceof Collection) {
                    Collection<SelectItem> collection = (Collection<SelectItem>) selectItems;

                    for (SelectItem item : collection)
                        items.add(new SelectItem(item.getValue(), item.getLabel()));
                }
            }
        }

        return items;
    }

    public static String idTojQuerySelector(String id) {
        return "#" + id.replaceAll(":", "\\\\\\\\:");
    }

    public static String findClientIds(FacesContext context, UIComponent component, String list) {
        if (list == null) return "@none";

        //System.out.println("ComponentUtils.findClientIds()  component.clientId: " + component.getClientId(context) + "  list: " + list);

        String[] ids = list.split("[,\\s]+");
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < ids.length; i++) {
            if (i != 0) buffer.append(" ");
            String id = ids[i].trim();
            //System.out.println("ComponentUtils.findClientIds()    ["+i+"]  id: " + id);

            if (id.equals("@all") || id.equals("@none")) {
                //System.out.println("ComponentUtils.findClientIds()    ["+i+"]  " + id);
                buffer.append(id);
            }
            else if (id.equals("@this")) {
                //System.out.println("ComponentUtils.findClientIds()    ["+i+"]  @this  : " + component.getClientId(context));
                buffer.append(component.getClientId(context));
            }
            else if (id.equals("@parent")) {
                //System.out.println("ComponentUtils.findClientIds()    ["+i+"]  @parent: " + component.getParent().getClientId(context));
                buffer.append(component.getParent().getClientId(context));
            }
            else if (id.equals("@form")) {
                UIComponent form = ComponentUtils.findParentForm(context, component);
                if (form == null)
                    throw new FacesException("Component " + component.getClientId(context) + " needs to be enclosed in a form");

                buffer.append(form.getClientId(context));
            }
            else {
                UIComponent comp = component.findComponent(id);

                //For portlets, if the standard search doesn't work, it may be necessary to do an absolute search
                //which requires including the portlet's namespace. So the resulting encoded id looks something
                //like portletNamespace:container:componentId.  We make the search absolute by pre-pending
                //a leading colon (:).
                if(comp == null){
                    String encodedId = encodeNameSpace(context,id);
                    if( !encodedId.startsWith(":")){
                        encodedId = ":" + encodedId;
                    }
                    comp = component.findComponent(encodedId);
//                    System.out.println("ComponentUtils.findClientIds()   ["+i+"]  comp   : " + (comp == null ? "null" : comp.getClientId(context)) + "  id: " + encodedId);
                }

                if (comp != null) {
                    buffer.append(comp.getClientId(context));
                }
                else {
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
    public static String encodeNameSpace(FacesContext fc, String id){

        if( id == null || id.trim().length() == 0){
            return id;
        }

        String tempId = id;
        ExternalContext ec = fc.getExternalContext();
        String encodedId = ec.encodeNamespace(tempId);

        //If no namespace was applied, we're done.
        if( encodedId.equals(id)){
            return id;
        }

        //Extract the actual namespace.
        int idStart = encodedId.indexOf(id);
        String ns = encodedId.substring(0,idStart);

        //Check if the id already had the namespace.  If so, we're done.
        if( id.startsWith(ns) ){
            return id;
        }

        //If necessary, add the separator character before including the namespace.
        String sep = String.valueOf(UINamingContainer.getSeparatorChar(fc));
        if( !id.startsWith(sep)){
            id = ":" + id;
        }

        //Add the namespace.
        id = ns + id;
        return id;
    }

    public static String findComponentClientId(String id) {
        UIComponent component = null;

        FacesContext facesContext = FacesContext.getCurrentInstance();
        component = findComponent(facesContext.getViewRoot(), id);

        return component.getClientId(facesContext);
    }

    public static UIComponent findComponent(UIComponent base, String id) {
        return CoreUtils.findComponentById(base, id);
    }

    public static boolean isLiteralText(UIComponent component) {
        return component.getFamily().equalsIgnoreCase("facelets.LiteralText");
    }

    public static void enableOnElementUpdateNotify(ResponseWriter writer, String id) throws IOException {
        CoreUtils.enableOnElementUpdateNotify(writer, id);
    }

    /**
     * used by GrowlMessagesRenderer and MessagesRenderer for option @inView
     * @param context
     * @return
     */
    public static List<String> findIdsInView(FacesContext context){
        List<String> idList = new ArrayList<String>();
        UIViewRoot root =  context.getViewRoot();
        for (UIComponent uic : root.getChildren()){
            getChildIds(uic,idList,context);
        }
        return idList;
    }

    /**
     *
     * @param uic
     * @param idList
     * @param context
     */
    private static void  getChildIds(UIComponent uic, List<String> idList, FacesContext context) {
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
     * @param context
     * @param idsInView
     * @return
     */
    public static Iterator<FacesMessage> getMessagesInView(FacesContext context, List<String> idsInView){
        List<FacesMessage> compiledList = new ArrayList<FacesMessage>();
        for (String id: idsInView ){
            if (context.getMessages(id).hasNext() ){
                Iterator msgIt =  context.getMessages(id);
                while(msgIt.hasNext()){
                    FacesMessage fm = (FacesMessage)msgIt.next();
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
}