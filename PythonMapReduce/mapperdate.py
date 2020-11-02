#!/usr/bin/env python
"""Mapper for finding when cares are most likely to get ticketed """

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
        # map the ISSUE DATE (column E in input data)
        print (cols[4] +  separator + str(1))

if __name__ == "__main__":
    main()
