import numpy as np
import matplotlib.pyplot as plt
from scipy import stats
import math
import sys
from sklearn.linear_model import BayesianRidge, Ridge,Lasso,RidgeCV,LogisticRegression
from sklearn import cross_validation
#from sklearn.preprocessing import PolynomialFeatures
#from sklearn.kernel_ridge import KernelRidge
#from sklearn.feature_selection import RFECV
from sklearn.externals import joblib
from sklearn.cross_validation import KFold
from utils import readdata
from sklearn import preprocessing

print "Running classification...."

X,y=readdata(True,sys.argv[1])
Xtest,ytest=readdata(True,sys.argv[2])

X=np.array(X,dtype=float)

le = preprocessing.LabelEncoder()
le.fit(y+ytest)

y=np.array(le.transform(y),dtype=int)
ytest=le.transform(ytest)

total_labels=float(sys.argv[4])
numlabels=len(np.unique(y))#total_labels # 
numlabelsindata=len(le.classes_)
#print X,y
bestalpha=-1
bestscore=float('-inf')
#bestalphaSE=-1
#bestscoreSE=1e200
ncv=5
if(len(y)<10):
	ncv=len(y)

for alpha in [0.00000001,0.001,0.01,0.1,1.0,10.0,1000.0]:
#	reg2=Ridge(alpha=alpha)
	kf = KFold(len(y), n_folds=ncv)
	nd=0
#	acc_v=0.0
	loglkl=0.0
	for train_index, test_index in kf:
		X_train, X_v = X[train_index], X[test_index]
		y_train, y_v = y[train_index], y[test_index]
		if(len(np.unique(y_train))==1):
			singlelabel=y_train[0]
			pseen=(1.0+len(y_train))/(total_labels+len(y_train))
			punseen=1.0/(total_labels+len(y_train))
			for lb in y_v:
				if(lb==singlelabel):
					loglkl += math.log(pseen)
					#print 'ok single',pseen
				else:
					loglkl += math.log(punseen)
					#print 'no single',punseen
		else:
			reg1=LogisticRegression(solver='lbfgs',multi_class='multinomial',C=alpha)
			reg1.fit(X_train,y_train)
			#print reg1.classes_
			labelindex={}
			for aa,bb in enumerate(reg1.classes_):
				labelindex[bb]=aa
			#print labelindex,reg1.classes_,le.classes_
			predy_v=reg1.predict_log_proba(X_v)
			for i in range(len(y_v)):
				try:
					loglkl +=predy_v[i, labelindex[y_v[i]] ]+math.log(1.0-(total_labels-len(reg1.classes_))/(len(y_train)+total_labels))
					#print 'ok',i,1.0-(total_labels-len(reg1.classes_))/(len(y_train)+total_labels)
				except KeyError:
					loglkl +=math.log(1.0/(len(y_train)+total_labels))
					#print 'no',i,1.0/(len(y_train)+total_labels)
#		loglkl1+=-np.dot(np.array(predy_v-y_v),np.array(predy_v-y_v))/STD**2/2.0+len(y_v)/2.0*math.log(1/STD**2)-len(y_v)/2.0*math.log(2.0*math.pi)
#		print loglkl,loglkl1
#	score=-cross_validation.cross_val_score(reg2, X, y, cv=ncv,scoring='mean_squared_error')
#	score=np.average(score)
	print alpha,loglkl
#	print score
	if(loglkl>bestscore):
		bestscore=loglkl
		bestalpha=alpha
		
#	if(acc_v<bestscoreSE):
#		bestscoreSE=acc_v
#		bestalphaSE=alpha
#print bestalpha,bestscore
#print bestalphaSE,bestscoreSE
SEtest=0.0
RMSEtest=0.0
loglklTest=0.0
weightlabels=1.0
reg=LogisticRegression(solver='lbfgs',multi_class='multinomial',C=bestalpha)
if(numlabels==1):
	singlelabel=np.unique(y)[0]
	pseen=(1.0+len(y))/(total_labels+len(y))
	probnotseen=1.0/(total_labels+len(y))
	for i in range(len(ytest)):
		if(ytest[i]==singlelabel):
			loglklTest += math.log(pseen)
		else:
			loglklTest += math.log(probnotseen)
else:
	reg.fit(X,y)
	labelindex={}
	for aa,bb in enumerate(reg.classes_):
		labelindex[bb]=aa
	#print labelindex
	weightlabels=1.0-(total_labels-len(reg.classes_))/(len(y)+total_labels)
	if(weightlabels<1.0):
		probnotseen=1.0/(len(y)+total_labels)
	else:
		probnotseen=0.0
	if len(ytest)>0:
		ypred=reg.predict_log_proba(Xtest)
		for i in range(len(ytest)):
			try:
				loglklTest +=ypred[i,labelindex[ytest[i]]]+math.log(weightlabels)
			except KeyError:
				loglklTest +=math.log(probnotseen)
#print bestscore,loglklTest
#print reg.alpha_
#print reg.intercept_,reg.coef_
param=open(sys.argv[3],'w')

param.write("weight and labels and number models,")
param.write(str(weightlabels)+",")

param.write(str(numlabels)+",")

if(numlabels==1):
	param.write("0\n")
	param.write("seen labels")
	for label in np.unique(y):
		param.write(","+str(le.inverse_transform([label])[0]))
	param.write("\n")
	param.write("label prob,")
	param.write(str(le.inverse_transform([y[0]])[0])+",")
	param.write(str(pseen))
	param.write(str("\n"))
elif(numlabels>2):
	param.write(str(numlabels))
	param.write("\n")
	param.write("seen labels")
	for label in np.unique(y):
		param.write(","+str(le.inverse_transform([label])[0]))
	param.write("\n")
	for label in np.unique(y):
		param.write("label and bias and coefficients,")
		param.write(str(le.inverse_transform([label])[0])+",")
		param.write(str(reg.intercept_[label]))
		for c in reg.coef_[label]:
			param.write(","+str(c))
		param.write(str("\n"))
else:
	param.write("1\n")
	param.write("seen labels")
	for label in np.unique(y):
		param.write(","+str(le.inverse_transform([label])[0]))
	param.write("\n")
	param.write("label bias and coefficients,")
	param.write(str(le.inverse_transform([1])[0])+",")
	param.write(str(reg.intercept_[0]))
	for c in reg.coef_[0]:
		param.write(","+str(c))
	param.write(str("\n"))
param.write("probability unseen,"+str(probnotseen)+"\n")
param.write("sum loglikelihood CV train,")
param.write(str(bestscore))
param.write("\n")
param.write("sum loglikelihood test,")
param.write(str(loglklTest))
param.write("\n")
# param.write("sum squared error CV train,")
# param.write(str(bestscoreSE))
# param.write("\n")
# param.write("sum squared error test,")
# param.write(str(SEtest))
# param.write("\n")
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
param.write(str(bestalpha))
#param.write(str(bestalphaSE))
param.close()

# save model
#joblib.dump(reg, 'model.pkl')
#clf = joblib.load('model.pkl')
print "Finished ..."

exit()
