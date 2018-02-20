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

package org.icefaces.ace.component.tristatecheckbox;


import org.icefaces.component.PassthroughAttributes;
import org.icefaces.ace.resources.ACEResourceNames;
import org.icefaces.resources.ICEResourceDependencies;
import org.icefaces.resources.ICEResourceDependency;

import org.icefaces.ace.meta.baseMeta.UIInputMeta;
import org.icefaces.ace.meta.annotation.*;
import org.icefaces.resources.ICEResourceLibrary;

@Component(
        tagName        = "triStateCheckbox",
        componentClass = "org.icefaces.ace.component.tristatecheckbox.TriStateCheckbox",
        rendererClass  = "org.icefaces.ace.component.tristatecheckbox.TriStateCheckboxRenderer",
        generatedClass = "org.icefaces.ace.component.tristatecheckbox.TriStateCheckboxBase",
        extendsClass   = "javax.faces.component.UIInput",
        componentType  = "org.icefaces.ace.component.TriStateCheckbox",
        rendererType   = "org.icefaces.ace.component.TriStateCheckboxRenderer",
		componentFamily= "org.icefaces.ace.TriStateCheckbox",
		tlddoc="The TriStateCheckbox a component renders a button that works like a checkbox but with three states: "+
		       "unchecked, checked, and indeterminate. " +
		       "The indeterminate state can have different meanings, depending on the application. " +
		       "The order in which these states cycle is determine by the 'indeterminateBeforeChecked' attribute." +
		       "The possible values are the strings 'checked', 'indeterminate', and 'unchecked'. Any other string or a null value defaults to 'unchecked'. The values are case insensitive." +
               "<p>For more information, see the <a href=\"http://wiki.icefaces.org/display/ICE/TriStateCheckbox\">TriStateCheckbox Wiki Documentation</a>."
        )
@ICEResourceLibrary(ACEResourceNames.ACE_LIBRARY)
@ICEResourceDependencies({
	@ICEResourceDependency(name = "fontawesome/font-awesome.css"),
	@ICEResourceDependency(name = "util/ace-core.js"),
	@ICEResourceDependency(name = "jquery/jquery.js"),
	@ICEResourceDependency(name = "tristatecheckbox/tristatecheckbox.js")
})
@ClientBehaviorHolder(events = {
	@ClientEvent(name="action",
            javadoc="Fired when the button is clicked or pressed by any other means (default event).",
            tlddoc="Fired when the button is clicked or pressed by any other means (default event).",
            defaultRender="@all", defaultExecute="@this")
}, defaultEvent="action")
@PassthroughAttributes({
        "alt",
        "dir",
        "lang",
        "title",
        "type",
        "onclick",
        "ondblclick",
        "onkeydown",
        "onkeypress",
        "onkeyup",
        "onmousedown",
        "onmousemove",
        "onmouseout",
        "onmouseover",
        "onmouseup",
        "onblur",
        "onfocus",
        "onchange",
        "onselect"
})
public class TriStateCheckboxMeta extends UIInputMeta {

    @Property(tlddoc="Label to be displayed on the button.")
    private String label;

    @Property(tlddoc = "Position of label relative to the checkbox button. Supported values are \"inField/left/right/top/bottom/none\".", defaultValue="inField")
    private String labelPosition;

    @Property(tlddoc="The inline style of the component, rendered on the root div of the component.")
	private String style;

    @Property(tlddoc="The CSS style class of the component, rendered on the root div of the component.")
	private String styleClass;

    @Property (tlddoc="Tabindex of the component.")
    private Integer tabindex;

    @Property (defaultValue="false",
    		tlddoc="If true no input may be submitted via this component.")
    private boolean disabled;

    @Property(tlddoc = "ID of button group component if this button is outside of the button group but wants to logically belong to the group.")
    private String group;

	@Property(tlddoc = "Access key that, when pressed, transfers focus to this component.")
	String accesskey;

	@Property(tlddoc = "If true, the order of the states when this button is activated will be \"unchecked-indeterminate-checked\". Otherwise, the order will be \"unchecked-checked-indeterminate\".", defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
	boolean indeterminateBeforeChecked;
}
