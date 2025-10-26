#!/bin/sh
#
# Copyright © 2017-2025 C. Amengual
#
# This script generates the aggregated Javascript and CSS files used in this
# website. Some small CSS files are located in $CSSSRCDIR, but the main style
# sheets are at the site's root directory ($SITEDIR), for easier development.
#
# Javascript sources are at this directory ($SRCDIR), and the environment
# variables ending with "VER" are versioning suffixes.
#
#

# Google Closure compiler location
CLOSURE_COMP=${HOME}/java/lib/closure-compiler.jar
# CSS4J minifier location
CSS4J=${HOME}/java/lib/css4j-6.1.1-alldeps.jar
# Root of website
SITEDIR=${HOME}/www/css4j.github.io
# Source directories for JS and CSS
SRCDIR=${SITEDIR}/src
CSSSRCDIR=${SITEDIR}/src
# Contributed CSS and scripts
CONTRIBDIR=${SRCDIR}/contrib
PRISM=${CONTRIBDIR}/prism-markup-css-clike-gradle-java
PRISM_USAGE=${CONTRIBDIR}/prism-markup-css-clike-gradle-java-shell
# Working directory
WORKDIR=$TMP/css4j.github
# Versioning
FAQJSVER=a
USAGEJSVER=e
USAGECSSVER=f
BASEJSVER=a
BASECSSVER=c

# Create working directory, if does not exist
if [ ! -d $WORKDIR ]
then mkdir $WORKDIR
fi

# Check for minifiers
if [ ! -r ${CLOSURE_COMP} ]
then wget -q -O ${CLOSURE_COMP} https://repo1.maven.org/maven2/com/google/javascript/closure-compiler/v20250820/closure-compiler-v20250820.jar
fi
if [ ! -r ${CSS4J} ]
then wget -q -O ${CSS4J} https://github.com/css4j/css4j/releases/download/v6.1.1/css4j-6.1.1-alldeps.jar
fi
if [ ! -r ${CLOSURE_COMP} ] || [ ! -r ${CSS4J} ]
then echo "Either closure compiler or css4j weren't found. Exiting."
     exit
fi

cat $SRCDIR/copyright-src.js $SRCDIR/framebreak-src.js $SRCDIR/faq-src.js > ${WORKDIR}/faq-a.js
cat $SRCDIR/framebreak-src.js $SRCDIR/indexbuilder-src.js > ${WORKDIR}/indexbuilder-a.js
java -jar $CLOSURE_COMP --js ${WORKDIR}/faq-a.js --js_output_file ${SITEDIR}/js/faq-${FAQJSVER}.js
java -jar $CLOSURE_COMP --js ${WORKDIR}/indexbuilder-a.js --js_output_file ${SITEDIR}/js/indexbuilder-compr.js
cat $SRCDIR/copyright-prism-src.js ${PRISM}.js > ${SITEDIR}/js/code-${BASEJSVER}.js
cat $SRCDIR/copyright-prism-src.js ${SITEDIR}/js/indexbuilder-compr.js ${PRISM_USAGE}.js > ${SITEDIR}/js/usage-${USAGEJSVER}.js
# CSS
cat ${SITEDIR}/common.css ${SITEDIR}/normal.css > ${WORKDIR}/basic-a.css
cat ${WORKDIR}/basic-a.css ${CSSSRCDIR}/faq.css > ${WORKDIR}/faq-a.css
cat ${PRISM}.css ${WORKDIR}/basic-a.css ${CSSSRCDIR}/code.css > ${WORKDIR}/code-a.css
cat ${PRISM_USAGE}.css ${SITEDIR}/common.css ${SITEDIR}/normal.css ${CSSSRCDIR}/usage.css > ${WORKDIR}/usage-a.css
java -jar $CSS4J ${WORKDIR}/basic-a.css --charset utf-8 > ${SITEDIR}/basic-${BASECSSVER}.css
java -jar $CSS4J ${WORKDIR}/code-a.css --charset utf-8 > ${SITEDIR}/code-${BASECSSVER}.css
java -jar $CSS4J ${WORKDIR}/faq-a.css --charset utf-8 > ${SITEDIR}/faq-${BASECSSVER}.css
java -jar $CSS4J ${WORKDIR}/usage-a.css --charset utf-8 > ${SITEDIR}/usage-${USAGECSSVER}.css
# Remove work directory
if [ -r $WORKDIR ]
then rm -r $WORKDIR
fi
