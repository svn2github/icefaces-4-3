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

package org.icefaces.samples.showcase.example.compat.divider;

import java.io.Serializable;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.event.ActionEvent;

import org.icefaces.samples.showcase.metadata.annotation.ComponentExample;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResource;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResources;
import org.icefaces.samples.showcase.metadata.annotation.ResourceType;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;

@ComponentExample(
        parent = DividerBean.BEAN_NAME,
        title = "example.compat.divider.position.title",
        description = "example.compat.divider.position.description",
        example = "/resources/examples/compat/divider/dividerPosition.xhtml"
)
@ExampleResources(
        resources ={
            // xhtml
            @ExampleResource(type = ResourceType.xhtml,
                    title="dividerPosition.xhtml",
                    resource = "/resources/examples/compat/"+
                               "divider/dividerPosition.xhtml"),
            // Java Source
            @ExampleResource(type = ResourceType.java,
                    title="DividerPosition.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/"+
                               "showcase/example/compat/divider/DividerPosition.java")
        }
)
@ManagedBean(name= DividerPosition.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class DividerPosition extends ComponentExampleImpl<DividerPosition> implements Serializable {
	
	public static final String BEAN_NAME = "dividerPosition";
	
	private Random randomizer = new Random(System.nanoTime());
	private int position = randomPosition();
	
	public DividerPosition() {
		super(DividerPosition.class);
	}
	
    @PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }

	public int getPosition() { return position; }
	
	public void setPosition(int position) { this.position = position; }
	
	private int randomPosition() {
	    return 1+randomizer.nextInt(99);
	}
	
	public void randomEvent(ActionEvent event) {
	    position = randomPosition();
	}
}
