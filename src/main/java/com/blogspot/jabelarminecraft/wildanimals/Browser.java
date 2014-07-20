package com.blogspot.jabelarminecraft.wildanimals;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import net.minecraft.client.gui.ScaledResolution;

public class Browser {

	public static void browse(String parUrl) {
		// Create Desktop object
	    Desktop d=Desktop.getDesktop();

	    // Browse a URL, say google.com
	    try {
			d.browse(new URI(parUrl));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    ScaledResolution sr;
	}
}
