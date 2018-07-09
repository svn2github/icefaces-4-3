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

package org.icefaces.ace.component.dataexporter;

import org.icefaces.ace.resources.ACEResourceNames;
import org.icefaces.resources.ICEResourceDependencies;
import org.icefaces.resources.ICEResourceDependency;

import org.icefaces.ace.meta.annotation.Component;
import org.icefaces.ace.meta.annotation.DefaultValueType;
import org.icefaces.ace.meta.annotation.Property;
import org.icefaces.ace.meta.annotation.Required;
import org.icefaces.ace.meta.annotation.Expression;

import javax.el.ValueExpression;
import javax.el.MethodExpression;
import org.icefaces.ace.meta.baseMeta.UIComponentBaseMeta;

import org.icefaces.ace.meta.annotation.ClientBehaviorHolder;
import org.icefaces.ace.meta.annotation.ClientEvent;
import org.icefaces.ace.api.IceClientBehaviorHolder;
import org.icefaces.resources.ICEResourceLibrary;

@Component(
        tagName       = "dataExporter",
        componentClass  = "org.icefaces.ace.component.dataexporter.DataExporter",
		rendererClass   = "org.icefaces.ace.component.dataexporter.DataExporterRenderer",
        generatedClass  = "org.icefaces.ace.component.dataexporter.DataExporterBase",
        extendsClass    = "javax.faces.component.UIComponentBase",
        componentType   = "org.icefaces.ace.component.DataExporter",
        rendererType    = "org.icefaces.ace.component.DataExporterRenderer",
		componentFamily  = "org.icefaces.ace.component",
		tlddoc = "Utility to export data from a datatable as an Excel, PDF, XML or CSV document. This component renders an HTML button. More components and HTML elements can be nested inside this tag to give a different look to the button." +
                 "<p>For more information, see the " +
                 "<a href=\"http://wiki.icefaces.org/display/ICE/DataExporter\">DataExporter Wiki Documentation</a>."
)
@ICEResourceLibrary(ACEResourceNames.ACE_LIBRARY)
@ICEResourceDependencies({
	@ICEResourceDependency(name = "util/ace-core.js"),
	@ICEResourceDependency(name = "jquery/jquery.js"),
	@ICEResourceDependency(name = "util/ace-jquery-ui.js"),
	@ICEResourceDependency(name="util/ace-datatable.js")
})
@ClientBehaviorHolder(events = {
	@ClientEvent(name="action", javadoc="Triggers when the button is clicked or pressed by any other means. By default, both this data exporter component and the target table are executed in this ajax request.", tlddoc="Triggers when the button is clicked or pressed by any other means. By default, both this data exporter component and the target table are executed in this ajax request.", defaultRender="@all", defaultExecute="@this")
}, defaultEvent="action")
public class DataExporterMeta extends UIComponentBaseMeta {

	@Property(required=Required.yes, tlddoc="Define the id of the ace:dataTable whose data will be exported.")
	private String target;
	
	@Property(tlddoc="Define the format of file export. Available formats: \"xls\", \"xlsx\", \"pdf\", \"csv\", and \"xml\".", defaultValue="csv")
	private String type;
	
	@Property(required=Required.yes, tlddoc="Define the filename of the generated file, defaults to dataTable id.")
	private String fileName;
	
	@Property(required=Required.no, tlddoc="Specifies an object that implements a custom format type to use instead of one of the built-in formats. When this attribute is specified, it will override the \"type\" attribute. The object must extend org.icefaces.ace.component.dataexporter.Exporter.")
	private Object customExporter;

	@Property(required=Required.no, tlddoc="Define the text that will appear on the button to trigger the export. Default value is 'Export'. Attribute applies only if the component has no children.")
	private String label;
	
	@Property(required=Required.no, tlddoc="Enable to export only the current page instead of the whole data set.", defaultValue="false")
	private boolean pageOnly;
	
