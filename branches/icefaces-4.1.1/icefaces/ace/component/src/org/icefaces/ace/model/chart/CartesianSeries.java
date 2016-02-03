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

package org.icefaces.ace.model.chart;

import org.icefaces.ace.component.chart.Axis;
import org.icefaces.ace.component.chart.AxisType;
import org.icefaces.ace.component.chart.Chart;
import org.icefaces.ace.model.SimpleEntry;
import org.icefaces.ace.util.JSONBuilder;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import java.lang.Boolean;
import java.lang.String;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CartesianSeries extends ChartSeries {
    private static final Logger log = Logger.getLogger(CartesianSeries.class.getName());
    public static enum CartesianType implements ChartType {
        BAR,
        LINE;
    }

    public ChartType getDefaultType() {
        return CartesianType.LINE;
    }

    Boolean pointLabels;
    Integer pointLabelTolerance;
    Boolean pointLabelStacked;
    String[] pointLabelList;

    Boolean dragable;
    DragConstraintAxis dragConstraintAxis;

    long fillToValue;
    String fillAxis; // x or y
    String fillColor; // CSS
    int fillAlpha; // 0 - 100 alpha

    /* next two are for both bar and line */
    boolean highlightMouseOver;  //True to highlight slice when mouseover
    boolean highlightMouseDown;  //Ture to highlight when mouse button is pressed over a slice
    String[] highlightColors; // an array of colors to use when highlighting a bar

    Boolean smooth;
    LinePattern linePattern;
    /* REST OF Props are for Bar Graph only */
    /* docs on jqplot say this next one is an arragy of colors to sue when highlighting a bar */
    /* note that seriesColors is not attribute of rendering optiosn but is applied when varyBarColor is true */

    int barWidth = Integer.MIN_VALUE; ///Width of the bar in pixels (auto by devaul)
    int barMargin = Integer.MIN_VALUE;  //number of pixels between groups of bars at adjacentaxis values
    int barPadding = Integer.MIN_VALUE; //number of pixels between adjacent bars at the same axis value
    Boolean horizontalBar; //when true have horizontal bars, default is false translates to barDirection of 'vertical' or 'horizontal'
    Boolean varyBarColor;
//    String[] seriesColors; //can override colors from options
    boolean waterfall;
    int groups; //gorup bars into this many groups


    // Add object as y-value with x-index relative to position in series vs separately determined x-axis ticks

    /**
     * Add an point as a y-value with a x-value relative to the position in this series and the value at an accompanying index in a
     * separately determined list of x-axis ticks.
     * @param y the value of the point
     */
    public void add(Object y) {
        getData().add(new SimpleEntry<Object, Object>(null, y));
    }

    /**
     * Add an point as an explicit x and y value pair.
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     */
    public void add(Object x, Object y) {
        getData().add(new SimpleEntry<Object, Object>(x, y));
    }

    /**
     * Used by the ChartRenderer to produce a JSON representation of the configuration of this series.
     * @return the serialized JSON object
     */
    @Override
    public JSONBuilder getDataJSON(UIComponent component) {
        JSONBuilder data = super.getDataJSON(component);
        Chart chart = (Chart)component;
        Map<String, Integer> xCategoryToIndexMap = null;
        Map<String, Integer> yCategoryToIndexMap = null;
        Class valueType = null;
        Class keyType = null;

        for (Object obj : (List<Object>)getData()) {
            Map.Entry<Object, Object> point = (Map.Entry<Object, Object>)obj;
            Object key = point.getKey();
            Object value = point.getValue();
            // Narrow type of value to write correct JS type.
            // If JS type is String for all cases (which is, and should be supported)
            // x-axis is not autoscaled correctly.
            Object outObj = null;

            if (key != null && keyType == null) {
                if (key instanceof Number) keyType = Number.class;
                else if (key instanceof String) keyType = String.class;
                else if (key instanceof Date) keyType = Date.class;
            }

            if (valueType == null) {
                if (value instanceof Number) valueType = Number.class;
                else if (value instanceof String) valueType = String.class;
                else if (value instanceof Date) valueType = Date.class;
            }

            if (key != null && value != null) {
                data.beginArray();

                if (keyType == Number.class)
                    data.item(((Number)key).doubleValue());
                else if (keyType == String.class) {
                    String strKey = key.toString();

                    // If string and plotted against CategoryAxis with defined ticks,
                    // convert Strings to matching tick indicies if available.
                    if (areAxisTicksPredefined(chart, 'x')) {
                        if (xCategoryToIndexMap == null)
                            xCategoryToIndexMap = createCategoryToIndexMap(chart, 'x');

                        data.item(xCategoryToIndexMap.get(strKey));
                    }
                    else
                        data.item(strKey);
                }
                else if (keyType == Date.class)
                    data.item(((Date)key).getTime());

                if (valueType == Number.class)
                    data.item(((Number)value).doubleValue());
                else if (valueType == String.class) {
                    String strVal = value.toString();

                    // If string and plotted against CategoryAxis with defined ticks,
                    // convert Strings to matching tick indicies if available.
                    if (areAxisTicksPredefined(chart, 'y')) {
                        if (yCategoryToIndexMap == null)
                            yCategoryToIndexMap = createCategoryToIndexMap(chart, 'y');

                        data.item(yCategoryToIndexMap.get(strVal));
                    }
                    else
                        data.item(strVal);
                }
                else if (valueType == Date.class)
                    data.item(((Date)value).getTime());

                data.endArray();
            }
            if (key==null && value!=null ){
                if (isNumber(value)){
                    data.item(((Number)value).doubleValue());
                }
            }
        }
        data.endArray();
        return data;
    }

    private boolean isNumber(Object value){
        Class c = value.getClass();
        if (c == double.class || c == Double.class || c==int.class || c==Integer.class
                || c == float.class || c == Float.class || c == Long.class || c == long.class){
            return true;
        }
        return false;
    }

    private Map<String, Integer> createCategoryToIndexMap(Chart chart, char axisDir) {
        Map<String, Integer> map = new HashMap<String, Integer>();

        Axis axis = null;

        if (axisDir == 'x') {
            if (Integer.valueOf(2).equals(getXAxis())) axis = chart.getX2Axis();
            else axis = chart.getXAxis();
        }
        else if (axisDir == 'y') {
            Axis[] yAxes = chart.getYAxes();
            Integer yAxisIndex = getYAxis();

            // If series is not explicitly plotted against an axis
            // plot it against the first axis
            if (yAxisIndex == null || yAxisIndex < 2)
                axis = yAxes[0];
            else axis = yAxes[yAxisIndex-1];
        }

        Integer index = 0;
        String[] ticks = axis.getTicks();
        for (String tick : ticks)
            map.put(tick, ++index);

        return map;
    }

    private boolean areAxisTicksPredefined(Chart chart, char axisDir) {
        Axis axis = null;

        if (axisDir == 'x') {
            if (Integer.valueOf(2).equals(getXAxis())) axis = chart.getX2Axis();
            else axis = chart.getXAxis();
        }
        else if (axisDir == 'y') {
            Axis[] yAxes = chart.getYAxes();
            Integer yAxisIndex = getYAxis();

            // If series is not explicitly plotted against an axis
            // plot it against the first axis
            if (yAxisIndex == null || yAxisIndex < 2)
                axis = yAxes[0];
            else axis = yAxes[yAxisIndex-1];
        }

        String[] ticks;
        return (axis != null && (ticks = axis.getTicks()) != null && ticks.length > 0);
    }

    /**
     * must take into account default series which may determine barType
     * @param cfg
     */
    public void encodeRendererOptions(JSONBuilder cfg){
        Boolean horiz = getHorizontalBar();
        Boolean varyColor = getVaryBarColor();
        Boolean fill = getFill();
        Boolean fillToZero = getFillToZero();
        ChartType barType = getType();
  /*      if (barType==null){
            barType = defaultChartType;
            logging statement to set bar type????
        }*/
        if (barType !=null && barType.equals(CartesianType.BAR) && hasBarRenderOptionsSet()){
            cfg.beginMap("rendererOptions");
            if (fillToZero != null) cfg.entry("fillToZero", fill);
            if (varyColor != null) cfg.entry("varyBarColor", varyColor);
            if (horiz != null) cfg.entry("barDirection", horiz ? "horizontal" : "vertical");
            if (barWidth > 0){
                cfg.entry("barWidth", barWidth);
            }
            if (barPadding > 0) {
                cfg.entry("barPadding", barPadding);
            }
            if (barMargin > 0){
                cfg.entry("barMargin", barMargin);
            }
            cfg.endMap();
    //     log.info(" \t\t cfg after bar options ="+cfg.toString());

        } else if (hasLineRenderOptionsSet()){
            cfg.beginMap("rendererOptions");
            Boolean smooth = getSmooth();
            LinePattern pattern = getLinePattern();
            if (pattern != null){
                cfg.entry("linePattern", pattern.toString());
            }
            if (smooth != null) cfg.entry("smooth", smooth);
            if (fill != null) cfg.entry("fill", fill);   //this is not a rendererOption in examples???
            cfg.endMap();
        }
    }

    /**
     * Used by the ChartRenderer to produce a JSON representation of the data of this series.
     * @return the JSON object
     * @param component
     */
    @Override
    public JSONBuilder getConfigJSON(UIComponent component) {
        JSONBuilder cfg = super.getConfigJSON(component);
        Chart chart = (Chart) component;
        boolean isBar = false;
        if (type != null) {
            if (type.equals(CartesianType.BAR)){
                isBar = type.equals(CartesianType.BAR);
                cfg.entry("renderer", "ice.ace.jq.jqplot.BarRenderer", true) ;
            }
            else if (type.equals(CartesianType.LINE))   {
                cfg.entry("renderer", "ice.ace.jq.jqplot.LineRenderer", true) ;
            }
        }

        if (hasPointLabelOptionSet()) encodePointLabelOptions(cfg);
        // dragable is only availabe in series not default!!...how to deal with this?
        //according to http://www.jqplot.com/docs/files/jqPlotOptions-txt.html supposed
        //to be at top level of options...seems to work within series but not seriesDefault
        Boolean dragable = getDragable();
        if (dragable != null && isConfiguredForDragging(chart)) {
            cfg.entry("isDragable", dragable);
            if (getDragConstraintAxis() != null) {
                cfg.beginMap("dragable");
                cfg.entry("constrainTo", getDragConstraintAxis().toString());
                cfg.endMap();
            }
        }
        encodeRendererOptions(cfg);
        cfg.endMap();
        return cfg;
    }



    private boolean isConfiguredForDragging(Chart chart) {
        if (!explicitXValuesDefined()) throw new FacesException(
                "Points in a dragable series must be added with an explicit x and y value.");
        if (!categoryAxesHaveExplicitTicks(chart)) throw new FacesException(
                "Category axes of a dragable series must have explicit ticks to prevent loss of categories during reordering.");
        if (!xValuesAreSortedAscending()) throw new FacesException(
                "Dragable points must be added in an ascending order of X value. This is " +
                "to prevent reindexing during plotting that breaks index association of points " +
                "in the model on the server and the model internal to the plot on the client.");
        return true;
    }

    private boolean xValuesAreSortedAscending() {
        boolean ret = true;
        Date lastDate = null;
        Number lastNumber = null;
        Class keyType = null;

        for (Object o : getData()) {
            SimpleEntry entry = (SimpleEntry) o;

            if (keyType == null) {
                Object key = entry.getKey();
                if (key instanceof Number)
                    keyType = Number.class;
                else if (key instanceof Date)
                    keyType = Date.class;
                else return true;
            }

            if (keyType == Date.class) {
                Date key = (Date) entry.getKey();
                if (lastDate != null)
                    ret = ret && 0 < (key.compareTo(lastDate));
                lastDate = key;
            } else if (keyType == Number.class) {
                Number key = (Number) entry.getKey();
                if (lastNumber != null)
                    ret = ret && (key.doubleValue() > lastNumber.doubleValue());
                lastNumber = key;
            }
        }

        return ret;
    }

    private boolean categoryAxesHaveExplicitTicks(Chart chart) {
        boolean ret = true;

        String[] ticks;
        Axis[] ys;
        Axis y;
        Integer yIndex = getYAxis();
        yIndex = yIndex != null && yIndex > 1 ? yIndex-1 : 0;

        if ((ys = chart.getYAxes()) != null)
            if ((y = ys[yIndex]) != null && y.getType() == AxisType.CATEGORY)
                    ret = ret && ((ticks = y.getTicks()) != null) && ticks.length > 0;

        Axis x = (getXAxis() != null && getXAxis() == 2) ? chart.getX2Axis() : chart.getXAxis();
        if (x != null && x.getType() == AxisType.CATEGORY)
            ret = ret && ((ticks = x.getTicks()) != null) && ticks.length > 0;

        return ret;
    }

    private boolean explicitXValuesDefined() {
        boolean ret = true;

        for (Object o : getData()) {
            SimpleEntry entry = (SimpleEntry) o;
            ret = ret && (entry.getKey() != null);
        }

        return ret;
    }

    private void encodePointLabelOptions(JSONBuilder cfg) {
        cfg.beginMap("pointLabels");

        if (pointLabels != null)
            cfg.entry("show", pointLabels);

        if (pointLabelList != null) {
            cfg.beginArray("labels");
            for (String s : pointLabelList)
                cfg.item(s);
            cfg.endArray();
        }

        if (pointLabelTolerance != null)
            cfg.entry("edgeTolerance", pointLabelTolerance);

        if (pointLabelStacked != null)
            cfg.entry("stackedValue", pointLabelStacked);

        cfg.endMap();
    }

    private boolean hasPointLabelOptionSet() {
        return (pointLabels != null || pointLabelList != null || pointLabelTolerance != null || pointLabelStacked != null);
    }

    private boolean hasLineRenderOptionsSet(){
        return (getFill() !=null || getSmooth() !=null);
    }

    private boolean hasBarRenderOptionsSet(){
        return (getVaryBarColor() != null || getHorizontalBar() !=null || getFill() !=null ||
                getBarWidth() > 0 || getBarMargin() > 0 || getBarPadding() > 0);
    }
    private boolean hasRenderOptionsSet() {
        return (getFill() != null ||  getVaryBarColor() != null || getHorizontalBar() != null || getSmooth() != null);
    }


    /**
     * Determine if this series is bar type, is it horizontal?
     * @return true if horizontal
     */
    public Boolean getHorizontalBar() {
        return horizontalBar;
    }

    /**
     * Set if this bar series is a horizontal type.
     * horizontal meaning up and down bars (default)
     * vertical meaning up and down bars.
     * equates to barDirection property in
     * @param horizontalBar bar series horizontal
     */
    public void setHorizontalBar(Boolean horizontalBar) {
        this.horizontalBar = horizontalBar;
    }

    /**
     * Get if this series has labels rendered near each point
     * @return true if labels are rendered
     */
    public Boolean isPointLabels() {
        return pointLabels;
    }

    /**
     * Set if this series has labels rendered near each point.
     * @param pointLabels series point labelling
     */
    public void setPointLabels(Boolean pointLabels) {
        this.pointLabels = pointLabels;
    }

    /**
     * Set the distances that point labels must be from a boundary
     * if they are to be rendered.
     * @return distance in pixels
     */
    public Integer getPointLabelTolerance() {
        return pointLabelTolerance;
    }

    /**
     * Get the distance that point labels must be from a boundary
     * if they are to be rendered,
     * @param pointLabelTolerance distance in pixels
     */
    public void setPointLabelTolerance(Integer pointLabelTolerance) {
        this.pointLabelTolerance = pointLabelTolerance;
    }

    /**
     * Get if the point labels are to be rendered in a stacked plot.
     * @return true if the point labels are intended for a stacked plot.
     */
    public Boolean getPointLabelStacked() {
        return pointLabelStacked;
    }

    /**
     * Set if the point labels are to be rendered in a stacked plot.
     * @param pointLabelStacked if the labels are intended for a stacked plot
     */
    public void setPointLabelStacked(Boolean pointLabelStacked) {
        this.pointLabelStacked = pointLabelStacked;
    }

    /**
     * Get the list of labels for the points of this series.
     * @return list of labels to be applied to points of associated index.
     */
    public String[] getPointLabelList() {
        return pointLabelList;
    }

    /**
     * Set the list of labels for the points of this series.
     * @param pointLabelList list of labels to be applied to points of associated index
     */
    public void setPointLabelList(String[] pointLabelList) {
        this.pointLabelList = pointLabelList;
    }

    /**
     * Get if the points of this series are draggable.
     * @return true, if the points of this series are draggable.
     */
    public Boolean getDragable() {
        return dragable;
    }

    /**
     * Enable dragging for the points of this series. Enables dragStart / dragStop client
     * behaviour events as well as raising PointValueChangeEvents on the on the server. Note that
     * enabling this mode causes several restrictions to be checked (and possibly raised as FacesExceptions)
     * due to the requirements of communicating edits to server model. In particular, the data points of this series must have explicit x values,
     * if this series has a category type axis, the axis must define its own ticks rather than deriving them from the data and the data points of
     * must be sorted ascending in the x axis to prevent reindexing during plotting.
     * @param dragable if the points of this series are dragale.
     */
    public void setDragable(Boolean dragable) {
        this.dragable = dragable;
    }

    /**
     * Get the configured axis that dragging of points is confined to.
     * @return enum representation of the X, Y, or no axis.
     */
    public DragConstraintAxis getDragConstraintAxis() {
        return dragConstraintAxis;
    }

    /**
     * Set the configured axis that dragging of points is confined to.
     * @param dragConstraintAxis enum representation of the X, Y, or no axis.
     */
    public void setDragConstraintAxis(DragConstraintAxis dragConstraintAxis) {
        this.dragConstraintAxis = dragConstraintAxis;
    }

    /**
     * Get the pattern of stroke applied to the lines of this series.
     * @return the LinePattern object representing the stoke type
     */
    public LinePattern getLinePattern() {
        return linePattern;
    }

    /**
     * Set the pattern of stroke applied to the lines of this series.
     * @param linePattern enum representation of the stroke type
     */
    public void setLinePattern(LinePattern linePattern) {
        this.linePattern = linePattern;
    }

    /**
     * Get if the lines of this series have curves rendered between points rather than straight line segments.
     * @return if the lines of this series are smoothed
     */
    public Boolean getSmooth() {
        return smooth;
    }

    /**
     * Set if the lines of this series have curves rendered between points rather than straight line segments.
     * @param smooth if the lines of this series are smoothed
     */
    public void setSmooth(Boolean smooth) {
        this.smooth = smooth;
    }

    public Boolean getVaryBarColor() {
        return varyBarColor;
    }

    /**
     * Ture to color each bar of a series separately rather than have every bar of a given series the same color.
     * If used for non-stacked multiple series bar plots, user should specify a separate 'seriesColors' array for each
     * series.  Otherwise, erach series will set tehir bars tot he same color array.  This option has no Effect for
     * stacked bar charts and is disabled.
     * @param varyBarColor
     */
    public void setVaryBarColor(Boolean varyBarColor) {
        this.varyBarColor = varyBarColor;
    }

    public int getBarWidth() {
        return barWidth;
    }

    /**
     * Width of the bar in pixels (auto by default).  null = calculated automatically.
     * @param barWidth
     */
    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
    }

    public String[] getHighlightColors() {
        return highlightColors;
    }

    public void setHighlightColors(String[] highlightColors) {
        this.highlightColors = highlightColors;
    }

    public int getBarMargin() {
        return barMargin;
    }

    /**
     * Number of pixels between groups of bars at adjacent axis values
     * @param barMargin
     */
    public void setBarMargin(int barMargin) {
        this.barMargin = barMargin;
    }

    public int getBarPadding() {
        return barPadding;
    }

    /**
     * Number of pixels between adjacent bars at the same axis value
     * @param barPadding
     */
    public void setBarPadding(int barPadding) {
        this.barPadding = barPadding;
    }
}
