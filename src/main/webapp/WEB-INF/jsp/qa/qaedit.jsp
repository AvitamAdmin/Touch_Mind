<%@ include file="../include.jsp" %>
<div class="main-content">
  <div class="row">
    <div class="col-sm-12">
      <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
    </div>
  </div>
  <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/qa/results/edit" modelAttribute="editForm">
    <div class="row">
      <div class="col-sm-12">
        <div class="dt-buttons">
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/qa/results')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
        </div>
      </div>
    </div>

    </br>
    </br>
    </br>
    <form:input path="id" class="inputbox-cheil-small" placeholder="PK" readonly="true" type="hidden"/>
    <form:input path="recordId" class="inputbox-cheil-small" placeholder="Record Id" style="display:none" type="hidden"/>
    <div class="row">
      <div class="col-sm-6">
         <select name="errorType"  class="3col active cheil-select" id="selectpicker2">
            <c:forEach items="${errorTypes}" var="child">
              <c:choose>
                  <c:when test="${editForm.errorType == child.identifier}">
                    <option value="${child.recordId}" selected>${child.identifier}</option>
                  </c:when>
                  <c:otherwise>
                     <option value="${child.recordId}" >${child.identifier}</option>
                  </c:otherwise>
              </c:choose>
          </c:forEach>
        </select>
      </div>
      <div class="col-sm-6">
        <form:input path="errorMessage" class="inputbox-cheil-long" placeholder="Error Message" />
        <span>Error Message</span>
        <form:errors path="errorMessage" class="text-danger"></form:errors>
      </div>
    </div>
    </form:form>
</div>
</div>
</div>
<script type="text/javascript">

  $(document).ready(function() {
          var sitesData = new Choices('#selectpicker2', {
                       removeItemButton: true,
                       maxItemCount:-1,
                       searchResultLimit:20,
                       renderChoiceLimit:-1,
                       placeholder: true,
                       placeholderValue: "Select Error Type"
                    });
   });

</script>