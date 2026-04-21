package net.maaroufi.claudeassistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private String reply;
    private String model;
    private long inputTokens;
    private long outputTokens;
}
