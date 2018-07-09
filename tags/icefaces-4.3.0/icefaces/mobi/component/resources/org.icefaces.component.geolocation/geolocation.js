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
if (!window.ice) {
    window.ice = {};
};
if (!window['mobi']) {
    window.mobi = {};
};
ice.mobi.storeLocation = function(id, coords) {
    if (!coords) {
        return;
    }
    var el = document.getElementById(id);
    if (!el) {
        return;
    }
    var elValue = (el.value) ? el.value : "";
    var parts = elValue.split(',');
    if (4 != parts.length) {
        parts = new Array(4)
    }
    ;
    parts[0] = coords.latitude;
    parts[1] = coords.longitude;
    parts[2] = coords.altitude;
    el.value = parts.join();
};

ice.mobi.storeDirection = function(id, orient) {
    if (orient.webkitCompassAccuracy <= 0) {
        return;
    }
    var el = document.getElementById(id);
    if (!el) {
        return;
    }
    var elValue = (el.value) ? el.value : "";
    var parts = elValue.split(',');
    if (4 != parts.length) {
        parts = new Array(4)
    }
    parts[3] = orient.webkitCompassHeading;
    el.value = parts.join();
};

ice.mobi.geolocation = {
	orientationCallbacks: {},
    watchIds: {},

    /**
     * Perform a call to watchPosition to fetch running updates to position.
     *
     * @param pClientId base id of hidden field
     * @param highAccuracy true if highAccuracy results to be fetched
     * @param maxAge oldest acceptable cached results (in ms)
     * @param timeout longest time to wait for value (in ms)
     */
    watchLocation: function (pClientId, highAccuracy, maxAge, timeout) {

        ice.mobi.geolocation.clearWatch(pClientId);
        // It seems like on Android that passing any argument at all for enableHighAccuracy
        // enables high accuracy.
        var geoParams = {};
        if (maxAge > 0)  {
            geoParams.maximumAge = maxAge * 1000;
        }
        if (timeout > 0)  {
            geoParams.timeout = timeout * 1000;
        }
        if (highAccuracy != 'false')  {
            geoParams.enableHighAccuracy = true;
        }
        console.log('Launching watchPosition, ' +
            'maxAge: ' + geoParams.maximumAge + '(ms),' +
            ' timeout: ' + geoParams.timeout + '(ms)' +
            ' highAccuracy: ' + geoParams.enableHighAccuracy);

        ice.mobi.geolocation.watchIds[pClientId] = navigator.geolocation.watchPosition(
                ice.mobi.geolocation.getSuccessCallback(pClientId), ice.mobi.geolocation.getErrorCallback(pClientId),
                geoParams);

		if (ice.mobi.geolocation.orientationCallbacks[pClientId]) {
			window.removeEventListener('deviceorientation', ice.mobi.geolocation.orientationCallbacks[pClientId]);
		}
		ice.mobi.geolocation.orientationCallbacks[pClientId] = ice.mobi.geolocation.getOrientationCallback(pClientId);
        ice.mobi.addListener(window, 'deviceorientation', ice.mobi.geolocation.orientationCallbacks[pClientId]);
        ice.onElementRemove(pClientId, function() { ice.mobi.geolocation.clearWatch(pClientId); });
        console.log('Lauching positionWatch for client: ' + pClientId +
                ' watchId: ' + ice.mobi.geolocation.watchIds[pClientId]);
    },

    /**
     * Perform a single call to the navigator getCurrentPosition call.
     */
    getLocation: function (pClientId, highAccuracy, maxAge, timeout) {

        ice.mobi.geolocation.clearWatch(pClientId);

        var geoParams = {};
        if (maxAge > 0)  {
            geoParams.maximumAge = maxAge * 1000;
        }
        if (timeout > 0)  {
            geoParams.timeout = timeout * 1000;
        }
        if (highAccuracy != 'false')  {
            geoParams.enableHighAccuracy = true;
        }
        console.log('Launching getCurrentPosition, ' +
            'maxAge: ' + geoParams.maximumAge + '(ms),' +
            ' timeout: ' + geoParams.timeout + '(ms)' +
            ' highAccuracy: ' + geoParams.enableHighAccuracy);

       navigator.geolocation.getCurrentPosition(ice.mobi.geolocation.getSuccessCallback(pClientId), ice.mobi.geolocation.getErrorCallback(pClientId), geoParams);

		if (ice.mobi.geolocation.orientationCallbacks[pClientId]) {
			window.removeEventListener('deviceorientation', ice.mobi.geolocation.orientationCallbacks[pClientId]);
		}
		ice.mobi.geolocation.orientationCallbacks[pClientId] = ice.mobi.geolocation.getOrientationCallback(pClientId);
        ice.mobi.addListener(window, 'deviceorientation', ice.mobi.geolocation.orientationCallbacks[pClientId]);
        ice.onElementRemove(pClientId, function() { ice.mobi.geolocation.clearWatch(pClientId); });
    },

	getSuccessCallback: function(id) {
		return function(pos) { ice.mobi.geolocation.successCallback(pos,id) };
	},

    successCallback: function(pos, id) {
        console.log('Position update for client: ' + id);
        try {
            inputId = id + "_locHidden";
            ice.mobi.storeLocation(inputId, pos.coords);

        } catch(e) {
            console.log('Exception: ' + e);
        }
    },

	getErrorCallback: function(id) {
		return function(pos) { ice.mobi.geolocation.errorCallback(pos,id) };
	},

    errorCallback: function(positionError, id) {
        console.log('Error in watchPosition, code: ' + positionError.code + ' Message: ' + positionError.message);
        ice.mobi.geolocation.clearWatch(id);
    },

	getOrientationCallback: function(id) {
		return function(orient) { ice.mobi.geolocation.orientationCallback(orient,id) };
	},

    orientationCallback: function(orient, id) {
        inputId = id + "_locHidden";
        ice.mobi.storeDirection(inputId, orient);
    },

    // Clear any existing positionUpdate listeners
    clearWatch: function(pClientId) {
        if (ice.mobi.geolocation.watchIds[pClientId]) {
            console.log('Existing positionWatch: ' + ice.mobi.geolocation.watchIds[pClientId] + ' removed');
            navigator.geolocation.clearWatch(ice.mobi.geolocation.watchIds[pClientId]);
            ice.mobi.geolocation.watchIds[pClientId] = 0;
        }
        window.removeEventListener('deviceorientation', ice.mobi.geolocation.orientationCallbacks[pClientId]);
    }
};

