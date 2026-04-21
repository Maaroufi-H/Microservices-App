package net.maaroufi.claudeassistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationMessage {
    private String role; // "user" or "assistant"
    private String content;
}
