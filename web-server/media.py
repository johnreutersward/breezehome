import os
import subprocess

def add(uri):
	os.system("mpc add " + uri)

def current():
        proc = subprocess.Popen(["mpc", "current"], stdout=subprocess.PIPE, shell=True)
        (out, err) = proc.communicate()
        return "currently playing " + out

def next():
	os.system("mpc next")

def play():
        os.system("mpc play")

def pause():
        os.system("mpc pause")

def stop():
        os.system("mpc stop")

def volumeUp():
	os.system("mpc volume +30")

def volumeDown():
	os.system("mpc volume -30")