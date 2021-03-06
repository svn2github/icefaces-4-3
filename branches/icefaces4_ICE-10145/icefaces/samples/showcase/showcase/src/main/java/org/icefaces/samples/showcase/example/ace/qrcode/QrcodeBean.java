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

package org.icefaces.samples.showcase.example.ace.qrcode;
import org.icefaces.samples.showcase.metadata.annotation.*;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;

import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;

@ComponentExample(
        title = "example.ace.qrcode.title",
        description = "example.ace.qrcode.description",
        example = "/resources/examples/ace/qrcode/qrcodeOverview.xhtml"
)
@ExampleResources(
        resources ={
            // xhtml
            @ExampleResource(type = ResourceType.xhtml,
                    title="qrcodeOverview.xhtml",
                    resource = "/resources/examples/ace/qrcode/qrcodeOverview.xhtml"),
            // Java Source
            @ExampleResource(type = ResourceType.java,
                    title="QrcodeBean.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/showcase"+
                    "/example/ace/qrcode/QrcodeBean.java")
        }
)
@Menu(
            title = "menu.ace.qrcode.subMenu.title",
            menuLinks = {
                @MenuLink(title = "menu.ace.qrcode.subMenu.main", isDefault = true, exampleBeanName = QrcodeBean.BEAN_NAME)
            }
)
@ManagedBean(name= QrcodeBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class QrcodeBean extends ComponentExampleImpl< QrcodeBean > implements Serializable {
    public static final String BEAN_NAME = "qrcodeBean";

    // input string for qr image generation.
    private String qrString = "test string";

    public QrcodeBean() {
        super(QrcodeBean.class);
    }

    @PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }

    public String getQrString() {
        return qrString;
    }

    public void setQrString(String qrString) {
        this.qrString = qrString;
    }
}