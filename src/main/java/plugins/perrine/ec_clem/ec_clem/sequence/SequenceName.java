/**
 * Copyright 2010-2018 Perrine Paul-Gilloteaux <Perrine.Paul-Gilloteaux@univ-nantes.fr>, CNRS.
 * Copyright 2019 Guillaume Potier <guillaume.potier@univ-nantes.fr>, INSERM.
 *
 * This file is part of EC-CLEM.
 *
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 **/
package plugins.perrine.ec_clem.ec_clem.sequence;



public class SequenceName {

    private String Path = new String();
    private String Name = new String();
    
    public void set(String pathName,String filename) {
        Path=pathName;
        Name=filename;
    }
    public void setPath(String pathName) {
        Path=pathName;
        
    }
    
    public void setName(String name) {
        Name=name;
        
    }

    public String getName() {
       return Name;
    }
    public String getPath() {
        return Path;
     }
}
