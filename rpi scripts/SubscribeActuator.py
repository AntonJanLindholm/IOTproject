# Imports for MQTT
import time
import datetime
import paho.mqtt.client as mqtt
import paho.mqtt.publish as publish
import os

# Set MQTT broker and topic
broker = "test.mosquitto.org"	# Broker 

pub_topic = "iotlab/masterg2/sensors"       # send messages to this topic


############### MQTT section ##################

# when connecting to mqtt do this;
def on_connect(client, userdata, flags, rc):
	if rc==0:
		print("Connection established. Code: "+str(rc))
	else:
		print("Connection failed. Code: " + str(rc))
		
def on_publish(client, userdata, mid):
    print("Published: " + str(mid))
	
def on_disconnect(client, userdata, rc):
	if rc != 0:
		print ("Unexpected disonnection. Code: ", str(rc))
	else:
		print("Disconnected. Code: " + str(rc))
	
def on_log(client, userdata, level, buf):		# Message is in buf
    print("MQTT Log: " + str(buf))
    
def on_message(client, userdata, message):
    print("received message =",str(message.payload.decode("utf-8")))
    if str(message.payload.decode("utf-8")) == "On":
        Cmd = "tdtool --on 2"
        stream = os.popen(Cmd)
    elif str(message.payload.decode("utf-8")) == "Off":
        Cmd = "tdtool --off 2"
        stream = os.popen(Cmd)
    return str(message.payload.decode("utf-8"))

# Connect functions for MQTT
client = mqtt.Client()
client.on_connect = on_connect
client.on_disconnect = on_disconnect
client.on_publish = on_publish
client.on_log = on_log
client.on_message = on_message

# Connect to MQTT 
print("Attempting to connect to broker " + broker)
client.connect(broker)	# Broker address, port and keepalive (maximum period in seconds allowed between communications with the broker)
client.loop_start()


# Loop that publishes message
while True:  
	client.subscribe("iotlab/masterg2/sensors/Lamp")
	time.sleep(1.0)