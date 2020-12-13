#!/bin/sh
#
# Copyright © 2017-2020 C. Amengual
#
YUICOMP=${HOME}/java/lib/yuicompressor-2.4.8.jar
SRCDIR=${HOME}/www/css4j.github.io/src
CONTRIBDIR=${SRCDIR}/contrib
PRISM=${CONTRIBDIR}/prism-markup-css-clike-java-shell
PRISM_USAGE=${CONTRIBDIR}/prism-markup-css-clike-java
LIBDIR=${HOME}/www/jslib
CSSSRCDIR=${HOME}/www/css4j.github.io/src
SITEDIR=${HOME}/www/css4j.github.io
WORKDIR=$TMP/css4j.github
FAQJSVER=a
USAGEJSVER=c
USAGECSSVER=c
BASECSSVER=c
# Work directory
if [ ! -d $WORKDIR ]
then mkdir $WORKDIR
fi
#if [ ! -r $TMP/yui-dom-event.js ]
#then /usr/bin/wget -q -O $TMP/yui-dom-event.js http://yui.yahooapis.com/combo?2.9.0/build/yahoo-dom-event/yahoo-dom-event.js
#fi
#if [ ! -r $TMP/yui-dom-event-connection.js ]
#then /usr/bin/wget -q -O $TMP/yui-dom-event-connection.js "http://yui.yahooapis.com/combo?2.9.0/build/yahoo-dom-event/yahoo-dom-event.js&2.9.0/build/connection/connection-min.js"
#fi
#if [ ! -r $TMP/yui-dom-event.js ] || [ ! -r $TMP/yui-dom-event-connection.js ]
#then echo "YUI not found. Exiting."
#     exit
#fi
cat $SRCDIR/copyright-src.js $SRCDIR/framebreak-src.js $SRCDIR/faq-src.js > ${WORKDIR}/faq-a.js
cat $SRCDIR/framebreak-src.js $SRCDIR/indexbuilder-src.js > ${WORKDIR}/indexbuilder-a.js
java -jar $YUICOMP --charset utf-8 ${WORKDIR}/faq-a.js > ${SITEDIR}/js/faq-${FAQJSVER}.js
java -jar $YUICOMP --charset utf-8 ${WORKDIR}/indexbuilder-a.js > ${SITEDIR}/js/indexbuilder-compr.js
cat $SRCDIR/copyright-prism-src.js ${SITEDIR}/js/indexbuilder-compr.js ${PRISM_USAGE}.js > ${SITEDIR}/js/usage-${USAGEJSVER}.js
# CSS
cat ${SITEDIR}/common.css ${SITEDIR}/normal.css > ${WORKDIR}/basic-a.css
cat ${WORKDIR}/basic-a.css ${CSSSRCDIR}/faq.css > ${WORKDIR}/faq-a.css
#cat ${PRISM}.css ${WORKDIR}/basic-a.css > ${WORKDIR}/code-a.css
cat ${PRISM_USAGE}.css ${SITEDIR}/common.css ${SITEDIR}/normal.css ${CSSSRCDIR}/usage.css > ${WORKDIR}/usage-a.css
java -jar $YUICOMP ${WORKDIR}/basic-a.css --charset utf-8 > ${SITEDIR}/basic-${BASECSSVER}.css
#java -jar $YUICOMP ${WORKDIR}/code-a.css --charset utf-8 > ${SITEDIR}/code-${BASECSSVER}.css
java -jar $YUICOMP ${WORKDIR}/faq-a.css --charset utf-8 > ${SITEDIR}/faq-${BASECSSVER}.css
java -jar $YUICOMP ${WORKDIR}/usage-a.css --charset utf-8 > ${SITEDIR}/usage-${USAGECSSVER}.css
# Remove work directory
if [ -r $WORKDIR ]
then rm -r $WORKDIR
fi
