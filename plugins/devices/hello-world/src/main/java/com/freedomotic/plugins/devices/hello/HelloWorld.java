package com.freedomotic.plugins.devices.hello;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.validation.annotation.Validated;

import com.freedomotic.api.EventTemplate;
import com.freedomotic.api.Protocol;
import com.freedomotic.behaviors.BehaviorLogic;
import com.freedomotic.exceptions.UnableToExecuteException;
import com.freedomotic.reactions.Command;
import com.freedomotic.things.EnvObjectLogic;
import com.freedomotic.things.ThingRepository;
import com.google.inject.Inject;
import com.variable.TemperatureObs;

public class HelloWorld
        extends Protocol {

	
    private static final Logger LOG = Logger.getLogger(HelloWorld.class.getName());
    final int POLLING_WAIT;
    public Etat etat;
    
    private static double TC = 18;
	private static String Mode="economique";
	private static double coef = 1/3;
	private static double delta = 1;
	
    final static String THERMO_EXT="Thermo_Ext";
    final static String THERMO_INT="Thermo_Int";
    final static String RADIATEUR="Radiateur";
    final static String PROTE="porte";
    final static String MODE_CONFORT="Confort";
    final static String MODE_ECO="Eco";
    
    final static String mode=MODE_ECO;
    
    
    @Inject
    private ThingRepository thingsRepository;

    public HelloWorld() {
        //every plugin needs a name and a manifest XML file
        super("HelloWorld", "/hello-world/hello-world-manifest.xml");
        //read a property from the manifest file below which is in
        //FREEDOMOTIC_FOLDER/plugins/devices/com.freedomotic.hello/hello-world.xml
        POLLING_WAIT = configuration.getIntProperty("time-between-reads", 10000);
        //POLLING_WAIT is the value of the property "time-between-reads" or 2000 millisecs,
        //default value if the property does not exist in the manifest
        setPollingWait(POLLING_WAIT); //millisecs interval between hardware device status reads
    }

    @Override
    protected void onShowGui() {
        /**
         * uncomment the line below to add a GUI to this plugin the GUI can be
         * started with a right-click on plugin list on the desktop frontend
         * (com.freedomotic.jfrontend plugin)
         */
        //bindGuiToPlugin(new HelloWorldGui(this));
    }

    @Override
    protected void onHideGui() {
        //implement here what to do when the this plugin GUI is closed
        //for example you can change the plugin description
        setDescription("My GUI is now hidden");
    }

    @Override
    protected void onRun() {
    	File fichier =  new File("/Users/mac/Desktop/doc/commande.ser") ;
    	try{
    		ObjectInputStream ois =  new ObjectInputStream(new FileInputStream(fichier)) ;
    		CommandeObj objC = (CommandeObj)ois.readObject() ;
    		
	    	if(fichier.length()>0 && objC!=null && objC.getTc()!=null && objC.getMode()!=null){
	    		System.out.println("Changement Mode");
	    		System.out.println(objC) ;
	    		setTC(Double.parseDouble(objC.getTc()));
	    		setMode(objC.getMode());
	    		if(mode.equals(MODE_ECO))
	    		{
	    			setCoef(1/3);
	    			setDelta(1);
	    			
	    		}else{
	    			setCoef(2/3);
	    			setDelta(0.5);
	    		}
	    		System.out.println(getTC()) ;
	    		System.out.println(getMode()) ;
    	    }
	    	
	    	
	    	/*Serialisation nouvelle valeur de TC*/
    		ObjectOutputStream oos = null;
    		TemperatureObs tmpObs=new TemperatureObs();
    		
			File tcValObj =  new File("/Users/mac/Desktop/doc/tc.ser") ;
			if(fichier.exists())
			        fichier.delete();
			oos = new ObjectOutputStream(new FileOutputStream(tcValObj));
		    tmpObs.setTC(TC);
		    tmpObs.setTCmDetlatProfil(TC-delta);
		    /*traitement objet*/
		    List<EnvObjectLogic> listObj = thingsRepository.findAll();
			synchronized(listObj){
				for (EnvObjectLogic object : listObj) {
					if(object.getPojo().getName().equals(THERMO_EXT)){
						for (BehaviorLogic behavior : object.getBehaviors()) {
							tmpObs.setTE(Double.parseDouble(behavior.getValueAsString()));
		                }
					}else if(object.getPojo().getName().equals(THERMO_INT)){
						for (BehaviorLogic behavior : object.getBehaviors()) {
							tmpObs.setTI(Double.parseDouble(behavior.getValueAsString()));
		                }
					}
				}
			}
			oos.writeObject(tmpObs) ;
			oos.close();
    	}catch(Exception e){
    		e.getStackTrace();
    	}finally{
    		try{
	    		ObjectOutputStream oos = null;
	    		oos = new ObjectOutputStream(new FileOutputStream(fichier));
				oos.close();
    		}catch(Exception d){
    			
    		}
    		fichier.delete();
    	}
		
    }

    @Override
    protected void onStart() {
        LOG.info("HelloWorld plugin is started");
        etat = new Etat();
    }

    @Override
    protected void onStop() {
        LOG.info("HelloWorld plugin is stopped ");
    }

    @Override
    protected void onCommand(Command c)
            throws IOException, UnableToExecuteException {
        LOG.info("HelloWorld plugin receives a command called " + c.getName() + " with parameters "
                + c.getProperties().toString());
    }

    @Override
    protected boolean canExecute(Command c) {
        //don't mind this method for now
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void onEvent(EventTemplate event) {
        //don't mind this method for now
        throw new UnsupportedOperationException("Not supported yet.");
    }
    

	//MÃ©thode qui s'exÃ©cute chaque seconde
	public void Regulator()
	{
		String etat_fenetre = "" ; // implementer une mÃ©thode qui Ã©coute la fenetre communicante en push;
		
		if(etat_fenetre.equals("ouverte"))
		{
			//envoyer requete au radiateur pour le mettre en mode eteint 
		}
		
		else
		{
			double TE = 1 ; // Lire sur  Thermostat externe en push 
			double TI = 4; // Requete au Thermostat interne en pull 
			
			regler(TE,TI);
		}
				
	}
	
	
	public void regler(double TE,double TI)
	{
		if(TI >= TC-delta)
		{
			//envoyer requete au radiateur pour le mettre en mode eteint 
		}
		else if(TE + coef*(TC-TE) <= TI & TI < TC-delta)
		{
			//envoyer requete au radiateur pour le mettre en mode Faible
		}
		else if(TI < TE + coef*(TC-TE))
		{
			//envoyer requete au radiateur pour le mettre en mode Fort
		}
	}
	
		
	public static double getTC() {
		return TC;
	}

	public static void setTC(double tC) {
		TC = tC;
	}

	public static String getMode() {
		return Mode;
	}

	public static void setMode(String mode) {
		Mode = mode;
	}

	public static double getCoef() {
		return coef;
	}

	public static void setCoef(double coef) {
		HelloWorld.coef = coef;
	}

	public static double getDelta() {
		return delta;
	}

	public static void setDelta(double delta) {
		HelloWorld.delta = delta;
	}

	
}
