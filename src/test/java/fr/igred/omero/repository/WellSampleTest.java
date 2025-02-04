/*
 *  Copyright (C) 2020-2022 GReD
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

package fr.igred.omero.repository;


import fr.igred.omero.UserTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


class WellSampleTest extends UserTest {


    @Test
    void testGetImage() throws Exception {
        final String name = "screen1.fake [screen1 2]";

        PlateWrapper plate = client.getPlate(PLATE1.id);
        WellWrapper  well  = plate.getWells(client).get(0);

        WellSampleWrapper sample = well.getWellSamples().get(1);

        ImageWrapper image = sample.getImage();

        assertFalse(image.isOrphaned(client));
        assertEquals(name, image.getName());
    }


    @Test
    void testGetWell() throws Exception {
        final long  wellId = 1L;
        WellWrapper well   = client.getWell(wellId);
        WellSampleWrapper sample = well.getWellSamples().get(0);

        assertEquals(wellId, sample.getWell(client).getId());
    }


    @Test
    void testGetPositionX() throws Exception {
        WellWrapper well = client.getWells(1L).get(0);

        WellSampleWrapper sample = well.getWellSamples().get(0);
        assertEquals(0.0, sample.getPositionX(null).getValue(), Double.MIN_VALUE);
    }


    @Test
    void testGetPositionY() throws Exception {
        WellWrapper well = client.getWells(1L).get(0);

        WellSampleWrapper sample = well.getWellSamples().get(0);
        assertEquals(1.0, sample.getPositionY(null).getValue(), Double.MIN_VALUE);
    }


    @Test
    void testGetStartTime() throws Exception {
        final long time = 1146766431000L;

        WellWrapper well = client.getWells(1L).get(0);

        WellSampleWrapper sample = well.getWellSamples().get(0);
        assertEquals(time, sample.getStartTime());
    }

}