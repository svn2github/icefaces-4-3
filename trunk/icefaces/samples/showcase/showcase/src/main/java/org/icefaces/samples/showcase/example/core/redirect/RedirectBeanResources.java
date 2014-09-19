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

package org.icefaces.samples.showcase.example.core.redirect;

import org.icefaces.samples.showcase.metadata.annotation.ExampleResource;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResources;
import org.icefaces.samples.showcase.metadata.annotation.ResourceType;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;

@ExampleResources(
        resources = {
                @ExampleResource(type = ResourceType.tld,
                        title = "icecore:redirect",
                        resource = "http://res.icesoft.org/docs/v4_latest/core/comps/tld/icecore/redirect.html"),
                @ExampleResource(type = ResourceType.wiki,
                        title = "icecore:redirect",
                        resource = "http://www.icesoft.org/wiki/display/ICE/redirect")
        }
)
@ManagedBean
@ApplicationScoped
public class RedirectBeanResources extends ComponentExampleImpl<RedirectBeanResources> implements Serializable {
    public RedirectBeanResources() {
        super(RedirectBeanResources.class);
    }

    @PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }
}
