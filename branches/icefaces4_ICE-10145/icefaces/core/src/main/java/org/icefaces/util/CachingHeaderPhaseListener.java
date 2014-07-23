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

package org.icefaces.util;

import org.icefaces.impl.component.NavigationNotifier;

import javax.annotation.PostConstruct;
import javax.faces.FactoryFinder;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import java.io.Serializable;

/**
 * User: Nils Lundquist
 * Date: 5/22/13
 * Time: 9:05 AM
 * Prevent browser from caching ICEfaces GET requests as part of browser history.
 * Returning to the old GET request result and attempt to interact with it will
 * result in a mismatch between the old DOM held by DOM diff, and the DOM being
 * show to the user.
 *
 * Besides returning to a state other than the one they left being confusing for
 * the user, this could cause JS errors if DOM diff attempts to narrow a response
 * to a region that is not in the client DOM.
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class CachingHeaderPhaseListener implements Serializable {
    @PostConstruct
    private void initialize() {
        LifecycleFactory factory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        Lifecycle lifecycle = factory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
        lifecycle.addPhaseListener(new PhaseListener() {
            public void afterPhase(PhaseEvent phaseEvent) {
                ExternalContext ec = phaseEvent.getFacesContext().getExternalContext();
                Object responseObj = ec.getResponse();

                //Attempting to add these headers to a PortletResponse that is not of type ResourceResponse results
                //in the portlet bridge logging warnings.  So we avoid doing it in that particular scenario.
                boolean avoidAddingHeaders = EnvUtils.instanceofPortletResponse(responseObj) &&
                        !EnvUtils.instanceofPortletResourceResponse(responseObj);
                boolean navigationNotifierPresent = ec.getRequestMap().containsKey(NavigationNotifier.class.getName());
                if (!avoidAddingHeaders && !navigationNotifierPresent) {
                    ec.addResponseHeader("Cache-Control", "must-revalidate");
                    ec.addResponseHeader("Pragma", "no-cache");
                    ec.addResponseHeader("Expires", "0");
                }
            }

            public void beforePhase(PhaseEvent phaseEvent) {
            }

            public PhaseId getPhaseId() {
                return PhaseId.RENDER_RESPONSE;
            }
        });
    }
}
