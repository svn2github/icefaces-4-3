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

package org.icefaces.samples.showcase.example.ace.dataTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.icefaces.ace.component.datatable.DataTable;
import org.icefaces.samples.showcase.dataGenerators.utilityClasses.DataTableData;
import org.icefaces.samples.showcase.util.FacesUtils;

import org.icefaces.ace.event.DataTableFindEvent;
import org.icefaces.ace.model.table.RowState;
import org.icefaces.ace.model.table.RowStateMap;
import org.icefaces.util.JavaScriptRunner;

@ManagedBean(name= DataTableFind.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class DataTableFind implements Serializable {
    public static final String BEAN_NAME = "aceDataTableFind";
	public String getBeanName() { return BEAN_NAME; }

    public String selectedEffectType = "default";
    public String selectedSearchMode = "contains";
    public String selectedFindMode = "row";
    public String searchQuery = "";
    public String[] selectedColumns = new String[]{"name", "id", "chassis", "weight", "acceleration", "mpg", "cost"};
    public int lastFoundIndex = -1;
    private boolean caseSensitive;
    private List<Car> cars;
	private RowStateMap stateMap = new RowStateMap();

    public final SelectItem[] SEARCH_MODES = {new SelectItem("startsWith", "Starts With"),
            new SelectItem("endsWith", "Ends With"),
            new SelectItem("contains", "Contains"),
            new SelectItem("exact", "Exact Match")};

    public final SelectItem[] COLUMNS = {new SelectItem("id", "Id"),
            new SelectItem("name", "Name"),
            new SelectItem("chassis", "Chassis"),
            new SelectItem("weight", "Weight"),
            new SelectItem("acceleration", "Acceleration"),
            new SelectItem("mpg", "MPG"),
            new SelectItem("cost", "Cost")};

    public final SelectItem[] EFFECT_TYPES = {new SelectItem("none", "None"),
            new SelectItem("default", "Default (Highlight)"),
            new SelectItem("pulsate", "Pulsate")};

    public final SelectItem[] FIND_MODES = {new SelectItem("row", "Find Single Row"),
            new SelectItem("cells", "Find All Cells on a Page and Select Rows")};
    
    public DataTableFind() {
        cars = new ArrayList<Car>(DataTableData.getDefaultData());
    }

    public Class getClazz() {
        return getClass();
    }

    public void find(DataTableFindEvent e) {
		if ("row".equals(selectedFindMode)) {
			findRow(e);
		} else if ("cells".equals(selectedFindMode)) {
			findCells(e);
		}
	}

    public void findRow(DataTableFindEvent e) {
        DataTable iceTable = e.getDataTable();

        DataTable.SearchType type = null;
        if (selectedSearchMode.equals("contains"))
            type = DataTable.SearchType.CONTAINS;
        else if (selectedSearchMode.equals("startsWith"))
            type = DataTable.SearchType.STARTS_WITH;
        else if (selectedSearchMode.equals("endsWith"))
            type = DataTable.SearchType.ENDS_WITH;
        else if (selectedSearchMode.equals("exact"))
            type = DataTable.SearchType.EXACT;
        else type = DataTable.SearchType.CONTAINS;

        int newFoundIndex = iceTable.findRow(searchQuery, selectedColumns, lastFoundIndex + 1, type, caseSensitive);

        if (newFoundIndex < 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(iceTable.getClientId(context),
                    new FacesMessage("Search starting at index " + (lastFoundIndex + 1) + " for \"" + searchQuery + "\" did not return a result."));
            return;
        }

        lastFoundIndex = newFoundIndex;

        if (selectedEffectType.equals("default"))
            iceTable.navigateToRow(lastFoundIndex);
        else if (selectedEffectType.equals("pulsate"))
            iceTable.navigateToRow(lastFoundIndex, DataTable.SearchEffect.PULSATE);
        else if (selectedEffectType.equals("none"))
            iceTable.navigateToRow(lastFoundIndex, null);
    }

    public void reset(javax.faces.event.ActionEvent e) {
        lastFoundIndex = -1;
    }
    
    public SelectItem[] getSEARCH_MODES() {
        return SEARCH_MODES;
    }

    public String getSelectedSearchMode() {
        return selectedSearchMode;
    }

    public void setSelectedSearchMode(String selectedSearchMode) {
        this.selectedSearchMode = selectedSearchMode;
    }

    public String[] getSelectedColumns() {
        return selectedColumns;
    }

    public void setSelectedColumns(String[] selectedColumns) {
        this.selectedColumns = selectedColumns;
    }

    public SelectItem[] getCOLUMNS() {
        return COLUMNS;
    }

    public SelectItem[] getEFFECT_TYPES() {
        return EFFECT_TYPES;
    }

    public String getSelectedEffectType() {
        return selectedEffectType;
    }

    public void setSelectedEffectType(String selectedEffectType) {
        this.selectedEffectType = selectedEffectType;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public RowStateMap getStateMap() {
		return stateMap;
	}

    public void setStateMap(RowStateMap stateMap) {
		this.stateMap = stateMap;
	}

    public SelectItem[] getFIND_MODES() {
        return FIND_MODES;
    }

    public String getSelectedFindMode() {
        return selectedFindMode;
    }

    public void setSelectedFindMode(String selectedFindMode) {
        this.selectedFindMode = selectedFindMode;
    }

    public void findCells(DataTableFindEvent e) {
        DataTable iceTable = e.getDataTable();

        DataTable.SearchType type = null;
        if (selectedSearchMode.equals("contains"))
            type = DataTable.SearchType.CONTAINS;
        else if (selectedSearchMode.equals("startsWith"))
            type = DataTable.SearchType.STARTS_WITH;
        else if (selectedSearchMode.equals("endsWith"))
            type = DataTable.SearchType.ENDS_WITH;
        else if (selectedSearchMode.equals("exact"))
            type = DataTable.SearchType.EXACT;
        else type = DataTable.SearchType.CONTAINS;

		ArrayList<Integer> indexes = new ArrayList<Integer>();
		int firstFoundIndex = -1;
		do {
			int newFoundIndex = iceTable.findRow(searchQuery, selectedColumns, lastFoundIndex + 1, type, caseSensitive);

			if (newFoundIndex >= 0) {
				if (firstFoundIndex == -1) {
					indexes.add(new Integer(newFoundIndex));
					firstFoundIndex = newFoundIndex;
					iceTable.navigateToRow(firstFoundIndex, null);
				} else if (newFoundIndex >= (iceTable.getPage() * iceTable.getRows())) {
					break;
				} else {
					indexes.add(new Integer(newFoundIndex));
				}
			} else {
				break;
			}

			lastFoundIndex = newFoundIndex;
		} while (lastFoundIndex >= 0);

        if (indexes.size() == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(iceTable.getClientId(context),
                    new FacesMessage("Search starting at index " + (lastFoundIndex + 1) + " for \"" + searchQuery + "\" did not return a result."));
			return;
        }

		stateMap.setAllSelected(false);
		int originalIndex = iceTable.getRowIndex();
		String indexList = "";
		for (int i = 0; i < indexes.size(); i++) {
			int index = indexes.get(i);
			iceTable.setRowIndex(index);
			indexList += index + ",";
			
			RowState state = stateMap.get(iceTable.getRowData());
			if (state != null) state.setSelected(true);
		}
		iceTable.setRowIndex(originalIndex);
		indexList = indexList.substring(0, indexList.length() - 1);

		JavaScriptRunner.runScript(FacesContext.getCurrentInstance(),
			"(function(){var tableId = ice.ace.escapeClientId('" + iceTable.getClientId() + "');"
				+ "var rowIndexes = [" + indexList + "];"
				+ "for (var i = 0; i < rowIndexes.length; i++) {"
					+ "ice.ace.jq(tableId + '_row_' + rowIndexes[i]).find('td').each(function(){"
					+ "var td = ice.ace.jq(this);"
					+ "if (td.text().indexOf('" + searchQuery + "') > -1) td.effect('highlight');"
					+ "});"
				+ "}"
				+ "})();");
    }

}
