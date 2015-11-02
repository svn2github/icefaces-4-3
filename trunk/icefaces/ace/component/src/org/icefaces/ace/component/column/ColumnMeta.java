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

package org.icefaces.ace.component.column;

import org.icefaces.ace.meta.annotation.Component;
import org.icefaces.ace.meta.annotation.DefaultValueType;
import org.icefaces.ace.meta.annotation.Expression;
import org.icefaces.ace.meta.annotation.Property;
import org.icefaces.ace.meta.baseMeta.UIColumnMeta;

import org.icefaces.resources.ICEResourceDependencies;
import java.util.Comparator;

@Component(
        tagName         = "column",
        componentClass  = "org.icefaces.ace.component.column.Column",
        generatedClass  = "org.icefaces.ace.component.column.ColumnBase",
        extendsClass    = "javax.faces.component.UIColumn",
        componentType   = "org.icefaces.ace.component.Column",
        rendererType    = "",
		componentFamily = "org.icefaces.ace.component",
		tlddoc = "<p>Component that represents a column in an ace:dataTable.</p>" +
                 "<p>For more information, see the <a href=\"http://wiki.icefaces.org/display/ICE/Column\">Column Wiki Documentation</a>.</p>")
public class ColumnMeta extends UIColumnMeta {

    @Property(tlddoc = "Custom inline CSS styles to use for this component. These styles are generally applied to the root DOM element of the component. This is intended for per-component basic style customizations. Note that due to browser CSS precedence rules, CSS rendered on a DOM element will take precedence over the external stylesheets used to provide the ThemeRoller theme on this component. If the CSS properties applied with this attribute do not affect the DOM element you want to style, you may need to create a custom theme styleClass for the theme CSS class that targets the particular DOM elements you wish to customize.")
	private String style;

    @Property(tlddoc = "Custom CSS style class(es) to use for this component. These style classes can be defined in your page or in a theme CSS file.")
	private String styleClass;

//    @Property(expression = Expression.VALUE_EXPRESSION,
//            tlddoc = "Defines a value expression representing the value of the column per row, " +
//            "optionally superseded by the more specific value definitions like sortBy, filterBy etc.")
//    private Object `value;

    @Property(expression = Expression.VALUE_EXPRESSION,
            tlddoc="Defines a value expression representing the value of this column per row when determining " +
                   "if the values of this column in sibling rows are equal. If they are equal, span the rows with" +
                   "a single column cell.")
    private Object groupBy;

	@Property(expression = Expression.VALUE_EXPRESSION,
            tlddoc="Defines a value expression representing the value of this column per row when sorting. " +
                   "Setting this attribute, or the \"value\" attribute for a column enables sorting.")
	private Object sortBy;
	
	@Property(expression = Expression.VALUE_EXPRESSION,
            tlddoc="Defines an alternate method of sorting. Sort this column using a " +
            "Comparator<Object> object that takes the sortBy values of this column as input.")
	private Comparator sortFunction;

	@Property(tlddoc="Specifies a string that will be used to identify this column when working in lazy mode. This string will be passed as a key of the filters map and will be returned from SortCriteria.getPropertyName() in the LazyDataModel.load() method. If this attribute is defined, then this value will be used instead of the value obtained by parsing the EL expressions of the sortyBy and filterBy attributes. This attribute is useful when using complex and/or dynamic EL expressions that can't be parsed nor resolved at render time, such as when using c:forEach to define the columns.")
	private String lazyColumnKey;

	@Property(expression = Expression.VALUE_EXPRESSION,
            tlddoc="Defines a ValueExpression of the value of this row to use when filtering this column. " +
                   "Setting this attribute, or the \"value\" attribute for a column enables filtering.")
	private Object filterBy;

    @Property(tlddoc="Defines the string input filtering this column, coming from the client, or from " +
            "the application via a value binding.")
    private String filterValue;
    
	@Property(tlddoc="Defines additional CSS rules to be applied to the filter text input.")
	private String filterStyle;
	
	@Property(tlddoc="Defines supplementary CSS classes to add to those already " +
            "applied on this component.")
	private String filterStyleClass;
	
	@Property(tlddoc="Defines a collection of SelectItem objects for use as filter choices.")
	private Object filterOptions;
	
	@Property(tlddoc="Defines the method of filter comparison used, default is \"startsWith\". " +
            "Types available include: \"contains\", \"exact\", \"startsWith\" and \"endsWith\".", defaultValue="startsWith")
	private String filterMatchMode;

