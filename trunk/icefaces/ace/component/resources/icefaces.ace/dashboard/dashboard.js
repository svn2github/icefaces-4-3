if (!window.ice['ace']) {
    window.ice.ace = {};
}

ice.ace.Dashboard = function(id, cfg) {

var self = this;
this.id = id;
this.jqId = ice.ace.escapeClientId(id);
this.jq = ice.ace.jq(this.jqId);
this.cfg = cfg;

this.applySizeAndPositionData();

this.gridster = this.jq.gridster({
	max_cols: (this.cfg.maxColumns > 0 ? this.cfg.maxColumns : null),
	widget_margins: [this.cfg.marginX, this.cfg.marginY],
	widget_base_dimensions: [this.cfg.paneWidth, this.cfg.paneHeight],
	widget_selector: 'div.ice-ace-dashboard-pane',
	draggable: {
		handle: 'div.ui-widget-header'
	},
	resize: {
		enabled: this.cfg.resizable,
		handle_class: 'ice-ace-dashboard-resize-handle'
	}
}).data('gridster');

ice.ace.jq(this.jqId + ' > .ice-ace-dashboard-pane > .ui-widget-header > .ice-ace-dashboard-button-close')
	//.attr('title', this.cfg.closeTitle)
	.on('click', function(e) {
		self.gridster.remove_widget(ice.ace.jq(e.target).closest('.ice-ace-dashboard-pane').get(0));
		/* TODO: send the client id of the pane that was closed, in the ajax request
		/*if (self.cfg.north.behaviors && self.cfg.north.behaviors.close) {
			ice.ace.ab(self.cfg.north.behaviors.close);
		}*/
	}).hover(function(){ice.ace.jq(this).addClass('ui-state-hover');},
		function(){ice.ace.jq(this).removeClass('ui-state-hover');});
};

ice.ace.Dashboard.data = {};

ice.ace.Dashboard.prototype.enableDragging = function() {
	this.gridster.enable();
	this.jq.addClass('ice-ace-dashboard-draggable');
};

ice.ace.Dashboard.prototype.disableDragging = function() {
	this.gridster.disable();
	this.jq.removeClass('ice-ace-dashboard-draggable');
};

ice.ace.Dashboard.prototype.applySizeAndPositionData = function() {

	if (ice.ace.Dashboard.data[this.id]) {
			var jsonArray = ice.ace.Dashboard.data[this.id];
			var i;
			for (i = 0; i < jsonArray.length; i++) {
				var pane = jsonArray[i];
				var paneRoot = ice.ace.jq(ice.ace.escapeClientId(pane.paneId));
				paneRoot.attr('data-sizex', pane.sizeX);
				paneRoot.attr('data-sizey', pane.sizeY);
				paneRoot.attr('data-row', pane.row);
				paneRoot.attr('data-col', pane.column);
			}
	}
};