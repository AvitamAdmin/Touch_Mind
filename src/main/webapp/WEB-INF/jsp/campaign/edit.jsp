<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/campaigns/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/campaigns')" aria-controls="tableData" type="button" title="Cancel">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="submitFormById('#editForm');" aria-controls="tableData" type="button" title="Save">Save</button>
                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <div class="row">
            <div class="col-sm-4">
            <input name="recordId" class="inputbox-cheil"  type="hidden"  value="${editForm.recordId}"></input>
            <input name="identifier" class="inputbox-cheil" placeholder="Identifier"  value="${editForm.identifier}"></input>
            <span>Identifier</span>
            </div>
            <div class="col-sm-4">
            <input name="shortDescription" class="inputbox-cheil" placeholder="Description"  value="${editForm.shortDescription}"></input>
            <span>Description</span>
            </div>
        </div>
        <br/><br/>
        <div class="row">
            <div class="col-sm-12">
                 <select name="domPaths[]" placeholder="Select Dom paths" multiple class="3col active cheil-select" id="pathsPicker">
                     <c:forEach items="${crawlerPaths}" var="child">
                         <c:choose>
                             <c:when test="${fn:contains( editForm.domPaths, child.pathCategory ) }">
                               <option value="${child.pathCategory}" selected>${child.pathCategory}</option>
                             </c:when>
                             <c:otherwise>
                                <option value="${child.pathCategory}" >${child.pathCategory}</option>
                             </c:otherwise>
                         </c:choose>
                     </c:forEach>
                 </select>
            </div>
        </div>
       </form:form>
       <c:if test="${not empty message}">
           <div class="alert alert-danger" role="alert" id="errorMessage">
               <spring:message code="${message}" />
           </div>
       </c:if>
</div>
<script type="text/javascript">
         var multipleCancelButton = new Choices('#pathsPicker', {
             removeItemButton: true,
             maxItemCount:-1,
             searchResultLimit:20,
             renderChoiceLimit:-1,
             placeholder: true,
             placeholderValue: "Select Dom Path"
           });
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

