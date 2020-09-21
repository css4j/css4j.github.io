/*
 * Copyright © 2009,2010 C. Amengual.
 */
// Frame break stuff
var CA_break_frame = function() {
	if (self.location.hostname != top.location.hostname) {
		top.location.replace(window.location.href);
	}
};
CA_break_frame();
