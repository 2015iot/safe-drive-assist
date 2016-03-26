
import cv2
import numpy as np
import serial
ser = serial.Serial(port='COM17', baudrate=9600)
ser.close()
ser.open()

def nothing(x):
    pass

cv2.namedWindow('Thresholded image')
cap = cv2.VideoCapture(1)
cnt=1
while(1):
    if cnt > 4:
        print 'Drowsy'
        ser.write('drowsy\r')
    else:
        print 'Normal'
        ser.write('normal\r')
    _,frame = cap.read()
    img = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    ret,thresh = cv2.threshold(img,100,255,cv2.THRESH_BINARY)
    #ret,thresh = cv2.threshold(img,thr,255,cv2.THRESH_BINARY_INV)
    # ret, thresh = cv2.threshold(img,0,255,cv2.THRESH_BINARY+cv2.THRESH_OTSU)
    s1 = np.sum(sum(thresh))/255
    print s1
    cv2.imshow('image',np.array(img))
    cv2.imshow('Thresholded image',np.array(thresh))
    if s1 < 100:
        cnt=cnt+1
    else:
        cnt=1
    cv2.waitKey(5)
cap.release()
