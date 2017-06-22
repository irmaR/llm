import numpy as np
import matplotlib.pyplot as plt
from scipy import stats
import math
import sys
import time
from sklearn.linear_model import BayesianRidge, Ridge,Lasso,RidgeCV
from sklearn import cross_validation
#from sklearn.preprocessing import PolynomialFeatures
#from sklearn.kernel_ridge import KernelRidge
#from sklearn.feature_selection import RFECV
from sklearn.externals import joblib
from sklearn.cross_validation import KFold

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

start_time = time.time()
X,y=readdata(sys.argv[1])
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
	

for alpha in [0.0001,0.001,0.01,0.1, 1.0, 10.0,100.0]: 
#	reg2=Ridge(alpha=alpha)
	kf = KFold(len(y), n_folds=ncv)
	nd=0
	MSE_v=0.0
	loglkl=0.0
	loglkl1=0.0
	for train_index, test_index in kf:
		X_train, X_v = X[train_index], X[test_index]
		y_train, y_v = y[train_index], y[test_index]
		reg1=Ridge(alpha=alpha)
		reg1.fit(X_train,y_train)
		predytrain=reg1.predict(X_train)
		STD=math.sqrt(np.dot(np.array(y_train-predytrain),np.array(y_train-predytrain))/float(len(predytrain)-1.0))+1e-10 # to avoid problems
#		print STD,len(predytrain)
		predy_v=reg1.predict(X_v)
		MSE_v +=np.dot(np.array(predy_v-y_v),np.array(predy_v-y_v))#/float(len(y_v))
		loglkl +=loglklnormal(y_v,predy_v,STD)
#		loglkl1+=-np.dot(np.array(predy_v-y_v),np.array(predy_v-y_v))/STD**2/2.0+len(y_v)/2.0*math.log(1/STD**2)-len(y_v)/2.0*math.log(2.0*math.pi)
#		print loglkl,loglkl1
#	score=-cross_validation.cross_val_score(reg2, X, y, cv=ncv,scoring='mean_squared_error')
#	score=np.average(score)
#	print alpha,MSE_v,loglkl,STD
#	print score
	if(loglkl>bestscore):
		bestscore=loglkl
		bestalpha=alpha
		
	if(MSE_v<bestscoreSE):
		bestscoreSE=MSE_v
		bestalphaSE=alpha
#print bestalpha,bestscore
#print bestalphaSE,bestscoreSE

reg=Ridge(alpha=bestalpha)
reg.fit(X,y)
predy=reg.predict(X)
vartrain=np.dot(np.array(predy-y),np.array(predy-y))/float(len(y)-1.0)+1e-10
#print vartrain
Xtest,ytest=readdata(sys.argv[2])

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
param=open(sys.argv[3],'w')
#print "Writing to: ",sys.argv[3]
param.write("bias and coefficients,")
param.write(str(reg.intercept_))
for c in reg.coef_:
    param.write(","+str(c))
param.write("\n")
param.write("STD train,")
param.write(str(math.sqrt(vartrain)))
param.write("\n")
param.write("sum loglikelihood CV train,")
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


exit()
