'''
Created on Apr 1, 2016

@author: irma
'''

import argparse
import os

def parse_dc_clause(input,output):
    line_counter=0
    reset=True
    with open(input,'r') as f,open(output,'w') as f1:
        for line in f.readlines():
           line_counter+=1
           if not line.strip():
               reset=True
               f1.write("\n")
               continue
           nr_spaces_space=max(2,150-len(line))
           space = " " * nr_spaces_space
           if reset:
               #meaning we are in the first line
               new_line=line.rstrip(",")
               f1.write(line.rstrip()+ space+"writeln(("+str(line_counter)+","+new_line.rstrip().replace("~",",").replace(":=","")+")),\n" )
               reset=False
           else:
               if line.rstrip().endswith("."):
                   new_line=line.rstrip().rstrip(".")
                   #meaning last line   
                   f1.write(new_line+","+space+"writeln(("+str(line_counter)+","+new_line.rstrip().replace(".","")+")).\n" )
               else:
                  new_line=line.strip().rstrip(",")
                  f1.write(line.rstrip()+space+"writeln(("+str(line_counter)+","+new_line+")),\n" )


if __name__ == '__main__':
	'''
	parser = argparse.ArgumentParser(description='Process some integers.')
	parser.add_argument('-i', metavar='N')
	parser.add_argument('-o', metavar='N')
	args = parser.parse_args()
	print args
	
	'''
	dirmod="../Models/synthetic/"
	for file1 in os.listdir(dirmod):
		if file1.endswith(".dclause"):
			print file1
			parse_dc_clause(dirmod+file1,dirmod+file1+'.debug')
    