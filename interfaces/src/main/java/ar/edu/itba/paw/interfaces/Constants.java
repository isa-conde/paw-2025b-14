package ar.edu.itba.paw.interfaces;

import java.util.List;

public final class Constants {
    private Constants() {}

    public static final long MAX_PFP_SIZE = 2 * 1024 * 1024;
    public static final long MAX_BANNER_SIZE = 3 * 1024 * 1024;
    public static final long MAX_PDF_SIZE = 4 * 1024 * 1024;
    public static final int MAX_NAME_SIZE = 100;
    public static final int MAX_BIO_SIZE = 255;
    public static final List<Integer> TEAM_SIZES = List.of(1,2,3,4,5);
    public static final String TOURNAMENTS_OWNED = "owned";
    public static final String TOURNAMENTS_FINISHED= "finished";
    public static final String TOURNAMENTS_ACTIVE= "active";
    public static final boolean FINISHED = true;
    public static final boolean ONGOING = false;
    public static final boolean CREATOR = true;
    public static final boolean PARTICIPANT = false;
    public static final boolean WON = true;
    public static final boolean ALL_TOURNEYS = false;
    public static final int MIN_HYBRID_PARTICIPANTS = 9;
    public static final int MIN_PARTICIPANTS_PER_GROUP = 3;
    public static final int MAX_GROUPS = 16;

}