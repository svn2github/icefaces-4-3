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

if (!window.ice['ace']) {
    window.ice.ace = {};
}
if (!ice.ace.DrawerPanels) ice.ace.DrawerPanels = {};
ice.ace.DrawerPanel = function(parentID, cfg) {
	var id = parentID + "_main";

    var callee = arguments.callee, prevAceDrawerPanel = callee[id], jqo;
    ice.ace.DrawerPanels[id] = prevAceDrawerPanel;
    if (prevAceDrawerPanel) {
        jqo = prevAceDrawerPanel.jq;
        if (jqo.dialog("isOpen")) {
            jqo.dialog("close", {type: "close", synthetic: true});
        }
    }
    this.id = id;
    this.cfg = cfg;
    this.jqId = ice.ace.escapeClientId(id);
    this.jq = ice.ace.jq(this.jqId);

    var _self = this;
	var root = this.jq.get(0);
	
	var hasSpecificWidth = function(node) {
		if (node == root) return false;
		var width = ice.ace.jq(node).css('width');
		if (width != '100%' && width != 'auto' && width.substring(0,1) != '0') return true;
		else return hasSpecificWidth(node.parentNode);
	};

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

/*
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
*/

    //Remove scripts to prevent duplicate widget issues
    this.jq.find("script").remove();

/*
    this.positionOnWindowResize = function() {
        _self.jq.dialog({
            position: _self.cfg.position
        })
    };
*/

    //Create the dialog
    this.cfg.autoOpen = false;

	this.cfg.dialogClass = 'ice-ace-drawer';
	this.cfg.resizable = false;
	this.cfg.draggable = false;

	if (this.cfg.container != 'window') {
		this.cfg.container = ice.ace.escapeClientId(this.cfg.container);
	} else {
		this.cfg.container = window;
		this.cfg.dialogClass += ' ice-ace-drawer-fixed';
	}
	

	if (this.cfg.drawerPosition == 'right') {
		this.cfg.position = {my: 'right', at: 'right', of: this.cfg.container, collision: 'none'};
		if (this.cfg.effect == 'drop') {
			this.cfg.show = {effect: 'drop', duration: 500, direction: 'right'};
			this.cfg.hide = {effect: 'drop', duration: 500, direction: 'right'};
		} else if (this.cfg.effect == 'fade') {
			this.cfg.show = {effect: 'fade', duration: 500};
			this.cfg.hide = {effect: 'fade', duration: 500};
		} else {
			this.cfg.show = {effect: 'slide', duration: 500, direction: 'right'};
			this.cfg.hide = {effect: 'slide', duration: 500, direction: 'right'};
		}
	} else if (this.cfg.drawerPosition == 'bottom') {
		this.cfg.position = {my: 'bottom', at: 'bottom', of: this.cfg.container, collision: 'none'};
		if (this.cfg.effect == 'drop') {
			this.cfg.show = {effect: 'drop', duration: 500, direction: 'down'};
			this.cfg.hide = {effect: 'drop', duration: 500, direction: 'down'};
		} else if (this.cfg.effect == 'fade') {
			this.cfg.show = {effect: 'fade', duration: 500};
			this.cfg.hide = {effect: 'fade', duration: 500};
		} else {
			this.cfg.show = {effect: 'slide', duration: 500, direction: 'down'};
			this.cfg.hide = {effect: 'slide', duration: 500, direction: 'down'};
		}
	} else if (this.cfg.drawerPosition == 'top') {
		this.cfg.position = {my: 'top', at: 'top', of: this.cfg.container, collision: 'none'};
		if (this.cfg.effect == 'drop') {
			this.cfg.show = {effect: 'drop', duration: 500, direction: 'up'};
			this.cfg.hide = {effect: 'drop', duration: 500, direction: 'up'};
		} else if (this.cfg.effect == 'fade') {
			this.cfg.show = {effect: 'fade', duration: 500};
			this.cfg.hide = {effect: 'fade', duration: 500};
		} else {
			this.cfg.show = {effect: 'slide', duration: 500, direction: 'up'};
			this.cfg.hide = {effect: 'slide', duration: 500, direction: 'up'};
		}
	} else { // left
		this.cfg.position = {my: 'left', at: 'left', of: this.cfg.container, collision: 'none'};
		if (this.cfg.effect == 'drop') {
			this.cfg.show = {effect: 'drop', duration: 500, direction: 'left'};
			this.cfg.hide = {effect: 'drop', duration: 500, direction: 'left'};
		} else if (this.cfg.effect == 'fade') {
			this.cfg.show = {effect: 'fade', duration: 500};
			this.cfg.hide = {effect: 'fade', duration: 500};
		} else {
			this.cfg.show = {effect: 'slide', duration: 500, direction: 'left'};
			this.cfg.hide = {effect: 'slide', duration: 500, direction: 'left'};
		}
	}



    this.jq.dialog(this.cfg);

	// set style attribute
	var dialogParent = this.jq.parent();
	var style = dialogParent.attr('style');
	dialogParent.attr('style', style + ';' + this.cfg.dialogStyle);

    ice.onElementUpdate(parentID, function() {
        if (_self.cfg.isVisible){
            _self.hide(true);
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

	this.jq.parent().children('.ui-dialog-titlebar').removeClass('ui-corner-all').addClass('ui-state-active');

    //Hide header if showHeader is false
    if (this.cfg.showHeader == false && this.cfg.showHandleClose == false) {
        this.jq.parent().children('.ui-dialog-titlebar').hide();
    } else {
		var headerText = '';
		if (this.cfg.showHandleClose == true) {
			headerText = '<span class="fa fa-bars fa-lg"></span>'
		}
		if (this.cfg.headerText) {
			headerText += this.cfg.headerText;
		}
		var titlebar = this.jq.parent().children('.ui-dialog-titlebar').children('.ui-dialog-title');
		titlebar.html(headerText);
		titlebar.children('span.fa-bars').on('click', function() {
			ice.ace.instance(parentID).hide();
		});
	}
    if (this.cfg.isVisible){
        this.show(true);
    }
    if (this.cfg.isVisible==false){
        this.hide(true);
    }
    callee[id] = this;
};

ice.ace.DrawerPanel.prototype.setupEventHandlers = function(id){
    //Event handlers
    if (ice.ace.DrawerPanels && ice.ace.DrawerPanels[id]) {
        this.jq.bind('dialogclose', function(event, ui) {
            ice.ace.DrawerPanels[id].onHide(event, ui);
        });
        this.jq.bind('dialogopen', function(event, ui) {
            ice.ace.DrawerPanels[id].onShow(event, ui);
        });
    }
};

ice.ace.DrawerPanel.prototype.show = function(synthetic) {
	var self = this;
    var focusOn = this.cfg.setFocus;
    setTimeout(function() {
       self.cfg.isVisible=true;
       var dialogParent = self.jq;
       if (dialogParent.hasClass("ice-ace-drawer-hidden")) {
                dialogParent.removeClass("ice-ace-drawer-hidden");
                dialogParent.children().removeClass("ice-ace-drawer-hidden");
        }
		if (self.cfg.drawerPosition == 'top' || self.cfg.drawerPosition == 'bottom') {
			var width = ice.ace.jq(self.cfg.container).width();
			self.jq.dialog({width: width});
		} else {
			var height = ice.ace.jq(self.cfg.container).height();
			self.jq.dialog({height: height});
		}
        self.jq.dialog('open');
		self.resizeScrollableTables();
		self.resizeMaps();
		self.reRenderScheduleEvents();
        if ('**none' != focusOn) {
            self.focusInput(focusOn);
        }
        ice.ace.jq(window).on('resize', self.positionOnWindowResize);
		if (!synthetic && self.cfg.behaviors) {
			var openBehavior = self.cfg.behaviors['open'];
			if (openBehavior) {
				ice.ace.ab(openBehavior);
			}
		}
    }, 1);
    setTimeout(function() {
		self.recreateChildEditors();
    }, 1);
};

ice.ace.DrawerPanel.prototype.hide = function(synthetic) {
	var self = this;
    setTimeout(function(){
        if (self.cfg.isVisible){
            ice.ace.jq(window).off('resize', self.positionOnWindowResize);
            self.jq.dialog('close');
            var oldClass = self.jq.dialogClass;
            self.cfg.isVisible=false;
            var dialogParent = self.jq.parent();
			if (!synthetic && self.cfg.behaviors) {
				var closeBehavior = self.cfg.behaviors['close'];
				if (closeBehavior) {
					ice.ace.ab(closeBehavior);
				}
			}
/*
            if (dialogParent.hasClass("ice-ace-drawer")) {
                dialogParent.removeClass("ice-ace-drawer");
                dialogParent.addClass("ice-ace-drawer-hidden");
            }
*/
        }
    },1);
};

/**
 * Invokes user provided callback
 */
ice.ace.DrawerPanel.prototype.onShow = function(event, ui) {
    if (typeof event.originalEvent != 'undefined') {
        if (event.originalEvent.synthetic) return;
    } else {
        if (event.synthetic) return;
    }

    if (this.cfg.onShow) {
        this.cfg.onShow.call(this, event, ui);
    }
};

/**
 * Fires an ajax request to invoke a closeListener passing a CloseEvent
 */
ice.ace.DrawerPanel.prototype.onHide = function(event, ui) {
    if (typeof event.originalEvent != 'undefined') {
        if (event.originalEvent.synthetic) return;
    } else {
        if (event.synthetic) return;
    }

    if (this.cfg.onHide) {
        this.cfg.onHide.call(this, event, ui);
    }
};

ice.ace.DrawerPanel.prototype.focusInput = function(id) {
	var self = this;
	setTimeout(function() {
		if (id) {
			document.getElementById(id).focus();
		} else {
			self.focusFirstInput();
		}
	}, 150);
};

ice.ace.DrawerPanel.prototype.focusFirstInput = function() {
    this.jq.find(':not(:submit):not(:button):input:visible:enabled:first').focus();
};

ice.ace.DrawerPanel.prototype.recreateChildEditors = function() {
	var editors = this.jq.find('.ice-ace-richtextentry');
	editors.each(function(){ice.ace.richtextentry.registry[this.id]();});
};

ice.ace.DrawerPanel.prototype.resizeMaps = function() {
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
};

ice.ace.DrawerPanel.prototype.resizeScrollableTables = function() {
	var tables = this.jq.find('.ui-datatable');
	tables.each(function(){
		var id = this.id;
		if (id) {
			var table = ice.ace.instance(id);
			if (table.cfg.scrollable) table.resizeScrolling();
			if (table.cfg.paginator) table.resizePaginator();
		}
	});
};

ice.ace.DrawerPanel.prototype.reRenderScheduleEvents = function() {
	var schedules = this.jq.find('.ice-ace-schedule');
	schedules.each(function(){
		var id = this.id;
		if (id) {
			var schedule = ice.ace.instance(id);
			if (schedule && (schedule.cfg.viewMode == 'week' || schedule.cfg.viewMode == 'day')) {
				schedule.render();
			}
		}
	});
};

ice.ace.DrawerPanel.browser = function() {
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
