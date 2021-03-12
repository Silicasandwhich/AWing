from networktables import NetworkTables
import keyboard
import threading
import sys

import logging

logging.basicConfig(level=logging.DEBUG)

def blocking_connect_to_networktables():
    cond = threading.Condition()
    notified = [False]

    def connectionListener(connected, info):
        print(info, '; Connected=%s'%connected)
        with cond:
            notified[0] = True
            cond.notify()
    #Register to Networktables.
    NetworkTables.initialize('10.34.87.2')
    NetworkTables.addConnectionListener(connectionListener,immediateNotify=True)
    with cond:
        print("Waiting")
        if not notified[0]:
            cond.wait(1)

    print('Connected')


key_shift_status = [False, False]
safety_status = False

#register keyboard listener to shift
def left_shift_release(e):
    key_shift_status[0] = False

def left_shift_pressed(e):
    key_shift_status[0] = True

def right_shift_release(e):
    key_shift_status[1] = False

def right_shift_pressed(list):
    key_shift_status[1] = True    

def register_keys():
    keyboard.on_press_key("left shift", left_shift_pressed)
    keyboard.on_release_key("left shift", left_shift_release)

    keyboard.on_press_key("right shift", right_shift_pressed)
    keyboard.on_release_key("right shift", right_shift_release)

if __name__ == "__main__":
    #Put cool intro here
    try:
        blocking_connect_to_networktables()
        safety_table = NetworkTables.getTable("Safety")

        register_keys()

        while True:
            if key_shift_status[0] == True and key_shift_status[1] == True and safety_status == False:
                safety_status = True
                safety_table.putBoolean("deadman", True)
            elif safety_status == True and (key_shift_status[0] != True or key_shift_status[1] != True):
                safety_status = False
                safety_table.putBoolean('deadman', False)

    except KeyboardInterrupt:
        print("Ending")
        exit(0)