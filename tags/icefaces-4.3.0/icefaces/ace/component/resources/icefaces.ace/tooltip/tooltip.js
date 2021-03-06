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

if (!ice.ace.Tooltips) ice.ace.Tooltips = {};
if (!ice.ace.DelegateTooltips) ice.ace.DelegateTooltips = {};
if (!ice.ace.DelegateTooltipObservers) ice.ace.DelegateTooltipObservers = [];
/*
 *  Tooltip Widget
 */
ice.ace.Tooltip = function(id, cfg) {
    var callee = arguments.callee, prevTooltip = callee[id];
    if (prevTooltip) {
         prevTooltip.jq.qtip("destroy");
    }
	// destroy previous qtip instances of this tooltip component if it was global
	ice.ace.jq('[title]').each(function(){
		var api = ice.ace.jq(this).qtip('api');
		if (api) {
			var tooltipId = api.get('id'); 
			if (tooltipId) {
				if (tooltipId.indexOf(id) == 0) api.destroy(true);
			}
		}
	});
	this.cfg = cfg;
	this.target = "";
	this.preventDisplay = false;
	ice.ace.Tooltip.instances[this.cfg.id] = this;

    if (this.cfg.behaviors === undefined)
        this.cfg.behaviors = {};

	if(this.cfg.global) {
		this.target = "[title]";
	}else {
		if (this.cfg.forDelegate) {
			this.target = ice.ace.escapeClientId(this.cfg.forDelegate);
		} else if (this.cfg.forComponent) {
			this.target = ice.ace.escapeClientId(this.cfg.forComponent);
		} else if (this.cfg.forComponents) {
			var arr = this.cfg.forComponents;
			for (var i = 0; i < arr.length; i++) {
				arr[i] = ice.ace.escapeClientId(arr[i]);
			}
			this.target = arr.join(', ');
		}
	}

    this.jq = ice.ace.jq(this.target);
    if (this.jq.length <= 0) {
        return;
    }
	
	var styleClasses = 'ui-widget-content ice-ace-tooltip ui-corner-all';
	var showTip = false;
    if (this.cfg.speechBubble) {
		styleClasses += ' ice-ace-speechbubble';
		showTip = true;
    }
	styleClasses += (this.cfg.styleClass ? ' ' + this.cfg.styleClass : '');
	this.cfg.style = {widget:true, tip:{corner:showTip, width:12, height:12}};
	var inlineStyle = this.cfg.inlineStyle;
	
	var self = this;
	var events = {};
	events.render = function(event, api) {
		var jqTooltip = api.elements.tooltip;
		jqTooltip.addClass(styleClasses);
		var nodeStyle = jqTooltip.attr('style');
		jqTooltip.attr('style', nodeStyle + ';' + inlineStyle);
	};
	events.show = function() { if (!ice.ace.Tooltips[self.cfg.id] && (self.cfg.displayListener || self.cfg.behaviors.display)) { ice.ace.Tooltips[self.cfg.id] = true; self.triggerDisplayListener(); }};
	events.hide = function() { delete ice.ace.Tooltips[self.cfg.id] };
	this.cfg.events = events;
	
	if (ice.ace.jq.browser.msie) {
		var content = ice.ace.jq(ice.ace.escapeClientId(this.cfg.id+'_content'));
		var contentStyle = content.attr('style');
		contentStyle = typeof contentStyle == 'undefined'? '': contentStyle;
		content.css('left', -10000);
		content.attr('style', contentStyle + ' display: block !important;');
		content.find('img').each(function(i,e) {
			var self = ice.ace.jq(e);
			var width = self.width();
			var height = self.height();
			e.width = width;
			e.height = height;
		});
		content.attr('style', contentStyle);
	}

	if (!this.cfg.forDelegate) {
		if (this.cfg.global) {
			var count = 0;
			this.jq.each(function() {
				var eachCfg = {};
				for (var p in self.cfg) {
					eachCfg[p] = self.cfg[p];
				}
				eachCfg.id = self.cfg.id + '_' + count++;
				if (eachCfg.content && eachCfg.content.text && eachCfg.content.text().replace(/^\s+|\s+$/g) != '') {
					ice.ace.jq(this).qtip(eachCfg);
				} else {
					eachCfg.content = { text: ice.ace.jq(this).attr('title') };
					ice.ace.jq(this).qtip(eachCfg);
				}
			});
		} else {
			this.jq.qtip(this.cfg);
		}
	} else {
		delete self.cfg.events.show; delete self.cfg.events.hide; // will call them manually
		ice.ace.DelegateTooltips[self.cfg.id] = {};
		var delegateNode = this.jq.children().get(0);
		this.jq.delegate('*', this.cfg.show.event, function(event) {
			// 'this' in this scope refers to the current DOM node in the event bubble
			if (this === delegateNode) { // event bubbled to the highest point, we can now begin
				var findTargetComponent = function(node) {
					if (node) {
						if (node.id) {
							var endsWithId = false;
							if (self.cfg.forComponents) {
								var forComponents = self.cfg.forComponents;
								var i;
								for (i = 0; i < forComponents.length; i++) {
									if (ice.ace.Tooltip.endsWith(node.id, forComponents[i])) {
										endsWithId = true;
										break;
									}
								}
							} else {
								endsWithId = ice.ace.Tooltip.endsWith(node.id, self.cfg.forComponent);
							}
							if (endsWithId) {
								return node.id;
							} else {
								return findTargetComponent(node.parentNode);
							}
						} else {
							return findTargetComponent(node.parentNode);
						}
					}
					return '';
				};
				var targetComponent = findTargetComponent(event.target);
				if (targetComponent) {
					var instanceId = ice.ace.escapeClientId(targetComponent);
					var jqTargetComponent = ice.ace.jq(instanceId);
					var cfg = ice.ace.jq.extend({}, self.cfg);
					cfg.events.hide = function() { delete ice.ace.DelegateTooltips[self.cfg.id][instanceId]; };
					jqTargetComponent.qtip(cfg);
					var openInstances = ice.ace.DelegateTooltips[self.cfg.id];
					for (var id in openInstances) {
						openInstances[id].qtip('hide');
					}
					ice.ace.DelegateTooltips[self.cfg.id][instanceId] = jqTargetComponent;
					while (ice.ace.DelegateTooltipObservers.length > 0) {
						clearTimeout(ice.ace.DelegateTooltipObservers.shift());
					}
					ice.ace.DelegateTooltipObservers.push(setTimeout(function(){
						self.activeComponent = targetComponent;
						self.currentTooltip = instanceId;
						self.triggerDisplayListener(function() {
							if (self.preventDisplay) { self.preventDisplay = false; return; }
							var instance = ice.ace.DelegateTooltips[self.cfg.id][instanceId];
							if (instance && self.currentTooltip == instanceId) instance.qtip('show');
						});
						self.activeComponent = '';
					}, self.cfg.show.delay));
				}
			}
		});
		this.jq.delegate('*', 'mouseout', function(event) {
			while (ice.ace.DelegateTooltipObservers.length > 0) {
				clearTimeout(ice.ace.DelegateTooltipObservers.shift());
			}			
		});
	}
    callee[id] = this;
};

