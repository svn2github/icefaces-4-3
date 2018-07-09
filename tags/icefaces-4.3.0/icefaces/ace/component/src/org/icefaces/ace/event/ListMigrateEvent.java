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

import org.icefaces.ace.component.list.ACEList;
import java.util.List;

public class ListMigrateEvent extends AjaxBehaviorEvent {

	private ACEList srcList;
	private List<MigrationRecord> migrationRecords;

    public ListMigrateEvent(AjaxBehaviorEvent event, List<MigrationRecord> migrationRecords, ACEList srcList) {
        super(event.getComponent(), event.getBehavior());
		this.migrationRecords = migrationRecords;
		this.srcList = srcList;
    }

	public List<MigrationRecord> getMigrationRecords() { return migrationRecords; }
	public ACEList getSrcList() { return srcList; }

	public static class MigrationRecord {

		private int srcIndex;
		private int destIndex;

		public MigrationRecord(int srcIndex, int destIndex) {
			this.srcIndex = srcIndex;
			this.destIndex = destIndex;
		}

		public int getSrcIndex() { return srcIndex; }
		public int getDestIndex() { return destIndex; }
	}
}
