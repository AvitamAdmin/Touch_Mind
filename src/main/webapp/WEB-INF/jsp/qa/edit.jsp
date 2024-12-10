<%@ include file="../include.jsp" %>
<div class="main-content">
  <div class="row">
    <div class="col-sm-12">
      <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
    </div>
  </div>
  <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/qa/add" modelAttribute="editForm">
    <div class="row">
      <div class="col-sm-12">
        <div class="dt-buttons">
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/qa')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
        </div>
      </div>
    </div>
    <%@ include file="../commonFields.jsp" %>
    <form:input path="recordId" class="inputbox-cheil-small" placeholder="Record Id" style="display:none" type="hidden"/>
    <div class="row">
      <div class="col-sm-4">
        <form:input path="identifier" class="inputbox-cheil-small" placeholder="Enter Id" required="required" />
        <span>Enter Id</span>
        <form:errors path="identifier" class="text-danger"></form:errors>
      </div>
      <div class="col-sm-4">
        <form:input path="shortDescription" class="inputbox-cheil-long" placeholder="Enter Description" />
        <span>Enter Description</span>
        <form:errors path="shortDescription" class="text-danger"></form:errors>
      </div>
      <div class="col-sm-4">
         <select name="subsidiary"  class="3col active cheil-select" id="selectpicker2">
            <option value="">Select subsidiary</option>
            <c:forEach items="${subsidiaries}" var="child">
                <c:choose>
                    <c:when test="${editForm.subsidiary == child.id}">
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
    </br></br>
    <div class="row">
    <div class="col-sm-6">
            <select name="testLocatorGroups" class="3col active cheil-select" placeholder="Select Test Group" multiple id="selectpicker" required="required">
            <span>Select test group</span>
              <c:forEach items="${testLocatorGroups}" var="child">
                <c:choose>
                  <c:when test="${fn:contains(editForm.testLocatorGroups, child.id) }">
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

  $(document).ready(function() {
          var sitesData = new Choices('#selectpicker', {
                         removeItemButton: true,
                         maxItemCount:-1,
                         searchResultLimit:20,
                         renderChoiceLimit:-1
          });
          var sitesData = new Choices('#selectpicker2', {
                                   removeItemButton: true,
                                   maxItemCount:-1,
                                   searchResultLimit:20,
                                   renderChoiceLimit:-1
                    });
   });

</script>