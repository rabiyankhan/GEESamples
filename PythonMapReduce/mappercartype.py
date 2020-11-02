#!/usr/bin/env python
"""Mapper for finding the types of cars that are most commonly ticketed"""

import sys

def read_input(file):
    for line in file:
        # split the file into separate lineitems
        yield line.splitlines()

def main(separator='\t'):
    # input comes from STDIN (standard input)
    data = read_input(sys.stdin)
    for rows in data:
        cols = rows[0].split(",")
        keycol = cols[0] + "|" + cols[1]
      
        # write the results to STDOUT (standard output);
        # what we output here will be the input for the
        # Reduce step, i.e. the input for reducer.py
        # map the VEHICLE MAKE (column H in input data)
        print (cols[7] +  separator + str(1))

if __name__ == "__main__":
    main()
