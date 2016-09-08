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

ice.ace.GrowlMessages = function (id, cfg, nativeNotifications) {
    function runGrowlNotifications() {
        var jq = ice.ace.jq, msgs = cfg.msgs,
            i, opts;

        jq.jGrowl.defaults.pool = cfg.pool;
        jq.jGrowl.defaults.closer = cfg.closer;
        jq.jGrowl.defaults.openDuration = cfg.openDuration;
        jq.jGrowl.defaults.closeDuration = cfg.closeDuration;
        jq.jGrowl.defaults.closeTemplate = "<span class='ui-icon ui-icon-circle-close'></span>";
        jq.jGrowl.defaults.closerTemplate = "<div>close all</div>";

        opts = {header: cfg.header, group: cfg.group, position: cfg.position, glue: cfg.glue, life: cfg.life};

        for (i = 0; i < msgs.length; i++) {
            opts.icon = msgs[i].icon;
            opts.themeState = msgs[i].state;
            opts.sticky = msgs[i].sticky;
            jq.jGrowl(msgs[i].text, opts);
        }

        ice.onElementUpdate(id, function () {
            var instance = jq('#jGrowl').data('jGrowl.instance');
            instance && instance.shutdown();
            jq('#jGrowl').remove();
        });
    }

    function runNativeNotifications() {
        for (var i = 0, l = cfg.msgs.length; i < l; i++) {
            new Notification(cfg.header, {
                body: cfg.msgs[i].text,
                sticky: cfg.msgs[i].sticky
            });
        }
    }

    if (nativeNotifications && window.Notification) {
        window.Notification.requestPermission(function (permission) {
            if (permission === "granted") {
                runNativeNotifications();
            } else {
                runGrowlNotifications();
            }
        })
    } else {
        runGrowlNotifications();
    }
};
