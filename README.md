MyBar-web
=========

Amazing Cocktails Management System

version 1.0 :
- implemented shelf services
- implemented cocktails management services
- implemented ingredients service

version 1.1
- secured all the end-points with basic auth
- added user registration service
- added db schema for users and roles management
- implemented users management services
- implemented rate cocktails service
- switched to latest spring core and spring security 4.x versions

  todos:
  - validation of inputs like rate, passwords
  - rest tests with authentication for bar end-points from v1.0
  - obfuscate sensitive user data (username, emails)

future version 1.2
- history end-point
- investigate possibility to use messaging for rates service
