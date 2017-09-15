/*
 * Copyright 2004-2016 ICEsoft Technologies Canada Corp.
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

if (!window['ice']) window.ice = {};
if (!window.ice['ace']) window.ice.ace = {};

ice.ace.Schedule = function(id, cfg) {
	this.id = id;
	this.jqId = ice.ace.escapeClientId(id);
	this.jqRoot = ice.ace.jq(this.jqId);
	this.jq = this.jqRoot.find('.schedule-main');
	this.cfg = cfg;
	this.messages = cfg.messages;
	this.events = cfg.events;
	var self = this;

    if (!ice.ace.instance(this.id)) {
        ice.onElementUpdate(this.id, function() { self.unload(); });
    }

	// order events according to their server-side index
	this.eventsMap = {};
	var i;
	for (i = 0; i < cfg.events.length; i++) {
		var event = cfg.events[i];
		this.eventsMap[''+event.index] = event;
	}

	this.cfg.currentYear = parseInt(document.getElementById(this.id + '_currentYear').getAttribute('value'), 10);
	this.cfg.currentMonth = parseInt(document.getElementById(this.id + '_currentMonth').getAttribute('value'), 10);
	this.cfg.currentDay = parseInt(document.getElementById(this.id + '_currentDay').getAttribute('value'), 10);

	this.jqRoot.find('.schedule-details-popup-content').attr('title', this.messages.EventDetails); // localise title
	
	this.renderConfiguration = {};
	if (this.cfg.viewMode == 'week') {
		this.renderConfiguration.render = function() { return self.renderWeekView.call(self); };
		this.renderConfiguration.doneRendering = function() { self.renderWeekEvents.call(self); };
	} else if (this.cfg.viewMode == 'day') {
		this.renderConfiguration.render = function() { return self.renderDayView.call(self); };
		this.renderConfiguration.doneRendering = function() { self.renderDayEvents.call(self); };
	} else {
		this.renderConfiguration.render = function() { return self.renderMonthView.call(self); };
		this.renderConfiguration.doneRendering = function() { self.renderMonthEvents.call(self); };
	}

	this.render();

	var behaviors = self.cfg.behaviors;
	this.jqRoot.delegate('.schedule-event', 'click', function(event) {
		event.stopPropagation();
		var node = event['target'];
		var eventIndex = self.extractEventIndex(node);
		var eventData = self.eventsMap[''+eventIndex];
		// add selected styling
		if (self.cfg.viewMode == 'week') {
			self.jqRoot.find('.schedule-selected').removeClass('schedule-selected');
			var dow = self.extractDayOfWeek(node);
			self.jqRoot.find('.schedule-dow-header.schedule-dow-' + dow).addClass('schedule-selected');
		} else {
			self.jqRoot.find('.schedule-selected').removeClass('schedule-selected');
			self.jqRoot.find('.schedule-calendar-day-' + eventData.startDate + ' .schedule-state').addClass('schedule-selected');
		}
		document.getElementById(self.id + '_selectedDate').setAttribute('value', eventData.startDate);
		if (behaviors && behaviors.eventClick) {
			self.sendClickRequest(event, 'event', eventIndex);
		}
	});
	if (cfg.eventDetails != 'disabled') {
		this.jqRoot.delegate('.schedule-event, .schedule-list-event', 'click', function(event) {
			event.stopImmediatePropagation();
			var node = event['target'];
			var eventIndex = self.extractEventIndex(node);
			var eventData = self.eventsMap[''+eventIndex];
			var markup = self.getEventDetailsMarkup(eventData, false,
				self.cfg.isEventEditing, self.cfg.isEventDeletion);
			if (self.cfg.eventDetails == 'sidebar')
				self.displayEventDetailsSidebar(markup, eventData);
			else
				self.displayEventDetailsPopup(markup, eventData);
		});
	}
	if (cfg.displayTooltip) {
		this.jqRoot.delegate('.schedule-event', 'mouseover', function(event) {
			var node = event['target'];
			var eventIndex = self.extractEventIndex(node);
			var eventData = self.eventsMap[''+eventIndex];
			var markup = self.getEventDetailsMarkup(eventData);
			if (node.tagName == 'DIV') {
				self.displayEventDetailsTooltip(markup, node);
			} else {
				self.displayEventDetailsTooltip(markup, node.parentNode);
			}
		});
		this.jqRoot.delegate('.schedule-event', 'mouseout', function(event) {
			self.hideEventDetailsTooltip();
		});
	}
	if (behaviors && behaviors.dayDblclick) {
		this.jqRoot.delegate('.schedule-day', 'dblclick', function(event) {
			var node = event['target'];
			node = node.className.indexOf('schedule-day-number') > -1 ? node.parentNode : node;
			node = node.className.indexOf('schedule-state') > -1 ? node.parentNode : node;
			var date = self.extractEventDate(node);
			self.sendClickRequest(event, 'day', date);
		});
	}
	if (behaviors && behaviors.timeDblclick) {
		this.jqRoot.delegate('.schedule-cell', 'dblclick', function(event) {
			var node = event['target'];
			node = node.className.indexOf('schedule-day-number') > -1 ? node.parentNode : node;
			node = node.className.indexOf('schedule-state') > -1 ? node.parentNode : node;
			var date = self.extractEventDate(node);
			var time = self.extractEventTime(node);
			self.sendClickRequest(event, 'time', date + ' ' + time);
		});
	}
	if (self.cfg.isEventAddition) {
		this.jqRoot.delegate('.schedule-day, .schedule-cell', 'dblclick', function(event) {
			event.stopPropagation();
			var date, time;
			var node = event['target'];
			node = node.className.indexOf('schedule-day-number') > -1 ? node.parentNode : node;
			node = node.className.indexOf('schedule-state') > -1 ? node.parentNode : node;
			date = self.extractEventDate(node);
			time = self.extractEventTime(node);
			var eventData = {startDate: date, startTime: time, endDate: date, endTime: '', title: '', location: '', notes: '', index: ''};
			var markup = self.getEventDetailsMarkup(eventData, true, false, false);
			if (self.cfg.eventDetails == 'sidebar')
				self.displayEventDetailsSidebar(markup, eventData);
			else
				self.displayEventDetailsPopup(markup, eventData);
		});
		if (self.cfg.viewMode == 'week') {
			this.jqRoot.delegate('.schedule-dow-header', 'dblclick', function(event) {
				event.stopPropagation();
				var node = ice.ace.jq(event['target']);
				var dow = parseInt(self.extractDayOfWeek(node.get(0)), 10);

				var currentDate = new Date();
				currentDate.setFullYear(self.cfg.currentYear);
				currentDate.setMonth(self.cfg.currentMonth);
				currentDate.setDate(self.cfg.currentDay + dow);
				var date = currentDate.getFullYear() + '-' + self.addLeadingZero(currentDate.getMonth() + 1) + '-' + self.addLeadingZero(currentDate.getDate());

				var eventData = {startDate: date, startTime: '', endDate: date, endTime: '', title: '', location: '', notes: '', index: ''};
				var markup = self.getEventDetailsMarkup(eventData, true, false, false);
				if (self.cfg.eventDetails == 'sidebar')
					self.displayEventDetailsSidebar(markup, eventData);
				else
					self.displayEventDetailsPopup(markup, eventData);
			});
		}
		if (self.cfg.viewMode == 'day') {
			this.jqRoot.delegate('.schedule-dow-header', 'dblclick', function(event) {
				event.stopPropagation();

				var date = self.cfg.currentYear + '-' + self.addLeadingZero(self.cfg.currentMonth + 1) + '-' + self.addLeadingZero(self.cfg.currentDay);

				var eventData = {startDate: date, startTime: '', endDate: date, endTime: '', title: '', location: '', notes: '', index: ''};
				var markup = self.getEventDetailsMarkup(eventData, true, false, false);
				if (self.cfg.eventDetails == 'sidebar')
					self.displayEventDetailsSidebar(markup, eventData);
				else
					self.displayEventDetailsPopup(markup, eventData);
			});
		}
	}
	this.jqRoot.delegate('.schedule-state', 'mouseenter mouseover', function(event) {
		event.stopPropagation();
		var node = ice.ace.jq(event['target']);
		var stateNode = node.closest('.schedule-state');
		if (stateNode.hasClass('schedule-state') && !stateNode.hasClass('ui-state-hover')) {
			stateNode.addClass('ui-state-hover');
			if (stateNode.hasClass('ui-state-highlight')) {
				stateNode.removeClass('ui-state-highlight');
				stateNode.addClass('ui-state-highlight-disabled');
			}
		}
	});
	this.jqRoot.delegate('.schedule-state', 'mouseleave', function(event) {
		event.stopPropagation();
		var node = ice.ace.jq(event['target']);
		var stateNode = node.closest('.schedule-state');
		if (stateNode.hasClass('schedule-state') && stateNode.hasClass('ui-state-hover')) {
			self.jqRoot.find('.schedule-state').removeClass('ui-state-hover');
			if (stateNode.hasClass('ui-state-highlight-disabled')) {
				stateNode.removeClass('ui-state-highlight-disabled');
				stateNode.addClass('ui-state-highlight');
			}
		}
	});
	if (this.cfg.viewMode == 'month' || this.cfg.viewMode == 'week') {
		this.jqRoot.delegate('.schedule-state', 'click', function(event) {
			event.stopPropagation();
			var node = ice.ace.jq(event['target']);
			var stateNode = node.closest('.schedule-state');
			if (stateNode.hasClass('schedule-state')) {
				// add selected styling
				if (self.cfg.viewMode == 'week') {
					self.jqRoot.find('.schedule-selected').removeClass('schedule-selected');
					var dow = self.extractDayOfWeek(stateNode.parent().get(0));
					self.jqRoot.find('.schedule-dow-header.schedule-dow-' + dow).addClass('schedule-selected');
				} else {
					self.jqRoot.find('.schedule-selected').removeClass('schedule-selected');
					stateNode.addClass('schedule-selected');
				}
				var date = self.extractEventDate(stateNode.parent().get(0));
				document.getElementById(self.id + '_selectedDate').setAttribute('value', date);
			}
		});
	}
	if (this.cfg.viewMode == 'week') { // allow day selection on day headers
		this.jqRoot.delegate('.schedule-dow-header', 'click', function(event) {
			event.stopPropagation();
			var node = ice.ace.jq(event['target']);
			// add selected styling
			self.jqRoot.find('.schedule-selected').removeClass('schedule-selected');
			var dow = parseInt(self.extractDayOfWeek(node.get(0)), 10);
			self.jqRoot.find('.schedule-dow-header.schedule-dow-' + dow).addClass('schedule-selected');

			var currentDate = new Date();
			currentDate.setFullYear(self.cfg.currentYear);
			currentDate.setMonth(self.cfg.currentMonth);
			currentDate.setDate(self.cfg.currentDay + dow);
			var date = currentDate.getFullYear() + '-' + self.addLeadingZero(currentDate.getMonth() + 1) + '-' + self.addLeadingZero(currentDate.getDate());
			document.getElementById(self.id + '_selectedDate').setAttribute('value', date);
		});
	}
	this.jqRoot.delegate('.schedule-list-title', 'click', function(event) {
		self.expandEventList();
	});
	this.jqRoot.delegate('.schedule-details-title', 'click', function(event) {
		self.expandEventDetails();
	});
	if (self.cfg.eventDetails == 'popup') {
		this.jqRoot.on('keydown', function(e) {
			if (e.keyCode == 27) {
				ice.ace.jq(document.getElementById(self.id)).find('.schedule-details-popup-content').dialog('close');
			}
		});
	}
};

ice.ace.Schedule.prototype.render = function() {
	var mainContainer = this.jqRoot.find('.schedule-main');
	mainContainer.children().remove();
	mainContainer.html(this.renderConfiguration.render.apply(this, []));

	this.addNavigationListeners();
	this.addResizeListeners();

	this.renderConfiguration.doneRendering.apply(this, []);
};

ice.ace.Schedule.prototype.extractEventIndex = function(node) {
	if (node.tagName == 'SPAN') node = node.parentNode; // event text in month view
	var result = 0;
	var classes = node.className.split(' ');
	var i;
	for (i = 0; i < classes.length; i++) {
		var styleClass = classes[i];
		if (styleClass.indexOf('schedule-event-') == 0 && styleClass != 'schedule-event-allday') {
			result = styleClass.substring(15);
			break;
		}
	}
	return result;
};

ice.ace.Schedule.prototype.extractEventDate = function(node) {
	var result = '';
	var classes = node.className.split(' ');
	var i;
	for (i = 0; i < classes.length; i++) {
		var styleClass = classes[i];
		if (styleClass.indexOf('schedule-calendar-day-') == 0) {
			result = styleClass.substring(22);
			break;
		}
	}
	return result;
};

ice.ace.Schedule.prototype.extractEventTime = function(node) {
	var result = '';
	var classes = node.className.split(' ');
	var i;
	for (i = 0; i < classes.length; i++) {
		var styleClass = classes[i];
		if (styleClass.indexOf('schedule-time-') == 0) {
			result = styleClass.substring(14);
			break;
		}
	}
	if (result) result = result.substring(0,2) + ':' + result.substring(2);
	return result;
};

ice.ace.Schedule.prototype.extractDayOfWeek = function(node) {
	if (node.tagName == 'SPAN') node = node.parentNode; // event text in month view
	var result = '';
	var classes = node.className.split(' ');
	var i;
	for (i = 0; i < classes.length; i++) {
		var styleClass = classes[i];
		if (styleClass.indexOf('schedule-dow-') == 0 && styleClass != 'schedule-dow-header') {
			result = styleClass.substring(13);
			break;
		}
	}
	return result;
};

ice.ace.Schedule.prototype.extractOverlappingLevel = function(node) {
	if (node.tagName == 'SPAN') node = node.parentNode; // event text in month view
	var result = 0;
	var classes = node.className.split(' ');
	var i;
	for (i = 0; i < classes.length; i++) {
		var styleClass = classes[i];
		if (styleClass.indexOf('schedule-overlapping-') == 0) {
			result = styleClass.substring(21);
			break;
		}
	}
	return result;
};

ice.ace.Schedule.prototype.getEventDetailsMarkup = function(data, isEventAddition, isEventEditing, isEventDeletion) {
	if (data) {
		var markup = '';
		var msgs = this.messages;
		var id = this.id;
		var replaceSpaces = this.replaceSpaces;
		var escapeHtml = this.escapeHtml;
		if (isEventAddition || isEventEditing) {
			markup += '<div class="ui-state-error ui-corner-all" style="display:none;">' + msgs.ERROR1 + '</div>';
			markup += '<div class="ui-state-error ui-corner-all" style="display:none;">' + msgs.ERROR2 + '</div>';
			markup += '<div class="ui-state-error ui-corner-all" style="display:none;">' + msgs.ERROR3 + '</div>';
			markup += '<table><tr><td><label for="' + id + '_title">' + msgs.Title + ':</label></td><td><input type="text" name="'+id+'_title" id="'+id+'_title" value="'+ escapeHtml(data.title)+'"/></td></tr><tr><td><label for="' + id + '_date">' + replaceSpaces(msgs.StartDate) + ':</label></td><td><input type="text" name="'+id+'_date" id="'+id+'_date" value="'+data.startDate+'"/></td></tr><tr><td>' + replaceSpaces(msgs.StartTime) + ':</td><td>'+this.getHourSelectionMarkup(data.startTime, data.isAllDay)+'&nbsp;:&nbsp;'+this.getMinuteSelectionMarkup(data.startTime, data.isAllDay)+'</td></tr><tr><td><label for="' + id + '_endDate">' + replaceSpaces(msgs.EndDate) + ':</label></td><td><input type="text" name="'+id+'_endDate" id="'+id+'_endDate" value="'+data.endDate+'"/></td></tr><tr><td>' + replaceSpaces(msgs.EndTime) + ':</td><td>'+this.getHourSelectionMarkup(data.endTime, data.isAllDay)+'&nbsp;:&nbsp;'+this.getMinuteSelectionMarkup(data.endTime, data.isAllDay)+'</td></tr><tr><td><label for="' + id + '_allDay">' + msgs.AllDayEvent + ':</label></td><td><input type="checkbox" name="'+id+'_allDay" id="'+id+'_allDay" '+(data.isAllDay?'checked':'')+'/></td></tr><tr><td><label for="' + id + '_location">' + msgs.Location + ':</label></td><td><input type="text" name="'+id+'_location" id="'+id+'_location" value="'+escapeHtml(data.location)+'"/></td></tr><tr><td><label for="' + id + '_notes">' + msgs.Notes + ':</label></td><td><textarea name="'+id+'_notes" id="'+id+'_notes">'+escapeHtml(data.notes)+'</textarea></td></tr></table><input type="hidden" name="'+this.id+'_index" value="'+data.index+'"/>';
			if (isEventEditing) {
				markup += '<input type="hidden" name="'+this.id+'_old_startDate" value="'+data.startDate+'"/><input type="hidden" name="'+this.id+'_old_startTime" value="'+data.startTime+'"/><input type="hidden" name="'+this.id+'_old_endDate" value="'+data.endDate+'"/><input type="hidden" name="'+this.id+'_old_endTime" value="'+data.endTime+'"/><input type="hidden" name="'+this.id+'_old_allDay" value="'+data.isAllDay+'"/><input type="hidden" name="'+this.id+'_old_title" value="'+escapeHtml(data.title)+'"/><input type="hidden" name="'+this.id+'_old_location" value="'+escapeHtml(data.location)+'"/><input type="hidden" name="'+this.id+'_old_notes" value="'+escapeHtml(data.notes)+'"/>';
			}
		} else {
			markup = '<table><tr><td>' + msgs.Title + ':</td><td>'+escapeHtml(data.title)+'</td></tr><tr><td>' + replaceSpaces(msgs.StartDate) + ':</td><td>'+data.startDate+'</td></tr><tr><td>' + replaceSpaces(msgs.StartTime) + ':</td><td>'+(data.isAllDay ? msgs.ALLDAY : data.startTime)+'</td></tr><tr><td>' + replaceSpaces(msgs.EndDate) + ':</td><td>'+data.endDate+'</td></tr><tr><td>' + replaceSpaces(msgs.EndTime) + ':</td><td>'+(data.isAllDay ? msgs.ALLDAY : data.endTime)+'</td></tr><tr><td>' + msgs.Location + ':</td><td>'+escapeHtml(data.location)+'</td></tr><tr><td>' + msgs.Notes + ':</td><td>'+escapeHtml(data.notes)+'</td></tr></table>';
		}
		if (data.styleClass) markup += '<input type="hidden" name="'+this.id+'_styleClass" value="'+data.styleClass+'"/>';
		if (data.id) markup += '<input type="hidden" name="'+this.id+'_id" value="'+data.id+'"/>';
		if (isEventAddition) markup += '<button onclick="var s = ice.ace.instance(\''+this.id+'\');'
			+ 'if (s.validateInputs()) { s.sendEditRequest(event, \'add\'); } return false;">' + msgs.Add + '</button>';
		else {
			if (isEventEditing) {
				var closeDetailsMarkup = '';
				if (this.cfg.eventDetails == 'popup') {
					closeDetailsMarkup = 'ice.ace.jq(document.getElementById(\''+this.id+'\')).find(\'.schedule-details-popup-content\').dialog(\'close\');';
				} else if (this.cfg.eventDetails == 'sidebar') {
					closeDetailsMarkup = 'ice.ace.instance(\''+this.id+'\').expandEventList();';
				}
				markup += '<button onclick="var s = ice.ace.instance(\''+this.id+'\');'
					+ 'if (s.validateInputs()) { s.sendEditRequest(event, \'edit\');'
					+ closeDetailsMarkup + '} return false;">' + msgs.Save + '</button> ';
			}
			if (isEventDeletion) markup += '<span><button onclick="ice.ace.instance(\''+this.id+'\').confirmDeletion(this);return false;">' + msgs.Delete + '</button><span role="alert" style="display:none;"><span>' + msgs.AreYouSure + ' </span><br/><button onclick="ice.ace.instance(\''+this.id+'\').sendEditRequest(event, \'delete\');return false;">' + msgs.Yes + '</button> <button onclick="ice.ace.instance(\''+this.id+'\').cancelDeletion(this);return false;">' + msgs.No + '</button></span></span>';
		}
		return markup;
	} else {
		return '<div>No Data</div>';
	}
}

ice.ace.Schedule.prototype.getHourSelectionMarkup = function(time, isAllDay) {
	var hour = time.substring(0,2);
	var markup = '<select aria-label="' + this.messages.AriaSelectHour + '"><option value="hh">hh</option>';
	var i;
	for (i = 0; i < 24; i++) {
		var value = ( i < 10 ? '0' : '') + i;
		var selected = hour == value && !isAllDay ? ' selected' : '';
		markup += '<option value="'+value+'"'+selected+'>'+value+'</option>';
	}
	markup += '<select>';
	return markup;
};

ice.ace.Schedule.prototype.getMinuteSelectionMarkup = function(time, isAllDay) {
	var minute = time.substring(3);
	var markup = '<select aria-label="' + this.messages.AriaSelectMinute + '"><option value="mm">mm</option>';
	var i;
	for (i = 0; i < 60; i = i + 5) {
		var value = ( i < 10 ? '0' : '') + i;
		var selected = minute == value && !isAllDay ? ' selected' : '';
		markup += '<option value="'+value+'"'+selected+'>'+value+'</option>';
	}
	markup += '<select>';
	return markup;
};

ice.ace.Schedule.prototype.addTimeParameters = function(params) {
	var detailsContainerClass = this.cfg.eventDetails == 'sidebar' ?
		'.schedule-details-sidebar-content' : '.schedule-details-popup-content';
	var timeInputs = this.jqRoot.find(detailsContainerClass).find('select');

	if (timeInputs.size() >= 4) {
		var startHour = timeInputs.get(0).value;
		var startMinute = timeInputs.get(1).value;
		var endHour = timeInputs.get(2).value;
		var endMinute = timeInputs.get(3).value;

		if (startHour == 'hh') params[this.id + "_time"] = '';
		else {
			if (startMinute == 'mm') startMinute = '00';
			params[this.id + "_time"] = startHour + ':' + startMinute;
		}

		if (endHour == 'hh') params[this.id + "_endTime"] = '';
		else {
			if (endMinute == 'mm') endMinute = '00';
			params[this.id + "_endTime"] = endHour + ':' + endMinute;
		}
	}
};

ice.ace.Schedule.prototype.confirmDeletion = function(button) {
	ice.ace.jq(button).hide().siblings().show();
};

ice.ace.Schedule.prototype.cancelDeletion = function(button) {
	ice.ace.jq(button.parentNode).hide().siblings().show();
};

ice.ace.Schedule.prototype.displayEventDetailsPopup = function(markup, event) {
	var eventDetails = ice.ace.jq(this.jqId).find('.schedule-details-popup-content');
	eventDetails.find('*').off(); // remove previous event handlers
	eventDetails.html(markup);
	eventDetails.dialog({dialogClass: 'schedule-details-popup', resizable: false, width: 320});
	var root = ice.ace.jq(this.jqId);
	root.find('.schedule-details-popup .ui-dialog-titlebar').attr('id', this.id + '_detailsPopupTitle');
	root.find('.schedule-details-popup .ui-dialog-content').attr('id', this.id + '_detailsPopupContent');
	root.find('.schedule-details-popup').attr('role', 'dialog')
		.attr('aria-labelledby', this.id + '_detailsPopupTitle')
		.attr('aria-describedby', this.id + '_detailsPopupContent');
	eventDetails.find('input[type="text"]:eq(1),input[type="text"]:eq(2)').datepicker({dateFormat: 'yy-mm-dd'});
	eventDetails.find('button').button();
	if (!event.isAllDay) this.addDefaultDurationFunctionality();
	this.addAllDayFunctionality();
};

ice.ace.Schedule.prototype.addDefaultDurationFunctionality = function() {
	var self = this;
	var displayLocation = self.cfg.eventDetails == 'sidebar' ? 'sidebar-' : 'popup-';
	var timeInputs = ice.ace.jq(this.jqId).find('.schedule-details-'+displayLocation+'content').find('select');

	if (timeInputs.size() >= 4) {
		var startHour = timeInputs.get(0);
		var startMinute = timeInputs.get(1);
		var endHour = timeInputs.get(2);
		var endMinute = timeInputs.get(3);
		var startDate = ice.ace.jq(self.jqId).find('.schedule-details-'+displayLocation+'content')
			.find('input[type="text"]:eq(1)');
		var endDate = ice.ace.jq(self.jqId).find('.schedule-details-'+displayLocation+'content')
			.find('input[type="text"]:eq(2)');

		var addDefaultDuration = function() {
			if (startHour.value != 'hh' && startMinute.value != 'mm') {
				var startDateValue = startDate.datepicker('getDate');
				if (startDateValue) {
					var endDateValue = new Date();
					endDateValue.setFullYear(startDateValue.getFullYear());
					endDateValue.setMonth(startDateValue.getMonth());
					endDateValue.setDate(startDateValue.getDate());
					endDateValue.setHours(parseInt(startHour.value, 10));
					endDateValue.setMinutes(parseInt(startMinute.value, 10) + self.cfg.defaultDuration);

					if (endHour.value == 'hh' && endMinute.value == 'mm' && (endDate.val() == startDate.val())) {
						endDate.datepicker('setDate', endDateValue.getFullYear() + '-'
							+ (endDateValue.getMonth() + 1) + '-'
							+ endDateValue.getDate());
						var endHours = endDateValue.getHours();
						ice.ace.jq(endHour).val(endHours < 10 ? '0' + endHours : '' + endHours);
						var endMinutes = endDateValue.getMinutes();
						ice.ace.jq(endMinute).val(endMinutes < 10 ? '0' + endMinutes : '' + endMinutes);
					}
				}
			}
		};

		ice.ace.jq(startHour).add(startMinute).on('change', function() {
			addDefaultDuration();
		});

		addDefaultDuration();
	}
};

ice.ace.Schedule.prototype.addAllDayFunctionality = function() {
	var self = this;
	var displayLocation = self.cfg.eventDetails == 'sidebar' ? 'sidebar-' : 'popup-';
	var timeInputs = ice.ace.jq(this.jqId).find('.schedule-details-'+displayLocation+'content').find('select');

	if (timeInputs.size() >= 4) {
		var allDayCheckbox = ice.ace.jq(this.jqId).find('.schedule-details-'+displayLocation+'content').find('input[type="checkbox"]');
		if (allDayCheckbox.size() > 0) {
			var applyAllDayFunctionality = function() {
				if (allDayCheckbox.get(0).checked) {
					// disable
					timeInputs.prop('disabled', true);
					allDayCheckbox.attr('value', 'true');
				} else {
					// enable
					timeInputs.prop('disabled', false);
					allDayCheckbox.attr('value', 'false');
				}
			};

			allDayCheckbox.on('change', function() {
				applyAllDayFunctionality();
			});

			applyAllDayFunctionality();
		}
	}
};

ice.ace.Schedule.prototype.displayEventDetailsSidebar = function(markup, event) {
	var eventDetails = ice.ace.jq(this.jqId).find('.schedule-details-sidebar-content');
	eventDetails.attr('role', 'dialog').attr('aria-labelledby', this.id + '_detailsSidebarTitle');
	eventDetails.find('*').off(); // remove previous event handlers
	eventDetails.html(markup);
	this.expandEventDetails();
	eventDetails.find('input[type="text"]:eq(1),input[type="text"]:eq(2)').datepicker({dateFormat: 'yy-mm-dd'});
	eventDetails.find('button').button();
	if (!event.isAllDay) this.addDefaultDurationFunctionality();
	this.addAllDayFunctionality();
};

ice.ace.Schedule.prototype.validateInputs = function() {
	var displayLocation = this.cfg.eventDetails == 'sidebar' ? 'sidebar-' : 'popup-';
	var dateInputs = ice.ace.jq(this.jqId).find('.schedule-details-'+displayLocation+'content').find('input.hasDatepicker');
	var startDateInput = dateInputs.get(0);
	var endDateInput = dateInputs.get(1);
	var errorMessages = ice.ace.jq(this.jqId).find('.schedule-details-'+displayLocation+'content').find('.ui-state-error');
	var startDateErrorMessage = errorMessages.get(0);
	var endDateErrorMessage = errorMessages.get(1);
	var logicalErrorMessage = errorMessages.get(2);

	var valid = true;
	var dateRegex = /^\d{4}-\d{2}-\d{2}$/;

	if (startDateInput && endDateInput) {
		// validate start date format yyyy-mm-dd
		if (!startDateInput.value.match(dateRegex)) {
			valid = false;
			if (startDateErrorMessage) startDateErrorMessage.setAttribute('style', 'display:block;');
		} else {
			if (startDateErrorMessage) startDateErrorMessage.setAttribute('style', 'display:none;');
		}
		// validate end date format yyyy-mm-dd
		if (!endDateInput.value.match(dateRegex)) {
			valid = false;
			if (endDateErrorMessage) endDateErrorMessage.setAttribute('style', 'display:block;');
		} else {
			if (endDateErrorMessage) endDateErrorMessage.setAttribute('style', 'display:none;');
		}
		// if formats are valid, make sure that the end date is later than the start date
		if (valid) {
			var timeInputs = ice.ace.jq(this.jqId).find('.schedule-details-'+displayLocation+'content').find('select');
			if (timeInputs.size() >= 4) {
				var startHour = parseInt(ice.ace.jq(timeInputs.get(0)).val(), 10);
				var startMinute = parseInt(ice.ace.jq(timeInputs.get(1)).val(), 10);
				var endHour = parseInt(ice.ace.jq(timeInputs.get(2)).val(), 10);
				var endMinute = parseInt(ice.ace.jq(timeInputs.get(3)).val(), 10);

				var startHour = isNaN(startHour) ? 0 : startHour;
				var startMinute = isNaN(startMinute) ? 0 : startMinute;
				var endHour = isNaN(endHour) ? 0 : endHour;
				var endMinute = isNaN(endMinute) ? 0 : endMinute;

				var startYear = startDateInput.value.substring(0,4);
				var startMonth = parseInt(startDateInput.value.substring(5,7), 10) - 1;
				var startDay = startDateInput.value.substring(8,10);
				var startDate = new Date(startYear, startMonth, startDay, startHour, startMinute, 0, 0);

				var endYear = endDateInput.value.substring(0,4);
				var endMonth = parseInt(endDateInput.value.substring(5,7), 10) - 1;
				var endDay = endDateInput.value.substring(8,10);
				var endDate = new Date(endYear, endMonth, endDay, endHour, endMinute, 0, 0);

				if (endDate.getTime() < startDate.getTime()) {
					valid = false;
					if (logicalErrorMessage) logicalErrorMessage.setAttribute('style', 'display:block;');
				} else {
					if (logicalErrorMessage) logicalErrorMessage.setAttribute('style', 'display:none;');
				}
			}
		} else {
			if (logicalErrorMessage) logicalErrorMessage.setAttribute('style', 'display:none;');
		}
	}

	return valid;
};

ice.ace.Schedule.prototype.displayEventDetailsTooltip = function(markup, node) {
	var eventDetails = ice.ace.jq(this.jqId).find('.schedule-details-tooltip-content');
	eventDetails.html(markup);
	eventDetails.dialog({resizable: false, draggable: false, dialogClass: 'schedule-details-tooltip', 
		position: { my: "left top", at: "right top", of: node }});
	ice.ace.jq(this.jqId).find('.schedule-details-tooltip').attr('role', 'tooltip').show();
};

ice.ace.Schedule.prototype.hideEventDetailsTooltip = function() {
	ice.ace.jq(this.jqId).find('.schedule-details-tooltip').hide();
};

ice.ace.Schedule.prototype.sendNavigationRequest = function(event, year, month, day, oldYear, oldMonth, oldDay, type) {
    var options = {};
	var behaviors = this.cfg.behaviors || {};

	if (type != 'selection') { // clear selected date
		document.getElementById(this.id + '_selectedDate').setAttribute('value', '');
	}

	if ((type == 'next' && !behaviors.navNext) || (type == 'previous' && !behaviors.navPrevious) || 
			(type == 'selection' && !behaviors.navSelection)) {
		if (!this.cfg.isLazy) return;
		options = {
			source: this.id,
			render: this.id,
			execute: this.id
		};
	}

    var params = {};
	params[this.id + "_navigation"] = true;
	params[this.id + "_oldYear"] = oldYear;
	params[this.id + "_oldMonth"] = oldMonth;
	params[this.id + "_oldDay"] = oldDay;

	var endYear = year;
	var endMonth = month;
	var endDay = day;
	if (this.cfg.viewMode == 'month') {
		endDay = this.determineLastDayOfMonth(year, month);
	} else if (this.cfg.viewMode = 'week') {
		var lastDayOfWeek = this.determineLastDayOfWeek(year, month, day);
		endYear = lastDayOfWeek.year;
		endMonth = lastDayOfWeek.month;
		endDay = lastDayOfWeek.day;
	}
	month++; endMonth++; // make 1-relative for delivery
	params[this.id + "_startDate"] = year + "-" + (month < 10 ? '0' + month : month) + "-" + (day < 10 ? '0' + day : day);
	params[this.id + "_endDate"] = endYear + "-" + (endMonth < 10 ? '0' + endMonth : endMonth) 
		+ "-" + (endDay < 10 ? '0' + endDay : endDay);

    options.params = params;

	if (type == 'next' && behaviors && behaviors.navNext) {
		ice.ace.AjaxRequest(ice.ace.extendAjaxArgs(behaviors.navNext, options));
	} else if (type == 'previous' && behaviors && behaviors.navPrevious) {
		ice.ace.AjaxRequest(ice.ace.extendAjaxArgs(behaviors.navPrevious, options));
	} else if (type == 'selection' && behaviors && behaviors.navSelection) {
		ice.ace.AjaxRequest(ice.ace.extendAjaxArgs(behaviors.navSelection, options));
	} else {
		ice.ace.AjaxRequest(options);
	}
};

ice.ace.Schedule.prototype.sendSelectionRequest = function(event) {
    var options = {};
	var behaviors = this.cfg.behaviors || {};

	if (!(behaviors && behaviors.selection)) {
		if (!this.cfg.isLazy) return;
		options = {
			source: this.id,
			render: this.id,
			execute: this.id
		};
	}

    var params = {};
	params[this.id + "_navigation"] = true;

    options.params = params;

	if (behaviors && behaviors.selection) {
		ice.ace.AjaxRequest(ice.ace.extendAjaxArgs(behaviors.selection, options));
	} else {
		ice.ace.AjaxRequest(options);
	}
};

ice.ace.Schedule.prototype.sendEditRequest = function(event, type) {
    var options = {};
	var behaviors = this.cfg.behaviors || {};

	if ((type == 'add' && !behaviors.addEvent) ||
		(type == 'edit' && !behaviors.editEvent) ||
		(type == 'delete' && !behaviors.deleteEvent)) {
		options = {
			source: this.id,
			render: this.id,
			execute: this.id
		};
	}

    var params = {};
	if (type == 'add') params[this.id + "_add"] = true;
    else if (type == 'edit') params[this.id + "_edit"] = true;
    else if (type == 'delete') params[this.id + "_delete"] = true;
	this.addTimeParameters(params);
    options.params = params;

	// save scroll position
	window[this.id + '_lastScrollTop'] = this.jqRoot.find('.schedule-days').get(0).scrollTop;

	if (type == 'add' && behaviors.addEvent) {
		ice.ace.AjaxRequest(ice.ace.extendAjaxArgs(behaviors.addEvent, options));
	} else if (type == 'edit' && behaviors.editEvent) {
		ice.ace.AjaxRequest(ice.ace.extendAjaxArgs(behaviors.editEvent, options));
	} else if (type == 'delete' && behaviors.deleteEvent) {
		ice.ace.AjaxRequest(ice.ace.extendAjaxArgs(behaviors.deleteEvent, options));
	} else {
		ice.ace.AjaxRequest(options);
	}
};

ice.ace.Schedule.prototype.sendClickRequest = function(event, type, data) {
    var options = {};
	var behaviors = this.cfg.behaviors || {};

	if (!behaviors.eventClick && !behaviors.dayDblclick && !behaviors.timeDblclick)
		return;

    var params = {};
	if (type == 'event') params[this.id + "_eventClick"] = data;
    else if (type == 'day') params[this.id + "_dayDblclick"] = data;
    else if (type == 'time') params[this.id + "_timeDblclick"] = data;
	else return;

	if (type == 'event') {
		var eventData = this.events[data];
		params[this.id + "_date"] = eventData.startDate;
		params[this.id + "_time"] = eventData.startTime;
		params[this.id + "_endDate"] = eventData.endDate;
		params[this.id + "_endTime"] = eventData.endTime;
		params[this.id + "_title"] = eventData.title;
		params[this.id + "_location"] = eventData.location;
		params[this.id + "_notes"] = eventData.notes;
		if (eventData.styleClass) params[this.id + "_styleClass"] = eventData.styleClass;
		if (eventData.id) params[this.id + "_id"] = eventData.id;
	}

    options.params = params;

	if (type == 'event' && behaviors.eventClick) {
		ice.ace.AjaxRequest(ice.ace.extendAjaxArgs(behaviors.eventClick, options));
	} else if (type == 'day' && behaviors.dayDblclick) {
		ice.ace.AjaxRequest(ice.ace.extendAjaxArgs(behaviors.dayDblclick, options));
	} else if (type == 'time' && behaviors.timeDblclick) {
		ice.ace.AjaxRequest(ice.ace.extendAjaxArgs(behaviors.timeDblclick, options));
	}
};

ice.ace.Schedule.prototype.renderMonthView = function(data) {
	var currentYear = this.cfg.currentYear;
	var currentMonth = this.cfg.currentMonth;
	var i, j;
	var markup =
	"<div class=\"schedule-title ui-state-active\">"
		+"<div class=\"schedule-button-previous\" role=\"button\" aria-label=\"" + this.messages.AriaPreviousMonth + "\"><i class=\"fa fa-arrow-left\"></i></div>"
		+"<div class=\"schedule-button-next\" role=\"button\" aria-label=\"" + this.messages.AriaNextMonth + "\"><i class=\"fa fa-arrow-right\"></i></div>"
		+"<div class=\"schedule-showing\" aria-label=\"" + this.messages.AriaCurrentMonth + "\">" + this.getMonthName(currentMonth) + " " + currentYear + "</div>"
	+"</div>"

	+"<div class=\"schedule-content\">"

		+"<div class=\"schedule-grid ui-widget-content\">"
			+"<table><thead class=\"schedule-dow ui-state-default\"><tr>";

				for (i = 0; i < 7; i++) {
					markup += "<th class=\"schedule-dow-header schedule-dow-" + i + "\">"
						+ this.getDayOfTheWeekNameShort(i) + "</th>";
				}

			markup += "</tr></thead>"
			+"<tbody class=\"schedule-days\">";

				// first day of the month
				var date = new Date(currentYear, currentMonth, 1, 0, 0, 0, 0);
				// set to previous Sunday
				date.setDate(date.getDate() - date.getDay());

				for (i = 0; i < 42; i++) { // 42 days == 6 weeks

					if (i % 7 == 0) markup += "<tr>";
					var adjacentMonth = date.getMonth() != currentMonth;
					var dateClass = 'schedule-calendar-day-' + date.getFullYear()
						+ '-' + this.addLeadingZero(date.getMonth() + 1)
						+ '-' + this.addLeadingZero(date.getDate());
					markup += "<td class=\"schedule-day " + dateClass + (adjacentMonth? ' schedule-adjacent-month' : '')
						+ " ui-widget-content\">";
					if (this.isToday(date)) markup+= "<div class=\"schedule-state ui-state-highlight\">";
					else markup+= "<div class=\"schedule-state\">";

					markup += "<div class=\"schedule-day-number\">" + date.getDate() + "</div>";
					markup += "</div></td>";
					if (i % 7 == 6) markup += "</tr>";

					// set to next day
					date.setDate(date.getDate() + 1);
				}

			markup += "</tbody></table>"
		+"</div>"

		+"<div class=\"schedule-sidebar ui-widget-content\">"

			+"<div class=\"schedule-list-title ui-state-default\">" + this.messages.EventsThisMonth + "</div>"
			+"<div class=\"schedule-list-content\" role=\"list\">";

			markup += "</div>"

			+"<div id=\"" + this.id + "_detailsSidebarTitle\" class=\"schedule-details-sidebar-title ui-state-default\">" + this.messages.EventDetails + "</div>"
			+"<div class=\"schedule-details-sidebar-content\"></div>"

		+"</div>"

	+"</div>";

	return markup;
};

ice.ace.Schedule.prototype.isToday = function(date) {
	var today = new Date();
	return (today.getFullYear() == date.getFullYear()
		&& today.getMonth() == date.getMonth()
		&& today.getDate() == date.getDate())
};

ice.ace.Schedule.prototype.renderMonthEvents = function(data) {
	var sidebarEventsContainer = ice.ace.jq(this.jqId).find('.schedule-list-content');
	var currentYear = this.cfg.currentYear;
	var currentMonth = this.cfg.currentMonth;
	var currentDay = this.cfg.currentDay;
	var monthStartDate = new Date(currentYear, currentMonth, 1, 0, 0, 0, 0);
	var lastDayOfMonth = this.determineLastDayOfMonth(currentYear, currentMonth);
	var monthEndDate = new Date(currentYear, currentMonth, lastDayOfMonth, 23, 59, 59, 999);

	// add event divs at appropriate day divs
	var i;
	var listing = 0;
	for (i = 0; i < this.events.length; i++) {
		var event = this.events[i];
		var eventStartYear = event.startDate.substring(0,4);
		var eventStartMonth = parseInt(event.startDate.substring(5,7), 10) - 1;
		var eventStartDay = parseInt(event.startDate.substring(8,10), 10);
		var eventStartDate = new Date(eventStartYear, eventStartMonth, eventStartDay, 0, 0, 0, 0);
		var eventEndYear = event.endDate.substring(0,4);
		var eventEndMonth = parseInt(event.endDate.substring(5,7), 10) - 1;
		var eventEndDay = parseInt(event.endDate.substring(8,10), 10);
		var eventEndDate = new Date(eventEndYear, eventEndMonth, eventEndDay, 23, 59, 59, 999);
		var customStyleClass = event.styleClass ? ' ' + event.styleClass : '';
		if (currentYear == eventStartYear && currentMonth == eventStartMonth) {
			var dayDiv = this.jq.find('.schedule-calendar-day-' + eventStartYear
				+ '-' + this.addLeadingZero(eventStartMonth + 1)
				+ '-' + this.addLeadingZero(eventStartDay) + ' .schedule-state');
			var eventElement = ice.ace.jq('<div class=\"ui-state-default ui-corner-all schedule-event schedule-event-' + event.index + customStyleClass + '\"></div>');
			var startTime;
			if (event.isAllDay) {
				startTime = this.messages.ALLDAY;
			} else if (this.cfg.isTwelveHourClock) {
				var hour = parseInt(event.startTime.substring(0,2), 10);
				var minutes = event.startTime.substring(3,5);
				startTime = hour < 13 ? (hour == 12 ? '12:' + minutes + 'p' : hour + ':' + minutes + 'a') : hour - 12 + ':' + minutes + 'p';
			} else {
				startTime = event.startTime;
			}
			eventElement.html('<span>' + startTime + ' ' + this.getTitle(event) + '</span>');
			if (event.isAllDay) {
				eventElement.addClass('schedule-event-allday');
				eventElement.insertAfter(dayDiv.find('.schedule-day-number'));
			} else {
				eventElement.appendTo(dayDiv);
			}
			var highlightClass = listing % 2 == 1 ? ' ui-state-highlight' : '';
			ice.ace.jq('<div aria-role="listitem" class="schedule-list-event schedule-event-' + event.index + highlightClass + '"><span class="schedule-list-event-day">'+event.startDate.substring(8,10)+'</span><span class="schedule-list-event-name">'+this.getTitle(event)+'</span><span class="schedule-list-event-location">'+this.escapeHtml(event.location)+'</span></div>').appendTo(sidebarEventsContainer);
			listing++;
			// spans multiple days
			if (eventStartYear == eventEndYear && eventStartMonth == eventEndMonth && eventEndDay > eventStartDay) {
				var j;
				for (j = eventStartDay+1; j <= eventEndDay; j++) {
					var dayDiv = this.jq.find('.schedule-calendar-day-' + eventStartYear
						+ '-' + this.addLeadingZero(eventStartMonth + 1)
						+ '-' + this.addLeadingZero(j) + ' .schedule-state');
					var eventElement = ice.ace.jq('<div class=\"ui-state-default ui-corner-all schedule-event schedule-event-' + event.index + customStyleClass + '\"></div>');
					if (event.isAllDay) {
						eventElement.html('<span>' + this.messages.ALLDAY + ' ' + this.getTitle(event) + '</span>');
						eventElement.addClass('schedule-event-allday');
						var lastAllDayEvent = dayDiv.find('.schedule-event-allday:last');
						if (lastAllDayEvent.size() > 0) eventElement.insertAfter(lastAllDayEvent);
						else eventElement.insertAfter(dayDiv.find('.schedule-day-number'));
					} else {
						eventElement.html('<span>(cont.) ' + this.getTitle(event) + '</span>');
						eventElement.appendTo(dayDiv);
					}
				}
			// spans till next month
			} else if (eventEndYear > eventStartYear || eventEndMonth > eventStartMonth) {
				var j;
				for (j = eventStartDay+1; j <= this.determineLastDayOfMonth(eventStartYear, eventStartMonth); j++) {
					var dayDiv = this.jq.find('.schedule-calendar-day-' + eventStartYear
						+ '-' + this.addLeadingZero(eventStartMonth + 1)
						+ '-' + this.addLeadingZero(j) + ' .schedule-state');
					var eventElement = ice.ace.jq('<div class=\"ui-state-default ui-corner-all schedule-event schedule-event-' + event.index + customStyleClass + '\"></div>');
					if (event.isAllDay) {
						eventElement.html('<span>' + this.messages.ALLDAY + ' ' + this.getTitle(event) + '</span>');
						eventElement.addClass('schedule-event-allday');
						var lastAllDayEvent = dayDiv.find('.schedule-event-allday:last');
						if (lastAllDayEvent.size() > 0) eventElement.insertAfter(lastAllDayEvent);
						else eventElement.insertAfter(dayDiv.find('.schedule-day-number'));
					} else {
						eventElement.html('<span>(cont.) ' + this.getTitle(event) + '</span>');
						eventElement.appendTo(dayDiv);
					}
				}
			}
		} else if (currentYear == eventEndYear && currentMonth == eventEndMonth) {
			var j;
			for (j = 1; j <= eventEndDay; j++) {
				var dayDiv = this.jq.find('.schedule-calendar-day-' + eventEndYear
					+ '-' + this.addLeadingZero(eventEndMonth + 1)
					+ '-' + this.addLeadingZero(j) + ' .schedule-state');
				var eventElement = ice.ace.jq('<div class=\"ui-state-default ui-corner-all schedule-event schedule-event-' + event.index + customStyleClass + '\"></div>');
				if (event.isAllDay) {
					eventElement.html('<span>' + this.messages.ALLDAY + ' ' + this.getTitle(event) + '</span>');
					eventElement.addClass('schedule-event-allday');
					var lastAllDayEvent = dayDiv.find('.schedule-event-allday:last');
					if (lastAllDayEvent.size() > 0) eventElement.insertAfter(lastAllDayEvent);
					else eventElement.insertAfter(dayDiv.find('.schedule-day-number'));
				} else {
					eventElement.html('<span>(cont.) ' + this.getTitle(event) + '</span>');
					eventElement.appendTo(dayDiv);
				}
			}
		} else if (eventStartDate <= monthStartDate && eventEndDate >= monthEndDate) {
			var j;
			for (j = 1; j <= lastDayOfMonth; j++) {
				var dayDiv = this.jq.find('.schedule-calendar-day-' + eventEndYear
					+ '-' + this.addLeadingZero(currentMonth + 1)
					+ '-' + this.addLeadingZero(j) + ' .schedule-state');
				var eventElement = ice.ace.jq('<div class=\"ui-state-default ui-corner-all schedule-event schedule-event-' + event.index + customStyleClass + '\"></div>');
				if (event.isAllDay) {
					eventElement.html('<span>' + this.messages.ALLDAY + ' ' + this.getTitle(event) + '</span>');
					eventElement.addClass('schedule-event-allday');
					var lastAllDayEvent = dayDiv.find('.schedule-event-allday:last');
					if (lastAllDayEvent.size() > 0) eventElement.insertAfter(lastAllDayEvent);
					else eventElement.insertAfter(dayDiv.find('.schedule-day-number'));
				} else {
					eventElement.html('<span>(cont.) ' + this.getTitle(event) + '</span>');
					eventElement.appendTo(dayDiv);
				}
			}
		}
	}
	// fix to make height:100%; work on Edge and IE
	if (/Trident.*rv[ :]*11\./.test(navigator.userAgent) || navigator.userAgent.indexOf('Edge\/') != -1) {
		var table = this.jqRoot.find('.schedule-grid > table');
		table.css('height', table.outerHeight() + 'px');
	}

	this.expandEventList();

	// add selected styling
	var selectedDate = document.getElementById(this.id + '_selectedDate').getAttribute('value');
	this.jqRoot.find('.schedule-selected').removeClass('schedule-selected');
	this.jqRoot.find('.schedule-calendar-day-' + selectedDate + ' .schedule-state').addClass('schedule-selected');
};

ice.ace.Schedule.prototype.renderWeekView = function() {
	var i, j;
	var markup =
	"<div class=\"schedule-title ui-state-active\">"
		+"<div class=\"schedule-button-previous\" role=\"button\" aria-label=\"" + this.messages.AriaPreviousWeek + "\"><i class=\"fa fa-arrow-left\"></i></div>"
		+"<div class=\"schedule-button-next\" role=\"button\" aria-label=\"" + this.messages.AriaNextWeek + "\"><i class=\"fa fa-arrow-right\"></i></div>"
		+"<div class=\"schedule-showing\" aria-label=\"" + this.messages.AriaCurrentWeek + "\"></div>"
	+"</div>"

	+"<div class=\"schedule-content\">"

		/* time grid */
		+"<div class=\"schedule-grid ui-widget-content\">"
			+"<table><thead class=\"schedule-dow ui-state-default\"><tr>"
				+"<th class=\"schedule-dow-header schedule-cell-time\">" + this.messages.Time + "</th>";

				for (i = 0; i < 7; i++) {
					markup += "<th class=\"schedule-dow-header schedule-dow-" + i + "\"></th>";
				}

			markup += "<th></th>"; // scrollbar width

			markup += "</tr></thead></table>"
			+"<div class=\"schedule-days\"><table><tbody>";

					for (i = 0; i < 24; i++) {
						var iString = i < 10 ? '0' + i : i;
						markup += "<tr>";

							if (this.cfg.isTwelveHourClock) {
								var hour = i < 13 ? (i == 12 ? '12:00 pm' : i + ':00 am') : i - 12 + ':00 pm';
								markup += "<td class=\"ui-widget-content schedule-cell schedule-cell-time\">" + hour + "</td>";
							} else {
								markup += "<td class=\"ui-widget-content schedule-cell schedule-cell-time\">" + iString + ":00</td>";
							}

							markup += "<td class=\"ui-widget-content schedule-cell schedule-dow-0 schedule-time-" + iString + "00\"><div class=\"schedule-state\"></div></td>"
							+"<td class=\"ui-widget-content schedule-cell schedule-dow-1 schedule-time-" + iString + "00\"><div class=\"schedule-state\"></div></td>"
							+"<td class=\"ui-widget-content schedule-cell schedule-dow-2 schedule-time-" + iString + "00\"><div class=\"schedule-state\"></div></td>"
							+"<td class=\"ui-widget-content schedule-cell schedule-dow-3 schedule-time-" + iString + "00\"><div class=\"schedule-state\"></div></td>"
							+"<td class=\"ui-widget-content schedule-cell schedule-dow-4 schedule-time-" + iString + "00\"><div class=\"schedule-state\"></div></td>"
							+"<td class=\"ui-widget-content schedule-cell schedule-dow-5 schedule-time-" + iString + "00\"><div class=\"schedule-state\"></div></td>"
							+"<td class=\"ui-widget-content schedule-cell schedule-dow-6 schedule-time-" + iString + "00\"><div class=\"schedule-state\"></div></td>"
						+"</tr><tr>";

							if (this.cfg.isTwelveHourClock) {
								var hour = i < 13 ? (i == 12 ? '12:30 pm' : i + ':30 am') : i - 12 + ':30 pm';
								markup += "<td class=\"ui-widget-content schedule-cell schedule-cell-time\">" + hour + "</td>";
							} else {
								markup += "<td class=\"ui-widget-content schedule-cell schedule-cell-time\">" + iString + ":30</td>";
							}

							markup += "<td class=\"ui-widget-content schedule-cell schedule-dow-0 schedule-time-" + iString + "30\"><div class=\"schedule-state\"></div></td>"
							+"<td class=\"ui-widget-content schedule-cell schedule-dow-1 schedule-time-" + iString + "30\"><div class=\"schedule-state\"></div></td>"
							+"<td class=\"ui-widget-content schedule-cell schedule-dow-2 schedule-time-" + iString + "30\"><div class=\"schedule-state\"></div></td>"
							+"<td class=\"ui-widget-content schedule-cell schedule-dow-3 schedule-time-" + iString + "30\"><div class=\"schedule-state\"></div></td>"
							+"<td class=\"ui-widget-content schedule-cell schedule-dow-4 schedule-time-" + iString + "30\"><div class=\"schedule-state\"></div></td>"
							+"<td class=\"ui-widget-content schedule-cell schedule-dow-5 schedule-time-" + iString + "30\"><div class=\"schedule-state\"></div></td>"
							+"<td class=\"ui-widget-content schedule-cell schedule-dow-6 schedule-time-" + iString + "30\"><div class=\"schedule-state\"></div></td>"
						+"</tr>";
					}

				markup += "</tbody></table>"
			+"<div class=\"schedule-event-container\"></div></div>"
		+"</div>";

		markup += "<div class=\"schedule-sidebar ui-widget-content\">"

		+"<div class=\"schedule-list-title ui-state-default\">" + this.messages.EventsThisWeek + "</div>"
		+"<div class=\"schedule-list-content\" role=\"list\"></div>"

		+"<div id=\"" + this.id + "_detailsSidebarTitle\" class=\"schedule-details-sidebar-title ui-state-default\">" + this.messages.EventDetails + "</div>"
		+"<div class=\"schedule-details-sidebar-content\"></div>"

		+"</div>"

	+"</div>";

	return markup;
};

