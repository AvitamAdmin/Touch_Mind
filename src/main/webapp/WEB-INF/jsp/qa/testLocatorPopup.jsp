<%@ include file="../include.jsp" %>
<div class="main-content">
  <form:form method="POST" id="locatorForm" enctype="multipart/form-data" action="/admin/locatorGroup/updateLocator" modelAttribute="locatorForm">
    <div class="row">
      <div class="col-sm-12">
        <div class="dt-buttons">
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/locatorGroup/edit?id=${groupId}')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('locatorForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
        </div>
      </div>
    </div>
    <div style="font-weight:bolder;font-size:16px;">${locatorForm.identifier}</div>
    </br>
    <input name="groupId" class="inputbox-cheil-small" value="${groupId}" placeholder="Group Id" style="display:none" type="hidden"/>
    <form:input path="recordId" class="inputbox-cheil-small" placeholder="Record Id" style="display:none" type="hidden"/>
      <div class="row">
      <div class="col-sm-12">
            <div id="div1" style="height: 400px;">
                <div id="div2" style="height: inherit; overflow: auto; border:1px solid gray;">
                <c:forEach items="${locatorForm.uiLocatorSelector}" var="model">
                    <div class="row">
                    <div class="col-sm-2" style="margin-top:15px;"><b style="padding-left: 10px;">${model.key}</b></div>

                    <div class="col-sm-2">
                    <input type="text" class="inputbox-cheil-medium" name="uiLocatorSelector[${model.key}].xpathSelector" value="${model.value.xpathSelector}" title="UI selector should be By xpath" placeholder="Enter UI element xpath selector "/>&nbsp;&nbsp;&nbsp;
                    </div>
                    <div class="col-sm-2">
                    <input type="text" class="inputbox-cheil-medium" name="uiLocatorSelector[${model.key}].cssSelector" value="${model.value.cssSelector}" title="UI selector should be By css" placeholder="Enter UI element css selector "/>&nbsp;&nbsp;&nbsp;
                    </div>
                    <div class="col-sm-2">
                    <input type="text" class="inputbox-cheil-medium" name="uiLocatorSelector[${model.key}].idSelector" value="${model.value.idSelector}" title="UI selector should be By id" placeholder="Enter UI element id selector "/>&nbsp;&nbsp;&nbsp;
                    </div>
                    <div class="col-sm-2">
                    <input type="text" class="inputbox-cheil-medium" name="uiLocatorSelector[${model.key}].othersSelector" value="${model.value.othersSelector}" title="UI selector should be By others" placeholder="Enter UI element others selector "/>&nbsp;&nbsp;&nbsp;
                    </div>
                    <div class="col-sm-2">
                    <input type="text" class="inputbox-cheil-medium" name="uiLocatorSelector[${model.key}].inputData" value="${model.value.inputData}" title="Enter input data" placeholder="Enter input data"/>&nbsp;&nbsp;&nbsp;
                    </div>
                    </div>
                    <br/>
                </c:forEach>
                </div>
            </div>
        </div>
        </div>
        <br/><br/>
   </div>
  </form:form>
<script type="text/javascript">
    function copyLocator(groupId){
                $.ajax({
                  type: 'GET',
                  url: "/admin/locator/copyForm/" + groupId,
                  datatype: "json",
                  success: function(data) {
                    fire_ajax_submit('/admin/locator');
                  },
                  error:function(e){
                      console.log(e.statusText);
                  }
              });
            }
</script>

  <c:if test="${not empty message}">
    <div class="alert alert-danger" role="alert" id="errorMessage">
      <spring:message code="${message}" />
    </div>
  </c:if>
</div>
</div>
</div>