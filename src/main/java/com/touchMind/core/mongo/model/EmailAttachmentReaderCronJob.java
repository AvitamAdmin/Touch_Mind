package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("EmailAttachmentReaderCronJobs")
@Getter
@Setter
@NoArgsConstructor
public class EmailAttachmentReaderCronJob extends SchedulerJob {
}
