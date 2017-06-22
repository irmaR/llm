import numpy as np
import matplotlib
#matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.cm as cm
import math
import time
from sklearn.cross_validation import train_test_split
from sklearn.cross_validation import KFold
from sklearn.preprocessing import PolynomialFeatures

import random,sys
import utilstheano
import utils

from matplotlib import gridspec

import GPy

from robo.models.GPyModel import GPyModel
from robo.acquisition.EI import EI
from robo.acquisition.UCB import  UCB

from robo.maximizers.maximize import stochastic_local_search
from robo.recommendation.incumbent import compute_incumbent

from bayes_opt import BayesianOptimization
def posterior(bo, x):
    bo.gp.fit(bo.X, bo.Y)
    mu, sigma2 = bo.gp.predict(x.reshape(-1, 1), eval_MSE=True)
    return mu, np.sqrt(sigma2)


def unique_rows(a):
    """
    A functions to trim repeated rows that may appear when optimizing.
    This is necessary to avoid the sklearn GP object from breaking

    :param a: array to trim repeated rows from

    :return: mask of unique rows
    """

    # Sort array and kep track of where things should go back to
    order = np.lexsort(a.T)
    reorder = np.argsort(order)

    a = a[order]
    diff = np.diff(a, axis=0)
    ui = np.ones(len(a), 'bool')
    ui[1:] = (diff != 0).any(axis=1)

    return ui[reorder]
   
def posterior2(bo, X):
    ur = unique_rows(bo.X)
    bo.gp.fit(bo.X[ur], bo.Y[ur])
    mu, sigma2 = bo.gp.predict(X, eval_MSE=True)
    return mu, np.sqrt(sigma2), bo.util.utility(X, bo.gp, bo.Y.max())
   
def plot_gp(bo, x,kappa,nsplit,acq,label):
    
    fig = plt#.figure(figsize=(16, 10))
    plt.clf()
    fig.suptitle('{} GP nsplit {}, k: {},acq {}. {} Steps'.format(label,nsplit,kappa,acq,len(bo.X)), fontdict={'size':30})
    
    gs = gridspec.GridSpec(2, 1, height_ratios=[3, 1]) 
    axis = plt.subplot(gs[0])
    acq = plt.subplot(gs[1])
    
    mu, sigma = posterior(bo,x)
    
    axis.plot(x, mu, '--', color='k', label='Prediction')
   
    axis.fill(np.concatenate([x, x[::-1]]), 
              np.concatenate([mu - kappa * sigma, (mu + kappa * sigma)[::-1]]),
        alpha=.6, fc='c', ec='None', label='95% confidence interval')
    
    axis.set_xlim((x.min()-(x.max()-x.min())*0.001, x.max()+(x.max()-x.min())*0.001))
    axis.set_ylim((None,None))
    axis.set_ylabel('f(x)', fontdict={'size':20})
    axis.set_xlabel('x', fontdict={'size':20})
    
    utility = bo.util.utility(x.reshape((-1, 1)), bo.gp, 0)
    acq.plot(x, utility, label='Utility Function', color='purple')
    acq.plot(x[np.argmax(utility)], np.max(utility), '*', markersize=15, 
             label=u'Next Best Guess', markerfacecolor='gold', markeredgecolor='k', markeredgewidth=1)
    acq.set_xlim((x.min()-(x.max()-x.min())*0.001, x.max()+(x.max()-x.min())*0.001))
    acq.set_ylim((np.min(utility)-(np.max(utility)-np.min(utility))*0.05, np.max(utility)+(np.max(utility)-np.min(utility))*0.05))
    acq.set_ylabel('Utility', fontdict={'size':20})
    acq.set_xlabel('x', fontdict={'size':20})
    
    axis.plot(bo.X.flatten()[:-1], bo.Y[:-1], 'o', markersize=7, label=u'Observations', color='r')
    axis.plot(bo.X.flatten()[-1:], bo.Y[-1:], 'D', markersize=9, label=u'Observations', color=(0.9, 0.9, 0.0))

    axis.legend(loc=2, bbox_to_anchor=(1.01, 1), borderaxespad=0.)
    acq.legend(loc=2, bbox_to_anchor=(1.01, 1), borderaxespad=0.)
## load dataset
plt.rcParams['figure.figsize']=(20.0, 10.0);


plot=True
if(len(sys.argv)>1):
	if(sys.argv[1]=='n'):
		plot=False
if plot:
	plt.ion()
else:
	plt.ioff()
import theano
import theano.tensor as T
rng = np.random

