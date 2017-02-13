/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package org.icefaces.ace.component.ajax;

import java.io.Serializable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorHint;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletContext;

/**
 * <p class="changed_added_2_0">An instance of the class is used to 
 * manage {@link AjaxBehavior} instances.</p>
 *
 * @since 2.0
 */
public class AjaxBehaviors implements Serializable {

    private static final String AJAX_BEHAVIORS = "org.icefaces.ace.component.ajax.AjaxBehaviors";

    private LinkedList<BehaviorInfo> behaviorStack = null;

    public AjaxBehaviors() {
        behaviorStack = new LinkedList<BehaviorInfo>();
    }

    // Returns the AjaxBehaviors instance, creating it if necessary.
    public static AjaxBehaviors getAjaxBehaviors(FacesContext context,
                                                 boolean createIfNull) {

        Map<Object, Object> attrs = context.getAttributes();
        AjaxBehaviors ajaxBehaviors = (AjaxBehaviors)attrs.get(AJAX_BEHAVIORS);

        if ((ajaxBehaviors == null) && createIfNull) {
            ajaxBehaviors = new AjaxBehaviors();
            attrs.put(AJAX_BEHAVIORS, ajaxBehaviors);
        }

        return ajaxBehaviors;
    }

    // Adds AjaxBehaviors to the specified ClientBehaviorHolder
    public void addBehaviors(ClientBehaviorHolder behaviorHolder) {
        if ((behaviorStack == null) || behaviorStack.isEmpty()){
            return;
        }

        // Loop over pushed Behaviors and add to the ClientBehaviorHolder.
        // Note that we add most recently pushed behaviors first.  That
        // way the nearest behaviors take precedence.  Behaviors that were
        // pushed earlier won't be added since we'll already have a 
        // submitting behavior attached.
        int count = behaviorStack.size();
        for (int i = count - 1; i >= 0; i--) {
            behaviorStack.get(i).addBehavior(behaviorHolder);
        }
    }


    /**
     * <p>Push the AjaxBehavior into scope making it available 
     * for subsequent calls to getBehaviorForEvent.</p>
     *
     * @param ctx the FaceletContext
     * @param ajaxBehavior the AjaxBehavior instance
     * @param eventName the name of the event that the behavior is associated
     *     with.
     *
     * @since 2.0
     */ 
    public void pushBehavior(FaceletContext ctx,
                             AjaxBehaviorHandler handler,
                             String eventName) {
        behaviorStack.add(new BehaviorInfo(ctx, handler, eventName));
    }

    /**
     * <p>Pop the last {@link AjaxBehavior} instance 
     * from the <code>List</code>.</p>
     *
     * @since 2.0
     */
    public void popBehavior() {
         if (behaviorStack.size() > 0) {
             behaviorStack.removeLast();
         }
    }   

    // Helper class for storing and creating/applying inherited
    // AjaxBehaviors
    public static class BehaviorInfo {
        private FaceletContext ctx;
        private AjaxBehaviorHandler handler;
        private String eventName;

        public BehaviorInfo(FaceletContext ctx,
                            AjaxBehaviorHandler handler,
                            String eventName) {
            this.ctx = ctx;
            this.handler = handler;
            this.eventName = eventName;
        }

        public void addBehavior(ClientBehaviorHolder behaviorHolder) {

            String eventName = this.eventName;
            if (eventName == null) {
                eventName = behaviorHolder.getDefaultEventName();

                // No event name, default or otherwise - we're done
                if (eventName == null) {
                    return;
                }
            }

            // We only add the 
            if (shouldAddBehavior(behaviorHolder, eventName)) {
                ClientBehavior behavior = createBehavior(behaviorHolder);
                behaviorHolder.addClientBehavior(eventName, behavior);
            }

        }

        // Tests whether we should add an AjaxBehavior to the specified
        // ClientBehaviorHolder/event name.
        private boolean shouldAddBehavior(ClientBehaviorHolder behaviorHolder,
                                          String eventName) {

            // First need to make sure that this ClientBehaviorHolder
            // supports the specified event type.
            if (!behaviorHolder.getEventNames().contains(eventName)) {
                return false;
            }

            // Check for a submitting behavior already attached.
            // If we've already got one, we don't add another.
            Map<String,List<ClientBehavior>> allBehaviors =
                behaviorHolder.getClientBehaviors();
            List<ClientBehavior> eventBehaviors = allBehaviors.get(eventName);

            if ((eventBehaviors == null) || (eventBehaviors.isEmpty())) {
                return true;
            }

            for (ClientBehavior behavior : eventBehaviors) {
                Set<ClientBehaviorHint> hints = behavior.getHints();

                if (hints.contains(ClientBehaviorHint.SUBMITTING)) {
                    return false;
                }
            }

            return true;
        }

        // Creates the AjaxBehavior
        private ClientBehavior createBehavior(ClientBehaviorHolder behaviorHolder) {

			ClientBehavior behavior = this.handler.createAjaxBehavior(ctx, (UIComponent) behaviorHolder, eventName);

            return behavior;
        }

        private BehaviorInfo() {
        }
    }

}
