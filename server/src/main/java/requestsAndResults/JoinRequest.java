package requestsAndResults;

public record JoinRequest (
        String authToken,
        String playerColor,
        int gameID
){
}