label='3newap'
X,Y=utils.getdata(label,-1)
print X.shape,Y.shape
nobj=3


ind=np.array([[0,1,6,7,8,9,10,11],[2,3,12,13,14,15,16,17],[4,5,18,19,20,21,22,23]])
nfeats4obj=ind.shape[1]

newX=[]
newY=[]
for i in xrange(nobj):
	tY=Y[:,2*i:2*i+2]
	tX=X[:,ind[i]]
	for j in xrange(nobj):
		if j!=i:
			tX=np.concatenate((tX,X[:,ind[i]],X[:,ind[j]]),axis=1)
#	poly = PolynomialFeatures(2,include_bias=False)
#	tX=poly.fit_transform(tX)
	nfeats4obj=tX.shape[1]/(2*nobj-1)
	print nfeats4obj
	if newX==[]:
		newX=tX
		newY=tY
	else:
		newX=np.concatenate((newX,tX),axis=0)
		newY=np.concatenate((newY,tY),axis=0)
print newX.shape,newY.shape


nsplit=20
# Declare Theano symbolic variables
x = T.matrix("x")
y = T.matrix("y")


wrel = theano.shared(rng.randn(nfeats4obj*2,nsplit)/np.sqrt(nfeats4obj*2), name="wrel")
brel = theano.shared(np.zeros(nsplit), name="brel")
baggr = theano.shared(np.zeros(nsplit), name="baggr")

w = theano.shared(rng.randn(nfeats4obj+nsplit,2)/np.sqrt(nfeats4obj+nsplit), name="w")
b = theano.shared(np.zeros(2), name="b")

targetf=x[:,0:nfeats4obj]

xrel=x[:,nfeats4obj+i*nfeats4obj:nfeats4obj+(i+2)*nfeats4obj]
outrel=T.nnet.sigmoid(T.dot(xrel,wrel)+brel)
for j in xrange(nobj-1):
	i=j+1
	xrel=x[:,nfeats4obj+i*nfeats4obj:nfeats4obj+(i+2)*nfeats4obj]
	outrel+=T.nnet.sigmoid(T.dot(xrel,wrel)+brel)
outrel+=baggr

pred=T.dot(T.concatenate([targetf,outrel],axis=1),w)+b

penaltyh = theano.shared(0.0001, name="penaltyh")
penaltyo = theano.shared(0.0001, name="penaltyo")

allparam=[wrel,brel,w,b,baggr]



cost2 = T.mean((y-pred)**2)+penaltyo*((T.abs_(w)).sum())+penaltyh*((T.abs_(wrel)).sum())#+penaltyh*((T.abs_(wh2)**exppen).sum())


lrdef=0.005
learnrate = theano.shared(lrdef, name="learnrate")
updates=utilstheano.RMSpropmom(cost2,allparam,learnrate,0.98)



print("Initial model:")
#print(w.get_value())
#print(b.get_value())
print 'num leaves',nsplit
# Compile
train = theano.function(
          inputs=[x,y],
          outputs=cost2,
          updates=updates,
          allow_input_downcast=True)

predict = theano.function(inputs=[x], outputs=pred,allow_input_downcast=True)
costeval = theano.function(inputs=[x,y], outputs=cost2,allow_input_downcast=True)

#leafeval = theano.function(inputs=[x], outputs=T.dot(x,wh),allow_input_downcast=True)
ttest = theano.function(inputs=[x], outputs=xrel,allow_input_downcast=True)
#testd = theano.function(inputs=[x,y], outputs=T.sum(leaf*gauss,axis=1),allow_input_downcast=True)

#print 'testd', testd(X[:4,:],Y[:4])

#print 'pred', predict(newX[:10,:]).shape
colormap = np.array(['r', 'g', 'b','y','c','magenta','coral','g','sienna','pink','indigo','peru','m','tomato','hotpink'])


bestval=float('inf')



def mixexpert(X,Y,logalpha1,logalpha2,expp=2.,expe=2.,cvfolds=12,slowness=12,ploterr=False):
	if ploterr:
		plt.clf()
		plt.gca().cla()
	global bestval,savedw,savedb,savedwh,savedbh,minv,minTr
	alpha1=10**logalpha1
	alpha2=10**logalpha2
	
	nf=5
	olderr=float('inf')
	err=0.0
	training_steps = int(nf*5000)
	start_time = time.time()
	penaltyh.set_value(alpha1)
	penaltyo.set_value(alpha2)
	if cvfolds>1:
		kf = KFold(len(Y), n_folds=cvfolds,shuffle=True)
		listkf=[(train_index, test_index) for train_index, test_index in kf]
	else:
		listkf=[(X, [])]
	nd=0
	MSE_v=0.0
	loglkl=0.0
	loglkl1=0.0
	
