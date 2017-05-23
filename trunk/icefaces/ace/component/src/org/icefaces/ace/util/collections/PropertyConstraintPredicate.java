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

package org.icefaces.ace.util.collections;

import org.icefaces.ace.component.column.Column;
import org.icefaces.ace.component.column.ColumnType;
import org.icefaces.ace.component.list.ACEList;
import org.icefaces.ace.component.list.FilterType;
import org.icefaces.ace.model.filter.FilterConstraint;
import org.icefaces.ace.json.JSONArray;

import javax.el.ELResolver;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

public class PropertyConstraintPredicate implements Predicate {
    ValueExpression filterBy;
    String filterValue;
    FilterConstraint filterConstraint;
    FacesContext facesContext;
	Column column = null;
	ACEList list = null;
	JSONArray filterValues;

    public PropertyConstraintPredicate(FacesContext context, ValueExpression filterBy, String filterValue, FilterConstraint constraint, Column column) {
        this.filterValue = filterValue;
        this.filterConstraint = constraint;
        this.facesContext = context;
        this.filterBy = filterBy;
		this.column = column;
		if (column.getFilterValues() != null) {
			try {
				filterValues = new JSONArray(filterValue);
			} catch (Exception e) {
				filterValues = null;
			}
		}
    }

    public PropertyConstraintPredicate(FacesContext context, ValueExpression filterBy, String filterValue, FilterConstraint constraint, ACEList list) {
        this.filterValue = filterValue;
        this.filterConstraint = constraint;
        this.facesContext = context;
        this.filterBy = filterBy;
		this.list = list;
		if (list.getFilterValues() != null) {
			try {
				filterValues = new JSONArray(filterValue);
			} catch (Exception e) {
				filterValues = null;
			}
		}
    }

