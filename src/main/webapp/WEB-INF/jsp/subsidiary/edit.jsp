<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/subsidiary/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/subsidiary')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <div class="row">
            <div class="col-sm-3">
                <form:input path="identifier" class="inputbox-cheil-small" placeholder="Identifier" />
                <span>Identifier</span>
                <form:errors path="identifier" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="shortDescription" class="inputbox-cheil-long" placeholder="Short description (128 letters)" />
                <span>Short description</span>
                <form:errors path="shortDescription" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="cluster" class="inputbox-cheil-small" placeholder="Cluster id/Name" />
                <Span>Cluster id</span>

                <form:errors path="cluster" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="isoCode" class="inputbox-cheil-small" placeholder="IsoCode" />
                <span>IsoCode</span>
                <form:errors path="isoCode" class="text-danger"></form:errors>
            </div>

        </div>
        <br/><br/>

       <div class="row">
        <div class="col-sm-3">
         <select name="language"  class="3col active cheil-select" placeholder-"Select language" id="selectpicker">
            <c:forEach items="${countries}" var="child">
                <c:choose>
                    <c:when test="${editForm.language.id == child.id }">
                      <option value="${child.id}" selected>${child.identifier}</option>
                    </c:when>
                    <c:otherwise>
                       <option value="${child.id}" >${child.identifier}</option>
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
  </div>
</div>
<script type="text/javascript">
         var multipleCancelButton = new Choices('#selectpicker', {
             removeItemButton: true,
             maxItemCount:-1,
             searchResultLimit:20,
             renderChoiceLimit:-1,
             placeholder: true,
             placeholderValue: "Select Value"
           });
</script>