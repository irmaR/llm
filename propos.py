import numpy as np
import matplotlib
#matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.cm as cm
import math
import time
from sklearn.cross_validation import train_test_split
from sklearn.cross_validation import KFold
import random,sys
import os
from utils  import *

from sklearn.svm import SVR
from sklearn.grid_search import GridSearchCV
from sklearn.linear_model import BayesianRidge, Ridge,Lasso,RidgeCV,LassoCV,ARDRegression
from sklearn import tree
from sklearn.ensemble import RandomForestRegressor,GradientBoostingRegressor
from matplotlib import gridspec
from sklearn.kernel_ridge import KernelRidge
from sklearn.preprocessing import PolynomialFeatures

plt.rcParams['figure.figsize']=(20.0, 10.0);


plot=True
if(len(sys.argv)>1):
	if(sys.argv[1]=='n'):
		plot=False
if plot:
	plt.ion()
else:
	plt.ioff()
rng = np.random


cv=10
svr = GridSearchCV(SVR(kernel='rbf',degree=6,coef0=0), cv=cv,scoring='mean_squared_error',
                   param_grid={"C": np.logspace(-1, 7, 10),
                               "gamma": np.logspace(-5, 5, 10)},n_jobs=3)

kr = GridSearchCV(KernelRidge(kernel='rbf',degree=6,coef0=1), cv=cv,scoring='mean_squared_error',
                   param_grid={"alpha": np.logspace(-5, 4, 10),
                               "gamma": np.logspace(-5, 5, 10)},n_jobs=3)

rg = GridSearchCV(Ridge(), cv=cv,scoring='mean_squared_error',
                   param_grid={"alpha": np.logspace(-7, 1, 10)},n_jobs=3)

ls = GridSearchCV(Lasso( max_iter=1500), cv=cv,scoring='mean_squared_error',
                   param_grid={"alpha": np.logspace(-8, 1, 10)},n_jobs=3)
ls2 = GridSearchCV(LassoCV( max_iter=1500), cv=cv,scoring='mean_squared_error',
                  param_grid={})

tr = GridSearchCV(tree.DecisionTreeRegressor(), cv=cv,scoring='mean_squared_error',
                   param_grid={"max_depth": [3,4,5,6,7]},n_jobs=3)

rf = GridSearchCV(RandomForestRegressor(), cv=cv,scoring='mean_squared_error',
                   param_grid={"n_estimators": [10,100,300,1000]},n_jobs=3)

gb = GridSearchCV(GradientBoostingRegressor(), cv=cv,scoring='mean_squared_error',
                   param_grid={"n_estimators": [10,100,300,1000],"max_depth":[1,2,3,4,5]},n_jobs=3)

br = GridSearchCV(BayesianRidge(), cv=cv,scoring='mean_squared_error',
                   param_grid={},n_jobs=3)

ard = GridSearchCV(ARDRegression(), cv=cv,scoring='mean_squared_error',
                   param_grid={},n_jobs=3)

estim=    [	  ls,         kr,   rg,  tr,          rf,	        br,ard,gb]
nameestim='Lasso,KernelRidge,Ridge,tree,RandomForest,BayesianRidge,ARD,GradientBoosting'.split(',')

for label in ['2aprilr','2aprilmr','3aprilp','3aprilmp','2aprilp','2aprilmp']:#['3newp','2p','2r','3r','23r','23mr','2mp','3mp']:
	besterr=float('inf')
	if os.path.isfile(label+'.txt'):
		os.remove(label+'.txt')
	if label[-1]=='p':
		listf=[-1]
	else:
		listf=[-1,10,40,100]
	for ff in listf:
		print label,'cv',cv
		X,Y=getdata(label,ff)
		#Y=Y[:,0]
		print X.shape,Y.shape
		
		feats = X.shape[1]
		
	#	bestval=float('inf')
		minTr=np.min(X,0)
		minv=(np.max(X,0)-np.min(X,0))
		minv[minv==0]=1.
		
		X=(X-minTr)/minv
		#Xn=(X-np.mean(X,0))/np.std(X,0)
		Xc=X
		
		if label[-1]=='p':
			polf=[1,2,3]
		elif feats<50:
			polf=[1,2]
		else:
			polf=[1]
		for pf in polf:
			if pf>1: #if label[-1]=='p':
				poly = PolynomialFeatures(pf,include_bias=False)
				X=poly.fit_transform(Xc)
			
			for n,e in zip(nameestim,estim):
				e.fit(X, Y)
				print "{:13}".format(n),e.best_score_,(-e.best_score_)**0.5,(-e.score(X, Y))**0.5,e.best_params_ 
				if besterr>(-e.best_score_)**0.5:
					besterr=(-e.best_score_)**0.5
					bestestim=n
				with open(label+'.txt','a') as f:
					f.write(label+", "+str(feats)+", "+str(X.shape[1])+", "+str(cv)+", "+n+", "+str((-e.best_score_)**0.5))
					f.write("\n")
	print label+" Best {}".format(bestestim),besterr
	with open(label+'.txt','a') as f:
		f.write("Best "+label+", "+str(bestestim)+", "+str(besterr))
		f.write("\n")
exit()

br.fit(X, Y)
br1=BayesianRidge(compute_score=True)
br1.fit(X, Y)
print br1.scores_[-1]
