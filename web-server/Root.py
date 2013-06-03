import cherrypy
import webbrowser
import media
import os
import json as simplejson
import sys
import authentication


TEMPLATES_DIR = os.path.join(os.path.abspath("."), u"templates")
JS_DIR = os.path.join(os.path.abspath("."), u"js")
CSS_DIR = os.path.join(os.path.abspath("."), u"css")
IMG_DIR = os.path.join(os.path.abspath("."), u"img")
FONT_DIR = os.path.join(os.path.abspath("."), u"font")

cherrypy.server.socket_host = '0.0.0.0'
cherrypy.server.socket_port = 80

auth = authentication.Authentication()

class Root(object):
    @cherrypy.expose
    def get_status(self):
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return simplejson.dumps(media.get_status())

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
    def index(self, isadmin = None):
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
                return simplejson.dumps("denied")
        media.play()
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return simplejson.dumps("success")

    @cherrypy.expose
    def toggle(self, isadmin = None):
        if isadmin == None:
            if auth.authorizeMusic() != True:
                cherrypy.response.headers['Content-Type'] = 'application/json'
                return simplejson.dumps("denied")
        media.toggle()
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return simplejson.dumps("success")

    @cherrypy.expose
    def playNumber(self, nr, isadmin = None):
        if isadmin == None:
            if auth.authorizeMusic() != True:
                cherrypy.response.headers['Content-Type'] = 'application/json'
                return simplejson.dumps("denied")
        media.playNumber(nr)
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return simplejson.dumps("success")

    @cherrypy.expose
    def pause(self, isadmin = None):
        if isadmin == None:
            if auth.authorizeMusic() != True:
                cherrypy.response.headers['Content-Type'] = 'application/json'
                return simplejson.dumps("denied")
        media.pause()
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return simplejson.dumps("success")

    @cherrypy.expose
    def next(self, isadmin = None):
        if isadmin == None:
            if auth.authorizeMusic() != True:
                cherrypy.response.headers['Content-Type'] = 'application/json'
                return simplejson.dumps("denied")
        media.next()
        cherrypy.response.headers['Content-Type'] = 'application/json'
        return simplejson.dumps("success")

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
                return "denied"
        media.add(uri)
        return "success"


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
