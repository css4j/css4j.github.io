#!/bin/sh
#
# Copyright © 2017-2022 C. Amengual
#
# This script generates the aggregated Javascript and CSS files used in this
# website. Some small CSS files are located in $CSSSRCDIR, but the main style
# sheets are at the site's root directory ($SITEDIR), for easier development.
#
# Javascript sources are at this directory ($SRCDIR), and the environment
# variables ending with "VER" are versioning suffixes.
#
#
# YUI compressor location
YUICOMP=${HOME}/java/lib/yuicompressor-2.4.8.jar
# Root of website
SITEDIR=${HOME}/www/css4j.github.io
# Source directories for JS and CSS
SRCDIR=${SITEDIR}/src
CSSSRCDIR=${SITEDIR}/src
# Contributed CSS and scripts
CONTRIBDIR=${SRCDIR}/contrib
PRISM=${CONTRIBDIR}/prism-markup-css-clike-groovy-java
PRISM_USAGE=${CONTRIBDIR}/prism-markup-css-clike-groovy-java
# Working directory
WORKDIR=$TMP/css4j.github
# Versioning
FAQJSVER=a
USAGEJSVER=d
USAGECSSVER=e
BASEJSVER=a
BASECSSVER=c
# Create working directory, if does not exist
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
cat $SRCDIR/copyright-prism-src.js ${PRISM}.js > ${SITEDIR}/js/code-${BASEJSVER}.js
cat $SRCDIR/copyright-prism-src.js ${SITEDIR}/js/indexbuilder-compr.js ${PRISM_USAGE}.js > ${SITEDIR}/js/usage-${USAGEJSVER}.js
# CSS
cat ${SITEDIR}/common.css ${SITEDIR}/normal.css > ${WORKDIR}/basic-a.css
cat ${WORKDIR}/basic-a.css ${CSSSRCDIR}/faq.css > ${WORKDIR}/faq-a.css
cat ${PRISM}.css ${WORKDIR}/basic-a.css ${CSSSRCDIR}/code.css > ${WORKDIR}/code-a.css
cat ${PRISM_USAGE}.css ${SITEDIR}/common.css ${SITEDIR}/normal.css ${CSSSRCDIR}/usage.css > ${WORKDIR}/usage-a.css
java -jar $YUICOMP ${WORKDIR}/basic-a.css --charset utf-8 > ${SITEDIR}/basic-${BASECSSVER}.css
java -jar $YUICOMP ${WORKDIR}/code-a.css --charset utf-8 > ${SITEDIR}/code-${BASECSSVER}.css
java -jar $YUICOMP ${WORKDIR}/faq-a.css --charset utf-8 > ${SITEDIR}/faq-${BASECSSVER}.css
java -jar $YUICOMP ${WORKDIR}/usage-a.css --charset utf-8 > ${SITEDIR}/usage-${USAGECSSVER}.css
# Remove work directory
if [ -r $WORKDIR ]
then rm -r $WORKDIR
fi
