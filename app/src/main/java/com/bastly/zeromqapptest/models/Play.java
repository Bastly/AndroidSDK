package com.bastly.zeromqapptest.models;

import java.io.Serializable;

/**
 * Created by goofyahead on 20/04/15.
 */
public class Play implements Serializable{
    private String spell;
    private String material;
    private int strength;

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
}
