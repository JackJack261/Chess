DROP TABLE IF EXISTS `AuthData`;
DROP TABLE IF EXISTS `GameData`;
DROP TABLE IF EXISTS `UserData`;

-- User Table
CREATE TABLE `UserData` (
                        `username` VARCHAR(255) NOT NULL PRIMARY KEY,
                        `passwordHash` VARCHAR(255) NOT NULL,
                        `email` VARCHAR(255) NOT NULL
);

-- Game Table
CREATE TABLE `GameData` (
                        `gameID` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        `whiteUsername` VARCHAR(255),
                        `blackUsername` VARCHAR(255),
                        `gameName` VARCHAR(255) NOT NULL UNIQUE,
                        `gameData` LONGTEXT NOT NULL
);

-- AuthToken Table
CREATE TABLE `AuthData` (
                             `authToken` VARCHAR(255) NOT NULL PRIMARY KEY,
                             `username` VARCHAR(255) NOT NULL
);