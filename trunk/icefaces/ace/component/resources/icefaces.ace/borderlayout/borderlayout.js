if (!window.ice['ace']) {
    window.ice.ace = {};
}
if (!ice.ace.BorderLayouts) ice.ace.BorderLayouts = {};
ice.ace.BorderLayout = function(parentID, cfg) {

ice.ace.jq(ice.ace.escapeClientId(parentID)).layout(cfg);

};