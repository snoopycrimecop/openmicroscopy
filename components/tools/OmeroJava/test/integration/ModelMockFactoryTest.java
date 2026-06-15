/*
 * Copyright 2006-2017 University of Dundee. All rights reserved.
 * Use is subject to license terms supplied in LICENSE.txt
 */
package integration;

import java.io.File;
import java.util.List;

import omero.model.Pixels;

import org.testng.annotations.Test;

import ome.xml.model.OME;
import ome.specification.XMLMockObjects;

/**
 * Tests for ModelMockFactory
 */
public class ModelMockFactoryTest extends AbstractServerImportTest {

    /**
     * Test creating and importing an OME-TIFF file
     */
    @Test
    public void testCreateOMETiffFile() throws Throwable {
        // Create mock OME metadata
        XMLMockObjects xml = new XMLMockObjects();
        OME ome = xml.createImageWithAcquisitionData();

        // Create OME-TIFF file
        File tiffFile = null;
        try {
            tiffFile = mmFactory.createOMETiffFile(ome);
            System.out.println("Created OME-TIFF: " + tiffFile.getAbsolutePath());
            System.out.println("File size: " + tiffFile.length() + " bytes");

            // Import the file
            List<Pixels> pixels = importFile(tiffFile, "ome.tiff");

            // Verify import succeeded
            assert pixels != null : "importFile returned null";
            assert !pixels.isEmpty() : "importFile returned empty list";

            Pixels pix = pixels.get(0);
            assert pix != null : "First pixel is null";
            assert pix.getImage() != null : "Image is null";

            System.out.println("Import successful! Image ID: " + pix.getImage().getId().getValue());
        } finally {
            if (tiffFile != null && tiffFile.exists()) {
                tiffFile.delete();
            }
        }
    }
}
