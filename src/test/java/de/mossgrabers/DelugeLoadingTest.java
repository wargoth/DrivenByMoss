package de.mossgrabers;

import de.mossgrabers.bitwig.controller.synthstrom.deluge.DelugeControllerExtensionDefinition;
import org.junit.Test;

public class DelugeLoadingTest {
    @Test
    public void testInstantiation() {
        System.out.println("Attempting to instantiate DelugeControllerExtensionDefinition...");
        DelugeControllerExtensionDefinition def = new DelugeControllerExtensionDefinition();
        System.out.println("Success! Name: " + def.getName());
        System.out.println("Vendor: " + def.getHardwareVendor());
        System.out.println("Model: " + def.getHardwareModel());
    }
}
