/*
 * Original Code Copyright Prime Technology.
 * Subsequent Code Modifications Copyright 2011-2014 ICEsoft Technologies Canada Corp. (c)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * NOTE THIS CODE HAS BEEN MODIFIED FROM ORIGINAL FORM
 *
 * Subsequent Code Modifications have been made and contributed by ICEsoft Technologies Canada Corp. (c).
 *
 * Code Modification 1: Integrated with ICEfaces Advanced Component Environment.
 * Contributors: ICEsoft Technologies Canada Corp. (c)
 *
 * Code Modification 2: [ADD BRIEF DESCRIPTION HERE]
 * Contributors: ______________________
 * Contributors: ______________________
 */

package org.icefaces.ace.component.celleditor;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;
import javax.faces.context.ResponseWriter;

import org.icefaces.ace.component.datatable.DataTable;
import org.icefaces.ace.component.datatable.DataTableConstants;
import org.icefaces.ace.component.roweditor.RowEditor;
import org.icefaces.ace.model.table.RowState;
import org.icefaces.ace.util.HTML;
import org.icefaces.resources.ICEResourceDependencies;

import java.util.*;

import javax.faces.model.DataModel;
import org.icefaces.ace.model.table.TreeDataModel;

@ICEResourceDependencies({

})
public class CellEditor extends CellEditorBase {
    UIComponent input;
    UIComponent output;
    String rowStateVar;

    @Override
    public void processDecodes(FacesContext context) {
        if (input == null) input = getFacet("input");
        if (output == null) output = getFacet("output");
        if (rowStateVar == null) rowStateVar = getRowStateVar();
        RowState state = (RowState) context.getExternalContext().getRequestMap().get(rowStateVar);

        // Work around MyFaces warning for 'rendered' input w/o incoming value
        if (!state.getActiveCellEditorIds().contains(getId())) {
            input.setRendered(false);
        }

		// avoid processing decodes in edit mode if request wasn't initiated by save edits button
		List<String> selectedEditorIds = state.getActiveCellEditorIds();
		if (selectedEditorIds.contains(getId())) {
			UIComponent table = getParent();
			while (table != null && !(table instanceof DataTable))
				table = table.getParent();

			if (table != null) {
				// check if this is a child row
				DataModel model = ((DataTable)table).getModel();
				TreeDataModel treeDataModel = null;
				boolean inSubrows = false;
                if (model instanceof TreeDataModel) {
                    treeDataModel = (TreeDataModel) model;
				}
				if (treeDataModel != null && treeDataModel.isRootIndexSet()) {
					inSubrows = true;
				}

				int currentRowIndex = ((DataTable)table).getRowIndex();
				String editSubmit = ((DataTable)table).getEditSubmitParameter();

				if (editSubmit != null) {
					if (!inSubrows) {
						int editRowIndex = -1;
						try {
							editRowIndex = Integer.parseInt(editSubmit.trim());
						} catch (Exception e) {}
						if (currentRowIndex == editRowIndex) {
							super.processDecodes(context);
						}
					} else {
						String rootIndex = treeDataModel.getRootIndex();
						if (!"".equals(rootIndex) && editSubmit.equals(rootIndex+"."+currentRowIndex)) {
							super.processDecodes(context);
						}
					}
				} else {
					super.processDecodes(context);
				}
			}
		} else {
			super.processDecodes(context);
		}

        input.setRendered(true);
    }

    String getRowStateVar() {
        UIComponent table = getParent();

        while (table != null && !(table instanceof DataTable))
            table = table.getParent();

        return ((DataTable)table).getRowStateVar();
    }
}