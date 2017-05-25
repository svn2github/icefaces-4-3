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

import org.icefaces.ace.event.ListSelectEvent;
import org.icefaces.ace.model.filter.*;
import org.icefaces.ace.util.collections.*;
import org.icefaces.ace.util.collections.*;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.NavigationHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.model.*;
import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.util.*;

import org.icefaces.ace.model.SingleExpressionComparator;
import org.icefaces.ace.model.table.SortCriteria;

public class ACEList extends ListBase {
    private static Class SQL_RESULT = null;
    static {
        try {
            SQL_RESULT = Class.forName("javax.servlet.jsp.jstl.sql.Result");
        } catch (Throwable t)  {
            //ignore if sql.result not available
        }
    }

    DataModel model;
    boolean selectItemModel = false;
	transient protected FilterState savedFilterState;
	transient protected SortState savedSortState;

    @Override
    protected DataModel getDataModel() {
        // Return any previously cached DataModel instance
        if (this.model != null) {
            return (model);
        }

        // Synthesize a DataModel around our current value if possible
        Object current = getValue();
        List<SelectItem> selectItems = getSelectItems();

        if (selectItems != null) {
            setSelectItemModel(true);
            setDataModel(new ListDataModel(selectItems));
            return model;
        } else if (current == null) {
            setDataModel(new ListDataModel(Collections.EMPTY_LIST));
        } else if (current instanceof DataModel) {
            setDataModel((DataModel) current);
        } else if (current instanceof List) {
            setDataModel(new ListDataModel((List) current));
        } else if (Object[].class.isAssignableFrom(current.getClass())) {
            setDataModel(new ArrayDataModel((Object[]) current));
        } else if (current instanceof ResultSet) {
            setDataModel(new ResultSetDataModel((ResultSet) current));
        } else if ((null != SQL_RESULT) && SQL_RESULT.isInstance(current)) {
            DataModel dataModel = new ResultDataModel();
            dataModel.setWrappedData(current);
            setDataModel(dataModel);
        } else {
            setDataModel(new ScalarDataModel(current));
        }

        setSelectItemModel(false);

        return (model);
    }

	@Override
	public Object getValue() {
		Object superValue = super.getValue();
		int superValueHash;
		if (superValue != null) superValueHash = superValue.hashCode();
		else return null;

		// If model is altered or new reapply filters / sorting
		if (getValueHashCode() == null || superValueHash != getValueHashCode()) {
			setValueHashCode(superValueHash);

			applySorting();

			if (getFilteredData() != null) {
				applyFilters();
			}
		}

		// If we have filtered data return that instead of the standard collection
		// Lazy case should recalculate filters in the persistence layer with every load
		List filteredValue = getFilteredData();
		if (filteredValue != null)
			return filteredValue;
			else return superValue;
	}

    @Override
    public void setRowIndex(int i) {
        if (i < 0) model = null;
        super.setRowIndex(i);
    }

    @Override
    protected void setDataModel(DataModel newModel) {
        model = newModel;
    }

    private List<SelectItem> getSelectItems() {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();

        for (UIComponent c : getChildren())
            if (c instanceof UISelectItems)
                selectItems.addAll(processUISelectItems(elContext, (UISelectItems) c));
            else if (c instanceof UISelectItem) {
                SelectItem item = processUISelectItem(elContext, (UISelectItem) c);
                if (item != null) selectItems.add(item);
            }

        return selectItems.size() == 0 ? null : selectItems;
    }

    private SelectItem processUISelectItem(ELContext elContext, UISelectItem itemComponent) {
        ValueExpression itemExpression = null;
        SelectItem selectItem = null;

        if (itemComponent.getValue() instanceof SelectItem)
            selectItem = (SelectItem) itemComponent.getValue();
        else if ((itemExpression = itemComponent.getValueExpression("value")) != null &&
                itemExpression.getType(elContext).equals(SelectItem.class)) {
            selectItem = (SelectItem)itemExpression.getValue(elContext);
        } else if (itemComponent.getItemLabel() != null) {
            selectItem = new SelectItem(
                    itemComponent.getItemValue(),
                    itemComponent.getItemLabel(),
                    itemComponent.getItemDescription(),
                    itemComponent.isItemDisabled(),
                    itemComponent.isItemEscaped(),
                    itemComponent.isNoSelectionOption()
            );
        }

        return selectItem;
    }

