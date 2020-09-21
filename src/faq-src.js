/*
 * Copyright © 2009-2017 C. Amengual
 */
function CA_showFaq(){
	this.style.cursor='auto';
	var elist=this.parentNode.childNodes;
	for(var i=0; i<elist.length; i++){
		if(elist[i].nodeType===1){
			elist[i].style.display='block';
		}
	}
	elist=this.getElementsByTagName('span');
	for(i=0;i<elist.length;i++){
		if(elist[i].className=='clkLeerMas'){
			this.removeChild(elist[i]);
		}
	}
}
function CA_hideFaq(){
	var pghash;
	if(location.hash) {
		pghash = location.hash.slice(1);
	}
	var ndList = document.getElementById('faqlist').childNodes;
	var ndlen = ndList.length;
	for(var i=0; i<ndlen; i++){
		var elm=ndList[i];
		if(elm.nodeName=='DIV' && elm.id !== pghash){
			window.faqEntry=elm.childNodes;
			for(var j in faqEntry){
				if(faqEntry[j].nodeType===1){
					if(faqEntry[j].nodeName=='H4'){
						var divLeerMas=document.createElement('span');
						divLeerMas.className='clkLeerMas';
						divLeerMas.innerHTML=' ...';
						divLeerMas.style.color = '#A7A7FF';
						divLeerMas.id='leerMas'+i;
						faqEntry[j].appendChild(divLeerMas);
						faqEntry[j].addEventListener('click', CA_showFaq);
						faqEntry[j].style.cursor='pointer';
					}else{
						faqEntry[j].style.display='none';
					}
				}
			}
		}
	}
	if(navigator.userAgent.indexOf('Google Wireless Transcoder')<0){
		var eHelp=document.getElementById('faqlisthelp');
		eHelp.style.display='block';
	}
}
document.addEventListener("DOMContentLoaded", CA_hideFaq);
