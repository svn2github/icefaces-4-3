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

/**
 *  gMap Widget
 */

function interpretLocations(locationList) {
    var locationDescriptions = locationList.split(":");
    var locations = [];
    for (var i = 0, l = locationDescriptions.length; i < l; i++) {
        var locationDescription = locationDescriptions[i];
        var pair = locationDescription.match(/[+-]?([0-9]*[.])?[0-9]+/g);
        if (pair != null) {
            var x = Number(pair[0]);
            var y = Number(pair[1]);
            var coordinates = new google.maps.LatLng(x, y);
            locations.push(coordinates);
        } else {
            locations.push(locationDescription);
        }
    }
    return locations;
}

ice.ace.gMap = function (id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = ice.ace.escapeClientId(id);
    this.jq = ice.ace.jq(this.jqId + '_gMap');
    this.stateHolder = ice.ace.jq(this.jqId + '_active');
    var _self = this;

    //Create gMap
    this.jq.gMap(this.cfg);

    if (this.cfg.dynamic && this.cfg.cache) {
        this.markAsLoaded(this.jq.children('div').get(this.cfg.active));
    }
};

if (!GMapRepository) var GMapRepository = [];


function GMapWrapper(eleId, realGMap) {
    this.eleId = eleId;
    this.realGMap = realGMap;
    this.overlays = {};
    this.markers = {};
    this.freeWindows = {};
    this.infoWindows = {};
    this.directions = {};
    var options = "";
    this.services = {};
    this.events = {};
    this.eventModels = {};
    this.layer = {};
    this.getElementId = ice.ace.gMap.getElementId;
    this.getRealGMap = ice.ace.gMap.getRealGMap;
}
ice.ace.gMap.getElementId = function () {
    return this.eleId;
};

ice.ace.gMap.getRealGMap = function () {
    return this.realGMap;
};

ice.ace.gMap.getGMapWrapper = function (id) {
    var gmapWrapper = GMapRepository[id];
    if (gmapWrapper) {
        var gmapComp = document.getElementById(id);
        //the googlemap view must be unrendered, however
        //javascript object still exist, so recreate the googlemap
        //with its old state.
        if (!gmapComp.hasChildNodes()) {
            gmapWrapper = ice.ace.gMap.recreate(id, gmapWrapper);
        }
    } else {
        //googleMap not found create a fresh new googleMap object
        gmapWrapper = ice.ace.gMap.create(id);
    }
    return gmapWrapper;
};

ice.ace.gMap.addMapLayer = function (ele, layerId, layerType, options, url) {
    var gmapWrapper = ice.ace.gMap.getGMapWrapper(ele);
    var layer = gmapWrapper.layer;
    switch (layerType.toLowerCase()) {
        case "bicycling":
        case "bicyclinglayer":
        case "bicycle":
            layer[layerId] = new google.maps.BicyclingLayer();
            layer[layerId].setMap(gmapWrapper.getRealGMap());
            break;
        case "fusion":
        case "fusiontable":
        case "fusiontables":
            //This is still in it's experimental stage, and I can't get access to the API to make my own fusion table yet. (Google Trusted Testers Only)
            //So I cannot verify if it works. Double check when Fusion Tables is properly released.
            layer[layerId] = new google.maps.FusionTablesLayer(options);
            layer[layerId].setMap(gmapWrapper.getRealGMap());
            break;
        case "kml":
        case "kmllayer":
            layer[layerId] = new google.maps.KmlLayer(url, options);
            layer[layerId].setMap(gmapWrapper.getRealGMap());
            break;
        case "traffic":
        case "trafficlayer":
            layer[layerId] = new google.maps.TrafficLayer();
            layer[layerId].setMap(gmapWrapper.getRealGMap());
            break;
        case "transit":
        case "transitlayer":
            layer[layerId] = new google.maps.TransitLayer();
            layer[layerId].setMap(gmapWrapper.getRealGMap());
            break;
        default:
            if (window.console) {
                window.console.log("ERROR: Not a valid layer type");
            }
            return;
    }//switch
};

ice.ace.gMap.removeMapLayer = function (ele, layerId) {
    var gmapWrapper = ice.ace.gMap.getGMapWrapper(ele);
    var layer = gmapWrapper.layer[layerId];
    if (layer) {
        layer.setMap(null);
    }
};

