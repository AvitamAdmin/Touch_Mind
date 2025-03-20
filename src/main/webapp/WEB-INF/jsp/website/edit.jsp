<%@ include file="../include.jsp" %>
<div class="main-content">
  <div class="row">
    <div class="col-sm-12">
      <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
    </div>
  </div>
  <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/website/edit" modelAttribute="editForm">
    <div class="row">
      <div class="col-sm-12">
        <div class="dt-buttons">
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/website')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
        </div>
      </div>
    </div>
    <%@ include file="../commonFields.jsp" %>
    <form:input path="recordId" class="inputbox-cheil-small" placeholder="Record Id" style="display:none" type="hidden"/>
    <div class="row">
      <div class="col-sm-4">
        <form:input path="identifier" class="inputbox-cheil-small" placeholder="Enter the unique identifier" required="required" />
        <span>Enter Id</span>
        <form:errors path="identifier" class="text-danger"></form:errors>
      </div>
      <div class="col-sm-4">
        <input type="datetime-local" name="qaResultReportStartDate" class="inputbox-cheil" placeholder="Input Start date"  autocomplete="off"/>
        <span>QA Report Start Date</span>
        </select>
     </div>
      <div class="col-sm-4">
        <input type="datetime-local" name="qaResultReportEndDate" class="inputbox-cheil" placeholder="Input End date"  autocomplete="off"/>
        <span>QA Report End Date</span>
     </div>
    </div>
   <br/><br/>
   <div class="row">
         <div class="col-sm-4">
           <form:input path="reportPassDeleteDays" class="inputbox-cheil-small" placeholder="Report pass delete days"/>
           <span>Report Pass delete days</span>
           <form:errors path="identifier" class="text-danger"></form:errors>
         </div>
         <div class="col-sm-4">
           <form:input path="reportFailedDeleteDays" class="inputbox-cheil" placeholder="Report failed delete days"/>
           <span>Report Failed delete days</span>
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