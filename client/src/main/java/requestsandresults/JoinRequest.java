package requestsandresults;

public record JoinRequest (
        String authToken,
        String playerColor,
        int gameID
){
}
