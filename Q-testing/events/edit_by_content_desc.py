#!/usr/bin/python

from uiautomator import Device
import sys
import random

# get the target device
d = Device(sys.argv[1])

# inputs = ["test", "12"]
# inputs = ["test", "12", "liwenamo1@163.com", "ha147258963", "www.baidu.com", "www.163.com"]
inputs = ["test", "12", "0.8", "1.2", "1.0", "9.8"]

# edit text
if d(description=sys.argv[2]).exists:
    d(description=sys.argv[2]).set_text(inputs[random.randint(0, 5)])









