/*
 *  Copyright (C) 2020 GReD
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.

 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package fr.igred.omero;


import fr.igred.omero.metadata.annotation.MapAnnotationContainer;
import fr.igred.omero.metadata.annotation.TagAnnotationContainer;
import fr.igred.omero.repository.DatasetContainer;
import ij.ImagePlus;
import ij.plugin.Duplicator;
import ij.plugin.ImageCalculator;
import ij.process.ImageStatistics;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import loci.common.DebugTools;
import loci.plugins.BF;
import omero.gateway.model.MapAnnotationData;
import omero.model.NamedValue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertNotEquals;


public class ImageTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName Name of the test case.
     */
    public ImageTest(String testName) {
        super(testName);
    }


    /**
     * @return the suite of tests being tested.
     */
    public static Test suite() {
        return new TestSuite(ImageTest.class);
    }


    public void testImportImage() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        File f = new File("./8bit-unsigned&pixelType=uint8&sizeZ=5&sizeC=5&sizeT=7&sizeX=512&sizeY=512.fake");
        if(!f.createNewFile())
            System.err.println("\"" + f.getCanonicalPath() + "\" could not be created.");

        File f2 = new File("./8bit-unsigned&pixelType=uint8&sizeZ=4&sizeC=5&sizeT=6&sizeX=512&sizeY=512.fake");
        if(!f2.createNewFile())
            System.err.println("\"" + f2.getCanonicalPath() + "\" could not be created.");

        DatasetContainer dataset = root.getDataset(2L);

        dataset.importImages(root,
                             "./8bit-unsigned&pixelType=uint8&sizeZ=5&sizeC=5&sizeT=7&sizeX=512&sizeY=512.fake",
                             "./8bit-unsigned&pixelType=uint8&sizeZ=4&sizeC=5&sizeT=6&sizeX=512&sizeY=512.fake");

        List<ImageContainer> images = dataset.getImages(root);

        assertEquals(2, images.size());

        for (ImageContainer image : images) {
            root.deleteImage(image);
        }

        images = dataset.getImages(root);

        assert (images.isEmpty());
    }


    public void testPairKeyValue() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        File f = new File("./8bit-unsigned&pixelType=uint8&sizeZ=3&sizeC=5&sizeT=7&sizeX=512&sizeY=512.fake");
        if(!f.createNewFile())
            System.err.println("\"" + f.getCanonicalPath() + "\" could not be created.");

        DatasetContainer dataset = root.getDataset(2L);

        dataset.importImages(root, "./8bit-unsigned&pixelType=uint8&sizeZ=3&sizeC=5&sizeT=7&sizeX=512&sizeY=512.fake");

        List<ImageContainer> images = dataset.getImages(root);

        ImageContainer image = images.get(0);

        List<NamedValue> result1 = new ArrayList<>();
        result1.add(new NamedValue("Test result1", "Value Test"));
        result1.add(new NamedValue("Test2 result1", "Value Test2"));

        List<NamedValue> result2 = new ArrayList<>();
        result2.add(new NamedValue("Test result2", "Value Test"));
        result2.add(new NamedValue("Test2 result2", "Value Test2"));

        MapAnnotationContainer mapAnnotation1 = new MapAnnotationContainer(result1);

        MapAnnotationData mapData2 = new MapAnnotationData();
        mapData2.setContent(result2);
        MapAnnotationContainer mapAnnotation2 = new MapAnnotationContainer(mapData2);

        assertEquals(result1, mapAnnotation1.getContent());

        image.addMapAnnotation(root, mapAnnotation1);
        image.addMapAnnotation(root, mapAnnotation2);

        List<NamedValue> result = image.getKeyValuePairs(root);

        assert (result.size() == 4);
        assertEquals(image.getValue(root, "Test result1"), "Value Test");

        root.deleteImage(image);
    }


    public void testPairKeyValue2() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        File f = new File("./8bit-unsigned&pixelType=uint8&sizeZ=3&sizeC=5&sizeT=7&sizeX=512&sizeY=512.fake");
        if(!f.createNewFile())
            System.err.println("\"" + f.getCanonicalPath() + "\" could not be created.");

        DatasetContainer dataset = root.getDataset(2L);

        dataset.importImages(root, "./8bit-unsigned&pixelType=uint8&sizeZ=3&sizeC=5&sizeT=7&sizeX=512&sizeY=512.fake");

        List<ImageContainer> images = dataset.getImages(root);

        ImageContainer image = images.get(0);

        List<NamedValue> result = new ArrayList<>();
        result.add(new NamedValue("Test result1", "Value Test"));
        result.add(new NamedValue("Test2 result1", "Value Test2"));

        MapAnnotationContainer mapAnnotation = new MapAnnotationContainer();
        mapAnnotation.setContent(result);

        image.addMapAnnotation(root, mapAnnotation);

        List<NamedValue> results = image.getKeyValuePairs(root);

        assert (results.size() == 2);
        assertEquals(image.getValue(root, "Test result1"), "Value Test");

        root.deleteImage(image);
    }


    public void testPairKeyValue3() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        File f = new File("./8bit-unsigned&pixelType=uint8&sizeZ=3&sizeC=5&sizeT=7&sizeX=512&sizeY=512.fake");
        if(!f.createNewFile())
            System.err.println("\"" + f.getCanonicalPath() + "\" could not be created.");

        DatasetContainer dataset = root.getDataset(2L);

        dataset.importImages(root, "./8bit-unsigned&pixelType=uint8&sizeZ=3&sizeC=5&sizeT=7&sizeX=512&sizeY=512.fake");

        List<ImageContainer> images = dataset.getImages(root);

        ImageContainer image = images.get(0);

        image.addPairKeyValue(root, "Test result1", "Value Test");
        image.addPairKeyValue(root, "Test result2", "Value Test2");

        List<NamedValue> results = image.getKeyValuePairs(root);

        assert (results.size() == 2);
        try {
            image.getValue(root, "Nonexistent value");
            assert (false);
        } catch (Exception e) {
            assert (true);
        } finally {
            root.deleteImage(image);
        }
    }


    public void testGetImageInfo() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        ImageContainer image = root.getImage(1L);

        assertEquals("image1.fake", image.getName());
        assertNull(image.getDescription());
        assert (1L == image.getId());
    }


    public void testGetImageTag() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        ImageContainer image = root.getImage(1L);

        List<TagAnnotationContainer> tags = image.getTags(root);
        assert (tags.size() == 2);
    }


    public void testGetImageSize() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        ImageContainer image = root.getImage(1L);

        PixelContainer pixels = image.getPixels();

        int sizeX = pixels.getSizeX();
        int sizeY = pixels.getSizeY();
        int sizeZ = pixels.getSizeZ();
        int sizeC = pixels.getSizeC();
        int sizeT = pixels.getSizeT();

        assert (512 == sizeX);
        assert (512 == sizeY);
        assert (3 == sizeZ);
        assert (5 == sizeC);
        assert (7 == sizeT);
    }


    public void testGetRawData() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        ImageContainer   image  = root.getImage(1L);
        PixelContainer   pixels = image.getPixels();
        double[][][][][] value  = pixels.getAllPixels(root);

        int sizeX = pixels.getSizeX();
        int sizeY = pixels.getSizeY();
        int sizeZ = pixels.getSizeZ();
        int sizeC = pixels.getSizeC();
        int sizeT = pixels.getSizeT();

        assertEquals(sizeX, value[0][0][0][0].length);
        assertEquals(sizeY, value[0][0][0].length);
        assertEquals(sizeC, value[0][0].length);
        assertEquals(sizeZ, value[0].length);
        assertEquals(sizeT, value.length);
    }


    public void testGetRawData2() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        ImageContainer image  = root.getImage(1L);
        PixelContainer pixels = image.getPixels();
        byte[][][][]   value  = pixels.getRawPixels(root, 1);

        int sizeX = pixels.getSizeX();
        int sizeY = pixels.getSizeY();
        int sizeZ = pixels.getSizeZ();
        int sizeC = pixels.getSizeC();
        int sizeT = pixels.getSizeT();

        assertEquals(sizeX * sizeY, value[0][0][0].length);
        assertEquals(sizeC, value[0][0].length);
        assertEquals(sizeZ, value[0].length);
        assertEquals(sizeT, value.length);
    }


    public void testGetRawDataBound() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        ImageContainer image  = root.getImage(1L);
        PixelContainer pixels = image.getPixels();

        int[] xBound = {0, 2};
        int[] yBound = {0, 2};
        int[] cBound = {0, 2};
        int[] zBound = {0, 2};
        int[] tBound = {0, 2};

        double[][][][][] value = pixels.getAllPixels(root, xBound, yBound, cBound, zBound, tBound);

        assertEquals(3, value[0][0][0][0].length);
        assertEquals(3, value[0][0][0].length);
        assertEquals(3, value[0][0].length);
        assertEquals(3, value[0].length);
        assertEquals(3, value.length);
    }


    public void testGetRawDataBoundError() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        ImageContainer image  = root.getImage(1L);
        PixelContainer pixels = image.getPixels();

        int[] xBound = {511, 513};
        int[] yBound = {0, 2};
        int[] cBound = {0, 2};
        int[] zBound = {0, 2};
        int[] tBound = {0, 2};
        try {
            double[][][][][] value = pixels.getAllPixels(root, xBound, yBound, cBound, zBound, tBound);
            assertNotEquals(3, value[0][0][0][0].length);
        } catch (Exception e) {
            assert (true);
        }
    }


    public void testGetRawDataBoundErrorNegative() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        ImageContainer image  = root.getImage(1L);
        PixelContainer pixels = image.getPixels();

        int[] xBound = {-1, 1};
        int[] yBound = {0, 2};
        int[] cBound = {0, 2};
        int[] zBound = {0, 2};
        int[] tBound = {0, 2};
        try {
            double[][][][][] value = pixels.getAllPixels(root, xBound, yBound, cBound, zBound, tBound);
            assertNotEquals(3, value[0][0][0][0].length);
        } catch (Exception e) {
            assert (true);
        }
    }


    public void testToImagePlusBound() throws Exception {
        DebugTools.enableLogging("OFF");
        int[] xBound = {0, 2};
        int[] yBound = {0, 2};
        int[] cBound = {0, 2};
        int[] zBound = {0, 2};
        int[] tBound = {0, 2};

        Random random = new Random();
        xBound[0] = random.nextInt(500);
        yBound[0] = random.nextInt(500);
        cBound[0] = random.nextInt(3);
        tBound[0] = random.nextInt(5);
        xBound[1] = random.nextInt(507 - xBound[0]) + xBound[0] + 5;
        yBound[1] = random.nextInt(507 - yBound[0]) + yBound[0] + 5;
        cBound[1] = random.nextInt(3 - cBound[0]) + cBound[0] + 2;
        tBound[1] = random.nextInt(5 - tBound[0]) + tBound[0] + 2;

        String fake     = "8bit-unsigned&pixelType=uint8&sizeZ=3&sizeC=5&sizeT=7&sizeX=512&sizeY=512.fake";
        File   fakeFile = new File(fake);

        if(!fakeFile.createNewFile())
            System.err.println("\"" + fakeFile.getCanonicalPath() + "\" could not be created.");

        ImagePlus reference = BF.openImagePlus(fake)[0];

        if(!fakeFile.delete())
            System.err.println("\"" + fakeFile.getCanonicalPath() + "\" could not be deleted.");

        Duplicator duplicator = new Duplicator();
        reference.setRoi(xBound[0], yBound[0], xBound[1] - xBound[0] + 1, yBound[1] - yBound[0] + 1);
        ImagePlus crop = duplicator
                .run(reference, cBound[0] + 1, cBound[1] + 1, zBound[0] + 1, zBound[1] + 1, tBound[0] + 1,
                     tBound[1] + 1);

        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        ImageContainer image = root.getImage(1L);

        ImagePlus imp = image.toImagePlus(root, xBound, yBound, cBound, zBound, tBound);

        int[] dimensions          = imp.getDimensions();
        int[] referenceDimensions = crop.getDimensions();

        ImageCalculator calculator = new ImageCalculator();
        ImagePlus       difference = calculator.run("difference create stack", crop, imp);
        ImageStatistics stats      = difference.getStatistics();

        assertEquals(0.5, imp.getCalibration().pixelHeight);
        assertEquals(0.5, imp.getCalibration().pixelWidth);
        assertEquals(1.0, imp.getCalibration().pixelDepth);
        assertEquals("MICROMETER", imp.getCalibration().getUnit());
        assertEquals(referenceDimensions[0], dimensions[0]);
        assertEquals(referenceDimensions[1], dimensions[1]);
        assertEquals(referenceDimensions[2], dimensions[2]);
        assertEquals(referenceDimensions[3], dimensions[3]);
        assertEquals(referenceDimensions[4], dimensions[4]);
        assertEquals(0, (int) stats.max);
    }


    public void testToImagePlus() throws Exception {
        DebugTools.enableLogging("OFF");

        String fake     = "8bit-unsigned&pixelType=uint8&sizeZ=3&sizeC=5&sizeT=7&sizeX=512&sizeY=512.fake";
        File   fakeFile = new File(fake);

        if(!fakeFile.createNewFile())
            System.err.println("\"" + fakeFile.getCanonicalPath() + "\" could not be created.");

        ImagePlus reference = BF.openImagePlus(fake)[0];

        if(!fakeFile.delete())
            System.err.println("\"" + fakeFile.getCanonicalPath() + "\" could not be deleted.");

        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        ImageContainer image = root.getImage(1L);

        ImagePlus imp = image.toImagePlus(root);

        int[] dimensions          = imp.getDimensions();
        int[] referenceDimensions = reference.getDimensions();

        ImageCalculator calculator = new ImageCalculator();
        ImagePlus       difference = calculator.run("difference create stack", reference, imp);
        ImageStatistics stats      = difference.getStatistics();

        assertEquals(0.5, imp.getCalibration().pixelHeight);
        assertEquals(0.5, imp.getCalibration().pixelWidth);
        assertEquals(1.0, imp.getCalibration().pixelDepth);
        assertEquals("MICROMETER", imp.getCalibration().getUnit());
        assertEquals(referenceDimensions[0], dimensions[0]);
        assertEquals(referenceDimensions[1], dimensions[1]);
        assertEquals(referenceDimensions[2], dimensions[2]);
        assertEquals(referenceDimensions[3], dimensions[3]);
        assertEquals(referenceDimensions[4], dimensions[4]);
        assertEquals(0, (int) stats.max);
    }


    public void testGetImageChannel() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        ImageContainer image = root.getImage(1L);
        assertEquals("0", image.getChannelName(root, 0));
    }


    public void testGetImageChannelError() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        ImageContainer image = root.getImage(1L);

        try {
            image.getChannelName(root, 6);
            assert (false);
        } catch (Exception e) {
            assert (true);
        }
    }


    public void testAddTagToImage() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        ImageContainer image = root.getImage(3L);

        TagAnnotationContainer tag = new TagAnnotationContainer(root, "image tag", "tag attached to an image");

        image.addTag(root, tag);

        List<TagAnnotationContainer> tags = image.getTags(root);

        assert (tags.size() == 1);

        root.deleteTag(tag);

        tags = image.getTags(root);

        assert (tags.size() == 0);
    }


    public void testAddTagToImage2() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        ImageContainer image = root.getImage(3L);

        image.addTag(root, "image tag", "tag attached to an image");

        List<TagAnnotationContainer> tags = root.getTags("image tag");
        assert (tags.size() == 1);

        root.deleteTag(tags.get(0).getId());

        tags = root.getTags("image tag");
        assert (tags.size() == 0);
    }


    public void testAddTagIdToImage() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        ImageContainer image = root.getImage(3L);

        TagAnnotationContainer tag = new TagAnnotationContainer(root, "image tag", "tag attached to an image");

        image.addTag(root, tag.getId());

        List<TagAnnotationContainer> tags = image.getTags(root);

        assert (tags.size() == 1);

        root.deleteTag(tag);

        tags = image.getTags(root);

        assert (tags.size() == 0);
    }


    public void testAddTagsToImage() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        ImageContainer image = root.getImage(3L);

        TagAnnotationContainer tag1 = new TagAnnotationContainer(root, "Image tag", "tag attached to an image");
        TagAnnotationContainer tag2 = new TagAnnotationContainer(root, "Image tag", "tag attached to an image");
        TagAnnotationContainer tag3 = new TagAnnotationContainer(root, "Image tag", "tag attached to an image");
        TagAnnotationContainer tag4 = new TagAnnotationContainer(root, "Image tag", "tag attached to an image");

        image.addTags(root, tag1.getId(), tag2.getId(), tag3.getId(), tag4.getId());

        List<TagAnnotationContainer> tags = image.getTags(root);

        assert (tags.size() == 4);

        root.deleteTag(tag1);
        root.deleteTag(tag2);
        root.deleteTag(tag3);
        root.deleteTag(tag4);

        tags = image.getTags(root);

        assert (tags.size() == 0);
    }


    public void testAddTagsToImage2() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        ImageContainer image = root.getImage(3L);

        TagAnnotationContainer tag1 = new TagAnnotationContainer(root, "Image tag", "tag attached to an image");
        TagAnnotationContainer tag2 = new TagAnnotationContainer(root, "Image tag", "tag attached to an image");
        TagAnnotationContainer tag3 = new TagAnnotationContainer(root, "Image tag", "tag attached to an image");
        TagAnnotationContainer tag4 = new TagAnnotationContainer(root, "Image tag", "tag attached to an image");

        image.addTags(root, tag1, tag2, tag3, tag4);

        List<TagAnnotationContainer> tags = image.getTags(root);

        assert (tags.size() == 4);

        root.deleteTag(tag1);
        root.deleteTag(tag2);
        root.deleteTag(tag3);
        root.deleteTag(tag4);

        tags = image.getTags(root);

        assert (tags.size() == 0);
    }


    public void testImageOrder() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        List<ImageContainer> images = root.getImages();

        for (int i = 1; i < images.size(); i++) {
            assert (images.get(i - 1).getId() <= images.get(i).getId());
        }
    }


    public void testAddFileImage() throws Exception {
        DebugTools.enableLogging("OFF");
        Client root = new Client();
        root.connect("omero", 4064, "root", "omero", 3L);

        ImageContainer image = root.getImage(1L);

        File file = new File("./test.txt");
        if(!file.createNewFile())
            System.err.println("\"" + file.getCanonicalPath() + "\" could not be created.");

        Long id = image.addFile(root, file);
        if(!file.delete())
            System.err.println("\"" + file.getCanonicalPath() + "\" could not be deleted.");

        root.deleteFile(id);
    }

}
