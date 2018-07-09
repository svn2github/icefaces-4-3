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

package org.icefaces.ace.component.list;

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
    Map<ACEList, String> valueMap = new HashMap<ACEList, String>();
    Map<ACEList, Object> minValueMap = new HashMap<ACEList, Object>();
    Map<ACEList, Object> maxValueMap = new HashMap<ACEList, Object>();
    Map<ACEList, String> minSubmittedValueMap = new HashMap<ACEList, String>();
    Map<ACEList, String> maxSubmittedValueMap = new HashMap<ACEList, String>();

    public FilterState() {}

    /* Create comprehensive current filter state */
    public FilterState(ACEList list) {
		saveState(list);
    }

    /* Create delta state from incoming filter input */
    public FilterState(FacesContext context, ACEList list) {
        String clientId = list.getClientId(context);
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();

		// restore submitted value maps
		minSubmittedValueMap = new HashMap<ACEList, String>();
		maxSubmittedValueMap = new HashMap<ACEList, String>();

		if (list.getFilterFacet() != null) return;

		String filterId = list.getClientId(context) + "_filter";
		FilterType type = list.getFilterType();
		if (type == FilterType.TEXT || type == FilterType.BOOLEAN || !list.isRangedFilter()) {
			filterId = list.getFilterType() == FilterType.DATE ? filterId + "_input" : filterId;
			saveState(list, params.get(filterId));
		} else {
			String filterIdMin = filterId + "_min";
			String filterIdMax = filterId + "_max";
			if (type == FilterType.DATE) {
				filterIdMin = filterIdMin + "_input";
				filterIdMax = filterIdMax + "_input";
				String inputDateMin = params.get(filterIdMin);
				String inputDateMax = params.get(filterIdMax);
				Locale locale = list.calculateLocale(context);
				DateFormat format = new SimpleDateFormat(list.getFilterDatePattern(), locale);
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
				saveState(list, dateMin, dateMax);
				if (inputDateMin == null) inputDateMin = "";
				if (inputDateMax == null) inputDateMax = "";
				saveSubmittedValues(list, inputDateMin, inputDateMax);
			} else if (type == FilterType.BYTE) {
				String inputMin = params.get(filterIdMin);
				String inputMax = params.get(filterIdMax);
				Byte min = null;
				Byte max = null;
				try {
					if (inputMin != null && !"".equals(inputMin)) min = new Byte(Byte.parseByte(inputMin));
					if (inputMax != null && !"".equals(inputMax)) max = new Byte(Byte.parseByte(inputMax));
				} catch (NumberFormatException e) {
					min = null;
					max = null;
				}
				saveState(list, min, max);
				saveSubmittedValues(list, inputMin, inputMax);
			} else if (type == FilterType.SHORT) {
				String inputMin = params.get(filterIdMin);
				String inputMax = params.get(filterIdMax);
				Short min = null;
				Short max = null;
				try {
					if (inputMin != null && !"".equals(inputMin)) min = new Short(Short.parseShort(inputMin));
					if (inputMax != null && !"".equals(inputMax)) max = new Short(Short.parseShort(inputMax));
				} catch (NumberFormatException e) {
					min = null;
					max = null;
				}
				saveState(list, min, max);
				saveSubmittedValues(list, inputMin, inputMax);
			} else if (type == FilterType.INT) {
				String inputMin = params.get(filterIdMin);
				String inputMax = params.get(filterIdMax);
				Integer min = null;
				Integer max = null;
				try {
					if (inputMin != null && !"".equals(inputMin)) min = new Integer(Integer.parseInt(inputMin));
					if (inputMax != null && !"".equals(inputMax)) max = new Integer(Integer.parseInt(inputMax));
				} catch (NumberFormatException e) {
					min = null;
					max = null;
				}
				saveState(list, min, max);
				saveSubmittedValues(list, inputMin, inputMax);
			} else if (type == FilterType.LONG) {
				String inputMin = params.get(filterIdMin);
				String inputMax = params.get(filterIdMax);
				Long min = null;
				Long max = null;
				try {
					if (inputMin != null && !"".equals(inputMin)) min = new Long(Long.parseLong(inputMin));
					if (inputMax != null && !"".equals(inputMax)) max = new Long(Long.parseLong(inputMax));
				} catch (NumberFormatException e) {
					min = null;
					max = null;
				}
				saveState(list, min, max);
				saveSubmittedValues(list, inputMin, inputMax);
			} else if (type == FilterType.FLOAT) {
				String inputMin = params.get(filterIdMin);
				String inputMax = params.get(filterIdMax);
				Float min = null;
				Float max = null;
				try {
					if (inputMin != null && !"".equals(inputMin)) min = new Float(Float.parseFloat(inputMin));
					if (inputMax != null && !"".equals(inputMax)) max = new Float(Float.parseFloat(inputMax));
				} catch (NumberFormatException e) {
					min = null;
					max = null;
				}
				saveState(list, min, max);
				saveSubmittedValues(list, inputMin, inputMax);
			} else if (type == FilterType.DOUBLE) {
				String inputMin = params.get(filterIdMin);
				String inputMax = params.get(filterIdMax);
				Double min = null;
				Double max = null;
				try {
					if (inputMin != null && !"".equals(inputMin)) min = new Double(Double.parseDouble(inputMin));
					if (inputMax != null && !"".equals(inputMax)) max = new Double(Double.parseDouble(inputMax));
				} catch (NumberFormatException e) {
					min = null;
					max = null;
				}
				saveState(list, min, max);
				saveSubmittedValues(list, inputMin, inputMax);
			}
		}
    }

    public void saveState(ACEList list) {
		Object filterValues = list.getFilterValues();
		if (filterValues != null) {
			if (filterValues instanceof Collection || Object[].class.isAssignableFrom(filterValues.getClass())) {
				JSONArray jsonArray = null;
				try {
					if (filterValues instanceof Collection) jsonArray = new JSONArray((Collection) filterValues);
					else jsonArray = new JSONArray(filterValues);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (jsonArray != null) valueMap.put(list, jsonArray.toString());
			} else {
				throw new FacesException("Attribute filterValues in ace:list with ID '" + list.getId() + "' must be either a Collection or an array of strings.");
			}
		} else {
			valueMap.put(list, list.getFilterValue());
		}
    }

    public void saveState(ACEList list, String value) {
        valueMap.put(list, value);
    }

    public void saveState(ACEList list, Object valueMin, Object valueMax) {
        minValueMap.put(list, valueMin);
        maxValueMap.put(list, valueMax);
    }

    public void saveSubmittedValues(ACEList list, String inputMin, String inputMax) {
        minSubmittedValueMap.put(list, inputMin);
        maxSubmittedValueMap.put(list, inputMax);
    }

    private void restoreState(ACEList list) {
		if (!list.isRangedFilter()) {
			String val = valueMap.get(list);
			if (val != null)
				list.setFilterValue(val);
		} else {
			Object minVal = minValueMap.get(list);
			String submittedMinVal = minSubmittedValueMap.get(list);
			if (minVal != null || (submittedMinVal != null && "".equals(submittedMinVal.trim())))
				list.setFilterValueMin(minVal);
			Object maxVal = maxValueMap.get(list);
			String submittedMaxVal = maxSubmittedValueMap.get(list);
			if (maxVal != null || (submittedMaxVal != null && "".equals(submittedMaxVal.trim())))
				list.setFilterValueMax(maxVal);
		}
    }

    public void apply(ACEList list) {
		restoreState(list);
    }
}
