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
ice.ace.TriStateCheckbox = function(clientId, options) {
	ice.ace.TriStateCheckbox.register(clientId, options.groupId);
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

	ice.ace.TriStateCheckbox.setResetValue(clientId, options.checkboxButtons);

    // References
    this.button = ice.ace.jq(this.buttonSelector);
    this.icon = ice.ace.jq(this.iconSelector);
    var self = this,
        event = ice.ace.util.getEvent();

	if (!options.disabled) {
		// Event Binding
		ice.ace.jq(this.jqId)
				.on("mouseenter", function() { self.addStateCSSClasses('hover'); })
				.on("mouseleave", function() { self.removeStateCSSClasses('hover') ; });
	}

    if (!options.disabled)
        ice.ace.jq(this.jqId).on("click", function () {
			if (self.options.checkboxButtons) {
				if (self.options.mutuallyExclusive) {
					ice.ace.TriStateCheckbox.toggleOthers(self.options, self.id);
				}
				self.toggleCheckbox(true);
			} else {
				ice.ace.TriStateCheckbox.toggleOthers(self.options, self.id);
				self.toggleCheckbox(true);
			}
        });

    if (options.ariaEnabled)
        ice.ace.jq(this.jqId).on("keypress", function(e) { self.onAriaKeypress(e); });

    if (!options.disabled && event && event.type == "mouseover")
        this.addStateCSSClasses('hover');

    var unload = function() {
        // Unload events
        ice.ace.jq(self.jqId).off("click mouseenter mouseleave keypress");
    };

    ice.onElementUpdate(this.id, unload);
};

ice.ace.TriStateCheckbox.register = function(clientId, groupId) {
    var groups = ice.ace.TriStateCheckbox.groups,
        groupId = groupId;
    if (groupId) {
        groups[groupId] = groups[groupId] || {};
		if (!groups[groupId][clientId]) {
			groups[groupId][clientId] = clientId;

			//cleanup id from the registered checkboxes
			ice.onElementUpdate(clientId, function () {
				delete groups[groupId][clientId];
			});
		}
    }
};

ice.ace.TriStateCheckbox.prototype.isChecked = function() {
    if (this.options.checkboxButtons) {
        return (!!ice.ace.jq(this.optionSelector).attr('selected'));
    } else {
		return ice.ace.jq(this.fieldSelector).val() == 'checked' ? true : false;
	}
};

ice.ace.TriStateCheckbox.prototype.getState = function() {
	var field = ice.ace.jq(this.fieldSelector);
	if (!field.val()) return 'unchecked';
	else if (field.val() == 'indeterminate') return 'indeterminate';
	else if (field.val() == 'checked') return 'checked';
	else return 'unchecked';
};

ice.ace.TriStateCheckbox.prototype.setState = function(state) {
    if (this.options.checkboxButtons) {
        var option = ice.ace.jq(this.optionSelector);
        if (!option.attr('selected')) {
            option.attr('selected', "selected");
        } else {
            option.removeAttr('selected');
        }
        option[0].parentNode.click();
    } else {
		ice.ace.jq(this.fieldSelector).val(state);
	}
};

ice.ace.TriStateCheckbox.prototype.addStateCSSClasses = function(state) {
    if (state == 'hover') {
        this.button.addClass('ui-state-hover');
    }
    else if (state == 'indeterminate') {
		this.button.removeClass('ice-tristatecheckbox-unchecked')
                 .removeClass('ice-tristatecheckbox-checked')
                 .addClass('ice-tristatecheckbox-indeterminate');
        this.icon.removeClass('fa-square-o')
                 .removeClass('fa-check-square-o')
                 .addClass('fa-minus-square-o');
    }
    else if (state == 'checked') {
		this.button.removeClass('ice-tristatecheckbox-unchecked')
                 .removeClass('ice-tristatecheckbox-indeterminate')
                 .addClass('ice-tristatecheckbox-checked');
        this.icon.removeClass('fa-square-o')
                 .removeClass('fa-minus-square-o')
                 .addClass('fa-check-square-o');
    }
};

ice.ace.TriStateCheckbox.prototype.removeStateCSSClasses = function(state) {
    if (state == 'hover') {
        this.button.removeClass('ui-state-hover');
    }
    else if (state == 'checked') {
		this.button.removeClass('ice-tristatecheckbox-checked')
                 .removeClass('ice-tristatecheckbox-indeterminate')
                 .addClass('ice-tristatecheckbox-unchecked');
        this.icon.removeClass('fa-check-square-o')
                 .removeClass('fa-square-o')
                 .addClass('fa-square-o');
    }
};

