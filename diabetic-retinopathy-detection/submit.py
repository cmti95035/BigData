import graphlab as gl
import re
import random
from copy import copy
import os

# Run this script in the same directory as the train/ test/ and
# processed/ directories -- where you ran the preprocess.sh.  It will
# put a image-sframes/ directory with train and test SFrames in the
# save_path location below. 

save_path = "./"

# gl.set_runtime_config("GRAPHLAB_CACHE_FILE_LOCATIONS", os.path.expanduser("~/data/tmp/"))

# shuffle the training images
X = gl.image_analysis.load_images("processed/")
X["is_train"] = X["path"].apply(lambda p: "train" in p)

# Add in all the relevant information in places
source_f = lambda p: re.search("run-(?P<source>[^/]+)", p).group("source")
X["source"] = X["path"].apply(source_f)

extract_name = lambda p: re.search("[0-9]+_(right|left)", p).group(0)
X["name"] = X["path"].apply(extract_name)

X_train = X[X["is_train"] == True]
X_test = X[X["is_train"] != True]

# Add in the training labels
labels_sf = gl.SFrame.read_csv("trainLabels.csv")
label_d = dict( (d["image"], d["level"]) for d in labels_sf)

X_train["level"] = X_train["name"].apply(lambda p: label_d[p])

# Get roughly equal class representation by duplicating the different levels.
X_train_levels = [X_train[X_train["level"] == lvl] for lvl in [1,2,3,4] ]
n_dups = [int(round((1.0/5) / (float(xtl.num_rows()) / X_train.num_rows()) )) for xtl in X_train_levels]

for nd, xtl_src in zip(n_dups, X_train_levels):
    for i in range(nd):
        X_train = X_train.append(xtl_src)
        
# Do a poor man's random shuffle
X_train["_random_"] = random.sample(xrange(X_train.num_rows()), X_train.num_rows())
X_train = X_train.sort("_random_")
del X_train["_random_"]

# Save sframes to a bucket
X_train.save(save_path + "image-sframes/train")
X_test.save(save_path + "image-sframes/test")

import graphlab.aggregate as agg
import array
import numpy as np
import sys

train_path = "image-sframes/train-%d/"
valid_path = "image-sframes/validation-%d/"

X_data = gl.SFrame("image-sframes/train/")

def save_as_train_and_test(X, train_loc, valid_loc):

    # Can't just randomly sample the indices
    all_names = list(X["name"].unique())

    n_valid = (2 * len(all_names)) / 100
    
    random.shuffle(all_names)

    tr_names = gl.SArray(all_names[n_valid:])
    valid_names = gl.SArray(all_names[:n_valid])

    X_train = X.filter_by(tr_names, 'name')
    X_valid = X.filter_by(valid_names, 'name')

    X_train.save(train_loc)
    X_valid.save(valid_loc)

# The classes were already balanced by create_image_sframe, so we
# don't need to balance them below.
if not (os.path.exists(train_path % 0) and os.path.exists(valid_path % 0)):
    print "Skipping class 0; already present.  If error, remove these directories and restart."
    save_as_train_and_test(X_data, train_path % 0, valid_path % 0)

################################################################################
# Now do the other splitting parts

for mi in [1,2,3,4]:

    if os.path.exists(train_path % mi) and os.path.exists(valid_path % mi):
        print "Skipping class %d; already present.  If error, remove these directories and restart." % mi
        continue

    print "Running class %d" % mi
    
    X_data["class"] = (X_data["level"] >= mi)

    X_data_local = copy(X_data)

    n_class_0 = (X_data["class"] == 0).sum()
    n_class_1 = (X_data["class"] == 1).sum()

    if n_class_0 < n_class_1:

        num_to_sample = n_class_1 - n_class_0

        # Oversample the ones on the border
        level_to_sample = mi - 1
        class_to_sample = 0
        
    else:

        num_to_sample = n_class_0 - n_class_1

        # Oversample the ones on the border
        level_to_sample = mi
        class_to_sample = 1

    X_data_lvl = X_data[X_data["level"] == level_to_sample]

    # Do one extra of the closest class to slightly oversample the hard examples. 
    n = min(X_data_lvl.num_rows(), num_to_sample)
    X_data_local = X_data_local.append(X_data_lvl[:n])
    num_to_sample -= n

    if num_to_sample > 0:

        X_data_class = X_data[X_data["class"] == class_to_sample]
        
        while num_to_sample > 0:
            n = min(X_data_class.num_rows(), num_to_sample)
            X_data_local = X_data_local.append(X_data_class[:n])
            num_to_sample -= n

    # Sort the rows
    X_data_local["_random_"] = np.random.uniform(size = X_data_local.num_rows())
    X_data_local = X_data_local.sort("_random_")
    del X_data_local["_random_"]

    save_as_train_and_test(X_data_local, train_path % mi, valid_path % mi)

