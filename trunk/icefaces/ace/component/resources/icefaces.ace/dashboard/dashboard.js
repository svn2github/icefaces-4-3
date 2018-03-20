if (!window.ice['ace']) {
    window.ice.ace = {};
}

ice.ace.Dashboard = function(id, cfg) {

var self = this;
this.id = id;
this.jqId = ice.ace.escapeClientId(id);
this.jq = ice.ace.jq(this.jqId);
this.cfg = cfg;

ice.ace.jq(".gridster ul").gridster({
	widget_margins: [10, 10],
	widget_base_dimensions: [140, 140]
});
};