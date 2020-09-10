#!/usr/bin/python

from uiautomator import Device
import sys

# get the target device
d = Device(sys.argv[1])

# scroll to begin or end
# if sys.argv[2] == 'up':
# 	d(scrollable=True).scroll.toBeginning()
# elif sys.argv[2] == 'down':
# 	d(scrollable=True).scroll.toEnd()
# else:
# 	print "error direction?"

# scroll forward or backward
if sys.argv[2] == 'up':
    d(scrollable=True).scroll.vert.backward()
elif sys.argv[2] == 'down':
    d(scrollable=True).scroll.vert.forward()
else:
    print "error direction?"

