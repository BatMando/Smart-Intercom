
import io
import picamera
import logging
import socketserver
from threading import Thread
from threading import Condition
from http import server
import firebase_admin
from firebase_admin import credentials 
from firebase_admin import db
import sys
from time import sleep
import signal
import time 
import RPi.GPIO as GPIO
import subprocess
PAHT_CRED = '/home/pi/Desktop/proj/key.json'
URL_DB = 'https://smartintercom-6cdc3.firebaseio.com/'
REF_HOME = 'home'
REF_DOOR = 'doorstatus'
#GPIO.setmode(GPIO.BOARD)
#GPIO.setup(7,GPIO.OUT)


PAGE="""\
<html>
<head>
<title>Raspberry Pi - Surveillance Camera</title>
</head>
<body>
<center><h1>Raspberry Pi - Surveillance Camera</h1></center>
<center><img src="stream.mjpg" width="640" height="535"></center>
</body>
</html>
"""
class IOT():
    
    def __init__(self):
        cred = credentials.Certificate(PAHT_CRED)
        firebase_admin.initialize_app(cred, {
            'databaseURL': URL_DB
        })

        self.refHome = db.reference(REF_HOME)
        self.refDoor = self.refHome.child(REF_DOOR)
    
    def OpenDoor(self, status):
        #print (status)
        if (status == 'True'):
            print('door opened')
           # GPIO.output(7,True)
           #time.sleep(5)
           # GPIO.output(7,False)
           # GPIO.setwarnings(False)
           # GPIO.cleanup()
            
        #else:
         #   print('door closed')
             

    def DoorStart(self):
    
        E, i = [], 0

        status_before = self.refDoor.get()
        self.OpenDoor(status_before)

        E.append(status_before)

        while True:
          status_now = self.refDoor.get()
          E.append(status_now)

          if E[i] != E[-1]:
              self.OpenDoor(status_now)

          del E[0]
          i = i + i
          sleep(0.4)  
        
class StreamingOutput(object):
    def __init__(self):
        self.frame = None
        self.buffer = io.BytesIO()
        self.condition = Condition()

    def write(self, buf):
        if buf.startswith(b'\xff\xd8'):
            self.buffer.truncate()
            with self.condition:
                self.frame = self.buffer.getvalue()
                self.condition.notify_all()
            self.buffer.seek(0)
        return self.buffer.write(buf)

class StreamingHandler(server.BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == '/':
            self.send_response(301)
            self.send_header('Location', '/index.html')
            self.end_headers()
        elif self.path == '/index.html':
            content = PAGE.encode('utf-8')
            self.send_response(200)
            self.send_header('Content-Type', 'text/html')
            self.send_header('Content-Length', len(content))
            self.end_headers()
            self.wfile.write(content)
        elif self.path == '/stream.mjpg':
            self.send_response(200)
            self.send_header('Age', 0)
            self.send_header('Cache-Control', 'no-cache, private')
            self.send_header('Pragma', 'no-cache')
            self.send_header('Content-Type', 'multipart/x-mixed-replace; boundary=FRAME')
            self.end_headers()
            try:
                while True:
                    with output.condition:
                        output.condition.wait()
                        frame = output.frame
                    self.wfile.write(b'--FRAME\r\n')
                    self.send_header('Content-Type', 'image/jpeg')
                    self.send_header('Content-Length', len(frame))
                    self.end_headers()
                    self.wfile.write(frame)
                    self.wfile.write(b'\r\n')
            except Exception as e:
                logging.warning(
                    'Removed streaming client %s: %s',
                    self.client_address, str(e))
        else:
            self.send_error(404)
            self.end_headers()

class StreamingServer(socketserver.ThreadingMixIn, server.HTTPServer):
    allow_reuse_address = True
    daemon_threads = True

with picamera.PiCamera(resolution='640x535', framerate=30) as camera:
    output = StreamingOutput()
    camera.rotation = 180
    camera.start_recording(output, format='mjpeg')
    try:
        print ('START !')
        iot = IOT()

        thread_door = Thread(target=iot.DoorStart)
        thread_door.daemon = True
        thread_door.start()
       
        address = ('', 8000)
        server = StreamingServer(address, StreamingHandler)
        server.serve_forever()
        signal.pause()
      
        
    finally:
        camera.stop_recording()


 