ice.ace.gMap.locateAddress = function (clientId, address) {

    var geocoder = new google.maps.Geocoder();
    geocoder.geocode({'address': address}, function (results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            var map = ice.ace.gMap.getGMapWrapper(clientId).getRealGMap();
            map.setCenter(new google.maps.LatLng(results[0].geometry.location.lat(), results[0].geometry.location.lng()));
        } else {
            var message;
            if (status == 'ERROR') message = 'There was a problem contacting the Google servers.';
            else if (status == 'INVALID_REQUEST') message = 'This GeocoderRequest was invalid.';
            else if (status == 'OVER_QUERY_LIMIT') message = 'The webpage has gone over the requests limit in too short a period of time.';
            else if (status == 'REQUEST_DENIED') message = 'The webpage is not allowed to use the geocoder.';
            else if (status == 'ZERO_RESULTS') message = 'No result was found for this GeocoderRequest.';
            else message = 'A geocoding request could not be processed due to a server error. The request may succeed if you try again.'; // UNKNOWN_ERROR
            alert("GMaps Geocode was not successful for the following reason: " + message);
        }
    });

};

ice.ace.gMap.replaceAddDomListener = function () {
    ice.ace.gMap.original_addDomListener = google.maps.event.addDomListener;
    google.maps.event.addDomListener = function (instance, eventName, handler, capture) {
        var eventListener = ice.ace.gMap.original_addDomListener.call(google.maps.event, instance, eventName, handler, capture);
        if (instance == window && eventName == 'resize') {
            google.maps.event.removeListener(eventListener);
            if (!ice.ace.gMap.windowResizeListener) {
                ice.ace.gMap.windowResizeListener =
                    ice.ace.gMap.original_addDomListener.call(google.maps.event, window, 'resize', ice.ace.gMap.windowResizeHandler);
            }
        }
        return eventListener;
    };
};

ice.ace.gMap.windowResizeHandler = function () {
    for (var mapKey in GMapRepository) {
        var map = GMapRepository[mapKey];
        if (map) google.maps.event.trigger(map, 'resize');
    }
};

ice.ace.gMap.create = function (ele, lat, lng, zoom, type) {
    // replace addDomListener here, because the events code is lazily loaded by google
    if (!ice.ace.gMap.original_addDomListener) ice.ace.gMap.replaceAddDomListener();

    if (lat == undefined && lng == undefined)
        var gmapWrapper = new GMapWrapper(ele, new google.maps.Map(document.getElementById(ele), {
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            zoom: 5,
            center: new google.maps.LatLng(0, 0)
        }));
    else
        var gmapWrapper = new GMapWrapper(ele, new google.maps.Map(document.getElementById(ele), {
            mapTypeId: type,
            zoom: zoom,
            center: new google.maps.LatLng(lat, lng)
        }));
    var hiddenField = document.getElementById(ele);
    var mapTypedRegistered = false;
    var map = gmapWrapper.getRealGMap();
    google.maps.event.addDomListener(map, "center_changed", function () {
        var lat = document.getElementById(ele + "_lat");
        if (lat) lat.value = map.getCenter().lat();
        var lng = document.getElementById(ele + "_lng");
        if (lng) lng.value = map.getCenter().lng();
    });
    google.maps.event.addDomListener(map, "zoom_changed", function () {
        var zoom = document.getElementById(ele + "_zoom");
        if (zoom) zoom.value = map.getZoom();
    });
    google.maps.event.addDomListener(map, "maptypeid_changed", function () {
        var type = document.getElementById(ele + "_type");
        if (type) type.value = map.getMapTypeId();
    });
    var previousVisible = ice.ace.jq(map.getDiv()).is(':visible');
    gmapWrapper.poll = window.setInterval(function () {
        var currentVisible = ice.ace.jq(map.getDiv()).is(':visible');
        if (!previousVisible && currentVisible) {
            google.maps.event.trigger(map, 'resize');
        }
        previousVisible = currentVisible;
    }, 1000);
    var initializing = false;
    GMapRepository[ele] = gmapWrapper;

    //if IE8 or less apply ICE-10375 fix for Element.getBoundingClientRect
    if (!document.addEventListener) {
        var mapDirectionsElement = document.getElementById('gMapDirections');
        if (mapDirectionsElement) {
            var originalFunction = mapDirectionsElement.getBoundingClientRect;
            mapDirectionsElement.getBoundingClientRect = function () {
                try {
                    return originalFunction.apply(this, arguments);
                } catch (e) {
                    return {
                        left: '',
                        right: '',
                        top: '',
                        bottom: ''
                    };
                }
            };
        }
    }

    return gmapWrapper;
};

