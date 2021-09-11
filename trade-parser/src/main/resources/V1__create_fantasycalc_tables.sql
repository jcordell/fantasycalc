DROP TABLE IF EXISTS Leagues;
DROP TABLE IF EXISTS Players;
DROP TABLE IF EXISTS Trades;
DROP TABLE IF EXISTS TradedPlayers;

create table Leagues (
	leagueId TEXT NOT NULL,
	numTeams INT NOT NULL,
	ppr FLOAT NOT NULL,
	numQbs FLOAT NOT NULL,
	numWrs INT,
	numRbs INT,
	numTes INT,
	isDynasty BOOLEAN NOT NULL
);

create table Players (
    playerId INT NOT NULL AUTO_INCREMENT,
	name TEXT NOT NULL,
	mfl_id TEXT
);

create table Trades (
    tradeId INT NOT NULL AUTO_INCREMENT,
    leagueId TEXT NOT NULL,
    FOREIGN KEY (leaugeId) REFERENCES Leagues(leagueId)
);

create table TradedPlayers(
    playerId INT NOT NULL,
    tradeId INT NOT NULL,
    tradeSide INT NOT NULL,
    FOREIGN KEY (playerId) REFERENCES Players(playerId),
    FOREIGN KEY (tradeId) REFERENCES Trades(tradeId)
);