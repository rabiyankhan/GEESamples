#!/usr/bin/env python
"""Mapper for finding the color for cars that are mostly ticketed"""

import sys
import re

def read_input(file):
    for line in file:
        # split the file into separate lineitems
        yield line.splitlines()

def main(separator='\t'):
    # input comes from STDIN (standard input)
    data = read_input(sys.stdin)
    for rows in data:
        print(rows)
        cols = rows[0].split(",")
      
       
        # write the results to STDOUT (standard output);
        # what we output here will be the input for the
        # Reduce step, i.e. the input for reducer.py
        # map the VEHICLE COLOR (column AH in input data)
        print (cols[33] +  separator + str(1))

if __name__ == "__main__":
    main()
