if(!window.mobi){window.mobi={};}mobi.flipswitch={lastTime:0,init:function(a,g){if(g.transHack){var j=new Date().getTime();if((j-mobi.flipswitch.lastTime)<100){console.log("Double click suppression required");return;}mobi.flipswitch.lastTime=j;}this.id=a;this.cfg=g;this.flipperEl=g.elVal;this.singleSubmit=g.singleSubmit;this.event=g.event;var c=false;if(this.cfg.behaviors){c=true;}if(this.flipperEl){var f=this.flipperEl.className;var i="off";var b=this.flipperEl.children[0].className;var h=this.flipperEl.children[2].className;if(f.indexOf("-off ")>0){this.flipperEl.className="mobi-flipswitch mobi-flipswitch-on ui-widget";this.flipperEl.children[0].className="mobi-flipswitch-txt-on ui-state-active";this.flipperEl.children[2].className="mobi-flipswitch-txt-off";i=true;}else{this.flipperEl.className="mobi-flipswitch mobi-flipswitch-off ui-widget";this.flipperEl.children[0].className="mobi-flipswitch-txt-on";this.flipperEl.children[2].className="mobi-flipswitch-txt-off";i=false;}var e=this.id+"_hidden";var d=document.getElementById(e);if(d){d.value=i.toString();}if(this.singleSubmit){ice.se(this.event,this.id);}if(c){if(this.cfg.behaviors.activate){this.cfg.behaviors.activate();}}}}};