model_name = "full-inet-small"
which_model = 0

print "Running model %d, %s" % (which_model, model_name)

alt_path = os.path.expanduser("~/data/tmp/")
if os.path.exists(alt_path):
    gl.set_runtime_config("GRAPHLAB_CACHE_FILE_LOCATIONS", alt_path)

model_path = "nn_256x256/models/"

X_train = gl.SFrame("image-sframes/train-%d/" % which_model)
X_valid = gl.SFrame("image-sframes/validation-%d/" % which_model)
X_test = gl.SFrame("image-sframes/test/")
X_train["image"] = gl.image_analysis.resize(X_train["image"], 256, 256, 3)
X_valid["image"] = gl.image_analysis.resize(X_valid["image"], 256, 256, 3)
X_test["image"] = gl.image_analysis.resize(X_test["image"], 256, 256, 3)
network_str = '''
netconfig=start
layer[0->1] = conv
kernel_size = 8
  padding = 1
  stride = 4
  num_channels = 64
  random_type = xavier
layer[1->2] = max_pooling
  kernel_size = 3
  stride = 2
layer[2->3] = conv
  kernel_size = 3
  padding = 1
  stride = 2
  num_channels = 64
  random_type = xavier
layer[3->4] = max_pooling
  kernel_size = 3
  stride = 2
layer[4->5] = dropout
  threshold = 0.5
layer[5->6] = flatten
layer[6->7] = fullc
num_hidden_units = 128
  init_sigma = 0.01
layer[7->8] = dropout
  threshold = 0.5
layer[8->9] = sigmoid
layer[9->10] = fullc
  num_hidden_units = 128
  init_sigma = 0.01
layer[10->11] = fullc
  num_hidden_units = %d
  init_sigma = 0.01
layer[11->12] = softmax
netconfig=end

# input shape not including batch
input_shape = 3,256,256
batch_size = 100

## global parameters
init_random = gaussian

## learning parameters
learning_rate = 0.025
momentum = 0.9
l2_regularization = 0.0
divideby = 255
# end of config
''' % (5 if which_model == 0 else 2)

network = gl.deeplearning.NeuralNet(conf_str=network_str)

if os.path.exists("image-sframes/mean_image"):
    mean_image_sf = gl.SFrame("image-sframes/mean_image")
    mean_image = mean_image_sf["image"][0]
else:
    mean_image = X_train["image"].mean()
    mean_image_sf = gl.SFrame({"image" : [mean_image]})
    mean_image_sf.save("image-sframes/mean_image")

if which_model == 0:

    m = gl.classifier.neuralnet_classifier.create(
        X_train, features = ["image"], target = "level",
        network = network, mean_image = mean_image,
        device = "gpu", random_mirror=True, max_iterations = 25,
        validation_set=X_valid)

else:
    assert which_model in [1,2,3,4]

    X_train["class"] = (X_train["level"] >= which_model)
    X_valid["class"] = (X_valid["level"] >= which_model)

    # Downsample the less common class
    n_class_0 = (X_train["class"] == 0).sum()
    n_class_1 = (X_train["class"] == 1).sum()
    
    m = gl.classifier.neuralnet_classifier.create(
        X_train,
        features = ["image"], target = "class",
        network = network, mean_image = mean_image,
        device = "gpu", random_mirror=True, max_iterations = 25, validation_set=X_valid)
    
m.save(model_path + "gpu_model_%d-%s" % (which_model, model_name))

