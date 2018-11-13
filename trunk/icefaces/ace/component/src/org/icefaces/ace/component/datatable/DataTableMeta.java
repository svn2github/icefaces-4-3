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

package org.icefaces.ace.component.datatable;

import org.icefaces.ace.meta.annotation.*;
import org.icefaces.ace.meta.baseMeta.UIDataMeta;
import org.icefaces.ace.model.table.RowStateMap;
import org.icefaces.ace.resources.ACEResourceNames;

import org.icefaces.resources.ICEResourceDependencies;
import org.icefaces.resources.ICEResourceDependency;
import org.icefaces.resources.ICEResourceLibrary;

import javax.faces.component.UIComponent;
import javax.el.MethodExpression;
import java.util.List;


@Component(
        tagName = "dataTable",
        componentClass = "org.icefaces.ace.component.datatable.DataTable",
        generatedClass = "org.icefaces.ace.component.datatable.DataTableBase",
        rendererClass = "org.icefaces.ace.component.datatable.DataTableRenderer",
        extendsClass = "javax.faces.component.UIData",
        componentType = "org.icefaces.ace.component.DataTable",
        rendererType = "org.icefaces.ace.component.DataTableRenderer",
        componentFamily = "org.icefaces.ace.DataTable",
        tlddoc = "Renders an HTML table element. This table and its associated " +
                "components offers support for features like: scrolling, " +
                "sorting, filtering, row selection, row editing, lazy loading " +
                "and expandable subrows and subpanels." +
                "<p>For more information, see the <a href=\"http://wiki.icefaces.org/display/ICE/DataTable\">DataTable Wiki Documentation</a>.</p>"
)
@ICEResourceLibrary(ACEResourceNames.ACE_LIBRARY)
@ICEResourceDependencies({
	@ICEResourceDependency(name = "fontawesome/font-awesome.css"),
	@ICEResourceDependency(name = "util/ace-core.js"),
	@ICEResourceDependency(name = "jquery/jquery.js"),
	@ICEResourceDependency(name = "dnd/dragdrop.js"),
	@ICEResourceDependency(name="util/ace-datatable.js"),
	@ICEResourceDependency(name = "util/ace-jquery-ui.js"),
	@ICEResourceDependency(name = "datetimeentry/jquery-ui-timepicker-addon.js"),
	@ICEResourceDependency(name = "datetimeentry/datetimeentry.js")
})
@ClientBehaviorHolder(events = {
        @ClientEvent(name="page", javadoc="Fired when the page is changed on the DataTable.",
                    tlddoc="Fired when the page is changed on the DataTable.", defaultRender="@all", defaultExecute="@this"),

        @ClientEvent(name="rowsPerPage", javadoc="Fired when the rows per page is changed on the DataTable via the paginator control.",
                     tlddoc="Fired when the rows per page is changed on the DataTable via the paginator control.", defaultRender="@all", defaultExecute="@this"),

        @ClientEvent(name="cellClick",
                     javadoc="Fired when a cell is clicked on the DataTable.",
                     tlddoc="Fired when a cell is clicked on the DataTable.",
                     defaultRender="@all", defaultExecute="@this"),

        @ClientEvent(name="cellDblClick",
                     javadoc="Fired when a cell is double clicked on the DataTable.",
                     tlddoc="Fired when a cell is double clicked on the DataTable.",
                     defaultRender="@all", defaultExecute="@this"),

        @ClientEvent(name="select",
                     javadoc="Fired when a row or cell is selected on the DataTable.",
                     tlddoc="Fired when a row or cell is selected on the DataTable.",
                     defaultRender="@all", defaultExecute="@this"),

        @ClientEvent(name="deselect",
                     javadoc="Fired when a row or cell is deselected on the DataTable.",
                     tlddoc="Fired when a row or cell is deselected on the DataTable.",
                     defaultRender="@all", defaultExecute="@this"),

        @ClientEvent(name="sort",
                     javadoc="Fired when a change to the current sort occurs on the DataTable.",
                     tlddoc="Fired when a change to the current sort occurs on the DataTable.",
                     defaultRender="@all", defaultExecute="@this"),

        @ClientEvent(name="filter",
                     javadoc="Fired when a change to the current filters occurs on the DataTable.",
                     tlddoc="Fired when a change to the current filters occurs on the DataTable.",
                     defaultRender="@all", defaultExecute="@this"),

        @ClientEvent(name="reorder",
                     javadoc="Fired when a column is dragged and dropped into a new ordering.",
                     tlddoc="Fired when a column is dragged and dropped into a new ordering.",
                     defaultRender="@all", defaultExecute="@this"),

        // Edit has custom render and execute, @none is just a null placeholder for additional update/execute fields
        @ClientEvent(name="editStart",
                     javadoc="Fired when a row is enabled for editing.",
                     tlddoc="Fired when a row is enabled for editing.",
                     defaultRender="@this", defaultExecute="@this"),

        @ClientEvent(name="editSubmit",
                     javadoc="Fired when a row submits its edits (Note: This event has a special execute attribute. The client ids of all the cell editors in the row will be automatically specified. Using @all or @form won't submit inputs of other rows in edit mode.).",
                     tlddoc="Fired when a row is submits its edits.",
                     defaultRender="@all", defaultExecute="@this"),

        @ClientEvent(name="editCancel",
                     javadoc="Fired when a row cancels an in-progress edit.",
                     tlddoc="Fired when a row cancels an in-progress edit.",
                     defaultRender="@this", defaultExecute="@this"),

        @ClientEvent(name="expand",
                     javadoc="Fired when a child ExpansionToggler component is clicked to expand (Note: for nested tables, the execute value should be @form or @all for the expansion to work).",
                     tlddoc="Fired when a child ExpansionToggler component is clicked to expand  (Note: for nested tables, the execute value should be @form or @all for the expansion to work).",
                     defaultRender="@all", defaultExecute="@this"),

        @ClientEvent(name="contract",
                     javadoc="Fired when a child ExpansionToggler component is clicked to contract  (Note: for nested tables, the execute value should be @form or @all for the contraction to work).",
                     tlddoc="Fired when a child ExpansionToggler component is clicked to contract  (Note: for nested tables, the execute value should be @form or @all for the contraction to work).",
                     defaultRender="@all", defaultExecute="@this"),

        @ClientEvent(name="pin",
                     javadoc="Fired when a column is added to the pinning region of the table.",
                     tlddoc="Fired when a column is added to the pinning region of the table.",
                     defaultRender="@this", defaultExecute="@this"),

        @ClientEvent(name="unpin",
                     javadoc="Fired when a column is removed to the pinning region of the table.",
                     tlddoc="Fired when a column is removed to the pinning region of the table.",
                     defaultRender="@this", defaultExecute="@this"),


        @ClientEvent(name="find",
                     javadoc="Fired for a find request.",
                     tlddoc="Fired for a find request.",
                     defaultRender="@all", defaultExecute="@all", argumentClass="org.icefaces.ace.event.DataTableFindEvent")}
        ,
        defaultEvent = "select"
)
public class DataTableMeta extends UIDataMeta {
    /* ##################################################################### */
    /* ############################ Misc. Prop. ############################ */
    /* ##################################################################### */

