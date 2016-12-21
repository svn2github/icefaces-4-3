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

package org.icefaces.component;

/**
 * Components that are focus managed and want to specify the element that should receive focus (other than the root element)
 * need to implement this interface.
 */
public interface Focusable {

    /**
     * Provide the client ID of the element that should be focused.
     * @return the ID of the element
     */
     String getFocusedElementId();
}
