<%@ include file="../include.jsp" %>
<div class="main-content">
      <div class="row">
        <div class="col-sm-12">
          <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
      </div>
      <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/profile/add" modelAttribute="editForm">
        <div class="row">
          <div class="col-sm-12">
            <div class="dt-buttons">
              <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/profile')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
              <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
              <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="copyProfile('${editForm.recordId}');" type="button"  title="Copy the group">Copy</button>
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
            <form:input path="shortDescription" class="inputbox-cheil-long" placeholder="Enter Description" />
            <span>Enter Description</span>
            <form:errors path="shortDescription" class="text-danger"></form:errors>
          </div>
          <div class="col-sm-4">
          <select name="subsidiary"  class="3col active cheil-select">
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
         <br/>
         <br/>
         <c:forEach items="${editForm.profileLocators}" var="profileLocator" varStatus="loop">
             <div class="row" id="locator${loop.index}">
                  <input type="hidden" name="profileLocators[${loop.index}].recordId" value="${profileLocator.recordId}"/>
                  <div class="col-sm-3">
                     <input name="profileLocators[${loop.index}].locatorId" value="${profileLocator.locatorId}" class="inputbox-cheil-small" placeholder="${profileLocator.locatorId}" readonly="readonly" />
                  </div>
                  <div class="col-sm-3">
                    <input name="profileLocators[${loop.index}].description" value="${profileLocator.description}" class="inputbox-cheil-small" placeholder="Short Description" readonly="readonly"/>
                  </div>
                  <div class="col-sm-3">
                      <input name="profileLocators[${loop.index}].testDataType" value="${profileLocator.testDataType}" class="inputbox-cheil-small" placeholder="Data Type" readonly="readonly"/>
                    </div>
                  <div class="col-sm-3">
                      <input name="profileLocators[${loop.index}].inputValue" value="${profileLocator.inputValue}" class="inputbox-cheil-small" placeholder="Input value"/>
                    </div>
             </div>
             </br>
         </c:forEach>
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
<script>
function copyProfile(profileId){
            $.ajax({
              type: 'GET',
              url: "/admin/profile/copyProfile/" + profileId,
              datatype: "json",
              success: function(data) {
                fire_ajax_submit('/admin/profile');
              },
              error:function(e){
                  console.log(e.statusText);
              }
          });
        }
</script>