    @Property(tlddoc="If true, range filtering is enabled. When range filtering is enabled, two input fields will be rendered for filtering this column. The first one is to specify the minimum value and the second one is to specify the maximum value. All rows with values for this column in between this range will be matched. If only the minimum value is specified, all values greater than or equal to it will be matched; if only the maximum value is specified, all values less than or equal to it will be matched.",
            defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean rangedFilter;

    @Property(tlddoc="When range filtering is enabled, this property is used to access or to set the minimum filter value of the range. The value can't be a primitive type; use the corresponding wrapper object instead.")
    private Object filterValueMin;

    @Property(tlddoc="When range filtering is enabled, this property is used to access or to set the maximum filter value of the range. The value can't be a primitive type; use the corresponding wrapper object instead.")
    private Object filterValueMax;

    @Property(defaultValue = "yyyy-MM-dd", tlddoc = "DateFormat pattern for the date filter input. See the " +
            "<a href=\"http://wiki.icefaces.org/display/ICE/DateTimeEntry\">DateTimeEntry Wiki Documentation</a> for limitations. ")
    private String filterDatePattern;

    @Property(tlddoc = "Locale to be used for the date filter input. Valid values can be a Locale string or java.util.Locale objects. Default is locale of view root.")
    private Object filterDateLocale;

	@Property(tlddoc="Defines the number of rows the rendered cell spans. Only " +
            "significant to Column components within a column group.", defaultValue="1")
	private int rowspan;
	
	@Property(tlddoc="Defines the number of columns the rendered cell spans. Only " +
            "significant to Column components within a column group.", defaultValue="1")
	private int colspan;
	
	@Property(tlddoc="Defines a plain text header with less effort than adding a facet. Note that even when using a header facet this property should also be set as a short-form label for the column that will be used within the ace:tableConfigPanel.")
	private String headerText;
	
	@Property(tlddoc="Defines a  plain text footer with less effort than adding a facet.")
	private String footerText;

    @Property(tlddoc="When enabled, this column is rendered underneath the previous column.")
    private boolean stacked;

    @Property(tlddoc="When disabled, this column is excluded from the list of columns available" +
            " for configuration on a TableConfigPanel component.",
            defaultValue = "true", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean configurable;

    @Property(tlddoc="Defines the priority of a column during sorting. The column " +
            "priorities incoming from the client during a sort request overwrite any " +
            "set by the application. Processing the sorted columns is done by the " +
            "component whenever a client edits a sort control or the application calls table.applySorting().")
    private Integer sortPriority;

    @Property(tlddoc="When disabled, this column will neither be sortable nor automatically sorted when using the groupBy attribute.",
            defaultValue = "true", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean sortWhenGrouping;

    @Property(tlddoc="When true, the sort controls in the column header will not be rendered.",
            defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean hideSortControls;

    @Property(tlddoc="Defines the direction of column values during sorting. " +
            "The column directions incoming from the client during a sort request " +
            "overwrite any set by the application. Processing the sorted columns is " +
            "done by the component whenever a client edits a sort control or the " +
            "application calls table.applySorting().",
            defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private Boolean sortAscending;

    @Property(tlddoc="Enables per-column control of column ordering when the " +
            "attribute (\"reorderableColumns\") is true at the table level.",
            defaultValue = "true", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean reorderable;

    @Property(tlddoc = "Define the position of this column in the pinned column " +
            "region when column pinning and scrolling are enabled at the table level. " +
            "Having a value in this field indicates a column is pinned. 1 indexed.")
    private Integer pinningOrder;

    @Property(tlddoc = "Enables responsive column display for this column by specifying "+
			"a priority value that ranges from 1 to 6, with lower numerical values representing higher priorities. " +
			"A prioritized column is subject to being conditionally displayed in the client, based on the user agent's " +
			"viewport width. It is necessary to include the mobi:deviceResource component on the page in order for this " +
			"feature to work correctly on mobile devices. Columns with higher priority take precedence over columns with lower " + "priority. Columns that don't have this attribute set will always be displayed. As for stacked columns, the " +
			"ace:column component at the top of a column stack dictates the display priority of the rendered column in the " +
			"client. As for multi-row headers (i.e. ace:columnGroup/ace:row), each header column must have this attribute " +
			"set, and, if it's a parent header column, its value must be the same as that of its highest priority child, " + "otherwise the parent will remain visible when all its children are hidden, taking up space.")
    private Integer displayPriority;

    @Property(defaultValue = "text",
              defaultValueType = DefaultValueType.EXPRESSION,
              tlddoc = "Specifies the data type of this column, determining how the " +
                      "'value' attribute of this component will be handled when filtering. Valid options " +
                      "include : 'text', 'boolean', 'date', 'byte', 'short', 'int', 'long', 'float', and 'double' " +
                      "(when 'text' is used, all values are treated as strings; 'date' assumes the value object is a Date object).")
    String type;

//    These per-feature configuration attributes will be used when the 'value' property is added to Column.
//    Until then they are redundant.
//    @Property(tlddoc="Enables per-column control of column sorting when either the " +
//            "attribute (\"sortBy\") or (\"value\") are set.",
//            defaultValue = "true", defaultValueType = DefaultValueType.EXPRESSION)
//    private boolean sortable;
//
//    @Property(tlddoc="Enables per-column control of column filtering when either the " +
//            "attribute (\"filterBy\") or (\"value\") are set.",
//            defaultValue = "true", defaultValueType = DefaultValueType.EXPRESSION)
//    private boolean filterable;
//
//    @Property(tlddoc="Enables per-column control of Cell selection when either the " +
//            "attribute (\"selectBy\") or (\"value\") are set.",
//            defaultValue = "true", defaultValueType = DefaultValueType.EXPRESSION)
//    private boolean selectable;
}
