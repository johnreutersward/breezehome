import os
import subprocess

def add(uri):
        os.system("mpc add " + uri)

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
        proc = subprocess.Popen(["mpc search Artist " + str, "current"], stdout=subprocess.PIPE, shell=True)
        (out, err) = proc.communicate()
        result = out.split('\n')
        return result

def changetrack(nr):
        for i in range(1, nr):
                os.system("mpc del 1")
        os.system("mpc del 0")
        os.system("mpc play 1")

def next():
        os.system("mpc next")

def toggle():
        os.system("mpc toggle")

def play():
        os.system("mpc play")

def playNumber(nr):
         changetrack(int(nr))

def pause():
        os.system("mpc pause")

def stop():
        os.system("mpc stop")

def volumeUp():
        os.system("mpc volume +30")

def volumeDown():
        os.system("mpc volume -30")
