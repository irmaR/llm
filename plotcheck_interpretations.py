#!/usr/bin/python

import sys
import numpy as np
import matplotlib.pyplot as plt
import time
from mpl_toolkits.mplot3d import Axes3D
import re
import sys
#plt.ion()

import glob, os
dirf='balancedsynthetic'
os.chdir('../Data/'+dirf+'/')

plot=False

a=1


dx={}
dy={}
dz={}
armx=[0.,0.]
army=[0.,0.]
armz=[0.,0.]
action=[0,'t',0.0]
ok=False
removelist="rm "
X=[]

mirror=False
for file in glob.glob("*.txt"):
	if (not mirror) and (file.endswith("_mirror.txt")):
		continue
	try:
		p=plt.subplot(1,1,1)
		p.clear()
		fig = plt.gcf()
		plt.gca().cla()
		p.set_xlim(-0.3,0.3)
		p.set_ylim(0,0.5)
		f=open(file)
		data = f.read()
		f.close()
		if len(data)>1:
			print file
			#print data
			data = data.split('\n')
			ok=True
			data.pop()
			
			predicate = [re.split(r'[,()]',row)[0] for row in data]

			arg1 = [re.split(r'[,()]',row)[1] for row in data]
			arg2 = [re.split(r'[,()]',row)[2] for row in data]

			x={}
			y={}
			z={}
			displacement={}
	#		ax.autoscale(False)
			for idx, val in enumerate(predicate):
				#print idx, val
				if val=='action':
					action[0]=int(arg1[idx][3])
					action[1]=arg2[idx]
					print action
				if val=='displacement':
					displacement[int(arg1[idx][3])]=float(arg2[idx])
				if val=='arm_x_cur':
					armx[0]=float(arg2[idx])
			
				#	print "arm_x("+arg1[idx]+","+str(armx[-1])+").\n"
				elif val=='arm_y_cur':
					army[0]=float(arg2[idx])
			
				elif val=='arm_z_cur':
					armz[0]=float(arg2[idx])
					
				elif val=='arm_x_next':
					armx[1]=float(arg2[idx])
			
				elif val=='arm_y_next':
					army[1]=float(arg2[idx])
					
				elif val=='arm_z_next':
					armz[1]=float(arg2[idx])

				elif val=='pos_x_cur':
					if not (int(arg1[idx][3]) in x):
						x[int(arg1[idx][3])]=[None,None]
					x[int(arg1[idx][3])][0]=float(arg2[idx])
					print 'pos_x_cur', arg1[idx],arg2[idx]
				elif val=='pos_y_cur':
					if not (int(arg1[idx][3]) in y):
						y[int(arg1[idx][3])]=[None,None]
					y[int(arg1[idx][3])][0]=float(arg2[idx])
				elif val=='pos_z_cur':
					if not (int(arg1[idx][3]) in z):
						z[int(arg1[idx][3])]=[None,None]
					z[int(arg1[idx][3])][0]=float(arg2[idx])
					
				elif val=='pos_x_next':
					if not (int(arg1[idx][3]) in x):
						x[int(arg1[idx][3])]=[None,None]
					x[int(arg1[idx][3])][1]=float(arg2[idx])
					print 'pos_x_next', arg1[idx],arg2[idx]
			
				elif val=='pos_y_next':
					if not (int(arg1[idx][3]) in y):
						y[int(arg1[idx][3])]=[None,None]
					y[int(arg1[idx][3])][1]=float(arg2[idx])
			
				elif val=='pos_z_next':
					if not (int(arg1[idx][3]) in z):
						z[int(arg1[idx][3])]=[None,None]
					z[int(arg1[idx][3])][1]=float(arg2[idx])

			#p=plt.subplot(5,5,a)
			rem=False
			datarow=[]
			namerow=""
			for key in sorted(x.iterkeys()):
				datarow+=x[key][1:2]
				datarow+=y[key][1:2]
				namerow+='x_next'+str(key)+','
				namerow+='y_next'+str(key)+','
			for key in sorted(x.iterkeys()):
				datarow+=x[key][0:1]
				datarow+=y[key][0:1]
				namerow+='x_cur'+str(key)+','
				namerow+='y_cur'+str(key)+','
			for key in sorted(x.iterkeys()):
				if(action[0]==key):
					namerow+='action'+str(action[0])+','
					if action[1]=='g':
						datarow+=[0.,0.,0.,0.,1.]
					elif action[1]=='t':
						datarow+=[0.,0.,0.,1.,0.]
					elif action[1]=='p':
						datarow+=[0.,0.,1.,0.,0.]
					elif action[1]=='b':
						datarow+=[0.,1.,0.,0.,0.]						
				else:
					datarow+=[1.,0.,0.,0.,0.]
					namerow+='action'+str(key)+','
				if key in displacement:
					datarow+=[displacement[key]]
					namerow+='displacement'+str(key)+','
				else:
					print 'err',key,displacement
					#exit()
				plt.plot(x[key],y[key],marker='o')
#				plt.arrow(x[key][0], y[key][0], x[key][1]-x[key][0], y[key][1]-y[key][0], head_width=0.01, head_length=0.01, fc='k', ec='k')
				circle1=plt.Circle((x[key][0],y[key][0]),0.03,color='b',fill=False)
				fig.gca().add_artist(circle1)
				if (None in x[key]) or (None in y[key]):
					rem=True
				
			if not (action[0] in x):
				rem=True
			if len(displacement)!=len(x):
				rem=True
#			ax.plot(armx,army,color='blue',marker='x')
	#		ax.scatter(xgoal,ygoal,color='black',marker='x')
			print armx,army
			a=a+1

			#p.plot(armx,army,color='black',marker='x')
			#p.arrow(armx[0], army[0], armx[1]-armx[0], army[1]-army[0], head_width=0.01, head_length=0.01, fc='k', ec='k')
			print 'displacement',displacement,len(displacement)
			print 'x',x
			print 'y',y
			if rem:
				removelist+=file+' '
			else:
				print len(X)
				print datarow
				print namerow
				if action[1]=='t' or action[1]=='g':
					print 'a',x[action[0]][0], y[action[0]][0], displacement[action[0]]
					plt.arrow(x[action[0]][0], y[action[0]][0], displacement[action[0]], 0, head_width=0.01, head_length=0.01, fc='k', ec='k')
				elif action[1]=='p' or action[1]=='b':
					plt.arrow(x[action[0]][0], y[action[0]][0], 0, displacement[action[0]], head_width=0.01, head_length=0.01, fc='k', ec='k')
				X+=	[datarow]
				#if((len(X)-1) in [ 89, 214 , 75 , 31 , 32 , 81, 179,  82 ,136,  83]):
				if plot:
					plt.show()
	except IOError as e:
		print e
		exc=True
print removelist

X=np.array(X)
print X.shape
if mirror:
	np.save('../'+dirf+'_mirror', X)
else:
	np.save('../'+dirf, X)