ice.ace.gMap.recreate = function (ele, gmapWrapper) {
    var map = gmapWrapper.getRealGMap();
    var options = gmapWrapper.options;
    var lat = map.getCenter().lat();
    var lng = map.getCenter().lng();
    var zoom = map.getZoom();
    var type = map.getMapTypeId();
    var markers = gmapWrapper.markers;
    var freeWindows = gmapWrapper.freeWindows;
    var infoWindows = gmapWrapper.infoWindows;
    var overlays = gmapWrapper.overlays;
    var layer = gmapWrapper.layer;
    var events = gmapWrapper.eventModels;
    ice.ace.gMap.remove(ele);
    gmapWrapper = ice.ace.gMap.create(ele, lat, lng, zoom, type);
    map = gmapWrapper.getRealGMap();
    map.setOptions(options);
    gmapWrapper.options = options;
    for (var marker in markers) {
        if (gmapWrapper.markers[marker] == null) {
            markers[marker].setMap(map);
            gmapWrapper.markers[marker] = markers[marker];
        }
    }
    for (var freeWin in freeWindows) {
        if (gmapWrapper.freeWindows[freeWin] == null) {
            freeWindows[freeWin].open(map);
            gmapWrapper.freeWindows[freeWin] = freeWindows[freeWin];
        }
    }
    for (var win in infoWindows) {

        if (gmapWrapper.infoWindows[win] == null) {
            gmapWrapper.infoWindows[win] = infoWindows[win];
        }

    }
    for (var overlay in overlays) {
        if (gmapWrapper.overlays[overlay] == null) {
            overlays[overlay].setMap(map);
            gmapWrapper.overlays[overlay] = overlays[overlay];
        }
    }
    if (layer != null) {
        for (var l in layer) {
            layer[l].setMap(map);
        }
        gmapWrapper.layer = layer;
    }
    for (var event in events) {
        if (gmapWrapper.eventModels[event] == null) {
            var e = events[event];
            if (!e) continue;
            ice.ace.gMap.addEvent(e.mapId, e.parentId, e.eventId, e.parentName, e.eventType, e.rendererType, e.script, e.listener);
        }
    }
    return gmapWrapper;
};

ice.ace.gMap.remove = function (ele) {
    var newRepository = [];
    for (var map in GMapRepository) {
        if (map != ele) {
            newRepository[map] = GMapRepository[map];
        }
        else {
            var divParent = document.getElementById(ele).parentNode;
            var styleClass = document.getElementById(ele).getAttribute('class');
            var style = document.getElementById(ele).getAttribute('style');
            var mapDiv = document.getElementById(ele);
            mapDiv.parentNode.removeChild(mapDiv);
            var div = document.createElement("div");
            div.setAttribute("class", styleClass);
            div.setAttribute("style", style);
            div.setAttribute('id', ele);
            divParent.appendChild(div);
        }
    }
    GMapRepository = newRepository;
};

