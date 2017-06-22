#!/usr/bin/python

import sys
import numpy as np
import matplotlib.pyplot as plt
import time
from mpl_toolkits.mplot3d import Axes3D
import re,os

plot=False

def closeabove(x,y,x2,y2):
	if y>y2 and abs(x-x2)<0.07 and abs(y-y2)<0.07:
		return True
	else:
		return False
def closebelow(x,y,x2,y2):
	if y<y2 and abs(x-x2)<0.07 and abs(y-y2)<0.07:
		return True
	else:
		return False

def closeleft(x,y,x2,y2):
	if x<x2 and abs(x-x2)<0.07 and abs(y-y2)<0.07:
		return True
	else:
		return False

def closeright(x,y,x2,y2):
	if x>x2 and abs(x-x2)<0.07 and abs(y-y2)<0.07:
		return True
	else:
		return False

def disp(idaction,displacement):
	action=""
	if(displacement>=0):
		if(displacement<=0.04):
			action+="disp(obj"+str(idaction)+",a).\n"
		if(displacement<=0.08):
			action+="disp(obj"+str(idaction)+",b).\n"
		if(displacement<=0.12):
			action+="disp(obj"+str(idaction)+",c).\n"
		if(displacement>0.12):
			action+="disp(obj"+str(idaction)+",g).\n"
	else:
		if(displacement>= -0.04):
			action+="disp(obj"+str(idaction)+",d).\n"
		if(displacement>= -0.08):
			action+="disp(obj"+str(idaction)+",e).\n"
		if(displacement>= -0.12):
			action+="disp(obj"+str(idaction)+",f).\n"
		if(displacement< -0.12):
			action+="disp(obj"+str(idaction)+",h).\n"
	return action

numobj=2
numsamples=1000
x_cur=np.random.uniform(-0.2,0.2,(numsamples,numobj))
y_cur=np.random.uniform(0.1,0.3,(numsamples,numobj))

actionlist='ptgb'

signdisplacement=np.random.randint(2, size=numsamples)*2-1
displacement=np.random.uniform(0.03,0.15,numsamples)*signdisplacement
action=np.random.randint(4, size=numsamples)
action=[actionlist[ac] for ac in action]
actionid=np.random.randint(numobj, size=numsamples)

x_next=np.zeros((numsamples,numobj))
y_next=np.zeros((numsamples,numobj))


dirf='syntheticsimple'
os.chdir('../Data/'+dirf+'/')
for i in xrange(numsamples):
	if plot:
		p=plt.subplot(1,1,1)
		p.clear()
		fig = plt.gcf()
		plt.gca().cla()
		p.set_xlim(-0.5,0.5)
		p.set_ylim(0,0.5)
	with open(str(i)+'.txt','w') as f:
		f.write('arm(arm).\n')
		f.write('action(obj'+str(actionid[i])+','+action[i]+').\n')
		interact=False
		for id in xrange(numobj):
			if actionid[i]==id and (action[i]=='t' or action[i]=='g'):
				x_next[i,id]=x_cur[i,id]+displacement[i]+np.random.randn()/10000.
				y_next[i,id]=y_cur[i,id]+np.random.randn()/10000.
			elif actionid[i]==id and (action[i]=='p' or action[i]=='b'):
				y_next[i,id]=y_cur[i,id]+displacement[i]+np.random.randn()/10000.
				x_next[i,id]=x_cur[i,id]+np.random.randn()/10000.

			elif actionid[i]!=id and (action[i]=='t' or action[i]=='g') and displacement[i]>0. and closeright(x_cur[i,id],y_cur[i,id],x_cur[i,actionid[i]],y_cur[i,actionid[i]]):
				x_next[i,id]=x_cur[i,id]+displacement[i]+np.random.randn()/10000.
				y_next[i,id]=y_cur[i,id]+np.random.randn()/10000.
				interact=True
			elif actionid[i]!=id and (action[i]=='t' or action[i]=='g') and displacement[i]<0. and closeleft(x_cur[i,id],y_cur[i,id],x_cur[i,actionid[i]],y_cur[i,actionid[i]]):
				x_next[i,id]=x_cur[i,id]+displacement[i]+np.random.randn()/10000.
				y_next[i,id]=y_cur[i,id]+np.random.randn()/10000.
				interact=True
			elif actionid[i]!=id and (action[i]=='p' or action[i]=='b') and displacement[i]>0. and closeabove(x_cur[i,id],y_cur[i,id],x_cur[i,actionid[i]],y_cur[i,actionid[i]]):
				y_next[i,id]=y_cur[i,id]+displacement[i]+np.random.randn()/10000.
				x_next[i,id]=x_cur[i,id]+np.random.randn()/10000.
				interact=True
			elif actionid[i]!=id and (action[i]=='p' or action[i]=='b') and displacement[i]<0. and closebelow(x_cur[i,id],y_cur[i,id],x_cur[i,actionid[i]],y_cur[i,actionid[i]]):
				y_next[i,id]=y_cur[i,id]+displacement[i]+np.random.randn()/10000.
				x_next[i,id]=x_cur[i,id]+np.random.randn()/10000.
				interact=True
			else:
				y_next[i,id]=y_cur[i,id]+np.random.randn()/10000.
				x_next[i,id]=x_cur[i,id]+np.random.randn()/10000.
			if actionid[i]==id and (action[i]=='t' or action[i]=='g'):
				plt.arrow(x_cur[i,id], y_cur[i,id], displacement[i], 0, head_width=0.01, head_length=0.01, fc='k', ec='k')
			elif actionid[i]==id and (action[i]=='p' or action[i]=='b'):
				plt.arrow(x_cur[i,id], y_cur[i,id], 0, displacement[i], head_width=0.01, head_length=0.01, fc='k', ec='k')
		
			plt.plot([x_cur[i,id],x_next[i,id]],[y_cur[i,id],y_next[i,id]],marker='o')
			print action[i],x_next[i,id]-x_cur[i,id],y_next[i,id]-y_cur[i,id]
			
			f.write('object(obj'+str(id)+').\n')
			if actionid[i]==id:
				f.write('displacement(obj'+str(id)+','+str(displacement[i])+').\n')
				f.write(disp(id,displacement[i]))
			else:
				f.write('displacement(obj'+str(id)+',0.0).\n')
				f.write('disp(obj'+str(id)+',n).\n')
			f.write('pos_x_cur(obj'+str(id)+','+str(x_cur[i,id])+').\n')
			f.write('pos_y_cur(obj'+str(id)+','+str(y_cur[i,id])+').\n')
			f.write('pos_x_next(obj'+str(id)+','+str(x_next[i,id])+').\n')
			f.write('pos_y_next(obj'+str(id)+','+str(y_next[i,id])+').\n')
	if  interact==False:
		os.remove(str(i)+'.txt')
	elif plot:
		plt.show()
		
		
exit()