ice.ace.Schedule.prototype.renderWeekEvents = function() {
	var eventsContainer = ice.ace.jq(this.jqId).find('.schedule-event-container');
	eventsContainer.html('');
	var sidebarEventsContainer = ice.ace.jq(this.jqId).find('.schedule-list-content');
	var currentYear = this.cfg.currentYear;
	var currentMonth = this.cfg.currentMonth;
	var currentDay = this.cfg.currentDay;
	var weekStartDate = new Date(currentYear, currentMonth, currentDay, 0, 0, 0, 0);
	var lastDayOfWeek = this.determineLastDayOfWeek(currentYear, currentMonth, currentDay);
	var weekEndDate = new Date(lastDayOfWeek.year, lastDayOfWeek.month, lastDayOfWeek.day, 23, 59, 59, 999);
	var weekStartYear = weekStartDate.getFullYear();
	var weekEndYear = weekEndDate.getFullYear();
	weekStartYear = currentYear == weekEndYear ? '' : ', ' + weekStartYear;
	var title = this.getMonthName(weekStartDate.getMonth()) + ' ' + weekStartDate.getDate() + weekStartYear + ' - '
		+ this.getMonthName(weekEndDate.getMonth()) + ' ' + weekEndDate.getDate() + ', ' + weekEndYear;
	this.jq.find('.schedule-showing').html(title);
	// set the scrollable height, if supplied
	if  (this.cfg.scrollHeight) this.jq.find('.schedule-days').css({height:this.cfg.scrollHeight});
	// set day of the week headers and calendar day CSS classes
	var dowCount;
	var dowDate = new Date(weekStartDate.getTime());
	for (dowCount = 0; dowCount < 7; dowCount++) {
		var dayHeader = this.jq.find('.schedule-dow-header.schedule-dow-'+dowCount);
		dayHeader.html(this.getDayOfTheWeekNameShort(dowCount) + ', ' 
			+ this.getMonthNameShort(dowDate.getMonth()) + '/' + dowDate.getDate());
		var month = dowDate.getMonth() + 1;
		var day = dowDate.getDate();
		this.jq.find('.schedule-cell.schedule-dow-'+dowCount).addClass('schedule-calendar-day-'+dowDate.getFullYear()+'-'+(month < 10 ? '0' + month : month)+'-'+(day < 10 ? '0' + day : day));
		dowDate.setDate(dowDate.getDate() + 1);
	}
	var today = new Date();
	this.jqRoot.find('.schedule-calendar-day-' + today.getFullYear() + '-' + (today.getMonth() + 1) + '-'
		+ today.getDate() + ' .schedule-state').addClass('ui-state-highlight');
	// add event divs at appropriate positions
	var i,j;
	this.weekTimeSlots = [[], [], [], [], [], [], []];
	for (i = 0; i < 7; i++) {
		for (j = 0; j < 48; j++) this.weekTimeSlots[i][j] = 0;
	}
	// process all day events first
	var allDayEventCount = -1;
	for (i = 0; i < this.events.length; i++) {
		var event = this.events[i];
		var eventStartDate = new Date();
		var startYear = event.startDate.substring(0,4);
		var startMonth = parseInt(event.startDate.substring(5,7) - 1, 10);
		var startDay = event.startDate.substring(8,10);
		eventStartDate.setFullYear(startYear);
		eventStartDate.setMonth(startMonth);
		eventStartDate.setDate(startDay);
		var eventEndDate = new Date();
		var endYear = event.endDate.substring(0,4);
		var endMonth = parseInt(event.endDate.substring(5,7) - 1, 10);
		var endDay = event.endDate.substring(8,10);
		eventEndDate.setFullYear(endYear);
		eventEndDate.setMonth(endMonth);
		eventEndDate.setDate(endDay);
		eventEndDate.setHours(23, 59, 59, 999);
		if (eventStartDate >= weekStartDate && eventStartDate < weekEndDate) {
			if (event.isAllDay) {
				allDayEventCount++;
				this.addAllDayRow(allDayEventCount);

				// determine the day of the week
				var dateMillis = eventStartDate.getTime();
				var startDateMillis = weekStartDate.getTime();
				var millisDelta = dateMillis - startDateMillis;
				var dow = Math.floor(millisDelta / 86400000);

				var customStyleClass = event.styleClass ? ' ' + event.styleClass : '';
				var eventElement = ice.ace.jq('<div class="ui-state-default schedule-dow-' + dow + ' schedule-event schedule-event-allday schedule-event-' + event.index + customStyleClass + '"></div>');
				eventElement.html('<span class="schedule-event-bold">' + this.getTitle(event) + '</span> ' + this.escapeHtml(event.location));
				var selector = '.schedule-dow-'+dow+'.schedule-time-allday-'+allDayEventCount+' .schedule-state';
				eventElement.appendTo(ice.ace.jq(selector));

				var spansMultipleDays = !(startYear == endYear && startMonth == endMonth && startDay == endDay);

				if (spansMultipleDays) {
					for (dow = dow + 1; dow <= 6; dow++) {
						eventStartDate.setDate(eventStartDate.getDate() + 1);
						eventStartDate.setHours(0, 0, 0, 0);

						if (eventEndDate >= eventStartDate) {
							var eventElement = ice.ace.jq('<div class="ui-state-default schedule-dow-' + dow + ' schedule-event schedule-event-allday schedule-event-' + event.index + customStyleClass + '"></div>');
							eventElement.html('<span class="schedule-event-bold">' + this.getTitle(event) + '</span> ' + this.escapeHtml(event.location));
							var selector = '.schedule-dow-'+dow+'.schedule-time-allday-'+allDayEventCount+' .schedule-state';
							eventElement.appendTo(ice.ace.jq(selector));
						}
					}
				}
			}
		// event starts before the current week and ends in it
		} else if (eventEndDate >= weekStartDate && eventEndDate < weekEndDate) {
			if (event.isAllDay) {
				allDayEventCount++;
				this.addAllDayRow(allDayEventCount);

				var date = new Date();
				date.setFullYear(weekStartDate.getFullYear());
				date.setMonth(weekStartDate.getMonth());
				date.setDate(weekStartDate.getDate());
				var dow;
				for (dow = 0; dow <= 6; dow++) {
					date.setDate(weekStartDate.getDate() + dow);
					date.setHours(0, 0, 0, 0);

					if (eventEndDate >= date) {
						var customStyleClass = event.styleClass ? ' ' + event.styleClass : '';
						var eventElement = ice.ace.jq('<div class="ui-state-default schedule-dow-' + dow + ' schedule-event schedule-event-allday schedule-event-' + event.index + customStyleClass + '"></div>');
						eventElement.html('<span class="schedule-event-bold">' + this.getTitle(event) + '</span> ' + this.escapeHtml(event.location));
						var selector = '.schedule-dow-'+dow+'.schedule-time-allday-'+allDayEventCount+' .schedule-state';
						eventElement.appendTo(ice.ace.jq(selector));
					}
				}
			}
		} else if (eventStartDate <= weekStartDate && eventEndDate >= weekEndDate) {
			if (event.isAllDay) {
				allDayEventCount++;
				this.addAllDayRow(allDayEventCount);

				var dow;
				for (dow = 0; dow <= 6; dow++) {
					var customStyleClass = event.styleClass ? ' ' + event.styleClass : '';
					var eventElement = ice.ace.jq('<div class="ui-state-default schedule-dow-' + dow + ' schedule-event schedule-event-allday schedule-event-' + event.index + customStyleClass + '"></div>');
					eventElement.html('<span class="schedule-event-bold">' + this.getTitle(event) + '</span> ' + this.escapeHtml(event.location));
					var selector = '.schedule-dow-'+dow+'.schedule-time-allday-'+allDayEventCount+' .schedule-state';
					eventElement.appendTo(ice.ace.jq(selector));
				}
			}
		}
	}
	// process regular events
	var listing = 0;
	var isChrome = ice.ace.browser.isChrome() && navigator.userAgent.indexOf('Edge\/') == -1;
	for (i = 0; i < this.events.length; i++) {
		var event = this.events[i];
		if (event.isAllDay) continue;
		var eventStartDate = new Date();
		var startYear = event.startDate.substring(0,4);
		var startMonth = parseInt(event.startDate.substring(5,7) - 1, 10);
		var startDay = event.startDate.substring(8,10);
		eventStartDate.setFullYear(startYear);
		eventStartDate.setMonth(startMonth);
		eventStartDate.setDate(startDay);
		var eventEndDate = new Date();
		var endYear = event.endDate.substring(0,4);
		var endMonth = parseInt(event.endDate.substring(5,7) - 1, 10);
		var endDay = event.endDate.substring(8,10);
		eventEndDate.setFullYear(endYear);
		eventEndDate.setMonth(endMonth);
		eventEndDate.setDate(endDay);
		eventEndDate.setHours(23, 59, 59, 999);
		if (eventStartDate >= weekStartDate && eventStartDate < weekEndDate) {
			// determine the day of the week
			var dateMillis = eventStartDate.getTime();
			var startDateMillis = weekStartDate.getTime();
			var millisDelta = dateMillis - startDateMillis;
			var dow = Math.floor(millisDelta / 86400000);

			var hour = event.startTime.substring(0,2);
			var minutes = parseInt(event.startTime.substring(3,5), 10);
			var startingTimeSlot = hour+(minutes >= 30 ? '30' : '00');
			var selector = '.schedule-dow-'+dow+'.schedule-time-'+startingTimeSlot;
			var timeCell = this.jq.find(selector);
			var position = timeCell.position();
			var width = timeCell.outerWidth() - 1;
			var spansMultipleDays = !(startYear == endYear && startMonth == endMonth && startDay == endDay);
			var endHour = spansMultipleDays ? '24' : event.endTime.substring(0,2);
			var endMinutes = spansMultipleDays ? '00' : parseInt(event.endTime.substring(3,5), 10);
			var endingTimeSlot = this.determinePreviousTimeCell(endHour, endMinutes);
			var endSelector = '.schedule-dow-'+dow+'.schedule-time-'+endingTimeSlot;
			var endTimeCell = this.jq.find(endSelector);
			var endPosition = endTimeCell.position();
			var height = endTimeCell.outerHeight() - 1;
			var customStyleClass = event.styleClass ? ' ' + event.styleClass : '';
			var timeSlotMultiplicity = this.weekTimeSlots[dow][this.timeSlotIndexMap[startingTimeSlot]];
			timeSlotMultiplicity = timeSlotMultiplicity < 4 ? timeSlotMultiplicity : 4;
			var multiplicityAdjustment = timeSlotMultiplicity * 5;
			var overlappingClass = ' schedule-overlapping-' + timeSlotMultiplicity;
			var eventElement = ice.ace.jq('<div class=\"ui-state-default schedule-dow-' + dow + ' schedule-event schedule-event-' + event.index + customStyleClass + overlappingClass + '\"></div>');
			eventElement.html(this.getEventDivMarkup(hour, minutes, endHour, endMinutes, event));
			eventElement.css({position:'absolute',
				top:position.top+(isChrome?1:0),
				left:position.left+multiplicityAdjustment+(isChrome?1:0),
				width: (width - multiplicityAdjustment) + 'px', 
				height: (endPosition.top - position.top + height) + 'px'}).appendTo(eventsContainer);
			var highlightClass = listing % 2 == 1 ? ' ui-state-highlight' : '';
			ice.ace.jq('<div role="listitem" class="schedule-list-event schedule-event-' + event.index + highlightClass + '"><span class="schedule-list-event-day">'+this.getDayOfTheWeekName(dow)+', '+this.getMonthNameShort(eventStartDate.getMonth())+' '+event.startDate.substring(8)+'</span><span class="schedule-list-event-name">'+this.getTitle(event)+'</span><span class="schedule-list-event-location">'+this.escapeHtml(event.location)+'</span></div>').appendTo(sidebarEventsContainer);
			this.markUsedTimeSlots(this.weekTimeSlots[dow], startingTimeSlot, endingTimeSlot);
			listing++;
			if (spansMultipleDays) {
				for (dow = dow + 1; dow <= 6; dow++) {
					eventStartDate.setDate(eventStartDate.getDate() + 1);
					eventStartDate.setHours(0, 0, 0, 0);

					if (eventEndDate >= eventStartDate) {
						var startingTimeSlot = '0000';
						var selector = '.schedule-dow-'+dow+'.schedule-time-'+startingTimeSlot;
						var timeCell = this.jq.find(selector);
						var position = timeCell.position();
						var width = timeCell.outerWidth() - 1;
						var isLastDay = eventStartDate.getFullYear() == eventEndDate.getFullYear() 
								&& eventStartDate.getMonth() == eventEndDate.getMonth()
								&& eventStartDate.getDate() == eventEndDate.getDate();
						if (isLastDay && event.endTime == '00:00') break; // ends previous day at midnight
						var endHour = isLastDay ? event.endTime.substring(0,2) : '24';
						var endMinutes = isLastDay ? parseInt(event.endTime.substring(3,5), 10) : '00';
						var endingTimeSlot = this.determinePreviousTimeCell(endHour, endMinutes);
						var endSelector = '.schedule-dow-'+dow+'.schedule-time-'+endingTimeSlot;
						var endTimeCell = this.jq.find(endSelector);
						var endPosition = endTimeCell.position();
						var height = endTimeCell.outerHeight() - 1;
						var customStyleClass = event.styleClass ? ' ' + event.styleClass : '';
						var timeSlotMultiplicity = this.weekTimeSlots[dow][this.timeSlotIndexMap[startingTimeSlot]];
						timeSlotMultiplicity = timeSlotMultiplicity < 4 ? timeSlotMultiplicity : 4;
						var multiplicityAdjustment = timeSlotMultiplicity * 5;
						var overlappingClass = ' schedule-overlapping-' + timeSlotMultiplicity;
						var eventElement = ice.ace.jq('<div class=\"ui-state-default schedule-dow-' + dow + ' schedule-event schedule-event-' + event.index + customStyleClass + overlappingClass + '\"></div>');
						eventElement.html('(cont.) ' + this.getTitle(event));
						eventElement.css({position:'absolute',
							top:position.top+(isChrome?1:0),
							left:position.left+multiplicityAdjustment+(isChrome?1:0),
							width: (width - multiplicityAdjustment) + 'px', 
							height: (endPosition.top - position.top + height) + 'px'}).appendTo(eventsContainer);
						this.markUsedTimeSlots(this.weekTimeSlots[dow], startingTimeSlot, endingTimeSlot);
					}
				}
			}
		// event starts before the current week and ends in the current week
		} else if (eventEndDate >= weekStartDate && eventEndDate < weekEndDate) {
			var date = new Date();
			date.setFullYear(weekStartDate.getFullYear());
			date.setMonth(weekStartDate.getMonth());
			date.setDate(weekStartDate.getDate());
			var dow;
			for (dow = 0; dow <= 6; dow++) {
				date.setDate(weekStartDate.getDate() + dow);
				date.setHours(0, 0, 0, 0);

				if (eventEndDate >= date) {
					var startingTimeSlot = '0000';
					var selector = '.schedule-dow-'+dow+'.schedule-time-'+startingTimeSlot;
					var timeCell = this.jq.find(selector);
					var position = timeCell.position();
					var width = timeCell.outerWidth() - 1;
					var isLastDay = date.getFullYear() == eventEndDate.getFullYear() 
							&& date.getMonth() == eventEndDate.getMonth()
							&& date.getDate() == eventEndDate.getDate();
					if (isLastDay && event.endTime == '00:00') break; // ends previous day at midnight
					var endHour = isLastDay ? event.endTime.substring(0,2) : '24';
					var endMinutes = isLastDay ? parseInt(event.endTime.substring(3,5), 10) : '00';
					var endingTimeSlot = this.determinePreviousTimeCell(endHour, endMinutes);
					var endSelector = '.schedule-dow-'+dow+'.schedule-time-'+endingTimeSlot;
					var endTimeCell = this.jq.find(endSelector);
					var endPosition = endTimeCell.position();
					var height = endTimeCell.outerHeight() - 1;
					var customStyleClass = event.styleClass ? ' ' + event.styleClass : '';
					var timeSlotMultiplicity = this.weekTimeSlots[dow][this.timeSlotIndexMap[startingTimeSlot]];
					timeSlotMultiplicity = timeSlotMultiplicity < 4 ? timeSlotMultiplicity : 4;
					var multiplicityAdjustment = timeSlotMultiplicity * 5;
					var overlappingClass = ' schedule-overlapping-' + timeSlotMultiplicity;
					var eventElement = ice.ace.jq('<div class=\"ui-state-default schedule-dow-' + dow + ' schedule-event schedule-event-' + event.index + customStyleClass + overlappingClass + '\"></div>');
					eventElement.html('(cont.) ' + this.getTitle(event));
					eventElement.css({position:'absolute',
						top:position.top+(isChrome?1:0),
						left:position.left+multiplicityAdjustment+(isChrome?1:0),
						width: (width - multiplicityAdjustment) + 'px', 
						height: (endPosition.top - position.top + height) + 'px'}).appendTo(eventsContainer);
					this.markUsedTimeSlots(this.weekTimeSlots[dow], startingTimeSlot, endingTimeSlot);
				}
			}
		// event starts before the current week and ends after the current week
		} else if (eventStartDate <= weekStartDate && eventEndDate >= weekEndDate) {
			var dow;
			for (dow = 0; dow <= 6; dow++) {
				var startingTimeSlot = '0000';
				var selector = '.schedule-dow-'+dow+'.schedule-time-'+startingTimeSlot;
				var timeCell = this.jq.find(selector);
				var position = timeCell.position();
				var width = timeCell.outerWidth() - 1;
				var endHour = '24';
				var endMinutes = '00';
				var endingTimeSlot = this.determinePreviousTimeCell(endHour, endMinutes);
				var endSelector = '.schedule-dow-'+dow+'.schedule-time-'+endingTimeSlot;
				var endTimeCell = this.jq.find(endSelector);
				var endPosition = endTimeCell.position();
				var height = endTimeCell.outerHeight() - 1;
				var customStyleClass = event.styleClass ? ' ' + event.styleClass : '';
				var timeSlotMultiplicity = this.weekTimeSlots[dow][this.timeSlotIndexMap[startingTimeSlot]];
				timeSlotMultiplicity = timeSlotMultiplicity < 4 ? timeSlotMultiplicity : 4;
				var multiplicityAdjustment = timeSlotMultiplicity * 5;
				var overlappingClass = ' schedule-overlapping-' + timeSlotMultiplicity;
				var eventElement = ice.ace.jq('<div class=\"ui-state-default schedule-dow-' + dow + ' schedule-event schedule-event-' + event.index + customStyleClass + overlappingClass + '\"></div>');
				eventElement.html('(cont.) ' + this.getTitle(event));
				eventElement.css({position:'absolute',
					top:position.top+(isChrome?1:0),
					left:position.left+multiplicityAdjustment+(isChrome?1:0),
					width: (width - multiplicityAdjustment) + 'px', 
					height: (endPosition.top - position.top + height) + 'px'}).appendTo(eventsContainer);
				this.markUsedTimeSlots(this.weekTimeSlots[dow], startingTimeSlot, endingTimeSlot);
			}
		}
	}
	this.expandEventList();
	if  (this.cfg.scrollHeight) {
		if (window[this.id + '_lastScrollTop']) {
			this.jqRoot.find('.schedule-days').animate({scrollTop: window[this.id + '_lastScrollTop']}, 1);
		}
		window[this.id + '_lastScrollTop'] = 0;
	}

	// add selected styling
	var selectedDate = document.getElementById(this.id + '_selectedDate').getAttribute('value');
	this.jqRoot.find('.schedule-selected').removeClass('schedule-selected');
	var dow = this.extractDayOfWeek(this.jqRoot.find('.schedule-calendar-day-' + selectedDate).get(0));
	this.jqRoot.find('.schedule-dow-header.schedule-dow-' + dow).addClass('schedule-selected');
};

