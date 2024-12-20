package com.touchmind.core.mongo.mapper;

import com.touchmind.core.mongo.dto.CronJobDto;
import com.touchmind.core.mongo.model.CronJob;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-19T16:41:47+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 22.0.2 (Amazon.com Inc.)"
)
@Component
public class CronJobMapperImpl implements CronJobMapper {

    @Override
    public CronJob toEntity(CronJobDto cronJobDto) {
        if ( cronJobDto == null ) {
            return null;
        }

        CronJob cronJob = new CronJob();

        cronJob.setIdentifier( cronJobDto.getIdentifier() );
        cronJob.setShortDescription( cronJobDto.getShortDescription() );
        cronJob.setStatus( cronJobDto.getStatus() );
        cronJob.setRecordId( cronJobDto.getRecordId() );
        cronJob.setCreator( cronJobDto.getCreator() );
        cronJob.setCreationTime( cronJobDto.getCreationTime() );
        cronJob.setLastModified( cronJobDto.getLastModified() );
        cronJob.setModifiedBy( cronJobDto.getModifiedBy() );
        cronJob.setEmailSubject( cronJobDto.getEmailSubject() );
        cronJob.setCronExpression( cronJobDto.getCronExpression() );
        cronJob.setSkus( cronJobDto.getSkus() );
        cronJob.setJobStatus( cronJobDto.getJobStatus() );
        cronJob.setEnableHistory( cronJobDto.getEnableHistory() );
        cronJob.setDashboard( cronJobDto.getDashboard() );
        cronJob.setSiteUrl( cronJobDto.getSiteUrl() );
        cronJob.setCronProfileId( cronJobDto.getCronProfileId() );
        List<String> list = cronJobDto.getEnvProfiles();
        if ( list != null ) {
            cronJob.setEnvProfiles( new ArrayList<String>( list ) );
        }
        cronJob.setCampaign( cronJobDto.getCampaign() );

        return cronJob;
    }

    @Override
    public CronJobDto toDto(CronJob cronJob) {
        if ( cronJob == null ) {
            return null;
        }

        CronJobDto cronJobDto = new CronJobDto();

        cronJobDto.setRecordId( cronJob.getRecordId() );
        cronJobDto.setIdentifier( cronJob.getIdentifier() );
        cronJobDto.setStatus( cronJob.getStatus() );
        cronJobDto.setShortDescription( cronJob.getShortDescription() );
        cronJobDto.setCreator( cronJob.getCreator() );
        cronJobDto.setCreationTime( cronJob.getCreationTime() );
        cronJobDto.setLastModified( cronJob.getLastModified() );
        cronJobDto.setModifiedBy( cronJob.getModifiedBy() );
        cronJobDto.setEmailSubject( cronJob.getEmailSubject() );
        cronJobDto.setCronExpression( cronJob.getCronExpression() );
        cronJobDto.setSkus( cronJob.getSkus() );
        cronJobDto.setJobStatus( cronJob.getJobStatus() );
        cronJobDto.setEnableHistory( cronJob.getEnableHistory() );
        cronJobDto.setDashboard( cronJob.getDashboard() );
        cronJobDto.setSiteUrl( cronJob.getSiteUrl() );
        cronJobDto.setCronProfileId( cronJob.getCronProfileId() );
        List<String> list = cronJob.getEnvProfiles();
        if ( list != null ) {
            cronJobDto.setEnvProfiles( new ArrayList<String>( list ) );
        }
        cronJobDto.setCampaign( cronJob.getCampaign() );

        return cronJobDto;
    }

    @Override
    public List<CronJobDto> toDtoList(List<CronJob> cronJobs) {
        if ( cronJobs == null ) {
            return null;
        }

        List<CronJobDto> list = new ArrayList<CronJobDto>( cronJobs.size() );
        for ( CronJob cronJob : cronJobs ) {
            list.add( toDto( cronJob ) );
        }

        return list;
    }
}
