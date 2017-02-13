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

package org.icefaces.resources;

import java.lang.annotation.*;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
@Inherited
public @interface ICEBrowserDependency {
    /**
     * <p>Define the browser that must be used for this dependency to be served.</p>
     *
     * @return the BrowserType 
     */
    public BrowserType browser();

    /**
     * <p class="changed_added_2_0">The <em>resourceName</em> of the
     * resource pointed to by this <code>ResourceDependency</code>.  It
     * is valid to have EL Expressions in the value of this attribute,
     * as long as the expression resolves to an instance of the expected
     * type.</p>
     *
     * @return the name 
     */
    public String name();

    /**
     * <p class="changed_added_2_0">The <em>libraryName</em> in which
     * the resource pointed to by this <code>ResourceDependency</code>
     * resides.  If not specified, defaults to the empty string.  It is
     * valid to have EL Expressions in the value of this attribute, as
     * long as the expression resolves to an instance of the expected
     * type.</p>
     *
     * @return the libraryName 
     */
    public String library() default "";

    /**
     * <p class="changed_added_2_0">The value given for this attribute
     * will be passed as the "target" argument to {@link
     * javax.faces.component.UIViewRoot#addComponentResource(javax.faces.context.FacesContext,
     * javax.faces.component.UIComponent, java.lang.String)}.  If this
     * attribute is specified, {@link
     * javax.faces.component.UIViewRoot#addComponentResource(javax.faces.context.FacesContext,javax.faces.component.UIComponent)}
     * must be called instead, as described above.  It is valid to have
     * EL Expressions in the value of this attribute, as long as the
     * expression resolves to an instance of the expected type.</p>
     *
     * @return the value 
     */
    public String target() default "";
}

