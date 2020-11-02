#!/usr/bin/env python
"""Reducer for finding *when* cars are most likely to get ticketed """

from itertools import groupby
from operator import itemgetter
import sys

def read_mapper_output(file, separator='\t'):
    for line in file:
        yield line.rstrip().split(separator, 1)

def main(separator='\t'):
    # input comes from STDIN (standard input)
    
    data = read_mapper_output(sys.stdin, separator=separator)
    result = {}
    
    # groupby groups multiple pairs by car color,
    # and creates an iterator that returns consecutive keys and their group:
    #   current_word - string containing a car type (the key)
    #   group - iterator yielding all ["&lt;current_word&gt;", "&lt;count&gt;"] items
    for current_word, group in groupby(data, itemgetter(0)):
        try:
            total_count = sum(int(count) for current_word, count in group)
            result[current_word] = total_count
        except ValueError:
            # count was not a number, so silently discard this item
            pass

    sortedresult = sorted(result.items(), key = lambda kv: (kv[1], kv[0]), reverse = True) 
    (issuedate, ticketcount) = next(iter(sortedresult))
    print("The date when most cars are ticketed is : " +issuedate)
   
if __name__ == "__main__":
    main()
