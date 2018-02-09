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

package org.icefaces.samples.showcase.example.ace.borderlayout;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ManagedBean(name= BorderLayoutNestingBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class BorderLayoutNestingBean implements Serializable {
    public static final String BEAN_NAME = "borderLayoutNestingBean";
	public String getBeanName() { return BEAN_NAME; }

	final private static String PATH_BASE = "/resources/examples/ace/borderlayout/";

	private List<Demo> demoList = new ArrayList() {{
		add(new Demo("ace:audioPlayer", "demo1.xhtml",
			"The <ace:audioPlayer> component uses the HTML5 <audio> element to play sounds on web pages.",
			"	<ace:audioPlayer value=\"./resources/media/canary.mp3\"\n" +
			"		 type=\"audio/mpeg\"\n" +
			"	 	 controls=\"true\"/>"));
		add(new Demo("ace:checkboxButton", "demo2.xhtml",
			"The <ace:checkboxButton> component is a simple UI button control for toggling boolean input values.",
			"	<ace:checkboxButton value=\"#{checkboxButton.selected}\">\n" +
			"		<ace:ajax />\n" +
			"	</ace:checkboxButton>\n" +
			"	<br/>\n" +
			"	The checkbox is:\n" +
			"	<h:outputText value=\"#{checkboxButton.boxValueDescription}\"\n" +
			"		style=\"font-weight: bold;\"/>"));
		add(new Demo("ace:colorEntry", "demo3.xhtml",
			"The <ace:colorEntry> component allows the user to input a color value via either an input field or a highly customizable popup color selection dialog.",
			"	<ace:colorEntry title=\"#{colorEntryBean.title}\" />"));
		add(new Demo("ace:dateTimeEntry", "demo4.xhtml",
			"The <ace:dateTimeEntry> component is a calendar component used to capture date entry.",
			"	<ace:dateTimeEntry timeZone=\"Canada/Mountain\"\n" +
			"			   pattern=\"MMM/dd/yyyy\" />"));
		add(new Demo("ace:list", "demo5.xhtml",
			"The <ace:list> component renders an Array or List of objects as a styled HTML UL element.",
			"	<ace:list value=\"#{listReorderBean.stringList}\"\n" +
			"			  selectionMode=\"single\"\n" +
			"			  dragging=\"true\"\n" +
			"			  controlsEnabled=\"true\"/>"));
		add(new Demo("ace:panel", "demo6.xhtml",
			"The <ace:panel> component is a generic layout container that supports customizable header / footers, accordion toggling, open and close popup features as well as embedded menu operations.",
			"	<ace:panel header=\"Panel Header\"\n" +
			"			   toggleable=\"true\">\n" +
			"		<p>This panel can be toggled.</p>\n" +
			"	</ace:panel>"));
		add(new Demo("ace:pushButton", "demo7.xhtml",
			"The <ace:pushButton> component performs basic button functionality and can be used for executing listeners, action navigation and form submission.",
			"	<ace:pushButton value=\"Press me\" />"));
		add(new Demo("ace:sliderEntry", "demo8.xhtml",
			"The <ace:sliderEntry> allows a user to enter an input value by dragging or clicking on the slider path.",
			"	<ace:sliderEntry axis=\"x\"\n" +
			"		 length=\"200\"\n" +
			"		 showLabels=\"true\"\n" +
			"		 min=\"0\"\n" +
			"		 max=\"100\"/>"));
		add(new Demo("ace:textAreaEntry", "demo9.xhtml",
			"The <ace:textAreaEntry> component is a text input component based on the <textarea> element.",
			"	<ace:textAreaEntry label=\"Comments\" labelPosition=\"inField\" />"));
		add(new Demo("ace:videoPlayer", "demo10.xhtml",
			"The <ace:videoPlayer> component uses the HTML5 <video> element to play videos on web pages.",
			"	<ace:videoPlayer value=\"./resources/media/spinningdancer.mp4\"\n" +
			"				 type=\"video/mp4\"\n" +
			"				 autoplay=\"true\"\n" +
			"				 controls=\"true\"\n" +
			"				 loop=\"true\"/>"));
	}};

	public List<Demo> getDemoList() { return demoList; }

	public BorderLayoutNestingBean() {
		
	}

	private String currentPath = PATH_BASE + "landing.xhtml";
	public String getCurrentPath() {
		return currentPath;
	}

	private String currentInfo = "Information about the component will appear here. This pane is resizable.";
	public String getCurrentInfo() {
		return currentInfo;
	}

	private String currentSource = "n/a";
	public String getCurrentSource() {
		return currentSource;
	}

	public void selectMenuItem() {

        Map<String, String> requestParameterMap = 
			FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

		String indexParam = requestParameterMap.get("BorderLayoutNestingBean_index");
		int index;
		try {
			index = Integer.valueOf(indexParam).intValue();
		} catch (Exception e) {
			index = 0;
		}

		Demo demo = demoList.get(index);

		currentPath = PATH_BASE + demo.getPath();
		currentInfo = demo.getInfo();
		currentSource = demo.getSource();
	}

	public static class Demo {

		private String name = "";
		public String getName() { return name; }

		private String path = "";
		public String getPath() { return path; }

		private String info = "";
		public String getInfo() { return info; }

		private String source = "";
		public String getSource() { return source; }

		public Demo(String name, String path, String info, String source) {
			this.name = name;
			this.path = path;
			this.info = info;
			this.source = source;
		}
	}
}