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

import org.icefaces.ace.component.column.Column;
import org.icefaces.ace.component.column.ColumnType;
import org.icefaces.ace.context.RequestContext;
import org.icefaces.ace.json.JSONArray;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class FilterState {
    Map<Column, String> valueMap = new HashMap<Column, String>();
    Map<Column, Object> minValueMap = new HashMap<Column, Object>();
    Map<Column, Object> maxValueMap = new HashMap<Column, Object>();
    Map<Column, String> minSubmittedValueMap = new HashMap<Column, String>();
    Map<Column, String> maxSubmittedValueMap = new HashMap<Column, String>();

    public FilterState() {}

    /* Create comprehensive current filter state */
    public FilterState(DataTable table) {
        List<Column> columnList = table.getColumns(true);
        for (Column column : columnList)
            saveState(column);
    }

    /* Create delta state from incoming filter input */
    public FilterState(FacesContext context, DataTable table) {
        String clientId = table.getClientId(context);
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();

		// restore submitted value maps
		minSubmittedValueMap = new HashMap<Column, String>();
		maxSubmittedValueMap = new HashMap<Column, String>();

        Map<String,Column> filterMap = table.getFilterMap();
		for (Column column : filterMap.values()) {
			if (column.getFilterFacet() != null) continue;
			String columnId = column.getClientId(context) + "_filter";
			ColumnType type = column.getColumnType();
			if (type == ColumnType.TEXT || type == ColumnType.BOOLEAN || !column.isRangedFilter()) {
				columnId = column.getColumnType() == ColumnType.DATE ? columnId + "_input" : columnId;
				saveState(column, params.get(columnId));
			} else {
				String columnIdMin = columnId + "_min";
				String columnIdMax = columnId + "_max";
				if (type == ColumnType.DATE) {
					columnIdMin = columnIdMin + "_input";
					columnIdMax = columnIdMax + "_input";
					String inputDateMin = params.get(columnIdMin);
					String inputDateMax = params.get(columnIdMax);
					Locale locale = column.calculateLocale(context);
					DateFormat format = new SimpleDateFormat(column.getFilterDatePattern(), locale);
					Date dateMin = null;
					Date dateMax = null;
					try {
						if (inputDateMin != null && !"".equals(inputDateMin)) dateMin = format.parse(inputDateMin);
					} catch (ParseException e) {
						dateMin = null;
					}
					try {
						if (inputDateMax != null && !"".equals(inputDateMax)) dateMax = format.parse(inputDateMax);
					} catch (ParseException e) {
						dateMax = null;
					}
					saveState(column, dateMin, dateMax);
					if (inputDateMin == null) inputDateMin = "";
					if (inputDateMax == null) inputDateMax = "";
					saveSubmittedValues(column, inputDateMin, inputDateMax);
				} else if (type == ColumnType.BYTE) {
					String inputMin = params.get(columnIdMin);
					String inputMax = params.get(columnIdMax);
					Byte min = null;
					Byte max = null;
					try {
						if (inputMin != null && !"".equals(inputMin)) min = new Byte(Byte.parseByte(inputMin));
						if (inputMax != null && !"".equals(inputMax)) max = new Byte(Byte.parseByte(inputMax));
					} catch (NumberFormatException e) {
						min = null;
						max = null;
					}
					saveState(column, min, max);
					saveSubmittedValues(column, inputMin, inputMax);
				} else if (type == ColumnType.SHORT) {
					String inputMin = params.get(columnIdMin);
					String inputMax = params.get(columnIdMax);
					Short min = null;
					Short max = null;
					try {
						if (inputMin != null && !"".equals(inputMin)) min = new Short(Short.parseShort(inputMin));
						if (inputMax != null && !"".equals(inputMax)) max = new Short(Short.parseShort(inputMax));
					} catch (NumberFormatException e) {
						min = null;
						max = null;
					}
					saveState(column, min, max);
					saveSubmittedValues(column, inputMin, inputMax);
				} else if (type == ColumnType.INT) {
					String inputMin = params.get(columnIdMin);
					String inputMax = params.get(columnIdMax);
					Integer min = null;
					Integer max = null;
					try {
						if (inputMin != null && !"".equals(inputMin)) min = new Integer(Integer.parseInt(inputMin));
						if (inputMax != null && !"".equals(inputMax)) max = new Integer(Integer.parseInt(inputMax));
					} catch (NumberFormatException e) {
						min = null;
						max = null;
					}
					saveState(column, min, max);
					saveSubmittedValues(column, inputMin, inputMax);
				} else if (type == ColumnType.LONG) {
					String inputMin = params.get(columnIdMin);
					String inputMax = params.get(columnIdMax);
					Long min = null;
					Long max = null;
					try {
						if (inputMin != null && !"".equals(inputMin)) min = new Long(Long.parseLong(inputMin));
						if (inputMax != null && !"".equals(inputMax)) max = new Long(Long.parseLong(inputMax));
					} catch (NumberFormatException e) {
						min = null;
						max = null;
					}
					saveState(column, min, max);
					saveSubmittedValues(column, inputMin, inputMax);
				} else if (type == ColumnType.FLOAT) {
					String inputMin = params.get(columnIdMin);
					String inputMax = params.get(columnIdMax);
					Float min = null;
					Float max = null;
					try {
						if (inputMin != null && !"".equals(inputMin)) min = new Float(Float.parseFloat(inputMin));
						if (inputMax != null && !"".equals(inputMax)) max = new Float(Float.parseFloat(inputMax));
					} catch (NumberFormatException e) {
						min = null;
						max = null;
					}
					saveState(column, min, max);
					saveSubmittedValues(column, inputMin, inputMax);
				} else if (type == ColumnType.DOUBLE) {
					String inputMin = params.get(columnIdMin);
					String inputMax = params.get(columnIdMax);
					Double min = null;
					Double max = null;
					try {
						if (inputMin != null && !"".equals(inputMin)) min = new Double(Double.parseDouble(inputMin));
						if (inputMax != null && !"".equals(inputMax)) max = new Double(Double.parseDouble(inputMax));
					} catch (NumberFormatException e) {
						min = null;
						max = null;
					}
					saveState(column, min, max);
					saveSubmittedValues(column, inputMin, inputMax);
				}
			}
		}
    }

    public void saveState(Column column) {
		Object filterValues = column.getFilterValues();
		if (filterValues != null) {
			if (filterValues instanceof Collection || Object[].class.isAssignableFrom(filterValues.getClass())) {
				JSONArray jsonArray = null;
				try {
					if (filterValues instanceof Collection) jsonArray = new JSONArray((Collection) filterValues);
					else jsonArray = new JSONArray(filterValues);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (jsonArray != null) valueMap.put(column, jsonArray.toString());
			} else {
				throw new FacesException("Attribute filterValues in ace:column with ID '" + column.getId() + "' must be either a Collection or an array of strings.");
			}
		} else {
			valueMap.put(column, column.getFilterValue());
		}
    }

    public void saveState(Column column, String value) {
        valueMap.put(column, value);
    }

    public void saveState(Column column, Object valueMin, Object valueMax) {
        minValueMap.put(column, valueMin);
        maxValueMap.put(column, valueMax);
    }

    public void saveSubmittedValues(Column column, String inputMin, String inputMax) {
        minSubmittedValueMap.put(column, inputMin);
        maxSubmittedValueMap.put(column, inputMax);
    }

    private void restoreState(Column column) {
		if (!column.isRangedFilter()) {
			String val = valueMap.get(column);
			if (val != null)
				column.setFilterValue(val);
		} else {
			Object minVal = minValueMap.get(column);
			String submittedMinVal = minSubmittedValueMap.get(column);
			if (minVal != null || (submittedMinVal != null && "".equals(submittedMinVal.trim())))
				column.setFilterValueMin(minVal);
			Object maxVal = maxValueMap.get(column);
			String submittedMaxVal = maxSubmittedValueMap.get(column);
			if (maxVal != null || (submittedMaxVal != null && "".equals(submittedMaxVal.trim())))
				column.setFilterValueMax(maxVal);
		}
    }

    public void apply(DataTable table) {
        List<Column> columnList = table.getColumns(true);
        for (Column column : columnList)
            restoreState(column);
    }
}
