from scipy import stats
import numpy as np
from sklearn.linear_model import BayesianRidge, Ridge,Lasso,RidgeCV,LassoCV,ARDRegression,RandomizedLasso
from sklearn.grid_search import GridSearchCV
from sklearn.ensemble import RandomForestRegressor

def selectionf(thr,X,Y,ind,cv,numf=10,percentile=-1):
	#	print X.shape,Y.shape,ind.shape
#	print ind
	# normalize
	minTr=np.min(X,0)
	minv=(np.max(X,0)-np.min(X,0))
	minv[minv==0]=1.
	X=(X-minTr)/minv
	'''
	sfm = SelectFromModel(LassoCV(cv=cv), threshold=0.000001)
	sfm.fit(X, Y)
	return sfm.get_support(True)
	'''
	lasso=GridSearchCV(Lasso( max_iter=1000), cv=cv,scoring='mean_squared_error',
                   param_grid={"alpha": np.logspace(-8, 1, 9)}, n_jobs=2)
	lasso.fit(X, Y)
#	randomized_lasso = RandomizedLasso()
#	randomized_lasso.fit(X, Y)
	ard = ARDRegression()
	ard.fit(X, Y)
	
	RF=RandomForestRegressor(n_estimators=300, n_jobs=2)
	RF.fit(X, Y)
#	print (-lasso.best_score_)**0.5#,(-ard.best_score_)**0.5
	lassorank=np.abs(lasso.best_estimator_.coef_)
	lassorank=lassorank/lassorank.sum()
	ARDrank=np.abs(ard.coef_)
	ARDrank=ARDrank/ARDrank.sum()
	
	bestindexes=np.abs(lasso.best_estimator_.coef_).argsort()[-numf:][::-1]
#	bestindexesRL=np.abs(randomized_lasso.scores_).argsort()[-numf:][::-1]
	bestindexesARD=np.abs(ard.coef_).argsort()[-numf:][::-1]
	bestindexesRF=RF.feature_importances_.argsort()[-numf:][::-1]

#	print lasso.best_estimator_.coef_[bestindexes]
#	print ard.best_estimator_.coef_[bestindexesARD]
#	print (1*(np.abs(lasso.best_estimator_.coef_)>thr)).sum()
	if percentile<0:
		bestindexesRF = bestindexesRF[ np.flatnonzero(1*(RF.feature_importances_[bestindexesRF]>thr)) ]
		bestindexes = bestindexes[ np.flatnonzero(1*(np.abs(lasso.best_estimator_.coef_[bestindexes])>thr)) ]
		bestindexesARD = bestindexesARD[ np.flatnonzero(1*(np.abs(ard.coef_[bestindexesARD])>thr)) ]
	else:
		bestindexesRF = np.flatnonzero(1*(RF.feature_importances_>np.percentile(RF.feature_importances_, percentile)))
		bestindexes =  np.flatnonzero(1*(lassorank >np.percentile(lassorank, percentile)))
		bestindexesARD =  np.flatnonzero(1*(ARDrank >np.percentile(ARDrank, percentile)))
	#	print percentile,RF.feature_importances_,np.percentile(RF.feature_importances_, percentile),np.percentile(lassorank, percentile),np.percentile(ARDrank, percentile)
	print bestindexes
	print bestindexesRF
	print bestindexesARD
	return np.union1d(np.union1d(ind[bestindexes],ind[bestindexesARD]),ind[bestindexesRF])

def selection(thr,nfile,cv,numf=10,percentile=-1):
	X,Y,ind=readdata2(False,nfile,returnindex=True)
	X=np.array(X)
	Y=np.array(Y)
	ind=np.array(ind)
	return selectionf(thr,X,Y,ind,cv,numf,percentile)
	
def readdata(classification,filen):
	X=[]
	y=[]
	"read data from file"
	with open(filen) as f:
		data = f.read()
		if len(data)>0:
			data = data.split('\n')
			for i,row in enumerate(data):
				if len(row)>0:
					data[i]=row.split(",")
					features=[]
					if(classification==True):
						y.append(data[i][0])
					else:
						y.append(float(data[i][0]))
					for j,elem in enumerate(data[i][1:]):
						if elem=='true':
						   features.append(1.0)
						elif elem=='false':
						   features.append(0.0)
						elif elem!='':
							features.append(float(elem))
					#if data[i][0]!=data[i][1]:
					X.append(features)
					    #XX.append(data[i][1:300])
					#y.append(data[i][0])
		f.close()
	return X,y

