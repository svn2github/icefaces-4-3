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

/**
 *  Dialog Widget
 */
if (!window.ice['ace']) {
    window.ice.ace = {};
}
if (!ice.ace.DialogPanels) ice.ace.DialogPanels = {};
ice.ace.Dialog = function(parentID, cfg) {
	var id = parentID + "_main";

    var callee = arguments.callee, prevAceDialog = callee[id], jqo;
    ice.ace.DialogPanels[id] = prevAceDialog;
    if (prevAceDialog) {
        jqo = prevAceDialog.jq;
        if (jqo.dialog("isOpen")) {
            jqo.dialog("close", {type: "close", synthetic: true});
        }
    }
    cfg.height = cfg.height || "auto";
    this.id = id;
    this.cfg = cfg;
    this.jqId = ice.ace.escapeClientId(id);
    this.jq = ice.ace.jq(this.jqId);

    var _self = this, closable = this.cfg.closable;
	var root = this.jq.get(0);
	
	var hasSpecificWidth = function(node) {
		if (node == root) return false;
		var width = ice.ace.jq(node).css('width');
		if (width != '100%' && width != 'auto' && width.substring(0,1) != '0') return true;
		else return hasSpecificWidth(node.parentNode);
	}

	if (!cfg.width) {
		var tables = this.jq.find('.ui-datatable');
		var tableWithoutSpecificWidthFound = false;
		for (var i = 0; i < tables.length; i++) {
			if (!hasSpecificWidth(tables.eq(i).get(0))) {
				tableWithoutSpecificWidthFound = true;
				break;
			}
		}
		if (tableWithoutSpecificWidthFound) {
			cfg.width = '98%';
		} else {
			cfg.width = 'auto';
		}
	}
	
    if (closable == false) {
        this.cfg.closeOnEscape = false;
    }

    // disable unsupported effects
    if (this.cfg.hide == 'pulsate') {
        this.cfg.hide = null;
    } else {
        var browser = ice.ace.Dialog.browser();
        if (browser == 'ie7' || browser == 'ie8') {
            var hide = this.cfg.hide;
            if (hide) {
                if (hide == 'highlight' || hide == 'bounce')
                    this.cfg.hide = null;
            }
        }
    }
	if (this.cfg.show == 'explode') {
        var browser = ice.ace.Dialog.browser();
        if (browser == 'ie7' || browser == 'ie8') {
			this.cfg.show = null;
		}
	}

    //Remove scripts to prevent duplicate widget issues
    this.jq.find("script").remove();
	
	if (this.cfg.relativeTo) {
		var relativeToElement = ice.ace.jq(ice.ace.escapeClientId(this.cfg.relativeTo)).get(0);
		if (relativeToElement != null) {
			this.cfg.position = {my: this.cfg.dialogPosition, at: this.cfg.relativePosition, of: relativeToElement, collision: 'none'};
		}
	}

    //Create the dialog
    this.cfg.autoOpen = false;
    this.jq.dialog(this.cfg);
	
	// set style attribute
	var dialogParent = this.jq.parent();
	var style = dialogParent.attr('style');
	dialogParent.attr('style', style + ';' + this.cfg.dialogStyle);

    ice.onElementUpdate(parentID, function() {
        if (_self.cfg.isVisible){
            _self.hide();
        }  else {
            _self.jq.dialog('close');
        }
        _self.setupEventHandlers(id);
    });
  //Event handlers
    this.jq.bind('dialogclose', function(event, ui) {
        _self.onHide(event, ui);
    });
    this.jq.bind('dialogopen', function(event, ui) {
        _self.onShow(event, ui);
    });


    //Hide close icon if dialog is not closable
    if (closable == false) {
        this.jq.parent().find('.ui-dialog-titlebar-close').hide();
    }

    //Hide header if showHeader is false
    if (this.cfg.showHeader == false) {
        this.jq.parent().children('.ui-dialog-titlebar').hide();
    }
    if (this.cfg.isVisible){
        this.show();
    }
    if (this.cfg.isVisible==false){
        this.hide();
    }
    callee[id] = this;
};

