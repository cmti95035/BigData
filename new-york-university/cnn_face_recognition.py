# -*-coding:utf8-*-#
import os
import sys
import time
import cPickle

import numpy
from PIL import Image

import theano
import theano.tensor as T
from theano.tensor.signal import downsample
from theano.tensor.nnet import conv


def load_params(params_file):
    f=open(params_file,'rb')
    layer0_params=cPickle.load(f)
    layer1_params=cPickle.load(f)
    layer2_params=cPickle.load(f)
    layer3_params=cPickle.load(f)
    f.close()
    return layer0_params,layer1_params,layer2_params,layer3_params

def load_data(dataset_path):
    img = Image.open(dataset_path)
    img_ndarray = numpy.asarray(img, dtype='float64')/256

    faces=numpy.empty((400,2679))
    for row in range(20):
	   for column in range(20):
		faces[row*20+column]=numpy.ndarray.flatten(img_ndarray [row*57:(row+1)*57,column*47:(column+1)*47])

    label=numpy.empty(400)
    for i in range(40):
	label[i*10:i*10+10]=i
    label=label.astype(numpy.int)

    train_data=numpy.empty((320,2679))
    train_label=numpy.empty(320)
    valid_data=numpy.empty((40,2679))
    valid_label=numpy.empty(40)
    test_data=numpy.empty((40,2679))
    test_label=numpy.empty(40)

    for i in range(40):
	train_data[i*8:i*8+8]=faces[i*10:i*10+8]
	train_label[i*8:i*8+8]=label[i*10:i*10+8]
	valid_data[i]=faces[i*10+8]
	valid_label[i]=label[i*10+8]
	test_data[i]=faces[i*10+9]
	test_label[i]=label[i*10+9]

    def shared_dataset(data_x, data_y, borrow=True):
        shared_x = theano.shared(numpy.asarray(data_x,
                                               dtype=theano.config.floatX),
                                 borrow=borrow)
        shared_y = theano.shared(numpy.asarray(data_y,
                                               dtype=theano.config.floatX),
                                 borrow=borrow)
        return shared_x, T.cast(shared_y, 'int32')



    train_set_x, train_set_y = shared_dataset(train_data,train_label)
    valid_set_x, valid_set_y = shared_dataset(valid_data,valid_label)
    test_set_x, test_set_y = shared_dataset(test_data,test_label)
    rval = [(train_set_x, train_set_y), (valid_set_x, valid_set_y),
            (test_set_x, test_set_y)]
    return rval, faces, label



class LogisticRegression(object):
    def __init__(self, input, n_in, n_out, W=None, b=None):
        if W is None:
            self.W = theano.shared(
                value=numpy.zeros(
                    (n_in, n_out),
                    dtype=theano.config.floatX
                ),
                name='W',
                borrow=True
            )
        else: self.W = W
        if b is None:
            self.b = theano.shared(
                value=numpy.zeros(
                    (n_out,),
                    dtype=theano.config.floatX
                ),
                name='b',
                borrow=True
            )
        else: self.b = b
        self.p_y_given_x = T.nnet.softmax(T.dot(input, self.W) + self.b)
        self.y_pred = T.argmax(self.p_y_given_x, axis=1)
        self.params = [self.W, self.b]

    def negative_log_likelihood(self, y):
        return -T.mean(T.log(self.p_y_given_x)[T.arange(y.shape[0]), y])

    def errors(self, y):
        if y.ndim != self.y_pred.ndim:
            raise TypeError(
                'y should have the same shape as self.y_pred',
                ('y', y.type, 'y_pred', self.y_pred.type)
            )
        if y.dtype.startswith('int'):
            return T.mean(T.neq(self.y_pred, y))
        else:
            raise NotImplementedError()


class HiddenLayer(object):
    def __init__(self, rng, input, n_in, n_out, W=None, b=None,
                 activation=T.tanh):

        self.input = input

        if W is None:
            W_values = numpy.asarray(
                rng.uniform(
                    low=-numpy.sqrt(6. / (n_in + n_out)),
                    high=numpy.sqrt(6. / (n_in + n_out)),
                    size=(n_in, n_out)
                ),
                dtype=theano.config.floatX
            )
            if activation == theano.tensor.nnet.sigmoid:
                W_values *= 4
            W = theano.shared(value=W_values, name='W', borrow=True)

        if b is None:
            b_values = numpy.zeros((n_out,), dtype=theano.config.floatX)
            b = theano.shared(value=b_values, name='b', borrow=True)

        self.W = W
        self.b = b

        lin_output = T.dot(input, self.W) + self.b
        self.output = (
            lin_output if activation is None
            else activation(lin_output)
        )
        # parameters of the model
        self.params = [self.W, self.b]


