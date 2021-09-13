DROP TABLE IF EXISTS Leagues;
DROP TABLE IF EXISTS LeagueSettings;
DROP TABLE IF EXISTS Players;
DROP TABLE IF EXISTS Trades;
DROP TABLE IF EXISTS TradedPlayers;
DROP TABLE IF EXISTS FantasySite;

create table FantasySite (
    siteId INTEGER NOT NULL UNIQUE,
    siteName TEXT NOT NULL
);

create table Leagues(
	leagueId VARCHAR(50) NOT NULL UNIQUE,
	siteId INTEGER NOT NULL,
--	numTeams INT NOT NULL,
--	ppr FLOAT NOT NULL,
--	numQbs FLOAT NOT NULL,
--	numWrs INT,
--	numRbs INT,
--	numTes INT,
--	isDynasty BOOLEAN NOT NULL
	CONSTRAINT fk_siteId FOREIGN KEY(siteId) REFERENCES FantasySite(siteId)
);

create table LeagueSettings(
	leagueId VARCHAR(50) NOT NULL,
	numTeams INT NOT NULL,
	ppr FLOAT NOT NULL,
	numQbs FLOAT NOT NULL,
	numWrs INT,
	numRbs INT,
	numTes INT,
	isDynasty BOOLEAN NOT NULL,
	CONSTRAINT fk_leagueId FOREIGN KEY(leagueId) REFERENCES Leagues(leagueId)
);
--
--create table Players(
--    playerId INT NOT NULL AUTO_INCREMENT,
--	name TEXT NOT NULL,
--	mfl_id TEXT
--);
--
--create table Trades(
--    tradeId INT NOT NULL AUTO_INCREMENT,
--    leagueId TEXT NOT NULL,
--    FOREIGN KEY (leaugeId) REFERENCES Leagues(leagueId)
--);
--
--create table TradedPlayers(
--    playerId INT NOT NULL,
--    tradeId INT NOT NULL,
--    tradeSide INT NOT NULL,
--    FOREIGN KEY (playerId) REFERENCES Players(playerId),
--    FOREIGN KEY (tradeId) REFERENCES Trades(tradeId)
--);

INSERT INTO FantasySite VALUES (1, 'MFL');
