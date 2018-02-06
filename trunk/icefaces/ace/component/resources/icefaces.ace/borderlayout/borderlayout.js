if (!window.ice['ace']) {
    window.ice.ace = {};
}

ice.ace.BorderLayout = function(id, cfg) {

var self = this;
this.id = id;
this.jqId = ice.ace.escapeClientId(id);
this.jq = ice.ace.jq(this.jqId);
this.cfg = cfg;

// --- tidy measures ---
ice.onElementUpdate(id, function() { ice.ace.destroy(self.id); });

// --- send ajax request for toggle event when expanding panes ---
var expandEventOptions = {};
var params = {};
params[this.id + "_collapsed"] = false;
expandEventOptions.params = params;

if (this.cfg.north && this.cfg.north.behaviors && this.cfg.north.behaviors.toggle) {
	this.cfg.north.onopen_start = function() { ice.ace.ab(ice.ace.extendAjaxArgs(self.cfg.north.behaviors.toggle, expandEventOptions)); };
}

if (this.cfg.south && this.cfg.south.behaviors && this.cfg.south.behaviors.toggle) {
	this.cfg.south.onopen_start = function() { ice.ace.ab(ice.ace.extendAjaxArgs(self.cfg.south.behaviors.toggle, expandEventOptions)); };
}

if (this.cfg.east && this.cfg.east.behaviors && this.cfg.east.behaviors.toggle) {
	this.cfg.east.onopen_start = function() { ice.ace.ab(ice.ace.extendAjaxArgs(self.cfg.east.behaviors.toggle, expandEventOptions)); };
}

if (this.cfg.west && this.cfg.west.behaviors && this.cfg.west.behaviors.toggle) {
	this.cfg.west.onopen_start = function() { ice.ace.ab(ice.ace.extendAjaxArgs(self.cfg.west.behaviors.toggle, expandEventOptions)); };
}

// --- listener for resizing nested layouts ---
cfg.onresize_end = function() {
	ice.ace.jq(self.jqId + ' > .ui-layout-pane > div  > div > div.ice-ace-borderlayout').each(function(i,e) {
		if (this.id) {
			var inst = ice.ace.instance(this.id);
			if (inst) inst.layout.resizeAll();
		}
	});
};

// --- layout widget init ---
this.layout = this.jq.layout(cfg);

// --- buttons' behaviors and events ---
var collapseEventOptions = {};
var params = {};
params[this.id + "_collapsed"] = true;
collapseEventOptions.params = params;

if (this.cfg.north) {
	ice.ace.jq(this.jqId + ' > .ice-ace-boderlayout-north > div > div > div > .ice-ace-boderlayout-button-close')
		.attr('title', this.cfg.closeTitle)
		.on('click', function(e) {
			self.layout.hide('north');
			if (self.cfg.north.behaviors && self.cfg.north.behaviors.close) {
				ice.ace.ab(self.cfg.north.behaviors.close);
			}
		});

	ice.ace.jq(this.jqId + ' > .ice-ace-boderlayout-north > div > div > div > .ice-ace-boderlayout-button-toggle')
		.attr('title', this.cfg.collapseTitle)
		.on('click', function(e) {
			self.layout.toggle('north');
			if (self.cfg.north.behaviors && self.cfg.north.behaviors.toggle) {
				ice.ace.ab(ice.ace.extendAjaxArgs(self.cfg.north.behaviors.toggle, collapseEventOptions));
			}
		});
}

if (this.cfg.south) {
	ice.ace.jq(this.jqId + ' > .ice-ace-boderlayout-south > div > div > div > .ice-ace-boderlayout-button-close')
		.attr('title', this.cfg.closeTitle)
		.on('click', function(e) {
			self.layout.hide('south');
			if (self.cfg.south.behaviors && self.cfg.south.behaviors.close) {
				ice.ace.ab(self.cfg.south.behaviors.close);
			}
		});

	ice.ace.jq(this.jqId + ' > .ice-ace-boderlayout-south > div > div > div > .ice-ace-boderlayout-button-toggle')
		.attr('title', this.cfg.collapseTitle)
		.on('click', function(e) {
			self.layout.toggle('south');
			if (self.cfg.south.behaviors && self.cfg.south.behaviors.toggle) {
				ice.ace.ab(ice.ace.extendAjaxArgs(self.cfg.south.behaviors.toggle, collapseEventOptions));
			}
		});
}

if (this.cfg.east) {
	ice.ace.jq(this.jqId + ' > .ice-ace-boderlayout-east > div > div > div > .ice-ace-boderlayout-button-close')
		.attr('title', this.cfg.closeTitle)
		.on('click', function(e) {
			self.layout.hide('east');
			if (self.cfg.east.behaviors && self.cfg.east.behaviors.close) {
				ice.ace.ab(self.cfg.east.behaviors.close);
			}
		});

	ice.ace.jq(this.jqId + ' > .ice-ace-boderlayout-east > div > div > div > .ice-ace-boderlayout-button-toggle')
		.attr('title', this.cfg.collapseTitle)
		.on('click', function(e) {
			self.layout.toggle('east');
			if (self.cfg.east.behaviors && self.cfg.east.behaviors.toggle) {
				ice.ace.ab(ice.ace.extendAjaxArgs(self.cfg.east.behaviors.toggle, collapseEventOptions));
			}
		});
}

if (this.cfg.west) {
	ice.ace.jq(this.jqId + ' > .ice-ace-boderlayout-west > div > div > div > .ice-ace-boderlayout-button-close')
		.attr('title', this.cfg.closeTitle)
		.on('click', function(e) {
			self.layout.hide('west');
			if (self.cfg.west.behaviors && self.cfg.west.behaviors.close) {
				ice.ace.ab(self.cfg.west.behaviors.close);
			}
		});

	ice.ace.jq(this.jqId + ' > .ice-ace-boderlayout-west > div > div > div > .ice-ace-boderlayout-button-toggle')
		.attr('title', this.cfg.collapseTitle)
		.on('click', function(e) {
			self.layout.toggle('west');
			if (self.cfg.west.behaviors && self.cfg.west.behaviors.toggle) {
				ice.ace.ab(ice.ace.extendAjaxArgs(self.cfg.west.behaviors.toggle, collapseEventOptions));
			}
		});
}

// --- togglers styling ---
this.jq.find('> .ui-layout-resizer')
	.addClass('ui-state-default')
	.css('font-size','');

this.jq.find('> .ui-layout-resizer > .ui-layout-toggler')
	.addClass('ui-state-default')
	.css('font-size','')
	.hover(function(){ice.ace.jq(this).addClass('ui-state-hover');},
		function(){ice.ace.jq(this).removeClass('ui-state-hover');});

this.jq.find('> .ui-layout-resizer > .ui-layout-toggler-north')
	.append('<span class="fa fa-caret-down fa-lg"></span>');

this.jq.find('> .ui-layout-resizer > .ui-layout-toggler-south')
	.append('<span class="fa fa-caret-up fa-lg"></span>');

this.jq.find('> .ui-layout-resizer > .ui-layout-toggler-east')
	.append('<div style="float:left;height:50%;width:100%;margin-top:-10px;"></div>'
		+ '<span class="fa fa-caret-left fa-lg" style="clear:both;"></span>');

this.jq.find('> .ui-layout-resizer > .ui-layout-toggler-west')
	.append('<div style="float:left;height:50%;width:100%;margin-top:-10px;"></div>'
		+ '<span class="fa fa-caret-right fa-lg" style="clear:both;"></span>');

// --- add aria roles to togglers ---
this.jq.find('> .ui-layout-resizer > .ui-layout-toggler').attr('role', 'button');

// --- resizing is necessary for nested layouts to look well ---
this.layout.resizeAll();
};

