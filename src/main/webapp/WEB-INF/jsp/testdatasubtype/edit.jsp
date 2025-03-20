<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/testdatasubtype/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/testdatasubtype')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <div class="row">
            <div class="col-sm-3">
                <form:input path="identifier" class="inputbox-cheil-small" placeholder="Enter Id" required="required" />
                <span>Enter Id</span>
                <form:errors path="identifier" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="shortDescription" class="inputbox-cheil-long" placeholder="Enter Description" />
                <span>Enter Description</span>
                <form:errors path="shortDescription" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                 <select name="testDataType"  id="selectpicker2" class="3col active cheil-select">
                    <option value="">Select Test Data Type</option>
                    <c:forEach items="${testDataTypes}" var="child">
                        <c:choose>
                            <c:when test="${editForm.testDataType == child.id}">
                              <option value="${child.id}" selected>${child.identifier}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${child.id}" >${child.identifier}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>
             <div class="col-sm-3">
             <select class="cheil-select" name="subsidiaries[]" placeholder="Select subsidiaries" multiple id="selectpicker" required="required">
                 <span>Select subsidiaries</span>
                 <c:forEach items="${subsidiaries}" var="child">
                     <c:choose>
                         <c:when test="${fn:contains( editForm.subsidiaries, child.identifier ) }">
                           <option value="${child.identifier}" selected>${child.identifier}</option>
                         </c:when>
                         <c:otherwise>
                            <option value="${child.identifier}" >${child.identifier}</option>
                         </c:otherwise>
                     </c:choose>
                 </c:forEach>
             </select>
        </div>
        </div>
        <br/><br/>
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
          shouldSort: false
        });
var multipleCancelButton2 = new Choices('#selectpicker2', {
          removeItemButton: true,
          maxItemCount:-1,
          searchResultLimit:20,
          renderChoiceLimit:-1,
          shouldSort: false
        });
    $(document).ready(function() {

      $("body").on("click",".remove",function(){
          $(this).parents(".control-group").remove();
      });

    });

  function removeRow(countVal) {
    $('#inputParamRow'+countVal).remove();
  }

</script>