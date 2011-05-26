#####################################################################
#
#   Makefile for Matrix Reducing program
#   By Stewart Bracken
#
#####################################################################


JAVASRC    = matrix.java Fraction.java GetOpt.java coordinate.java
SOURCES    = ${JAVASRC} Makefile README
ALLSOURCES = ${SOURCES}
MAINCLASS  = matrix
CLASSES    = matrix.class Fraction.class GetOpt.class coordinate.class
JARCLASSES = ${CLASSES} ${INNCLASSES}
JARFILE    = matrix
message	  =

all : ${JARFILE}

${JARFILE} : ${CLASSES}
	echo Main-class: ${MAINCLASS} >Manifest
	jar cvfm ${JARFILE} Manifest ${JARCLASSES}
	chmod +x ${JARFILE}
	- rm Manifest
 
%.class : %.java
	javac -Xlint $<
 
clean :
	- rm ${JARCLASSES}

spotless : clean
	- rm ${JARFILE}

again : 
	gmake --no-print-directory spotless ci all lis
   
commit :
	git add ${SOURCES}
	git commit -m '${message}'

push :
	git push -u origin master
   

   

   

   

   

