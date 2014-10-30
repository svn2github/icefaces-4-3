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

package org.icefaces.samples.showcase.example.ace.tooltip; 

import org.icefaces.samples.showcase.metadata.annotation.*;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;

import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;


@ComponentExample(
        parent = TooltipOverviewBean.BEAN_NAME,
        title = "example.ace.globalTooltip.title",
        description = "example.ace.globalTooltip.description",
        example = "/resources/examples/ace/tooltip/globalTooltip.xhtml"
)
@ExampleResources(
        resources ={
            // xhtml
            @ExampleResource(type = ResourceType.xhtml,
                    title="globalTooltip.xhtml",
                    resource = "/resources/examples/ace/tooltip/globalTooltip.xhtml")
        }
)

@ManagedBean(name= GlobalTooltipBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class GlobalTooltipBean extends ComponentExampleImpl<GlobalTooltipBean> implements Serializable {

    public static final String BEAN_NAME = "globalTooltipBean";
	public String getBeanName() { return BEAN_NAME; }
    /////////////---- CONSTRUCTOR BEGIN
    public GlobalTooltipBean() {
        super(GlobalTooltipBean.class);
    }

    @PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }
}
