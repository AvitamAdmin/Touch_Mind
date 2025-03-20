<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/site/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/site')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="submitFormById('#editForm');" aria-controls="tableData" title="Save" type="button">Save</button>
                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <div class="row">
            <div class="col-sm-3">
                <form:input path="identifier" class="inputbox-cheil-small" placeholder="Id/name" />
                <span>Id/Name</span>
                <form:errors path="identifier" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="shortDescription" class="inputbox-cheil-long" placeholder="Short description (128 letters)" />
                <span>Short description</span>
                <form:errors path="shortDescription" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="affiliateId" class="inputbox-cheil" placeholder="Affiliate Id" />
                <span>Affiliate Id</span>
                <form:errors path="affiliateId" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="affiliateName" class="inputbox-cheil" placeholder="Affiliate Name" />
                <span>Affiliate Name</span>
                <form:errors path="affiliateName" class="text-danger"></form:errors>
            </div>
        </div>
        <br/>
        <br/>
        <div class="row">
            <div class="col-sm-3">
                <form:input type="password" path="secretKey" class="inputbox-cheil" placeholder="Secret Key" />
                <span>Secret Key</span>
                <form:errors path="secretKey" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="siteChannel" class="inputbox-cheil" placeholder="Site Channel" />
                <span>Site Channel</span>
                <form:errors path="siteChannel" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                 <select name="subsidiary"  class="3col active cheil-select" id="selectpicker">
                 <option value="">Select subsidiary</option>
                    <c:forEach items="${subsidiaries}" var="child">
                        <c:choose>
                            <c:when test="${fn:contains( editForm.subsidiary, child ) }">
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
             renderChoiceLimit:-1
           });
</script>