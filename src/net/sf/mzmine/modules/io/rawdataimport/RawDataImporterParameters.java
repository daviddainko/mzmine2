/*
 * Copyright 2006-2008 The MZmine Development Team
 * 
 * This file is part of MZmine.
 * 
 * MZmine is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.modules.io.rawdataimport;

import net.sf.mzmine.data.Parameter;
import net.sf.mzmine.data.ParameterType;
import net.sf.mzmine.data.PreloadLevel;
import net.sf.mzmine.data.impl.SimpleParameter;
import net.sf.mzmine.data.impl.SimpleParameterSet;

public class RawDataImporterParameters extends SimpleParameterSet {

    public static final Parameter importDirectory = new SimpleParameter(
            ParameterType.STRING, "Last open directory",
            "Last directory used to import files");

    public static final Parameter preloadLevel = new SimpleParameter(
            ParameterType.STRING, "Preload level",
            "Defines which scans will be pre-loaded to memory, if any",
            PreloadLevel.NO_PRELOAD, PreloadLevel.values());

    public RawDataImporterParameters() {
        super(new Parameter[] { importDirectory, preloadLevel });
    }

}
