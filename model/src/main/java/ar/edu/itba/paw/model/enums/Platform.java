package ar.edu.itba.paw.model.enums;

public enum Platform {
    DISCORD,
    STEAM,
    EPIC_GAMES,
    RIOT_GAMES,
    PLAYSTATION,
    XBOX,
    NINTENDO,
    BATTLE_NET,
    UBISOFT_CONNECT,
    ROCKSTAR;

    public String getLabel() {
        String normal = this.name().toLowerCase().replace("_", " ");
        return normal.substring(0, 1).toUpperCase() + normal.substring(1);
    }
}
