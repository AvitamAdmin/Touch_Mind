package com.touchMind.core.mongo.dto;

import com.touchMind.core.mongo.model.Action;
import com.touchMind.core.mongo.model.Library;
import com.touchMind.core.mongo.model.Media;
import com.touchMind.core.mongo.model.Site;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LibraryWsDto extends CommonWsDto {
    private List<LibraryDto> libraries;
    private String backUrl;
    private String libId;
    private String[] types;
    private List<Action> actionList;
    private List<Media> mediaList;
    private Map<String, List<Media>> actionMediaMap;
    private Map<String, List<Library>> actionLibMap;
    private List<Library> subLibraries;
    private Map<String, List<Site>> subSiteMap;
    private List<Action> actionsList;
    private List<Library> subLibrariesList;
}
