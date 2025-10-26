/*
 Copyright ? 2017-2025 C. Amengual.
*/
var CA_break_frame=function(){self.location.hostname!=top.location.hostname&&top.location.replace(window.location.href)};CA_break_frame();function CA_showFaq(){this.style.cursor="auto";for(var b=this.parentNode.childNodes,a=0;a<b.length;a++)b[a].nodeType===1&&(b[a].style.display="block");b=this.getElementsByTagName("span");for(a=0;a<b.length;a++)b[a].className=="clkLeerMas"&&this.removeChild(b[a])}
function CA_hideFaq(){var b;location.hash&&(b=location.hash.slice(1));for(var a=document.getElementById("faqlist").childNodes,f=a.length,e=0;e<f;e++){var c=a[e];if(c.nodeName=="DIV"&&c.id!==b){window.faqEntry=c.childNodes;for(var d in faqEntry)faqEntry[d].nodeType===1&&(faqEntry[d].nodeName=="H4"?(c=document.createElement("span"),c.className="clkLeerMas",c.innerHTML=" ...",c.style.color="#A7A7FF",c.id="leerMas"+e,faqEntry[d].appendChild(c),faqEntry[d].addEventListener("click",CA_showFaq),faqEntry[d].style.cursor=
"pointer"):faqEntry[d].style.display="none")}}navigator.userAgent.indexOf("Google Wireless Transcoder")<0&&(document.getElementById("faqlisthelp").style.display="block")}document.addEventListener("DOMContentLoaded",CA_hideFaq);