ice.ace.Tooltip.instances = {};

ice.ace.Tooltip.prototype.triggerDisplayListener = function(callback) {
	var formId = this.jq.parents('form:first').attr('id'),
	    options = {
		source: this.cfg.id,
		execute: this.cfg.id,
		formId: formId,
		async: true
	};

	if (callback) {
		options.onsuccess = callback;
	}
	var params = {};
	if (this.cfg.displayListener) {
		params[this.cfg.id + '_displayListener'] = true;
	}
	if (this.activeComponent) {
		params[this.cfg.id + '_activeComponent'] = this.activeComponent;
	}

	options.params = params;

    var behavior = this.cfg && this.cfg.behaviors && this.cfg.behaviors.display;
    if (behavior) {
        ice.ace.ab(ice.ace.extendAjaxArgs(
                behavior,
                ice.ace.clearExecRender(options)
        ));
    } else ice.ace.AjaxRequest(options);
};

ice.ace.Tooltip.prototype.cancelDisplay = function() {
	this.preventDisplay = true;
};

ice.ace.Tooltip.endsWith = function(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
};

ice.ace.Tooltip.activate = function(config, txt, showConfig, hideConfig) {
	ice.ace.jq(function() {
		config.content = {
			text: txt ? txt : function () {
				return document.getElementById(config.id + '_content').innerHTML;
			}
		};
		config.show = {
			'event': showConfig[0], 'delay': showConfig[1], 'effect': function () {
				ice.ace.jq(this)[showConfig[2]](showConfig[3]);
			}
		};
		config.hide = {
			'event': hideConfig[0], 'delay': hideConfig[1], 'fixed': true, 'effect': function () {
				ice.ace.jq(this)[hideConfig[2]](hideConfig[3]);
			}
		};
		ice.ace.create('Tooltip', [config.id, config]);
	});
};