    @Property(tlddoc = "Disable all features of the data table.", defaultValue = "false",
            defaultValueType= DefaultValueType.EXPRESSION)
    private boolean disabled;

    @Property(tlddoc = "The request-scope attribute (if any) under which the data " +
        "object index for the current row will be exposed when iterating.")
    private String rowIndexVar;

    @Property(tlddoc = "The request-scope attribute (if any) under which the table" +
            " state object for the current row will be exposed when iterating.", defaultValue = "rowState")
    private String rowStateVar;

    @Property(tlddoc = "Enables lazy loading. Lazy loading expects the 'value' property to reference " +
            "an instance of LazyDataModel, an interface to support incremental fetching of " +
            "table entities. Note that it is necessary that the data object implement custom hashCode() and equals() methods for advanced features to work correctly. see wiki for more information.")
    private boolean lazy;

    @Property(tlddoc = "Defines a tabindex to be shared by all keyboard navigable elements of the table. " +
            "This includes sort controls, filter fields and individual rows themselves.",
            defaultValue = "0",
            defaultValueType = DefaultValueType.EXPRESSION)
    private Integer tabIndex;

    @Property(defaultValue="0",
            defaultValueType= DefaultValueType.EXPRESSION,
            implementation=Implementation.GENERATE,
            tlddoc="The number of rows to be displayed, or zero to display the entire " +
                    "set of available rows.")
    private int rows;

