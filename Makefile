
ANTLR4=java -jar /thayerfs/courses/22spring/cosc057/workspace/antlr-4.9.3-complete.jar

all: MiniC.class

MiniCParser.java: MiniC.g4
	$(ANTLR4) MiniC.g4


MiniC.class: MiniCParser.java MiniC.java MiniCWalker.java TreeUtils.java
	javac *.java

clean: 
	rm -f M*.class M*tokens M*interp M*class MiniCBaseListener.java MiniCListener.java MiniCParser.java MiniCLexer.java
