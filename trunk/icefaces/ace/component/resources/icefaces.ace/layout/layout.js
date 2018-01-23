if (!window.ice['ace']) {
    window.ice.ace = {};
}
if (!ice.ace.Layouts) ice.ace.Layouts = {};
ice.ace.Layout = function(parentID, cfg) {

ice.ace.jq(ice.ace.escapeClientId(parentID)).layout();

};