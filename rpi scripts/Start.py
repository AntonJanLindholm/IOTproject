# Imports for MQTT
import time
import datetime
import paho.mqtt.client as mqtt
import paho.mqtt.publish as publish
import os

# Using decimal to round the value for lux
from decimal import Decimal

# Imports for sensor
import board
import busio
import adafruit_vcnl4010 	# Proximity sensor
#import adafruit_tcs34725 	# RGB sensor
import adafruit_tsl2591 	# High range lux sensor

# Set MQTT broker and topic
broker = "test.mosquitto.org"	      # Broker 
pub_topic = "iotlab/masterg2/sensors" # send messages to this topic

# Initialize I2C bus and sensor.
i2c = busio.I2C(board.SCL, board.SDA)

sensor_Proximity = adafruit_vcnl4010.VCNL4010(i2c)	# Proximity
#sensor_RGB = adafruit_tcs34725.TCS34725(i2c)	    # RGB sensor
sensor_Lux = adafruit_tsl2591.TSL2591(i2c)		    # High range lux sensor

############### MQTT section ##################

lamp_topic = "iotlab/masterg2/sensors/Lamp"
lux_limit = int(-1)


# when connecting to mqtt do this;
def on_connect(client, userdata, flags, rc):
    if rc==0:
        print("Connection established. Code: "+str(rc))
        client.subscribe(lamp_topic)
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

# Actuators
def on_message(client, userdata, message):
    msg_string = str(message.payload.decode("utf-8"))
    if str(message.topic) == lamp_topic:
        global lux_limit 
        lux_limit = int(msg_string)
        print("lux limit set to " + str(lux_limit))
    # if str(message.payload.decode("utf-8")) == "On":
    #     Cmd = "tdtool --on 2"
    #     stream = os.popen(Cmd)
    # elif str(message.payload.decode("utf-8")) == "Off":
    #     Cmd = "tdtool --off 2"
    #     stream = os.popen(Cmd)
    return msg_string

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

############### Sensor section ##################	

def get_lux():
	lux = sensor_Lux.lux
	lux_value = round(Decimal(lux), 3) 	# Rounds the lux value to 3 decimals, and prints it
	print('Total light: {0} lux'.format(lux_value))
	return lux_value
	
#def get_rgb():
#	rgb_value = '{0},{1},{2}'.format(sensor_RGB.color_rgb_bytes)
#	print('Proximity: {0}'.format(rgb_value))
#	return rgb_value
	
def get_proximity():
	proximity = sensor_Proximity.proximity # The higher the value, object closer to sensor
	print('Proximity: {0}'.format(proximity))
	return proximity
    
def get_temperature():
    Cmd = "tdtool -l | tail -n 2  | head -c 52 | tail -c 4"
    stream = os.popen(Cmd)
    output = stream.read()
    temperature = output
    print('Temperature: {0} C'.format(temperature))
    return temperature
    
def get_humidity():
    Cmd = "tdtool -l | tail -n 2  | head -c 57 | tail -c 2"
    stream = os.popen(Cmd)
    output = stream.read()
    humidity = output
    print('Humidity: {0} %'.format(humidity))
    return humidity

lamp_state = int(0) #replace with get_lamp_state() like nic showed
def get_lamp_state():
    return lamp_state

# Here, call the correct function from the sensor section depending on sensor
def publishAll():
    client.publish("iotlab/masterg2/sensors/lux", str(get_lux()))
    client.publish("iotlab/masterg2/sensors/prox", str(get_proximity()))
    client.publish("iotlab/masterg2/sensors/temperature", str(get_temperature()))
    client.publish("iotlab/masterg2/sensors/humidity", str(get_humidity))

### ACTUATORS ###

def toggleLamp():
    curr_lux = get_lux()
    state = get_lamp_state()
    if state == 0 and curr_lux < lux_limit:
        Cmd = "tdtool --on 2"
        stream = os.popen(Cmd)
        print("light toggled on")
        return 1
    elif lamp_state == 1 and curr_lux > lux_limit:
        Cmd = "tdtool --off 2"
        stream = os.popen(Cmd)
        print("light toggled off")
        return 0
    return state

# Loop that publishes message
while True:
    publishAll()
    time.sleep(4.0)	# Set delay
    if(lux_limit > -1):
        lamp_state = toggleLamp() #this assignment will be removed when nics method is added

	
