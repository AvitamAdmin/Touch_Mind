<%@ include file="../include.jsp" %>
<div class="main-content">
  <div class="row">
    <div class="col-sm-12">
      <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
    </div>
  </div>
  <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/qaErrorMapper/edit" modelAttribute="editForm">
    <div class="row">
      <div class="col-sm-12">
        <div class="dt-buttons">
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/qaErrorMapper')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
        </div>
      </div>
    </div>
    <%@ include file="../commonFields.jsp" %>
    <form:input path="recordId" class="inputbox-cheil-small" placeholder="Record Id" style="display:none" type="hidden"/>
    <div class="row">
      <div class="col-sm-4">
        <form:input path="identifier" class="inputbox-cheil-small" placeholder="Enter the identifier" required="required" />
        <span>Enter Id</span>
        <form:errors path="identifier" class="text-danger"></form:errors>
      </div>
      <div class="col-sm-4">
        <form:input path="locatorRegEx" class="inputbox-cheil-long" placeholder="Enter locator Regex" />
        <span>Enter locator Regex</span>
        <form:errors path="locatorRegEx" class="text-danger"></form:errors>
      </div>
      <div class="col-sm-4">
          <form:input path="descriptionRegEx" class="inputbox-cheil-long" placeholder="Short Desc Regex" />
          <span>Short Desc Regex</span>
          <form:errors path="descriptionRegEx" class="text-danger"></form:errors>
      </div>
    </div>
      <br/><br/>
      <div class="row">
        <div class="col-sm-4">
          <form:input path="messageRegEx" class="inputbox-cheil-long" placeholder="Enter Message Regex" />
          <span>Enter Message Regex</span>
          <form:errors path="messageRegEx" class="text-danger"></form:errors>
      </div>
        <div class="col-sm-4">
           <select name="errorType"  class="3col active cheil-select" id="selectpicker2">
              <c:forEach items="${errorTypes}" var="child">
                <c:choose>
                    <c:when test="${editForm.errorType == child.recordId}">
                      <option value="${child.recordId}" selected>${child.identifier}</option>
                    </c:when>
                    <c:otherwise>
                       <option value="${child.recordId}" >${child.identifier}</option>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
          </select>
        </div>
        <div class="col-sm-4">
          <form:input path="errorMessage" class="inputbox-cheil-long" placeholder="Enter Error Message" />
          <span>Enter Error Message</span>
          <form:errors path="errorMessage" class="text-danger"></form:errors>
        </div>
      </div>
  </form:form>
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
        var multipleCancelButton2 = new Choices('#selectpicker2', {
                removeItemButton: true,
                maxItemCount:-1,
                searchResultLimit:20,
                renderChoiceLimit:-1
              });
    });
</script>