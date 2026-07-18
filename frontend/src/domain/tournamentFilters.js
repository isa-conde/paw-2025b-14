export const TOURNAMENT_FILTER_KEYS = [
    'gameId',
    'region',
    'elo',
    'genre',
    'playersPerTeam',
];

export const REGIONS = ['NA', 'LAS', 'LAN', 'BR', 'EUW', 'EUNE', 'OCE', 'ASIA'];
export const ELOS = ['LOW', 'MID', 'HIGH', 'FREE'];
export const STRUCTURES = ['ELIMINATION', 'LEAGUE', 'HYBRID'];
export const GENRES = ['MOBA', 'FPS', 'Fighting', 'TPS', 'BattleRoyale', 'RTS', 'Sports', 'DGC', 'MOBILE'];
export const PLAYERS_PER_TEAM_OPTIONS = [1, 2, 3, 4, 5];

const toLabelMap = (values, t, prefix) => values.reduce((options, value) => ({
    ...options,
    [value]: t(`${prefix}.${value}`),
}), {});

export const getRegionOptions = (t) => toLabelMap(REGIONS, t, 'region');
export const getEloOptions = (t) => toLabelMap(ELOS, t, 'elo');
export const getStructureOptions = (t) => toLabelMap(STRUCTURES, t, 'structure');
export const getGenreOptions = (t) => toLabelMap(GENRES, t, 'genre');
