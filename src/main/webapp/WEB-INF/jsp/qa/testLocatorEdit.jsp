<%@ include file="../include.jsp" %>
<div class="main-content">
  <div class="row">
    <div class="col-sm-12">
      <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
    </div>
  </div>
  <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/locator/add" modelAttribute="editForm">
    <div class="row">
      <div class="col-sm-12">
        <div class="dt-buttons">
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/locator')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="copyLocator('${editForm.recordId}');" type="button"  title="Copy the group">Copy</button>
        </div>
      </div>
    </div>
    <%@ include file="../commonFieldsLocator.jsp" %>
    <form:input path="recordId" class="inputbox-cheil-small" placeholder="Record Id" style="display:none" type="hidden"/>
    <div class="row">
      <div class="col-sm-3">
        <form:input path="identifier" class="inputbox-cheil-small" placeholder="Enter the unique identifier" required="required" />
        <span>Enter Id</span>
        <form:errors path="identifier" class="text-danger"></form:errors>
      </div>
      <div class="col-sm-3">
        <form:input path="description" class="inputbox-cheil-long" placeholder="Enter Description" />
        <span>Enter Description</span>
        <form:errors path="description" class="text-danger"></form:errors>
      </div>
      <div class="col-sm-3">
      <select name="methodName" class="3col active">
          <c:forEach items="${methods}" var="method">
              <option value="${method}" <c:if test="${method == editForm.methodName}">selected</c:if>>${method}</option>
          </c:forEach>
      </select>
        <span>Select method</span>
        <form:errors path="methodName" class="text-danger"></form:errors>
      </div>
        <div class="col-sm-3">
          <form:input path="errorMsg" class="inputbox-cheil-long" placeholder="Default Error Msg" />
          <span>Default Error Msg</span>
          <form:errors path="errorMsg" class="text-danger"></form:errors>
        </div>

      </div>
      <br/><br/>
      <div class="row">
            <div class="col-sm-4">
                  <select name="testDataType" class="cheil-select" id="selectpicker" onchange="updateTestDataSubtypeDropdown(this.value, '')">
                      <option value="">Please select datatype</option>
                      <c:forEach items="${testDataTypes}" var="testDataType">
                          <option value="${testDataType.id}" <c:if test="${testDataType.id == editForm.testDataType}">selected</c:if>>${testDataType.identifier}</option>
                      </c:forEach>
                  </select>
            </div>
            <div class="col-sm-4">
                <select name="testDataSubtype" id="selectpicker2" class="3col active cheil-select">
                </select>
            </div>
            <div class="col-sm-4">

                <select id="labelsData" class="cheil-select" name="labels" multiple>
                <c:forEach items="${editForm.labels}" var="labelData">
                  <option value="${labelData}" selected>${labelData}</option>
              </c:forEach>
                <input id="labels" class="inputbox-cheil-small" placeholder="Enter Labels"/>
                </select>
            <form:errors path="labels" class="text-danger"></form:errors>
          </div>
      </div>
      <br/><br/>
            <div class="row">
                  <div class="col-sm-4">
                        <select name="subLocators" class="cheil-select" id="selectpicker3" multiple>
                            <option value="">Please select subLocator</option>
                            <c:forEach items="${subLocators}" var="locator">
                                <option value="${locator.identifier}" <c:if test="${fn:contains(editForm.subLocators, locator.identifier)}">selected</c:if>>${locator.identifier}</option>
                            </c:forEach>
                        </select>
                  </div>
                   <div class="col-sm-8">
                      <textarea id="expression" class="inputbox-cheil-textarea-report" name="expression" rows="3" cols="80" placeholder="Please enter Expression">${editForm.expression}</textarea>
                      <span class="searchtext">Please enter the Expression's</span>
                      <form:errors path="expression" class="text-danger"></form:errors>
                   </div>
            </div>
      <br/><br/>
      <div class="row">
        <div class="col-sm-12">
        <p style="font-weight: bold;font-size:20px;">Configure Selectors</p>
            <div id="div1" style="height: 400px;">
                <div id="div2" style="height: inherit; overflow: auto; border:1px solid gray;">
                <c:forEach items="${editForm.uiLocatorSelector}" var="model">
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
        <div class="row">
        <div class="col-sm-12">
            <p style="font-weight: bold;font-size:20px;">Selector groups</p>
            <div id="div1" style="height: 400px;">
                <div id="div2" style="height: inherit; overflow: auto; border:1px solid gray;">
                <c:forEach items="${editForm.testLocatorGroups}" var="model" varStatus="loop">
                    <div class="row">
                    <input name="testLocatorGroups[${loop.index}].identifier" value="${model.identifier}" type="hidden"/>
                        <div class="col-sm-4" style="margin-top:15px;word-wrap: break-word;">
                            <b style="padding-left: 10px;">${model.identifier}</b>
                        </div>
                         <div class="col-sm-1">
                             <input type="text" class="inputbox-cheil-medium" name="testLocatorGroups[${loop.index}].priority" value="${model.priority}" title="Priority" placeholder="Priority"/>
                          </div>
                          <div class="col-sm-7">
                               <input type="text" class="inputbox-cheil-medium" name="testLocatorGroups[${loop.index}].errorMsg" value="${model.errorMsg}" title="Error Msg" placeholder="Error Msg"/>
                            </div>
                     </div><br/>
                </c:forEach>
                </div>
                </div>
            </div>
        </div>
      </div>
   </div>
  </form:form>
