package requestsAndResults;

import java.util.List;

public record ListResult (
        List<GameInfo> games
) {}