    private List<SelectItem> processUISelectItems(ELContext elContext, UISelectItems c) {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        UISelectItems itemsComponent = c;
        Object value = itemsComponent.getValue();

        try  {
            if (value == null)
                value = itemsComponent.getValueExpression("value").getValue(elContext);
        } catch (NullPointerException npe) {
            // If ValueExpression is null return empty list
            return selectItems;
        }

        if (value instanceof SelectItem)
            selectItems.add((SelectItem) value);
        else if (value instanceof Collection) {
            Collection values = (Collection) value;

            for (Iterator<Object> itemIter = values.iterator(); itemIter.hasNext();)
                selectItems.add((SelectItem) itemIter.next());
        } else if (value instanceof SelectItem[]) {
            SelectItem[] values = (SelectItem[]) value;

            for (SelectItem item : values)
                selectItems.add(item);
        } else if (value instanceof Map) {
            Map itemMap = (Map)value;

            for (Iterator<Object> keyIter = itemMap.keySet().iterator(); keyIter.hasNext();) {
                Object key = keyIter.next();

                selectItems.add(new SelectItem(
                        itemMap.get(key),
                        key.toString()
                ));
            }
        }

        return selectItems;
    }

    @Override
    public Boolean isDragging() {
        if (isSelectItemModel()) return false;
        else return super.isDragging();
    }

    @Override
    public Boolean isControlsEnabled() {
        if (isSelectItemModel()) return false;
        else return super.isControlsEnabled();
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public Set<Object> getSelections() {
        Set<Object> set = super.getSelections();
        if (set == null) {
            set = new HashSet<Object>();
            setSelections(set);
        }
        return set;
    }

    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);

        FacesContext context = FacesContext.getCurrentInstance();
        String outcome = null;
        MethodExpression me = null;

        if (event instanceof ListSelectEvent) me = getSelectionListener();

        if (me != null) {
            if (!context.isValidationFailed()) {
                outcome = (String) me.invoke(context.getELContext(), new Object[] {event});
            }

            if (outcome != null) {
                NavigationHandler navHandler = context.getApplication().getNavigationHandler();
                navHandler.handleNavigation(context, null, outcome);
                context.renderResponse();
            }
        }
    }

    // References to immigrant objects are gathered immediately
    List<ImmigrationRecord> immigrants;

    public List<ImmigrationRecord> getImmigrants() {
        return immigrants;
    }

    public void setImmigrants(List<ImmigrationRecord> immigrants) {
        this.immigrants = immigrants;
    }

    public boolean isSelectItemModel() {
        return selectItemModel;
    }

    public void setSelectItemModel(boolean isSelect) {
        selectItemModel = isSelect;
    }

	// ---------------------
	// ----- FILTERING -----
	// ---------------------

    public void applyFilters() {
        setApplyingFilters(true);
    }

