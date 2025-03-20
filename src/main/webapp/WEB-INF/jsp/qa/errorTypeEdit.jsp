<%@ include file="../include.jsp" %>
<div class="main-content">
  <div class="row">
    <div class="col-sm-12">
      <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
    </div>
  </div>
  <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/errorType/edit" modelAttribute="editForm">
    <div class="row">
      <div class="col-sm-12">
        <div class="dt-buttons">
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/errorType')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
        </div>
      </div>
    </div>
    <%@ include file="../commonFields.jsp" %>
    <form:input path="recordId" class="inputbox-cheil-small" placeholder="Record Id" style="display:none" type="hidden"/>
    <div class="row">
      <div class="col-sm-3">
        <form:input path="identifier" class="inputbox-cheil-small" placeholder="Enter the identifier" required="required" />
        <span>Enter Id</span>
        <form:errors path="identifier" class="text-danger"></form:errors>
      </div>
      <div class="col-sm-3">
        <form:input path="message" class="inputbox-cheil-long" placeholder="Enter Message" />
        <span>Enter Message</span>
        <form:errors path="message" class="text-danger"></form:errors>
      </div>
      </div>
      <br/><br/>
  </form:form>
</div>
</div>
</div>