def readdata2(classification,filename,returnindex=False):
	X=[]
	y=[]
	numf=-1
	"read data from file"
	with open(filename) as f:
		data = f.read()
		if len(data)>0:
			data = data.split('\n')
			for i,row in enumerate(data):
				if len(row)>0:
					data[i]=row.split(",")
					features=[]
					indexes=[]
					nf=0
					for j,elem in enumerate(data[i][2:]):
						if elem=='true':
							features.append(1.0)
							indexes+=[nf]
						elif elem=='false':
							features.append(0.0)
							indexes+=[nf]
						elif elem=='g':
							features+=[0.,0.,0.,0.,1.]
							indexes+=[nf]*5
						elif elem=='t':
							features+=[0.,0.,0.,1.,0.]
							indexes+=[nf]*5
						elif elem=='p':
							features+=[0.,0.,1.,0.,0.]
							indexes+=[nf]*5
						elif elem=='b':
							features+=[0.,1.,0.,0.,0.]
							indexes+=[nf]*5
						elif elem=='NDVal':
							features+=[1.,0.,0.,0.,0.]
							indexes+=[nf]*5
						elif elem!='':
							try:
								features.append(float(elem))
								indexes+=[nf]
							except ValueError:
								features.append(float(-100.0))
								print i,j,elem,'error'
								raise ValueError
						if elem!='':
							nf+=1
					#if data[i][0]!=data[i][1]:
					if(numf==-1 or len(features)==numf):
						X.append(features)
						if(classification==True):
							y.append(data[i][1])
						else:
							y.append(float(data[i][1]))
					else:
						raise NameError('num features wrong '+str(numf)+" "+str(len(features)))
					#y.append(data[i][0])
					numf=len(features)
		f.close()
	if returnindex:
		return X,y,indexes
	else:
		return X,y