	@Property(required=Required.no, tlddoc="Define a comma separated list of column indexes (zero-relative) to be excluded from export. Only Column components, without a rendered ExcludeFromExport child, count towards this indexing scheme.")
	private String excludeColumns;
	
	@Property(required=Required.no, tlddoc="Defines a public void method to invoke before the PDF or XLS document is generated, allowing developers to manipulate the document. It must take a single argument of type Object. The object will be of type com.lowagie.text.Document (iText library) for PDF documents and of type org.apache.poi.ss.usermodel.Workbook (Apache POI library) for XLS documents.", expression = Expression.METHOD_EXPRESSION, methodExpressionArgument="Object")
	private MethodExpression preProcessor;
	
	@Property(required=Required.no, tlddoc="Defines a public void method to invoke after the PDF or XLS document has been generated, allowing developers to manipulate the document. It must take a single argument of type Object. The object will be of type com.lowagie.text.Document (iText library) for PDF documents and of type org.apache.poi.ss.usermodel.Workbook (Apache POI library) for XLS documents.", expression = Expression.METHOD_EXPRESSION, methodExpressionArgument="Object")
	private MethodExpression postProcessor;
	
	@Property(required=Required.no, tlddoc="Define a character encoding to use. Valid values will depend on the host system and format in use. For plain text formats (CSV and XML), the encoding has to be supported by the JVM running the server. Typically, these values include UTF-8, UTF-16, US-ASCII, ISO-8859-1, Windows-1252, among others. For the PDF format, this attribute is only applicable if a custom font is also specified via the 'pdfFont' attribute, and the encoding has to be supported by such font. For the XLS format, this attributr is not applicable; Unicode is used by default.", defaultValue="UTF-8")
	private String encoding;
	
	@Property(required=Required.no, tlddoc="Disable whether column headers should be included at the top of the file. This is not applicable when attribute 'type' is set to 'xml'.", defaultValue="true")
	private boolean includeHeaders;

	@Property(required=Required.no, tlddoc="Disable whether column footers should be included at the bottom of the file. This is not applicable when attribute 'type' is set to 'xml'.", defaultValue="true")
	private boolean includeFooters;
	
	@Property(required=Required.no, tlddoc="Enable to export only the currently selected rows instead of the whole dataset.", defaultValue="false")
	private boolean selectedRowsOnly;
	
	@Property(required=Required.no, tlddoc="Specify whether the columns should be exported according to the order established by the user in the client. When false, the column order defined in the markup will be used.", defaultValue="true")
	private boolean userColumnOrder;
	
	@Property(required=Required.no, tlddoc="When using row expansion in the table, specify whether the child rows should be exported only if the parent row has been expanded by the user.", defaultValue="false")
	private boolean expandedOnly;

    @Property(required=Required.no, tlddoc = "Custom CSS style class(es) to use for this component. These style classes can be defined in your page or in a theme CSS file.")
    private String styleClass;  

    @Property(required=Required.no, tlddoc = "Custom inline CSS styles to use for this component. These styles are generally applied to the root DOM element of the component. This is intended for per-component basic style customizations. Note that due to browser CSS precedence rules, CSS rendered on a DOM element will take precedence over the external stylesheets used to provide the ThemeRoller theme on this component. If the CSS properties applied with this attribute do not affect the DOM element you want to style, you may need to create a custom theme styleClass for the theme CSS class that targets the particular DOM elements you wish to customize.")
    private String style;

	@Property(tlddoc = "Access key that, when pressed, transfers focus to this component.")
	String accesskey;

	@Property(tlddoc = "The absolute path in the host system (e.g. C:\\Windows\\Fonts\\arial.ttf) to a font file to be used in the PDF exporting instead of the default font. If specifying a custom 'encoding' value, it has to be supported by this custom font. Note that the default font does not support unicode characters, so a custom font has to be set in order to support these characters.")
	String pdfFont;
}