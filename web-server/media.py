import os
import subprocess

def add(uri):
	os.system("mpc add " + uri)

def current():
        proc = subprocess.Popen(["mpc current", "current"], stdout=subprocess.PIPE, shell=True)
        (out, err) = proc.communicate()
        return out

def playlist():
        proc = subprocess.Popen(["mpc playlist", "current"], stdout=subprocess.PIPE, shell=True)
        (out, err) = proc.communicate()
        result = out.split('\n')
        return result

def next():
	os.system("mpc next")

def play():
        os.system("mpc play")

def play(nr):
        os.system("mpc play " + nr)

def pause():
        os.system("mpc pause")

def stop():
        os.system("mpc stop")

def volumeUp():
	os.system("mpc volume +30")

def volumeDown():
	os.system("mpc volume -30")