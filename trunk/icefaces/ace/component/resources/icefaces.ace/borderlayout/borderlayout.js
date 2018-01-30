if (!window.ice['ace']) {
    window.ice.ace = {};
}
if (!ice.ace.BorderLayouts) ice.ace.BorderLayouts = {};
ice.ace.BorderLayout = function(id, cfg) {

this.jqId = ice.ace.escapeClientId(id);
this.cfg = cfg;

var layout = ice.ace.jq(this.jqId).layout(cfg);

var self = this;

// buttons
if (this.cfg.north) {
	ice.ace.jq(this.jqId + ' .ice-ace-boderlayout-north .ice-ace-boderlayout-button-close')
		.on('click', function(e) {
			layout.hide('north');
			if (self.cfg.north.behaviors && self.cfg.north.behaviors.close) {
				ice.ace.ab(self.cfg.north.behaviors.close);
			}
		});

	ice.ace.jq(this.jqId + ' .ice-ace-boderlayout-north .ice-ace-boderlayout-button-collapsible')
		.on('click', function(e) {
			layout.toggle('north');
			if (self.cfg.north.behaviors && self.cfg.north.behaviors.toggle) {
				ice.ace.ab(self.cfg.north.behaviors.toggle);
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

	ice.ace.jq(this.jqId + ' .ice-ace-boderlayout-south .ice-ace-boderlayout-button-collapsible')
		.on('click', function(e) {
			layout.toggle('south');
			if (self.cfg.south.behaviors && self.cfg.south.behaviors.toggle) {
				ice.ace.ab(self.cfg.south.behaviors.toggle);
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

	ice.ace.jq(this.jqId + ' .ice-ace-boderlayout-east .ice-ace-boderlayout-button-collapsible')
		.on('click', function(e) {
			layout.toggle('east');
			if (self.cfg.east.behaviors && self.cfg.east.behaviors.toggle) {
				ice.ace.ab(self.cfg.east.behaviors.toggle);
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

	ice.ace.jq(this.jqId + ' .ice-ace-boderlayout-west .ice-ace-boderlayout-button-collapsible')
		.on('click', function(e) {
			layout.toggle('west');
			if (self.cfg.west.behaviors && self.cfg.west.behaviors.toggle) {
				ice.ace.ab(self.cfg.west.behaviors.toggle);
			}
		});
}


/*
layout.addCloseBtn(this.jqId + ' .ice-ace-boderlayout-north .ice-ace-boderlayout-button-close', 'north');
layout.addOpenBtn( "#tbarOpenSouth", "south" );
layout.addCloseBtn( "#tbarCloseSouth", "south" );
layout.addPinBtn( "#tbarPinWest", "west" );
layout.addPinBtn( "#tbarPinEast", "east" );
*/

};