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

if (!window.ice['ace']) {
    window.ice.ace = {};
}
if (!ice.ace.BorderLayouts) ice.ace.BorderLayouts = {};

ice.ace.BorderLayout = function (id, cfg) {
    console.log(" in ice.ace.BorderLayout....");
    var self = this;
    this.id = id;
    this.jqId = ice.ace.escapeClientId(this.id);
    this.cfg  = cfg;
    var defaultVar = cfg.defaults || null;
    var northVar = cfg.north || null;
    var eastVar = cfg.east || null;
    var southVar = cfg.south || null;
    var westVar = cfg.west || null;
    var centerVar = cfg.center || null;
    //will have to get behaviors by panel
  //  this.behaviors = cfg.behaviors;

    ice.ace.BorderLayouts[id] = self;
 //   var $this = ice.ace.jq(this.jqId);
   // ice.ace.jq(this.jqId).layout({ applyDefaultStyles: true });
    var $ = ice.ace.jq;
    this.container = ice.ace.jq(this.jqId);
    console.log(" id for layout="+this.jqId+" container id ="+this.container.id);
    var options={};
    if (defaultVar != null){
        options.defaults = defaultVar ;
    }

    if (northVar !=null && Object.keys(northVar).length > 0){
        options.north = northVar;
    }
    if (eastVar !=null && Object.keys(eastVar).length > 0){
        options.east = eastVar;
    }
    if (southVar !=null && Object.keys(southVar).length > 0){
        options.south = southVar;
    }
    if (centerVar !=null && Object.keys(centerVar).length > 0){
        options.center = centerVar;
    }
    if (westVar !=null && Object.keys(westVar).length > 0){
        options.west = westVar;
    }
    this.myLayout;
    if ($.isEmptyObject(options))  {
        this.myLayout = this.container.layout({ applyDefaultStyles: true });
        console.log("empty object");
    }
    else {
        console.log(" have options");
        this.myLayout = this.container.layout(options);
    }
};
/**
 * Close the specified pane. If the pane is already closed, nothing happens.
 * @param panel
 */
ice.ace.BorderLayout.prototype.closePanel = function(panel) {
    if (this.myLayout){
        //check to see if pane is in layout?
        this.myLayout.close(panel);
    }
};
/**
 * Open the specified pane. If the pane is already open, nothing happens.
 * If the pane is currently 'hidden' (not just 'closed'), then the pane will be unhidden and opened.
 * @param panel
 */
ice.ace.BorderLayout.prototype.openPanel = function(panel) {
    if (this.myLayout){
            this.myLayout.open(panel);
    }
};
/**
 * 2nd parameter decides if panel is open
 * @param panel
 * @param openPanel
 */
ice.ace.BorderLayout.prototype.showPanel = function(panel, open ) {
    if (this.myLayout){
        console.log("have layout attempt to show="+panel);
        //if panel is already open, northing happens
        this.myLayout.show(panel, openPane=open);
    }
};
/**
 * hides the panel from the layout
 * @param panel
 */
ice.ace.BorderLayout.prototype.hidePanel = function(panel) {
    if (this.myLayout){
        this.myLayout.hide(panel);
    }
};
/**
 * Toggle the specified pane open or closed - the opposite of its current state.
 * @param panel
 */
ice.ace.BorderLayout.prototype.togglePanel = function(panel) {
    if (this.myLayout){
            console.log("have layout attempt to toggle panel="+panel);
            //if already closed, nothing happens
            this.myLayout.toggle(panel);
    }
};
ice.ace.BorderLayout.prototype.sizePanel = function(panel, sizeInPixels){
    if (this.layout){
        this.myLayout.sizePane(panel, sizeInPixels);
    }
} ;
ice.ace.BorderLayout.prototype.resizePanelContent = function(panel){
    if (this.layout ){
        this.myLayout.resizeContent(panel);
    }
}  ;
ice.ace.BorderLayout.prototype.resizeAllPanes = function(){
    if (this.layout ){
        this.myLayout.resizeAll();
    }
} ;