ice.ace.Dialog.prototype.setupEventHandlers = function(id){
    //Event handlers
    if (ice.ace.DialogPanels && ice.ace.DialogPanels[id]) {
        this.jq.bind('dialogclose', function(event, ui) {
            ice.ace.DialogPanels[id].onHide(event, ui);
        });
        this.jq.bind('dialogopen', function(event, ui) {
            ice.ace.DialogPanels[id].onShow(event, ui);
        });
    }
};


ice.ace.Dialog.prototype.show = function() {
	var self = this;
    var focusOn = this.cfg.setFocus;
    setTimeout(function() {
       var dialogParent = self.jq.parent();
       if (dialogParent.hasClass("ace-dialog-hidden")) {
                dialogParent.removeClass("ace-dialog-hidden");
                dialogParent.children().removeClass("ace-dialog-hidden");
        }
        self.jq.dialog('open');
		self.resizeMaps();
        if ('**none' != focusOn) {
            self.focusInput(focusOn);
        }
    }, 1);
    setTimeout(function() {
		self.recreateChildEditors();
    }, 1);
};

ice.ace.Dialog.prototype.hide = function() {
	var self = this;
    setTimeout(function(){
        if (self.cfg.isVisible){
            self.jq.dialog('close');
            var oldClass = self.jq.dialogClass;
            self.cfg.isVisible=false;
            var dialogParent = self.jq.parent();
            if (dialogParent.hasClass("ace-dialog")) {
                dialogParent.removeClass("ace-dialog");
                dialogParent.addClass("ace-dialog-hidden");
            }
        }
    },1);
};

/**
 * Invokes user provided callback
 */
ice.ace.Dialog.prototype.onShow = function(event, ui) {
    if (this.cfg.onShow) {
        this.cfg.onShow.call(this, event, ui);
    }
};

/**
 * Fires an ajax request to invoke a closeListener passing a CloseEvent
 */
ice.ace.Dialog.prototype.onHide = function(event, ui) {
    if (typeof event.originalEvent != 'undefined') {
        this.ajaxHide();
        if (event.originalEvent.synthetic) return;
    } else {
        if (event.synthetic) return;
    }

    if (this.cfg.onHide) {
        this.cfg.onHide.call(this, event, ui);
    }
};

ice.ace.Dialog.prototype.ajaxHide = function() {
    if (this.cfg.behaviors) {
        var closeBehavior = this.cfg.behaviors['close'];
        if (closeBehavior) {
            ice.ace.ab(closeBehavior);
        }
    }
}


ice.ace.Dialog.prototype.focusInput = function(id) {
	var self = this;
	setTimeout(function() {
		if (id) {
			document.getElementById(id).focus();
		} else {
			self.focusFirstInput();
		}
	}, 150);
};

ice.ace.Dialog.prototype.focusFirstInput = function() {
    this.jq.find(':not(:submit):not(:button):input:visible:enabled:first').focus();
};

ice.ace.Dialog.prototype.recreateChildEditors = function() {
	var editors = this.jq.find('.ice-ace-richtextentry');
	editors.each(function(){ice.ace.richtextentry.registry[this.id]();});
};

ice.ace.Dialog.prototype.resizeMaps = function() {
	var maps = this.jq.find('.ice-ace-gmap');
	maps.each(function(){
		var id = this.id;
		if (id) {
			id = id.replace(/_wrapper$/, '');
			var map = ice.ace.gMap.getGMapWrapper(id).getRealGMap();
			var center = map.getCenter();
			google.maps.event.trigger(map, 'resize');
			map.setCenter(center);
		}
	});
}

ice.ace.Dialog.browser = function() {
    if (ice.ace.jq.browser.msie)
        if (ice.ace.jq.browser.version < 8) {
            if (navigator.userAgent.indexOf("Trident/5") < 0) // detects IE9, regardless of compatibility mode
                return 'ie7';
        } else {
            if (ice.ace.jq.browser.version < 9)
                return 'ie8';
        }
    return '';
};
