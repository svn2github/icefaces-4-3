if (!window['ice']) window.ice = {};
if (!window.ice['ace']) window.ice.ace = {};

ice.ace.ColorEntry = function(id, cfg) { 
    var behavior, altFieldVal; 
    this.id = id;
    this.cfg = cfg; 
    this.jqId = ice.ace.escapeClientId(id); 
    this.jqElId =  this.jqId + '_input'; 
    this.jq = ice.ace.jq(this.jqElId); 
    this.options = cfg;
    this.colorpickerFn = "colorpicker";
    if (this.options.buttonText){
        this.options.buttonImage='';
    }
    if (this.options.color && !this.options.inline) { 
        this.jq.value = this.options.color; 
    } 
    //create or update colrEntry 
    this.jq.colorpicker(this.cfg); 
    ice.ace.ColorEntry.instances[id] = this;
    ice.ace.setResetValue(this.id, this.options.color);  
    var initialColor = ice.ace.ColorEntry.initialColor[id] ||null;
    if ( this.options.inline && initialColor !=null){  //workaround for inline to set preview of previous color
        var PREVIOUS_COLOR_VAL_INLINE = ".ui-colorpicker-preview-initial";
        var initElem = ice.ace.jq(this.jqElId).find(PREVIOUS_COLOR_VAL_INLINE);
        if (initElem){
            initElem.css("backgroundColor", initialColor) ;
        }else {
            initElem.css("backgroundColor", transparent);
        }
    }
};
 
ice.ace.ColorEntry.instances = {}; // keep track of initialized instances
ice.ace.ColorEntry.initialColor = {}; //keep track of initial or previous color

ice.ace.ColorEntry.prototype.configureLocale = function() {
    var localeSettings = ice.ace.locales[this.cfg.options.locale];
    if(localeSettings) {
        for(var setting in localeSettings) {
            this.cfg[setting] = localeSettings[setting];
        }
    }
};

ice.ace.ColorEntry.prototype.showPopup = function(){
    this.jq.colorpicker(this.cfg);
    this.jq.colorpicker('open');
};

ice.ace.ColorEntry.prototype.enable = function() {
    this.jq.colorpicker('enable');
};

ice.ace.ColorEntry.prototype.disable = function() {
    this.jq.colorpicker('disable');
};

ice.ace.ColorEntry.prototype.destroy = function() {
    if (this.jq && this.jq.colorpicker){
        this.jq.colorpicker("destroy");
    }
    this.jq =  null;
};

