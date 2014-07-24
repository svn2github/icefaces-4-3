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

package org.icefaces.samples.showcase.example.core;

import org.icefaces.samples.showcase.metadata.annotation.ComponentExample;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResource;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResources;
import org.icefaces.samples.showcase.metadata.annotation.ResourceType;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;

import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import java.io.Serializable;

@ComponentExample(
        title = "DefaultAction Component",
        description = "The <b>&lt;icecore:defaultAction&gt;</b> component allows users to execute actions triggered by key presses when no other component in the page will. The following key presses are wired to trigger default action: Enter, Esc, Left Arrow, F3. The rest of key presses are ignored.",
        example = "/resources/examples/core/default-action.xhtml"
)

@ExampleResources(
        resources = {
                @ExampleResource(type = ResourceType.xhtml,
                        title="autoCompleteEntry.xhtml",
                        resource = "/resources/examples/core/navigation-notifier.xhtml"),
                @ExampleResource(type = ResourceType.xhtml,
                        title="autoCompleteEntry.xhtml",
                        resource = "/resources/examples/core/navigation-notifier-next.xhtml"),
                @ExampleResource(type = ResourceType.java,
                        title="AutoCompleteEntryBean.java",
                        resource = "/WEB-INF/classes/org/icefaces/samples/showcase/example/core/NavigationNotifierBean.java"),
        }
)

@ManagedBean
@ViewScoped
public class NavigationNotifierBean extends ComponentExampleImpl<NavigationNotifierBean> implements Serializable {
    private boolean navigationDetected;

    public NavigationNotifierBean() {
        super(NavigationNotifierBean.class);
    }

    @PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }

    public void navigationDetected() {
        navigationDetected = true;
    }

    public boolean getNavigationDetected() {
        return navigationDetected;
    }
}
