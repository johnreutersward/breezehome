import os
import subprocess


def play():
        os.system("mpc play")

def pause():
        os.system("mpc pause")

def stop():
        os.system("mpc stop")

def current():
        proc = subprocess.Popen(["mpc", "current"], stdout=subprocess.PIPE, shell=True)
        (out, err) = proc.communicate()
        return "currently playing " + out