ice.ace.gMap.addMarker = function (ele, markerID, Lat, Lon, address, options) {
    var wrapper = ice.ace.gMap.getGMapWrapper(ele);
    var marker = wrapper.markers[markerID];
	if (marker != null) { // update marker object
        var markerOps;
        markerOps = options;
        markerOps.map = wrapper.getRealGMap();
        if (address) {
            markerOps.position = new google.maps.LatLng(0, 0);
            var visible = markerOps.visible !== false;
            markerOps.visible = false;
            marker.setOptions(markerOps);
            var initGeocoder = new google.maps.Geocoder();
            initGeocoder.geocode({'address': address}, function (results, status) {
                if (status == google.maps.GeocoderStatus.OK) {
                    var result = results[0];
                    if (result) {
                        marker.setPosition(result.geometry.location);
                        marker.setVisible(visible);
                        var callbacks = ice.ace.gMap.markerCallbacks[markerID];
                        if (callbacks) {
                            while (callbacks.length > 0)
                                callbacks.pop().call(marker);
                        }
                    }
                }
            });
        } else {
            markerOps.position = new google.maps.LatLng(Lat, Lon);
            marker.setOptions(markerOps);
        }
    } else if (marker == null || marker.getMap() == null) {
        var markerOps;
        markerOps = options;
        markerOps.map = wrapper.getRealGMap();
        if (address) {
            markerOps.position = new google.maps.LatLng(0, 0);
            var visible = markerOps.visible !== false;
            markerOps.visible = false;
            marker = new google.maps.Marker(markerOps);
            wrapper.markers[markerID] = marker;
            var initGeocoder = new google.maps.Geocoder();
            initGeocoder.geocode({'address': address}, function (results, status) {
                if (status == google.maps.GeocoderStatus.OK) {
                    var result = results[0];
                    if (result) {
                        marker.setPosition(result.geometry.location);
                        marker.setVisible(visible);
                        var callbacks = ice.ace.gMap.markerCallbacks[markerID];
                        if (callbacks) {
                            while (callbacks.length > 0)
                                callbacks.pop().call(marker);
                        }
                    }
                }
            });
        } else {
            markerOps.position = new google.maps.LatLng(Lat, Lon);
            marker = new google.maps.Marker(markerOps);
            wrapper.markers[markerID] = marker;
        }
    }
	var previousListener = marker.listener;
	if (previousListener) google.maps.event.removeListener(previousListener);
	if (marker.getDraggable()) {
		marker.listener = google.maps.event.addListener(marker, "dragend", 	function(event) {
			var options = {
				source: markerID,
				execute: markerID,
				render: '@all'
			};

			var params = {};
			params[markerID + "_lat"] = event.latLng.lat();
			params[markerID + "_lng"] = event.latLng.lng();

			options.params = params;

			ice.ace.AjaxRequest(options);
		});
	}
};

if (!ice.ace.gMap.markerCallbacks) ice.ace.gMap.markerCallbacks = {};

ice.ace.gMap.addMarkerCallback = function (id, callback) {
    if (!ice.ace.gMap.markerCallbacks[id]) ice.ace.gMap.markerCallbacks[id] = [];
    ice.ace.gMap.markerCallbacks[id].push(callback);
};

ice.ace.gMap.removeMarker = function (ele, markerId, persistObject) {
    var gmapWrapper = ice.ace.gMap.getGMapWrapper(ele);
    var marker = gmapWrapper.markers[markerId];
    if (marker != null) {
        marker.setMap(null);
    } else {
        //nothing found just return
        return;
    }
	if (!persistObject) {
		var newMarkerArray = {};
		for (var markerObj in gmapWrapper.markers) {
			if (marker != markerObj) {
				newMarkerArray[markerObj] = gmapWrapper.markers[markerObj];
			}
		}
		gmapWrapper.markers = newMarkerArray;
	}
};

ice.ace.gMap.animateMarker = function (ele, markerId, animation) {
    var gmapWrapper = ice.ace.gMap.getGMapWrapper(ele);
    var marker = gmapWrapper.markers[markerId];
    if (animation == "none")
        marker.setOptions({animation: null});
    else if (animation.toLowerCase() == "bounce")
        marker.setOptions({animation: google.maps.Animation.BOUNCE});
    else if (animation.toLowerCase() == "drop")
        marker.setOptions({animation: google.maps.Animation.DROP});
    else
        alert("Invalid Animation Type");
};

ice.ace.gMap.addOptions = function (ele, options) {
    var map = ice.ace.gMap.getGMapWrapper(ele).getRealGMap();
    ice.ace.gMap.getGMapWrapper(ele).options = options;
    map.setOptions(options);
};

