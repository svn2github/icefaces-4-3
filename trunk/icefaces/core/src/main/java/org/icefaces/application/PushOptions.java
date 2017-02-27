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

package org.icefaces.application;

import java.util.HashMap;
import java.util.Map;

public class PushOptions {
    private HashMap<String, Object> attributes = new HashMap();

    public PushOptions()  {
        attributes = new HashMap<String, Object>();
    }

    public PushOptions(HashMap<String, Object> attributes)  {
        this.attributes = attributes;
    }

    public PushOptions and(final PushOptions pushOptions) {
        attributes.putAll(pushOptions.attributes);
        return this;
    }

    public Map<String, Object> getAttributes()  {
        return attributes;
    }

}