ice.ace.Schedule.prototype.renderDayView = function() {
	var i, j;
	var markup =
	"<div class=\"schedule-title ui-state-active\">"
		+"<div class=\"schedule-button-previous\" role=\"button\" aria-label=\"" + this.messages.AriaPreviousDay + "\"><i class=\"fa fa-arrow-left\"></i></div>"
		+"<div class=\"schedule-button-next\" role=\"button\" aria-label=\"" + this.messages.AriaNextDay + "\"><i class=\"fa fa-arrow-right\"></i></div>"
		+"<div class=\"schedule-showing\" aria-label=\"" + this.messages.AriaCurrentDay + "\"></div>"
	+"</div>"

	+"<div class=\"schedule-content\">"

		/* time grid */
		+"<div class=\"schedule-grid ui-widget-content\">"
			+"<table><thead class=\"schedule-dow ui-state-default\"><tr>"
				+"<th class=\"schedule-dow-header schedule-cell-time\">" + this.messages.Time + "</th>"
				+"<th class=\"schedule-dow-header schedule-dow-single\"></th>"
				+"<th></th>" // scrollbar width
			+"</tr></thead></table>"

			+"<div class=\"schedule-days\"><table><tbody>";

					for (i = 0; i < 24; i++) {
						var iString = i < 10 ? '0' + i : i;
						markup += "<tr>";

							if (this.cfg.isTwelveHourClock) {
								var hour = i < 13 ? (i == 12 ? '12:00 pm' : i + ':00 am') : i - 12 + ':00 pm';
								markup += "<td class=\"ui-widget-content schedule-cell schedule-cell-time\">" + hour + "</td>";
							} else {
								markup += "<td class=\"ui-widget-content schedule-cell schedule-cell-time\">" + iString + ":00</td>";
							}

							markup += "<td class=\"ui-widget-content schedule-cell schedule-dow-single schedule-time-" + iString + "00\"><div class=\"schedule-state\"></div></td>"
						+"</tr><tr>";

							if (this.cfg.isTwelveHourClock) {
								var hour = i < 13 ? (i == 12 ? '12:30 pm' : i + ':30 am') : i - 12 + ':30 pm';
								markup += "<td class=\"ui-widget-content schedule-cell schedule-cell-time\">" + hour + "</td>";
							} else {
								markup += "<td class=\"ui-widget-content schedule-cell schedule-cell-time\">" + iString + ":30</td>";
							}

							markup += "<td class=\"ui-widget-content schedule-cell schedule-dow-single schedule-time-" + iString + "30\"><div class=\"schedule-state\"></div></td>"
						+"</tr>";
					}

				markup += "</tbody></table>"
			+"<div class=\"schedule-event-container\"></div></div>"
		+"</div>";

		markup += "<div class=\"schedule-sidebar ui-widget-content\">"

			+"<div class=\"schedule-list-title ui-state-default\">" + this.messages.EventsThisDay + "</div>"
			+"<div class=\"schedule-list-content\" role=\"list\"></div>"

			+"<div id=\"" + this.id + "_detailsSidebarTitle\" class=\"schedule-details-sidebar-title ui-state-default\">" + this.messages.EventDetails + "</div>"
			+"<div class=\"schedule-details-sidebar-content\"></div>"

		+"</div>"

	+"</div>";

	return markup;
};

