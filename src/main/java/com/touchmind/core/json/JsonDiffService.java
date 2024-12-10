package com.touchmind.core.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.JsonDiff;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class JsonDiffService {

    //@Autowired
    //private Report report;
    public boolean compareJsonFiles(JsonNode jsonNodeSource, JsonNode jsonNodeTarget) {
        JsonNode patch = JsonDiff.asJson(jsonNodeSource, jsonNodeTarget);
        JsonNode patch2 = JsonDiff.asJson(jsonNodeTarget, jsonNodeSource);

        if (patch.size() > 0) {
            Iterator<JsonNode> sourceElements = patch.elements();
            Iterator<JsonNode> targetElements = patch2.elements();
            //report.reportStart("Uploaded file and Remote Api not Identical !!");
            while (sourceElements.hasNext()) {
                JsonNode sourceNode = sourceElements.next();
                JsonNode targetNode = targetElements.next();
                //report.reportFailed(sourceNode.get("path")+" <=> "+targetNode.get("path"),sourceNode.get("value")+" <=> "+targetNode.get("value"));
            }
        } else {
            //report.reportStart("Json comparison with remote API Result");
            //report.reportPassed("Report","","Remote API response and uploaded files are identical!");
        }
        return true;
    }
}
