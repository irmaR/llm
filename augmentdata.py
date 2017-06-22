#!/usr/bin/python

import sys
import numpy as np
import matplotlib.pyplot as plt
import time
from mpl_toolkits.mplot3d import Axes3D
import re,os
import sys

dir="../Data/alldatamirrored/"
for file1 in os.listdir(dir):
	if file1.endswith(".txt") and (not file1.endswith("_mirror.txt")):
		try:
			with open(dir+file1) as f:				
				data = f.read()
				f.close()
				if len(data)>1:
					data = data.split('\n')
					
					data.pop()
					#print data
					predicate = [re.split(r'[,()]',row)[0] for row in data]
			
					arg1 = [re.split(r'[,()]',row)[1] for row in data]
					arg2 = [re.split(r'[,()]',row)[2] for row in data]
	
					x={}
					y={}
					z={}
					print file1
					ok=False
					for idx, val in enumerate(predicate):
						#print idx, val
						if val=='action' and (arg2[idx]=='t' or arg2[idx]=='g'):
							ok=True
							print val,arg2[idx]
							break
					if True:
						f2=open(dir+file1+"_mirror.txt",'w')
						for idx, val in enumerate(predicate):
							if val=='arm_x_cur':
								f2.write("arm_x_cur("+arg1[idx]+","+str(-float(arg2[idx]))+").\n")
							#	print "arm_x("+arg1[idx]+","+str(armx[-1])+").\n"
							elif val=='arm_y_cur':
								f2.write("arm_y_cur("+arg1[idx]+","+str(float(arg2[idx]))+").\n")
							elif val=='arm_x_next':
								f2.write("arm_x_next("+arg1[idx]+","+str(-float(arg2[idx]))+").\n")
							#	print "arm_x("+arg1[idx]+","+str(armx[-1])+").\n"
							elif val=='arm_y_next':
								f2.write("arm_y_next("+arg1[idx]+","+str(float(arg2[idx]))+").\n")
							elif val=='arm_z_cur':
								f2.write(data[idx]+'\n')
							elif val=='pos_x_cur':
								x[arg1[idx]]=arg2[idx]
								print data[idx]
								f2.write("pos_x_cur("+arg1[idx]+","+str(-float(x[arg1[idx]]))+").\n")
							elif val=='pos_y_cur':
								y[arg1[idx]]=arg2[idx]
								f2.write(data[idx]+'\n')
							elif val=='pos_x_next':
								x[arg1[idx]]=arg2[idx]
								f2.write("pos_x_next("+arg1[idx]+","+str(-float(x[arg1[idx]]))+").\n")
							elif val=='pos_y_next':
								y[arg1[idx]]=arg2[idx]
								f2.write(data[idx]+'\n')
							elif val=='pos_z_cur':
								z[arg1[idx]]=arg2[idx]
								f2.write(data[idx]+'\n')
							elif val=='displacement' and abs(float(arg2[idx]))>0 and ok:
								f2.write("displacement("+arg1[idx]+","+str(-float(arg2[idx]))+").\n")
							else:
								f2.write(data[idx]+'\n')					
						f2.close()
		except IOError as e:
			print e
			exc=True