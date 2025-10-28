package requestsAndResults;

public record GameInfo (
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName
) {}