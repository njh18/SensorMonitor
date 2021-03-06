package com.tl_ntu.sensormonitor;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.Sensor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.tl_ntu.sensormonitor.pobjects.*;

class DataManagement implements SensorListener{

    // Context from application to save file
    Context context;

    private SensorManagement sensorManagement;

    List<Integer> requiredSensors;

    DataAccess dataAccess;

    private Records records;
    private Record record;

    // Accelerometer
    private boolean accelerometerState;
    com.tl_ntu.sensormonitor.pobjects.Sensor accelerometer;
    private int accelerometerDataID;
    private List<Data> accelerometerData;

    // Gyroscope
    private boolean gyroscopeState;
    com.tl_ntu.sensormonitor.pobjects.Sensor gyroscope;
    private int gyroscopeDataID;
    private List<Data> gyroscopeData;
    
    // Proximity
    private boolean proximityState;
    com.tl_ntu.sensormonitor.pobjects.Sensor proximity;
    private int proximityDataID;
    private List<Data> proximityData;
    
    // Magnetometer
    private boolean magnetometerState;
    com.tl_ntu.sensormonitor.pobjects.Sensor magnetometer;
    private int magnetometerDataID;
    private List<Data> magnetometerData;
    
    // Barometer
    private boolean barometerState;
    com.tl_ntu.sensormonitor.pobjects.Sensor barometer;
    private int barometerDataID;
    private List<Data> barometerData;
    
    // Ambient Light
    private boolean ambientLightState;
    com.tl_ntu.sensormonitor.pobjects.Sensor ambientLight;
    private int ambientLightDataID;
    private List<Data> ambientLightData;

    public DataManagement(Context context){
        this.context = context;
        sensorManagement = new SensorManagement(context, this);

        requiredSensors = new ArrayList<Integer>();

        accelerometerData = new ArrayList<Data>();
        gyroscopeData = new ArrayList<Data>();
        proximityData = new ArrayList<Data>();
        magnetometerData = new ArrayList<Data>();
        barometerData = new ArrayList<Data>();
        ambientLightData = new ArrayList<Data>();

        records = new Records();

        dataAccess = new DataAccess(context);
    }


    public void read(ArrayList<Integer> requiredSensors, String fileName){
        
        // Get relevant sensors
        this.requiredSensors = requiredSensors;
        
        // Prepare 
        enableSensorMeasurements();
        records = dataAccess.loadFileFromStorage(fileName);
        record = new Record();
        records.getRecords().add(record);

        // Add required sensors to record
        if(accelerometerState){
            accelerometer = new com.tl_ntu.sensormonitor.pobjects.Sensor();
            accelerometer.setName("accelerometer");
            accelerometer.setDataentries(accelerometerData);
            record.getSensors().add(accelerometer);
        }
        
        if(gyroscopeState) {
            gyroscope = new com.tl_ntu.sensormonitor.pobjects.Sensor();
            gyroscope.setName("gyroscope");
            gyroscope.setDataentries(gyroscopeData);
            record.getSensors().add(gyroscope);
        }

        if(proximityState) {
            proximity = new com.tl_ntu.sensormonitor.pobjects.Sensor();
            proximity.setName("proximity");
            proximity.setDataentries(proximityData);
            record.getSensors().add(proximity);
        }

        if(magnetometerState) {
            magnetometer = new com.tl_ntu.sensormonitor.pobjects.Sensor();
            magnetometer.setName("magnetometer");
            magnetometer.setDataentries(magnetometerData);
            record.getSensors().add(magnetometer);
        }

        if(barometerState) {
            barometer = new com.tl_ntu.sensormonitor.pobjects.Sensor();
            barometer.setName("barometer");
            barometer.setDataentries(barometerData);
            record.getSensors().add(barometer);
        }

        if(ambientLightState) {
            ambientLight = new com.tl_ntu.sensormonitor.pobjects.Sensor();
            ambientLight.setName("ambientLight");
            ambientLight.setDataentries(ambientLightData);
            record.getSensors().add(ambientLight);
        }

        // Start receiving Data
        record.setStart(Long.toString(System.currentTimeMillis()));
        sensorManagement.registerSensors(requiredSensors);
    }
    
