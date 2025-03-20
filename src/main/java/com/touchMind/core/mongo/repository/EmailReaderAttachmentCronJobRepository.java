package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.EmailAttachmentReaderCronJob;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("EmailReaderAttachmentCronJobRepository")
public interface EmailReaderAttachmentCronJobRepository extends GenericImportRepository<EmailAttachmentReaderCronJob> {
    EmailAttachmentReaderCronJob findByIdentifier(String valueOf);

    EmailAttachmentReaderCronJob findByNodePath(String path);

    List<EmailAttachmentReaderCronJob> findByStatusOrderByIdentifier(Boolean status);

    void deleteByIdentifier(String identifier);

}
