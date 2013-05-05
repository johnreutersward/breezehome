import cherrypy
import webbrowser
import media
import os
import json as simplejson
import sys

TEMPLATES_DIR = os.path.join(os.path.abspath("."), u"templates")
JS_DIR = os.path.join(os.path.abspath("."), u"js")
CSS_DIR = os.path.join(os.path.abspath("."), u"css")
IMG_DIR = os.path.join(os.path.abspath("."), u"img")
FONT_DIR = os.path.join(os.path.abspath("."), u"font")

class Root(object):
    @cherrypy.expose
    def index(self):
        return open(os.path.join(TEMPLATES_DIR, u'index.html'))

    @cherrypy.expose
    def play_paus(self):
        media.play()
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return simplejson.dumps("Done")

    @cherrypy.expose
    def next(self):
        media.next()
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return simplejson.dumps("Done")

    @cherrypy.expose
    def back(self):
        media.play()
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return simplejson.dumps("Done")

config = {'/templates':
                {'tools.staticdir.on': True,
                 'tools.staticdir.dir': TEMPLATES_DIR,
                },
          '/js':
                {'tools.staticdir.on': True,
                 'tools.staticdir.dir': JS_DIR,
                },
          '/css':
                {'tools.staticdir.on': True,
                 'tools.staticdir.dir': CSS_DIR,
                },
          '/img':
                {'tools.staticdir.on': True,
                 'tools.staticdir.dir': IMG_DIR,
                },
          '/font':
                {'tools.staticdir.on': True,
                 'tools.staticdir.dir': FONT_DIR,
                }
        }

def open_page():
    webbrowser.open("http://127.0.0.1:8080/")
cherrypy.engine.subscribe('start', open_page)
cherrypy.tree.mount(Root(), '/', config=config)
cherrypy.engine.start()