ice.ace.gMap.addAutoComplete = function (mapId, autoId, windowOptions, offset, windowRender, focus) {

    var input = ice.ace.jq(ice.ace.escapeClientId(autoId)).children().get(0);
    if (focus) {
        if (input.createTextRange) { // IE
            input.focus();
            if (input.value.length > 0) {
                var fieldRange = input.createTextRange();
                fieldRange.moveStart('character', input.value.length);
                fieldRange.collapse(false);
                fieldRange.select();
            }
        }
        else {
            input.focus();
            var length = input.value.length;
            input.setSelectionRange(length, length);
        }
    }
    var init = function () {
        ice.ace.jq(input).off('keypress').on('keypress', function (e) {
            if (e.keyCode == 13 || e.which == 13) return false;
        });
        input.submitOnEnter = 'disabled';
        var autocomplete = new google.maps.places.Autocomplete(input);
        var map = ice.ace.gMap.getGMapWrapper(mapId).getRealGMap();
        if (windowRender) {
            var infowindow = new google.maps.InfoWindow();
            var marker = new google.maps.Marker({
                map: map
            });
        }
        var splitOffset = offset.split(",");
        var xOffset = splitOffset[0];
        var yOffset = splitOffset[1];
        google.maps.event.addListener(autocomplete, 'place_changed', function () {
            var place = autocomplete.getPlace();
            var hasSpecialChars = (input.value.indexOf('[') > -1) || (input.value.indexOf(']') > -1) || (input.value.indexOf('/') > -1);
            if (place.geometry && !hasSpecialChars) {
                if (place.geometry.viewport) {
                    map.fitBounds(place.geometry.viewport);
                } else {
                    map.setCenter(place.geometry.location);
                }
                map.panBy(Number(xOffset), Number(yOffset));
                if (windowRender) {
                    marker.setPosition(place.geometry.location);
                    infowindow.setContent("<a href='" + place.url + "' target='_blank'>" + place.formatted_address + "</a>");
                    infowindow.setOptions(windowOptions);
                    infowindow.open(map, marker);
                }
                document.getElementById(autoId + "_latLng").value = place.geometry.location.toString();
                document.getElementById(autoId + "_address").value = place.formatted_address;
                document.getElementById(autoId + "_types").value = place.types.toString();
                document.getElementById(autoId + "_url").value = place.url;
                ice.se(null, autoId);
            } else {
                var geocoder = new google.maps.Geocoder();
                geocoder.geocode({'address': place.name}, function (results, status) {
                    if (status == google.maps.GeocoderStatus.OK) {
                        var result = results[0];
                        if (result.geometry.viewport) {
                            map.fitBounds(result.geometry.viewport);
                        } else {
                            map.setCenter(result.geometry.location);
                        }
                        input.value = result.formatted_address;

                        var url = 'https://maps.google.com/maps/place?q=' + encodeURIComponent(result.formatted_address);
                        map.panBy(Number(xOffset), Number(yOffset));
                        if (windowRender) {
                            marker.setPosition(result.geometry.location);
                            infowindow.setContent("<a href='" + url + "' target='_blank'>" + result.formatted_address + "</a>");
                            infowindow.setOptions(windowOptions);
                            infowindow.open(map, marker);
                        }

                        document.getElementById(autoId + "_latLng").value = result.geometry.location.toString();
                        document.getElementById(autoId + "_address").value = result.formatted_address;
                        document.getElementById(autoId + "_types").value = result.types.toString();
                        document.getElementById(autoId + "_url").value = url;
                        ice.se(null, autoId);
                    }
                });
            }
        });
        var address = input.value;
        if (address) {
            var initGeocoder = new google.maps.Geocoder();
            initGeocoder.geocode({'address': address}, function (results, status) {
                if (status == google.maps.GeocoderStatus.OK) {
                    var result = results[0];
                    if (result.geometry.viewport) {
                        map.fitBounds(result.geometry.viewport);
                    } else {
                        map.setCenter(result.geometry.location);
                    }
                    var initSplitOffset = offset.split(",");
                    var initXOffset = initSplitOffset[0];
                    var initYOffset = initSplitOffset[1];
                    map.panBy(Number(initXOffset), Number(initYOffset));
                }
            });
        }
    };
    if (focus) {
        setTimeout(init, 100);
    } else {
        init();
    }
};

ice.ace.gMap.addControl = function (ele, name, givenPosition, style) {
    var wrapper = ice.ace.gMap.getGMapWrapper(ele);
    var map = wrapper.getRealGMap();
    var option = {};
    if (name == "all")
        option.disableDefaultUI = false;
    else {
        var control = ice.ace.gMap.nameToControl(name);
        option[control] = true;
        if (givenPosition != "none" || style != "none") {
            if (givenPosition != "none" && style == "none") {
                var position = ice.ace.gMap.textToPosition(givenPosition);
                option[control + "Options"] = {'position': position};
            }
            else if (givenPosition == "none" && style != "none") {
                var fullStyle = ice.ace.gMap.textToStyle(name, style);
                option[control + "Options"] = {'style': fullStyle};
            }
            else if (givenPosition != "none" && style != "none") {
                var position = ice.ace.gMap.textToPosition(givenPosition);
                var fullStyle = ice.ace.gMap.textToStyle(name, style);
                option[control + "Options"] = {'position': position, 'style': fullStyle};
            }
        }
    }
    wrapper.options = option;
    map.setOptions(option);
};