ice.ace.Schedule.prototype.renderDayEvents = function() {
	var eventsContainer = ice.ace.jq(this.jqId).find('.schedule-event-container');
	eventsContainer.html('');
	var sidebarEventsContainer = ice.ace.jq(this.jqId).find('.schedule-list-content');
	var currentYear = this.cfg.currentYear;
	var currentMonth = this.cfg.currentMonth;
	var currentDay = this.cfg.currentDay;
	var title = this.getMonthName(currentMonth) + ' ' + currentDay + ', ' + currentYear;
	this.jq.find('.schedule-showing').html(title);
	// set the scrollable height, if supplied
	if  (this.cfg.scrollHeight) this.jq.find('.schedule-days').css({height:this.cfg.scrollHeight});
	// set day of the week header
	var dayHeader = this.jq.find('.schedule-dow-header.schedule-dow-single');
	var date = new Date();
	date.setFullYear(currentYear);
	date.setMonth(currentMonth);
	date.setDate(currentDay);
	dayHeader.html(this.getDayOfTheWeekName(date.getDay()));
	// add calendar day CSS classes
	var displayMonth = currentMonth + 1;
	this.jq.find('.schedule-cell.schedule-dow-single').addClass('schedule-calendar-day-'+currentYear+'-'+(displayMonth < 10 ? '0' + displayMonth : displayMonth)+'-'+(currentDay < 10 ? '0' + currentDay : currentDay));
	// add event divs at appropriate positions
	var i;
	this.dayTimeSlots = [];
	for (i = 0; i < 48; i++) this.dayTimeSlots[i] = 0;
	// process all day events first
	var allDayEventCount = -1;
	for (i = 0; i < this.events.length; i++) {
		var event = this.events[i];
		var eventStartDate = new Date();
		var startYear = event.startDate.substring(0,4);
		var startMonth = parseInt(event.startDate.substring(5,7) - 1, 10);
		var startDay = event.startDate.substring(8,10);
		eventStartDate.setFullYear(startYear);
		eventStartDate.setMonth(startMonth);
		eventStartDate.setDate(startDay);
		var eventEndDate = new Date();
		var endYear = event.endDate.substring(0,4);
		var endMonth = event.endDate.substring(5,7) - 1;
		var endDay = event.endDate.substring(8,10);
		eventEndDate.setFullYear(endYear);
		eventEndDate.setMonth(endMonth);
		eventEndDate.setDate(endDay);
		if ((eventStartDate.getFullYear() == currentYear && eventStartDate.getMonth() == currentMonth 
			&& eventStartDate.getDate() == currentDay) || (eventEndDate.getFullYear() == currentYear 
			&& eventEndDate.getMonth() == currentMonth && eventEndDate.getDate() == currentDay) 
			|| (eventStartDate < date && eventEndDate > date)) {
			if (event.isAllDay) {
				allDayEventCount++;
				this.addAllDayRow(allDayEventCount);

				var customStyleClass = event.styleClass ? ' ' + event.styleClass : '';
				var eventElement = ice.ace.jq('<div class="ui-state-default schedule-dow-single schedule-event schedule-event-allday schedule-event-' + event.index + customStyleClass + '"></div>');
				eventElement.html('<span class="schedule-event-bold">' + this.getTitle(event) + '</span> ' + this.escapeHtml(event.location));
				var selector = '.schedule-dow-single.schedule-time-allday-'+allDayEventCount+' .schedule-state';
				eventElement.appendTo(ice.ace.jq(this.jqId).find(selector));
			}
		}
	}
	// process regular events
	var listing = 0;
	var isChrome = ice.ace.browser.isChrome() && navigator.userAgent.indexOf('Edge\/') == -1;
	for (i = 0; i < this.events.length; i++) {
		var event = this.events[i];
		if (event.isAllDay) continue;
		var eventStartDate = new Date();
		var startYear = event.startDate.substring(0,4);
		var startMonth = event.startDate.substring(5,7) - 1;
		var startDay = event.startDate.substring(8,10);
		eventStartDate.setFullYear(startYear);
		eventStartDate.setMonth(startMonth);
		eventStartDate.setDate(startDay);
		var eventEndDate = new Date();
		var endYear = event.endDate.substring(0,4);
		var endMonth = event.endDate.substring(5,7) - 1;
		var endDay = event.endDate.substring(8,10);
		eventEndDate.setFullYear(endYear);
		eventEndDate.setMonth(endMonth);
		eventEndDate.setDate(endDay);
		// events that start on this day
		if (eventStartDate.getFullYear() == currentYear 
			&& eventStartDate.getMonth() == currentMonth && eventStartDate.getDate() == currentDay) {
			var hour = event.startTime.substring(0,2);
			var minutes = parseInt(event.startTime.substring(3,5), 10);
			var startingTimeSlot = hour+(minutes >= 30 ? '30' : '00');
			var selector = '.schedule-dow-single.schedule-time-'+startingTimeSlot;
			var timeCell = this.jq.find(selector);
			var position = timeCell.position();
			var width = timeCell.outerWidth() - 1;
			var spansMultipleDays = !(startYear == endYear && startMonth == endMonth && startDay == endDay);
			var endHour = spansMultipleDays ? '24' : event.endTime.substring(0,2);
			var endMinutes = spansMultipleDays ? '00' : parseInt(event.endTime.substring(3,5), 10);
			var endingTimeSlot = this.determinePreviousTimeCell(endHour, endMinutes);
			var endSelector = '.schedule-dow-single.schedule-time-'+endingTimeSlot;
			var endTimeCell = this.jq.find(endSelector);
			var endPosition = endTimeCell.position();
			var height = endTimeCell.outerHeight() - 1;
			var customStyleClass = event.styleClass ? ' ' + event.styleClass : '';
			var timeSlotMultiplicity = this.dayTimeSlots[this.timeSlotIndexMap[startingTimeSlot]];
			timeSlotMultiplicity = timeSlotMultiplicity < 4 ? timeSlotMultiplicity : 4;
			var multiplicityAdjustment = timeSlotMultiplicity * 5;
			var overlappingClass = ' schedule-overlapping-' + timeSlotMultiplicity;
			var eventElement = ice.ace.jq('<div class=\"ui-state-default schedule-event schedule-event-' + event.index + customStyleClass + overlappingClass + '\"></div>');
			eventElement.html(this.getEventDivMarkup(hour, minutes, endHour, endMinutes, event));
			eventElement.css({position:'absolute',
				top:position.top+(isChrome?1:0),
				left:position.left+multiplicityAdjustment+(isChrome?1:0),
				width: (width - multiplicityAdjustment) + 'px',
				height: (endPosition.top - position.top + height) + 'px'}).appendTo(eventsContainer);
			var highlightClass = listing % 2 == 1 ? ' ui-state-highlight' : '';
			ice.ace.jq('<div role="listitem" class="schedule-list-event schedule-event-' + event.index + highlightClass + '"><span class="schedule-list-event-day">'+event.startTime+'</span><span class="schedule-list-event-name">'+this.getTitle(event)+'</span><span class="schedule-list-event-location">'+this.escapeHtml(event.location)+'</span></div>').appendTo(sidebarEventsContainer);
			this.markUsedTimeSlots(this.dayTimeSlots, startingTimeSlot, endingTimeSlot);
			listing++;
		// events that don't start on this day but end on this day
		} else if (eventEndDate.getFullYear() == currentYear 
			&& eventEndDate.getMonth() == currentMonth && eventEndDate.getDate() == currentDay) {
			if (event.endTime == '00:00') continue; // ends previous day at midnight
			var hour = '00';
			var minutes = '00';
			var startingTimeSlot = hour + minutes;
			var selector = '.schedule-dow-single.schedule-time-'+startingTimeSlot;
			var timeCell = this.jq.find(selector);
			var position = timeCell.position();
			var width = timeCell.outerWidth() - 1;
			var endHour = event.endTime.substring(0,2);
			var endMinutes = parseInt(event.endTime.substring(3,5), 10);
			var endingTimeSlot = this.determinePreviousTimeCell(endHour, endMinutes);
			var endSelector = '.schedule-dow-single.schedule-time-'+endingTimeSlot;
			var endTimeCell = this.jq.find(endSelector);
			var endPosition = endTimeCell.position();
			var height = endTimeCell.outerHeight() - 1;
			var customStyleClass = event.styleClass ? ' ' + event.styleClass : '';
			var timeSlotMultiplicity = this.dayTimeSlots[this.timeSlotIndexMap[startingTimeSlot]];
			timeSlotMultiplicity = timeSlotMultiplicity < 4 ? timeSlotMultiplicity : 4;
			var multiplicityAdjustment = timeSlotMultiplicity * 5;
			var overlappingClass = ' schedule-overlapping-' + timeSlotMultiplicity;
			var eventElement = ice.ace.jq('<div class=\"ui-state-default schedule-event schedule-event-' + event.index + customStyleClass + overlappingClass + '\"></div>');
			eventElement.html(this.getEventDivMarkup(hour, minutes, endHour, endMinutes, event, true));
			eventElement.css({position:'absolute',
				top:position.top+(isChrome?1:0),
				left:position.left+multiplicityAdjustment+(isChrome?1:0),
				width: (width - multiplicityAdjustment) + 'px',
				height: (endPosition.top - position.top + height) + 'px'}).appendTo(eventsContainer);
			var highlightClass = listing % 2 == 1 ? ' ui-state-highlight' : '';
			ice.ace.jq('<div class="schedule-list-event schedule-event-' + event.index + highlightClass + '"><span class="schedule-list-event-day">(continued)</span><span class="schedule-list-event-name">'+this.getTitle(event)+'</span><span class="schedule-list-event-location">'+this.escapeHtml(event.location)+'</span></div>').appendTo(sidebarEventsContainer);
			this.markUsedTimeSlots(this.dayTimeSlots, startingTimeSlot, endingTimeSlot);
			listing++;
		// events that neither start nor end on this day but that encompass it
		} else if (eventStartDate < date && eventEndDate > date) {
			var hour = '00';
			var minutes = '00';
			var startingTimeSlot = hour + minutes;
			var selector = '.schedule-dow-single.schedule-time-'+startingTimeSlot;
			var timeCell = this.jq.find(selector);
			var position = timeCell.position();
			var width = timeCell.outerWidth() - 1;
			var endHour = '24';
			var endMinutes = 0;
			var endingTimeSlot = '2330';
			var endSelector = '.schedule-dow-single.schedule-time-'+endingTimeSlot;
			var endTimeCell = this.jq.find(endSelector);
			var endPosition = endTimeCell.position();
			var height = endTimeCell.outerHeight() - 1;
			var customStyleClass = event.styleClass ? ' ' + event.styleClass : '';
			var timeSlotMultiplicity = this.dayTimeSlots[this.timeSlotIndexMap[startingTimeSlot]];
			timeSlotMultiplicity = timeSlotMultiplicity < 4 ? timeSlotMultiplicity : 4;
			var multiplicityAdjustment = timeSlotMultiplicity * 5;
			var overlappingClass = ' schedule-overlapping-' + timeSlotMultiplicity;
			var eventElement = ice.ace.jq('<div class=\"ui-state-default schedule-event schedule-event-' + event.index + customStyleClass + overlappingClass + '\"></div>');
			eventElement.html(this.getEventDivMarkup(hour, minutes, endHour, endMinutes, event, true));
			eventElement.css({position:'absolute',
				top:position.top+(isChrome?1:0),
				left:position.left+multiplicityAdjustment+(isChrome?1:0),
				width: (width - multiplicityAdjustment) + 'px',
				height: (endPosition.top - position.top + height) + 'px'}).appendTo(eventsContainer);
			var highlightClass = listing % 2 == 1 ? ' ui-state-highlight' : '';
			ice.ace.jq('<div class="schedule-list-event schedule-event-' + event.index + highlightClass + '"><span class="schedule-list-event-day">(continued)</span><span class="schedule-list-event-name">'+this.getTitle(event)+'</span><span class="schedule-list-event-location">'+this.escapeHtml(event.location)+'</span></div>').appendTo(sidebarEventsContainer);
			this.markUsedTimeSlots(this.dayTimeSlots, startingTimeSlot, endingTimeSlot);
			listing++;
		}
	}
	this.expandEventList();
	if  (this.cfg.scrollHeight) {
		if (window[this.id + '_lastScrollTop']) {
			this.jqRoot.find('.schedule-days').animate({scrollTop: window[this.id + '_lastScrollTop']}, 1);
		}
		window[this.id + '_lastScrollTop'] = 0;
	}
};