#	for train_index, test_index in kf:
	for train_index, test_index in listkf[0:slowness]:
		Xtr=X[train_index]
		Ytr=Y[train_index]
		Xval=X[test_index]
		Yval=Y[test_index]
		
		
		minTr=np.min(Xtr,0)
		minv=(np.max(Xtr,0)-np.min(Xtr,0))
		minv[minv==0]=1.
		Xtr=(Xtr-minTr)/minv
		Xval=(Xval-minTr)/minv
		
		#print mean,minv,np.min(Xtr),np.max(Xtr),np.min(Xval),np.max(Xval)
		
		'''
		mean=np.mean(Ytr,0)
		minv=(np.max(Ytr,0)-np.min(Ytr,0))
		Ytr=(Ytr-np.min(Ytr,0))/minv
		Yval=(Yval-np.min(Ytr,0))/minv
		'''
		batch_size= Xtr.shape[0]/nf
		besterrval=float('inf')
		besterr=float('inf')
		learnrate.set_value(lrdef)
		
		w.set_value(rng.randn(nfeats4obj+nsplit,2)/np.sqrt(nfeats4obj+nsplit))
		b.set_value(np.zeros(2))
		wrel.set_value(rng.randn(nfeats4obj*2,nsplit)/np.sqrt(nfeats4obj*2))
		brel.set_value(np.zeros(nsplit))
		baggr.set_value(np.zeros(nsplit))

		movavg=1000.0
		movvar=0
		arr = np.arange(nf)
		np.random.shuffle(arr)
		bestvalindex=0
		trainvec=[]
		valvec=[]
		learnvec=[]
		costvec=[]
		numparamh=np.count_nonzero(wrel.get_value()>0.0001)
		numparam=np.count_nonzero(w.get_value()>0.0001)
		for i in range(training_steps):
			epoc=i/nf
			fold= arr[i%nf]
			#print i,fold,i%nf,arr
			trainb=Xtr[fold*batch_size:(fold+1)*batch_size,:]
			
			err += train(trainb, Ytr[fold*batch_size:(fold+1)*batch_size] )
			
			if(fold==nf-1):
				'''
				movavg=movavg*0.9+err*0.1
				movvar=movvar*0.9+0.1*(err-movavg)**2
				print (err/10)**0.5,(movavg/10)**0.5,movvar**0.5
				'''
				if(err<olderr):
					learnrate.set_value(learnrate.get_value()*1.01)
				else:
					learnrate.set_value(learnrate.get_value()*0.95)
		#		print 'score',(err/nf)**0.5,'learnrate',learnrate.get_value(),'penaltyo',penaltyo.get_value()
				olderr=err
				err=0.0
				duration = time.time() - start_time
		#		print 'time',duration
				start_time = time.time()
			printevery=2
			plotevery=100
			if(i%(nf*printevery)==0):
				#print np.nonzero(w.get_value()>0.001)
				#print w.get_value()
				#print np.unique(predictcluster(Xtr)).shape
				tmptr=  np.mean((predict(Xtr)-Ytr)**2)
				predval=predict(Xval)
				tmpval= np.mean((predval-Yval)**2)
				costloss=costeval(Xtr,Ytr)**0.5
				#print w.get_value(),b.get_value()
				if (i-bestvalindex>400*nf and i>400*nf) or learnrate.get_value()<1e-8:
					print 'stop',tmptr**0.5,tmpval**0.5
					break
				if(i%(nf*100)==0):
					print 'accuracy',i/nf,tmptr**0.5,tmpval**0.5,'learnrate',learnrate.get_value(),np.count_nonzero(wrel.get_value()>0.0001),np.count_nonzero(w.get_value()>0.0001)
					print 'best',besterr**0.5,besterrval**0.5,numparamh,numparam
				
				#if(tmpval<besterrval):
				besterrval=tmpval
				besterr=tmptr
				bestvalindex=i
				numparamh=np.count_nonzero(wrel.get_value()>0.0001)
				numparam=np.count_nonzero(w.get_value()>0.0001)
				'''	
				#	if(tmpval<bestval):
				#	bestval=tmpval
					savedw=w.get_value()
					savedb=b.get_value()
					savedwh=wh.get_value()
					savedbh=bh.get_value()
				'''
				#	for nn in xrange(0,nsplit):
				#		print wh.get_value()[:,nn].argsort()[-10:][::-1]+1,np.sort(wh.get_value()[:,nn])[-10:][::-1]
				trainvec+=[tmptr**0.5]
				valvec+=[tmpval**0.5]
				costvec+=[costloss]
				learnvec+=[learnrate.get_value()*10]
				if False and plot and len(trainvec)>20 and i%(nf*plotevery)==0:
					plt.clf()
					#plt.autoscale(enable=False)
					#plt.axis([0,750,0,0.1])				
					plt.plot(range(0,len(trainvec)*printevery,printevery)[20:], trainvec[20:], color='black',linewidth=2)
					plt.plot(range(0,len(valvec)*printevery,printevery)[20:], valvec[20:], color='blue',linewidth=2)
					plt.plot(range(0,len(costvec)*printevery,printevery)[20:], costvec[20:], color='red',linewidth=2)
					plt.plot(range(0,len(learnvec)*printevery,printevery)[20:], learnvec[20:], color='green',linewidth=2)
					plt.draw()
		'''
		w.set_value( savedw)
		b.set_value( savedb)
		wh.set_value( savedwh)
		bh.set_value( savedbh)
		'''
		print wrel.get_value().T
		
