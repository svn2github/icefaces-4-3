if (!window.ice['ace']) {
    window.ice.ace = {};
}
if (!ice.ace.BorderLayouts) ice.ace.BorderLayouts = {};
ice.ace.BorderLayout = function(id, cfg) {
var self = this;
this.id = id;
this.jqId = ice.ace.escapeClientId(id);
this.jq = ice.ace.jq(this.jqId);
this.cfg = cfg;

// send ajax request for toggle event when expanding panes
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

var layout = this.jq.layout(cfg);

// buttons

var collapseEventOptions = {};
var params = {};
params[this.id + "_collapsed"] = true;
collapseEventOptions.params = params;

if (this.cfg.north) {
	ice.ace.jq(this.jqId + ' .ice-ace-boderlayout-north .ice-ace-boderlayout-button-close')
		.on('click', function(e) {
			layout.hide('north');
			if (self.cfg.north.behaviors && self.cfg.north.behaviors.close) {
				ice.ace.ab(self.cfg.north.behaviors.close);
			}
		});

	ice.ace.jq(this.jqId + ' .ice-ace-boderlayout-north .ice-ace-boderlayout-button-toggle')
		.on('click', function(e) {
			layout.toggle('north');
			if (self.cfg.north.behaviors && self.cfg.north.behaviors.toggle) {
				ice.ace.ab(ice.ace.extendAjaxArgs(self.cfg.north.behaviors.toggle, collapseEventOptions));
			}
		});
}

if (this.cfg.south) {
	ice.ace.jq(this.jqId + ' .ice-ace-boderlayout-south .ice-ace-boderlayout-button-close')
		.on('click', function(e) {
			layout.hide('south');
			if (self.cfg.south.behaviors && self.cfg.south.behaviors.close) {
				ice.ace.ab(self.cfg.south.behaviors.close);
			}
		});

	ice.ace.jq(this.jqId + ' .ice-ace-boderlayout-south .ice-ace-boderlayout-button-toggle')
		.on('click', function(e) {
			layout.toggle('south');
			if (self.cfg.south.behaviors && self.cfg.south.behaviors.toggle) {
				ice.ace.ab(ice.ace.extendAjaxArgs(self.cfg.south.behaviors.toggle, collapseEventOptions));
			}
		});
}

if (this.cfg.east) {
	ice.ace.jq(this.jqId + ' .ice-ace-boderlayout-east .ice-ace-boderlayout-button-close')
		.on('click', function(e) {
			layout.hide('east');
			if (self.cfg.east.behaviors && self.cfg.east.behaviors.close) {
				ice.ace.ab(self.cfg.east.behaviors.close);
			}
		});

	ice.ace.jq(this.jqId + ' .ice-ace-boderlayout-east .ice-ace-boderlayout-button-toggle')
		.on('click', function(e) {
			layout.toggle('east');
			if (self.cfg.east.behaviors && self.cfg.east.behaviors.toggle) {
				ice.ace.ab(ice.ace.extendAjaxArgs(self.cfg.east.behaviors.toggle, collapseEventOptions));
			}
		});
}

if (this.cfg.west) {
	ice.ace.jq(this.jqId + ' .ice-ace-boderlayout-west .ice-ace-boderlayout-button-close')
		.on('click', function(e) {
			layout.hide('west');
			if (self.cfg.west.behaviors && self.cfg.west.behaviors.close) {
				ice.ace.ab(self.cfg.west.behaviors.close);
			}
		});

	ice.ace.jq(this.jqId + ' .ice-ace-boderlayout-west .ice-ace-boderlayout-button-toggle')
		.on('click', function(e) {
			layout.toggle('west');
			if (self.cfg.west.behaviors && self.cfg.west.behaviors.toggle) {
				ice.ace.ab(ice.ace.extendAjaxArgs(self.cfg.west.behaviors.toggle, collapseEventOptions));
			}
		});
}

// togglers styling

this.jq.find('.ui-layout-resizer')
	.addClass('ui-state-default')
	.css('font-size','');

this.jq.find('.ui-layout-resizer .ui-layout-toggler')
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
};