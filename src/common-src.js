/*
 * Copyright © 2009,2010 C. Amengual.
 */
var CA = {};
//
function CA_getText(elem) {
    if (document.all) {
		// IE
        return elem.innerText;
    } else {
        return elem.textContent;
    }
}
//
function is_js_search_engine() {
	// List of engines that may support Javascript to a limited degree
	var aEngines = ['Googlebot','Slurp','msnbot','Twiceler'];
	var ua = navigator.userAgent;
	for (var i in aEngines) {
		if(ua.indexOf(aEngines[i]) >= 0) {
		  return 1;
		}
	}
	return 0;
}
//
var CA_logOnConsole=function(o) {
	if(typeof(console) !== 'undefined' && console !== null) {
		console.log(o);
	}
};
//
var CA_noTranscoder=function() {
	return navigator.userAgent.indexOf('Google Wireless Transcoder')<0;
};
//
var CA_noRobotOrTranscoder=function() {
	return (is_js_search_engine()===0 && CA_noTranscoder());
};
//
var CA_getDocumentLanguage = function() {
	var ndList = document.childNodes;
	for(var i in ndList){
		if(ndList[i].nodeType==1 && ndList[i].nodeName=='HTML'){
			return ndList[i].getAttribute('lang');
		}
	}
	return 'en';
};
var CA_setDocumentLanguage = function() {
	window.ca_document_language = CA_getDocumentLanguage();
};
document.addEventListener("DOMContentLoaded", CA_setDocumentLanguage);