ice.ace.gMap.removeControl = function (ele, name) {
    var wrapper = ice.ace.gMap.getGMapWrapper(ele);
    var map = wrapper.getRealGMap();
    var option = {};
    if (name == "all")
        option.disableDefaultUI = true;
    else {
        var control = ice.ace.gMap.nameToControl(name);
        option[control] = false;
    }
    wrapper.options = option;
    map.setOptions(option);
};

ice.ace.gMap.nameToControl = function (name) {
    switch (name.toLowerCase()) {
        case "type":
            return "mapTypeControl";
            break;
        case "overview":
            return "overviewMapControl";
            break;
        case "pan":
            return "panControl";
            break;
        case "rotate":
            return "rotateControl";
            break;
        case "scale":
            return "scaleControl";
            break;
        case "streetview":
            return "streetViewControl";
            break;
        case "zoom":
            return "zoomControl";
            break;
    }
};

ice.ace.gMap.textToPosition = function (position) {
    switch (position.toLowerCase()) {
        case "bottomcenter":
            return google.maps.ControlPosition.BOTTOM_CENTER;
        case "bottomright":
            return google.maps.ControlPosition.BOTTOM_RIGHT;
        case "bottomleft":
            return google.maps.ControlPosition.BOTTOM_LEFT;
        case "topcenter":
            return google.maps.ControlPosition.TOP_CENTER;
        case "topright":
            return google.maps.ControlPosition.TOP_RIGHT;
        case "topleft":
            return google.maps.ControlPosition.TOP_LEFT;
        case "lefttop":
            return google.maps.ControlPosition.LEFT_TOP;
        case "leftcenter":
            return google.maps.ControlPosition.LEFT_CENTER;
        case "leftbottom":
            return google.maps.ControlPosition.LEFT_BOTTOM;
        case "righttop":
            return google.maps.ControlPosition.RIGHT_TOP;
        case "rightcenter":
            return google.maps.ControlPosition.RIGHT_CENTER;
        case "rightbottom":
            return google.maps.ControlPosition.RIGHT_BOTTOM;
    }
};

ice.ace.gMap.textToStyle = function (rawname, rawstyle) {
    var name = rawname.toLowerCase();
    var style = rawstyle.toLowerCase();
    if (name == "type") {
        if (style == "default")
            return google.maps.MapTypeControlStyle.DEFAULT;
        else if (style == "dropdown")
            return google.maps.MapTypeControlStyle.DROPDOWN_MENU;
        else if (style == "bar")
            return google.maps.MapTypeControlStyle.HORIZONTAL_BAR;
    }
    else if (name == "zoom") {
        if (style == "default")
            return google.maps.ZoomControlStyle.DEFAULT;
        else if (style == "large")
            return google.maps.ZoomControlStyle.LARGE;
        else if (style == "small")
            return google.maps.ZoomControlStyle.SMALL;
    }
};

ice.ace.gMap.gService = function (ele, id, name, locationList, options, div) {
    var map = ice.ace.gMap.getGMapWrapper(ele).getRealGMap();
    var service;
    var previousRenderer = ice.ace.gMap.gService.instances[id];
    if (previousRenderer) {
        previousRenderer.setMap(null);
        previousRenderer.setPanel(null);
    }
    var points = interpretLocations(locationList);

    switch (name.toLowerCase()) {
        case "direction":
        case "directions":
        case "directionsservice":
            //Required options: travelMode, 2 points/addresses (First=origin, last=dest, others=waypoints
            service = new google.maps.DirectionsService();
            var lastElement = points.length - 1;
            var request = options;
            request.origin = points[0];
            request.destination = points[lastElement];
            if (points.length >= 3) {
                var waypoints = [];
                for (var i = 1; i < points.length - 1; i++) {
                    waypoints.push({
                        'location': points[i]
                    });
                }
                request.waypoints = waypoints;
            }
        function directionsCallback(response, status) {
            if (status != google.maps.DirectionsStatus.OK) {
                alert('Error was: ' + status);
            } else {
                var renderer = new google.maps.DirectionsRenderer();
                renderer.setMap(map);
                renderer.setDirections(response);
                renderer.setPanel(document.getElementById(div));
                ice.ace.gMap.gService.instances[id] = renderer;
            }
        }

            service.route(request, directionsCallback);
            break;
        case "elevation":
        case "elevationservice":
            service = new google.maps.ElevationService();
            var request = {
                'locations': points
            };

        function elevationCallback(response, status) {
            if (status != google.maps.ElevationStatus.OK) {
                alert('Error was: ' + status);
            } else {
                for (var i = 0; i < response.length; i++) {
                    alert(response[i].elevation);
                }
            }
        }

            service.getElevationForLocations(request, elevationCallback);
            break;
        case "maxzoom":
        case "maxzoomservice":
            service = new google.maps.MaxZoomService();
            var point = points[0];

        function maxZoomCallback(response) {
            if (response.status != google.maps.MaxZoomStatus.OK) {
                alert('Error occurred in contacting Google servers');
            } else {
                alert("Max zoom at point is: " + response.zoom);
            }
        }

            service.getMaxZoomAtLatLng(point, maxZoomCallback);
            break;
        case "distance":
        case "distancematrix":
        case "distancematrixservice":
            //Required options: travelMode, 2 points/addresses
            if (!points[1]) break;
            service = new google.maps.DistanceMatrixService();
            var request = options;
            request.origins = points[0];
            request.destinations = points[1];

        function distanceCallback(response, status) {
            if (status != google.maps.DistanceMatrixStatus.OK) {
                alert('Error was: ' + status);
            } else {
                alert("Distance is:" + response.rows[0].elements[0].distance.text + " in " + response.rows[0].elements[0].duration.text);
            }
        }

            service.getDistanceMatrix(request, distanceCallback);
            break;
        default:
            if (window.console) {
                window.console.log("Not a valid service name");
            }
            return;
    }// end switch
};

