/*
 * Copyright 2009-2017 C. Amengual
 */
var CA_hasClass = function(elm, classname){
	if(elm.className) {
		var list = elm.classList;
		for(var i=0; i<list.length; i++) {
			if(list.item(i) == classname) {
				return true;
			}
		}
	}
	return false;
}
function CA_addIndexEntry(elm, olIndex, hdrtag, itemid, idxentryclass){
	var indexEntry = document.createElement('li');
	if(idxentryclass) {
		indexEntry.className = idxentryclass;
	}
	olIndex.appendChild(indexEntry);
	var indexLink = document.createElement('a');
	indexLink.setAttribute('href', '#' + itemid);
	var textcontent = document.createTextNode(elm.textContent);
	indexLink.appendChild(textcontent);
	indexEntry.appendChild(indexLink);
	return indexEntry;
}
function CA_buildIndex(headertext, itemclass, hdrtag, subitemclass, subhdrtag, indexclass, olclass, idxentryclass){
	var items = document.getElementsByClassName(itemclass);
	var ndlen = items.length;
	var olIndex;
	if(ndlen != 0) {
		var divIndex=document.createElement('div');
		divIndex.className=indexclass;
		var firstitem = items.item(0);
		firstitem.parentNode.insertBefore(divIndex, firstitem);
		var header = document.createElement(hdrtag);
		header.className = itemclass;
		header.textContent = headertext;
		divIndex.appendChild(header);
		olIndex=document.createElement('ol');
		olIndex.setAttribute('class', olclass);
		divIndex.appendChild(olIndex);
		ndlen++;
	}
	for(var i=0; i<ndlen; i++){
		var elm=items.item(i);
		var itemid = elm.id;
		if(itemid) {
			if(elm.nodeName != hdrtag){
				elm = elm.getElementsByTagName(hdrtag).item(0);
			}
			var indexEntry = CA_addIndexEntry(elm, olIndex, hdrtag, itemid, idxentryclass);
			var olSubindex = null;
			var next = elm.nextSibling;
			while(next != null) {
				if(next.nodeType == 1) {
					if(CA_hasClass(next, itemclass)) {
						break;
					}
					if(next.id && CA_hasClass(next, subitemclass)) {
						if(olSubindex == null) {
							olSubindex=document.createElement('ol');
							indexEntry.appendChild(olSubindex);
						}
						CA_addIndexEntry(next, olSubindex, subhdrtag, next.id);
					}
				}
				next = next.nextSibling;
			}
		}
	}
}
var CA_stdBuildIndex = function() {
	CA_buildIndex('Index', 'tema', 'H3', 'subtema', 'H4', 'index', 'nestedidx', 'indexentry');
};
document.addEventListener("DOMContentLoaded", CA_stdBuildIndex);
