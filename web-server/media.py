import os
import subprocess

def add(uri):
        os.system("mpc add " + uri)

def get_status():
        proc = subprocess.Popen(["mpc status"], stdout=subprocess.PIPE, shell=True)
        (out, err) = proc.communicate()
        tmp = out.split('\n')
        tmp2 = tmp[1]
        result = tmp2.split(' ')
        if result[0] == "[playing]" or result[0] == "[paused]":
                return result[0]
        else:
                return "[stopped]"

def current():
        proc = subprocess.Popen(["mpc current", "current"], stdout=subprocess.PIPE, shell=True)
        (out, err) = proc.communicate()
        return out

def queue():
        proc = subprocess.Popen(["mpc playlist", "current"], stdout=subprocess.PIPE, shell=True)
        (out, err) = proc.communicate()
        result = out.split('\n')
        return result

def search(str):
        proc = subprocess.Popen(["mpc search any '" + str +"'", "current"], stdout=subprocess.PIPE, shell=True)
        (out, err) = proc.communicate()
        result = out.split('\n')
        return result

def next():
        playNumber("1")

def toggle():
        os.system("mpc toggle")

def play():
        os.system("mpc play")

def playNumber(nr):
        for i in range(1, int(nr)+1):
                os.system("mpc del 1")
        #os.system("mpc del 0")
        os.system("mpc play 1")

def pause():
        os.system("mpc pause")

def stop():
        os.system("mpc stop")

def volumeUp():
        os.system("mpc volume +30")

def volumeDown():
        os.system("mpc volume -30")
