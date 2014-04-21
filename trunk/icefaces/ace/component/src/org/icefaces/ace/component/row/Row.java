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

/*
 * Generated, Do Not Modify
 */

package org.icefaces.ace.component.row;

import org.icefaces.ace.component.datatable.DataTable;
import org.icefaces.ace.util.collections.Predicate;

import javax.el.ELContext;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;
import org.icefaces.resources.ICEResourceDependencies;

@ICEResourceDependencies({

})
public class Row extends RowBase {
    private Predicate predicate;

    /**
     * Return true if this conditional row should render at the given index, either before it or after it.
     * @param index the index of the row being rendered
     * @return True if this conditional row should render.
     */
    public boolean evaluateCondition(int index, DataTable table) {
        String condition = this.getCondition();
        if (condition == null) return false;

        if (predicate == null) {
            if (condition.equals("interval")) {
                predicate = new IntervalPredicate(getInterval());
            }
            else if (condition.equals("group")) {
                predicate = new GroupPredicate(getValueExpression("groupBy"), getPos().equals("before"), table);
            }
            else if (condition.equals("predicate"))
                predicate = getPredicate();
        }

        return predicate.evaluate(index);
    }

    private class IntervalPredicate implements Predicate {
        int interval;

        public IntervalPredicate(int interval) {
            if (interval < 1) throw new IllegalArgumentException();
            this.interval = interval;
        }

        public boolean evaluate(Object index) {
            if (index instanceof Integer) {
                if (((Integer)index % interval) == 0) {
                    return true;
                }
            }
            return  false;
        }
    }

    private class GroupPredicate implements Predicate {
        ValueExpression groupBy;
        boolean before;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext elContext = facesContext.getELContext();
        DataTable table;

        public GroupPredicate(ValueExpression gb, boolean before, DataTable table) {
            groupBy = gb;
            this.before = before;
            this.table = table;
        }

        public boolean evaluate(Object object) {
            int currentIndex = table.getRowIndex();
            Integer index = (Integer) object;

            table.setRowIndex(index);
            Object currentValue = groupBy.getValue(elContext);
            
            if (index == 0 && before)
                return true;
            
            if (before) {
                table.setRowIndex(index - 1);
                Object lastValue = table.isRowAvailable() ? groupBy.getValue(elContext) : null;
                table.setRowIndex(currentIndex);

                if (currentValue.equals(lastValue)) return false;
                else return true;
            } else {
                table.setRowIndex(index + 1);
                Object nextValue = table.isRowAvailable() ? groupBy.getValue(elContext) : null;
                table.setRowIndex(currentIndex);

                if (currentValue.equals(nextValue)) return false;
                else return true;
            }
        }
    }

    public void resetRenderVariables() {
        predicate = null;
    }
}