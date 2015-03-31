# Steps:
* Download data:
  wget -x -c --load-cookies ./cookies.txt -nH --cut-dirs=5 https://www.kaggle.com/c/diabetic-retinopathy-detection/download/train.zip.00{1..5}
* Run preprocess.sh on each image to resize all the images:
  ls train/*.jpeg test/*.jpeg | parallel ./preprocess.sh
* Use the neural net toolkits and boosted regression model in Dato's GraphLab Create package to build the classifier and submission:
  python submit.py
