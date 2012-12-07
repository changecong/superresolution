CC=gcc
CFLAGS=-c -Wall -g 
LDFLAGS=
LFLAGS=-lm
SOURCES=$(wildcard *.c)
OBJECTS=$(SOURCES:.c=.o)
EXECUTABLE=main

all: $(SOURCES) $(EXECUTABLE)

$(EXECUTABLE): $(OBJECTS)
	$(CC) $(LDFLAGS) $(OBJECTS) -o $@ $(LFLAGS)

.cpp.o:
	$(CC) $(CFLAGS) $< -o $@

test:
	./$(EXECUTABLE)

clean:
	rm -rf *.o $(EXECUTABLE) test.bmp
