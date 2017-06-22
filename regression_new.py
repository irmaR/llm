import numpy as np
import matplotlib.pyplot as plt
from scipy import stats
import math
import sys
import time
from utils import *
from sklearn.linear_model import BayesianRidge, Ridge,Lasso,RidgeCV
from sklearn import cross_validation
#from sklearn.preprocessing import PolynomialFeatures
#from sklearn.kernel_ridge import KernelRidge
#from sklearn.feature_selection import RFECV
from sklearn.externals import joblib
from sklearn.cross_validation import KFold
'''
def readdata(file):
    X=[]
    y=[]
    "read data from file"
    with open(file) as f:
        data = f.read()
        if len(data)>0:
            data = data.split('\n')
            for i,row in enumerate(data):
                if len(row)>0:
                    data[i]=row.split(",")
                    for j,elem in enumerate(data[i]):
                        if elem=='true':
                            data[i][j]=1.0
                        elif elem=='false':
                            data[i][j]=0.0
                        elif elem=='':
                            data[i].pop(j)
                        else:
                            data[i][j]=float(elem)
                    #if data[i][0]!=data[i][1]:
                    X.append(data[i][1:])
                        #XX.append(data[i][1:300])
                    y.append(data[i][0])
        f.close()
    return X,y

def loglklnormal(ground_truth, predictions,STD):
	return sum(stats.norm.logpdf(ground_truth,predictions,STD))
'''

def regression(trainfile,testfile,resultsfile,learner,weightdata=False,writefile=True):
	start_time = time.time()
	X,y=readdata2(False,trainfile)
	X=np.array(X)
	y=np.array(y)
	#print X,y
	bestalpha=-1
	bestscore=-1e200
	bestalphaSE=-1
	bestscoreSE=1e200
	ncv=10
	if(len(y)<20):
		ncv=len(y)
		
	if(learner=='Ridge' or learner=='Lasso'):
		alphalist=np.append(np.logspace(-7, 1, 10),[0])
	elif(learner=='BayesianRidge'):
		alphalist=[0]
		
	for alpha in alphalist:
		kf = KFold(len(y), n_folds=ncv)
		nd=0
		MSE_v=0.0
		loglkl=0.0
		loglkl1=0.0
		for train_index, test_index in kf:
			X_train, X_v = X[train_index], X[test_index]
			y_train, y_v = y[train_index], y[test_index]
			if weightdata:
				cond=np.abs(X_train[:,0]-y_train)<0.004
				print float(np.count_nonzero(cond))/y_train.shape[0]
			if(learner=='Ridge'):
				reg1=Ridge(alpha=alpha)
			elif(learner=='BayesianRidge'):
				reg1=BayesianRidge()
			elif(learner=='Lasso'):
				reg1=Lasso(alpha=alpha)
			reg1.fit(X_train,y_train)
			predytrain=reg1.predict(X_train)
	
			predy_v=reg1.predict(X_v)
			MSE_v +=np.dot(np.array(predy_v-y_v),np.array(predy_v-y_v))#/float(len(y_v))
			if(learner!='BayesianRidge'):
				STD=math.sqrt(np.dot(np.array(y_train-predytrain),np.array(y_train-predytrain))/float(len(predytrain)-1.0))+1e-12 # to avoid problems
				loglkl +=loglklnormal(y_v,predy_v,STD)
		if(learner!='BayesianRidge'):
			if(loglkl>bestscore):
				bestscore=loglkl
				bestalpha=alpha
			
		if(MSE_v<bestscoreSE):
			bestscoreSE=MSE_v
			bestalphaSE=alpha
	#print bestalpha,bestscore
	#print bestalphaSE,bestscoreSE
	
	# retrain on all the dataset
	if(learner=='Ridge'):
		reg=Ridge(alpha=bestalpha)
	elif(learner=='BayesianRidge'):
		reg=BayesianRidge(compute_score=True)
	elif(learner=='Lasso'):
		reg=Lasso(alpha=bestalpha)
	#reg=Ridge(alpha=bestalpha)
	reg.fit(X,y)
	predy=reg.predict(X)
	vartrain=np.dot(np.array(predy-y),np.array(predy-y))/float(len(y)-1.0)+1e-12
	#print vartrain
	Xtest,ytest=readdata2(False,testfile)
	
	SEtest=0.0
	RMSEtest=0.0
	loglklTest=0
	
	if len(ytest)>0:
		ypred=reg.predict(Xtest)
		SEtest=np.dot(np.array(ypred-ytest),np.array(ypred-ytest))
		print SEtest/float(len(ytest))
		my=sum(ytest)/float(len(ytest))
		vv=[(yy-my)*(yy-my) for yy in ytest]
		#    print (1-reg.score(Xtest, ytest))*sum(vv)
		RMSEtest=math.sqrt(SEtest/float(len(ytest)))
		loglklTest=loglklnormal(ytest,ypred,math.sqrt(vartrain))
	#print bestscore,loglklTest
	#print reg.alpha_
	#print reg.intercept_,reg.coef_
	#print "HERE"
	if writefile:
		param=open(resultsfile,'w')
		#print "Writing to: ",resultsfile
		param.write("bias and coefficients,")
		param.write(str(reg.intercept_))
		for c in reg.coef_:
			param.write(","+str(c))
		param.write("\n")
		param.write("STD train,")
		param.write(str(math.sqrt(vartrain)))
		param.write("\n")
		param.write("sum loglikelihood CV train,")
		if(learner=='BayesianRidge'):
			param.write(str(reg.scores_[-1]))
		else:
			param.write(str(bestscore))
		param.write("\n")
		param.write("sum loglikelihood test,")
		param.write(str(loglklTest))
		param.write("\n")
		param.write("sum squared error CV train,")
		param.write(str(bestscoreSE))
		param.write("\n")
		param.write("sum squared error test,")
		param.write(str(SEtest))
		param.write("\n")
		print RMSEtest
		# param.write("Root MSE (STD) CV train,")
		# param.write(str(math.sqrt(bestscore)))
		# param.write("\n")
		# param.write("squared error sum (score) CV train,")
		# param.write(str(bestscore*len(y)))
		# param.write("\n")
		# param.write("squared error sum test,"+ str(SEtest))
		# param.write("\n")
		# param.write("Root MSE test,"+ str(RMSEtest))
		# param.write("\n")
		param.write(str(type(reg))+",")
		param.write(str(bestalpha)+",")
		param.write(str(bestalphaSE))
		param.close()
		print("--- %s seconds ---" % (time.time() - start_time))
		# save model
		#joblib.dump(reg, 'model.pkl')
		#clf = joblib.load('model.pkl')
	return reg

if __name__ == '__main__':
	"""
	args: train test results typelearner
	typelearner={Ridge,Lasso,BayesianRidge}
	"""
	regression(sys.argv[1],sys.argv[2],sys.argv[3],sys.argv[4],False)