// --- destroy function ---
ice.ace.BorderLayout.prototype.destroy = function () {

this.layout.destroy();

if (this.cfg.north) {
	ice.ace.jq(this.jqId + ' > .ice-ace-boderlayout-north > div > div > div > .ice-ace-boderlayout-button-close').off();
	ice.ace.jq(this.jqId + ' > .ice-ace-boderlayout-north > div > div > div > .ice-ace-boderlayout-button-toggle').off();
}

if (this.cfg.south) {
	ice.ace.jq(this.jqId + ' > .ice-ace-boderlayout-south > div > div > div > .ice-ace-boderlayout-button-close').off();
	ice.ace.jq(this.jqId + ' > .ice-ace-boderlayout-south > div > div > div > .ice-ace-boderlayout-button-toggle').off();
}

if (this.cfg.east) {
	ice.ace.jq(this.jqId + ' > .ice-ace-boderlayout-east > div > div > div > .ice-ace-boderlayout-button-close').off();
	ice.ace.jq(this.jqId + ' > .ice-ace-boderlayout-east > div > div > div > .ice-ace-boderlayout-button-toggle').off();
}

if (this.cfg.west) {
	ice.ace.jq(this.jqId + ' > .ice-ace-boderlayout-west > div > div > div > .ice-ace-boderlayout-button-close').off();
	ice.ace.jq(this.jqId + ' > .ice-ace-boderlayout-west > div > div > div > .ice-ace-boderlayout-button-toggle').off();
}

};