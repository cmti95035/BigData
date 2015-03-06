# Description

This is a machine image (ami-3a5e0452) with a number of useful pieces of software enabled and 
configured, in the hopes that by making it available to others I will be able 
to decrease the total amount of pain and suffering in the world by a substantial
amount. 

Caffe is installed from the latest master branch of the github repo (current as of 
October 30, 2014), as are its dependencies, as well as NVidia's CUDA drivers.

IPython and IPython-Notebook are also installed. The Notebook part is already set up 
for remote access, albeit with a very basic configuration that you might want to
change. 

To use it:

```bash
cd ~/example

ipython notebook --profile=nbserver
```

Once the notebook is running, point your browser to `https://<INSTANCE_IP>:8888` 
and you should be good to go. The password is "nbserver". 
