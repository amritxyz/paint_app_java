JAVA = java -cp
JAVAC = javac
JFLAGS = _JAVA_AWT_WM_NONREPARENTING=1
SRC = src/PaintApp.java
CLASS = PaintApp
BDIR = builds
BUILD = -d $(BDIR)

all:
	$(JFLAGS) $(JAVAC) $(BUILD) $(SRC)

run:
	$(JFLAGS) $(JAVA) $(BDIR) $(CLASS)

clean:
	rm -rf $(BDIR)/*.class
