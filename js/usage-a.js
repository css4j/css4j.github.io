/*!
 * Copyright 2009-2017 C. Amengual.
 */
;var CA_break_frame=function(){if(self.location.hostname!=top.location.hostname){top.location.replace(window.location.href)}};CA_break_frame();var CA_hasClass=function(d,c){if(d.className){var b=d.classList;for(var a=0;a<b.length;a++){if(b.item(a)==c){return true}}}return false};function CA_addIndexEntry(h,e,b,c,f){var g=document.createElement("li");if(f){g.className=f}e.appendChild(g);var a=document.createElement("a");a.setAttribute("href","#"+c);var d=document.createTextNode(h.textContent);a.appendChild(d);g.appendChild(a);return g}function CA_buildIndex(s,b,f,g,c,a,k,u){var l=document.getElementsByClassName(b);var j=l.length;var n;if(j!=0){var p=document.createElement("div");p.className=a;var o=l.item(0);o.parentNode.insertBefore(p,o);var q=document.createElement(f);q.className=b;q.textContent=s;p.appendChild(q);n=document.createElement("ol");n.setAttribute("class",k);p.appendChild(n);j++}for(var r=0;r<j;r++){var d=l.item(r);var e=d.id;if(e){if(d.nodeName!=f){d=d.getElementsByTagName(f).item(0)}var t=CA_addIndexEntry(d,n,f,e,u);var h=null;var m=d.nextSibling;while(m!=null){if(m.nodeType==1){if(CA_hasClass(m,b)){break}if(m.id&&CA_hasClass(m,g)){if(h==null){h=document.createElement("ol");t.appendChild(h)}CA_addIndexEntry(m,h,c,m.id)}}m=m.nextSibling}}}}var CA_stdBuildIndex=function(){CA_buildIndex("Index","tema","H3","subtema","H4","index","nestedidx","indexentry")};document.addEventListener("DOMContentLoaded",CA_stdBuildIndex);