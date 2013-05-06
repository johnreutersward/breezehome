import cherrypy
import webbrowser
import media
import os
import json as simplejson
import sys
import jinja2
#from jinja2 import Template, Environment

TEMPLATES_DIR = os.path.join(os.path.abspath("."), u"templates")
JS_DIR = os.path.join(os.path.abspath("."), u"js")
CSS_DIR = os.path.join(os.path.abspath("."), u"css")
IMG_DIR = os.path.join(os.path.abspath("."), u"img")
FONT_DIR = os.path.join(os.path.abspath("."), u"font")

cherrypy.server.socket_host = '0.0.0.0'
cherrypy.server.socket_port = 80

template_env = jinja2.Environment(loader=jinja2.FileSystemLoader('templates'))
template = template_env.get_template('playlist.html')

class Root(object):
    @cherrypy.expose
    def index(self):
        return open(os.path.join(TEMPLATES_DIR, u'index.html'))

    @cherrypy.expose
    def play_paus(self):
        media.play()
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return simplejson.dumps(media.current())

    @cherrypy.expose
    def play(self, nr):
        media.play(nr)
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return simplejson.dumps(media.current())

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

    @cherrypy.expose
    def add(self, URI):
        media.add(URI)
        return "added " + URI

    @cherrypy.expose
    def playlist(self):
        p1 = media.playlist()
        return template.render(playlist=p1)

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

#def open_page():
#    webbrowser.open("http://192.168.1.101:80/")
#cherrypy.server.server_host = '192.168.1.101'
#cherrypy.server.socket_host = 80
#cherrypy.engine.subscribe('start', open_page)
cherrypy.quickstart(Root(), '/', config=config)
#cherrypy.engine.start()
#cherrypy.quickstart(Root())
