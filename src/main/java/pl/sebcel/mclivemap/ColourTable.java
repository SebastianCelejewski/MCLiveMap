package pl.sebcel.mclivemap;

import java.awt.Color;
import java.util.Map;

public class ColourTable {
    
    private Map<String, Color> data;
    
    public ColourTable(Map<String, Color> data) {
        this.data = data;
    }
    
    public Color getColor(String id) {
        return data.get(id);
    }
    

}
