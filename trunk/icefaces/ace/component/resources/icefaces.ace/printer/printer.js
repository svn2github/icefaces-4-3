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

/*
 * eros@recording.it
 * jqprint 0.3
 */
(function($) {
    var opt;

    $.fn.jqprint = function (options) {
        opt = $.extend({}, $.fn.jqprint.defaults, options);

        var $element = (this instanceof ice.ace.jq) ? this : $(this);
		var $iframe;
		var doc;
		var tab;

        if (opt.operaSupport && $.browser.opera)
        {
            tab = window.open("","jqPrint-preview");
            tab.document.open();

            doc = tab.document;
        }
        else
        {
            $iframe = $("<iframe  />");

            if (!opt.debug) { $iframe.css({ position: "absolute", width: "0px", height: "0px", left: "-600px", top: "-600px" }); }

            $iframe.appendTo("body");
            doc = $iframe[0].contentWindow.document;
        }

        if (opt.importCSS)
        {
            if ($("link[media=print]").length > 0)
            {
                $("link[media=print]").each( function() {
                    doc.write("<link type='text/css' rel='stylesheet' href='" + $(this).attr("href") + "' media='print' />");
                });
            }
            else
            {
                $("link").each( function() {
                    doc.write("<link type='text/css' rel='stylesheet' href='" + $(this).attr("href") + "' />");
                });
            }
        }

        if (opt.printContainer) {
			var container = ice.ace.jq(doc.createElement('div'));
			container.html($element.clone());
			container.find('script').remove();
			doc.write('<body></body>');
			doc.body.appendChild(container.get(0));
		}
        else { $element.each( function() { doc.write($(this).html()); }); }

        doc.close();

		setTimeout( function() {
				var w = opt.operaSupport && $.browser.opera ? tab : $iframe[0].contentWindow;
				w.focus();
				w.print();
				if (tab) { tab.close(); }
		}, 1000);
    }

    $.fn.jqprint.defaults = {
		debug: false,
		importCSS: true,
		printContainer: true,
		operaSupport: true
	};

    // Thanks to 9__, found at http://users.livejournal.com/9__/380664.html
    ice.ace.jq.fn.outer = function() {
		var copy = $('<div></div>');
		copy.html(this.clone());
		copy.find('script').remove();
		return $(copy).html();
    }
})(ice.ace.jq);