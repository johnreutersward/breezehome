import os
import cherrypy
import media


PATH = os.path.abspath(os.path.dirname(__file__))
class Root:
    def media_player(self, **kwargs):
            fields = kwargs
            playing = fields.get('playing', 'False')
            if(playing=='True'):
                    media.play()
                    current = media.current()
                    return '<center>' + current + '<br><a href="?playing=False">pause</a></center>'
            else:
                    media.pause()
                    return '<center>currently paused<br><a href="?playing=True">play</a></center>'
    media_player.exposed = True

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