import unittest

class Authentication(object):

	guestAuthorization = False

	def authorize(self):
		self.guestAuthorization = True

	def deny(self):
		self.guestAuthorization = False

	def isAuthorized(self):
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

	def test_isAuthurized(self):
		a = Authentication()
		a.authorize()
		self.assertEqual(a.isAuthurized(), True)
		a.deny()
		self.assertEqual(a.isAuthurized(), False)




if __name__ == '__main__':
    unittest.main()


