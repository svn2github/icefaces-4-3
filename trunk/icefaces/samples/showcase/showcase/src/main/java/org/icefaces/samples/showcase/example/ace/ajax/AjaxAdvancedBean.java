/*
 * Copyright 2004-2013 ICEsoft Technologies Canada Corp.
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

package org.icefaces.samples.showcase.example.ace.ajax;

import org.icefaces.samples.showcase.metadata.annotation.*;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;

import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;

@ComponentExample(
        parent = AjaxBean.BEAN_NAME,
        title = "example.ace.ajax.advanced.title",
        description = "example.ace.ajax.advanced.description",
        example = "/resources/examples/ace/ajax/ajaxAdvanced.xhtml"
)
@ExampleResources(
        resources ={
            // xhtml
            @ExampleResource(type = ResourceType.xhtml,
                    title="ajaxAdvanced.xhtml",
                    resource = "/resources/examples/ace/ajax/ajaxAdvanced.xhtml"),
            // Java Source
            @ExampleResource(type = ResourceType.java,
                    title="AjaxAdvancedBean.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/showcase"+
                    "/example/ace/ajax/AjaxAdvancedBean.java")
        }
)
@ManagedBean(name= AjaxAdvancedBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class AjaxAdvancedBean extends ComponentExampleImpl<AjaxAdvancedBean> implements Serializable
{
    public static final String BEAN_NAME = "ajaxAdvancedBean";

    public AjaxAdvancedBean() {
        super(AjaxAdvancedBean.class);
    }
    
    @PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }

    private boolean draggable;
    private boolean modal;

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public boolean isModal() {
        return modal;
    }

    public void setModal(boolean modal) {
        this.modal = modal;
    }
}