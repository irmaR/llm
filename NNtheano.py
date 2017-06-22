import numpy as np
import matplotlib.pyplot as plt
import math
import time
from sklearn.cross_validation import train_test_split
from sklearn.cross_validation import KFold
import random,sys
## load dataset
plt.rcParams['figure.figsize']=(15.0, 11.0);

plt.ion()
plot=True
if(len(sys.argv)>1):
	if(sys.argv[1]=='n'):
		plot=False

import theano
import theano.tensor as T
rng = np.random
import utilstheano
import utils
import feature_selection

label='2aprilr'
X,Y=utils.getdata(label,300)

feats = X.shape[1]
nout=1

# Declare Theano symbolic variables
x = T.matrix("x")
y = T.dvector("y")

numhidden=[feats,30]
wh=[]
bh=[]
o=x
w = theano.shared(rng.randn(numhidden[-1],1)/np.sqrt(numhidden[-1]), name="w")
b = theano.shared(np.zeros(1), name="b")

penaltyh = theano.shared(0.001, name="penaltyh")
penaltyo = theano.shared(0.001, name="penaltyo")

sumw=penaltyo*(w**2).sum()
for i in xrange(0,len(numhidden)-1):
	wh.append(theano.shared(rng.randn(numhidden[i],numhidden[i+1])/np.sqrt(numhidden[i])))
	bh.append(theano.shared(np.zeros(numhidden[i+1])+0.05))
	o=T.nnet.relu(T.dot(o, wh[-1]) + bh[-1])
	sumw+=penaltyh*((wh[-1])**2).sum()

pred = (T.dot(o,w)+b).reshape((-1,))
cost2 = T.mean((y-pred)**2)+sumw#+2*penaltyo*(b**2).sum()+2*penaltyh*(bh**2).sum()
#0*((T.abs_(w)).sum())+

allparam=wh+bh+[w]+[b]
lrdef=0.00004
learnrate = theano.shared(lrdef, name="learnrate")
updates=utilstheano.RMSpropmom(cost2,allparam,learnrate,0.98)



print("Initial model:")
#print(w.get_value())
#print(b.get_value())
print feats
# Compile
train = theano.function(
          inputs=[x,y],
          outputs=cost2,
          updates=updates,
          allow_input_downcast=True)

predict = theano.function(inputs=[x], outputs=pred,allow_input_downcast=True)
ttest = theano.function(inputs=[x], outputs=T.concatenate([x, x**2, x**3],1),allow_input_downcast=True)

#print ttest(Xtr[:10,:])
colormap = np.array(['r', 'g', 'b','y'])

'''
plt.scatter(Xval, Yval,  color='black')
plt.scatter(Xtr, np.zeros(ndata),  color=colormap[predictcluster(Xtr)])

plt.plot(np.sort(Xval,0), Yval[np.argsort(Xval,0)].reshape(-1,), color='blue')
plt.show()
'''


nf=5
olderr=float('inf')
err=0.0
training_steps = nf*1000
maxval=0.0
start_time = time.time()