ice.ace.Schedule.prototype.markUsedTimeSlots = function(timeSlots, startingTimeSlot, endingTimeSlot) {
	var startingIndex = this.timeSlotIndexMap[startingTimeSlot];
	var endingIndex = this.timeSlotIndexMap[endingTimeSlot];
	var i;
	for (i = startingIndex; i <= endingIndex; i++) timeSlots[i] = timeSlots[i] + 1;
};

ice.ace.Schedule.prototype.timeSlotIndexMap = {'0000':0, '0030':1, '0100':2, '0130':3, '0200':4, '0230':5,
	'0300':6, '0330':7, '0400':8, '0430':9, '0500':10, '0530':11, '0600':12, '0630':13, '7000':14, '0730':15,
	'0800':16, '0830':17, '0900':18, '0930':19, '1000':20, '1030':21, '1100':22, '1130':23, '1200':24, '1230':25,
	'1300':26, '1330':27, '1400':28, '1430':29, '1500':30, '1530':31, '1600':32, '1630':33, '1700':34, '1730':35,
	'1800':36, '1830':37, '1900':38, '1930':39, '2000':40, '2030':41, '2100':42, '2130':43, '2200':44, '2230':45,
	'2300':46, '2330':47
};

ice.ace.Schedule.prototype.getEventDivMarkup = function(startHour, startMinutes, endHour, endMinutes, event, continued) {
	var lines;
	if (startHour == endHour) { // only one line of text
		// only possible case start time is at 00 minutes, end time is at 30 minutes
		lines = 1;
	} else if ((parseInt(endHour, 10) - parseInt(startHour, 10)) == 1) {
		// case when start time is previous hour at 30 minutes and end time is this hour at 00 minutes (1 line)
		if (startMinutes == '30' && endMinutes == '00') lines = 1;
		// case when start time is previous hour at 30 minutes and end time is this hour at 30 minutes (2 lines)
		if (startMinutes == '30' && endMinutes == '30') lines = 2;
		// case when start time is previous hour at 00 minutes and end time is this hour at 00 minutes (2 lines)
		if (startMinutes == '00' && endMinutes == '00') lines = 2;
		// case when start time is previous hour at 00 minutes and end time is this hour at 30 minutes (3 lines)
		if (startMinutes == '00' && endMinutes == '30') lines = 3;
	} else { // 2 or more hours difference (3 lines)
		lines = 3;
	}
	if (lines == 1) {
		return (continued?'(cont.)':'') + '<span class="schedule-event-bold">' + this.getTitle(event) + '</span> ' + this.escapeHtml(event.location);
	} else {
		var startTime;
		if (continued) {
			startTime = '(cont.)';
		} else if (this.cfg.isTwelveHourClock) {
			var hour = parseInt(startHour, 10);
			var minutes = this.addLeadingZero(startMinutes);
			startTime = hour < 13 ? (hour == 12 ? '12:' + minutes + ' pm' : hour + ':' + minutes + ' am') : hour - 12 + ':' + minutes + ' pm';
		} else {
			startTime = event.startTime;		
		}
		if (lines == 2) {
			return startTime + '<br/><span class="schedule-event-bold">' + this.getTitle(event) + '</span> ' + this.escapeHtml(event.location);
		} else {
			return startTime + '<br/><span class="schedule-event-bold">' + this.getTitle(event) + '</span><br/>' + this.escapeHtml(event.location);
		}
	}
};

