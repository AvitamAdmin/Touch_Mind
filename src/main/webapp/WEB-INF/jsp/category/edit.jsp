<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/category/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/category')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="submitFormById('#editForm');" aria-controls="tableData" type="button" title="Save">Save</button>
                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <div class="row">
            <div class="col-sm-4">
                <form:input path="identifier" id="identifier" class="inputbox-cheil" placeholder="Category Id" />
                <span>Category Id</span>
                <form:errors path="identifier" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-4">
                <form:input path="shortDescription" class="inputbox-cheil" placeholder="Short description (128 letters)" />
                <span>Description</span>
                <form:errors path="shortDescription" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-4">
            <select name="subsidiaries[]"  id="selectpicker2" multiple class="3col active cheil-select">
               <option value="">Select subsidiaries</option>
              <c:forEach items="${subsidiaries}" var="child">
                <c:set var="selectedPerm"></c:set>
                <c:forEach items="${editForm.subsidiaries}" var="childSub">
                    <c:if test="${childSub.recordId == child.recordId}">
                        <c:set var="selectedPerm">selected</c:set>
                    </c:if>
                </c:forEach>
              <option value="${child.id}" ${selectedPerm}>${child.identifier}</option>
            </c:forEach>
           </select>
           </div>
           </div>
            <br/>
            <br/>
                <div class="col-sm-4"></div>
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
$(document).ready(function() {
      var multipleCancelButton = new Choices('#selectpicker', {
          removeItemButton: true,
          maxItemCount:-1,
          searchResultLimit:20,
          renderChoiceLimit:-1
        });
    var multipleCancelButton = new Choices('#selectpicker2', {
              removeItemButton: true,
              maxItemCount:-1,
              searchResultLimit:20,
              renderChoiceLimit:-1
            });
    var multipleCancelButton = new Choices('#parentId', {
              removeItemButton: true,
              maxItemCount:-1,
              searchResultLimit:20,
              renderChoiceLimit:-1
            });
});
</script>