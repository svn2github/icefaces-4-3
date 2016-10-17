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

(function() {
    function noop() {
    }

    function formOf(id) {
        var cursor = document.getElementById(id);
        while (cursor) {
            if (cursor.nodeName.toLowerCase() == 'form') {
                return cursor;
            }
            cursor = cursor.parentNode;
        }

        return null;
    }

    function isImmediate(element, options) {
        if (ice.ace.immediateComponents) {
            var cursor = element;
            while (cursor) {
                for (var i = 0, l = ice.ace.immediateComponents.length; i < l; i++) {
                    var entry = ice.ace.immediateComponents[i];
                    //verify if the submit event type matches the one defined (the event is undefined when 'immediate'
                    //is defined for the component)
                    var id = entry[0];
                    if (cursor.id == id) {
                        var eventType = entry[1];
                        if (eventType) {
                            return options['javax.faces.behavior.event'] == eventType;
                        } else {
                            return true;
                        }
                    }
                }
                cursor = cursor.parentNode;
            }
        }

        return false;
    }

    function cleanupMessages(elements) {
        for (var i = 0, l = elements.length; i < l; i++) {
            var element = elements[i];
            element.className = element.className.replace(' ui-state-error', '');
            var cleanupValidationMessage = element.cleanupValidationMessage;
            if (cleanupValidationMessage) {
                try {
                    cleanupValidationMessage();
                } catch (ex) {
                    //the node is cleared a second time
                }
            }
        }
    }

    var old = jsf.ajax.request;
    jsf.ajax.request = function (element, event, options) {
        var e = ((typeof element) == 'string') ? document.getElementById(element) : element;

        if (options && options['ice.skipClientValidation']) {
            old(element, event, options);
        } else {
            var form = formOf(e.id);
            var jqForm = ice.ace.jq(form);
            var validationResult = jqForm.validate();
            var validElements = validationResult.validElements();
            //determine what rules should not be applied (outside of the 'execute'd components)
            var disabledRules = disableRules(validationResult, options);
            try {
                //run validation
                var isValidForm = jqForm.valid();
                if (validElements) {
                    cleanupMessages(validElements);
                }

                var skipValidation = false;
                var targetElementID = options['ice.event.target'];
                //second try for finding for finding enclosing immediate component root is for components that use submitting
                //element located outside of the component markup
                if (isImmediate(element, options) || (targetElementID && isImmediate(document.getElementById(targetElementID), options))) {
                    skipValidation = true;
                    cleanupMessages(validationResult.invalidElements());
                }

                if (skipValidation || (form && isValidForm)) {
                    old(element, event, options);
                }
            } finally {
                enableRules(validationResult, disabledRules);
            }
        }
    };

    function disableRules(validationResult, options) {
        var rules = validationResult.settings.rules;
        var executedComponents = (options.execute || '@all').split(' ');
        var disabledRules = {};
        for (var p in rules) {
            if (rules.hasOwnProperty(p)) {
                disabledRules[p] = rules[p];
            }
        }
        for (var i = 0, l = executedComponents.length; i < l; i++) {
            var id = executedComponents[i];
            if ('@form' == id || '@all' == id) {
                return {};
            } else if (rules[id]) {
                delete disabledRules[id];
            } else {
                var e = document.getElementById(id);
                for (var p in rules) {
                    if (rules.hasOwnProperty(p)) {
                        var potentialChild = document.getElementById(p);
                        if (isParent(e, potentialChild)){
                            delete disabledRules[p];
                        }
                    }
                }
            }
        }

        for (var p in disabledRules) {
            if (disabledRules.hasOwnProperty(p)) {
                delete rules[p];
            }
        }
    }

    function enableRules(validationResult, disabledRules) {
        var rules = validationResult.settings.rules;
        for (var p in disabledRules) {
            if (disabledRules.hasOwnProperty(p)) {
                rules[p] = disabledRules[p];
            }
        }
    }

    function isParent(parent, child) {
        var cursor = child;
        while (cursor) {
            if (cursor == parent) {
                return true;
            }
            cursor = cursor.parentNode;
        }

        return false;
    }

	function updateContainerClassName(value) {
		if (!value) return '';
		var ret = value.replace('ui-state-highlight', '');
		ret = ret.indexOf('ui-state-error') > -1 ? ret : ret + ' ui-state-error';
		return ret;
	}

	function updateIconClassName(value) {
		if (!value) return '';
		var ret = value.replace('ui-icon-info', '');
		ret = ret.indexOf('ui-icon-alert') > -1 ? ret : ret + ' ui-icon-alert';
		return ret;
	}

    function clientValidationMessageFor(id, text) {
        return function (parameter, element) {
            var messageId = id + '_msg';
            var container = document.getElementById(messageId);
            if (container.childNodes.length == 0) {
                element.className += ' ui-state-error';
                container.className = 'ui-widget ui-corner-all ui-state-error';
                var icon = container.appendChild(document.createElement('span'));
                icon.className = 'ui-faces-message-icon';
                var iconAlert = icon.appendChild(document.createElement('span'));
                iconAlert.className = 'ui-icon ui-icon-alert';
                var message = container.appendChild(document.createElement('span'));
                message.className = 'ui-faces-message-text';
                message.appendChild(document.createTextNode(text));

                element.cleanupValidationMessage = function () {
                    container.className = '';
                    container.removeChild(icon);
                    container.removeChild(message);
                };
            } else {
				if (element.className.indexOf('ui-state-error') == -1)
					element.className += ' ui-state-error';
				container.className = updateContainerClassName(container.className);
                container.childNodes[0].childNodes[0].className = 
					updateIconClassName(container.childNodes[0].childNodes[0].className);
                container.childNodes[1].innerHTML = text;
            }

            return '';
        };
    }

    function clientValidationMessagesFor(id, text) {
        return function (parameter, element) {
            var messageId = id + '_msg_' + element.id;
            var container = document.getElementById(id);
            var node = document.getElementById(messageId);
            if (!node) {
                element.className += ' ui-state-error';
                node = container.appendChild(document.createElement('div'));
                node.id = messageId;
                node.className = 'ui-corner-all ui-state-error';
                var icon = node.appendChild(document.createElement('span'));
                icon.className = 'ui-icon ui-icon-alert';
                node.appendChild(document.createTextNode(text));

                element.cleanupValidationMessage = function () {
                    container.removeChild(node);
                };
            } else {
				if (element.className.indexOf('ui-state-error') == -1)
					element.className += ' ui-state-error';
				node.className = updateContainerClassName(node.className);
                node.childNodes[0].className = 
					updateIconClassName(node.childNodes[0].className);
                node.childNodes[1].nodeValue = text;
            }

            return '';
        }
    }

    function clientValidationGrowlMessagesFor(id, messageText, config) {
        return function (parameter, element) {
            if (element.validationMessageDisplayed) {
                if (element.validationMessageDisplayed != messageText) {
                    var messages = ice.ace.jq('#jGrowl > .jGrowl-notification');
                    messages.each(function (index, messageContainer) {
                        //remove old message and add the new one when the validation message changes for the given component
                        if (messageContainer.innerHTML.indexOf(element.validationMessageDisplayed) >= 0) {
                            messageContainer.parentElement.removeChild(messageContainer);
                            config.msgs[0].text = messageText;
                            ice.ace.GrowlMessages(id, config);
                        }
                    });
                    element.validationMessageDisplayed = messageText;
                }
            } else {
                config.msgs[0].text = messageText;
                ice.ace.GrowlMessages(id, config);
                element.validationMessageDisplayed = messageText;
                ice.ace.jq('#jGrowl').bind('jGrowl.close', function (messageContainer) {
                    if (messageContainer.target.innerHTML.indexOf(messageText) >= 0) {
                        delete element.validationMessageDisplayed;
                    }
                });
                element.cleanupValidationMessage = function () {
                    var messages = ice.ace.jq('#jGrowl > .jGrowl-notification');
                    messages.each(function (index, messageContainer) {
                        //remove old message and add the new one when the validation message changes for the given component
                        if (messageContainer.innerHTML.indexOf(element.validationMessageDisplayed) >= 0) {
                            messageContainer.parentElement.removeChild(messageContainer);
                            delete element.validationMessageDisplayed;
                        }
                    });
                };
            }
        };
    }

    ice.ace.setupClientValidation = function (id, focusID, rule, config, messageType, message, immediate, customEvents) {
        var form = formOf(id);
        if (!form.enabledValidation) {
            var jqForm = ice.ace.jq(ice.ace.escapeClientId(form.id));
            //cleanup validation messages for elements that where changed after last submit and now pass validation
            jqForm.validate().settings.showErrors = function() {
                cleanupMessages(jqForm.validate().successList);
            };
            form.enabledValidation = true;
        }

        var selector = ice.ace.escapeClientId(id);
        ice.ace.jq(selector).rules('remove', rule);

        var ruleConfig = {};
        ruleConfig[rule] = config;
        var messageCreator = {};
        if (messageType.aceMessage) {
            messageCreator[rule] = clientValidationMessageFor(messageType.id, message);
        } else if (messageType.aceMessages) {
            messageCreator[rule] = clientValidationMessagesFor(messageType.id, message);
        } else if (messageType.aceGrowlMessages) {
            messageCreator[rule] = clientValidationGrowlMessagesFor(messageType.id, message, messageType.configuration);
        }
        ruleConfig['messages'] = messageCreator;

        var element = document.getElementById(id);
        if (customEvents && customEvents.length > 0) {
            var jqForm = ice.ace.jq(ice.ace.escapeClientId(form.id));
            //disable default event listeners per entire form (not possible to go finer grain with current API)
            disableDefaultEvents(jqForm.validate());
            triggerValidationOn(id, focusID, customEvents);
            element.customValidation = true;
        }

        ice.ace.jq(selector).rules('add', ruleConfig);
        element.immediate = immediate;
    };

    function triggerValidationOn(id, focusID, events) {
        var element = document.getElementById(id);
        var eventTypes = events.split(' ');
        for (var i = 0; i < eventTypes.length; i++) {
            var eventType = eventTypes[i];
            if (eventType) {
                var triggeringElement = focusID ? document.getElementById(focusID) : element;
                triggeringElement.addEventListener(eventType, function () {
                    var jqElement = ice.ace.jq(element);
                    var valid = jqElement.valid();
                    if (valid) {
                        cleanupMessages([element]);
                    }
                });
            }
        }
    }

    function disableDefaultEvents(validator) {
        var cfg = validator.settings;
        var callbacks = ['onsubmit', 'onfocusout', 'onclick', 'onkeyup'];
        for (var i = 0, l = callbacks.length; i < l; i++) {
            var callback = callbacks[i];
            var originalCallback = cfg[callback];
            cfg[callback] = function(element, event) {
                if (!element.customValidation) {
                    originalCallback.apply(validator, [element, event]);
                }
            };
        }
    }
})();
