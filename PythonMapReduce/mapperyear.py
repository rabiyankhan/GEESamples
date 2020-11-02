#!/usr/bin/env python
"""Mapper for finding the YEAR that cars have mostly been ticketed """

import sys
from datetime import datetime

def read_input(file):
    for line in file:
        # split the file into separate lineitems
        yield line.splitlines()

def main(separator='\t'):
    # input comes from STDIN (standard input)
    data = read_input(sys.stdin)
    for rows in data:
        cols = rows[0].split(",")
        year = cols[4].split("/")[2]
       
        # write the results to STDOUT (standard output);
        # what we output here will be the input for the
        # Reduce step, i.e. the input for reducer.py
        # map the YEAR OF ISSUE  (extracted from column E in input data)
        print (year +  separator + str(1))

if __name__ == "__main__":
    main()
