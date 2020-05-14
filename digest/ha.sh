#!/bin/bash
#
# Generate digest files for ZIP distributions
#
# Files are created on the current directory
#
if [[ $# -eq 0 ]] ; then
	echo "No argument supplied (e.g. '/var/files/css4j-2.0.2.zip')"
	exit 1
fi
BNAME="${1##*/}"
BNAME="${BNAME%.*}"
BINPRE="/usr/bin"
echo "Creating digests for $1:"
$BINPRE/sha1sum -b $1|awk '{print $1}' >$BNAME.sha1
echo -n " ...$BNAME.sha1"
$BINPRE/sha256sum -b $1|awk '{print $1}' >$BNAME.sha256
echo -n " ...$BNAME.sha256"
$BINPRE/b2sum -b $1|awk '{print $1}' >$BNAME.b2
echo -n " ...$BNAME.b2"
$BINPRE/md5sum -b $1|awk '{print $1}' >$BNAME.md5
echo " ...$BNAME.md5"
