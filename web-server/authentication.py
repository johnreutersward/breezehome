import unittest
import time

class Authentication(object):

	guestAuthorization = True
	playControlsTimer = time.time()

	def authorize(self):
		self.guestAuthorization = True

	def deny(self):
		self.guestAuthorization = False

	def authorizeMusic(self):
		currentTime = time.time()
		# Guests should not be able to change music
		# too often!
		if currentTime - self.playControlsTimer > 10:
			self.playControlsTimer = currentTime
			return self.guestAuthorization
		return False

	def authorizePlaylist(self):
		return self.guestAuthorization

# UNIT TESTING
class TestAuthentication(unittest.TestCase):

	def test_authorize(self):
		a = Authentication()
		a.authorize()
		self.assertEqual(a.guestAuthorization, True)

	def test_deny(self):
		a = Authentication()
		a.deny()
		self.assertEqual(a.guestAuthorization, False)

	def test_isAuthorized(self):
		a = Authentication()
		a.authorize()
		# This causes tests to run slowly but is
		# completely necessary to test this
		# functionality
		time.sleep(11)
		self.assertEqual(a.authorizeMusic(), True)
		a.deny()
		self.assertEqual(a.authorizeMusic(), False)




if __name__ == '__main__':
    unittest.main()


