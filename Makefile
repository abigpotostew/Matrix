#####################################################################
#
#   Makefile for Matrix Reducing program
#
#####################################################################


JAVASRC    = matrix.java Fraction.java GetOpt.java
SOURCES    = ${JAVASRC} Makefile
ALLSOURCES = ${SOURCES}
MAINCLASS  = matrix
CLASSES    = matrix.class Fraction.class GetOpt.class
JARCLASSES = ${CLASSES} ${INNCLASSES}
JARFILE    = matrix

all : ${JARFILE}

${JARFILE} : ${CLASSES}
	echo Main-class: ${MAINCLASS} >Manifest
	jar cvfm ${JARFILE} Manifest ${JARCLASSES}
	chmod +x ${JARFILE}
	- rm Manifest
 
%.class : %.java
 #cid + $<
	javac -Xlint $<
 
clean :
	- rm ${JARCLASSES}

spotless : clean
	- rm ${JARFILE}

again : 
	gmake --no-print-directory spotless ci all lis




# matrix: matrix.class
#  echo Main-class: matrix > Manifest
#  jar cvfm matrix Manifest matrix.class Fraction.class
#  rm Manifest
#  chmod +x matrix
# 
# matrix.class: matrix.java Fraction.java
#  javac -Xlint matrix.java
#  javac -Xlint matrix.java
# 
# clean:
#  rm -f matrix.class
# 
# spotless: clean
#  rm -f matrix matrix.class




   # $Id: Makefile,v 1.8 2011/02/14 20:51:12 - - bbracken $
   # Stewart Bracken
   # bbracken@ucsc.edu

   
   
   

   

   

   

   

