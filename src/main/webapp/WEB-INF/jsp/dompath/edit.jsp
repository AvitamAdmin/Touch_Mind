<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/dompath/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/dompath')" aria-controls="tableData" type="button" title="Cancel">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="submitFormById('#editForm');" aria-controls="tableData" type="button" title="Save">Save</button>
                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <form:input path="recordId" class="inputbox-cheil-small" placeholder="Record Id" style="display:none" type="hidden"/>
        <div class="row">
            <div class="col-sm-12">
            <span class="warning">Important : changes will affect campaign and cronjob!!!</span>
            </div>
        </div>
        <br/><br/>
        <div class="row">
            <div class="col-sm-6">
            <form:input name="pathCategory" path="pathCategory" class="inputbox-cheil" placeholder="Path Category" required="required"/>
            <span>Path Category</span>
            </div>
             <div class="col-sm-6">
                 <select class="cheil-select" id="siteIsoCode" name="sites" multiple placeholder="Select Site">
                    <c:forEach items="${sites}" var="child">
                        <c:set var="selectedSites" value=""></c:set>
                        <c:forEach items="${editForm.sites}" var="site">
                            <c:if test="${site eq child.siteId}">
                                <c:set var="selectedSites" value="${site}"></c:set>
                            </c:if>
                        </c:forEach>
                        <c:choose>
                            <c:when test="${selectedSites eq child.siteId}">
                              <option value="${child.siteId}" selected>${child.siteId}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${child.siteId}" >${child.siteId}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                  </select>
              </div>
         </div>
         <br/><br/>
         <div class="row">
            <div class="col-sm-6">
                <span>Crawler Path</span>
                <form:textarea name="crawlerPath" id="crawlerPath" path="crawlerPath" rows="5" cols="200" class="inputbox-cheil" placeholder="Crawler Path"/>
                <form:errors path="crawlerPath" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-6">
                <span class="warning">Important : is ignored unless at least 2 Dom Paths with regex are created!!!</span><br/><br/>
                <span>Path pattern</span>
                <form:textarea name="pattern" id="pattern" path="pattern" rows="5" cols="200" class="inputbox-cheil" placeholder="Applicable pattern"/>
                <form:errors path="crawlerPath" class="text-danger"></form:errors>
            </div>
        </div>
        <br/><br/>

       </form:form>
       <c:if test="${not empty message}">
           <div class="alert alert-danger" role="alert" id="errorMessage">
               <spring:message code="${message}" />
           </div>
       </c:if>
</div>
<script type="text/javascript">
           var siteIsoCode = new Choices('#siteIsoCode', {
             removeItemButton: true,
             maxItemCount:-1,
             searchResultLimit:20,
             renderChoiceLimit:-1
           });
         var multipleCancelButton2 = new Choices('#subsidiary', {
            removeItemButton: true,
            maxItemCount:-1,
            searchResultLimit:20,
            renderChoiceLimit:-1
          });
   </script>

