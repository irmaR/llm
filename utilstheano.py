import numpy as np

import theano
import theano.tensor as T
rng = np.random

# https://github.com/Newmu/Theano-Tutorials/blob/master/4_modern_net.py
def RMSprop(cost, params, lr=0.001, rho=0.9, epsilon=1e-6):
    grads = T.grad(cost=cost, wrt=params)
    updates = []
    for p, g in zip(params, grads):
        acc = theano.shared(p.get_value() * 0.)
        acc_new = rho * acc + (1 - rho) * g ** 2
        gradient_scaling = T.sqrt(acc_new + epsilon)
        g = g / gradient_scaling
        updates.append((acc, acc_new))
        updates.append((p, p - lr * g))
    return updates

def RMSpropmom(cost, params, lr=0.001, momentum=0.9,rho=0.9, epsilon=1e-10):
	grads = T.grad(cost=cost, wrt=params)
	memory_ = [theano.shared(np.zeros_like(p.get_value())) for p in params]
	updates = []
	for n, (p, g) in enumerate(zip(params, grads)):
		acc = theano.shared(p.get_value() * 0.)
		acc_new = rho * acc + (1 - rho) * g ** 2
		gradient_scaling = T.sqrt(acc_new + epsilon)
		g = g / gradient_scaling
		
		memory = memory_[n]
		update = momentum * memory - lr * g
		update2 = momentum * momentum * memory - (1 + momentum) * lr * g
		updates.append((memory, update))
		updates.append((p, p + update2))
		
		updates.append((acc, acc_new))
	return updates

# buggy! https://gist.github.com/kastnerkyle/816134462577399ee8b2
def RMSpropMomentum(cost, params, learning_rate, momentum, rescale=5.):
	grads = T.grad(cost=cost, wrt=params)
	running_square_ = [theano.shared(np.zeros_like(p.get_value())) for p in params]
	running_avg_ = [theano.shared(np.zeros_like(p.get_value()))  for p in params]
	memory_ = [theano.shared(np.zeros_like(p.get_value())) for p in params]
	grad_norm = T.sqrt(sum(map(lambda x: T.sqr(x).sum(), grads)))
	not_finite = T.or_(T.isnan(grad_norm), T.isinf(grad_norm))
	grad_norm = T.sqrt(grad_norm)
	scaling_num = rescale
	scaling_den = T.maximum(rescale, grad_norm)
	# Magic constants
	combination_coeff = 0.9
	minimum_grad = 1E-4
	updates = []
	for n, (param, grad) in enumerate(zip(params, grads)):
		grad = T.switch(not_finite, 0.1 * param,
						grad * (scaling_num / scaling_den))
		old_square = running_square_[n]
		new_square = combination_coeff * old_square + (
			1. - combination_coeff) * T.sqr(grad)
		old_avg = running_avg_[n]
		new_avg = combination_coeff * old_avg + (
			1. - combination_coeff) * grad
		rms_grad = T.sqrt(new_square - new_avg ** 2)
		rms_grad = T.maximum(rms_grad, minimum_grad)
		memory = memory_[n]
		update = momentum * memory - learning_rate * grad / rms_grad
		update2 = momentum * momentum * memory - (
			1 + momentum) * learning_rate * grad / rms_grad
		updates.append((old_square, new_square))
		updates.append((old_avg, new_avg))
		updates.append((memory, update))
		updates.append((param, param + update2))
	return updates


def graddesc(cost, params, learning_rate):
	grads = T.grad(cost=cost, wrt=params)
	updates = []
	for n, (param, grad) in enumerate(zip(params, grads)):
	    updates.append((param, param - learning_rate * grad))
	return updates

# https://gist.github.com/kastnerkyle/816134462577399ee8b2
def sgd_nesterov(cost, params, learning_rate, momentum):
	grads = T.grad(cost=cost, wrt=params)
	memory_ = [theano.shared(np.zeros_like(p.get_value())) for p in params]
	updates = []
	for n, (param, grad) in enumerate(zip(params, grads)):
	    memory = memory_[n]
	    update = momentum * memory - learning_rate * grad
	    update2 = momentum * momentum * memory - (1 + momentum) * learning_rate * grad
	    updates.append((memory, update))
	    updates.append((param, param + update2))
	return updates