class LeNetConvPoolLayer(object):

    def __init__(self, rng, input, filter_shape, image_shape, poolsize=(2, 2), W=None, b=None):

        assert image_shape[1] == filter_shape[1]
        self.input = input

        fan_in = numpy.prod(filter_shape[1:])
        fan_out = (filter_shape[0] * numpy.prod(filter_shape[2:]) /
                   numpy.prod(poolsize))

        # initialize weights with random weights
        W_bound = numpy.sqrt(6. / (fan_in + fan_out))
        if W is None:
            self.W = theano.shared(
                numpy.asarray(
                    rng.uniform(low=-W_bound, high=W_bound, size=filter_shape),
                    dtype=theano.config.floatX
                ),
                borrow=True
            )
        else: self.W = W

        # the bias is a 1D tensor -- one bias per output feature map
        b_values = numpy.zeros((filter_shape[0],), dtype=theano.config.floatX)
        self.b = theano.shared(value=b_values, borrow=True) if b is None else b

        conv_out = conv.conv2d(
            input=input,
            filters=self.W,
            filter_shape=filter_shape,
            image_shape=image_shape
        )

        pooled_out = downsample.max_pool_2d(
            input=conv_out,
            ds=poolsize,
            ignore_border=True
        )

        self.output = T.tanh(pooled_out + self.b.dimshuffle('x', 0, 'x', 'x'))

        # store parameters of this layer
        self.params = [self.W, self.b]


def save_params(param1,param2,param3,param4):  
        import cPickle  
        write_file = open('params.pkl', 'wb')   
        cPickle.dump(param1, write_file, -1)
        cPickle.dump(param2, write_file, -1)
        cPickle.dump(param3, write_file, -1)
        cPickle.dump(param4, write_file, -1)
        write_file.close()  




def evaluate_olivettifaces(learning_rate=0.05, n_epochs=200,
                    dataset='olivettifaces.gif',
                    nkerns=[5, 10], batch_size=40):   

    rng = numpy.random.RandomState(23455)
    datasets, faces, label = load_data(dataset)
    train_set_x, train_set_y = datasets[0]
    valid_set_x, valid_set_y = datasets[1]
    test_set_x, test_set_y = datasets[2]

    n_train_batches = train_set_x.get_value(borrow=True).shape[0]
    n_valid_batches = valid_set_x.get_value(borrow=True).shape[0]
    n_test_batches = test_set_x.get_value(borrow=True).shape[0]
    n_train_batches /= batch_size
    n_valid_batches /= batch_size
    n_test_batches /= batch_size

    index = T.lscalar()
    x = T.matrix('x')  
    y = T.ivector('y')



    print '... building the model'

    # Reshape matrix of rasterized images of shape (batch_size, 57 * 47)
    # to a 4D tensor, compatible with our LeNetConvPoolLayer
    # (57, 47) is the size of  images.
    layer0_input = x.reshape((batch_size, 1, 57, 47))

    # 4D output tensor is thus of shape (batch_size, nkerns[0], 26, 21)
    layer0 = LeNetConvPoolLayer(
        rng,
        input=layer0_input,
        image_shape=(batch_size, 1, 57, 47),
        filter_shape=(nkerns[0], 1, 5, 5),
        poolsize=(2, 2)
    )

    # 4D output tensor is thus of shape (batch_size, nkerns[1], 11, 8)
    layer1 = LeNetConvPoolLayer(
        rng,
        input=layer0.output,
        image_shape=(batch_size, nkerns[0], 26, 21),
        filter_shape=(nkerns[1], nkerns[0], 5, 5),
        poolsize=(2, 2)
    )

    layer2_input = layer1.output.flatten(2)
    layer2 = HiddenLayer(
        rng,
        input=layer2_input,
        n_in=nkerns[1] * 11 * 8,
        n_out=2000,      
        activation=T.tanh
    )

    layer3 = LogisticRegression(input=layer2.output, n_in=2000, n_out=40)   


    cost = layer3.negative_log_likelihood(y)
    
    test_model = theano.function(
        [index],
        layer3.errors(y),
        givens={
            x: test_set_x[index * batch_size: (index + 1) * batch_size],
            y: test_set_y[index * batch_size: (index + 1) * batch_size]
        }
    )

    validate_model = theano.function(
        [index],
        layer3.errors(y),
        givens={
            x: valid_set_x[index * batch_size: (index + 1) * batch_size],
            y: valid_set_y[index * batch_size: (index + 1) * batch_size]
        }
    )

    params = layer3.params + layer2.params + layer1.params + layer0.params
    grads = T.grad(cost, params)
    updates = [
        (param_i, param_i - learning_rate * grad_i)
        for param_i, grad_i in zip(params, grads)
    ]
    train_model = theano.function(
        [index],
        cost,
        updates=updates,
        givens={
            x: train_set_x[index * batch_size: (index + 1) * batch_size],
            y: train_set_y[index * batch_size: (index + 1) * batch_size]
        }
    )


    print '... training'
    patience = 800
    patience_increase = 2  
    improvement_threshold = 0.99  
    validation_frequency = min(n_train_batches, patience / 2) 


    best_validation_loss = numpy.inf
    best_iter = 0
    test_score = 0.
    start_time = time.clock()

    epoch = 0
    done_looping = False

    while (epoch < n_epochs) and (not done_looping):
        epoch = epoch + 1
        for minibatch_index in xrange(n_train_batches):

            iter = (epoch - 1) * n_train_batches + minibatch_index

            if iter % 100 == 0:
                print 'training @ iter = ', iter
            cost_ij = train_model(minibatch_index)
            
            if (iter + 1) % validation_frequency == 0:

                # compute zero-one loss on validation set
                validation_losses = [validate_model(i) for i
                                     in xrange(n_valid_batches)]
                this_validation_loss = numpy.mean(validation_losses)
                print('epoch %i, minibatch %i/%i, validation error %f %%' %
                      (epoch, minibatch_index + 1, n_train_batches,
                       this_validation_loss * 100.))

                # if we got the best validation score until now
                if this_validation_loss < best_validation_loss:

                    #improve patience if loss improvement is good enough
                    if this_validation_loss < best_validation_loss *  \
                       improvement_threshold:
                        patience = max(patience, iter * patience_increase)

                    # save best validation score and iteration number
                    best_validation_loss = this_validation_loss
                    best_iter = iter
		    save_params(layer0.params,layer1.params,layer2.params,layer3.params)

                    # test it on the test set
                    test_losses = [
                        test_model(i)
                        for i in xrange(n_test_batches)
                    ]
                    test_score = numpy.mean(test_losses)
                    print(('     epoch %i, minibatch %i/%i, test error of '
                           'best model %f %%') %
                          (epoch, minibatch_index + 1, n_train_batches,
                           test_score * 100.))

            if patience <= iter:
                done_looping = True
                break

    end_time = time.clock()
    print('Optimization complete.')
    print('Best validation score of %f %% obtained at iteration %i, '
          'with test performance %f %%' %
          (best_validation_loss * 100., best_iter + 1, test_score * 100.))
    print >> sys.stderr, ('The code for file ' +
                          os.path.split(__file__)[1] +
                          ' ran for %.2fm' % ((end_time - start_time) / 60.))