#		print Y[tmperr.argsort()[-10:][::-1]],X[tmperr.argsort()[-10:][::-1]]
		MSE_v+=	besterrval
		print 'bestfold',alpha1,alpha2,expp,expe,'val',besterrval**0.5,'train',besterr**0.5
	MSE_v=(MSE_v/min(slowness,cvfolds))
	print alpha1,alpha2,expp,MSE_v**0.5
	if(slowness>=cvfolds):
		f = open('relnet.txt', 'a')
		f.write(label+' ')
		f.write('nsplit '+str(nsplit)+' ')
		f.write("alpha1 "+str(alpha1)+" alpha2 "+str(alpha2)+" exp "+str(expp)+" error "+str(MSE_v**0.5)+" param split "+str(numparamh)+" param lr "+str(numparam)+'\n')
		f.close()
	if ploterr:
#		plt.axis([-0.5,0.5,-0.07,0.43])
		plt.autoscale(enable=True)
#		plt.savefig(label+str(logalpha1)+" "+str(logalpha2) +str(expp)+" "+str(expe)+" "+str(cvfolds)+" "+str(slowness)+" "+".png", dpi=400)
		plt.draw()
	return (MSE_v**0.5)


def target(logalpha):
	x=(logalpha+6)*3
	return ((np.exp(-(x - 2)**2) + np.exp(-(x - 6)**2/10) + 1/ (x**2 + 1)-2)*2+np.sin(x*30)*0.1)#+ np.sin(x*10)*0.1)#*1000 np.random.randn()/5

def plotpoints():
	return 0


X_lower = np.array([-16.,-16.])
X_upper = np.array([-2.,-2.])
dims =X_lower.shape[0]

##
def plot_2d(bo,X,name=None):
	plt.ion()
	x=X[:,0]
	y=X[:,1]
	mu, s, ut = posterior2(bo, X)
	

	gridsize=50
	plt.clf()
	# fig.suptitle('Bayesian Optimization in Action', fontdict={'size':30})
	
	# GP regression output
	ax = plt.subplot(3,1, 1)
	ax.set_title('Gausian Process Predicted Mean', fontdict={'size':15})
	im00 = plt.hexbin(x, y, C=mu, gridsize=gridsize, cmap=plt.get_cmap('jet'), bins=None, vmin=mu.min(), vmax=mu.max())
	plt.axis([x.min(), x.max(), y.min(), y.max()])
	plt.plot(bo.X[:, 0], bo.X[:, 1], 'D', markersize=4, color='k', label='Observations')
	plt.colorbar(im00)
	ax = plt.subplot(3,1, 2)
	ax.set_title('Gausian Process Variance', fontdict={'size':15})
	im01 = plt.hexbin(x, y, C=s, gridsize=gridsize, cmap=plt.get_cmap('jet'), bins=None, vmin=s.min(), vmax=s.max())
	plt.axis([x.min(), x.max(), y.min(), y.max()])
	plt.colorbar(im01)
	ax = plt.subplot(3,1, 3)
	ax.set_title('Acquisition Function', fontdict={'size':15})
	im11 = plt.hexbin(x, y, C=ut, gridsize=gridsize, cmap=plt.get_cmap('jet'), bins=None, vmin=ut.min(), vmax=ut.max())
	plt.colorbar(im11)
	np.where(ut.reshape((100, 100)) == ut.max())[0]
	np.where(ut.reshape((100, 100)) == ut.max())[1]
	
	plt.plot([np.where(ut.reshape((100, 100)) == ut.max())[1]/50., 
	               np.where(ut.reshape((100, 100)) == ut.max())[1]/50.], 
	              [0, 6], 
	              'k-', lw=2, color='k')
	
	plt.plot([0, 6],
	              [np.where(ut.reshape((100, 100)) == ut.max())[0]/50., 
	               np.where(ut.reshape((100, 100)) == ut.max())[0]/50.], 
	              'k-', lw=2, color='k')
	
	plt.axis([x.min(), x.max(), y.min(), y.max()])
	
