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
* 
*/

ice.ace.Resizable = function(id, cfg) {
    var listener = cfg && cfg.behaviors && cfg.behaviors.resize;
    this.id = id;
    this.cfg = cfg;
    this.target = ice.ace.escapeClientId(this.cfg.target);

    if (listener) {
        this.cfg.ajaxResize = true;
    }
    var jqTarget = ice.ace.jq(this.target);
    if(this.cfg.ajaxResize) {
        this.cfg.formId = jqTarget.parents('form:first').attr('id');
    }

    var _self = this;

    this.cfg.stop = function(event, ui) {
        if(_self.cfg.ajaxResize) {
            _self.fireAjaxResizeEvent(event, ui);
        }
    };

    if (cfg.height && cfg.width) {
        var t = jqTarget;
        t.width(cfg.width);
        t.height(cfg.height);
    }

    jqTarget.resizable('destroy');
    jqTarget.resizable(this.cfg);
};

ice.ace.Resizable.prototype.fireAjaxResizeEvent = function(event, ui) {
    var behaviour = this.cfg && this.cfg.behaviors && this.cfg.behaviors.resize;
    var options = {
        source: this.id,
        execute: this.id,
        render: '@none',
        formId: this.cfg.formId
    };

    var params = {};
    params[this.id + '_ajaxResize'] = true;
    params[this.id + '_width'] = ui.helper.width();
    params[this.id + '_height'] = ui.helper.height();

    options.params = params;

    if (behaviour) {
        ice.ace.ab(ice.ace.extendAjaxArgs(
                behaviour,
                ice.ace.clearExecRender(options))
        );
    } else ice.ace.AjaxRequest(options);
};

ice.ace.Resizable.destroy = function (target) {
    var jqTarget = ice.ace.jq(ice.ace.escapeClientId(target));
    jqTarget.resizable('destroy');
};