ice.ace.Schedule.prototype.addAllDayRow = function(count) {

	if (!count) count = 0;

	var daysGrid = ice.ace.jq(this.jqId).find('.schedule-days > table > tbody');

	if (this.cfg.viewMode == 'week') {
		var markup = '<tr>';
		markup += '<td class="ui-widget-content schedule-cell schedule-cell-time schedule-cell-allday">' + this.messages.ALLDAY + '</td>';
		
		var i;
		for (i = 0; i < 7; i++) {
			markup += '<td class="ui-widget-content schedule-cell schedule-dow-' + i + ' schedule-time-allday schedule-time-allday-' + count + '"><div class="schedule-state"></div></td>';
		}

		markup += '</tr>';

		ice.ace.jq(markup).prependTo(daysGrid);
	} else if (this.cfg.viewMode == 'day') {
		var markup = '<tr>';
		markup += '<td class="ui-widget-content schedule-cell schedule-cell-time schedule-cell-allday">' + this.messages.ALLDAY + '</td>';

		markup += '<td class="ui-widget-content schedule-cell schedule-dow-single schedule-time-allday schedule-time-allday-' + count + '"><div class="schedule-state"></div></td>';

		markup += '</tr>';

		ice.ace.jq(markup).prependTo(daysGrid);
	}
};

