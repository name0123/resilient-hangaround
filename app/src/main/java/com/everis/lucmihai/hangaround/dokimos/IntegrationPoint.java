package com.everis.lucmihai.hangaround.dokimos;

import android.location.Location;

/**
 * Created by John on 7/19/2016.
 */
public interface IntegrationPoint extends Dokimos {
    public void getXPlacesAroundLocation(Location location, XPlaces x, Timeout timeout);

    class XPlaces {
        int xplaces;

        public void setXplaces(int xplaces) {
            this.xplaces = xplaces;
        }

        public XPlaces(int xplaces) {
            this.xplaces = xplaces;
        }
    }

    class Timeout {
        float timeout;

        public Timeout(float timeout) {
            this.timeout = timeout;
        }
    }
}
