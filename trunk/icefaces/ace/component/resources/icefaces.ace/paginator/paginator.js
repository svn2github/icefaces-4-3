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

ice.ace.DataTable.Paginator = function(table) {
    var labels = {} ;
    var cfg = table.cfg.paginator,
        container = ice.ace.jq(table.jqId + ' > .ui-paginator'),
        template = cfg.template || "{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink}",
        pageReportTemplate = cfg.pageReportTemplate || '\({currentPage} of {totalPages}\)',
        activeIndex = cfg.initialPage,
    // Maximum number of pages given total row count, if rowsPerPage zero set to 1.
        max = cfg.rowsPerPage == 0 || cfg.totalRecords < cfg.rowsPerPage ? 1 : Math.ceil(cfg.totalRecords / cfg.rowsPerPage);
        labels.first = cfg.firstLbl,
        labels.last = cfg.lastLbl,
        labels.next = cfg.nextLbl,
        labels.prev = cfg.prevLbl,
        labels.rewind = cfg.rewindLbl,
        labels.forward = cfg.forwardLbl,
        pagesToSkip = cfg.pagesToSkip > 0 ? cfg.pagesToSkip : 3,
    this.container = container;

    container.keyboardPagination = function(e) {
		var target = ice.ace.jq(e.target);
		if (table.cfg.filterEvent) {
			if (target.hasClass('ui-column-filter')) return true;
		}
		if (target.is('input,textarea,select,button,.ui-slider-handle')) return true;

        var keycode = e.which;
        //page down or right arrow key
        if (keycode == 34 || keycode == 39) {
            e.preventDefault();
			if (window['keyboardPaginationInProgress' + table.id]) return;
            if (activeIndex < max) {
                activeIndex++;
                submit({isKeyRequest: true});
            }
        }
        //page up or left arrow key
        if (keycode == 33 || keycode == 37) {
            e.preventDefault();
			if (window['keyboardPaginationInProgress' + table.id]) return;
            if (activeIndex > 1) {
                activeIndex--;
                submit({isKeyRequest: true});
            }
        }
        //home key
        if (keycode == 36) {
            e.preventDefault();
			if (window['keyboardPaginationInProgress' + table.id]) return;
            activeIndex = 1;
            submit({isKeyRequest: true});
        }
        //end key
        if (keycode == 35) {
            e.preventDefault();
			if (window['keyboardPaginationInProgress' + table.id]) return;
            activeIndex = max;
            submit({isKeyRequest: true});
        }
    };

	if (table.cfg.scrollable)
		this.tableContainer = ice.ace.jq(table.jqId + ' .ui-paginator');
	else
		this.tableContainer = ice.ace.jq(table.jqId);
    this.tableContainer.on('keydown', container.keyboardPagination);

    function initPageMarkup() {
        function getTemplateControlMarkup(keyword,specificContainer) {
            var markup = '';
            var currentPageButtonID = specificContainer.attr('id') + '_current_page';

            if (keyword == 'currentPageReport') {
				var totalRecords = cfg.totalRecords;
				var rowsPerPage = cfg.rowsPerPage;
				var startRecord = rowsPerPage * (activeIndex - 1) + 1;
				var endRecord = rowsPerPage * activeIndex;
                markup = '<span class="ui-paginator-current">';
                markup += pageReportTemplate.replace(new RegExp('({currentPage})', 'gi'), activeIndex).replace(new RegExp('({totalPages})', 'gi'), max).replace(new RegExp('({totalRecords})', 'gi'), totalRecords).replace(new RegExp('({startRecord})', 'gi'), startRecord).replace(new RegExp('({endRecord})', 'gi'), endRecord);
                markup += '</span>';
            }
            else if (keyword == 'firstPageLink') {
                var className = 'ui-paginator-first ui-state-default ui-corner-all';
				var buttonId = specificContainer.attr('id') + '_firstPageLink';
                if (activeIndex == 1) className += ' ui-state-disabled';
                markup = '<a href="#" id="'+buttonId+'" class="'+className+'" onclick="ice.setFocus(\'' + buttonId + '\');" onkeydown="var e = event || window.event; if (e.keyCode == 32 || e.keyCode == 13) { this.click();return false; }" tabindex="0" title="'+labels.first+'" aria-label="'+labels.first+'" style="vertical-align:middle;"><span class="ui-icon ui-icon-seek-first"></span></a>';
            }
            else if (keyword == 'lastPageLink') {
                var className = 'ui-paginator-last ui-state-default ui-corner-all';
				var buttonId = specificContainer.attr('id') + '_lastPageLink';
                if (activeIndex == max) className += ' ui-state-disabled';
                markup = '<a href="#" id="'+buttonId+'" class="'+className+'" onclick="ice.setFocus(\'' + buttonId + '\');" onkeydown="var e = event || window.event; if (e.keyCode == 32 || e.keyCode == 13) { this.click();return false; }" tabindex="0" title="'+labels.last+'" aria-label="'+labels.last+'" style="vertical-align:middle;"><span class="ui-icon ui-icon-seek-end"></span></a>';
            }
            else if (keyword == 'previousPageLink') {
                var className = 'ui-paginator-previous ui-state-default ui-corner-all';
				var buttonId = specificContainer.attr('id') + '_previousPageLink';
                if (activeIndex == 1) className += ' ui-state-disabled';
                markup = '<a href="#" id="'+buttonId+'" class="'+className+'" onclick="ice.setFocus(\'' + buttonId + '\');" onkeydown="var e = event || window.event; if (e.keyCode == 32 || e.keyCode == 13) { this.click();return false; }" tabindex="0" title="'+labels.prev+'" aria-label="'+labels.prev+'" style="vertical-align:middle;"><span class="ui-icon ui-icon-triangle-1-w"></span></a>';
            }
            else if (keyword == 'nextPageLink') {
                var className = 'ui-paginator-next ui-state-default ui-corner-all';
				var buttonId = specificContainer.attr('id') + '_nextPageLink';
                if (activeIndex == max) className += ' ui-state-disabled';
                markup = '<a href="#" id="'+buttonId+'" class="'+className+'" onclick="ice.setFocus(\'' + buttonId + '\');" onkeydown="var e = event || window.event; if (e.keyCode == 32 || e.keyCode == 13) { this.click();return false; }" tabindex="0" title="'+labels.next+'" aria-label="'+labels.next+'" style="vertical-align:middle;"><span class="ui-icon ui-icon-triangle-1-e"></span></a>';
            }
            else if (keyword == 'fastRewind') {
                var className = 'ui-paginator-rewind ui-state-default ui-corner-all';
				var buttonId = specificContainer.attr('id') + '_fastRewind';
                if (activeIndex == 1) className += ' ui-state-disabled';
                markup = '<a href="#" id="'+buttonId+'" class="'+className+'" onclick="ice.setFocus(\'' + buttonId + '\');" onkeydown="var e = event || window.event; if (e.keyCode == 32 || e.keyCode == 13) { this.click();return false; }" tabindex="0" title="'+labels.rewind+'" aria-label="'+labels.rewind+'" style="vertical-align:middle;"><span class="ui-icon ui-icon-seek-prev"></span></a>';
            }
            else if (keyword == 'fastForward') {
                var className = 'ui-paginator-forward ui-state-default ui-corner-all';
				var buttonId = specificContainer.attr('id') + '_fastForward';
                if (activeIndex == max) className += ' ui-state-disabled';
                markup = '<a href="#" id="'+buttonId+'" class="'+className+'" onclick="ice.setFocus(\'' + buttonId + '\');" onkeydown="var e = event || window.event; if (e.keyCode == 32 || e.keyCode == 13) { this.click();return false; }" tabindex="0" title="'+labels.forward+'" aria-label="'+labels.forward+'" style="vertical-align:middle;"><span class="ui-icon ui-icon-seek-next"></span></a>';
            }
            else if (keyword == 'rowsPerPageDropdown' && cfg.rowsPerPageOptions) {
                markup = '<select class="ui-paginator-rpp-options" title="Rows per page" id="' + specificContainer.attr('id') + keyword + '">';

                for (var i = 0; i < cfg.rowsPerPageOptions.length; i++) {
                    var value = cfg.rowsPerPageOptions[i],
                        label = value;

                    if (value.text) {
                        value = cfg.rowsPerPageOptions[i].value;
                        label = cfg.rowsPerPageOptions[i].text;
                    }

                    var selected = value == cfg.rowsPerPage ? 'selected' : '';
                    markup += '<option '+selected+' value="'+value+'">'+label+'</option>';
                }
                markup += '</select>';
            }
            else if (keyword == 'pageLinks') {
                markup = '<span class="ui-paginator-pages">';

                // Adjust start page forward if more pages exist than page buttons
                var startPage = activeIndex - (cfg.pageLinks / 2) > 1 && cfg.pageLinks < max ? activeIndex - Math.floor(cfg.pageLinks / 2) : 1;
                var extraLinks = activeIndex + Math.round(cfg.pageLinks / 2) - max - 1;

                // Adjust start page backward if we're at a current position where adjusting buttons indexes would point to out of range pages
                if (extraLinks > 0 && cfg.pageLinks < max)
                    startPage -= extraLinks;

                for (var i = startPage; i <= (cfg.pageLinks + startPage - 1) && ((i-1) * cfg.rowsPerPage < cfg.totalRecords); i++) {
                    if (i == activeIndex) {
						var accesskey = cfg.accesskey ? 'accesskey="' + cfg.accesskey + '"' : '';
                        markup += '<a  href="#" onclick="this.focus();" class="ui-paginator-page ui-state-default ui-corner-all ui-paginator-current-page ui-state-active" style="cursor: default;" id="' + currentPageButtonID + '" onkeydown="var e = event || window.event; if (e.keyCode == 32 || e.keyCode == 13) { this.click();return false; }" tabindex="0" ' + accesskey + '>'+i+'</a>';
                    }
                    else
                        markup += '<a href="#" class="ui-paginator-page ui-state-default ui-corner-all" onclick="ice.setFocus(\'' + currentPageButtonID + '\');" onkeydown="var e = event || window.event; if (e.keyCode == 32 || e.keyCode == 13) { this.click();return false; }" tabindex="0">'+i+'</a>';

                    // Only render a single page when non-positive integer is given for row count
                    if (cfg.rowsPerPage < 1) break;
                }

                markup += '</span>';
            }

            return markup;
        }

        function encodePaginatorTemplate() {
            var keywords = ['currentPageReport', 'firstPageLink', 'lastPageLink',
                    'previousPageLink', 'nextPageLink', 'jumpToPageDropdown',
                    'fastRewind', 'fastForward',
                    'pageLinks', 'rowsPerPageDropdown'],
                t = template;

			// top
			var containerTop = ice.ace.jq(table.jqId + ' > .ui-paginator-top');
            for (var i = 0; i < keywords.length; i++) {
                t = t.replace(
                    new RegExp('({'+keywords[i]+'})', 'gi'),
                    getTemplateControlMarkup(keywords[i], containerTop)
                );
            }
            containerTop.html(t);

			// bottom
			t = template;
			var containerBottom = ice.ace.jq(table.jqId + ' > .ui-paginator-bottom');
            for (var i = 0; i < keywords.length; i++) {
                t = t.replace(
                    new RegExp('({'+keywords[i]+'})', 'gi'),
                    getTemplateControlMarkup(keywords[i], containerBottom)
                );
            }
            containerBottom.html(t);
        }

        encodePaginatorTemplate();
    }

    function initControlEvents() {
        function addHoverEvents(i, control) {
            control = ice.ace.jq(control);
            if (control.hasClass('ui-paginator-pages')) control.children().each(addHoverEvents);
            else {
                control.on('mouseenter', function(e) {
                    ice.ace.jq(this).addClass('ui-state-hover');
                });

                control.on('mouseleave', function(e) {
                    ice.ace.jq(this).removeClass('ui-state-hover');
                });
            }
        }

        function addClickEvents(i, control) {
            function clickListener(e) {
                e.preventDefault();
                var control = ice.ace.jq(this);

                if (control.hasClass('ui-paginator-page'))
                    pageLinkClick(control);
                else if (control.hasClass('ui-paginator-first')
                    || control.hasClass('ui-paginator-next')
                    || control.hasClass('ui-paginator-last')
                    || control.hasClass('ui-paginator-previous')
                    || control.hasClass('ui-paginator-rewind')
                    || control.hasClass('ui-paginator-forward'))
                    directionLinkClick(control);
                else alert(control[0].className);
            }

            function rowsPerPageChangeListener(e) {
                e.preventDefault();
                cfg.rowsPerPage = parseInt(e.currentTarget.children[e.currentTarget.selectedIndex].value);
                submit({ rowChangeEvent : true});
            }

            function pageLinkClick(control) {
                var oldIndex = activeIndex;
                activeIndex = parseInt(control.html());

                if (oldIndex != activeIndex) submit();
            }

            function directionLinkClick(control) {
                var oldIndex = activeIndex;

                if (control.hasClass('ui-paginator-first'))
                    activeIndex = 1;
                if (control.hasClass('ui-paginator-previous') && activeIndex > 1)
                    activeIndex--;
                if (control.hasClass('ui-paginator-next') && activeIndex < max)
                    activeIndex++;
                if (control.hasClass('ui-paginator-last'))
                    activeIndex = max;
                if (control.hasClass('ui-paginator-rewind') && activeIndex > 1) {
                    activeIndex = activeIndex - pagesToSkip;
                    activeIndex = activeIndex < 1 ? 1 : activeIndex;
                }
                if (control.hasClass('ui-paginator-forward') && activeIndex < max) {
                    activeIndex = activeIndex + pagesToSkip;
                    activeIndex = activeIndex > max ? max : activeIndex;
                }

                if (oldIndex != activeIndex) submit();
            }

            control = ice.ace.jq(control);
            if (control.hasClass('ui-paginator-pages')) control.children().each(addClickEvents);
            else {
                if (control.hasClass('ui-paginator-rpp-options'))
                    control.on('change', rowsPerPageChangeListener);
                else
                    control.on('click', clickListener);
            }
        }

        container.children().filter(':not(.ui-paginator-current, .ui-paginator-rpp-options)').each(addHoverEvents);
        container.children().filter(':not(.ui-paginator-current)').each(addClickEvents);
    }

    function initDisabledStyling() {

    }

    function getState() {
        return {
            rowsPerPage : cfg.rowsPerPage,
            page : activeIndex
        };
    }

    function submit(conf) {
        if (conf == undefined) conf = {};

        var newState = getState(),
            rowChangeEvent = conf.rowChangeEvent;


        var options = {
            source:table.id,
            render:table.id,
            execute:table.id,
            formId:table.cfg.formId
        };

        options.onsuccess = function (responseXML) {
            if (table.cfg.scrollable) table.resizeScrolling();
			if (conf.isKeyRequest) {
				if (!table.cfg.scrollable) {
					document.getElementById(table.id).focus(); // in order to allow subsequent keyboard pagination events
				}
				setTimeout(function() { window['keyboardPaginationInProgress' + table.id] = false; }, 300);
			}
        };

        var params = {};
        params[table.id + "_paging"] = true;
        params[table.id + "_rows"] = newState.rowsPerPage;
        params[table.id + "_page"] = newState.page;

        options.params = params;

		if (conf.isKeyRequest) {
			window['keyboardPaginationInProgress' + table.id] = true;
		}

        if (table.behaviors && table.behaviors.page && !rowChangeEvent) {
            ice.ace.ab(ice.ace.extendAjaxArgs(
                table.behaviors.page,
                ice.ace.clearExecRender(options)
            ));
            return;
        } else if (table.behaviors && table.behaviors.rowsPerPage && rowChangeEvent) {
            ice.ace.ab(ice.ace.extendAjaxArgs(
                    table.behaviors.rowsPerPage,
                    ice.ace.clearExecRender(options)
            ));
            return;
        }

        ice.ace.AjaxRequest(options);
    }


    if (cfg.alwaysVisible == false && (cfg.totalRecords <= cfg.rowsPerPage || cfg.rowsPerPage == 0)) {
        container.css('display','none');
    } else {
        initPageMarkup();
        container.css('display','');
        if (!cfg.disabled) initControlEvents();
        else initDisabledStyling();
    }
};

ice.ace.DataTable.Paginator.prototype.destroy = function() {

    this.tableContainer.off('keydown', this.container.keyboardPagination);

    function removeEvents(container) {
        container.children().each(function(i, child) {
            child = ice.ace.jq(child);
            if (child.hasClass('ui-paginator-pages')) removeEvents(child);
            else child.off('click change mouseenter mouseleave');
        });
    }

    removeEvents(this.container);
};