def use_CNN(dataset='olivettifaces.gif',params_file='params.pkl',nkerns=[5, 10]):   
    
    datasets,faces,label=load_data(dataset)
    face_num = faces.shape[0]   
  
    layer0_params,layer1_params,layer2_params,layer3_params=load_params(params_file)
    
    x = T.matrix('x')  
    rng = numpy.random.RandomState(23455)
    layer0_input = x.reshape((face_num, 1, 57, 47)) 
    layer0 = LeNetConvPoolLayer(
        rng,
        input=layer0_input,
        image_shape=(face_num, 1, 57, 47),
        filter_shape=(nkerns[0], 1, 5, 5),
        poolsize=(2, 2),
        W=layer0_params[0],
        b=layer0_params[1],
    )

    layer1 = LeNetConvPoolLayer(
        rng,
        input=layer0.output,
        image_shape=(face_num, nkerns[0], 26, 21),
        filter_shape=(nkerns[1], nkerns[0], 5, 5),
        poolsize=(2, 2),
        W=layer1_params[0],
        b=layer1_params[1],
    )

    layer2_input = layer1.output.flatten(2)
    layer2 = HiddenLayer(
        rng,
        input=layer2_input,
        W=layer2_params[0],
        b=layer2_params[1],
        n_in=nkerns[1] * 11 * 8,
        n_out=2000,      
        activation=T.tanh
    )

    layer3 = LogisticRegression(input=layer2.output, W=layer3_params[0],b=layer3_params[1],n_in=2000, n_out=40)   
     
    f = theano.function(
        [x],    
        layer3.y_pred
    )
    
    pred = f(faces)
    

    for i in range(face_num): 
     if pred[i] != label[i]:
                print('picture: %i is person %i, but mis-predicted as person %i' %(i, label[i], pred[i]))


if __name__ == '__main__':
    evaluate_olivettifaces()
    use_CNN()