#	for im, axis in zip([im00, im01, im11], ax.flatten()):
#	    cb = plt.colorbar(im, ax=axis)
	    # cb.set_label('Value')
	
	if name is None:
	    name = '_'
	
	plt.tight_layout()
	
	# Save or show figure?
	# fig.savefig('bo_eg_' + name + '.png')
	plt.draw()
#	plt.close(fig)

def mixexpert2(logalpha1,logalpha2,expp=1.):
	return -mixexpert(newX,newY,logalpha1,logalpha2,expp=expp,cvfolds=6,slowness=6)


print label
bo = BayesianOptimization(mixexpert2, {'logalpha1': ( X_lower[0],X_upper[0]),'logalpha2': ( X_lower[1],X_upper[1])}) #,'expp': ( X_lower[2],X_upper[2])
#bo.explore({'logalpha': [(maxalpha+minalpha)/2.]})
#bo.explore({'logalpha1': [-10.46778014], 'logalpha2': [-7.01362049]})
gp_params = {'corr': 'squared_exponential',
             'nugget': 0.006**2,"theta0": 1,"thetaL": 0.01, "thetaU": 10,"normalize":True}
#gp_params = {'corr': 'cubic'}
#bo.maximize(init_points=2, n_iter=0, acq='ucb', kappa=5, **gp_params)
acq='ei'
kappa=7.
xi=0.007
print bo.bounds
bo.maximize(init_points=2, n_iter=0, acq=acq,kappa=kappa, xi=xi,**gp_params)
plt.figure(figsize=(7, 10))

for i in xrange(1,301):
#	print bo.X
#	kappa+=0.0
#	xi=0.07/np.max(np.abs(bo.Y))
	
#	x = np.linspace(minalpha,maxalpha, 1000)
#	plot_gp(bo,x,kappa,nsplit,acq,label)
#	plt.savefig(acq+str(i)+" xi "+str(xi)+" k "+str(kappa)+'t.png')
#	plt.draw()
	aa, bb = np.meshgrid(np.linspace(X_lower[0], X_upper[0], 100), np.linspace(X_lower[1], X_upper[1], 100))
	if plot:
		XXX=np.append(aa.reshape(-1,1),bb.reshape(-1,1),1)
		plot_2d(bo,XXX,"{:03}".format(len(bo.X)))
	bo.maximize(init_points=0,n_iter=1,acq=acq, kappa=kappa, xi=xi, **gp_params)
	'''
	mu,sigma,uf=posterior2(bo, XXX)
	valsacq=uf.reshape(100,100)
	valsacq-=valsacq.min()
	valsacq/=valsacq.max()-valsacq.min()+1e-100
	plt.ion()
	plt.clf()
	plt.pcolormesh(aa, bb, valsacq, cmap='RdBu', vmin=0, vmax=1)
	# set the limits of the plot to the limits of the data
	plt.axis([aa.min(), aa.max(), bb.min(), bb.max()])
	plt.colorbar()
	plt.draw()
	'''
	print label,acq,nsplit,kappa
#plt.savefig(label+acq+" xi "+str(xi)+" k "+str(kappa)+'.png')
#plt.show()

print(bo.res['max'])
print(bo.res['all'])
print(bo.res)
exit()
##







print dims
# Set the method that we will use to optimize the acquisition function
maximizer = stochastic_local_search

# Defining the method to model the objective function
kernel = GPy.kern.Exponential(input_dim=dims)
model = GPyModel(kernel, optimize=True, noise_variance=1e-5, num_restarts=10)

acqf='ucb'
# The acquisition function that we optimize in order to pick a new x
if acqf=='ei':
	acquisition_func = EI(model, X_upper=X_upper, X_lower=X_lower, compute_incumbent=compute_incumbent, par=0.05)  # par is the minimum improvement that a point has to obtain
