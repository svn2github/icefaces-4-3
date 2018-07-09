if (!window.ice['ace']) {
    window.ice.ace = {};
}

ice.ace.Dashboard = function(id, cfg) {

var self = this;
this.id = id;
this.jqId = ice.ace.escapeClientId(id);
this.jq = ice.ace.jq(this.jqId);
this.cfg = cfg;

// --- tidy measures ---
ice.onElementUpdate(id, function() { ice.ace.destroy(self.id); });

// --- apply data-* attributes that are required by gridster ---
this.applySizeAndPositionData();

// --- gridster widget init ---
this.gridster = this.jq.gridster({
	max_cols: (this.cfg.maxColumns < 0 || this.cfg.maxColumns > 100 ? 100 : this.cfg.maxColumns),
	extra_rows: 2,
	widget_margins: [this.cfg.marginX, this.cfg.marginY],
	widget_base_dimensions: [this.cfg.paneWidth, this.cfg.paneHeight],
	widget_selector: 'div.ice-ace-dashboard-pane',
	draggable: {
		handle: 'div.ui-widget-header',
		// --- dragStop event ---
		stop: function(e, ui) {
			self.updateSizeAndPositionData();
			var paneId = ui.$player.attr('id');
			if (paneId) {
				var paneCfg = self.cfg.panes[paneId];
				if (paneCfg) {
					if (paneCfg.behaviors && paneCfg.behaviors.dragStop) {
						paneCfg.behaviors.dragStop.execute = self.id + ' ' + paneCfg.behaviors.dragStop.execute ;
						ice.ace.ab(paneCfg.behaviors.dragStop);
					}
				}
			}
		}
	},
	resize: {
		enabled: this.cfg.resizable,
		handle_class: 'ice-ace-dashboard-resize-handle',
		// --- resize event ---
		stop: function(e, ui, pane) {
			self.updateSizeAndPositionData();
			var paneId = pane.attr('id');
			if (paneId) {
				var paneCfg = self.cfg.panes[paneId];
				if (paneCfg) {
					if (paneCfg.behaviors && paneCfg.behaviors.resize) {
						paneCfg.behaviors.resize.execute = self.id + ' ' + paneCfg.behaviors.resize.execute ;
						ice.ace.ab(paneCfg.behaviors.resize);
					}
				}
			}
		}
	}
}).data('gridster');

// --- close button and close event ---
ice.ace.jq(this.jqId + ' > .ice-ace-dashboard-pane > div > div > .ui-widget-header > .ice-ace-dashboard-button-close')
	.attr('title', this.cfg.closeTitle)
	.on('click', function(e) {
		self.updateSizeAndPositionData();
		var pane = ice.ace.jq(e.target).closest('.ice-ace-dashboard-pane');
		self.gridster.remove_widget(pane.get(0), true, function() {
			var paneId = pane.attr('id');
			if (paneId) {
				self.jq.append('<div id="'+paneId+'" style="display:none;"></div>'); // re-add empty div for dynamic update
				var paneCfg = self.cfg.panes[paneId];
				if (paneCfg) {
					if (paneCfg.behaviors && paneCfg.behaviors.close) {
						var params = {};
						params[paneId + "_close"] = true;
						paneCfg.behaviors.close.params = params;
						paneCfg.behaviors.close.execute = self.id + ' ' + paneCfg.behaviors.close.execute ;
						ice.ace.ab(paneCfg.behaviors.close);
					}
				}
			}
		});
	}).hover(function(){ice.ace.jq(this).addClass('ui-state-hover');},
		function(){ice.ace.jq(this).removeClass('ui-state-hover');});
};

ice.ace.Dashboard.data = {}; // used to store size and position data

ice.ace.Dashboard.prototype.applySizeAndPositionData = function() {

	if (ice.ace.Dashboard.data[this.id]) {
		var array = ice.ace.Dashboard.data[this.id];
		var i;
		for (i = 0; i < array.length; i++) {
			var pane = array[i];
			var paneRoot = ice.ace.jq(ice.ace.escapeClientId(pane.paneId));
			paneRoot.attr('data-sizex', pane.sizeX);
			paneRoot.attr('data-sizey', pane.sizeY);
			paneRoot.attr('data-row', pane.row);
			paneRoot.attr('data-col', pane.column);
		}
	}
};

ice.ace.Dashboard.prototype.updateSizeAndPositionData = function() {

	var array = [];
	this.jq.children('.ice-ace-dashboard-pane').each(function(){
		var pane = ice.ace.jq(this);
		var data = {};
		data.paneId = pane.attr('id');
		data.sizeX = pane.attr('data-sizex');
		data.sizeY = pane.attr('data-sizey');
		data.row = pane.attr('data-row');
		data.column = pane.attr('data-col');
		array.push(data);
	});
	ice.ace.Dashboard.data[this.id] = array;
	ice.ace.jq(this.jqId + '_state').val(JSON.stringify(array));
};

ice.ace.Dashboard.prototype.enableDragging = function() {
	this.gridster.enable();
	this.jq.addClass('ice-ace-dashboard-draggable');
};

ice.ace.Dashboard.prototype.disableDragging = function() {
	this.gridster.disable();
	this.jq.removeClass('ice-ace-dashboard-draggable');
};

// --- destroy function ---
ice.ace.Dashboard.prototype.destroy = function () {

	this.gridster.destroy();

	ice.ace.jq(this.jqId + ' > .ice-ace-dashboard-pane > div > div > .ui-widget-header > .ice-ace-dashboard-button-close').off();
};