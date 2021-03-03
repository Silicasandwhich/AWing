#By BeeBee8 on Stackoverflow
#https://stackoverflow.com/a/47632941
#Modified by Jacob Armstrong

import sys
import argparse

import cv2
print(cv2.__version__)

def extractImages(pathIn, pathOut):
    count = 0
    vidcap = cv2.VideoCapture(pathIn)
    success,image = vidcap.read()
    while success:
        vidcap.set(cv2.CAP_PROP_POS_FRAMES,(count*1000/600))    # added this line 
        success,image = vidcap.read()
        print ("Read a new frame #{}: {}".format(count, success))
        #image = cv2.rotate(image, cv2.cv2.ROTATE_180)
        cv2.imwrite( pathOut + "\\frame%d.jpg" % count, image)     # save frame as JPEG file
        count = count + 1

if __name__=="__main__":
    a = argparse.ArgumentParser()
    a.add_argument("--pathIn", help="path to video")
    a.add_argument("--pathOut", help="path to images")
    args = a.parse_args()
    print(args)
    extractImages(args.pathIn, args.pathOut)