<script type="text/javascript">


var multipleCancelButton = new Choices('#selectpicker', {
          removeItemButton: true,
          maxItemCount:-1,
          searchResultLimit:20,
          renderChoiceLimit:-1,
          shouldSort: false
        });
        var labelsBtn = new Choices('#labelsData', {
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

var multipleCancelButton3 = new Choices('#selectpicker3', {
          removeItemButton: true,
          maxItemCount:-1,
          searchResultLimit:20,
          renderChoiceLimit:-1,
          shouldSort: false
        });

        $("#labels").on("blur", function(e) {
            var jsonData = [];
            var value = $(this).val();
            if(value && value!=''){
           jsonData.push({
                   "value" : $(this).val().replace("'", ''),
                   "label" : $(this).val().replace("'", ''),
                   "selected": true
               });
       labelsBtn.setChoices(jsonData,'value','label', false);
       }
       $(this).val("");

        });
    $(document).ready(function() {
            updateTestDataSubtypeDropdown('${editForm.testDataType}', '${editForm.testDataSubtype}');
        });
        function updateTestDataSubtypeDropdown(selectedTestDataType, selectedTestDataSubtype) {
            if(selectedTestDataType==='') { //if no test data type is selected, removing all subtypes from the dropdown
                multipleCancelButton2.setChoices([{
                            value: '',
                            label: 'Select Test Data Subtype',
                            selected: true,
                            disabled: false
                        }], 'value', 'label', true);
            }
            else{
                $.ajax({
                    type: 'GET',
                    url: "/admin/testdatasubtype/getTestDataSubtypes/" + selectedTestDataType,
                    datatype: "json",
                    success: function(data){

                        var isMatchFound = false;

                        // Convert fetched data into a format suitable for setChoices
                        var choices = Object.keys(data).map(function (key) {
                            var isSelected = String(selectedTestDataSubtype) === String(key);
                            if (isSelected) {
                                isMatchFound = true;
                            }
                            return {
                                value: key, // The ID of the TestDataSubtype
                                label: data[key], // The name of the TestDataSubtype
                                selected: isSelected,
                                disabled: false
                            };
                        });

                        // Create a 'prompt' choice as the first option
                        var promptChoice = {
                            value: '',
                            label: 'Select Test Data Subtype',
                            selected: !isMatchFound, // Only select prompt if no match is found
                            disabled: false
                        };

                        // Prepend the 'prompt' choice to the choices array
                        choices.unshift(promptChoice);

                        // Update the dropdown with the fetched choices
                        multipleCancelButton2.setChoices(choices, 'value', 'label', true);

                    },
                    error:function(e){
                        console.log(e.statusText);
                    }
                });

            }
            }
    function copyLocator(groupId){
                event.stopImmediatePropagation();
                event.preventDefault();
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