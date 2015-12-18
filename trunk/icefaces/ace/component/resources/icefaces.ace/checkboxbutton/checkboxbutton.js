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

// Constructor
ice.ace.checkboxbutton = function(clientId, options) {
	ice.ace.checkboxbutton.register(clientId, options.groupId);
    this.options = options;

    // Selectors
    this.id = clientId;
    this.jqId = ice.ace.escapeClientId(clientId);
    this.spanSelector = this.jqId + " > span";
    this.innerSpanSelector = this.jqId + " > span > span";
    this.fieldSelector = this.jqId + " > input";
    this.buttonSelector = this.jqId + " > span > span > button";
    this.iconSelector = this.buttonSelector + " > span.fa";
    if (options.checkboxButtons) {
        this.optionSelector = ice.ace.escapeClientId(options.checkboxButtons) + " >> option[title='" + this.id + "']";
    }

    // References
    this.button = ice.ace.jq(this.buttonSelector);
    this.icon = ice.ace.jq(this.iconSelector);
    var self = this,
        event = ice.ace.util.getEvent();

    // Event Binding
    ice.ace.jq(this.jqId)
            .on("mouseenter", function() { self.addStateCSSClasses('hover'); })
            .on("mouseleave", function() { self.removeStateCSSClasses('hover') ; });

    if (!options.disabled)
        ice.ace.jq(this.jqId).on("click", function () {
			if (self.options.checkboxButtons) {
				if (self.options.mutuallyExclusive) {
					ice.ace.checkboxbutton.toggleOthers(self.options, self.id);
				}
				self.toggleCheckbox(true);
			} else {
				ice.ace.checkboxbutton.toggleOthers(self.options, self.id);
				self.toggleCheckbox(true);
			}
        });

    if (options.ariaEnabled)
        ice.ace.jq(this.jqId).on("keypress", function(e) { self.onAriaKeypress(e); });

    if (event.type == "mouseover")
        this.addStateCSSClasses('hover');

    var unload = function() {
        // Unload events
        ice.ace.jq(self.jqId).off("click mouseenter mouseleave keypress");
    };

    ice.onElementUpdate(this.id, unload);
};

ice.ace.checkboxbutton.register = function(clientId, groupId) {
    var groups = ice.ace.checkboxbutton.groups,
        groupId = groupId;
    if (groupId) {
        groups[groupId] = groups[groupId] || {};
        groups[groupId][clientId] = clientId;
    }
};

ice.ace.checkboxbutton.prototype.isChecked = function() {
    if (this.options.checkboxButtons) {
        return (!!ice.ace.jq(this.optionSelector).attr('selected'));
    } else {
		return ice.ace.jq(this.fieldSelector).val() == 'true' ? true : false;
	}
};

ice.ace.checkboxbutton.prototype.setChecked = function(bool) {
    if (this.options.checkboxButtons) {
		if (!ice.ace.jq(this.optionSelector).attr('selected'))
			ice.ace.jq(this.optionSelector).attr('selected', "selected");
		else
			ice.ace.jq(this.optionSelector).removeAttr('selected');
    } else {
		ice.ace.jq(this.fieldSelector).val(bool == true ? 'true' : 'false');
	}
};

ice.ace.checkboxbutton.prototype.addStateCSSClasses = function(state) {
    if (state == 'hover') {
        this.button.addClass('ui-state-hover');
    }
    else if (state == 'checked') {
		this.button.removeClass('ice-checkboxbutton-unchecked')
                 .addClass('ice-checkboxbutton-checked');
        this.icon.removeClass('fa-square-o')
                 .addClass('fa-check-square-o');
    }
};

ice.ace.checkboxbutton.prototype.removeStateCSSClasses = function(state) {
    if (state == 'hover') {
        this.button.removeClass('ui-state-hover');
    }
    else if (state == 'checked') {
		this.button.removeClass('ice-checkboxbutton-checked')
                 .addClass('ice-checkboxbutton-unchecked');
        this.icon.removeClass('fa-check-square-o')
                 .addClass('fa-square-o');
    }
};

ice.ace.checkboxbutton.prototype.onAriaKeypress = function (e) {
    var isSpace = e.keyCode == 32;
    var submittedValue = this.isChecked();

    if (isSpace) {
        var innerSpan = ice.ace.jq(this.innerSpanSelector);

        if (submittedValue) {
            innerSpan.attr("aria-checked", true);
        } else {
            innerSpan.attr("aria-checked", false);
        }
    }
};

ice.ace.checkboxbutton.prototype.toggleCheckbox = function (activeButton) {
    var newValue = !this.isChecked();

    this.setChecked(newValue);
    if (newValue == true) this.addStateCSSClasses('checked');
    else this.removeStateCSSClasses('checked');

    if (this.options.ariaEnabled) {
        ice.ace.jq(this.innerSpanSelector).attr("aria-checked", newValue);
    }

    if (this.options.behaviors) {
		if (this.options.behaviors.action) {
			if (activeButton) {
				ice.setFocus(this.id + '_button');
				ice.ace.ab(ice.ace.extendAjaxArgs(
					this.options.behaviors.action,
					{params: this.options.uiParams}
				));
			} else {
				ice.setFocus('');
			}
		}
		if (this.options.behaviors.change) {
			if (activeButton) {
				ice.setFocus(this.id + '_button');
				ice.ace.ab(ice.ace.extendAjaxArgs(
					this.options.behaviors.change,
					{params: this.options.uiParams}
				));
			} else {
				ice.setFocus('');
			}
		}
    }
};

if (!ice.ace.checkboxbutton.groups) ice.ace.checkboxbutton.groups = {};

ice.ace.checkboxbutton.toggleOthers = function (options, clientId) {
    var groups = ice.ace.checkboxbutton.groups,
        groupId = options.groupId,
        id, widget;
    if (groupId) {
        groups[groupId] = groups[groupId] || {};
        for (id in groups[groupId]) {
            if (groups[groupId].hasOwnProperty(id) && id != clientId) {
                widget = ice.ace.instance(id);
                if (widget && widget.isChecked()) {
                    widget.toggleCheckbox();
                }
            }
        }
    }
};

ice.ace.checkboxbutton.clear = function(id, ariaEnabled, multiple) {
    var jqId = ice.ace.escapeClientId(id);
    var innerSpanSelector = jqId + " > span > span";
    var buttonSelector = jqId + " > span > span > button";
    var iconSelector = buttonSelector + " > span.fa";

	if (multiple) {
		var optionSelector = jqId + "_option";
		ice.ace.jq(optionSelector).removeAttr('selected');
	} else {
		var fieldSelector = jqId + " > input";
		ice.ace.jq(fieldSelector).val('false');
	}

	ice.ace.jq(buttonSelector).removeClass('ice-checkboxbutton-checked')
			 .addClass('ice-checkboxbutton-unchecked');
	ice.ace.jq(iconSelector).removeClass('fa-check-square-o')
			 .addClass('fa-square-o');

    if (ariaEnabled) {
        ice.ace.jq(innerSpanSelector).attr("aria-checked", false);
    }
};