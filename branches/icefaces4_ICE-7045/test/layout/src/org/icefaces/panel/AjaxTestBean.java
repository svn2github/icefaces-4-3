package org.icefaces.panel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import org.icefaces.ace.event.CloseEvent;
import org.icefaces.ace.event.ToggleEvent;
import org.icefaces.util.JavaScriptRunner;
import java.util.*;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class AjaxTestBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int NUM_INPUT_FIELDS = 12;

    private int listenerCtr = 0;
    private static int ajaxCtr = 0;
    private String listenerMsg;

    public AjaxTestBean() {
        fields = new ArrayList<InputTextBinding>();
        for (int i = 0; i < NUM_INPUT_FIELDS; i++) {
            fields.add(new InputTextBinding());
        }
    }

    private List<InputTextBinding> fields;

    public List<InputTextBinding> getFields() {
        return fields;
    }

    public void clear() {
        for (int i = 0; i < NUM_INPUT_FIELDS; i++) {
            fields.get(i).getBinding().setValue("");
        }
        listenerMsg = "";
        listenerCtr = 0;
        ajaxCtr = 0;
    }

    public static class InputTextBinding {

        private static HtmlInputText getNewInputText() {
            return new HtmlInputText() {
                public void	decode(FacesContext context) {
                    super.decode(context);
                    String oldValue = (String) this.getValue();
                    if (oldValue == null) oldValue = "";
                    setSubmittedValue(oldValue + "executed");
                }
            };
        }

        public InputTextBinding() { }

        private HtmlInputText binding = null;

        public HtmlInputText getBinding() {
            if (binding == null) {
                binding = getNewInputText();
            }
            return binding;
        }

        public void setBinding(HtmlInputText binding) {
            this.binding = binding;
        }
    }

    public String getListenerMsg() {
        return listenerMsg;
    }

    public void setListenerMsg(String listenerMsg) {
        this.listenerMsg = listenerMsg;
    }

    public void toggleEventListener(ToggleEvent event) {
        ajaxCtr++;
        generateAjaxRequestReport();
    }

    public void closeEventListener(CloseEvent event) {
        ajaxCtr++;
        generateAjaxRequestReport();
    }

    public static void generateAjaxRequestReport() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();

        String ajaxParam = params.get("javax.faces.partial.ajax");
        if (ajaxParam == null) ajaxParam = "(not set)";
        /*String eventParam = params.get("javax.faces.partial.event");
        if (eventParam == null) eventParam = "(not set)";*/
        String executeParam = params.get("javax.faces.partial.execute");
        if (executeParam == null) executeParam = "(not set)";
        String renderParam = params.get("javax.faces.partial.render");
        if (renderParam == null) renderParam = "(not set)";
        String behaviorParam = params.get("javax.faces.behavior.event");
        if (behaviorParam == null) behaviorParam = "(not set)";

        JavaScriptRunner.runScript(context,
                "var body = document.getElementsByTagName('body')[0];" +
                        "var mainDiv = document.getElementById('ajax_report');" +
                        "if (mainDiv != null) body.removeChild(mainDiv);" +

                        // main div
                        "mainDiv = document.createElement('div');" +
                        "mainDiv.setAttribute('id', 'ajax_report');" +
                        "mainDiv.style.position = 'fixed';" +
                        "mainDiv.style.width = '300px';" +
                        "mainDiv.style.height = '150px';" +
                        "mainDiv.style.top = '0';" +
                        "mainDiv.style.right = '0';" +
                        "mainDiv.style.border = '1px #000 solid';" +
                        "mainDiv.style.backgroundColor = '#fff';" +
                        "mainDiv.style.zIndex = '1000';" +

                        // javax.faces.partial.ajax
                        "var div1 = document.createElement('div');" +
                        "var span11 = document.createElement('span');" +
                        "span11.style.fontWeight = 'bold';" +
                        "span11.appendChild(document.createTextNode('javax.faces.partial.ajax '));" +
                        "var span12 = document.createElement('span');" +
                        "span12.setAttribute('id', 'ajax_param');" +
                        "span12.appendChild(document.createTextNode('" + ajaxParam + "'));" +
                        "div1.appendChild(span11);" +
                        "div1.appendChild(span12);" +
                        "mainDiv.appendChild(div1);" +

                        // javax.faces.partial.event
                        /*"var div2 = document.createElement('div');" +
                        "var span21 = document.createElement('span');" +
                        "span21.style.fontWeight = 'bold';" +
                        "span21.appendChild(document.createTextNode('javax.faces.partial.event '));" +
                        "var span22 = document.createElement('span');" +
                        "span22.setAttribute('id', 'event_param');" +
                        "span22.appendChild(document.createTextNode('" + eventParam + "'));" +
                        "div2.appendChild(span21);" +
                        "div2.appendChild(span22);" +
                        "mainDiv.appendChild(div2);" +*/

                        // javax.faces.partial.execute
                        "var div3 = document.createElement('div');" +
                        "var span31 = document.createElement('span');" +
                        "span31.style.fontWeight = 'bold';" +
                        "span31.appendChild(document.createTextNode('javax.faces.partial.execute '));" +
                        "var span32 = document.createElement('span');" +
                        "span32.setAttribute('id', 'execute_param');" +
                        "span32.appendChild(document.createTextNode('" + executeParam + "'));" +
                        "div3.appendChild(span31);" +
                        "div3.appendChild(span32);" +
                        "mainDiv.appendChild(div3);" +

                        // javax.faces.partial.render
                        "var div4 = document.createElement('div');" +
                        "var span41 = document.createElement('span');" +
                        "span41.style.fontWeight = 'bold';" +
                        "span41.appendChild(document.createTextNode('javax.faces.partial.render '));" +
                        "var span42 = document.createElement('span');" +
                        "span42.setAttribute('id', 'render_param');" +
                        "span42.appendChild(document.createTextNode('" + renderParam + "'));" +
                        "div4.appendChild(span41);" +
                        "div4.appendChild(span42);" +
                        "mainDiv.appendChild(div4);" +

                        // javax.faces.behavior.event
                        "var div5 = document.createElement('div');" +
                        "var span51 = document.createElement('span');" +
                        "span51.style.fontWeight = 'bold';" +
                        "span51.appendChild(document.createTextNode('javax.faces.behavior.event '));" +
                        "var span52 = document.createElement('span');" +
                        "span52.setAttribute('id', 'event_param');" +
                        "span52.appendChild(document.createTextNode('" + behaviorParam + "'));" +
                        "div5.appendChild(span51);" +
                        "div5.appendChild(span52);" +
                        "mainDiv.appendChild(div5);" +

                        // ajax listener counter
                        "var div6 = document.createElement('div');" +
                        "var span61 = document.createElement('span');" +
                        "span61.style.fontWeight = 'bold';" +
                        "span61.appendChild(document.createTextNode('ajax listener count '));" +
                        "var span62 = document.createElement('span');" +
                        "span62.setAttribute('id', 'listener_count');" +
                        "span62.appendChild(document.createTextNode('" + ajaxCtr + "'));" +
                        "div6.appendChild(span61);" +
                        "div6.appendChild(span62);" +
                        "mainDiv.appendChild(div6);" +

                        "body.appendChild(mainDiv);"
        );
    }
}