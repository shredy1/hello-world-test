/**
 *
 * Copyright (c) 2009-2015 Freedomotic team http://freedomotic.com
 * 
* This file is part of Freedomotic
 * 
* This Program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 * 
* This Program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
* You should have received a copy of the GNU General Public License along with
 * Freedomotic; see the file COPYING. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.freedomotic.things.impl;

import com.freedomotic.events.ObjectReceiveClick;
import com.freedomotic.model.ds.Config;
import com.freedomotic.model.object.Behavior;
import com.freedomotic.model.object.RangedIntBehavior;
import com.freedomotic.things.EnvObjectLogic;
import com.freedomotic.behaviors.RangedIntBehaviorLogic;
import com.freedomotic.reactions.Command;
import com.freedomotic.reactions.Trigger;
import java.util.logging.Logger;


/**
 *
 * @author enrico
 */
public class Thermometer
        extends EnvObjectLogic {

	String temp_off = "200";
	String temp_faible = "110";
	
	
    private static final Logger LOG = Logger.getLogger(Thermometer.class.getName());
    private RangedIntBehaviorLogic temperature;
    private static final String BEHAVIOR_TEMPERATURE = "temperature";

    @Override
    public void init() {
//linking this property with the behavior defined in the XML
        temperature = new RangedIntBehaviorLogic((RangedIntBehavior) getPojo().getBehavior(BEHAVIOR_TEMPERATURE));
        temperature.addListener(new RangedIntBehaviorLogic.Listener() {
            @Override
            public void onLowerBoundValue(Config params, boolean fireCommand) {
            	if (params.getProperty("value.original").equals(params.getProperty("value"))) {
//ok here, just trying to set minimum
            		onRangeValue(temperature.getMin(), params, fireCommand);
            	} else {
//there is an hardware read error
            	}
            }

            @Override
            public void onUpperBoundValue(Config params, boolean fireCommand) {
            	if (params.getProperty("value.original").equals(params.getProperty("value"))) {
//ok here, just trying to set maximum
            		onRangeValue(temperature.getMax(), params, fireCommand);
            	} else {
//there is an hardware read error
            	}
            }

            @Override
            public void onRangeValue(int rangeValue, Config params, boolean fireCommand) {
                if (fireCommand) {
                    executeSetTemperature(rangeValue, params);
                } else {
                    setTemperature(rangeValue);
                }
            }
        });
//register this behavior to the superclass to make it visible to it
        registerBehavior(temperature);
        super.init();
    }

    public void executeSetTemperature(int rangeValue, Config params) {
        boolean executed = executeCommand("set temperature", params);
        if (executed) {
            temperature.setValue(rangeValue);
            getPojo().setCurrentRepresentation(0);
            setChanged(true);
        }
    }

    private void setTemperature(int value) {
        LOG.config("Setting behavior 'temperature' of object '" + getPojo().getName() + "' to "
                + value);
        temperature.setValue(value);
        getPojo().setCurrentRepresentation(0);
        setChanged(true);
    }

    /**
     * Creates user level commands for this class of freedomotic objects
     */
    @Override
    protected void createCommands() {
    	super.createCommands();
    
    	
    	
    
    	Command increaseThermTemp = new Command();
    	increaseThermTemp.setName("Increase " + getPojo().getName() + " conditioning temperature with O.5°C");
    	increaseThermTemp.setDescription("increases " + getPojo().getName() + " conditioning temperature of half a step");
    	increaseThermTemp.setReceiver("app.events.sensors.behavior.request.objects");
    	increaseThermTemp.setProperty("object", getPojo().getName());
    	increaseThermTemp.setProperty("behavior", BEHAVIOR_TEMPERATURE);
    	increaseThermTemp.setProperty("value", "200");
    	
   
    	Command increaseThermTemp2 = new Command();
    	increaseThermTemp2.setName("Increase " + getPojo().getName() + " conditioning temperature with 1°C");
    	increaseThermTemp2.setDescription("increases " + getPojo().getName() + " conditioning temperature of a step");
    	increaseThermTemp2.setReceiver("app.events.sensors.behavior.request.objects");
    	increaseThermTemp2.setProperty("object", getPojo().getName());
    	increaseThermTemp2.setProperty("behavior", BEHAVIOR_TEMPERATURE);
    	increaseThermTemp2.setProperty("value", "110");
    	
     
    	
    	Command decreaseThermTemp = new Command();
    	decreaseThermTemp.setName("Set temperature of  " + getPojo().getName() + " to TE");
    	decreaseThermTemp.setDescription("set temperature of  " + getPojo().getName() + " to TE");
    	decreaseThermTemp.setReceiver("app.events.sensors.behavior.request.objects");
    	decreaseThermTemp.setProperty("object", getPojo().getName());
    	decreaseThermTemp.setProperty("behavior", BEHAVIOR_TEMPERATURE);
    	decreaseThermTemp.setProperty("value", "90");

 	   
    	
    	commandRepository.create(increaseThermTemp);
    	commandRepository.create(increaseThermTemp2);
        commandRepository.create(decreaseThermTemp );
    
    }

    @Override
    protected void createTriggers() {
 
        Trigger clicked = new Trigger();
        clicked.setName("When " + this.getPojo().getName() + " is clicked");
        clicked.setChannel("app.event.sensor.object.behavior.clicked");
        clicked.getPayload().addStatement("object.name",
                this.getPojo().getName());
        clicked.getPayload().addStatement("click", ObjectReceiveClick.SINGLE_CLICK);
        clicked.setPersistence(false);
        triggerRepository.create(clicked);
        
        
        Trigger tempReach_off = new Trigger();
        tempReach_off.setName("When temp of " + this.getPojo().getName() + " reached OFF");
        tempReach_off.setChannel("app.event.sensor.object.behavior.change");
        tempReach_off.getPayload().addStatement("object.name",
                this.getPojo().getName());
        
        tempReach_off.getPayload().addStatement("AND", "Object.behavior." + BEHAVIOR_TEMPERATURE , "GREATER_EQUAL_THAN" , "200");
        
        tempReach_off.setPersistence(false);
        triggerRepository.create(tempReach_off);
        
        
        Trigger tempReach_faible = new Trigger();
        tempReach_faible.setName("When temp of " + this.getPojo().getName() + " reached FAIBLE");
        tempReach_faible.setChannel("app.event.sensor.object.behavior.change");
        tempReach_faible.getPayload().addStatement("object.name",
                this.getPojo().getName());
        
        tempReach_faible.getPayload().addStatement("AND", "Object.behavior." + BEHAVIOR_TEMPERATURE , "GREATER_EQUAL_THAN" , "110");
        tempReach_faible.getPayload().addStatement("AND", "Object.behavior." + BEHAVIOR_TEMPERATURE , "LESS_THAN" , "200");
        
        tempReach_faible.setPersistence(false);
        triggerRepository.create(tempReach_faible);
        
        
        
        Trigger tempReach_fort = new Trigger();
        tempReach_fort.setName("When temp of " + this.getPojo().getName() + " reached FORT");
        tempReach_fort.setChannel("app.event.sensor.object.behavior.change");
        tempReach_fort.getPayload().addStatement("object.name",
                this.getPojo().getName());
        
 
        tempReach_fort.getPayload().addStatement("AND", "Object.behavior." + BEHAVIOR_TEMPERATURE , "LESS_THAN" , "110");
        
        tempReach_fort.setPersistence(false);
        triggerRepository.create(tempReach_fort);
        
        
        
        
        
        
        
        
        
        
        
        
        
        
    }
}
