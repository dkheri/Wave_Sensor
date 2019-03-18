#!/usr/bin/python3

import spidev
import time as time
import RPi.GPIO as GPIO
import atexit
import socket
from _thread import *
import threading




enabled = True
ldr_channel = 0
baseline = 0
HOST = '192.168.8.102'
PORT = 7000  
delta = 50;
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
lock = threading.Lock()

spi = spidev.SpiDev()
spi.open(0, 0)
spi.max_speed_hz = 1350000
sensitivity = 30


def exit_handler():
    print("Cleaning up")
    sock.close()
    GPIO.cleanup()

atexit.register(exit_handler)
def readadc(adcnum):
    # read SPI data from the MCP3008, 8 channels in total
    if adcnum > 7 or adcnum < 0:
        return -1
    r = spi.xfer2([1, 8 + adcnum << 4, 0])
    data = ((r[1] & 3) << 8) + r[2]
    return data


def readLdr():
    ldr_reading = readadc(0)
    return ldr_reading

def DataListener(soc):
    global delta
    global enabled
    global sensitivity
    while True:
        data = soc.recv(1024).decode("UTF-8")
        Packet = data.split()
        if Packet[0] == 'POWER':
            if str(Packet[1]) == 'ON':
                print("ON")
                enabled = True
            elif Packet[1] == 'OFF':
                print("OFf")
                print(Packet[1])
                enabled = False
        elif Packet[0] == 'SENSITIVITY':
            print("Sensitivity Changed to"+Packet[1]);
            sensitivity = eval(Packet[1])

def sendData(time_elapsed):
    sock.sendall(((str(time_elapsed) + '\n')).encode())

def wavedetected():
    global baseline
    wave = True
    time_start = time.time()
    Delta_light = readLdr() - baseline

    #while not at baseline keep recording time
    while (Delta_light > 5):
        if (time.time() - time_start) > 5:
            wave = False
            baseline = readLdr()
            break
        Delta_light = readLdr() - baseline

    #it is a wave and not an enviroment change
    if (wave):
        time_end = time.time()
        duration = time_end - time_start
        sendData(duration)
        print("Wave lasted " + str(duration))

def main():
    global baseline
    sock.connect((HOST, PORT))
    baseline = readLdr()
    start_new_thread(DataListener, (sock,))
    while True:
        if enabled:
            ldr_value = readLdr()
            if (ldr_value - baseline > sensitivity):
                print('wave detected')
                wavedetected()

if __name__ == "__main__": main()