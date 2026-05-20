package sa.edu.kau.fcit.cpit252.project.dto;

import java.util.List;

public record PagedResponse<T>(
        List<T> items,
        int page,
        int limit,
        long total
) {}