ice.ace.TriStateCheckbox.prototype.onAriaKeypress = function (e) {
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

ice.ace.TriStateCheckbox.prototype.toggleCheckbox = function (activeButton) {
	var state = this.getState();
    var newState;

	if (activeButton) {
		if (this.options.indeterminateBeforeChecked) {
			if (state == 'unchecked') newState = 'indeterminate';
			else if (state == 'indeterminate') newState = 'checked';
			else if (state == 'checked') newState = 'unchecked';
		} else {
			if (state == 'unchecked') newState = 'checked';
			else if (state == 'checked') newState = 'indeterminate';
			else if (state == 'indeterminate') newState = 'unchecked';
		}
	} else { // avoid toggling if in indeterminate and unchecked states when triggered by other checkbox in the group
		if (state == 'checked') newState = 'unchecked';
		else return;
	}

    this.setState(newState);
    if (newState != 'unchecked') this.addStateCSSClasses(state);
    else this.removeStateCSSClasses('checked');

    if (this.options.ariaEnabled && newState == 'checked') {
        ice.ace.jq(this.innerSpanSelector).attr("aria-checked", true);
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

if (!ice.ace.TriStateCheckbox.groups) ice.ace.TriStateCheckbox.groups = {};

ice.ace.TriStateCheckbox.toggleOthers = function (options, clientId) {
	var self = ice.ace.instance(clientId);
	if (!self) return;
	var state = self.getState();
	// only toggle others if this checkbox is transitioning to the checked state
	if (self.options.indeterminateBeforeChecked) {
		if (state != 'indeterminate') return;
	} else {
		if (state != 'unchecked') return;
	}
    var groups = ice.ace.TriStateCheckbox.groups,
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

ice.ace.TriStateCheckbox.clear = function(id, ariaEnabled, multiple) {
	if (typeof ice.ace.resetValues[id] == 'undefined') ice.ace.TriStateCheckbox.setResetValue(id, multiple);

    var jqId = ice.ace.escapeClientId(id);
    var innerSpanSelector = jqId + " > span > span";
    var buttonSelector = jqId + " > span > span > button";
    var iconSelector = buttonSelector + " > span.fa";

	if (multiple) {
		var optionSelector = ice.ace.jq("option[title='" + id + "']");
		ice.ace.jq(optionSelector).removeAttr('selected');
	} else {
		var fieldSelector = jqId + " > input";
		ice.ace.jq(fieldSelector).val('false');
	}

	ice.ace.jq(buttonSelector).removeClass('ice-tristatecheckbox-checked')
			 .addClass('ice-tristatecheckbox-unchecked');
	ice.ace.jq(iconSelector).removeClass('fa-check-square-o')
			 .addClass('fa-square-o');

    if (ariaEnabled) {
        ice.ace.jq(innerSpanSelector).attr("aria-checked", false);
    }
};

ice.ace.TriStateCheckbox.reset = function(id, ariaEnabled, multiple) {
	var value = ice.ace.resetValues[id];
	if (ice.ace.isSet(value)) {
		var jqId = ice.ace.escapeClientId(id);
		var innerSpanSelector = jqId + " > span > span";
		var buttonSelector = jqId + " > span > span > button";
		var iconSelector = buttonSelector + " > span.fa";

		if (multiple) {
			var optionSelector = ice.ace.jq("option[title='" + id + "']");
			if (value === true) ice.ace.jq(optionSelector).attr('selected', 'selected');
			else ice.ace.jq(optionSelector).removeAttr('selected');
		} else {
			var fieldSelector = jqId + " > input";
			if (value === true) ice.ace.jq(fieldSelector).val('true');
			else ice.ace.jq(fieldSelector).val('false');
		}

		if (value === true) {
			ice.ace.jq(buttonSelector).removeClass('ice-tristatecheckbox-unchecked')
					 .addClass('ice-tristatecheckbox-checked');
			ice.ace.jq(iconSelector).removeClass('fa-square-o')
					 .addClass('fa-check-square-o');
		} else {
			ice.ace.jq(buttonSelector).removeClass('ice-tristatecheckbox-checked')
					 .addClass('ice-tristatecheckbox-unchecked');
			ice.ace.jq(iconSelector).removeClass('fa-check-square-o')
					 .addClass('fa-square-o');
		}

		if (ariaEnabled) {
			if (value === true) ice.ace.jq(innerSpanSelector).attr("aria-checked", true);
			else ice.ace.jq(innerSpanSelector).attr("aria-checked", false);
		}
	} else ice.ace.TriStateCheckbox.setResetValue(id, multiple);
};

ice.ace.TriStateCheckbox.setResetValue = function(id, multiple) {
	if (typeof ice.ace.resetValues[id] == 'undefined') {
		var jqId = ice.ace.escapeClientId(id);
		if (multiple) {
			var optionSelector = ice.ace.escapeClientId(multiple) + " >> option[title='" + id + "']";
			if (ice.ace.jq(optionSelector).attr('selected'))
				ice.ace.setResetValue(id, true);
			else
				ice.ace.setResetValue(id, false);
		} else {
			var fieldSelector = jqId + " > input";
			var initialValue = ice.ace.jq(fieldSelector).val();
			if (initialValue === 'true') ice.ace.setResetValue(id, true);
			else ice.ace.setResetValue(id, false);
		}
	}
};