else:
	acquisition_func = UCB(model, X_upper=X_upper, X_lower=X_lower, compute_incumbent=compute_incumbent, par=5)
xv = np.array([np.random.uniform(X_lower, X_upper, dims)])
xv = np.array([[-10.46778014 , -7.01362049]])

print xv
dimfold=1
slowness=X.shape[0]/dimfold
cvfolds=X.shape[0]/dimfold
yv = np.array([[mixexpert(X,Y,xv[0][0],xv[0][1],2,2,cvfolds=cvfolds,slowness=slowness,ploterr=True)]])
print yv

for i in xrange(500):
	# Fit the model on the data we observed so far
	model.train(xv, yv)
	
	if acqf=='ucb':
		deltab=0.5
		acquisition_func.par=1*np.sqrt( 2.*np.log((i+1)**(2)*2*np.pi**2/(3*deltab))+2.*dims*np.log((i+1)**(2)*dims*np.log(4*dims/deltab)**0.5)  )
	print 'par',acquisition_func.par
	
	# Update the acquisition function model with the retrained model
	acquisition_func.update(model)
	
	# Optimize the acquisition function to obtain a new point
	new_x = maximizer(acquisition_func, X_lower, X_upper)
	print new_x
	# Evaluate the point and add the new observation to our set of previous seen points
	if i%10==0 and dimfold>1:
		dimfold-=1
		slowness=X.shape[0]/dimfold
		cvfolds=X.shape[0]/dimfold
	print 'cvfolds',cvfolds
	new_y = np.array([[mixexpert(X,Y,new_x[0][0],new_x[0][1],2,2,cvfolds=cvfolds,slowness=slowness,ploterr=False)]])
	xv = np.append(xv, new_x, axis=0)
	yv = np.append(yv, new_y, axis=0)
	
	print acqf,label,nsplit,np.min(yv)
	
	print 'best',np.min(yv), xv[np.argmin(yv.reshape(-1,)),:]
	nyv=yv.reshape(-1,)
	normyv=(nyv-nyv.min())/(nyv.max()-nyv.min())
#	fig = plt.figure(figsize=(16, 10))
	aa, bb = np.meshgrid(np.linspace(X_lower[0], X_upper[0], 100), np.linspace(X_lower[1], X_upper[1], 100))
	valsacq=acquisition_func(np.append(aa.reshape(-1,1),bb.reshape(-1,1),1)).reshape(100,100)
	valsacq-=valsacq.min()
	valsacq/=valsacq.max()-valsacq.min()+1e-100
	plt.ion()
	plt.clf()
	plt.pcolormesh(aa, bb, valsacq, cmap='RdBu', vmin=0, vmax=1)
	# set the limits of the plot to the limits of the data
	plt.axis([aa.min(), aa.max(), bb.min(), bb.max()])
	plt.colorbar()
#	plt.show()
#	acquisition_func.plot(fig, a, b, plot_attr={"color": "red"}, resolution=1000)

	plt.suptitle('{}: cv {}, nsplit {}, par: {}, acq {}, best {}. {} Steps'.format(label,cvfolds,nsplit,acquisition_func.par,acqf,np.min(yv),i+1), fontdict={'size':30})
	
	plt.scatter(xv[:,0], xv[:,1],s=100,linewidth='1', c=normyv,cmap=plt.get_cmap('jet'))
	plt.scatter(xv[np.argmin(yv.reshape(-1,)),0], xv[np.argmin(yv.reshape(-1,)),1],s=130,linewidth='2', c=normyv[np.argmin(yv.reshape(-1,)):np.argmin(yv.reshape(-1,))+1],cmap=plt.get_cmap('jet'))
	plt.draw()
	
#yv=yv[-100:]
#xv=xv[-100:]
#print np.argmin(yv)
mixexpert(X,Y,xv[np.argmin(yv),0],xv[np.argmin(yv),1],2.,2.,cvfolds,slowness,False)

#plotbest(X,Y)
exit()































bestalpha=-1
bestscore=float('-inf')
bestalphaSE=-1
bestscoreSE=float('inf')
for alpha in [1e-5,1e-4,5e-4,1e-3,1e-2]:
	RMSE_v=mixexpert(alpha)
	if(RMSE_v<bestscoreSE):
		bestscoreSE=RMSE_v
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

print 'nsplit',nsplit
print 'best',bestscoreSE**0.5,bestalphaSE