bestalpha=-1
bestscore=float('-inf')
bestalphaSE=-1
bestscoreSE=float('inf')
cvfolds=8
for alpha in np.logspace(-5, -1, 10):
	print 'alpha',alpha
	penaltyh.set_value(alpha)
	penaltyo.set_value(alpha)
	kf = KFold(len(Y), n_folds=cvfolds)
	nd=0
	MSE_v=0.0
	loglkl=0.0
	loglkl1=0.0
	for train_index, test_index in kf:
		Xtr=X[train_index]
		Ytr=Y[train_index]
		Xval=X[test_index]
		Yval=Y[test_index]
		
		
		mean=np.mean(Xtr,0)
		minTr=np.min(Xtr,0)
		minv=(np.max(Xtr,0)-np.min(Xtr,0))
		minv[minv==0]=1.
		Xtr=(Xtr-minTr)/minv
		Xval=(Xval-minTr)/minv
		
		batch_size= Xtr.shape[0]/nf
		besterrval=float('inf')
		besterr=float('inf')
		learnrate.set_value(lrdef)
		
		w.set_value(rng.randn(numhidden[-1],1)/np.sqrt(numhidden[-1]))
		b.set_value(np.zeros(1))
		for i in xrange(0,len(numhidden)-1):
			wh[i].set_value(rng.randn(numhidden[i],numhidden[i+1])/np.sqrt(numhidden[i]))
			bh[i].set_value(np.zeros(numhidden[i+1])+0.05)
		arr = np.arange(nf)
		np.random.shuffle(arr)
		bestvalindex=0
		trainvec=[]
		valvec=[]
		learnvec=[]
		for i in range(training_steps):
			fold= arr[i%nf]
			
			trainb=Xtr[fold*batch_size:(fold+1)*batch_size,:]
			
			err += train(trainb, Ytr[fold*batch_size:(fold+1)*batch_size] )
			
			if(fold==nf-1):
				if(err<=olderr):
					learnrate.set_value(learnrate.get_value()*0.999)
				else:
					learnrate.set_value(learnrate.get_value()*0.9)
		#		print 'score',(err/nf)**0.5,'learnrate',learnrate.get_value(),'penaltyo',penaltyo.get_value()
				olderr=err
				err=0.0
				duration = time.time() - start_time
		#		print 'time',duration
				start_time = time.time()
			printevery=2
			if(i%(nf*printevery)==0):
				tmptr=  np.mean((predict(Xtr)-Ytr)**2)
				predval=predict(Xval)
				tmpval= np.mean((predval-Yval)**2)
				if (i-bestvalindex>250*nf and i>250*nf) or learnrate.get_value()<5e-8:
					print 'stop',tmptr**0.5,tmpval**0.5
					break
				if(i%(nf*10)==0):
					print 'accuracy',i,tmptr**0.5,tmpval**0.5,'learnrate',learnrate.get_value()
					print 'best',besterr,besterrval
				if(tmpval**0.5<besterrval):
					besterrval=tmpval**0.5
					besterr=tmptr**0.5
					bestvalindex=i
				trainvec+=[tmptr**0.5]
				valvec+=[tmpval**0.5]
				learnvec+=[learnrate.get_value()*1000]
				if plot and len(trainvec)>50 and i%(nf*10*printevery)==0:
					plt.clf()
					#plt.autoscale(enable=False)
					#plt.axis([0,750,0,0.1])				
					plt.plot(range(0,len(trainvec)*printevery,printevery), trainvec, color='black',linewidth=2)
					plt.plot(range(0,len(valvec)*printevery,printevery), valvec, color='blue',linewidth=2)
					plt.plot(range(0,len(learnvec)*printevery,printevery), learnvec, color='green',linewidth=2)
					plt.draw()
				
		MSE_v+=	tmpval
		print 'bestfold',besterrval,'last',tmpval
	MSE_v=(MSE_v/cvfolds)
	print alpha,MSE_v**0.5
	if(MSE_v<bestscoreSE):
		bestscoreSE=MSE_v
		bestalphaSE=alpha
		'''
		plt.clf()
		plt.autoscale(enable=False)
		plt.axis([-1,1,-2,3])
		plt.scatter(Xtr.reshape(-1,), Ytr.reshape(-1,),  color='black',s=5,marker='.')
		
		ll=lineseval(np.sort(Xtr,0))
		plt.plot(np.sort(Xtr.reshape(-1,)), ll[:,0], color=colormap[0],linewidth=2)
		plt.plot(np.sort(Xtr.reshape(-1,)), ll[:,1], color=colormap[1],linewidth=2)
		plt.plot(np.sort(Xtr.reshape(-1,)), ll[:,2], color=colormap[2],linewidth=2)
		plt.plot(np.sort(Xtr.reshape(-1,)), ll[:,3], color=colormap[3],linewidth=2)
		plt.scatter(Xtr, np.zeros(ndata)-1,  color=colormap[predictcluster(Xtr)])
		plt.plot(np.sort(Xtr.reshape(-1,)), predict(Xtr).reshape(-1,)[np.argsort(Xtr.reshape(-1,))], color='black',linewidth=2.5)

		plt.draw()
		'''
#print 'best',bestscoreSE**0.5,bestalphaSE
with open('NN.txt','a') as f:
	f.write(label+", "+str(feats)+", "+str(bestscoreSE**0.5)+", "+str(bestalphaSE))
	print 'best '+label+", "+str(feats)+", "+str(bestscoreSE**0.5)+", "+str(bestalphaSE)
	f.write("\n")
