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

package org.icefaces.ace.component.listexportervalue;

import org.icefaces.ace.meta.annotation.Component;
import org.icefaces.ace.meta.annotation.Property;
import org.icefaces.ace.meta.baseMeta.UIComponentBaseMeta;

@Component(
        tagName = "listExporterValue",
        componentClass = "org.icefaces.ace.component.listexportervalue.ListExporterValue",
        generatedClass = "org.icefaces.ace.component.listexportervalue.ListExporterValueBase",
        extendsClass = "javax.faces.component.UIComponentBase",
        componentType = "org.icefaces.ace.component.ListExporterValue",
        componentFamily = "org.icefaces.ace.component",
        tlddoc = "Embedding instances of this component inside ace:list causes ace:listExporter to export the values specified by these instances as separate columns for the same item, instead of rendering other nested markup." +
                 "<p>For more information, see the " +
                 "<a href=\"http://wiki.icefaces.org/display/ICE/ListExporterValue\">ListExporterValue Wiki Documentation</a>."
)
public class ListExporterValueMeta extends UIComponentBaseMeta {

	@Property(tlddoc="The value to be exported.")
	private Object value;

	@Property(tlddoc="The name to use as header for this value in the CSV, XLS, XLSX, and PDF formats and as the tag name in the XML format.")
	private String name;
}