    public void save(String fileName){
        disableSensorMeasurements();
        sensorManagement.unregisterSensors();
        record.setStop(Long.toString(System.currentTimeMillis()));

        dataAccess.saveFileToStorage(fileName, records);

        dropMeasurements();
    }

    @Override
    public void onValueChange(SensorEvent event, int sensor) {
        if(sensor == Sensor.TYPE_ACCELEROMETER){
            accelerometerDataID += 1;

            Data data = createData(accelerometerDataID);

            Value x = createValue("x", sensorManagement.getAccelerometerX());
            Value y = createValue("y", sensorManagement.getAccelerometerY());
            Value z = createValue("z", sensorManagement.getAccelerometerZ());

            data.getValues().add(x);
            data.getValues().add(y);
            data.getValues().add(z);

            accelerometerData.add(data);
        }

        if(sensor == Sensor.TYPE_GYROSCOPE){
            gyroscopeDataID += 1;

            Data data = createData(gyroscopeDataID);

            Value x = createValue("x", sensorManagement.getGyroscopeX());
            Value y = createValue("y", sensorManagement.getGyroscopeY());
            Value z = createValue("z", sensorManagement.getGyroscopeZ());

            data.getValues().add(x);
            data.getValues().add(y);
            data.getValues().add(z);

            gyroscopeData.add(data);
        }

        if(sensor == Sensor.TYPE_PROXIMITY){
            proximityDataID += 1;

            Data data = createData(proximityDataID);

            Value x = createValue("x", sensorManagement.getProximityX());

            data.getValues().add(x);

            proximityData.add(data);
        }

        if(sensor == Sensor.TYPE_MAGNETIC_FIELD){
            magnetometerDataID += 1;

            Data data = createData(magnetometerDataID);

            Value x = createValue("x", sensorManagement.getMagnetometerX());
            Value y = createValue("y", sensorManagement.getMagnetometerY());
            Value z = createValue("z", sensorManagement.getMagnetometerZ());

            data.getValues().add(x);
            data.getValues().add(y);
            data.getValues().add(z);

            magnetometerData.add(data);
        }

        if(sensor == Sensor.TYPE_PRESSURE){
            barometerDataID += 1;

            Data data = createData(barometerDataID);

            Value x = createValue("x", sensorManagement.getBarometerX());

            data.getValues().add(x);

            barometerData.add(data);
        }

        if(sensor == Sensor.TYPE_LIGHT){
            ambientLightDataID += 1;

            Data data = createData(ambientLightDataID);

            Value x = createValue("x", sensorManagement.getAmbientLightX());

            data.getValues().add(x);

            ambientLightData.add(data);
        }
    }

    private Data createData(int dataID){
        Data data = new Data();
        data.setId(Integer.toString(dataID));
        data.setTime(Long.toString(System.currentTimeMillis()));

        return data;
    }

    private Value createValue(String name, Float sensorData){
        Value value = new Value();
        value.setName(name);
        value.setValue(Float.toString(sensorData));

        return value;
    }

    //=========================================
    // Measure utils
    //=========================================
    private void enableSensorMeasurements(){

        // Reset Data ID
        accelerometerDataID = 0;
        gyroscopeDataID = 0;
        proximityDataID = 0;
        magnetometerDataID = 0;
        barometerDataID = 0;
        ambientLightDataID = 0;


        if(requiredSensors.contains(Sensor.TYPE_ACCELEROMETER))
            accelerometerState = true;

        if(requiredSensors.contains(Sensor.TYPE_GYROSCOPE))
            gyroscopeState = true;

        if(requiredSensors.contains(Sensor.TYPE_PROXIMITY))
            proximityState= true;

        if(requiredSensors.contains(Sensor.TYPE_MAGNETIC_FIELD))
            magnetometerState = true;

        if(requiredSensors.contains(Sensor.TYPE_PRESSURE))
            barometerState = true;

        if(requiredSensors.contains(Sensor.TYPE_LIGHT))
            ambientLightState = true;
    }

    private void disableSensorMeasurements(){
        accelerometerState = false;
        gyroscopeState = false;
        proximityState = false;
        magnetometerState = false;
        barometerState = false;
        ambientLightState = false;
    }

    private void dropMeasurements(){
        accelerometerData.clear();
        gyroscopeData.clear();
        proximityData.clear();
        magnetometerData.clear();
        barometerData.clear();
        ambientLightData.clear();
    }
}
