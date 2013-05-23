import cherrypy
import webbrowser
import media
import os
import json as simplejson
import sys
import jinja2
import authentication
from jinja2 import Template, Environment

TEMPLATES_DIR = os.path.join(os.path.abspath("."), u"templates")
JS_DIR = os.path.join(os.path.abspath("."), u"js")
CSS_DIR = os.path.join(os.path.abspath("."), u"css")
IMG_DIR = os.path.join(os.path.abspath("."), u"img")
FONT_DIR = os.path.join(os.path.abspath("."), u"font")

cherrypy.server.socket_host = '0.0.0.0'
cherrypy.server.socket_port = 80

template_env = jinja2.Environment(loader=jinja2.FileSystemLoader('templates'))
template = template_env.get_template('playlist.html')

auth = authentication.Authentication()

class Root(object):
    @cherrypy.expose
    def authorize(self, isadmin = None):
        if isadmin == None:
            return "Admin only"
        auth.authorize()
        return "Guest access granted!"

    @cherrypy.expose
    def deny(self, isadmin = None):
        if isadmin == None:
            return "Admin only!"
        auth.deny()
        return "Guest access revoked!"

    @cherrypy.expose
    def index(self):
        return open(os.path.join(TEMPLATES_DIR, u'index.html'))

    @cherrypy.expose
    def media(self, isadmin = None):
        return open(os.path.join(TEMPLATES_DIR, u'media_player.html'))

    @cherrypy.expose
    def light(self, isadmin = None):
        return open(os.path.join(TEMPLATES_DIR, u'light_switch.html'))

    @cherrypy.expose
    def play(self, isadmin = None):
        if isadmin == None:
            if auth.authorizeMusic() != True:
                cherrypy.response.headers['Content-Type'] = 'application/json'
                return simplejson.dumps(media.current())
        media.play()
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return simplejson.dumps(media.current())

    @cherrypy.expose
    def playNumber(self, nr, isadmin = None):
        if isadmin == None:
            if auth.authorizeMusic() != True:
                cherrypy.response.headers['Content-Type'] = 'application/json'
                return simplejson.dumps(media.current())
        media.playNumber(nr)
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return simplejson.dumps(media.current())

    @cherrypy.expose
    def pause(self, isadmin = None):
        if isadmin == None:
            if auth.authorizeMusic() != True:
                cherrypy.response.headers['Content-Type'] = 'application/json'
                return simplejson.dumps(media.current())
        media.pause()
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return simplejson.dumps(media.current())

    @cherrypy.expose
    def next(self, isadmin = None):
        if isadmin == None:
            if auth.authorizeMusic() != True:
                cherrypy.response.headers['Content-Type'] = 'application/json'
                return simplejson.dumps("Done")
        media.next()
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return simplejson.dumps("Done")

    @cherrypy.expose
    def getCurrentSong(self, isdmin = None):
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return simplejson.dumps(media.current());

    @cherrypy.expose
    def back(self, isadmin = None):
        if isadmin == None:
            if auth.authorizeMusic() != True:
                cherrypy.response.headers['Content-Type'] = 'application/json'
                return simplejson.dumps("Done")
        media.play()
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return simplejson.dumps("Done")

    @cherrypy.expose
    def queue(self, isadmin = None):
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return simplejson.dumps(media.queue())

    @cherrypy.expose
    def search(self, song):
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return simplejson.dumps(media.search(song))

    @cherrypy.expose
    def add(self, uri, isadmin = None):
        if isadmin == None:
            if auth.authorizePlaylist() != True:
                return "not Authorized!"
        media.add(uri)
        return "added " + uri

    @cherrypy.expose
    def playlist(self):
        p1 = media.queue()
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

cherrypy.quickstart(Root(), '/', config=config)
