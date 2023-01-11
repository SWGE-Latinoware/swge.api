package celtab.swge.util.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

@Data
public class EmailOptions {

    @Data
    @AllArgsConstructor
    public static class Attachment {

        private String name;

        private FileSystemResource resource;

    }

    @Data
    @AllArgsConstructor
    public static class InlineResource {

        private String contentId;

        private FileSystemResource resource;

    }

    @Value("${mail.noreply}")
    private String from;

    private List<String> to;

    private String text;

    private Boolean isTextHTML;

    private String subject;

    private List<Attachment> attachments;

    private List<InlineResource> inlineResources;

    public EmailOptions() {
        to = List.of();
        text = "";
        isTextHTML = false;
        subject = "";
    }

    public Boolean isMultipart() {
        return (attachments != null && !attachments.isEmpty()) || (inlineResources != null && !inlineResources.isEmpty());
    }

    public void setTo(String to) {
        this.to = List.of(to);
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

}
