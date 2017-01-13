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

package org.icefaces.ace.component.linkbutton;

import org.icefaces.ace.component.PassthroughAttributes;
import org.icefaces.ace.resources.ACEResourceNames;
import org.icefaces.resources.ICEResourceDependencies;
import org.icefaces.resources.ICEResourceDependency;

import org.icefaces.ace.meta.annotation.Component;
import org.icefaces.ace.meta.baseMeta.UICommandMeta;
import org.icefaces.ace.meta.annotation.Implementation;
import org.icefaces.ace.meta.annotation.Property;

import org.icefaces.ace.meta.annotation.ClientBehaviorHolder;
import org.icefaces.ace.meta.annotation.ClientEvent;
import org.icefaces.ace.api.IceClientBehaviorHolder;
import org.icefaces.resources.ICEResourceLibrary;

@Component(
        tagName         = "linkButton",
        componentClass  = "org.icefaces.ace.component.linkbutton.LinkButton",
        rendererClass   = "org.icefaces.ace.component.linkbutton.LinkButtonRenderer",
        generatedClass  = "org.icefaces.ace.component.linkbutton.LinkButtonBase",
        extendsClass    = "javax.faces.component.UICommand",
        componentType   = "org.icefaces.ace.component.LinkButton",
        rendererType    = "org.icefaces.ace.component.LinkButtonRenderer",
        componentFamily = "org.icefaces.ace.LinkButton",
		tlddoc = "The Link Button is a component that functions like an HTML link." +
                "<p>For more information, see the <a href=\"http://wiki.icefaces.org/display/ICE/LinkButton\">LinkButton Wiki Documentation</a>."
)
@ICEResourceLibrary(ACEResourceNames.ACE_LIBRARY)
@ICEResourceDependencies({
	@ICEResourceDependency(name = "util/ace-core.js"),
	@ICEResourceDependency(name = "jquery/jquery.js"),
	@ICEResourceDependency(name = "linkbutton/linkbutton.js")
})
@ClientBehaviorHolder(events = {
	@ClientEvent(name="action", javadoc="Fired when the button is clicked or pressed by any other means (default event).", tlddoc="Fired when the button is clicked or pressed by any other means (default event).", defaultRender="@all", defaultExecute="@this")
}, defaultEvent="action")
@PassthroughAttributes({
        "charset",
        "coords",
        "dir",
        "rel",
        "rev",
        "shape",
        "title",
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
        "onfocus"
})
public class LinkButtonMeta extends UICommandMeta {

    @Property(tlddoc = "Href attribute of the anchor element. If specified and actionListener is absent, linkButton works " +
                       "as a normal anchor. If specified and actionListener is present, linkButton works " +
                       "as AJAX event source, but href may be opened in a new tab or window.")
    private String href;

    @Property(tlddoc ="Standard HTML href language attribute.")
    private String hrefLang;

    @Property (defaultValue="false", tlddoc="If true, clicking the button does not send a request to the server, and also no page is loaded if href attribute was specified.")
    private boolean disabled;

    @Property (tlddoc="This property defines the link text visible in the component.", implementation= Implementation.GENERATE,
    defaultValue="Default Anchor Label")
    private Object value; 

    @Property (tlddoc="Tabindex of the component.")
    private Integer tabindex;

    @Property(tlddoc="The CSS style class of the component, rendered on the root div of the component.")
    private String styleClass;

    @Property(tlddoc="The inline style of the component, rendered on the root div of the component.")
    private String style;

    @Property(tlddoc="If the link is a traditional anchor then this is the traditional target attribute.")
    private String target; 

    @Property (defaultValue="button", tlddoc="Specifies the button type. The possible values are 'button', 'clear' and 'reset'. The 'button' type is the original and standard mode, where the button submits the form when activated or opens a link. If the type is 'clear', the component acts in a special way: the button will act in a client-side mode, clearing all fields and components in the form it is contained in. If the type is 'reset', the component will reset all the input elements and components to their original values at the time they were loaded. The button will not submit the form in the 'clear' and 'reset' modes, unless the component has a listener or an ajax event attached to it.")
    private String type;

	@Property(tlddoc = "Access key that, when pressed, transfers focus to this component.")
	String accesskey;
}
