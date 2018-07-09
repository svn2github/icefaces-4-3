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

import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SortState {
    Map<ACEList, Boolean> stateMap = new HashMap<ACEList, Boolean>();

    public SortState() {}

    public static SortState getSortState(ACEList list) {
        SortState state = new SortState();

        state.saveState(list);

        return state;
    }

    public static SortState getSortStateFromRequest(FacesContext context, ACEList list) {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = list.getClientId(context);
        SortState self = new SortState();
        String sortDir = params.get(clientId + "_sortDir");
		self.saveState(list, Boolean.parseBoolean(sortDir));
        return self;
    }

    public void saveState(ACEList list) {
        stateMap.put(list, new Boolean(list.isSortAscending()));
    }

    public void saveState(ACEList list, Boolean ascending) {
        stateMap.put(list, ascending);
    }

    private void restoreState(ACEList list) {
        Boolean state = stateMap.get(list);
        if (state != null) {
            list.setSortAscending(state);
        }
    }

    public void apply(ACEList list) {
		restoreState(list);
    }
}