ice.ace.gMap.gService.instances = {};

ice.ace.gMap.removeGOverlay = function (ele, overlayID) {
    var wrapper = ice.ace.gMap.getGMapWrapper(ele);
    var overlay = wrapper.overlays[overlayID];
    if (overlay != null) {
        overlay.setMap(null);
    } else {
        //nothing found just return
        return;
    }
    var newOverlayArray = {};
    for (var overlayObj in wrapper.overlays) {
        if (overlay != overlayObj) {
            newOverlayArray[overlayObj] = wrapper.overlays[overlayObj];
        }
    }
    wrapper.overlays = newOverlayArray;
};

ice.ace.gMap.gOverlay = function (ele, overlayID, shape, locationList, overlayOptions) {
    var wrapper = ice.ace.gMap.getGMapWrapper(ele);
    var map = ice.ace.gMap.getGMapWrapper(ele).getRealGMap();
    var overlay;
    var points = interpretLocations(locationList);
    switch (shape.toLowerCase()) {
        case "line":
        case "polyline":
            overlayOptions.map = map;
            overlayOptions.path = points;
            overlay = new google.maps.Polyline(overlayOptions);
            break;
        case "polygon":
            overlayOptions.map = map;
            overlayOptions.paths = points;
            overlay = new google.maps.Polygon(overlayOptions);
            break;
        case "rectangle":
            //needs SW corner in first point, NE in second
            overlayOptions.map = map;
            overlayOptions.bounds = new google.maps.LatLngBounds(points[0], points[1]);
            overlay = new google.maps.Rectangle(overlayOptions);
            break;
        case "circle":
            //Requires radius option
            overlayOptions.map = map;
            overlayOptions.center = points[0];
            overlay = new google.maps.Circle(overlayOptions);
            break;
        default:
            if (window.console) {
                window.console.log("Not a valid shape");
            }
            return;
    }//switch
    ice.ace.gMap.getGMapWrapper(ele).overlays[overlayID] = overlay;
};

ice.ace.gMap.addGWindow = function (ele, winId, content, position, options, markerId, showOnClick, startOpen, addressBasedMarker) {
    var init = function () {
        var wrapper = ice.ace.gMap.getGMapWrapper(ele);
        var map = ice.ace.gMap.getGMapWrapper(ele).getRealGMap();
        var win = wrapper.infoWindows[winId];
        if (win != null)
            win.close();
        win = new google.maps.InfoWindow();
        win.setPosition(position);
        win.setContent(content);
        if (addressBasedMarker) {
            options.disableAutoPan = true;
            win.setOptions(options);
        }
        if (markerId != "none") {
            var marker = wrapper.markers[markerId];
            if (showOnClick) {
                google.maps.event.addDomListener(marker, "click", function () {
                    var map = ice.ace.gMap.getGMapWrapper(ele).getRealGMap();
                    win.open(map, marker);
                });
                if (startOpen)
                    win.open(map, marker);
            }
            else
                win.open(map, marker);
        }
        else {
            win.open(map);
            ice.ace.gMap.getGMapWrapper(ele).freeWindows[winId] = win;
        }
        google.maps.event.addDomListener(win, "closeclick", function () {
            ice.ace.gMap.removeGWindow(ele, winId)
        });
        ice.ace.gMap.getGMapWrapper(ele).infoWindows[winId] = win;
    };
    if (addressBasedMarker) ice.ace.gMap.addMarkerCallback(markerId, init);
    else init();
};