ice.ace.Schedule.prototype.addNavigationListeners = function() {
	var self = this;
	var previousButton = this.jq.find('.schedule-button-previous');
	var nextButton = this.jq.find('.schedule-button-next');
	var view = this.cfg.viewMode;
	var currentYear = this.cfg.currentYear;
	var currentMonth = this.cfg.currentMonth;
	var currentDay = this.cfg.currentDay;
	var is31DaysMonth = currentMonth == 0 || currentMonth == 2 || currentMonth == 4 || currentMonth == 6
		|| currentMonth == 7 || currentMonth == 9 || currentMonth == 11;
	var isLeapYear = ((currentYear % 4 == 0) && (currentYear % 100 != 0)) || (currentYear % 400 == 0);
	previousButton.on('click', function(e) {
		e.stopPropagation();
		if (view == 'week') {
			if (is31DaysMonth) {
				if (currentMonth == 0) {
					if (currentDay <= 7) {
						currentYear--;
						currentMonth = 11;
						currentDay = currentDay - 7 + 31;
					} else {
						currentDay = currentDay - 7;
					}
				} else if (currentMonth == 2) {
					if (isLeapYear) {
						if (currentDay <= 7) {
							currentMonth = 1;
							currentDay = currentDay - 7 + 29;
						} else {
							currentDay = currentDay - 7;
						}
					} else {
						if (currentDay <= 7) {
							currentMonth = 1;
							currentDay = currentDay - 7 + 28;
						} else {
							currentDay = currentDay - 7;
						}
					}
				} else if (currentMonth == 7) {
					if (currentDay <= 7) {
						currentMonth = 6;
						currentDay = currentDay - 7 + 31;
					} else {
						currentDay = currentDay - 7;
					}
				} else if (currentDay <= 7) {
					currentMonth--;
					currentDay = currentDay - 7 + 30;
				} else {
					currentDay = currentDay - 7;
				}
			} else {
				if (currentDay <= 7) {
					currentMonth--;
					currentDay = currentDay - 7 + 31;
				} else {
					currentDay = currentDay - 7;
				}
			}
		} else if (view == 'day') {
			if (is31DaysMonth) {
				if (currentMonth == 0) {
					if (currentDay == 1) {
						currentYear--;
						currentMonth = 11;
						currentDay = 31;
					} else {
						currentDay--;
					}
				} else if (currentMonth == 2) {
					if (isLeapYear) {
						if (currentDay == 1) {
							currentMonth = 1;
							currentDay = 29;
						} else {
							currentDay--;
						}
					} else {
						if (currentDay == 1) {
							currentMonth = 1;
							currentDay = 28;
						} else {
							currentDay--;
						}
					}
				} else if (currentMonth == 7) {
					if (currentDay == 1) {
						currentMonth = 6;
						currentDay = 31;
					} else {
						currentDay--;
					}
				} else if (currentDay == 1) {
					currentMonth--;
					currentDay = 30;
				} else {
					currentDay--;
				}
			} else {
				if (currentDay == 1) {
					currentMonth--;
					currentDay = 31;
				} else {
					currentDay--;
				}
			}
		} else {
			if (currentMonth == 0) {
				currentYear--;
				currentMonth = 11;
			} else currentMonth--;
			currentDay = 1;
		}
		var oldYear = self.cfg.currentYear;
		var oldMonth = self.cfg.currentMonth;
		var oldDay = self.cfg.currentDay;
		self.cfg.currentYear = currentYear;
		self.cfg.currentMonth = currentMonth;
		self.cfg.currentDay = currentDay;
		document.getElementById(self.id + '_currentYear').setAttribute('value', currentYear);
		document.getElementById(self.id + '_currentMonth').setAttribute('value', currentMonth);
		document.getElementById(self.id + '_currentDay').setAttribute('value', currentDay);
		// save scroll position
		window[self.id + '_lastScrollTop'] = self.jqRoot.find('.schedule-days').get(0).scrollTop;
		if (!self.cfg.isLazy) {
			self.render();
		}
		self.sendNavigationRequest(e, currentYear, currentMonth, currentDay, oldYear, oldMonth, oldDay, 'previous');
	});
	nextButton.on('click', function(e) {
		e.stopPropagation();
		if (view == 'week') {
			if (is31DaysMonth) {
				if (currentMonth == 11) {
					if (currentDay >= 25) {
						currentYear++;
						currentMonth = 0;
						currentDay = currentDay + 7 - 31;
					} else {
						currentDay = currentDay + 7;
					}
				} else if (currentDay >= 24) {
					currentMonth++;
					currentDay = currentDay + 7 - 31;
				} else {
					currentDay = currentDay + 7;
				}
			} else {
				if (currentMonth == 1) {
					if (isLeapYear) {
						if (currentDay >= 23) {
							currentMonth = 2;
							currentDay = currentDay + 7 - 29;
						} else {
							currentDay = currentDay + 7;
						}
					} else {
						if (currentDay >= 22) {
							currentMonth = 2;
							currentDay = currentDay + 7 - 28;
						} else {
							currentDay = currentDay + 7;
						}
					}
				} else if (currentDay >= 24) {
					currentMonth++;
					currentDay = currentDay + 7 - 30;
				} else {
					currentDay = currentDay + 7;
				}
			}
		} else if (view == 'day') {
			if (is31DaysMonth) {
				if (currentMonth == 11) {
					if (currentDay == 31) {
						currentYear++;
						currentMonth = 0;
						currentDay = 1;
					} else {
						currentDay++;
					}
				} else if (currentDay == 31) {
					currentMonth++;
					currentDay = 1;
				} else {
					currentDay++;
				}
			} else {
				if (currentMonth == 1) {
					if (isLeapYear) {
						if (currentDay == 29) {
							currentMonth = 2;
							currentDay = 1;
						} else {
							currentDay++;
						}
					} else {
						if (currentDay == 28) {
							currentMonth = 2;
							currentDay = 1;
						} else {
							currentDay++;
						}
					}
				} else if (currentDay == 30) {
					currentMonth++;
					currentDay = 1;
				} else {
					currentDay++;
				}
			}
		} else {
			if (currentMonth == 11) {
				currentYear++;
				currentMonth = 0;
			} else currentMonth++;
			currentDay = 1;
		}
		var oldYear = self.cfg.currentYear;
		var oldMonth = self.cfg.currentMonth;
		var oldDay = self.cfg.currentDay;
		self.cfg.currentYear = currentYear;
		self.cfg.currentMonth = currentMonth;
		self.cfg.currentDay = currentDay;
		document.getElementById(self.id + '_currentYear').setAttribute('value', currentYear);
		document.getElementById(self.id + '_currentMonth').setAttribute('value', currentMonth);
		document.getElementById(self.id + '_currentDay').setAttribute('value', currentDay);
		// save scroll position
		window[self.id + '_lastScrollTop'] = self.jqRoot.find('.schedule-days').get(0).scrollTop;
		if (!self.cfg.isLazy) {
			self.render();
		}
		self.sendNavigationRequest(e, currentYear, currentMonth, currentDay, oldYear, oldMonth, oldDay, 'next');
	});
	// tabindex and keyboard navigation
	previousButton.attr('tabindex', this.cfg.tabindex);
	nextButton.attr('tabindex', this.cfg.tabindex);
	previousButton.on('keydown', function(e) {
		if(e.keyCode == 32 || e.keyCode == 13){
			previousButton.trigger('click');
			e.preventDefault();
			e.stopPropagation();
		}
	});
	nextButton.on('keydown', function(e) {
		if(e.keyCode == 32 || e.keyCode == 13){
			nextButton.trigger('click');
			e.preventDefault();
			e.stopPropagation();
		}
	});
	// set up navigation dialog
	var currentDateNode = ice.ace.jq(this.jqId).find('.schedule-showing');
	currentDateNode.on('click', function() {
		var navigationDialog = ice.ace.jq(self.jqId).find('.schedule-navigation-dialog-content');
		navigationDialog.html('<div></div>');
		navigationDialog.dialog({resizable: false, draggable: false, dialogClass: 'schedule-navigation-dialog', 
			position: { my: "center top", at: "center top", of: currentDateNode.get(0) }});
		ice.ace.jq(self.jqId).find('.schedule-navigation-dialog').attr('role', 'dialog').attr('aria-label', 'navigation dialog').css('width', '').show();
		navigationDialog.children().first().datepicker({dateFormat: 'yy-mm-dd', onSelect: function(date) {
			ice.ace.jq(self.jqId).find('.schedule-navigation-dialog').hide();
			document.getElementById(self.id + '_selectedDate').setAttribute('value', date);
			var currentYear = date.substring(0, 4);
			var currentMonth = date.substring(5, date.indexOf('-', 5));
			currentMonth = parseInt(currentMonth, 10) - 1;
			var currentDay;
			if (view == 'month') {
				currentDay = 1;
			} else {
				currentDay = date.substring(date.indexOf('-', 5) + 1);
				currentDay = parseInt(currentDay, 10);
			}
			if (view != 'day') { // set to previous Sunday
				var dateObject = new Date(currentYear, currentMonth, currentDay, 0, 0, 0, 0);
				dateObject.setDate(dateObject.getDate() - dateObject.getDay());
				currentYear = dateObject.getFullYear()
				currentMonth = dateObject.getMonth();
				currentDay = dateObject.getDate();
			}
			var oldYear = self.cfg.currentYear;
			var oldMonth = self.cfg.currentMonth;
			var oldDay = self.cfg.currentDay;
			self.cfg.currentYear = currentYear;
			self.cfg.currentMonth = currentMonth;
			self.cfg.currentDay = currentDay;
			document.getElementById(self.id + '_currentYear').setAttribute('value', currentYear);
			document.getElementById(self.id + '_currentMonth').setAttribute('value', currentMonth);
			document.getElementById(self.id + '_currentDay').setAttribute('value', currentDay);
			// save scroll position
			window[self.id + '_lastScrollTop'] = self.jqRoot.find('.schedule-days').get(0).scrollTop;
			if (!self.cfg.isLazy) {
				self.render();
			}
			self.sendSelectionRequest();
			//self.sendNavigationRequest(e, currentYear, currentMonth, currentDay, oldYear, oldMonth, oldDay, 'selection');
		}});
		navigationDialog.on('mouseleave', function(event) {
			ice.ace.jq(self.jqId).find('.schedule-navigation-dialog').hide();
		});
	});
};