X_train["class_scores"] = \
  (m.predict_topk(X_train[["image"]], k= (5 if which_model == 0 else 2))\
   .unstack(["class", "score"], "scores").sort("row_id")["scores"])

X_test["class_scores"] = \
    (m.predict_topk(X_test[["image"]], k=(5 if which_model == 0 else 2))
     .unstack(["class", "score"], "scores").sort("row_id")["scores"])
    
X_train["features"] = m.extract_features(X_train[["image"]])
X_test["features"] = m.extract_features(X_test[["image"]])

def flatten_dict(d):
    out_d = {}
    def _add_to_dict(base, out_d, d):
        for k, v in d.iteritems():
            new_key = k if base is None else (base + '.' + str(k))
            if type(v) is dict:
                _add_to_dict(new_key, out_d, v)
            elif type(v) is array.array:
                for j, x in enumerate(v):
                    if x != 0:
                        out_d[new_key + ".%d" % j] = x
            else:
                out_d[new_key] = v
    _add_to_dict(None, out_d, d)
    return out_d

score_column = "scores_%d" % which_model
features_column = "features_%d" % which_model
    
Xt = X_train[["name", "source", "class_scores", "level", "features"]]
Xty = Xt.groupby(["name", "level"], {"cs" : agg.CONCAT("source", "class_scores")})
Xty[score_column] = Xty["cs"].apply(flatten_dict)

Xty2 = Xt.groupby("name", {"ft" : agg.CONCAT("source", "features")})
Xty2[features_column] = Xty2["ft"].apply(flatten_dict)

Xty = Xty.join(Xty2[["name", features_column]], on = "name")

Xty[["name", score_column, "level", features_column]].save(model_path + "/scores_train_%d" % which_model)

Xtst = X_test[["name", "source", "class_scores", "features"]]
Xtsty = Xtst.groupby("name", {"cs" : agg.CONCAT("source", "class_scores")})
Xtsty[score_column] = Xtsty["cs"].apply(flatten_dict)

Xtsty2 = Xtst.groupby("name", {"ft" : agg.CONCAT("source", "features")})
Xtsty2[features_column] = Xtsty2["ft"].apply(flatten_dict)

Xtsty = Xtsty.join(Xtsty2[["name", features_column]], on = "name")

Xtsty[["name", score_column, features_column]].save(model_path + "/scores_test_%d" % which_model)

base_path = os.getcwd()

model_path = base_path + "/nn_256x256/models/"

train_sf = []
test_sf = []
feature_names = []

for n in [0,1,2,3,4]:
    
    try: 
        Xf_train = gl.SFrame(model_path + "/scores_train_%d" % n)
        Xf_test = gl.SFrame(model_path + "/scores_test_%d" % n)

        train_sf.append(Xf_train)
        test_sf.append(Xf_test)
        
        feature_names += ["scores_%d" % n, "features_%d" %n]
        
    except IOError, ier:
        print "Skipping %d" % n, ": ", str(ier)

    
# Train a simple regressor to classify the different outputs 
assert train_sf

for sf in train_sf[1:]:
    train_sf[0] = train_sf[0].join(sf, on = ["name", "level"])
        
for sf in test_sf[1:]:
    test_sf[0] = test_sf[0].join(sf, on = "name")

X_train, X_valid = train_sf[0].random_split(0.95)
X_test = test_sf[0]

m = gl.regression.boosted_trees_regression.create(
    X_train, target = "level", features = feature_names,
    max_iterations=500, validation_set=X_valid,
    column_subsample=0.2, row_subsample=1, step_size=0.01)

X_test['level'] = m.predict(X_test).apply(lambda x: min(4, max(0, int(round(x)))))

X_out = X_test[['name', 'level']]

def get_number(s):
    n = float(re.match('[0-9]+', s).group(0))
    if 'right' in s:
        n += 0.5
    return n
    
X_out['number'] = X_out['name'].apply(get_number)
X_out = X_out.sort('number')
X_out.rename({"name" : "image"})

import csv

with open('submission.csv', 'wb') as outfile:

    fieldnames = ['image', 'level']
    writer = csv.DictWriter(outfile, fieldnames=fieldnames)

    writer.writeheader()
    
    for d in X_out[['image', 'level']]:
        writer.writerow(d)
    
