<hr/>
<br/>
        <div class="row">
            <form:input path="id" class="inputbox-cheil-small" placeholder="PK" readonly="true" type="hidden"/>
            <div class="col-sm-3">
                <form:input path="creator" class="inputbox-cheil-small" placeholder="Creator" readonly="true"/>
                <span>Creator</span>
            </div>

            <div class="col-sm-3">
                <form:input path="creationTime" class="inputbox-cheil" placeholder="Creation Time" readonly="true" />
                <span>Creation Time</span>
            </div>
            <div class="col-sm-3">
                <form:input path="lastModified" class="inputbox-cheil" placeholder="Last modified" readonly="true"/>
                <span>Last modified</span>
            </div>
            <div class="col-sm-3">
                <c:choose>
                    <c:when test="${editForm.status}">
                         <c:set var="varChecked" value="checked"></c:set>
                     </c:when>
                     <c:otherwise>
                         <c:set var="varUnchecked" value="checked"></c:set>
                     </c:otherwise>
                </c:choose>
                <input type="radio" name="status"  value="true" ${varChecked}> Active
                <input type="radio" name="status" value="false" ${varUnchecked}> Inactive
            </div>
        </div>
        <br/>
        <br/>