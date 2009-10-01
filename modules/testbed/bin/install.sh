#!/bin/bash

# $Id install.sh $
# Author:
# Date:   2008-08-21
#
# Script for installing the testbed for use by the doms gui.
#
# USAGE: After unpacking, edit conf/config to suit your needs, and run this
# script.


#
# Sets up basic variables
#
SCRIPT_DIR=$(dirname $0)
pushd $SCRIPT_DIR > /dev/null
SCRIPT_DIR=$(pwd)
popd > /dev/null

# Import settings
source $SCRIPT_DIR/../config/conf.sh

BASEDIR=$SCRIPT_DIR/..

TOMCATZIP=`basename $BASEDIR/tomcat/*.zip`




function usage() {
    echo ""
    echo -n "Usage: install.sh  <install-dir>"
    echo ""
    exit 1
}

#
# Parses command line arguments.
# http://www.shelldorado.com/goodcoding/cmdargs.html
#
while getopts dD:f:p: opt
    do
    case "$opt" in
        h) usage;;
        \?) # unknown flag
             echo "Unrecognised option. Bailing out." 1>&2
             usage;;
    esac
done
shift $(expr $OPTIND - 1)

#
# Check for install-folder and potentially create it.
#
TESTBED_DIR=$@
if [ -z "$TESTBED_DIR" ]; then
    echo "install-dir not specified. Bailing out." 1>&2
    usage
fi
if [ -d $TESTBED_DIR ]; then
    echo ""
else
    mkdir $TESTBED_DIR
fi
pushd $@ > /dev/null
TESTBED_DIR=$(pwd)
popd > /dev/null
echo "Full path destination: $TESTBED_DIR"




# Setup a tomcat server (not starting it though)

# kill any running tomcats
#ps ax | grep org.apache.catalina.startup.Bootstrap | grep -v grep |\
# awk '{print $1}' | xargs kill >/dev/null 2>&1


cp $BASEDIR/tomcat/$TOMCATZIP $TESTBED_DIR/

pushd $TESTBED_DIR
unzip $TOMCATZIP
mv ${TOMCATZIP%.*} tomcat
rm $TOMCATZIP
popd



#Patch the config scripts
pushd $BASEDIR/tomcat
# sed/shell magic below according to  http://www.grymoire.com/Unix/Sed.html
# See section "Passing arguments into a sed script".
sed \
-e 's/\$TOMCATHTTP\$/'"$TOMCAT_HTTPPORT"'/g' \
-e 's/\$TOMCATSSL\$/'"$TOMCAT_SSLPORT"'/g' \
-e 's/\$TOMCATAJP\$/'"$TOMCAT_AJPPORT"'/g' \
-e 's/\$TOMCATSHUTDOWN\$/'"$TOMCAT_SHUTDOWNPORT"'/g' \
<server.xml.template >server.xml

#TODO
#cp $SCRIPT_DIR/tomcat/bin/*  $TESTBED_DIR/tomcat/bin

mv server.xml $TESTBED_DIR/tomcat/conf/
popd



# Install fedora including database
pushd $BASEDIR/fedora
# sed/shell magic below according to  http://www.grymoire.com/Unix/Sed.html
# See section "Passing arguments into a sed script".

sed \
-e 's|\$FEDORAADMIN\$|'"$FEDORAADMIN"'|g' \
-e 's|\$FEDORAADMINPASS\$|'"$FEDORAADMINPASS"'|g' \
-e 's|\$INSTALLDIR\$|'"$TESTBED_DIR"'/fedora|g' \
<fedora.properties.template >fedora.properties

java -jar fedora*.jar fedora.properties
rm fedora.properties
popd



pushd $TESTBED_DIR
cp fedora/install/fedora.war $TESTBED_DIR/tomcat/webapps
popd


# Install into tomcat: webservices
cp $BASEDIR/webservices/*.war $TESTBED_DIR/tomcat/webapps


# Make it possible to log into the tomcat web manager-interface
pushd $TESTBED_DIR/tomcat/conf
cp tomcat-users.xml tomcat-users-backup.xml

sed \
-e 's|<tomcat-users>|<tomcat-users>\
<role rolename="manager"/>\
<user username="tomcat" password="tomcat" roles="manager"/>|g' \
<tomcat-users-backup.xml >tomcat-users.xml

popd


#TODO: take care of Fedora validator hook..

#TODO: config webservices (ecm, bitstorage,..)


chmod +x $TESTBED_DIR/tomcat/bin/*.sh
# Start the tomcat server
$TESTBED_DIR/tomcat/bin/startup.sh

sleep 30

export FEDORA_HOME=$TESTBED_DIR/fedora

#TODO: provide initial objects to ingest
#sh $TESTBED_DIR/fedora/client/bin/fedora-ingest.sh dir $TESTBED_DIR/objects\
# 'info:fedora/fedora-system:FOXML-1.1'\
# localhost:$TOMCAT_HTTPPORT $FEDORAADMIN $FEDORAADMINPASS http

$TESTBED_DIR/tomcat/bin/shutdown.sh



