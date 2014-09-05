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

package org.icefaces.samples.showcase.example.ace.slider;

import org.icefaces.samples.showcase.metadata.annotation.ComponentExample;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResource;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResources;
import org.icefaces.samples.showcase.metadata.annotation.ResourceType;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;

import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;

@ComponentExample(
        parent = SliderBean.BEAN_NAME,
        title = "example.ace.slider.submition.title",
        description = "example.ace.slider.submition.description",
        example = "/resources/examples/ace/slider/sliderSubmition.xhtml"
)
@ExampleResources(
        resources = {
                // xhtml
                @ExampleResource(type = ResourceType.xhtml,
                        title = "sliderSubmition.xhtml",
                        resource = "/resources/examples/ace/slider/sliderSubmition.xhtml"),
                // Java Source
                @ExampleResource(type = ResourceType.java,
                        title = "SliderSubmitionExample.java",
                        resource = "/WEB-INF/classes/org/icefaces/samples/showcase/example/ace/slider/SliderSubmitionExample.java")
        }
)
@ManagedBean(name = SliderSubmitionExample.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class SliderSubmitionExample extends ComponentExampleImpl<SliderSubmitionExample> implements Serializable {
    
    public static final String BEAN_NAME = "sliderSubmitionExample";
    private int autoValue;
    private int manualValue;
    
    public SliderSubmitionExample() {
        super(SliderSubmitionExample.class);
    }

    @PostConstruct
    public void initMetaData() {
        super.initMetaData();
        setGroup(2);
    }

    public int getAutoValue() {
        return autoValue;
    }

    public void setAutoValue(int autoValue) {
        this.autoValue = autoValue;
    }

    public int getManualValue() {
        return manualValue;
    }

    public void setManualValue(int manualValue) {
        this.manualValue = manualValue;
    }
    
}