ice.ace.Schedule.windowResizeListeners = {};

ice.ace.Schedule.prototype.addResizeListeners = function() {
	var self = this;
	// sidebar
	if (this.cfg.resizableSidebar && (this.jq.hasClass('schedule-config-sidebar-right')
			|| this.jq.hasClass('schedule-config-sidebar-left'))) {
		var handles = this.jq.hasClass('schedule-config-sidebar-right') ? 'w' : 'e';
		self.sidebarOriginalSize = 0;
		self.gridOriginalWidth = 0;
		this.jq.find('.schedule-sidebar').resizable({handles: handles, containment: 'parent', 
			create: function (event, ui) {
				ice.ace.jq(this).parent().on('resize', function (e) {
					e.stopPropagation();
				});
			},
			start: function(event, ui) {
				event.stopPropagation();
				self.sidebarOriginalSize = ui.originalSize.width;
				self.gridOriginalWidth = self.jq.find('.schedule-grid').width();
				if (self.jq.hasClass('schedule-config-sidebar-left'))
					self.jq.find('.schedule-grid').css({position: 'absolute', top: 0, right: 0});
			},
			resize: function(event, ui) {
				event.stopPropagation();
				var grid = self.jq.find('.schedule-grid');
				grid.width(self.gridOriginalWidth + (self.sidebarOriginalSize - ui.size.width));

				var isChrome = ice.ace.browser.isChrome() && navigator.userAgent.indexOf('Edge\/') == -1;
				if (self.cfg.viewMode == 'day') {
					var timeCell = self.jq.find('.schedule-dow-single.schedule-time-0000');
					var timeCellWidth = timeCell.outerWidth() - 1;
					var timeCellLeft = timeCell.position().left;
					var events = self.jq.find('.schedule-event').not('.schedule-event-allday');
					events.each(function(){
						var eventIndex = self.extractEventIndex(this);
						var eventData = self.eventsMap[''+eventIndex];
						var hour = eventData.startTime.substring(0,2);
						var minutes = parseInt(eventData.startTime.substring(3,5), 10);
						var startingTimeSlot = hour+(minutes >= 30 ? '30' : '00');
						var timeSlotMultiplicity = self.extractOverlappingLevel(this);
						timeSlotMultiplicity = timeSlotMultiplicity < 4 ? timeSlotMultiplicity : 4;
						var multiplicityAdjustment = timeSlotMultiplicity * 5;
						ice.ace.jq(this).css({width: timeCellWidth - multiplicityAdjustment,
							left: timeCellLeft + multiplicityAdjustment + (isChrome?1:0)});
					});
				} else if (self.cfg.viewMode == 'week') {
					var i;
					for (i = 0; i < 7; i++) {
						var timeCell = self.jq.find('.schedule-dow-' + i + '.schedule-time-0000');
						var timeCellWidth = timeCell.outerWidth() - 1;
						var timeCellLeft = timeCell.position().left;
						var events = self.jq.find('.schedule-event.schedule-dow-' + i).not('.schedule-event-allday');
						events.each(function(){
							var eventIndex = self.extractEventIndex(this);
							var eventData = self.eventsMap[''+eventIndex];
							var hour = eventData.startTime.substring(0,2);
							var minutes = parseInt(eventData.startTime.substring(3,5), 10);
							var startingTimeSlot = hour+(minutes >= 30 ? '30' : '00');
							var timeSlotMultiplicity = self.extractOverlappingLevel(this);
							timeSlotMultiplicity = timeSlotMultiplicity < 4 ? timeSlotMultiplicity : 4;
							var multiplicityAdjustment = timeSlotMultiplicity * 5;
							ice.ace.jq(this).css({width: timeCellWidth - multiplicityAdjustment,
								left: timeCellLeft + multiplicityAdjustment + (isChrome?1:0)});
						});
					}
				}
			}
		});
	}

	// window
	if (ice.ace.Schedule.windowResizeListeners[this.id])
		ice.ace.jq(window).off('resize', ice.ace.Schedule.windowResizeListeners[this.id]);

	ice.ace.Schedule.windowResizeListeners[this.id] = function(e) {
		var root = ice.ace.jq(self.jqId);
		var grid = root.find('.schedule-grid');
		var sidebar = root.find('.schedule-sidebar');
		grid.attr('style', '');
		sidebar.attr('style', '');

		var isChrome = ice.ace.browser.isChrome() && navigator.userAgent.indexOf('Edge\/') == -1;
		if (self.cfg.viewMode == 'day') {
			var timeCell = root.find('.schedule-dow-single.schedule-time-0000');
			var timeCellWidth = timeCell.outerWidth() - 1;
			var timeCellLeft = timeCell.position().left;
			var events = root.find('.schedule-event').not('.schedule-event-allday');
			events.each(function(){
				var eventIndex = self.extractEventIndex(this);
				var eventData = self.eventsMap[''+eventIndex];
				var hour = eventData.startTime.substring(0,2);
				var minutes = parseInt(eventData.startTime.substring(3,5), 10);
				var startingTimeSlot = hour+(minutes >= 30 ? '30' : '00');
				var timeSlotMultiplicity = self.extractOverlappingLevel(this);
				timeSlotMultiplicity = timeSlotMultiplicity < 4 ? timeSlotMultiplicity : 4;
				var multiplicityAdjustment = timeSlotMultiplicity * 5;
				ice.ace.jq(this).css({width: timeCellWidth - multiplicityAdjustment,
					left: timeCellLeft + multiplicityAdjustment + (isChrome?1:0)});
			});
		} else if (self.cfg.viewMode == 'week') {
			var i;
			for (i = 0; i < 7; i++) {
				var timeCell = root.find('.schedule-dow-' + i + '.schedule-time-0000');
				var timeCellWidth = timeCell.outerWidth() - 1;
				var timeCellLeft = timeCell.position().left;
				var events = root.find('.schedule-event.schedule-dow-' + i).not('.schedule-event-allday');
				events.each(function(){
					var eventIndex = self.extractEventIndex(this);
					var eventData = self.eventsMap[''+eventIndex];
					var hour = eventData.startTime.substring(0,2);
					var minutes = parseInt(eventData.startTime.substring(3,5), 10);
					var startingTimeSlot = hour+(minutes >= 30 ? '30' : '00');
					var timeSlotMultiplicity = self.extractOverlappingLevel(this);
					timeSlotMultiplicity = timeSlotMultiplicity < 4 ? timeSlotMultiplicity : 4;
					var multiplicityAdjustment = timeSlotMultiplicity * 5;
					ice.ace.jq(this).css({width: timeCellWidth - multiplicityAdjustment,
						left: timeCellLeft + multiplicityAdjustment + (isChrome?1:0)});
				});
			}
		}
	};

	ice.ace.jq(window).on('resize', ice.ace.Schedule.windowResizeListeners[this.id]);
};

ice.ace.Schedule.prototype.determineLastDayOfWeek = function(currentYear, currentMonth, currentDay) {
	var endYear = currentYear;
	var endMonth = currentMonth;
	var endDay = currentDay;
	var is31DaysMonth = currentMonth == 0 || currentMonth == 2 || currentMonth == 4 || currentMonth == 6
		|| currentMonth == 7 || currentMonth == 9 || currentMonth == 11;
	var isLeapYear = ((currentYear % 4 == 0) && (currentYear % 100 != 0)) || (currentYear % 400 == 0);
	if (is31DaysMonth) {
		if (currentMonth == 11) {
			if (currentDay >= 26) {
				endYear = currentYear + 1;
				endMonth = 0;
				endDay = currentDay + 6 - 31;
			} else {
				endDay = currentDay + 6;
			}
		} else if (currentDay >= 26) {
			endMonth = currentMonth + 1;
			endDay = currentDay + 6 - 31;
		} else {
			endDay = currentDay + 6;
		}
	} else {
		if (currentMonth == 1) {
			if (isLeapYear) {
				if (currentDay >= 24) {
					endMonth = 2;
					endDay = currentDay + 6 - 29;
				} else {
					endDay = currentDay + 6;
				}
			} else {
				if (currentDay >= 23) {
					endMonth = 2;
					endDay = currentDay + 6 - 28;
				} else {
					endDay = currentDay + 6;
				}
			}
		} else if (currentDay >= 25) {
			endMonth = currentMonth + 1;
			endDay = currentDay + 6 - 30;
		} else {
			endDay = currentDay + 6;
		}
	}
	return {year: endYear, month: endMonth, day: endDay};
};

ice.ace.Schedule.prototype.determineLastDayOfMonth = function(currentYear, currentMonth) {
	var is31DaysMonth = currentMonth == 0 || currentMonth == 2 || currentMonth == 4 || currentMonth == 6
		|| currentMonth == 7 || currentMonth == 9 || currentMonth == 11;
	var isLeapYear = ((currentYear % 4 == 0) && (currentYear % 100 != 0)) || (currentYear % 400 == 0);
	if (currentMonth == 1) {
		if (isLeapYear) return 29;
		else return 28;
	} else if (is31DaysMonth) {
		return 31;
	} else {
		return 30;
	}
};

ice.ace.Schedule.prototype.determinePreviousTimeCell = function(hour, minutes) {
	var previousHour, previousMinutes;
	if (parseInt(minutes, 10) >= 30) {
		previousMinutes = '00';
		previousHour = hour;
	} else {
		if (hour == '00') {
			previousMinutes = '00';
			previousHour = '00';
		} else {
			previousMinutes = '30';
			previousHour = parseInt(hour, 10) - 1;
			previousHour = previousHour < 10 ? '0' + previousHour : previousHour;
		}
	}
	return previousHour + previousMinutes;
};

ice.ace.Schedule.prototype.expandEventList = function() {
	var contentHeight = this.getSidebarContentHeight();
	var listContent = ice.ace.jq(this.jqId).find('.schedule-list-content');
	var detailsContent = ice.ace.jq(this.jqId).find('.schedule-details-sidebar-content');
	detailsContent.css('height', '0');
	listContent.css('height', contentHeight + 'px');
};

ice.ace.Schedule.prototype.expandEventDetails = function() {
	var listContent = ice.ace.jq(this.jqId).find('.schedule-list-content');
	var detailsContent = ice.ace.jq(this.jqId).find('.schedule-details-sidebar-content');
	listContent.css('height', '0');
	detailsContent.css('height', 'auto');
};

ice.ace.Schedule.prototype.getSidebarContentHeight = function() {
	var sidebar = ice.ace.jq(this.jqId).find('.schedule-sidebar');
	var sidebarHeight = sidebar.outerHeight();
	var listTitle = ice.ace.jq(this.jqId).find('.schedule-list-title');
	var listTitleHeight = listTitle.outerHeight();
	var detailsTitle = ice.ace.jq(this.jqId).find('.schedule-details-sidebar-title');
	var detailsTitleHeight = detailsTitle.outerHeight();
	return sidebarHeight - listTitleHeight - detailsTitleHeight;
};

ice.ace.Schedule.prototype.getMonthName = function(monthNumber) {
	var msgs = this.messages;
	var months = [msgs.January, msgs.February, msgs.March, msgs.April, msgs.May, msgs.June,
		msgs.July, msgs.August, msgs.September, msgs.October, msgs.November, msgs.December];
	return months[monthNumber];
};
ice.ace.Schedule.prototype.getMonthNameShort = function(monthNumber) {
	var msgs = this.messages;
	var months = [msgs.Jan, msgs.Feb, msgs.Mar, msgs.Apr, msgs.May, msgs.Jun,
		msgs.Jul, msgs.Aug, msgs.Sep, msgs.Oct, msgs.Nov, msgs.Dec];
	return months[monthNumber];
};
ice.ace.Schedule.prototype.getDayOfTheWeekName = function(dayNumber) {
	var msgs = this.messages;
	var days = [msgs.Sunday, msgs.Monday, msgs.Tuesday, msgs.Wednesday, 
		msgs.Thursday, msgs.Friday, msgs.Saturday];
	return days[dayNumber];
};
ice.ace.Schedule.prototype.getDayOfTheWeekNameShort = function(dayNumber) {
	var msgs = this.messages;
	var days = [msgs.Sun, msgs.Mon, msgs.Tue, msgs.Wed, 
		msgs.Thu, msgs.Fri, msgs.Sat];
	return days[dayNumber];
};

ice.ace.Schedule.prototype.addLeadingZero = function(number) {
	if (number < 10) return ('0' + number);
	else return ('' + number);
};

ice.ace.Schedule.prototype.escapeHtml = function(str) {
	var div = document.createElement('div');
	div.appendChild(document.createTextNode(str));
	return div.innerHTML;
};

ice.ace.Schedule.prototype.getTitle = function(event) {
	var title = event.title;
	if (title) title = this.escapeHtml(title);
	else title = '(no title)';
	return title;
};

// replace spaces for non-breaking spaces
ice.ace.Schedule.prototype.replaceSpaces = function(str) {
	return str.replace(/ /g, '&nbsp;');
};

ice.ace.Schedule.prototype.unload = function() {
	this.jqRoot.off();
	this.jq.find('.schedule-button-previous').off();
	this.jq.find('.schedule-button-next').off();
	this.jq.find('.schedule-sidebar').resizable('destroy');
	ice.ace.jq(this.jqId).find('.schedule-details-popup-content').find('*').off();
	ice.ace.jq(this.jqId).find('.schedule-details-sidebar-content').find('*').off();
	ice.ace.jq(window).off('resize', ice.ace.Schedule.windowResizeListeners[this.id]);
};