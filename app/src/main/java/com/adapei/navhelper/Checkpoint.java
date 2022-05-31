package com.adapei.navhelper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * Represents a checkpoint, it extends from a location.
 */
public class Checkpoint extends Location {

    /**
     * The checkpoint's picture.
     */
    private URI pictureUri;

    public boolean isMarked;

    /**
     * Checkpoint's constructor.
     * @param latitude The location's latitude of the checkpoint.
     * @param longitude The location's longitude of the checkpoint.
     * @param displayName The location's name of the checkpoint.
     * @param pronunciation The location's pronunciation of the checkpoint.
     * @param pictureUri The checkpoint's picture.
     */
    public Checkpoint(double latitude, double longitude, String displayName, String pronunciation, URI pictureUri) {
        super(latitude, longitude, displayName, pronunciation);
        //set pictureURI to null or to a new URI
        if(pictureUri != null) {
            try {
                this.pictureUri = new URI(pictureUri.toString());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            this.pictureUri = null;
        }
        this.isMarked = false;
    }

    /**
     * Get the uri of the picture.
     * @return the uri of the picture.
     */
    public URI getPictureUri() {
        return this.pictureUri;
    }

    @Override
    /**
     * Check if 2 checkpoints are the same.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Checkpoint that = (Checkpoint) o;
        return Objects.equals(pictureUri, that.pictureUri);
    }

    @Override
    /**
     * Hash a checkpoint.
     */
    public int hashCode() {
        return Objects.hash(super.hashCode(), pictureUri);
    }
}