def getdata(label,select=-1,percentile=-1):
	"""
	return X,Y
	"""
	if label=='2r':
		nfile='../Data/mix2Obj/FullFeatureValues/pos_x_next.csv'
		X,Y=readdata2(False,nfile)#readdata(False,sys.argv[1])
		X=np.array(X)
		Y=np.array(Y)
	if label=='2aprilr':
		nfile='../Data/2objApril/Standard_Data_Feature_Values/FullFeatureValues/pos_x_next.csv'
		X,Y=readdata2(False,nfile)#readdata(False,sys.argv[1])
		X=np.array(X)
		Y=np.array(Y)
	if label=='2aprilmr':
		nfile='../Data/2objApril/Mirrored_DatA_FeatureValues/FullFeatureValues/pos_x_next.csv'
		X,Y=readdata2(False,nfile)#readdata(False,sys.argv[1])
		X=np.array(X)
		Y=np.array(Y)
	if label=='2sr':
		nfile='../Data/mix2Obj/FullFeatureValues/pos_x_next.csv'
		X,Y=readdata2(False,nfile)#readdata(False,sys.argv[1])
		X=np.array(X)
		Y=np.array(Y)
		with open('../Results/Results_With_Scripts/mix2Obj/All_Data/Feature_Preselection/10Fts/pos_x_next_indices.info') as ff:
			data = ff.read()
			if len(data)>0:
				data = data.split('\n')
				for i,row in enumerate(data):
					if len(row)>0:
						data[i]=row.split(",")
						print data[i]
						ind=np.array([int(dd) for dd in data[i] if dd!=' '])
		X=X[:,ind]
	if label=='3r':
		nfile='../Data/mix3ObjApril/FullFeatureValues/pos_x_next.csv'
		X,Y=readdata2(False,nfile)#readdata(False,sys.argv[1])
		X=np.array(X)
		Y=np.array(Y)
	elif label=='23r':
		X,Y=readdata2(False,'../Data/mix2Obj/FullFeatureValues/pos_x_next.csv')#readdata(False,sys.argv[1])
		X2,Y2=readdata2(False,'../Data/mix3Obj/FullFeatureValues/pos_x_next.csv')
		X=np.vstack((np.array(X),np.array(X2)))
		Y=np.append(np.array(Y),np.array(Y2))
	elif label=='23mr':
		nfile='../Data/mirrored/mix23Obj/FullFeatureValues/pos_x_next.csv'
		X,Y=readdata2(False,nfile)#readdata(False,sys.argv[1])
		X=np.array(X)
		Y=np.array(Y)
	elif label=='1p':
		alldata2=np.load('all_data/1Object/data.npy')
		X=alldata2[:,2:]
		Y=alldata2[:,0]
	elif label=='2p':
		alldata2=np.load('../Data/mix2Obj/DummyValidation/train/data.npy')
		X=alldata2[:,4:]
		Y=alldata2[:,0]
	elif label=='2mp':
		alldata2=np.load('../Data/mirrored/2obj/data.npy')
		X=alldata2[:,4:]
		Y=alldata2[:,0]
	elif label=='3mp':
		alldata2=np.load('../Data/mirrored/3obj/data.npy')
		X=alldata2[:,4:]
		Y=alldata2[:,0]
	elif label=='2rp':
		alldata2=np.load('../Data/mix2Obj/DummyValidation/train/data.npy')
		X=alldata2[:,4:]
		Y=alldata2[:,0:4]
		nobj=2
		ind=np.array([[0,1,4,5,6,7,8,9],[2,3,10,11,12,13,14,15]])
		newX=[]
		newY=[]
		for i in xrange(nobj):
			tY=Y[:,2*i:2*i+2]
			tX=X[:,ind[i]]
			for j in xrange(nobj):
				if j!=i:
					tX=np.concatenate((tX,X[:,ind[j]]),axis=1)
			if newX==[]:
				newX=tX
				newY=tY
			else:
				newX=np.concatenate((newX,tX),axis=0)
				newY=np.concatenate((newY,tY),axis=0)
		print newX.shape,newY.shape
		X=newX
		Y=newY
	elif label=='2ap':
		alldata2=np.load('../Data/mix2Obj/DummyValidation/train/data.npy')
		X=alldata2[:,4:]
		Y=alldata2[:,0:4]
	elif label=='3p':
		alldata2=np.load('../Data/mix3Obj/DummyValidation/train/data.npy')
		X=alldata2[:,6:]
		Y=alldata2[:,0]
	elif label=='3aprilp':
		alldata2=np.load('../Data/3objApril.npy')
		X=alldata2[:,6:]
		Y=alldata2[:,0]
	elif label=='3aprilmp':
		alldata2=np.load('../Data/3objApril_mirror.npy')
		X=alldata2[:,6:]
		Y=alldata2[:,0]
	elif label=='2aprilp':
		alldata2=np.load('../Data/2objApril.npy')
		X=alldata2[:,4:]
		Y=alldata2[:,0]
	elif label=='2aprilmp':
		alldata2=np.load('../Data/2objApril_mirror.npy')
		X=alldata2[:,4:]
		Y=alldata2[:,0]
	elif label=='3newap':
		alldata2=np.load('../Data/3objApril.npy')
		X=alldata2[:,6:]
		Y=alldata2[:,0:6]
	elif label=='3ap':
		alldata2=np.load('../Data/mix3Obj/DummyValidation/train/data.npy')
		X=alldata2[:,6:]
		Y=alldata2[:,0:6]
	elif label=='3rp':
		alldata2=np.load('../Data/mix3Obj/DummyValidation/train/data.npy')
		X=alldata2[:,6:]
		Y=alldata2[:,0:6]
		nobj=3
		ind=np.array([[0,1,6,7,8,9,10,11],[2,3,12,13,14,15,16,17],[4,5,18,19,20,21,22,23]])
		newX=[]
		newY=[]
		for i in xrange(nobj):
			tY=Y[:,2*i:2*i+2]
			tX=X[:,ind[i]]
			for j in xrange(nobj):
				if j!=i:
					tX=np.concatenate((tX,X[:,ind[j]]),axis=1)
			if newX==[]:
				newX=tX
				newY=tY
			else:
				newX=np.concatenate((newX,tX),axis=0)
				newY=np.concatenate((newY,tY),axis=0)
		print newX.shape,newY.shape
		X=newX
		Y=newY
#	print X.shape,Y.shape
	if select>0:
		ind= selectionf(0.0000001,X,Y,np.arange(X.shape[1]),10,select,percentile)
		print ind
		X=X[:,ind]
	return X,Y

def loglklnormal(ground_truth, predictions,STD):
	return sum(stats.norm.logpdf(ground_truth,predictions,STD))
def loglkllogistic(ground_truth, predictions,STD):
	return sum(stats.norm.logpdf(ground_truth,predictions,STD))
