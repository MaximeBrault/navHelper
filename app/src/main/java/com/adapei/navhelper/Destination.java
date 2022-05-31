package com.adapei.navhelper;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a destination, it extends location.
 */
public class Destination extends Location {
    private String nickname;
    private String iconId;
    private String foyer;

    /**
     * The destination's picture.
     */
    private String pictureUri;

    public Destination(String displayName, String nickname, String iconId, double latitude, double longitude, String pronunciation, String foyer) {
        super(latitude, longitude, displayName, pronunciation);

        if (iconId != null) {
            this.nickname = nickname;
            // regex for the picture
            Pattern p = Pattern.compile(".*\\..*");
            Matcher m = p.matcher(iconId);

            if(m.matches()) {
                this.pictureUri = iconId;
                this.iconId = null;
            } else {
                this.iconId = iconId;
                this.pictureUri = null;
            }
            this.foyer = foyer;
        } else System.err.println("ERROR-public Destination(double latitude, double longitude, String displayName, String pronunciation, String iconId) : iconId can't be null.");
    }

    public String getRepresentation() { return this.iconId == null ? this.pictureUri : this.iconId; }

    /**
     * Get the destination's nickname.
     * @return The destination's icon.
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * Tell if a destination is represented by an icon
     * @return
     */
    public boolean isRepresentedByIcon() { return this.iconId != null; }

    public boolean hasANick() { return this.nickname != null; }


    @Override
    /**
     * Check if two destinations are the same.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Destination that = (Destination) o;
        return Objects.equals(nickname, that.nickname) &&
                Objects.equals(pictureUri, that.pictureUri);
    }

    @Override
    /**
     * Hash a destination.
     */
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.nickname, this.pictureUri);
    }

    public void setNickname(String nick){ this.nickname = nick; }

    public String getFoyer() {
        return this.foyer;
    }

    public void setIconId(String iconId) { this.iconId = iconId; }
}
