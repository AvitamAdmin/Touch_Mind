<%@ include file="../include.jsp" %>
<div class="main-content">
  <div class="row">
    <div class="col-sm-12">
      <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
    </div>
  </div>
  <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/qa/results/repair" modelAttribute="editForm">
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
         <select name="resultStatus"  class="3col active cheil-select" id="selectpicker2">
            <option value="1" <c:if test="${editForm.resultStatus==1}">selected</c:if>>Passed</option>
            <option value="2" <c:if test="${editForm.resultStatus==2}">selected</c:if>>Failed</option>
            <option value="3" <c:if test="${editForm.resultStatus==3}">selected</c:if>>Partial Passed</option>
        </select>
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
                       renderChoiceLimit:-1
                    });
   });

</script>