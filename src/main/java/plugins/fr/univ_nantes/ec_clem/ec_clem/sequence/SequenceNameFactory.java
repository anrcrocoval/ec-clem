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
package plugins.fr.univ_nantes.ec_clem.ec_clem.sequence;

import icy.file.FileUtil;
import icy.sequence.DimensionId;
import icy.sequence.Sequence;

import java.nio.file.Paths;

import javax.inject.Inject;

public class SequenceNameFactory {

    @Inject
    public SequenceNameFactory() {}

    public SequenceName getFrom(Sequence sequence) {
        SequenceName sequenceName = new SequenceName();
        
        if (sequence.getFilename()==null) {
        	sequenceName.setName("Unsavedondisk_"+sequence.getName());
        	sequenceName.setPath(FileUtil.getApplicationDirectory());
        }
        else 
        	{
        	sequenceName.setName(Paths.get(sequence.getFilename()).getFileName().toString());
        	sequenceName.setPath(Paths.get(sequence.getFilename()).getParent().toString());
        	
        	}

       

        return sequenceName;
    }
}
