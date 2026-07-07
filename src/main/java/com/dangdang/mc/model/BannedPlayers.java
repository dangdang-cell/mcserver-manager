package com.dangdang.mc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BannedPlayers {
    private String uuid;
    private String name;
    private String created;
    private String source;
    private String expires;
    private String reason;
}
