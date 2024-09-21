package ru.practicum.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ElementStatsSaveDto {
    private int id;
    @NotEmpty
    @NotNull
    private String app;

    @NotEmpty
    @NotNull
    private String uri;

    @NotEmpty
    @NotNull
    private String ip;

    @NotEmpty
    @NotNull
    private LocalDateTime createdDate;
}
