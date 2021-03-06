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

import javax.faces.event.AjaxBehaviorEvent;
import java.util.List;

public class ListMoveEvent extends AjaxBehaviorEvent {

	private List<MoveRecord> moveRecords;

    public ListMoveEvent(AjaxBehaviorEvent event, List<MoveRecord> moveRecords) {
        super(event.getComponent(), event.getBehavior());
		this.moveRecords = moveRecords;
    }

	public List<MoveRecord> getMoveRecords() { return moveRecords; }

	public static class MoveRecord {

		private int oldIndex;
		private int newIndex;

		public MoveRecord(int oldIndex, int newIndex) {
			this.oldIndex = oldIndex;
			this.newIndex = newIndex;
		}

		public int getOldIndex() { return oldIndex; }
		public int getNewIndex() { return newIndex; }
	}
}