    @Property(tlddoc = "Define a string to render when there are no records to display. The 'emptyMessage' facet takes precedence over this attribute.")
    private String emptyMessage;

    @Facets
    class FacetsMeta {
        @Facet(tlddoc = "An optional facet that is rendered in place of data rows when the dataTable view contains no rows.")
        UIComponent emptyMessage;
    }
    
    @Property(tlddoc = "Disables sorting for multiple columns at once. Sorting " +
            "is enabled by the use of sortBy on ace:column components.",
            defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean singleSort;

    @Property(tlddoc = "Defines a map of your row data objects to UI states. Row-level " +
            "features (selection, expansion, etc.) are manipulable through this repository. Note that it is necessary that the data object implement custom hashCode() and equals() methods for advanced features to work correctly. see wiki for more information.")
    private RowStateMap stateMap;

    @Property(tlddoc = "Enable the decoding of child components during table feature " +
            "requests. The table attempts to decode children whenever it is executed, " +
            "meaning whenever a parent region is submitted, or the table submits itself " +
            "to paginate, make a selection, reorder columns, or any other feature. " +
            "Decoding children during feature requests can result in unwanted input " +
            "submission (during pagination for example), so by default this component " +
            "suppresses child decoding whenever submitting itself. To cause decoding in the " +
            "children of the table, use the row editing feature for row-scoped input " +
            "decoding, ajax submit the form (or other table parent) for broad decoding" +
            "or enable this option to submit during all table operations.",
            defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private Boolean alwaysExecuteContents;

    @Property(tlddoc = "Enable the the client to revert the edited row with the default state following a failed edit." +
            " By default when validation fails during a row editing request the row remains in editing mode.",
            defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private Boolean toggleOnInvalidEdit;

    @Property(tlddoc = "Enable the default handling of the scrollable table when " +
            "rendered into a hidden page region. The table attempts to poll its hidden " +
            "status, looking for when it is shown and then call the scrollable table sizing " +
            "JavaScript. This can be expensive in environments of reduced JavaScript performance with " +
            "many tables and a complex DOM. When this is disabled, upon revealing a hidden " +
            "scrollable table, to ensure it is sized correctly the JS " +
            "resizeScrolling() function must be called on the table instance in the client side.",
            defaultValue = "true", defaultValueType = DefaultValueType.EXPRESSION)
    private Boolean hiddenScrollableSizing;

    @Property(tlddoc = "Enable the default scrollable table behaviour of using JavaScript to size a " +
            "table header and footer that are in a fixed position regardless of table body scrolling.",
            defaultValue = "true", defaultValueType = DefaultValueType.EXPRESSION)
    private Boolean staticHeaders;

    @Property(tlddoc = "Causes a caption element to be rendered with the specified text, in order to facilitate better screen reader support and accessibility for this table.")
	private String caption;

	@Property(tlddoc = "Access key that, when pressed, transfers focus to this component.")
	String accesskey;

    // ID of the configPanel that has been associated with this table, used for
    // component lookups during decodes.
    @Field(defaultValue = "null", defaultValueIsStringLiteral = false)
    protected String tableConfigPanel;
    @Field(defaultValue = "null", defaultValueIsStringLiteral = false)
    protected List filteredData;
    @Field(defaultValue = "null", defaultValueIsStringLiteral = false)
    protected Integer valueHashCode;
    @Field(defaultValue = "true", defaultValueIsStringLiteral = false)
    protected Boolean applyingSorts;
    @Field(defaultValue = "true", defaultValueIsStringLiteral = false)
    protected Boolean applyingFilters;
    // Used to force update of entire table container when using forceTableUpdate
    @Field(defaultValue = "0", defaultValueIsStringLiteral = false)
    protected Integer forcedUpdateCounter;
    @Field
    private Object cachedGlobalFilter;
    @Field(defaultValue = "0l", defaultValueIsStringLiteral = false)
    private Long lastReordering;



    /* ##################################################################### */
    /* ########################## Style Properties ######################### */
    /* ##################################################################### */
    @Property(tlddoc = "Custom inline CSS styles to use for this component. These styles are generally applied to the root DOM element of the component. This is intended for per-component basic style customizations. Note that due to browser CSS precedence rules, CSS rendered on a DOM element will take precedence over the external stylesheets used to provide the ThemeRoller theme on this component. If the CSS properties applied with this attribute do not affect the DOM element you want to style, you may need to create a custom theme styleClass for the theme CSS class that targets the particular DOM elements you wish to customize.")
    private String style;

    @Property(tlddoc = "Custom CSS style class(es) to use for this component. These style classes can be defined in your page or in a theme CSS file.")
    private String styleClass;

    @Property(tlddoc = "Define css classes for each row of the dataTable. " +
            "EL can be used in this attribute to produce conditional row styling.")
    private String rowStyleClass;





    /* ##################################################################### */
    /* ############################# Listeners ############################# */
    /* ##################################################################### */
    @Property(expression = Expression.METHOD_EXPRESSION,
            methodExpressionArgument = "org.icefaces.ace.event.SelectEvent",
            tlddoc = "MethodExpression reference called whenever a table " +
                    "element is selected. The method receives a single " +
                    "argument, SelectEvent.")
    private MethodExpression rowSelectListener;

    @Property(expression = Expression.METHOD_EXPRESSION,
            methodExpressionArgument = "org.icefaces.ace.event.UnselectEvent",
            tlddoc = "MethodExpression reference called whenever a table " +
                    "element is deselected. The method receives a single " +
                    "argument, UnselectEvent.")
    private MethodExpression rowUnselectListener;

    @Property(expression = Expression.METHOD_EXPRESSION,
            methodExpressionArgument = "org.icefaces.ace.event.TableFilterEvent",
            tlddoc = "MethodExpression reference called whenever the table row " +
                    "is filtered. The method receives a single argument, TableFilterEvent.")
    private MethodExpression filterListener;


    /* ##################################################################### */
    /* ######################### Column Pinning ############################ */
    /* ##################################################################### */
    @Property(tlddoc = "Enable statically positioned columns along the left hand side of a scrollable table (i.e. the 'scrollable' attribute must be set to 'true' for this feature to work).")
    boolean columnPinning;

    @Property(tlddoc = "Enable client controls to add column to the list of statically positioned columns.")
    boolean columnPinningControlsInHeader;


    /* ##################################################################### */
    /* ############################# Pagination ############################ */
    /* ##################################################################### */
    @Property(tlddoc = "Defines a list of comma separated integer values that represent the options " +
            "of \"number of items per page\" presented to the user. \"all\" is a special keyword that allows" +
            "a user to reset the pagination offset show all rows. The \"all\" option label is defined by the " +
            " message key \"org.icefaces.ace.component.datatable.ALL_LABEL\". Any non-numerical value other than " +
            "\"all\" will cause a NumberFormatException.")
    private String rowsPerPageTemplate;

    @Property(tlddoc = "Defines a coded string representing the layout of the text displaying" +
            " the current page. The keywords available are {currentPage}, {totalPages}, {startRecord}, {endRecord}, and {totalRecords}. Default is: \"({currentPage} of {totalPages})\".")
    private String currentPageReportTemplate;

    @Property(tlddoc = "Defines a coded string representing the controls available as part of" +
            " the paginator. Default is: \"{FirstPageLink} {PreviousPageLink} " +
            "{PageLinks} {NextPageLink} {LastPageLink}\". The {FastRewind} and {FastForward} controls are also available. They skip a number of pages backward or forward, respectively. This number is defined by the 'pagesToSkip' property.")
    private String paginatorTemplate;

    @Property(tlddoc = "Defines the number of pages to skip when pressing the FastRewind and FastForward buttons.", defaultValue = "3")
    private int pagesToSkip;

    @Property(tlddoc = "Defines the location of the paginator if enabled. Available " +
            "options are top, bottom, or the default, both.", defaultValue = "both")
    private String paginatorPosition;

    @Property(tlddoc = "Defines whether the paginator always displays, even when fewer then 1 page " +
            "full of items are displayed.")
    private boolean paginatorAlwaysVisible;

    @Property(tlddoc = "Defines the maximum number of individual page links to display in paginator.",
            defaultValue = "10", defaultValueType = DefaultValueType.EXPRESSION)
    private Integer pageCount;

    @Property(tlddoc = "Defines the index of the current page, beginning at 1.")
    private int page;

    @Property(tlddoc = "Enables pagination on the table. Note that the paginator works by adjusting the " +
            "'first' and 'page' properties and that disabling the paginator will not return these " +
            "properties to their defaults; instead leaving the table at the position that was paginated to. " +
            "To return the table to the first page, 'first' must be set to 0, or 'page' must be set to 1." +
            "Alternately the table has a convenience method for this, DataTable.resetPagination().")
    private boolean paginator;

	@Property(tlddoc = "Access key that, when pressed, transfers focus to the current page button.")
	String paginatorAccesskey;





    /* ##################################################################### */
    /* ########################## Scrolling Prop. ########################## */
    /* ##################################################################### */
    @Property(tlddoc = "Defines a fixed height for the scrollable table in pixels.",
            defaultValue = "100", defaultValueType = DefaultValueType.EXPRESSION)
    private Integer scrollHeight;

    @Property(tlddoc= "(Deprecated) This attribute was used to support scrolling on IE7 in previous versions and has no effect now.")
    private String scrollWidthIE7;

    @Property(tlddoc = "Enabling renders a table that overflows the fixed height and adds " +
            "a scrollbar. Note, used in combination with multi-row headers defined by a ColumnGroup" +
            "component, it is assumed that every body column of the table will have a associated " +
            "single column spanning header column on the bottom row of the multi-row header. This is " +
            "to allow for appropriate sizing of the scrollable column and the associated header td.")
    private boolean scrollable;

    @Property(tlddoc = "When set to true, a number of rows will be automatically loaded after reaching the bottom of the table scroll bar. The rows correspond to the data items in the data model that follow the rows currently displayed. The number of rows to load is determined by the 'rows' and the 'liveScrollBufferPages' attributes.")
    private boolean liveScroll;

    @Property(tlddoc = "When using live scrolling, the number of pages specified by this attribute will be rendered before and after the current page. This means that if the value is 2, there will be 5 pages worth of rows displayed on the table.", defaultValue="2")
    private Integer liveScrollBufferPages;



    /* ##################################################################### */
    /* ########################## Selection Prop. ########################## */
    /* ##################################################################### */
    @Property(tlddoc = "Defines a code word indicating method of table element selection." +
            " Available values include: \"multiple\", \"single\", \"enhMultiple\"," +
            " \"cellblock\", \"cellrange\", \"multiplecell\" and \"singlecell\". The enhanced multiple mode (\"enhMultiple\") works in the following way. Single clicking a row will select it and deselect all other rows. No toggling occurs unless the Ctrl/Command key is held, in which case the other rows won't be deselected. Holding the Shift key will cause the range of rows between the last selected row and the current one to be selected.")
    private String selectionMode;

    @Property(tlddoc = "Enable to require a double-click to fire row/cell selection events.")
    private boolean doubleClickSelect;

    @Property(tlddoc = "Enable to have all column clicks accepted as selection and/or ajax click events. Typically only click events" +
            "that occur on a child div, span, or the td element itself are captured for processing.")
    private boolean allColumnClicks;



    /* ##################################################################### */
    /* ########################## Filtering Prop. ########################## */
    /* ##################################################################### */
    @Property(tlddoc = "Enable to force creation of the filtered data set from the bound " +
            "value every render. Alternately attempt to use hashCodes of the " +
            "value property to detect changes and prompt refiltering.",
            defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean constantRefilter;

    @Property(tlddoc="Defines the Javascript event on which to trigger filter event, ex. " +
            "\'keyup\', \'blur\', \'change\' and \'enter\' (Note: columns filtered based on Date values always trigger the event on change).", defaultValue="change")
    private String filterEvent;

    @Property(tlddoc="Defines the input to the column non-specific filter, coming from the client, or " +
            "from the app via a value binding.")
    private String filterValue;

    @Property(tlddoc="Enable to display all members of groups (as defined by ace:column groupBy) that contain a matching row during filtering.")
    private boolean groupedFilterResults;

    @Property(tlddoc = "Read-only property indicating the number of filtered rows currently being displayed on the table. If no filtering is taking place, the value will be -1. Note that the output component or expression bound to this property has to be rendered inside or after this data table component, in order for the value to be accurate and updated.",
            defaultValue = "-1",
            defaultValueType = DefaultValueType.EXPRESSION)
    private Integer filteredRows;



    /* ##################################################################### */
    /* ########################## Column Features ########################## */
    /* ##################################################################### */
    @Property(tlddoc = "Enable resizing of the table columns via handles on " +
            "the column headers.")
    private boolean resizableColumns;

    @Property(tlddoc = "When 'resizableColumns' is enabled, allow the table to also increase or decrease in width " +
			"proportionally to the column being resized. In the normal mode, when resizing a column, tables maintain " +
			"their original widths, and a specific column is made wider by making other columns narrower. HTML has a " +
			"limitation that prevents table columns to be narrower than their contents. This mode " +
			"is mainly oriented to tables that have too many columns that they can't fit in the viewport width, " +
			"resulting impossible to make a column wider in the normal mode due to this HTML limitation.")
    private boolean allowTableResizing;

    @Property(tlddoc = "Enable reordering of the table columns via header " +
            "dragging.")
    private boolean reorderableColumns;

    @Property(tlddoc = "Defines a list of integers representing a rendering order for " +
            "the Column children of the table.")
    private List<Integer> columnOrdering;

    @Property(tlddoc = "Defines a list of integers representing a rendering " +
            "order for the Column children of the header columnGroup. This " +
            "is separate from the columnOrdering property to maintain " +
            "backwards compatibility and simplify application code when " +
            "columns are added or removed. Both properties should be " +
            "maintained in sync with each other.")
    private List<Integer> headerColumnOrdering;

    @Property(tlddoc = "Enabling makes the entire header container the clickable region for sort events.",
            defaultValue =  "true", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean clickableHeaderSorting;

    @Property(tlddoc = "Row tabindex is required for dataTable Find feature and for selecting rows by using the keyboard. If the Find or Row Selection features are not used and the tabindex interferes with tabbing through inputs in the table, set this to false.",
            defaultValue =  "true")
    private boolean renderRowTabindex;

    @Property(tlddoc="To reduce redundancy in markup, body columns can detect "+
        "which header column that they correspond to, and inherit sorting, " +
        "filtering and grouping properties from it. Previously, it was " +
        "necessary to provide the same properties and values on both " +
        "components, but now it's possible to specify them on the header " +
        "column, and have the body column inherit them automatically. If a " +
        "property is specified on the body column, then that will take " +
        "precedence for it.", defaultValueType = DefaultValueType.EXPRESSION,
        defaultValue =  "true")
    private boolean columnsInheritProperties;
}