import os
import cherrypy
import media


PATH = os.path.abspath(os.path.dirname(__file__))
class Root:
    def media_player(self, **kwargs):
            return open(os.path.join(PATH, u'index2.html'))
    media_player.exposed = True

    def toggle(self):
    	media.toggle()
    	return open(os.path.join(PATH, u'index2.html'))
    toggle.exposed = True

    def next(self):
    	media.next()
    	return open(os.path.join(PATH, u'index2.html'))
    next.exposed = True

    def prev(self):
    	media.prev()
    	return open(os.path.join(PATH, u'index2.html'))
    prev.exposed = True
    # def printer():
    # 	return '<html><head><title>Dark side of the pi</title></head><body><img src="darth.jpg"></body></html>'
    # printer.exposed = True
cherrypy.tree.mount(Root(), '/', config={
	'/': {
	        'tools.staticdir.on': True,
	        'tools.staticdir.dir': PATH,
	        'tools.staticdir.index': 'index.html',
	    },
	})
cherrypy.server.socket_host = '0.0.0.0'
cherrypy.server.socket_port = 80
# cherrypy.quickstart(Root())
cherrypy.engine.start()
cherrypy.engine.block()