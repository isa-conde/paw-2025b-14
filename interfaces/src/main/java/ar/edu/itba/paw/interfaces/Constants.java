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
    public static final Boolean FINISHED = true;
    public static final Boolean ONGOING = false;
    public static final Boolean CREATOR = true;
    public static final Boolean PARTICIPANT = false;
    public static final Boolean WON = true;
    public static final Boolean ALLTOURNEYS = false;



}