ice.ace.ColorEntryInit = function( cfg) {
    ice.ace.jq().ready(function() {
        var options = cfg.options;
        var INPUT_STYLE_CLASS = "ui-inputfield ui-widget ui-state-default ui-corner-all ui-colorpicker-input";
        var INPUT_EMPTY_STYLE_CLASS = "ui-inputfield ui-widget ui-state-default ui-corner-all";
        var BUTTON_STYLE_CLASS = "ui-button ui-widget ui-state-default ui-corner-all ui-colorpicker-buttonColorize ";
        var BUTTON_TEXT_STYLE_CLASS = "ui-button-text-only";
        var PREVIOUS_COLOR_VAL_INLINE = ".ui-colorpicker-preview-initial";
        var id = options.id;
        var behaviors = cfg.behaviors || null;
        var positionId = ice.ace.escapeClientId(id);
        var inputSelector = (ice.ace.escapeClientId(id) + "_input") ;
        var buttonSelector = positionId + '> span';
        var input = ice.ace.jq(inputSelector);
        var hidden = ice.ace.jq(ice.ace.escapeClientId(id) + "_hidden");
        var trigger = null;
        var colorFormat = options.colorFormat;
        var allowEmpty = options.showNoneButton;
        var showOn = options.showOn || "focus";
        var buttonText = options.buttonText || "";
        var buttonImage = options.buttonImage;
        var isButtonTextColorize = false;
        var isButtonText = false;
        if (buttonText.length > 0){
            buttonImage = '';
            isButtonText = true;
            if (cfg.buttonColorize==true){
                isButtonTextColorize=true;
            }
        }
        var buttonImageOnly = options.buttonImageOnly || false;
        var myposition={
            of: positionId
        } ;
        if (buttonImageOnly){
            options.position=myposition;
        }
        var initEltSet = ice.ace.jq();
        var inline = (options.inline) || false;
        if (inline && !options.parts) {  //default inline component
            options.parts = ['preview', 'map', 'bar'];
            if (options.title) {
                options.parts.push("header");
            }
            if (colorFormat && colorFormat.indexOf("HEX") > -1) {
                options.parts.push("hex");
            }
            options.part = {
                map: {size: 128},
                bar: {size: 128}
            };
            options.layout = {
                preview: [0, 0, 1, 1],
                hex: [1, 0, 1, 1],
                map: [0, 1, 1, 1],
                bar: [1, 1, 1, 1]
            };
            options.hsv = false;
            options.rgb = false;
        }

        if (options.colorFormat){
            if (options.colorFormat == 'HEX3') {
                options.colorFormat = ['HEX3', 'HEX']
            }
            if (options.colorFormat == 'EXACT') {
                options.colorFormat = ['EXACT', 'NAME']
            }
            if (options.colorFormat == 'HSL') {
               options.colorFormat = ['HSL', 'HSLA']
            }
        }
        /* add function callbacks to options */
        function getHexValue(newColor) {
            var hexField = ice.ace.jq('.ui-colorpicker-hex-input');
            if (hexField) {
                newColor = "#" + hexField.val();
            }
            return newColor;
        } ;
        function setColorBar(color) {
            var newColor = color.formatted;
            if (allowEmpty && !color.formatted   ) {
                input.attr('class', INPUT_EMPTY_STYLE_CLASS);
                input.value = '';
                return;
            } else if (colorFormat.indexOf("HEX") > -1) {
                newColor = "#" + newColor;
            } else if (colorFormat.indexOf("HSL")> -1){
                newColor = getHexValue(newColor);
                var hiddenHexField =ice.ace.jq(ice.ace.escapeClientId(id + "_hiddenHex"));
                if (hiddenHexField){
                    hiddenHexField.attr('value',newColor);
                }
            }
            if (color.formatted ) {
                if (!isButtonText) {
                    input.attr('class', INPUT_STYLE_CLASS);
                } else{
                    var buttonElem = input.siblings(':button');
                    if (!buttonElem){
                        buttonElem = ice.ace.jq(buttonSelector);
                    }
                    if (buttonElem && isButtonTextColorize){
                        buttonElem.css('border-color', newColor) + " !important";
                        buttonElem.attr('class', BUTTON_STYLE_CLASS);
                    }
                }
            }
            input.value = color.formatted;
            input.css({"border-left-color": newColor});
            var borderRule = "border-left-color: " + newColor + " !important";
            input.attr('style', borderRule);
        };

        var okFn = function(event, color){ 
            if ( color.formatted=="false"){
                return;
            }
            ice.ace.setResetValue(this.id, color.formatted);  
            setColorBar(color);
            if (behaviors && behaviors.change) { 
                var inputChange = behaviors.change;
                ice.ace.ab(inputChange); 
            } 
         } ;
        var cancelFn = function(event, color){
            if (color.formatted){
                setColorBar(color);
            }
        } ;
        var selectFn = function(event, color){
            if (color.formatted=="false"){
               // console.log(" The current widget does not support the color format of "+colorFormat);
              //  setColorBar(null); //could be none button
                return;
            }
            var colorFormatted = color.formatted;
            if (!inline){  //set
                setColorBar(color);
                return;
            }
            var prevElem = ice.ace.jq(positionId).find(PREVIOUS_COLOR_VAL_INLINE);
          //  var initElem = ice.ace.jq(positionId + 'span'+PREVIOUS_COLOR_VAL_INLINE) ;
            if (prevElem) {
                var initialColor =  prevElem.css('backgroundColor');
                ice.ace.ColorEntry.initialColor[id]  = initialColor;
            }
            ice.ace.setResetValue(this.id, color.formatted);  
            ice.ace.jq(ice.ace.escapeClientId(id) + "_hidden").val(colorFormatted);
            ice.ace.jq(ice.ace.escapeClientId(id) + "_hidden2").val(colorFormatted);
            if (behaviors && behaviors.change) {
                ice.ace.ab(behaviors.change);
            }
        };
        if (!inline){
            options.ok = okFn;
        }
        options.select = selectFn;
        options.cancel=cancelFn;

        var create = function(){
            var widget=ice.ace.create("ColorEntry", [id,options]);
            ice.onElementUpdate(id, function(){
                   widget.destroy();
                   initEltSet.remove();
            });
            return widget;
        };
        var initAndShow = function(){
            create();
            if (trigger){
                  trigger.remove();
            }
            if (ice.ace.instance(id)){
                ice.ace.instance(id).jq[ice.ace.instance(id).colorpickerFn]("open");
            } else {
                ice.ace.ColorEntry.instances[id].showPopup();
            }
        };
        var initButtonAndShow = function(){
            if (trigger){
                trigger.remove();
            }
            create();
            if (ice.ace.instance(id) && ice.ace.instance(id).colorpicker){
                ice.ace.instance(id).jq[ice.ace.instance(id).colorpickerFn]("open");
            }  else {
                ice.ace.ColorEntry.instances[id].showPopup();
            }

        } ;
        if (inline){
            if (trigger) trigger.remove();
            return create();
        }
		// if instance was previously initialized, create right away and return
		if (ice.ace.ColorEntry.instances[id]) {
            var widget = ice.ace.ColorEntry.instances[id];
            if (widget){
                widget.destroy();
            }
            create();
            if (options.color && buttonText && isButtonTextColorize){
                setColorBar(options.color);
            }
			return;
		}
        if (buttonImageOnly){
            var buttonImageOnlyinputClass="ui-colorpicker-buttonImageOnly";
            input.attr("class", buttonImageOnlyinputClass);
        }
        initEltSet = initEltSet.add(input);
        if (!inline && ice.ace.jq.inArray(showOn, ["button","all", "both", "click"]) >= 0) {
            if (buttonImageOnly) {
                trigger = ice.ace.jq('<img/>',{
                    src: buttonImage,
                    'class': buttonImageOnlyinputClass
                });
                ice.ace.jq(positionId).on('click', trigger, function(){
                    initButtonAndShow();
                });
                trigger.insertAfter(inputSelector);
            }
            if (buttonText && buttonText != "" ) {
                trigger= ice.ace.jq("<button />");
                var buttonClass= BUTTON_STYLE_CLASS;
                if (!buttonImage){
                    buttonClass = buttonClass + BUTTON_TEXT_STYLE_CLASS;
                }
                trigger.attr("class", buttonClass);
                var spanElem = ice.ace.jq('<span />');
                spanElem.attr('class', 'ui-button-text');
                spanElem.text(buttonText);
                trigger.append(spanElem);
                ice.ace.jq(positionId).on('click', trigger, function(){
                    initButtonAndShow();
                });
                trigger.insertAfter(inputSelector);
            }
        }

        if (!inline && ice.ace.jq.inArray(showOn, ["focus"]) >= 0) {
            setTimeout(function(){
                input.one("focus", initAndShow);
            }, 350);
            initEltSet = initEltSet.add(input);
        }
        ice.onElementUpdate(id, function() {
            // .remove cleans jQuery state unlike .unbind
            initEltSet.remove();
        });
    });
};

ice.ace.ColorEntry.clear = function(id) {
	var instance = ice.ace.instanceNoLazyInit(id);
	if (instance) instance.setColor(null);
	var input = ice.ace.jq(ice.ace.escapeClientId(id + "_input"));
    var hidden = ice.ace.jq(ice.ace.escapeClientId(id+"_hidden2"));
    if (hidden)hidden.val('');
    if (input)input.val('');
};

ice.ace.ColorEntry.reset = function(id, color) {
	var instance = ice.ace.instanceNoLazyInit(id);
	if (instance) {
		var value = ice.ace.resetValues[id];
		if (ice.ace.isSet(value)) instance.setColor(value);
	}
};
