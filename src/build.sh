#!/bin/sh
#
# Copyright © 2017-2020 C. Amengual
#
YUICOMP=${HOME}/java/lib/yuicompressor-2.4.8.jar
SRCDIR=${HOME}/www/css4j.github.io/src
LIBDIR=${HOME}/www/jslib
CSSSRCDIR=${HOME}/www/css4j.github.io/src
SITEDIR=${HOME}/www/css4j.github.io
TMPDIR=/tmp
FAQJSVER=a
IDXBUILDERJSVER=b
BASECSSVER=a
# JS
if [ ! -d $TMPDIR/js ]
then mkdir $TMPDIR/js
fi
#if [ ! -r $TMPDIR/yui-dom-event.js ]
#then /usr/bin/wget -q -O $TMPDIR/yui-dom-event.js http://yui.yahooapis.com/combo?2.9.0/build/yahoo-dom-event/yahoo-dom-event.js
#fi
#if [ ! -r $TMPDIR/yui-dom-event-connection.js ]
#then /usr/bin/wget -q -O $TMPDIR/yui-dom-event-connection.js "http://yui.yahooapis.com/combo?2.9.0/build/yahoo-dom-event/yahoo-dom-event.js&2.9.0/build/connection/connection-min.js"
#fi
#if [ ! -r $TMPDIR/yui-dom-event.js ] || [ ! -r $TMPDIR/yui-dom-event-connection.js ]
#then echo "YUI not found. Exiting."
#     exit
#fi
cat $SRCDIR/copyright-src.js $SRCDIR/framebreak-src.js $SRCDIR/faq-src.js > $TMPDIR/js/faq-a.js
cat $SRCDIR/copyright-src.js $SRCDIR/framebreak-src.js $SRCDIR/indexbuilder-src.js > $TMPDIR/js/usage-a.js
java -jar $YUICOMP --charset utf-8 ${TMPDIR}/js/faq-a.js > ${SITEDIR}/js/faq-$FAQJSVER.js
java -jar $YUICOMP --charset utf-8 ${TMPDIR}/js/usage-a.js > ${SITEDIR}/js/usage-$IDXBUILDERJSVER.js
if [ -r $TMPDIR/js ]
then rm -r $TMPDIR/js
fi
# CSS
#cat $CSSSRCDIR/common.css $CSSSRCDIR/normal.css > $TMPDIR/basic-a.css
#cat $CSSSRCDIR/common.css $CSSSRCDIR/normal.css $CSSSRCDIR/faq.css > $TMPDIR/faq-a.css
#java -jar $YUICOMP $TMPDIR/basic-a.css -o $SITEDIR/css/basic-$BASECSSVER.css --charset utf-8
#java -jar $YUICOMP $TMPDIR/faq-a.css -o $SITEDIR/css/faq-$BASECSSVER.css --charset utf-8
