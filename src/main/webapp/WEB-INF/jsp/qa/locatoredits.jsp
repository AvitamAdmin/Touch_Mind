<%@ include file="../include.jsp" %>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/locator/edits" class="handle-upload" modelAttribute="editForm" >
            <div class="row">
                <div class="col-sm-12">
                    <div class="dt-buttons">
                        <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/locator')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                        <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
                    </div>
                </div>
            </div>
            </br>
            </br>
            <c:forEach items="${dataToEdit}" var="childData" varStatus="loop">
            <form:input path="locatorFormList[${loop.index}].recordId" value="${childData.recordId}" class="inputbox-cheil-small" placeholder="PK" readonly="true" type="hidden"/>
            <div class="row">
                <div class="col-sm-3">
                    <form:input path="locatorFormList[${loop.index}].identifier" value="${childData.identifier}" class="inputbox-cheil-small" placeholder="Id/Name" required="required" />
                    <form:errors path="identifier" class="text-danger"></form:errors>
                </div>
                <div class="col-sm-3">
                    <form:input path="locatorFormList[${loop.index}].description" value="${childData.description}" class="inputbox-cheil-long" placeholder="Short description (128 letters)" />
                    <form:errors path="description" class="text-danger"></form:errors>
                </div>
                <div class="col-sm-3">
                        <select name="locatorFormList[${loop.index}].strategy" class="3col active">
                            <c:forEach items="${selectorStrategies}" var="strategyItem"> <!-- list of strategies is sent from the controller -->
                                <option value="${strategyItem}" <c:if test="${strategyItem == childData.strategy}">selected</c:if>>${strategyItem}</option>
                            </c:forEach>
                        </select>
                  </div>
                <div class="col-sm-3">
                  <select name="locatorFormList[${loop.index}].methodName" class="3col active">
                      <c:forEach items="${methods}" var="method">
                          <option value="${method}" <c:if test="${method == childData.methodName}">selected</c:if>>${method}</option>
                      </c:forEach>
                  </select>
                    <span>Select method</span>
                    <form:errors path="methodName" class="text-danger"></form:errors>
                  </div>
            </div>
            </c:forEach>
           </form:form>


<script type="text/javascript">
         var multipleCancelButton = new Choices('#selectpicker', {
             removeItemButton: true,
             maxItemCount:-1,
             searchResultLimit:20,
             renderChoiceLimit:-1,
             placeholder: true,
             placeholderValue: "Select Value"
           });
</script>