#makefile begins

JFLAGS = -g
JC = javac
JVM= java 
FILE=
.SUFFIXES: .java .class
.java.class:
        $(JC) $(JFLAGS) $*.java

CLASSES = \
        Test.java

# CLASSES = \
#         Experiment.java \
#         Block.java \
#         Spring.java \
#         PhysicsElement.java \
#         Simulator.java

MAIN = Test

default: classes

classes: $(CLASSES:.java=.class)

run: $(MAIN).class
	$(JVM) $(MAIN) $(FILE)

clean:
        $(RM) *.class