    public boolean evaluate(Object object) {
        Object value = filterBy.getValue(facesContext.getELContext());

		if ((this.column != null && this.column.getColumnType() == ColumnType.TEXT)
			|| (this.list != null && this.list.getFilterType() == FilterType.TEXT)) {
			if (value instanceof Date) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				value = dateFormat.format(value);
			}

			if (value != null) {
				if (filterValues != null) {
					for (int i = 0; i < filterValues.length(); i++) {
						String fv;
						try {
							fv = filterValues.getString(i);
						} catch (Exception e) {
							fv = null;
						}
						if (fv != null && filterConstraint.applies(value.toString(), fv)) return true;
					}
					if (filterValues.length() == 0) return true;
					return false;
				} else {
					return filterConstraint.applies(value.toString(), filterValue);
				}
			} else if (filterValues != null && filterValues.length() > 0)
				return false;			
			else if (filterValue != null && filterValue.length() > 0)
				return false;
			else
				return true;
		} else if ((this.column != null && this.column.getColumnType() == ColumnType.BOOLEAN)
					|| (this.list != null && this.list.getFilterType() == FilterType.BOOLEAN)) {
			if (value != null) {
				if (filterValues != null) {
					for (int i = 0; i < filterValues.length(); i++) {
						String fv;
						try {
							fv = filterValues.getString(i);
						} catch (Exception e) {
							fv = null;
						}
						if (fv != null) {
							if (((Boolean) value) == Boolean.valueOf(fv)) {
								return true;
							}
						}
					}
					if (filterValues.length() == 0) return true;
					return false;
				} else {
					if ("true".equalsIgnoreCase(filterValue)) {
						return ((Boolean) value) == true;
					} else {
						return ((Boolean) value) == false;
					}
				}
			} else if (filterValues != null && filterValues.length() > 0)
				return false;			
			else if (filterValue != null)
				return false;
			else
				return true;			
		} else if ((this.column != null && this.column.getColumnType() == ColumnType.DATE)
					|| (this.list != null && this.list.getFilterType() == FilterType.DATE)) {
			Date filterDate = null;
			Date[] filterDates = null;
			DateFormat dateFormat;
				if (this.column != null) dateFormat = new SimpleDateFormat(this.column.getFilterDatePattern(), 
					this.column.calculateLocale(this.facesContext));
				else dateFormat = new SimpleDateFormat(this.list.getFilterDatePattern(), 
					this.list.calculateLocale(this.facesContext));
			try {
				if (filterValues != null) {
					int size = filterValues.length();
					filterDates = new Date[size];
					for (int i = 0; i < size; i++) {
						String fv;
						try {
							fv = filterValues.getString(i);
						} catch (Exception e) {
							fv = null;
						}
						if (fv != null && !"".equals(fv)) filterDates[i] = dateFormat.parse(fv);
						else filterDates[i] = null;
					}
				} else if (filterValue != null && !"".equals(filterValue)) filterDate = dateFormat.parse(filterValue);
			} catch (ParseException e) {
				filterDate = null;
			}

			if (value instanceof Date) {
				if (value != null && filterDates != null) {
					String valueString = dateFormat.format(value);
					for (int i = 0; i < filterDates.length; i++) {
						String filterString = dateFormat.format(filterDates[i]);
						if (valueString.equals(filterString)) return true;
					}
					return false;
				} else if (value != null && filterDate != null) {
					String valueString = dateFormat.format(value);
					String filterString = dateFormat.format(filterDate);
					return valueString.equals(filterString);
				} else if (filterValues != null && filterValues.length() > 0)
					return false;			
				else if (filterDate != null)
					return false;
				else
					return true;
			} else {
				return false;
			}
		} else if ((this.column != null && this.column.getColumnType() == ColumnType.BYTE)
					|| (this.list != null && this.list.getFilterType() == FilterType.BYTE)) {
			Byte convertedValue = null;
			Byte convertedFilterValue = null;
			Byte[] convertedFilterValues = null;
			try {
				if (filterValues != null) {
					int size = filterValues.length();
					convertedFilterValues = new Byte[size];
					for (int i = 0; i < size; i++) {
						String fv;
						try {
							fv = filterValues.getString(i);
						} catch (Exception e) {
							fv = null;
						}
						if (fv != null && !"".equals(fv)) convertedFilterValues[i] = new Byte(Byte.parseByte(fv));
						else convertedFilterValues[i] = null;
					}
				} else if (filterValue != null && !"".equals(filterValue))
					convertedFilterValue = new Byte(Byte.parseByte(filterValue));

				if (value != null && !"".equals(value))
					convertedValue = new Byte(Byte.parseByte(value.toString()));
			} catch (NumberFormatException e) {
				convertedValue = null;
				convertedFilterValue = null;
				convertedFilterValues = null;
			}

			if (convertedValue != null && convertedFilterValues != null) {
				for (int i = 0; i < convertedFilterValues.length; i++) {
					if (convertedValue.byteValue() == convertedFilterValues[i].byteValue()) return true;
				}
				return false;
			} else if (convertedValue != null && convertedFilterValue != null) {
				return convertedValue.byteValue() == convertedFilterValue.byteValue();
			} else if (filterValues != null && filterValues.length() > 0)
				return false;			
			else if (convertedFilterValue != null)
				return false;
			else
				return true;
		} else if ((this.column != null && this.column.getColumnType() == ColumnType.SHORT)
					|| (this.list != null && this.list.getFilterType() == FilterType.SHORT)) {
			Short convertedValue = null;
			Short convertedFilterValue = null;
			Short[] convertedFilterValues = null;
			try {
				if (filterValues != null) {
					int size = filterValues.length();
					convertedFilterValues = new Short[size];
					for (int i = 0; i < size; i++) {
						String fv;
						try {
							fv = filterValues.getString(i);
						} catch (Exception e) {
							fv = null;
						}
						if (fv != null && !"".equals(fv)) convertedFilterValues[i] = new Short(Short.parseShort(fv));
						else convertedFilterValues[i] = null;
					}
				} else if (filterValue != null && !"".equals(filterValue))
					convertedFilterValue = new Short(Short.parseShort(filterValue));

				if (value != null && !"".equals(value))
					convertedValue = new Short(Short.parseShort(value.toString()));
			} catch (NumberFormatException e) {
				convertedValue = null;
				convertedFilterValue = null;
				convertedFilterValues = null;
			}

			if (convertedValue != null && convertedFilterValues != null) {
				for (int i = 0; i < convertedFilterValues.length; i++) {
					if (convertedValue.shortValue() == convertedFilterValues[i].shortValue()) return true;
				}
				return false;
			} else if (convertedValue != null && convertedFilterValue != null) {
				return convertedValue.shortValue() == convertedFilterValue.shortValue();
			} else if (filterValues != null && filterValues.length() > 0)
				return false;			
			else if (convertedFilterValue != null)
				return false;
			else
				return true;
		} else if ((this.column != null && this.column.getColumnType() == ColumnType.INT)
					|| (this.list != null && this.list.getFilterType() == FilterType.INT)) {
			Integer convertedValue = null;
			Integer convertedFilterValue = null;
			Integer[] convertedFilterValues = null;
			try {
				if (filterValues != null) {
					int size = filterValues.length();
					convertedFilterValues = new Integer[size];
					for (int i = 0; i < size; i++) {
						String fv;
						try {
							fv = filterValues.getString(i);
						} catch (Exception e) {
							fv = null;
						}
						if (fv != null && !"".equals(fv)) convertedFilterValues[i] = new Integer(Integer.parseInt(fv));
						else convertedFilterValues[i] = null;
					}
				} else if (filterValue != null && !"".equals(filterValue))
					convertedFilterValue = new Integer(Integer.parseInt(filterValue));

				if (value != null && !"".equals(value))
					convertedValue = new Integer(Integer.parseInt(value.toString()));
			} catch (NumberFormatException e) {
				convertedValue = null;
				convertedFilterValue = null;
				convertedFilterValues = null;
			}

			if (convertedValue != null && convertedFilterValues != null) {
				for (int i = 0; i < convertedFilterValues.length; i++) {
					if (convertedValue.intValue() == convertedFilterValues[i].intValue()) return true;
				}
				return false;
			} else if (convertedValue != null && convertedFilterValue != null) {
				return convertedValue.intValue() == convertedFilterValue.intValue();
			} else if (filterValues != null && filterValues.length() > 0)
				return false;			
			else if (convertedFilterValue != null)
				return false;
			else
				return true;
		} else if ((this.column != null && this.column.getColumnType() == ColumnType.LONG)
					|| (this.list != null && this.list.getFilterType() == FilterType.LONG)) {
			Long convertedValue = null;
			Long convertedFilterValue = null;
			Long[] convertedFilterValues = null;
			try {
				if (filterValues != null) {
					int size = filterValues.length();
					convertedFilterValues = new Long[size];
					for (int i = 0; i < size; i++) {
						String fv;
						try {
							fv = filterValues.getString(i);
						} catch (Exception e) {
							fv = null;
						}
						if (fv != null && !"".equals(fv)) convertedFilterValues[i] = new Long(Long.parseLong(fv));
						else convertedFilterValues[i] = null;
					}
				} else if (filterValue != null && !"".equals(filterValue))
					convertedFilterValue = new Long(Long.parseLong(filterValue));

				if (value != null && !"".equals(value))
					convertedValue = new Long(Long.parseLong(value.toString()));
			} catch (NumberFormatException e) {
				convertedValue = null;
				convertedFilterValue = null;
				convertedFilterValues = null;
			}

			if (convertedValue != null && convertedFilterValues != null) {
				for (int i = 0; i < convertedFilterValues.length; i++) {
					if (convertedValue.longValue() == convertedFilterValues[i].longValue()) return true;
				}
				return false;
			} else if (convertedValue != null && convertedFilterValue != null) {
				return convertedValue.longValue() == convertedFilterValue.longValue();
			} else if (filterValues != null && filterValues.length() > 0)
				return false;			
			else if (convertedFilterValue != null)
				return false;
			else
				return true;
		} else if ((this.column != null && this.column.getColumnType() == ColumnType.FLOAT)
					|| (this.list != null && this.list.getFilterType() == FilterType.FLOAT)) {
			Float convertedValue = null;
			Float convertedFilterValue = null;
			Float[] convertedFilterValues = null;
			try {
				if (filterValues != null) {
					int size = filterValues.length();
					convertedFilterValues = new Float[size];
					for (int i = 0; i < size; i++) {
						String fv;
						try {
							fv = filterValues.getString(i);
						} catch (Exception e) {
							fv = null;
						}
						if (fv != null && !"".equals(fv)) convertedFilterValues[i] = new Float(Float.parseFloat(fv));
						else convertedFilterValues[i] = null;
					}
				} else if (filterValue != null && !"".equals(filterValue))
					convertedFilterValue = new Float(Float.parseFloat(filterValue));

				if (value != null && !"".equals(value))
					convertedValue = new Float(Float.parseFloat(value.toString()));
			} catch (NumberFormatException e) {
				convertedValue = null;
				convertedFilterValue = null;
				convertedFilterValues = null;
			}

			if (convertedValue != null && convertedFilterValues != null) {
				for (int i = 0; i < convertedFilterValues.length; i++) {
					if (convertedValue.floatValue() == convertedFilterValues[i].floatValue()) return true;
				}
				return false;
			} if (convertedValue != null && convertedFilterValue != null) {
				return convertedValue.floatValue() == convertedFilterValue.floatValue();
			} else if (filterValues != null && filterValues.length() > 0)
				return false;			
			else if (convertedFilterValue != null)
				return false;
			else
				return true;
		} else if ((this.column != null && this.column.getColumnType() == ColumnType.DOUBLE)
					|| (this.list != null && this.list.getFilterType() == FilterType.DOUBLE)) {
			Double convertedValue = null;
			Double convertedFilterValue = null;
			Double[] convertedFilterValues = null;
			try {
				if (filterValues != null) {
					int size = filterValues.length();
					convertedFilterValues = new Double[size];
					for (int i = 0; i < size; i++) {
						String fv;
						try {
							fv = filterValues.getString(i);
						} catch (Exception e) {
							fv = null;
						}
						if (fv != null && !"".equals(fv)) convertedFilterValues[i] = new Double(Double.parseDouble(fv));
						else convertedFilterValues[i] = null;
					}
				} else if (filterValue != null && !"".equals(filterValue))
					convertedFilterValue = new Double(Double.parseDouble(filterValue));

				if (value != null && !"".equals(value))
					convertedValue = new Double(Double.parseDouble(value.toString()));
			} catch (NumberFormatException e) {
				convertedValue = null;
				convertedFilterValue = null;
				convertedFilterValues = null;
			}

			if (convertedValue != null && convertedFilterValues != null) {
				for (int i = 0; i < convertedFilterValues.length; i++) {
					if (convertedValue.doubleValue() == convertedFilterValues[i].doubleValue()) return true;
				}
				return false;
			} if (convertedValue != null && convertedFilterValue != null) {
				return convertedValue.doubleValue() == convertedFilterValue.doubleValue();
			} else if (filterValues != null && filterValues.length() > 0)
				return false;			
			else if (convertedFilterValue != null)
				return false;
			else
				return true;
		}

		return true;
    }
}
