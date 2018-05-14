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

package org.icefaces.ace.event;

import org.icefaces.ace.component.datatable.DataTable;

import javax.faces.event.AjaxBehaviorEvent;

public class DataTableFindEvent extends AjaxBehaviorEvent {

    public DataTableFindEvent(AjaxBehaviorEvent event) {
		super(event.getComponent(), event.getBehavior());
    }

	public DataTable getDataTable() {
		return (DataTable) getComponent();
	}

    public int findRow(String query, String[] fields, int startRow, DataTable.SearchType searchType, boolean caseSensitive) {
		return ((DataTable) getComponent()).findRow(query, fields, startRow, searchType, caseSensitive);
    }

    public int findRow(String query, String[] fields, int startRow, DataTable.SearchType searchType) {
        return findRow(query, fields, startRow, searchType, true);
    }

    public int findRow(String query, String[] fields, int startRow) {
        return findRow(query, fields, startRow, DataTable.SearchType.CONTAINS, true);
    }
}