/*
    @Property(tlddoc = "Enable to force creation of the filtered data set from the bound " +
            "value every render. Alternately attempt to use hashCodes of the " +
            "value property to detect changes and prompt refiltering.",
            defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean constantRefilter;
*/
    public Boolean isApplyingFilters() {
        //if (isConstantRefilter()) return true;

        return super.isApplyingFilters();
    }

    @Override
    public void processUpdates(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (!isRendered()) {
            return;
        }

		super.processUpdates(context);

        pushComponentToEL(context, this);

		if (isFilterRequest(context)) {
			UIComponent filterFacet = getFilterFacet();
			if (filterFacet != null) {
				filterFacet.processUpdates(context);
			}

			// after model values have been updated, we save the filter state if using the filter facet,
			// since the filter values are decoded independetly in this case,
			// so we must now fetch them to use them later at the render response phase
			if (savedFilterState != null) {
				if (getFilterFacet() != null) {
					Object filterValue = getFilterValue();
					Object filterValues = getFilterValues();
					if (filterValue != null || filterValues != null) {
						savedFilterState.saveState(this);
					} else {
						savedFilterState.saveState(this, getFilterValueMin(), getFilterValueMax());
					}
				}
			}
		}

        if (isApplyingFilters()) {
            if (savedFilterState != null)
                savedFilterState.apply(this);
            setFilteredData(processFilters(context));
        }

        if (isApplyingSorts()) {
            if (savedSortState != null)
                savedSortState.apply(this);
            processSorting();
        }

        popComponentFromEL(context);
    }

    protected List processFilters(FacesContext context) {
        try {
			Predicate filterSet = AllPredicate.getInstance(new ArrayList<Predicate>());

			FilterType filterType = getFilterType();
			if (filterType == FilterType.TEXT || !isRangedFilter()) {
				String filterValue = getFilterValue();
				if (filterValue != null && !filterValue.equals("")) {
					filterSet = new PropertyConstraintPredicate(context,
								getValueExpression("filterBy"),
								filterValue,
								getFilterConstraint(),
								this);
				}
			} else {
				Object filterValueMin = getFilterValueMin();
				Object filterValueMax = getFilterValueMax();
				if (filterValueMin != null || filterValueMax != null) {
					filterSet = new RangeConstraintPredicate(context,
								getValueExpression("filterBy"),
								filterValueMin,
								filterValueMax,
								filterType);
				}
			}

            List filteredData = new ArrayList();
            setFilteredData(null);
			try { setDataModel(null); }
			catch (UnsupportedOperationException uoe) {
				//MyFaces doesn't support this method and throws an UnsupportedOperationException
			}

            DataModel model = getDataModel();
            String var = getVar();

            int index = 0;

            // UIData Iteration
            setRowIndex(index);
            while (model.isRowAvailable()) {
                Object rowData = model.getRowData();

                if (var != null) context.getExternalContext().getRequestMap().put(var, rowData);

                if  (filterSet.evaluate(rowData)) {
					filteredData.add(rowData);
                }
                index++;
                setRowIndex(index);
            }

            // Iteration clean up
            setRowIndex(-1);
            if (var != null) context.getExternalContext().getRequestMap().remove(var);
            return  filteredData;
        } finally {
            //setForcedUpdateCounter(getForcedUpdateCounter()+1);
            setApplyingFilters(false);
        }
    }

    public Locale calculateLocale(FacesContext facesContext) {
		Locale locale;
		Object userLocale = getFilterDateLocale();
		if (userLocale != null) {
			if (userLocale instanceof String) {
				String[] tokens = ((String) userLocale).split("_");
				if (tokens.length == 1)
					locale = new Locale(tokens[0], "");
				else
					locale = new Locale(tokens[0], tokens[1]);
			} else if (userLocale instanceof Locale)
				locale = (Locale) userLocale;
			else
				throw new IllegalArgumentException("Type:" + userLocale.getClass() + " is not a valid locale type for column:" + this.getClientId(facesContext));
		} else {
			locale = facesContext.getViewRoot().getLocale();
		}

        return locale;
    }

	public FilterType getFilterType() {
		String type = getType();
		if ("boolean".equalsIgnoreCase(type)) return FilterType.BOOLEAN;
		if ("date".equalsIgnoreCase(type)) return FilterType.DATE;
		if ("byte".equalsIgnoreCase(type)) return FilterType.BYTE;
		if ("short".equalsIgnoreCase(type)) return FilterType.SHORT;
		if ("int".equalsIgnoreCase(type)) return FilterType.INT;
		if ("long".equalsIgnoreCase(type)) return FilterType.LONG;
		if ("float".equalsIgnoreCase(type)) return FilterType.FLOAT;
		if ("double".equalsIgnoreCase(type)) return FilterType.DOUBLE;

		return FilterType.TEXT;
	}

	public FilterConstraint getFilterConstraint() {
		String filterMatchMode = getFilterMatchMode();
		FilterConstraint filterConstraint;

		if (filterMatchMode.equals("startsWith")) {
			filterConstraint = new StartsWithFilterConstraint();
		} else if (filterMatchMode.equals("endsWith")) {
			filterConstraint = new EndsWithFilterConstraint();
		} else if (filterMatchMode.equals("contains")) {
			filterConstraint = new ContainsFilterConstraint();
		} else if (filterMatchMode.equals("exact")) {
			filterConstraint = new ExactFilterConstraint();
		} else {
			filterConstraint = new StartsWithFilterConstraint();
		}

		return filterConstraint;
	}

	protected boolean isFilterRequest(FacesContext x) {
		return isIdPrefixedParamSet("_filtering", x);
	}

    private boolean isIdPrefixedParamSet(String param, FacesContext x) {
        return x.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(x) + param);
    }

	// -------------------
	// ----- SORTING -----
	// -------------------

    public void applySorting() {
        setApplyingSorts(true);
    }

    protected void processSorting() {
        Object value = getValue();
        if (value instanceof List) {
            List list = (List)value;
            SortCriteria criteria = getSortCriteria();
            String rowVar = getVar();

            if (criteria != null) {
                if (list.size() > 0 && list.get(0) instanceof Map.Entry)
                    Collections.sort(list, new EntryKeyComparatorWrapper(new SingleExpressionComparator(criteria, rowVar)));
                else
                    Collections.sort(list, new SingleExpressionComparator(criteria, rowVar));
            }
        }
        //setForcedUpdateCounter(getForcedUpdateCounter()+1);
        setApplyingSorts(false);
    }

    protected SortCriteria getSortCriteria() {
        SortCriteria criteria;
		Comparator<Object> comp = getSortFunction();
		if (comp == null) criteria = new SortCriteria(getValueExpression("sortBy"), isSortAscending());
		else criteria = new SortCriteria(getValueExpression("sortBy"), isSortAscending(), comp);
        return criteria;
    }

    private class EntryKeyComparatorWrapper<T> implements Comparator {
        Comparator<T> comparator;

        public EntryKeyComparatorWrapper(Comparator<T> comparator) {
            this.comparator = comparator;
        }

        public int compare(Object o1, Object o2) {
            return comparator.compare(((Map.Entry<T, Object>) o1).getKey(), ((Map.Entry<T, Object>) o2).getKey());
        }
    }
}
