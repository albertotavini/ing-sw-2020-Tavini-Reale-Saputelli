package it.polimi.ingsw.model;

interface GenericGod {

    void godPower();
    String getGodName();
    String getEffectDescription();

}


public class God implements GenericGod {

    private final String godName;
    private String effectDescription;

    public void godPower(){}

    public String getGodName(){
        return godName;
    }

    public String getEffectDescription(){
        return effectDescription;
    }


    public God(String godName, String effectDescription){

        this.godName = godName;
        this.effectDescription = effectDescription;

    }



}
