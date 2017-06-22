'''
Created on Apr 2, 2016

@author: davide
'''
import sys
from utils import *
import numpy as np
from regression_new import regression
from sklearn.linear_model import BayesianRidge, Ridge,Lasso,RidgeCV,LassoCV,ARDRegression
from sklearn.grid_search import GridSearchCV

if __name__ == '__main__':
	"""
	arguments csvfile outputfile maxnumfeatures percentile
	"""
	if len(sys.argv)>4:
		percentile=float(sys.argv[4])
	else:
		percentile=-1
	bestindexes=selection(0.000001,sys.argv[1],10,int(sys.argv[3]),percentile)
	out=sys.argv[2]
	with open(out,'w') as f:
		for i in bestindexes[:-1]:
			f.write(str(i)+',')
		f.write(str(bestindexes[-1]))
	exit()
	'''
	bestindexes=np.abs(reg.coef_).argsort()[-30:][::-1]
	print bestindexes
	print reg.coef_[bestindexes]
	print (1*(np.abs(reg.coef_)>0.000001)).sum()
	'''
