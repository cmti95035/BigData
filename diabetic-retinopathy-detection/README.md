# Steps:
* Download data:
```bash
  wget -x -c --load-cookies ./cookies.txt -nH --cut-dirs=5 https://www.kaggle.com/c/diabetic-retinopathy-detection/download/train.zip.00{1..5}
```
* Run preprocess.sh on each image to resize all the images:
```bash
  ls train/*.jpeg test/*.jpeg | parallel ./preprocess.sh
```
* Use the neural net toolkits and boosted tree regression model in Dato's GraphLab Create package to build the classifier and submission:
```bash
  python submit.py
```