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

if (!window['mobi']) {
    window.mobi = {};
}

mobi.datespinner = {
    pattern:{}, //supported formats are dd/MM/yyyy, MM-dd-yyyy, dd-MM-yyyy, yyyy-MM-dd, yyyy-dd-MM
    opened:{},
    centerCalculation:{},
    scrollEvent:{},
    cfg: {},
    nativeInit: function(clientId, val){
        var inputVal = document.getElementById(clientId).value;
        if (inputVal){
            this.setResetValue(clientId, inputVal);
        }
    }   ,
    init:function (clientId, cfgIn ) {
        var yrSel = cfgIn.yrInt;
        var mSel = cfgIn.mnthInt;
        var dSel = cfgIn.dateInt;
        var format = cfgIn.format;
        this.cfg[clientId] = cfgIn;
        
        var idPanel = clientId + "_bg";
        if (!document.getElementById(idPanel).className) {
            document.getElementById(idPanel).className = 'mobi-date-bg-inv';
        }
        var intDt = parseInt(dSel);
        var intMth = parseInt(mSel);
        var intYr = parseInt(yrSel);
        if (format) {
            this.pattern[clientId] = format;
        }
        this.opened[clientId] = false;
        //have to set the value controls to the correct integer
        var mnthEl = document.getElementById(clientId + "_mInt");
        var yrEl = document.getElementById(clientId + "_yInt");
        var dateEl = document.getElementById(clientId + "_dInt");
        if (mnthEl) {
            mnthEl.innerHTML = intMth;
        }
        if (yrEl) {
            yrEl.innerHTML = intYr;
        }
        if (dateEl) {
            dateEl.innerHTML = intDt;
        }
        this.updateDate(clientId);
        this.scrollEvent = 'ontouchstart' in window ? "touchmove" : "scroll";
    },
    mUp:function (clientId) {
        var mId = clientId + '_mInt';
        var mnthEl = document.getElementById(mId);
        if (mnthEl) {
            var mInt = this.getIntValue(mId);
            if (mInt == 12 || mInt == -1) {
                mnthEl.innerHTML = 1;
            }
            else {
                mnthEl.innerHTML = mInt + 1;
            }
            this.updateDate(clientId)
        };
    },
    mDn:function (clientId) {
        var mId = clientId + '_mInt';
        var mnthEl = document.getElementById(mId);
        if (mnthEl) {
            var mInt = this.getIntValue(mId);
            if (mInt == 1 || mInt == -1) {
                mnthEl.innerHTML = 12;
            }
            else {
                mnthEl.innerHTML = mInt - 1;
            }
        }
        this.updateDate(clientId);
    },
    checkForEmptyYear: function (yrEl) {
            var today = new Date();
            yrEl.innerHTML = today.getFullYear();

    },
    yUp:function (clientId, yrMin, yrMax) {
        var yId = clientId + '_yInt';
        var yrEl = document.getElementById(yId);
        if (yrEl) {
            var yInt = this.getIntValue(yId);
            if (yInt == -1) {
                this.checkForEmptyYear(yrEl);
            }
            else if (yInt != yrMax) {
                yrEl.innerHTML = yInt+1;
            }
            this.updateDate(clientId);
        }
    },
    yDn:function (clientId, yrMin, yrMax) {
        var yId = clientId + '_yInt';
        var yrEl = document.getElementById(yId);
        if (yrEl) {
            yInt = this.getIntValue(yId);
            var yInt = this.getIntValue(yId);
            if (yInt == -1) {
                this.checkForEmptyYear(yrEl);
            }
            else if (yInt != yrMin) {
                yrEl.innerHTML = yInt-1;
            }
            this.updateDate(clientId);
        }
    },
    dUp:function (clientId) {
        var dId = clientId + '_dInt';
        var dEl = document.getElementById(dId);
        var mEl = document.getElementById(clientId+'_mInt');
        var mInt = this.getIntValue(clientId + "_mInt");
        var yInt = this.getIntValue(clientId + "_yInt");
        var dInt = this.getIntValue(dId);
        var numDaysInMonth = this.daysInMonth(mInt, yInt);
        if (dEl) {
            if (dInt >= numDaysInMonth || dInt == -1) {
                dEl.innerHTML = 1;
            }
            else {
                dEl.innerHTML = dInt + 1;
            }
            this.updateDate(clientId);
        }

    },
    dDn:function (clientId) {
        var dId = clientId + '_dInt';
        var dEl = document.getElementById(dId);
        var mInt = this.getIntValue(clientId + "_mInt");
        var yInt = this.getIntValue(clientId + "_yInt");
        var dInt = this.getIntValue(dId);
        var numDaysInMonth = this.daysInMonth(mInt, yInt);
        if (dEl) {
            if (dInt == 1 || dInt > numDaysInMonth || dInt ==-1) {
                dEl.innerHTML = numDaysInMonth;
            }
            else {
                dEl.innerHTML = dInt - 1;
            }
            this.updateDate(clientId);
        }
    },
    updateDate:function (clientId) {
        var dId = clientId + "_dInt";
        var dEl = document.getElementById(dId);
        var mInt = this.getIntValue(clientId + "_mInt");
        var yInt = this.getIntValue(clientId + "_yInt");
        var dInt = this.getIntValue(dId);
        var upDate = this.validate(clientId, yInt, mInt, dInt);
        if (!upDate) {
            dInt = this.daysInMonth(mInt, yInt);
            dEl.innerHTML = dInt;
            upDate = this.validate(clientId, yInt, mInt, dInt);
        }
        var hiddenEl = document.getElementById(clientId + '_hidden');
        var inputEl = document.getElementById(clientId + '_input');
        hiddenEl.value = inputEl.value;
        this.setResetValue(clientId, inputEl.value);
        this.writeTitle(clientId, upDate);
    },

    writeTitle:function (clientId, date) {
        var titleElem = document.getElementById(clientId + '_title').childNodes[1];
        if (Object.prototype.toString.call(date) ==="[object Date]"){
           titleElem.innerHTML = ' ' + date.toDateString();
        }

    },

    daysInMonth:function (iMnth, iYr) {
        var aDate = new Date(iYr, iMnth, 0);
        return aDate.getDate();
    },

    validate:function (clientId, iY, iM, iD) {
        if (iY != parseInt(iY, 10) || iM != parseInt(iM, 10) || iD != parseInt(iD, 10)) return false;
        iM--;
        var cfgtmp = this.cfg[clientId];
        var yrMin = cfgtmp.yrMin;
        var yrMax = cfgtmp.yrMax;
        if (iY < yrMin){
            iY = yrMin;
            var yElem = document.getElementById(clientId + "_yInt");
            yElem.innerHTML=iY;
        }
        if (iY > yrMax){
            iY = yrMax;
        }
        var newDate = new Date(iY, iM, iD);
        if ((iY == newDate.getFullYear()) && (iM == newDate.getMonth()) && (iD == newDate.getDate())) {
            return newDate;
        }
        else return false;
    },

    getIntValue:function (id) {
        var element = document.getElementById(id);
        if (element) {
            var stringEl = element.innerHTML;
            if (stringEl == "NaN" || stringEl == "" )return -1;
            return parseInt(stringEl);
        } else return 1;
    },
    select:function (clientId, cfg) {
        var inputEl = document.getElementById(clientId + '_input');
        var hiddenEl = document.getElementById(clientId + '_hidden');
        var dInt = this.getIntValue(clientId + "_dInt");
        var mInt = this.getIntValue(clientId + "_mInt");
        var yInt = this.getIntValue(clientId + "_yInt");
        var dStr = dInt;
        var mStr = mInt;
        if (dInt < 10) {
            dStr = '0' + dInt;
        }
        if (mInt < 10) {
            mStr = '0' + mInt;
        }
        // default to american MM/dd/yyyy, pattern
        var dateStr = mStr + '/' + dStr + '/' + yInt;
        var myPattern = this.pattern[clientId];
        // compare '-' dash delimiter
        if (myPattern == 'MM-dd-yyyy') {
            dateStr = mStr + '-' + dStr + '-' + yInt;
        } else if (myPattern == 'yyyy-MM-dd') {
            dateStr = yInt + "-" + mStr + "-" + dStr;
        } else if (myPattern == 'yyyy-dd-MM') {
            dateStr = yInt + "-" + dStr + "-" + mStr;
        } else if (myPattern == 'dd-MM-yyyy') {
            dateStr = dStr + '-' + mStr + '-' + yInt;
        }
        // compare '/' dash delimiter
        else if (myPattern == 'MM/dd/yyyy') {
            dateStr = mStr + '/' + dStr + '/' + yInt;
        } else if (myPattern == 'yyyy/MM/dd') {
            dateStr = yInt + "/" + mStr + "/" + dStr;
        } else if (myPattern == 'yyyy/dd/MM') {
            dateStr = yInt + "/" + dStr + "/" + mStr;
        } else if (myPattern == 'dd/MM/yyyy') {
            dateStr = dStr + '/' + mStr + '/' + yInt;
        }
        hiddenEl.value = dateStr;
        inputEl.value = dateStr;

        this.dateSubmit(cfg, clientId);
        this.close(clientId);
    },
    dateSubmit: function(cfgIn, clientId) {
        this.cfg[clientId] = cfgIn;
        var dId = clientId + "_dInt";
        var dateElemId = clientId+"+input" ;
        var dateElem = document.getElementById(dateElemId);
        var mInt = this.getIntValue(clientId + "_mInt");
        var yInt = this.getIntValue(clientId + "_yInt");
        var dInt = this.getIntValue(dId);
        var updatedValue = this.validate(clientId, yInt, mInt, dInt);
        if (updatedValue){
            var dateInnerVal = new Date(updatedValue) ;
            var behaviors = cfgIn.behaviors;
            if (behaviors) {
                if (behaviors.event=='change') {
                    ice.ace.ab(behaviors);
                }
            }
        }
    },
    inputNative: function(clientId, behaviors, errorMessage){
        var dateElem = document.getElementById(clientId);
        var dateError = document.getElementById(clientId+"_error");
        var value = new Date(dateElem.value);
        ice.setFocus('');
        var dateMin = new Date(dateElem.min) ;
        var dateMax = new Date(dateElem.max);
        if (value < dateMin || value > dateMax){
           dateError.innerHTML = errorMessage;
           dateError.style.display="inherit";
        } else {
            dateError.style.display= "none";
            if (behaviors ) {
                ice.ace.ab(behaviors);
            }
        }
    },
    inputSubmit: function(clientId, cfg){
        if (this.opened[clientId]==true){
            return;
        }
        this.cfg[clientId]=cfg;
        ice.setFocus('');
        var errorMessage=cfg.errorMessage;
        var hiddenEl = document.getElementById(clientId + '_hidden');
        var inputEl = document.getElementById(clientId + '_input');
        var dateError = document.getElementById(clientId+"_error");
        if (inputEl && inputEl.value) {
            var value = new Date(inputEl.value);
            var yearVal = value.getFullYear();
            if (yearVal >= cfg.yrMin && yearVal <= cfg.yrMax){
                dateError.style.display= "none";
            }
            else {
                if (yearVal < cfg.yrMin) {
                    dateError.innerHTML = errorMessage;
                    dateError.style.display = "inherit";
                    value.setFullYear(cfg.yrMin);
                    inputEl.value = value.toDateString();
                }
                if (yearVal > cfg.yrMax) {
                    dateError.innerHTML = errorMessage;
                    dateError.style.display = "inherit";
                    value.setFullYear(cfg.yrMax);
                    inputEl.value = value.toDateString();
                }
            }
            hiddenEl.value = inputEl.value;
            var behaviors = cfg.behaviors;
            if (behaviors) { //will work as long as there is only a single event defined
                ice.ace.ab(behaviors);
            }
        } else {
            console.log(" year must be within "+cfg.yrMin+" and "+cfg.yrMax);
        }

    },
    toggle:function (clientId) {
        if (!this.opened[clientId]) {
            this.open(clientId);
        } else {
            this.close(clientId);
        }
    },
    open:function (clientId) {
        var idPanel = clientId + "_bg";
        var idPopPanel = clientId + "_popup";

        // add scroll listener
        this.centerCalculation[clientId] = function () {
            mobi.panelAutoCenter(idPopPanel);
        };

        ice.mobi.addListener(window, this.scrollEvent, this.centerCalculation[clientId]);
        ice.mobi.addListener(window, 'resize', this.centerCalculation[clientId]);

        // add visible style classes
		var overlay = document.getElementById(idPanel);
		overlay.style.width = Math.max(document.documentElement.scrollWidth, document.body.scrollWidth) + 'px';
		overlay.style.height = Math.max(document.documentElement.scrollHeight, document.body.scrollHeight) + 'px';
        overlay.className = "mobi-date-bg";
        document.getElementById(idPopPanel).className = "mobi-date-container ui-widget ui-widget-content";
        // set as visible.
        this.opened[clientId] = true;
        // calculate center for first view
        mobi.panelAutoCenter(idPopPanel);
    },
    close:function (clientId) {
        var idPanel = clientId + "_bg";

        // remove scroll listener
        if (window.removeEventListener) {
            window.removeEventListener(this.scrollEvent, this.centerCalculation[clientId], false);
            window.removeEventListener('resize', this.centerCalculation[clientId], false);
        } else { // older ie cleanup
            window.detachEvent(this.scrollEvent, this.centerCalculation[clientId], false);
            window.detachEvent('resize', this.centerCalculation[clientId], false);
        }
        // hide the dialog
        document.getElementById(idPanel).className = "mobi-date-bg-inv";
        document.getElementById(clientId + "_popup").className = "mobi-date-container-inv ui-widget ui-widget-content";
        this.opened[clientId] = false;
        this.centerCalculation[clientId] = undefined;
    },
    unload:function (clientId) {
        this.pattern[clientId] = null;
        this.opened[clientId] = null;
    } ,
    reset: function(clientId){
        if  (ice.ace){
            var value = ice.ace.resetValues[clientId];
            if (ice.ace.isSet(value)) {
                var dateElem = document.getElementById(clientId+"_input");
                var elem = document.getElementById(clientId);
                if (dateElem) {
                    dateElem.value = value;
                    var myPattern = mobi.datespinner.pattern[clientId];
                         // compare '-' dash delimiter
                    var month=1;
                    var day=1;
                    var year=2017;
                    var dDate = new Date(value);
                    var separator ='-' ;
                    if (value.indexOf('/')>=0){
                       separator = '/';
                    }
                    if (myPattern == "MM-dd-yyyy" || myPattern == 'MM/dd/yyyy') {
                        var vals = value.split(separator);
                        month = vals[0];
                        day=vals[1];
                        year = vals[2];
                    }else if (myPattern == 'yyyy-MM-dd' || myPattern == 'yyyy/MM/dd') {
                        var vals = value.split(separator);
                        month = vals[1];
                        day=vals[2];
                        year = vals[0];
                    } else if (myPattern == 'yyyy-dd-MM' || myPattern == 'yyyy/dd/MM') {
                        var vals = value.split(separator);
                        month = vals[2];
                        day=vals[1];
                        year = vals[0];
                    }  else if (myPattern =='dd-MM-yyyy' || myPattern == 'dd/MM/yyyy') {
                        var vals = value.split(separator);
                        month = vals[1];
                        day=vals[0];
                        year = vals[2];
                    } else {
                        month = dDate.getMonth()+1;
                        year = dDate.getFullYear();
                        day = dDate.getDay()+1;
                    }
                    var updatedValue = mobi.datespinner.validate(clientId, year, month, day);
                    mobi.datespinner.writeTitle(clientId, updatedValue);
                    var dayElem = document.getElementById(clientId+"_dInt");
                    dayElem.innerHTML = day;
                    var monElem =  document.getElementById(clientId+"_mInt");
                    monElem.innerHTML = month;
                    var yrElem =  document.getElementById(clientId+"_yInt");
                    yrElem.innerHTML = year;
                }
                if (elem){
                    elem.value=value;
                }
            }
        }
    } ,
    setResetValue: function(clientId, value){
        if (ice.ace){
            ice.ace.resetValues[clientId] = value ;
        }
    }  ,
    clear: function(clientId){
        if (ice.ace && typeof ice.ace.resetValues[clientId] == 'undefined') mobi.datespinner.setResetValue(clientId);
       	var element = document.getElementById(clientId);
       	var inputElem = document.getElementById(clientId + "_input");
       	if (element) {
            element.value="";
        }
        if (inputElem){
            inputElem.value="";
            var dayElem = document.getElementById(clientId+"_dInt");
           dayElem.innerHTML = "";
           var monElem =  document.getElementById(clientId+"_mInt");
           monElem.innerHTML = "";
            var yrElem =  document.getElementById(clientId+"_yInt");
            yrElem.innerHTML = "";
        }
    }
};