ice.ace.gMap.removeGWindow = function (mapId, winId) {
    var wrapper = ice.ace.gMap.getGMapWrapper(mapId);
    var win = wrapper.infoWindows[winId];
    if (win != null) {
        win.close();
        //let server know that window was closed
        ice.ser(null, winId, function (param) {
            param(winId, 'close')
        });
    } else {
        return;
    }
    var newWindowArray = {};
    delete(wrapper.infoWindows[winId]);
    var newFreeWindowArray = {};
    if (wrapper.freeWindows[winId] != null)
        delete(wrapper.freeWindows[winId]);
};

ice.ace.gMap.setMapType = function (ele, type) {
    var gmapWrapper = ice.ace.gMap.getGMapWrapper(ele);
    if (type == "MAP")
        type = "ROADMAP";
    if (gmapWrapper.getRealGMap().getMapTypeId() != null) {
        switch (type) {
            case "SATELLITE":
                gmapWrapper.getRealGMap().setMapTypeId(google.maps.MapTypeId.SATELLITE);
                break;
            case "HYBRID":
                gmapWrapper.getRealGMap().setMapTypeId(google.maps.MapTypeId.HYBRID);
                break;
            case "ROADMAP":
                gmapWrapper.getRealGMap().setMapTypeId(google.maps.MapTypeId.ROADMAP);
                break;
            case "TERRAIN":
                gmapWrapper.getRealGMap().setMapTypeId(google.maps.MapTypeId.TERRAIN);
                break;
        }
    }
};

ice.ace.gMap.addEvent = function (mapId, parentId, eventId, parentName, eventType, rendererType, callback, listener, addressBasedMarker) {
    var wrapper = ice.ace.gMap.getGMapWrapper(mapId);
    var componentToUse;
    //TODO: Update Autocomplete to work with this.
    if (parentName.indexOf("gmap.GMapAutocomplete") != -1) {
        componentToUse = wrapper.infoWindows[parentId];
    }
    else if (parentName.indexOf("gmap.GMapInfoWindow") != -1) {
        componentToUse = wrapper.infoWindows[parentId];
    }
    else if (parentName.indexOf("gmap.GMapLayer") != -1) {
        componentToUse = wrapper.layer;
    }
    else if (parentName.indexOf("gmap.GMapMarker") != -1) {
        componentToUse = wrapper.markers[parentId];
    }
    else if (parentName.indexOf("gmap.GMapOverlay") != -1) {
        componentToUse = wrapper.overlays[parentId];
    }
    else if (parentName.indexOf("gmap.GMap") != -1) {
        componentToUse = wrapper.getRealGMap();
    }

    var event = wrapper.events[eventId];
    if (event) {
        google.maps.event.removeListener(event);
    }

    function eventHandler() {
        var map = ice.ace.gMap.getGMapWrapper(mapId).getRealGMap();
        callback(map, componentToUse);
        if (listener) {
            ice.se(null, document.getElementById(mapId), function (p) {
                p(eventId, true);
            });
        }
    }

    if (componentToUse) {
        wrapper.events[eventId] = google.maps.event.addDomListener(componentToUse, eventType, eventHandler);
    } else if (addressBasedMarker) {
        ice.ace.gMap.addMarkerCallback(parentId, function () {
            wrapper.events[eventId] = google.maps.event.addDomListener(wrapper.markers[parentId], eventType, eventHandler);
        });
    }
    wrapper.eventModels[eventId] = {
        mapId: '' + mapId,
        parentId: '' + parentId,
        eventId: '' + eventId,
        parentName: '' + parentName,
        eventType: '' + eventType,
        rendererType: '' + rendererType,
        script: '' + callback,
        listener: '' + listener
    };
};

ice.ace.gMap.removeEvent = function (mapId, eventId) {
    var wrapper = ice.ace.gMap.getGMapWrapper(mapId);
    var event = wrapper.events[eventId];
    if (event) {
        google.maps.event.removeListener(event);
    }
    try {
        delete wrapper.eventModels[eventId];
    } catch (e) {
    }
};
