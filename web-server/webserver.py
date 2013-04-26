import cherrypy
import media

class webserver:
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

cherrypy.server.socket_host = '0.0.0.0'
cherrypy.server.socket_port = 80
cherrypy.quickstart(webserver())