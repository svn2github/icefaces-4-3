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

package org.icefaces.ace.generator.context;

import org.icefaces.ace.generator.artifacts.TagHandlerArtifact;
import org.icefaces.ace.meta.annotation.Component;
import org.icefaces.ace.meta.annotation.OnlyType;
import org.icefaces.ace.meta.annotation.TagHandler;

public class TagHandlerContext extends MetaContext {
	public TagHandlerContext(Class clazz) {
		super(clazz);
	}

    @Override
    protected void setupArtifacts() {
        artifacts.put(TagHandlerArtifact.class.getName(), new TagHandlerArtifact(this));
    }

    @Override
    protected boolean isRelevantClass(Class clazz) {
        return clazz.isAnnotationPresent(TagHandler.class);
    }

    @Override
    protected boolean isAllowedPropertyOnlyType(OnlyType onlyType) {
        return OnlyType.JSF.equals(onlyType);
    }

    @Override
    protected boolean isPropertyValueDisinherited(Class clazz, String name) {
        return false;
    }
}
