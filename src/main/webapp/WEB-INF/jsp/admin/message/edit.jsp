<%@ include file="../../include.jsp" %>
<div class="main-content">
  <div class="row">
    <div class="col-sm-12">
      <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
    </div>
  </div>
  <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/messages/add" modelAttribute="editForm">
    <div class="row">
      <div class="col-sm-12">
        <div class="dt-buttons">
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/messages')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
        </div>
      </div>
    </div>
    <%@ include file="../../commonFields.jsp" %>
    <form:input path="recordId" class="inputbox-cheil-small" placeholder="Record Id" style="display:none" type="hidden"/>
    <div class="row">
      <div class="col-sm-4">
        <form:input path="identifier" class="inputbox-cheil-small" placeholder="Enter the unique identifier" required="required" />
        <span>Enter Id</span>
        <form:errors path="identifier" class="text-danger"></form:errors>
      </div>
      <div class="col-sm-4">
        <form:input path="description" class="inputbox-cheil-long" placeholder="Enter Description" />
        <span>Enter Description</span>
        <form:errors path="description" class="text-danger"></form:errors>
      </div>
      <div class="col-sm-4">
      <select name="testPlanId" class="3col active">
          <c:forEach items="${testPlans}" var="testPlan">
              <option value="${testPlan.recordId}" <c:if test="${testPlan.recordId == editForm.testPlanId}">selected</c:if>>${testPlan.identifier}</option>
          </c:forEach>
      </select>
        <span>Select the test plan</span>
        <form:errors path="testPlanId" class="text-danger"></form:errors>
      </div>
      </div>
      <br/><br/>
      <div class="row">
        <div class="col-sm-4">
            <form:input path="type" class="inputbox-cheil-small" placeholder="Enter the channel type" required="required" />
            <span>Channel type</span>
            <form:errors path="type" class="text-danger"></form:errors>
          </div>
          <div class="col-sm-4">
            <form:input path="percentFailure" class="inputbox-cheil-long" placeholder="Enter failure condition value (only numbers < 100)" required="required"  />
            <span>Percent failure</span>
            <form:errors path="percentFailure" class="text-danger"></form:errors>
          </div>
          <div class="col-sm-4">
                <form:input path="recipients" class="inputbox-cheil-small" placeholder="Enter the recipients (example: +49111111,+4811111)" required="required" />
                 <span>Recipients</span>
                 <form:errors path="recipients" class="text-danger"></form:errors>
          </div>
        </div>
        <br/><br/>
  </form:form>
<script type="text/javascript">
</script>

  <c:if test="${not empty message}">
    <div class="alert alert-danger" role="alert" id="errorMessage">
      <spring:message code="${message}" />
    </div>
  </c:if>
</div>
</div>
</div>