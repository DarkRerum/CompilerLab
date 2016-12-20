all: compile run

compile:
	mkdir -p ./bin
	javac -d ./bin ./src/com/rerum/compiler/*.java
run:
	java -cp ./bin com.rerum.compiler.Main
