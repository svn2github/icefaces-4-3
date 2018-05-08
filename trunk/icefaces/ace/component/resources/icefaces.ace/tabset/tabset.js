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

ice.ace.tabset = {
    devMode : false,
    logAll : false,  // Set to true to enable more debugging to be logged

    consoleLog : function(important, msg) {
        if (window.console && window.console.log) {
            if (ice.ace.tabset.devMode && (ice.ace.tabset.logAll || important)) {
                console.log(msg);
            }
        }
    },

    initialize:function(clientId, jsProps, jsfProps, bindYUI) {
       var initializeStartTime = new Date().getTime();
       ice.ace.tabset.consoleLog(false, 'tabSet.initialize');
	 
	   var Dom = YAHOO.util.Dom;
       var initElem = document.getElementById(clientId);
		var initWidth = ice.ace.jq(initElem).width();

       var tabview = new YAHOO.widget.TabView(clientId), cachedOldTabs = [], cachedNewTab = null;
       tabview.set('orientation', jsProps.orientation);

       //if tabset is client side, lets find out if the state is already stored.
       initElem.suppressTabChange = true;
       if (jsfProps.isClientSide) {
    	   if(ice.ace.clientState.has(clientId)){
               ice.ace.tabset.consoleLog(false, 'tabSet.initialize  clientSide  setActiveIndex from clientState: ' + ice.ace.clientState.get(clientId));
    		   tabview.set('activeIndex', ice.ace.clientState.get(clientId));
    	   }
    	   else {
               ice.ace.tabset.consoleLog(false, 'tabSet.initialize  clientSide  setActiveIndex from server: ' + jsfProps.selectedIndex);
    		   tabview.set('activeIndex', jsfProps.selectedIndex);
    	   }
       }
       else {
           if(!ice.ace.clientState.has(clientId)) {
               ice.ace.tabset.consoleLog(false, 'tabSet.initialize  serverSide  setActiveIndex from server: ' + jsfProps.selectedIndex);
               tabview.set('activeIndex', jsfProps.selectedIndex);
           }
       }
       initElem.suppressTabChange = null;

		if (jsProps.scrollableTabs) {
			ice.ace.tabset.setupScrollableTabs(clientId, initWidth);
			ice.onElementUpdate(clientId + '_nav', function() {alert(1);
				ice.ace.tabset.setupScrollableTabs(clientId, initWidth);
			});
		}
       
       var tabChange=function(event) {
            var tabChangeStartTime = new Date().getTime();
            ice.ace.tabset.consoleLog(false, 'tabSet.tabChange  ENTER');
            var rootElem = document.getElementById(clientId);
            if (rootElem.suppressTabChange) {
                ice.ace.tabset.consoleLog(false, 'tabSet.tabChange  EXIT suppressTabChange');
                return;
            }
            var context = ice.ace.getJSContext(clientId);
            var tabview = context.getComponent();
            var sJSFProps = context.getJSFProps();
            event.target = rootElem;
            var currentIndex = tabview.getTabIndex(event.newValue);
            if (currentIndex == null) {
                ice.ace.tabset.consoleLog(false, 'tabSet.tabChange  EXIT null currentIndex');
                return;
            }
            ice.ace.tabset.consoleLog(false, 'tabSet.tabChange  currentIndex: ' + currentIndex);
            var tabIndexInfo = clientId + '='+ currentIndex;

			// do automatic scrolling if tab is not fully visible
			if (jsProps.scrollableTabs) {
				var tab = ice.ace.jq(event.newValue._getLabelEl()).closest('li');
				var middleDiv = tab.closest('div');
				var root = ice.ace.jq(initElem);
				var leftArrow = root.find('.ui-tabs-scrollable-left').first();
				var rightArrow = root.find('.ui-tabs-scrollable-right').first();

				var checkScroll = function() {
					var width = middleDiv.width();
					var scrollLeft = middleDiv.get(0).scrollLeft;
					var scrollWidth = middleDiv.get(0).scrollWidth;

					if (scrollLeft == 0) {
						leftArrow.find('button').button('disable').removeClass('ui-state-focus ui-state-hover ui-state-active');
					} else {
						leftArrow.find('button').button('enable');
					}
					if ((width + scrollLeft) >= scrollWidth) {
						rightArrow.find('button').button('disable').removeClass('ui-state-focus ui-state-hover ui-state-active');
					} else {
						rightArrow.find('button').button('enable');
					}
				};

				var leftArrowWidth = leftArrow.outerWidth();
				var rightArrowPositionLeft = rightArrow.position().left;
				var middleDivWidth = middleDiv.outerWidth();
				var tabPositionLeft = tab.position().left;
				var tabWidth = tab.outerWidth();

				if (tabPositionLeft < leftArrowWidth) {
					// left arrow
					var delta = leftArrowWidth - tabPositionLeft;
					if (delta < 0) delta = (delta * -1) + leftArrowWidth;
					middleDiv.animate({scrollLeft: (middleDiv.get(0).scrollLeft - delta - 10) + 'px'}, checkScroll);
				} else if ((tabPositionLeft + tabWidth) > rightArrowPositionLeft) {
					// right arrow
					var delta = (tabPositionLeft + tabWidth) - rightArrowPositionLeft;
					middleDiv.animate({scrollLeft: (middleDiv.get(0).scrollLeft + delta + 10) + 'px'}, checkScroll);
				}
			}

            var doOnSuccess = function() {
                if (rootElem.suppressServerSideTransition) {
                    rootElem.suppressServerSideTransition = null;
                    return;
                }
                ice.ace.tabset.consoleLog(false, 'tabSet.tabChange.doOnSuccess  ENTER');

                // We don't want the yti value lingering, since it's only set
                // from user tab clicks and not from server overrides, but we
                // can't use a parameter since its the actual element used for
                // the submit.
                var targetElement = ice.ace.tabset.getTabIndexField(rootElem, false);
                ice.ace.tabset.consoleLog(false, 'tabSet.doOnSuccess  clear yti if exists: ' + (targetElement?'true':'false'));
                if(targetElement) {
                    targetElement.value = '';
                }

                // Ajax content transition. YUI content transition doesn't execute for server side cases
                // allowing our companonent to trigger content transition when the server call succeeds.
                if (event.oldValue) {
                    event.oldValue.set('contentVisible', false);
                    event.oldValue.set('active', false);
                } else if (cachedOldTabs.length > 0) {
                    // When using caching, event.oldValue is undefined in this function
                    // thus we use a reference to the old tab cached during the standard contentTransition.
                    for (var i = 0; i < cachedOldTabs.length; i++) {
                        var flipDisabled = cachedOldTabs[i].get('active') &&
                                cachedOldTabs[i].get('disabled');
                        if (flipDisabled) {
                            cachedOldTabs[i].set('disabled', false);
                        }
                        cachedOldTabs[i].set('contentVisible', false);
                        cachedOldTabs[i].set('active', false);
                        if (flipDisabled) {
                            cachedOldTabs[i].set('disabled', true);
                        }
                    }
                    cachedOldTabs = [];
                }

                if (event.newValue) {
                    var flipDisabled = !event.newValue.get('active') &&
                            event.newValue.get('disabled');
                    if (flipDisabled) {
                        event.newValue.set('disabled', false);
                    }
                    event.newValue.set('contentVisible', true);
                    event.newValue.set('active', true);
                    if (flipDisabled) {
                        event.newValue.set('disabled', true);
                    }
                }

                try {
                    document.getElementById(event.newValue.get('element').firstChild.id).focus();
                } catch(e) {}

                var contentParent = ice.ace.jq(tabview._contentParent);
				contentParent.css({opacity:1});
				var firstFocusable = contentParent.find(':focusable:visible:enabled:first');
				if (firstFocusable.size() > 0) {
					firstFocusable.off('keydown', ice.ace.tabset.focusActiveTab)
						.on('keydown', ice.ace.tabset.focusActiveTab);
				}
				ice.ace.tabset.resizeMaps(contentParent);
				ice.ace.tabset.resizeTables(contentParent);
				ice.ace.tabset.resizeSchedules(contentParent);
                cachedNewTab = null;
                ice.ace.tabset.consoleLog(false, 'tabSet.tabChange.doOnSuccess  EXIT');
            };
            var params = function(parameter) {
							//parameter('ice.focus', event.newValue.get('element').firstChild.id);
                            parameter('onevent', function(data) {
                                if (data.status == 'success' && event.newValue == cachedNewTab) {
                                    doOnSuccess();
                                }
                            });
                        };
            if (sJSFProps.isClientSide){
                ice.ace.tabset.consoleLog(false, 'tabSet.tabChange  clientSide  currentIndex: ' + currentIndex);
            	ice.ace.clientState.set(clientId, currentIndex);
                if (sJSFProps.behaviors) {
                    if (sJSFProps.behaviors.clientSideTabChange) {
                        var submitClientSideStartTime = new Date().getTime();
                        ice.ace.ab(sJSFProps.behaviors.clientSideTabChange);
                        ice.ace.tabset.consoleLog(true, "ace:tabSet - ID: " + clientId + " - submit CS - " + (new Date().getTime() - submitClientSideStartTime) + "ms");
                    }
                }
                var contentParent = ice.ace.jq(tabview._contentParent);
				contentParent.css({opacity:1});
				var firstFocusable = contentParent.find(':focusable:visible:enabled:first');
				if (firstFocusable.size() > 0) {
					firstFocusable.off('keydown', ice.ace.tabset.focusActiveTab)
						.on('keydown', ice.ace.tabset.focusActiveTab);
				}
				ice.ace.tabset.resizeMaps(contentParent);
				ice.ace.tabset.resizeTables(contentParent);
				ice.ace.tabset.resizeSchedules(contentParent);
            } else {
                var targetElement = ice.ace.tabset.getTabIndexField(rootElem, true);
                if(targetElement) {
                	targetElement.value = tabIndexInfo;
                }
                ice.ace.tabset.consoleLog(false, 'tabSet.tabChange  serverSide  event: ' + event);
                try {
                    // When multiple requests are sent before the first returns, only transition if
                    // the new tab matches the last recorded here.
                    cachedNewTab = event.newValue;
                    var haveBehaviour = false;
                    if (sJSFProps.behaviors) {
                        if (sJSFProps.behaviors.serverSideTabChange) {
                            haveBehaviour = true;

                            var elementId = targetElement.id;
                            //replace id with the id of tabset component, so the "execute" property can be set to tabset id
                            targetElement.id = clientId;
                            var otherParams = {};
                            var submitBehaviourStartTime = new Date().getTime();
							var ajaxArgs = ice.ace.extendAjaxArgs(
                                    sJSFProps.behaviors.serverSideTabChange,
                                {params: otherParams, execute: "@this", render: "@this", onsuccess: doOnSuccess});
							// if there's a proxy, then the 'source' will be its client id
							var proxy = document.getElementById(clientId + '_tsc');
							if (proxy) ajaxArgs.source = proxy;
                            ice.ace.ab(ajaxArgs);
                            //restore id
                            targetElement.id = elementId;
                            ice.ace.tabset.consoleLog(true, "ace:tabSet - ID: " + clientId + " - submit B - " + (new Date().getTime() - submitBehaviourStartTime) + "ms");
                        }
                    }
                    if (!haveBehaviour) {
                        var submitServerSideStartTime = new Date().getTime();
                        ice.setFocus('');
                        ice.submit(event, targetElement, params);
                        ice.ace.tabset.consoleLog(true, "ace:tabSet - ID: " + clientId + " - submit SS - " + (new Date().getTime() - submitServerSideStartTime) + "ms");
                    }
                } catch(e) {
                    ice.ace.tabset.consoleLog(true, 'tabSet.tabChange  exception: ' + e);
                }
            }//end if
            ice.ace.tabset.consoleLog(true, "ace:tabSet - ID: " + clientId + " - tabChange - " + (new Date().getTime() - tabChangeStartTime) + "ms");
            ice.ace.tabset.consoleLog(false, 'tabSet.tabChange  EXIT end');
       };//tabchange;
       
       //Check for aria support

       var onKeyDown = null;
       var Event = YAHOO.util.Event;
       //add aria + keyboard support
       if (jsfProps.aria) {
           var goNext = function(target) {
               var nextLi = Dom.getNextSibling(target);
               if (nextLi == null) {
                   goFirst(target);
               } else {
                   Dom.getFirstChild(nextLi).focus();
               }
           };
       
           var goPrevious= function(target) {
               var previousLi = Dom.getPreviousSibling(target);
               if (previousLi == null) {
                  goLast(target);
               } else {
                  Dom.getFirstChild(previousLi).focus();
               }
           };
           
           var goLast= function(target) {
               var lastLi = Dom.getLastChild(target.parentNode);  
               Dom.getFirstChild(lastLi).focus(); 
           };
           
           var goFirst= function(target) {
               var firstLi = Dom.getFirstChild(target.parentNode);
               Dom.getFirstChild(firstLi).focus();                             
           };
                   
           onKeyDown = function(event) {
                var target = Event.getTarget(event).parentNode;
                var charCode = Event.getCharCode(event);
                switch (charCode) {
                   case 37://Left
                   case 38://Up
                     initElem.keyEvent = true;
                     goPrevious(target);
                     break;
                     
                   case 39://Right
                   case 40://Down
                     initElem.keyEvent = true;
                     goNext(target);
                     break;                     
                    
                   case 36: //HOME
                     initElem.keyEvent = true;
                     goFirst(target);
                     break;                   
                     
                   case 35: //End  
                     initElem.keyEvent = true;
                     goLast(target);
                     break;    
                }
				setTimeout(function() { initElem.keyEvent = false; }, 100);
           };
       }
       var onKeyPress = function(event, index) {
            var context = ice.ace.getJSContext(clientId);
            var tabview = context.getComponent();
            var target = Event.getTarget(event).parentNode;
			if(ice.ace.util.isEventSourceInputElement(event)) {
				return true ;
			}
			//check for enter or space key
            var isEnter = Event.getCharCode(event) == 13 || 
					Event.getCharCode(event) == 32 ; 
            if (isEnter) {
               tabview.set('activeIndex', index);
			   event.cancelBubble = true;
			   event.preventDefault();
			   event.stopPropagation();
            }
       };
       
       var tabs = tabview.get('tabs');
       for (i=0; i<tabs.length; i++) {
           if (onKeyDown){//do it for aria only
              tabs[i].on('keydown', onKeyDown);
           }
           //support enter key regardless of keyboard or aria support 
           tabs[i].on('keypress', onKeyPress, i); 
       }
       if (!onKeyDown) {
           tabview.removeListener('keydown', tabview.DOMEventHandler);
       }

	tabview.contentTransition = function(newTab, oldTab) {
        // Server side handles its own content transition
        if (jsfProps.isClientSide) {
            if (newTab) {
                newTab.set('contentVisible', true);
                newTab.set('active', true);
            }
            if (oldTab) {
                oldTab.set('contentVisible', false);
                oldTab.set('active', false);
            }
            document.getElementById(newTab.get('element').firstChild.id).focus();
        } else {
            // Cache old tab provided in contentTransition during server side case
            // transition attempted following server side call is passed null reference to oldTab
            // thus oldTab will be cached here until use by the request success callback.
            // Keep a list of oldTabs for cases of multiple request being sent before the first returns
            cachedOldTabs.push(oldTab);
        }
		// effect
		if (jsfProps.isClientSide && jsProps.showEffect) {
			var content = newTab.get('contentEl').childNodes[0];
			if (jsProps.showEffect == 'fade') ice.ace.jq(content).hide();
			if (content) ice.ace.animation.run({node: content, name: jsProps.showEffect}, {mode: 'show'}, jsProps.showEffectLength);
		}
	}

       ice.onAfterUpdate(function(xmlContent, source) {
           var contentDiv = document.getElementById(clientId + 'cnt');
           if (contentDiv) {
               ice.ace.tabset.loadDeferredIframeContent(contentDiv);
           }
       });

	   //console.info('effect >>> '+ jsfProps.effect );
 
	   tabview.addListener('activeTabChange', tabChange);
        tabview.addListener('beforeActiveTabChange', function (e) {
            ice.ace.jq(tabview._contentParent).css({opacity: 0.4});
        });
       bindYUI(tabview);

		var firstFocusable = ice.ace.jq(tabview._contentParent).find(':focusable:visible:enabled:first');
		if (firstFocusable.size() > 0) {
			firstFocusable.off('keydown', ice.ace.tabset.focusActiveTab)
				.on('keydown', ice.ace.tabset.focusActiveTab);
		}
		ice.ace.tabset.resizeTables(initElem);
		ice.ace.tabset.resizeSchedules(initElem);

       ice.ace.tabset.consoleLog(true, "ace:tabSet - ID: " + clientId + " - initialize - " + (new Date().getTime() - initializeStartTime) + "ms");
   },
   
   //this function is responsible to provide an element that keeps tab index
   //only one field will be used per form element.
   getTabIndexField:function(tabset, createIt) {
	   //YAHOO.log("in getTabIndexField");
	   var _form = null;
	   try {
		   //see if the tabset is enclosed inside a form
	       _form = ice.ace.util.formOf(tabset);
	   } catch(e) {
		   //seems like tabset is not enclosed inside a form, now look for tabsetproxy component 
		   if (!_form) {
			   var tsc = document.getElementById(tabset.id + '_tsc');
			   if(tsc) {
				   try {
					   _form = ice.ace.util.formOf(tsc);
				   } catch(e) {
                       ice.ace.tabset.consoleLog(true, 'ERROR: The tabSetProxy must be enclosed inside a form element');
				   }
			   } else {
                   ice.ace.tabset.consoleLog(true, 'ERROR: If tabSet is not inside a form, then you must use tabSetProxy component');
			   }
		   }
	   }
	   //form element has been resolved by now
	   if (_form) {
		   var f = document.getElementById(_form.id + 'yti');
		   //if tabindex holder is not exist already, then create it lazily.
		   if (!f && createIt) {
			   f = ice.ace.tabset.createHiddenField(_form, _form.id + 'yti');
		   }
	       return f 
	   } else {
		   return null;   
	   }
   },
   
   createHiddenField:function(parent, id) {
	   var field = document.createElement('input'); 
	   field.setAttribute('type', 'hidden');
	   field.setAttribute('id', id);
	   field.setAttribute('name', 'yti');
       field.setAttribute('autocomplete', 'off');
	   parent.appendChild(field);
	   return field;
   },
 
   //delegate call to ice.yui.updateProperties(..)  with the reference of this lib
   updateProperties:function(clientId, jsProps, jsfProps, events) {
      ice.ace.tabset.devMode = jsfProps.devMode;
      var updatePropertiesStartTime = new Date().getTime();
       var lib = this;
           var updatePropertiesDOMReadyStartTime = new Date().getTime();
           YAHOO.widget.Tab.prototype.ACTIVE_CLASSNAME = 'ui-state-active';
           YAHOO.widget.Tab.prototype.HIDDEN_CLASSNAME = 'ui-tabs-hide';
           YAHOO.widget.Tab.prototype.DISABLED_CLASSNAME = 'ui-state-disabled';

       // Call handlePotentialTabChanges if we're NOT going to initialise
       var oldJSFProps = null;
       var context = ice.ace.getJSContext(clientId);
       if (context) {
           oldJSFProps = context.getJSFProps();
       }
       var requiresInitialise = ice.ace.tabset.handlePotentialTabChanges(
               clientId, oldJSFProps, jsfProps);
       ice.ace.tabset.consoleLog(false, 'tabSet.updateProperties  requiresInitialise: ' + requiresInitialise);
       // If the tab info changed sufficiently to require an initialise
       if (context) {
           if (requiresInitialise) {
               var rootToReInit = document.getElementById(clientId);
               if (rootToReInit) {
                   rootToReInit['JSContext'] = null;
                   rootToReInit.removeAttribute('JSContext');
               }
               JSContext[clientId] = null;
           }
           else {
               var tabviewObj = context.getComponent();

               var index = jsfProps.selectedIndex;
               var objIndex = tabviewObj.get('activeIndex');

               var existingTabs = tabviewObj.get('tabs');
               existingTabs[index].set('disabled', false);

               ice.ace.tabset.consoleLog(false, 'tabSet.updateProperties  object index: ' + objIndex + '  server index: ' + index);
               if (index != objIndex) {
                   existingTabs[objIndex].set('disabled', false);

                   var rootElem = document.getElementById(clientId);
                   rootElem.suppressTabChange = true;
                   rootElem.suppressServerSideTransition = true;
                   if (!jsfProps.isClientSide){
                       //ice.ace.tabset.consoleLog(false, 'updateProperties: index mismatch BEFORE set activeIndex');
                       tabviewObj.set('activeIndex', index);
                       //ice.ace.tabset.consoleLog(false, 'updateProperties: index mismatch AFTER set activeIndex');

                       var tabs = tabviewObj.get('tabs');
                       var countIndex;
                       for(countIndex = 0; countIndex < tabs.length; countIndex++) {
                           var isCurr = (countIndex == index);
                           tabs[countIndex].set('contentVisible', isCurr);
                           tabs[countIndex].set('active', isCurr);
                           if (isCurr) {
                               try {
                                   document.getElementById(tabs[countIndex].get('element').firstChild.id).focus();
                               } catch(e) {}
                           }
                       }

                       //ice.ace.tabset.consoleLog(false, 'updateProperties: index mismatch BETWEEN set contentVisible/active AND opacity');
                   } else {
                       //ice.ace.tabset.consoleLog(false, 'updateProperties: index mismatch BEFORE selectTab');
                       ice.ace.clientState.set(clientId, index);
                       tabviewObj.selectTab(index);
                       //ice.ace.tabset.consoleLog(false, 'updateProperties: index mismatch AFTER selectTab');
                   }
                   ice.ace.jq(tabviewObj._contentParent).css({opacity:1});
                   rootElem.suppressTabChange = null;
               } else {
                   var rootElem = document.getElementById(clientId);
                   rootElem.suppressServerSideTransition = null;
               }
               if (jsProps.showEffect && jsProps.activeTabChangeRequest) {
                   var node = tabviewObj.getTab(index).get('contentEl').childNodes[0];
                   if (node) {
                       var cnt = ice.ace.jq(tabviewObj._contentParent);
                       var jqNode = ice.ace.jq(node);
                       if (jsProps.showEffect == 'fade') {
                           jqNode.hide();
                       }

                       var previousWidth = node.style.width;
                       //make the width equal to the width that the DIV will have once displayed inside the tab content area
                       jqNode.parent().removeClass('ui-tabs-hide');
                       ice.ace.animation.run({node: node, name: jsProps.showEffect}, {mode: 'show'}, jsProps.showEffectLength);
                       node.style.width = previousWidth;
                   }
               }
           }
       }

       ice.ace.getInstance(clientId, function(yuiComp) {
           var newDisabledTabs = jsfProps['disabledTabs'];
           var tabs = yuiComp.get('tabs');
           for (var i = 0; i < tabs.length; i++) {
               tabs[i].set('disabled', ice.ace.jq.inArray(i, newDisabledTabs) > -1);
           }
       }, lib, jsProps, jsfProps);

       ice.ace.updateProperties(clientId, jsProps, jsfProps, events, lib);

       ice.ace.tabset.consoleLog(true, "ace:tabSet - ID: " + clientId + " - updateProperties DR - " + (new Date().getTime() - updatePropertiesDOMReadyStartTime) + "ms");
       ice.ace.tabset.consoleLog(true, "ace:tabSet - ID: " + clientId + " - updateProperties - " + (new Date().getTime() - updatePropertiesStartTime) + "ms");
   },
 
   //delegate call to ice.yui.getInstance(..) with the reference of this lib 
   getInstance:function(clientId, callback) {
       ice.ace.getInstance(clientId, callback, this);
   },

    // Used by updateProperties(-) when we're already initialised
    // Updates the dom, re-parents the content, and triggers a new initialise
    handlePotentialTabChanges : function(clientId, oldJSFProps, newJSFProps) {
        var oldSafeIds = ( (!oldJSFProps) ? null : oldJSFProps.safeIds );
        var newSafeIds = ( (!newJSFProps) ? null : newJSFProps.safeIds );
        if (!oldSafeIds) {
            oldSafeIds = new Array();
        }
        if (!newSafeIds) {
            newSafeIds = new Array();
        }

        var ret = false;

        if (ice.ace.util.arraysEqual(oldSafeIds, newSafeIds)) {
            // We can have a scenario where the [client-side] tabSet is
            // completely updated by the dom-diff, and nothing has changed
            // with the tabs, but now the tab content is stored in the safe,
            // but the old safeIds list is not null, and is exactly equal to
            // the new safeIds list, so we don't know to re-parent the content
            // into the content area. So we'll need to scan through the new
            // safeIds list and see if the content is there, and handle it.
            var contentDiv = document.getElementById(clientId + 'cnt');
            if (contentDiv && !contentDiv.hasChildNodes()) {
                var index;
                for (index = 0; index < newSafeIds.length; index++) {
                    var safeDiv = document.getElementById(newSafeIds[index]);
                    if (safeDiv && safeDiv.hasChildNodes()) {
                        var isSelectedTab = (newJSFProps.selectedIndex == index);
                        var appendedDiv = ice.ace.tabset.createDiv(!isSelectedTab);

                        // Reparent new safe-house entry into content area
                        ice.ace.tabset.moveSafeToContent(safeDiv, appendedDiv);
                        contentDiv.appendChild(appendedDiv);

                        ret = true;
                    }
                }
            }

            return ret;
        }

        var appendNewContent = new Array();
        // [ [oldContent, newIndex where it should go or -1 for delete], � ]
        var moveOldContent = new Array();
        var skipNewIndexes = new Array();
        var oldSafeIndex = 0;
        var newSafeIndex = 0;

        //var contentNode = Y.one('#' + clientId + 'cnt');//' .yui-content'); // Y.DOM.byId(clientId + 'cnt');
        var contentDiv = document.getElementById(clientId + 'cnt');//Y.Node.getDOMNode(contentNode);

        while (true) {
            // (0) Detect if done
            if (oldSafeIndex >= oldSafeIds.length && newSafeIndex >= newSafeIds.length) {
                break;
            }

            // (3.5 skip) Skip past newSafeIndex if in skip list
            if (ice.ace.util.arrayIndexOf(skipNewIndexes, newSafeIndex, 0) >= 0) {
                newSafeIndex++;
                continue;
            }

            // (1) Detect if tab on end was deleted
            // If past end of new list but more in old list
            if (newSafeIndex >= newSafeIds.length && oldSafeIndex < oldSafeIds.length) {
                // Everything left in old list has been deleted. Just delete one, then loop to remove any more
                moveOldContent.push( [oldSafeIndex, -1] );
                oldSafeIndex++;
                ret = true;
                continue;
            }

            // (2) Detect if appended to end
            // If past end of old list, but at least another in new list
            if (oldSafeIndex >= oldSafeIds.length && newSafeIndex < newSafeIds.length) {
                // Current entry in new is appended
                // Create new div and append it to content area.
                var isSelectedTab = (newJSFProps.selectedIndex == newSafeIndex);
                var appendedDiv = ice.ace.tabset.createDiv(!isSelectedTab);

                // Reparent new safe-house entry into content area
                ice.ace.tabset.moveSafeIdToContent(newSafeIds[newSafeIndex], appendedDiv);
                contentDiv.appendChild(appendedDiv);

                // Increment newSafeIndex, but not oldSafeIndex, and continue looping
                newSafeIndex++;
                ret = true;
                continue;
            }

            // (3) Detect if non-end deleted, inserted, visited, moved
            var oldsid = oldSafeIds[oldSafeIndex];
            var newsid = newSafeIds[newSafeIndex];
            if (oldsid !== newsid) {
                // (3.5) Detect if old moved. Also covers unvisited inserts,
                // which inadvertently move the pre-existing sections, which
                // we'll try to avoid, since moving a section involves
                // refreshing iframe content, which we need to avoid
                // oldsid not null and in new list
                // ?? Search from newSafeIndex onwards or beginning?? Just use beginning
                var foundInNewIndex;
                if (oldsid !== null &&
                    (foundInNewIndex = ice.ace.util.arrayIndexOf(newSafeIds, oldsid, 0)) >= 0)
                {
                    // Detect if newsid is unvisited/visiting insert
                    var foundInOldIndex;
                    if (newsid === null ||
                        ( ((foundInOldIndex = ice.ace.util.arrayIndexOf(oldSafeIds, newsid, 0)) < 0) &&
                          document.getElementById(newsid).hasChildNodes()
                        )) {
                        var isSelectedTab = (newJSFProps.selectedIndex == newSafeIndex);
                        var newDiv = ice.ace.tabset.createDiv(!isSelectedTab);
                        var newIndex = contentDiv.childNodes.length;

                        // Reparent new safe-house entry into content area
                        // if newsid is not null
                        ice.ace.tabset.moveSafeIdToContent(newsid, newDiv);
                        contentDiv.appendChild(newDiv);

                        // Mark the new content div to be moved to it's proper insertion point
                        appendNewContent.push( [newIndex, newSafeIndex] );

                        // Increment newSafeIndex, but not oldSafeIndex, and continue looping
                        newSafeIndex++;
                        ret = true;
                        continue;
                    }

                    // Mark the location in new list as something to skip over
                    skipNewIndexes.push(foundInNewIndex);

                    // Save the reference to the old content, and where it should end up
                    moveOldContent.push( [oldSafeIndex, foundInNewIndex] );

                    // Increment oldSafeIndex, but not newSafeIndex, and continue looping
                    oldSafeIndex++;
                    ret = true;
                    continue;
                }

                // Unvisited tab. old goes to null and isn't in new list anymore
                if (oldsid !== null &&
                         newsid === null &&
                         (foundInNewIndex = ice.ace.util.arrayIndexOf(newSafeIds, oldsid, 0)) < 0) {
                    // Clear out / un-cache that tab's contents
                    var unvisitedDiv = contentDiv.childNodes[oldSafeIndex];
                    while (unvisitedDiv.hasChildNodes()) {
                        unvisitedDiv.removeChild(unvisitedDiv.firstChild);
                    }

                    oldSafeIndex++;
                    newSafeIndex++;
                    continue;
                }

                // Have to detect which of non-end delete, insert, or visit

                // (4) Detect if non-end deleted
                // If newsid is null, it's a delete (oldsid is non-null since oldsid !== newsid)
                if (newsid === null) {
                    moveOldContent.push( [oldSafeIndex, -1] );
                    oldSafeIndex++;
                    ret = true;
                    continue;
                }

                // (5) Detect if non-end deleted or instead insert/visit
                // If new safe entry has no child markup, then is non-end delete
                var safeDiv = document.getElementById(newsid);
                if (!safeDiv.hasChildNodes()) {
                    moveOldContent.push( [oldSafeIndex, -1] );
                    oldSafeIndex++;
                    ret = true;
                    continue;
                }
                // If new safe entry has child markup, then is either insert or visit
                else {// safeDiv.hasChildNodes()
                    // If changed from null to non-null means visited
                    if (oldsid === null && newsid !== null) {
                        // Get the content area
                        var visitedDiv = contentDiv.childNodes[oldSafeIndex];

                        // Reparent new safe-house entry into content area
                        ice.ace.tabset.moveSafeToContent(safeDiv, visitedDiv);

                        oldSafeIndex++;
                        newSafeIndex++;
                        continue;
                    }

                    // Inserted
                    // We don't want to alter the content area indexing as we
                    // go, so inserting right away is a no-go. We also don't
                    // want to special case this. So, we'll treat this as an
                    // append, where we'll subsequently move it to the
                    // insertion point
                    // Create new div and append it to content area.
                    var isSelectedTab = (newJSFProps.selectedIndex == newSafeIndex);
                    var newDiv = ice.ace.tabset.createDiv(!isSelectedTab);
                    var newIndex = contentDiv.childNodes.length;

                    // Reparent new safe-house entry into content area
                    ice.ace.tabset.moveSafeToContent(safeDiv, newDiv);
                    contentDiv.appendChild(newDiv);

                    // Mark the new content div to be moved to it's proper insertion point
                    appendNewContent.push( [newIndex, newSafeIndex] );

                    // Increment newSafeIndex, but not oldSafeIndex, and continue looping
                    newSafeIndex++;
                    ret = true;
                    continue;
                }
            }
            // If they're the same, move on to next. Increment both
            else { // oldsid === newsid
                oldSafeIndex++;
                newSafeIndex++;
                continue;
            }
        }

        // moveOldContent assumes that as its index increases, the old content
        // indexes increase as well. But when we append, we're putting larger
        // old content indexes early towards the beginning of moveOldContent.
        // So we'll buffer them in appendNewContent, and then use that to
        // place them toward the end of moveOldContent.
        var index;
        for (index = 0; index < appendNewContent.length; index++) {
            moveOldContent.push(appendNewContent[index]);
        }

        // moveOldContent, but our algorithm assumes that the the old indexes increase
        // Iterate through moveOldContent in reverse, removing the divs from
        // the content area, and setting the div into moveOldContent where
        // the index had been
        for (index = moveOldContent.length - 1; index >= 0; index--) {
            var removeIndex = moveOldContent[index][0];
            var removeDiv = contentDiv.childNodes[removeIndex];
            moveOldContent[index][0] = removeDiv;
            contentDiv.removeChild(removeDiv);
        }
        // Then iterate through that list in the original forward sequence,
        // inserting the divs into their designated positions, unless the
        // insert index is -1, in which case discard them.
        for (index = 0; index < moveOldContent.length; index++) {
            var fromTo = moveOldContent[index];
            var insertDiv = fromTo[0];
            var toIndex = fromTo[1];
            if (toIndex >= 0) {
                ice.ace.util.insertElementAtIndex(contentDiv, insertDiv, toIndex);
            }
        }

        return ret;
    },

    createDiv : function(preStyleHidden) {
        var theDiv = document.createElement('div');
        theDiv.style.height = '100%';
        if (preStyleHidden) {
            ////YAHOO.util.Dom.addClass(theDiv, YAHOO.widget.Tab.prototype.HIDDEN_CLASSNAME);
            // Y.YUI2.util.Dom.hasClass(theDiv, 'yui-hidden');
            // Y.YUI2.util.Dom.removeClass(theDiv, 'yui-hidden');
        }
        return theDiv;
    },

    moveSafeIdToContent : function(safeId, tabContentDiv) {
        if (safeId) {
            var safeDiv = document.getElementById(safeId);
            ice.ace.tabset.moveSafeToContent(safeDiv, tabContentDiv);
        }
    },

    moveSafeToContent : function(safeDiv, tabContentDiv) {
        if (safeDiv.hasChildNodes()) {
            // Clean out tabContentDiv, and put the div child of safeDiv into tabContentDiv
            while (tabContentDiv.hasChildNodes()) {
                tabContentDiv.removeChild(tabContentDiv.firstChild);
            }
            var contentsToMove = safeDiv.firstChild;
            safeDiv.removeChild(contentsToMove);
            tabContentDiv.appendChild(contentsToMove);
            ice.ace.tabset.loadDeferredIframeContent(contentsToMove);
        }
    },

    loadDeferredIframeContent : function(element) {
        var key = 'org.icefaces.ace.component.tabset.deferred_src';
        var iframes = element.getElementsByTagName('iframe');
        for (var i = 0; i < iframes.length; i++) {
            var ifr = iframes[i];
            var dsrc = ifr.attributes[key];
            if (dsrc) {
                ifr.src = dsrc.value;
                ifr.removeAttribute(key);
            }
        }
    },

	resizeMaps : function(root) {
		var maps = root.find('.ice-ace-gmap');
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
	},

	resizeSchedules : function(root) {
		var schedules = ice.ace.jq(root).find('.ice-ace-schedule');
		setTimeout(function(){
			schedules.each(function(){
				var id = this.id;
				if (id) {
					var schedule = ice.ace.instance(id);
					if (schedule) {
						schedule.render();
					}
				}
			});
		}, 1);
	},

	clearSelectedIndexState: function(clientId) {
		var rootElem = document.getElementById(clientId);
		if (rootElem) {
			var targetElement = ice.ace.tabset.getTabIndexField(rootElem, false);
			if (targetElement) {
				targetElement.value = '';
			}
		}
	},

	focusActiveTab: function(e) {
		if (e.shiftKey) {
			var keyCode = e.keyCode || e.which; 
			if (keyCode == 9) {
				e.preventDefault();
				var parents = ice.ace.jq(e.target).parents('.yui-navset');
				if (parents.size() > 0) {
					ice.ace.jq(parents.get(0)).find(' > ul > li.ui-state-active > div').focus();
				}
			} 
		}
	},

	resizeTables: function(root) {
		var tables = ice.ace.jq(root).find('.ui-datatable');
		tables.each(function(){
			var id = this.id;
			if (id) {
				var table = ice.ace.instance(id);
				if (table && table.cfg.paginator) table.resizePaginator();;
			}
		});
	},

	setupScrollableTabs: function(clientId, initWidth) {
		var root = ice.ace.jq(ice.ace.escapeClientId(clientId));
		var container = root.find('.ui-tabs-scrollable');
		container.find('ul').css('white-space', 'nowrap');
		var middleDiv = container.children('div');
		var leftArrow = root.find('.ui-tabs-scrollable-left').first();
		var rightArrow = root.find('.ui-tabs-scrollable-right').first();

		var lastTab = container.find('li').last();
		if (middleDiv.get(0).scrollLeft > 0
			|| (lastTab.position().left + lastTab.width()) > (initWidth-20)) {

			var checkScroll = function() {
				var width = middleDiv.width();
				var scrollLeft = middleDiv.get(0).scrollLeft;
				var scrollWidth = middleDiv.get(0).scrollWidth;

				if (scrollLeft == 0) {
					leftArrow.find('button').button('disable').removeClass('ui-state-focus ui-state-hover ui-state-active');
				} else {
					leftArrow.find('button').button('enable');
				}
				if ((width + scrollLeft) >= scrollWidth) {
					rightArrow.find('button').button('disable').removeClass('ui-state-focus ui-state-hover ui-state-active');
				} else {
					rightArrow.find('button').button('enable');
				}
			};
			leftArrow.html('<button><i class="fa fa-arrow-left"></i></button>');
			leftArrow.find('button').button().click(function() {middleDiv.animate({
				scrollLeft: (middleDiv.get(0).scrollLeft - 200) + 'px'}, checkScroll); return false;
			});
			rightArrow.html('<button><i class="fa fa-arrow-right"></i></button>');
			rightArrow.find('button').button().click(function() {middleDiv.animate({
				scrollLeft: (middleDiv.get(0).scrollLeft + 200) + 'px'}, checkScroll); return false;
			});
			middleDiv.css('margin-left', leftArrow.width());
			middleDiv.css('margin-right', rightArrow.width());
			middleDiv.width(initWidth - leftArrow.width() - rightArrow.width());
			checkScroll();
		} else {
			container.find('ul').css('white-space', '');
			leftArrow.html('');
			rightArrow.html('');
			middleDiv.css('margin-left', '');
			middleDiv.css('margin-right', '');
			middleDiv.width('');
		}
	}
};

