import cherrypy
# import ../media-player/media

class webserver:
	def media_player(self, **kwargs):
		fields = kwargs
		playing = fields.get('playing', 'False')
		if(playing=='True'):
			return '<a href="?playing=False">pause</a>'
		else:
			return '<a href="?playing=True">play</a>'
	media_player.exposed = True
cherrypy.quickstart(webserver())