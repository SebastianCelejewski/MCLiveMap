package pl.sebcel.mclivemap.domain;

import java.awt.Color;

public class Block {

    private int id;
    private String name;
    private boolean transparent;
    private Color color;

    public Block(int id, String name, boolean transparent, Color color) {
        this.id = id;
        this.name = name;
        this.transparent = transparent;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public Color getColor() {
        return color;
    }

}