<%@ include file="../include.jsp" %>
<div class="main-content">
  <div class="row">
    <div class="col-sm-12">
      <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
    </div>
  </div>
  <c:if test="${not empty message}">
      <div class="alert alert-danger" role="alert" id="errorMessage">
        <spring:message code="${message}" />
      </div>
    </c:if>
  <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/locatorGroup/add" modelAttribute="editForm">
    <div class="row">
      <div class="col-sm-12">
        <div class="dt-buttons">
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/locatorGroup')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="copyGroup('${editForm.recordId}');" type="button"  title="Copy the group">Copy</button>
        </div>
      </div>
    </div>
    <%@ include file="../commonFields.jsp" %>
    <form:input path="recordId" class="inputbox-cheil-small" placeholder="Record Id" style="display:none" type="hidden"/>
    <div class="row">
      <div class="col-sm-3">
        <form:input path="identifier" class="inputbox-cheil-small" placeholder="Enter Identifier" required="required" />
        <span>Enter Id</span>
        <form:errors path="identifier" class="text-danger"></form:errors>
      </div>
      <div class="col-sm-3">
        <form:input path="shortDescription" class="inputbox-cheil-long" placeholder="Enter Description" />
        <span>Enter Description</span>
        <form:errors path="shortDescription" class="text-danger"></form:errors>
      </div>
      <div class="col-sm-3">
           <select name="subsidiary" id="subsidiary" class="3col active cheil-select">
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
      <div class="col-sm-3" id="publish">
          <c:choose>
              <c:when test="${editForm.published}">
                   <c:set var="published" value="checked"></c:set>
               </c:when>
               <c:otherwise>
                   <c:set var="unpublished" value="checked"></c:set>
               </c:otherwise>
          </c:choose>
          <input type="radio" name="published"  value="true" ${published}> Published
          <input type="radio" name="published" value="false" ${unpublished}> Unpublished
          <br/>
          <c:choose>
                <c:when test="${editForm.takeAScreenshot}">
                     <c:set var="takeAScreenshot" value="checked"></c:set>
                 </c:when>
                 <c:otherwise>
                     <c:set var="noScreenshot" value="checked"></c:set>
                 </c:otherwise>
            </c:choose>
            <input type="radio" name="takeAScreenshot"  value="true" ${takeAScreenshot}> Screenshot
            <input type="radio" name="takeAScreenshot"  value="false" ${noScreenshot}> No screenshot
      </div>
    </div>
    <br/>
    <c:forEach items="${editForm.conditionGroupList}" var="condition" varStatus="loop">
    <div class="row" id="toolkit${loop.index}">
            <div class="col-sm-3">
             <select class="cheil-select toolkitNode" name="conditionGroupList[${loop.index}].toolkitId" id="toolkitNode${loop.index}" onchange="updateParams(this.value, '${loop.index}')">
                <option value="">Select toolkits</option>
                <c:forEach items="${nodes}" var="child">
                    <c:choose>
                        <c:when test="${condition.toolkitId eq child.id}">
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
                <select class="cheil-select" name="conditionGroupList[${loop.index}].paramName" placeholder="Select parameter" id="selectpicker${loop.index}">
                    <c:choose>
                        <c:when test="${not empty condition.paramName}">
                          <option value="${condition.paramName}" selected>${condition.paramName}</option>
                        </c:when>
                        <c:otherwise>
                           <option value="${condition.paramName}" selected>Select parameter</option>
                        </c:otherwise>
                    </c:choose>
                </select>
            </div>
            <div class="col-sm-2">
                <select name="conditionGroupList[${loop.index}].condition"  class="3col active cheil-select">
                  <option value="">Select condition</option>
                  <c:forEach items="${conditionOperators}" var="child">
                      <c:choose>
                          <c:when test="${fn:contains( condition.condition, child ) }">
                            <option value="${child}" selected>${child}</option>
                          </c:when>
                          <c:otherwise>
                             <option value="${child}" >${child}</option>
                          </c:otherwise>
                      </c:choose>
                  </c:forEach>
              </select>
            </div>
            <div class="col-sm-3">
                <input name="conditionGroupList[${loop.index}].paramValue" value="${condition.paramValue}" class="inputbox-cheil-small" placeholder="Param value" />
            </div>
            <div class="col-sm-1">
                <c:choose>
                 <c:when test="${condition.isOrChain}">
                     <c:set var="conditionVisible" value="checked"></c:set>
                 </c:when>
                 <c:otherwise>
                     <c:set var="conditionVisible" value=""></c:set>
                 </c:otherwise>
                 </c:choose>
                <input style="width: 20px;transform : scale(1.5);" type="checkbox" name="conditionGroupList[${loop.index}].isOrChain" title="select checkbox for AND condition" ${conditionVisible} />
                <img style="width:32px;height:32px;margin-left:25px;margin-bottom:10px;" onclick="javascript:removeToolkitRow('${loop.index}')" src="${contextPath}/images/remove.png" />
            </div>
        </div>
        </c:forEach>
        <div class="input-group control-group after-add-conditions-more"></div>
       <div class="row">
            <div class="col-sm-2">
                <input id="existingConditionCount" name="existingConditionCount" value="${existingConditionCount}" style="display:none;">
                <button class="btn btn-primary add-conditions-more" type="button"><i class="glyphicon glyphicon-add"></i>Add condition</button>
            </div>
            <div class="col-sm-10">
                <label for="enterKey"><strong>Epp Sso Check</strong></label>
                <c:choose>
                     <c:when test="${editForm.checkEppSso}">
                         <c:set var="varEppSso" value="checked"></c:set>
                     </c:when>
                     <c:otherwise>
                         <c:set var="varEppSso" value=""></c:set>
                     </c:otherwise>
                 </c:choose>
                <input type="checkbox" style="width: 20px;transform : scale(1.5);" id="testLocators[${loop.index}].enterKey" title="Validate Epp Sso ?" name="checkEppSso" ${varEppSso} />
            </div>
        </div>
        <br/><br/>
    <c:forEach items="${editForm.testLocators}" var="savedChild" varStatus="loop">
                <c:if test="${child!=''}">
                  <div class="row"  id="locator${loop.index}">
                    <div class="col-sm-4">
                        <select name="testLocators[${loop.index}].locatorId" class="3col active cheil-select" id="selectorlocator-${loop.index}">
                            <option value="">Please select the Selector</option>
                            <c:forEach items="${locators}" var="child">
                                <c:choose>
                                    <c:when test="${fn:contains( savedChild.locatorId, child.id ) }">
                                      <option value="${child.id}" selected>${child.identifier}</option>
                                    </c:when>
                                    <c:otherwise>
                                       <option value="${child.id}" >${child.identifier}</option>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-sm-1">
                    <input name="testLocators[${loop.index}].priority" value="${savedChild.priority}" class="inputbox-cheil-small" placeholder="Enter Name" required="required" />
                    </div>
                    <div class="col-sm-3">
                    <input name="testLocators[${loop.index}].errorMsg" value="${savedChild.errorMsg}" class="inputbox-cheil-small" placeholder="Error Message" />
                    </div>
                    <div class="col-sm-4" style="margin-top:10px;">
                    <label for="enterKey"><strong>E</strong></label>
                    <c:choose>
                         <c:when test="${savedChild.enterKey}">
                             <c:set var="varChecked" value="checked"></c:set>
                         </c:when>
                         <c:otherwise>
                             <c:set var="varChecked" value=""></c:set>
                         </c:otherwise>
                     </c:choose>
                    <input type="checkbox" style="width: 20px;transform : scale(1.5);" id="testLocators[${loop.index}].enterKey" title="Enable enter key press ? " name="testLocators[${loop.index}].enterKey" ${varChecked} />
                    <label for="waitForElementVisibleAndClickable" title="Enable enter key press ? ">&nbsp;<strong>W</strong></label>
                    <c:choose>
                         <c:when test="${savedChild.waitForElementVisibleAndClickable}">
                             <c:set var="varVisible" value="checked"></c:set>
                         </c:when>
                         <c:otherwise>
                             <c:set var="varVisible" value=""></c:set>
                         </c:otherwise>
                     </c:choose>
                    <input style="width: 20px;transform : scale(1.5);" type="checkbox" id="testLocators[${loop.index}].waitForElementVisibleAndClickable" title="Wait for element to load ?" name="testLocators[${loop.index}].waitForElementVisibleAndClickable" ${varVisible} />
                    <label for="testLocators[${loop.index}].checkIfElementPresentOnThePage" title="Wait for element to load ?">&nbsp;<strong>P</strong></label>
                        <c:choose>
                             <c:when test="${savedChild.checkIfElementPresentOnThePage}">
                                 <c:set var="varElementPresent" value="checked"></c:set>
                             </c:when>
                             <c:otherwise>
                                 <c:set var="varElementPresent" value=""></c:set>
                             </c:otherwise>
                         </c:choose>
                        <input style="width: 20px;transform : scale(1.5);" type="checkbox" id="testLocators[${loop.index}].checkIfElementPresentOnThePage" title="Check if element present on the page ?" name="testLocators[${loop.index}].checkIfElementPresentOnThePage" ${varElementPresent} />

                   <label for="testLocators[${loop.index}].checkIfIframe" title="Is it an Iframe element ?">&nbsp;<strong>I</strong></label>
                   <c:choose>
                        <c:when test="${savedChild.checkIfIframe}">
                            <c:set var="varIframe" value="checked"></c:set>
                        </c:when>
                        <c:otherwise>
                            <c:set var="varIframe" value=""></c:set>
                        </c:otherwise>
                    </c:choose>
                   <input style="width: 20px;transform : scale(1.5);" type="checkbox" id="testLocators[${loop.index}].checkIfIframe" title="Read Data from context for Cascade test ?" name="testLocators[${loop.index}].checkIfIframe" ${varIframe} />

                    <label for="testLocators[${loop.index}].isContextData" title="read data from context ?">&nbsp;<strong>R</strong></label>
                       <c:choose>
                            <c:when test="${savedChild.isContextData}">
                                <c:set var="varIframe" value="checked"></c:set>
                            </c:when>
                            <c:otherwise>
                                <c:set var="varIframe" value=""></c:set>
                            </c:otherwise>
                        </c:choose>
                       <input style="width: 20px;transform : scale(1.5);" type="checkbox" id="testLocators[${loop.index}].isContextData" title="Read Data from context for Cascade test ?" name="testLocators[${loop.index}].isContextData" ${varIframe} />
                       <img style="width:32px;height:32px;margin-left:25px;margin-bottom:10px;" onclick="javascript:removeRow('${loop.index}')" src="${contextPath}/images/remove.png" />
                       <img style="width:32px;height:32px;margin-left:25px;margin-bottom:10px;" onclick="javascript:editRow('${savedChild.locatorId}','${editForm.recordId}')" src="${contextPath}/images/edit.png" /></div>
                </div>
                <br/>
                </c:if>
            </c:forEach>
            <input id="existingParamsCount" name="existingParamsCount" value="${existingParamsCount}" style="display:none;">
            <input id="publishedVal" name="publishedVal" value="${editForm.published}" style="display:none;">
            <div class="input-group control-group after-add-input-more">
            <button class="btn btn-primary add-input-more" type="button"><i class="glyphicon glyphicon-add"></i>Add an input field</button>
        </div>
  </form:form>
</div>
</div>
</div>
<div id="myModalEdit" class="modal fade" tabindex="-1">
      <div class="modal-dialog-full" style="margin-left:200px;margin-top:100px;">
         <div class="modal-content" style="height:65%;margin-left:20px !important">
            <div class="modal-header">
                Edit
               <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body" id="editModelContent">

            </div>
         </div>
      </div>
</div>
<div id="myModalEdit" class="modal fade" tabindex="-1">
      <div class="modal-dialog-full" style="margin-left:200px;margin-top:100px;">
         <div class="modal-content" style="height:80%;margin-left:20px !important">
            <div class="modal-header">
                Edit
               <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body" id="editModelContent">

            </div>
         </div>
      </div>
</div>
<script type="text/javascript">
    $(document).ready(function() {
      var count = $('#existingParamsCount').val();
      var publishedVal = $('#publishedVal').val();
      if(publishedVal=='true'){
          $("#editForm :input").prop("disabled", true);
          $("#publish :input").prop("disabled", false);
      }
      $('#publish').change(function() {
      var value = $('input[name="published"]:checked').val();
      if(value=='false'){
        $(".btn-icon-small").removeAttr("disabled");
      }
      });


        $(".add-input-more").click(function(){
        var html = '<div class="row" id="locator' + count + '"><div class="col-sm-4"><select name="testLocators[' + count + '].locatorId" class="3col active cheil-select" id="selectorlocator-'+count+'"><option value="">Please select the Locator</option><c:forEach items="${locators}" var="child"><option value="${child.id}" >${child.identifier}</option></c:forEach></select></div><div class="col-sm-1"><input name="testLocators[' + count + '].priority" class="inputbox-cheil-long" value="' + count + '" placeholder="Enter the priority" /></div><div class="col-sm-3"><input name="testLocators[' + count + '].errorMsg" class="inputbox-cheil-long" value="' + count + '" placeholder="Error Message" /></div><div class="col-sm-4" style="margin-top:10px;"><label style="margin-right:4px;" for="testLocators[' + count + '].enterKey"><strong>E</strong></label><input style="width: 20px;transform : scale(1.5);" type="checkbox" title="Enable enter key press ? " id="testLocators[' + count + '].enterKey" name="testLocators[' + count + '].enterKey" />&nbsp;<label style="margin-right:4px;" for="testLocators[' + count + '].waitForElementVisibleAndClickable">&nbsp;<strong>W</strong></label><input style="width: 20px;transform : scale(1.5);" type="checkbox" id="testLocators[' + count + '].waitForElementVisibleAndClickable" title="Wait for element to load ?" name="testLocators[' + count + '].waitForElementVisibleAndClickable"/>&nbsp;<label style="margin-right:4px;" for="testLocators[' + count + '].checkIfElementPresentOnThePage">&nbsp;<strong>P</strong></label><input type="checkbox" style="width: 20px;transform : scale(1.5);" id="testLocators[' + count + '].checkIfElementPresentOnThePage" title="Check if element present on the page ?" name="testLocators[' + count + '].checkIfElementPresentOnThePage" ${varElementPresent} /><label style="margin-right:4px;" for="testLocators[' + count + '].checkIfIframe">&nbsp;<strong>I</strong></label><input type="checkbox" style="width: 20px;transform : scale(1.5);" id="testLocators[' + count + '].checkIfIframe" title="Is it an Iframe element ?" name="testLocators[' + count + '].checkIfIframe" ${varIframe} />&nbsp;<label style="margin-right:4px;" for="testLocators[' + count + '].isContextData">&nbsp;<strong>R</strong></label><input style="width: 20px;transform : scale(1.5);" type="checkbox" id="testLocators[' + count + '].isContextData" title="Save to context ?" name="testLocators[' + count + '].isContextData"/>&nbsp;<img style="width:32px;height:32px;margin-left:25px;margin-bottom:10px;" onclick="javascript:removeRow(' + count + ')" src="${contextPath}/images/remove.png" /></div></div></br>';
        $(".after-add-input-more").before(html);
        var sitesData3 = new Choices('#selectorlocator-'+count, {
                                 removeItemButton: true,
                                 maxItemCount:-1,
                                 searchResultLimit:20,
                                 renderChoiceLimit:-1
                  });
        count++;
    });
    var subsidiaryDataDynamic = new Choices('#subsidiary', {
                 removeItemButton: true,
                 maxItemCount:-1,
                 searchResultLimit:20,
                 renderChoiceLimit:-1
            });

    for(i=0;i<count;i++){
        var sitesDataDynamic = new Choices('#selectorlocator-'+i, {
             removeItemButton: true,
             maxItemCount:-1,
             searchResultLimit:20,
             renderChoiceLimit:-1
        });
    }
    $(document).on("click","#locator",function(){
              $(this).remove();
          });
    });
      function removeRow(countVal) {
        $('#locator'+countVal).remove();
      }
      function removeToolkitRow(countVal) {
        $('#toolkit'+countVal).remove();
      }
         var counter = $('#existingConditionCount').val();
          $(".add-conditions-more").click(function(){
          var html = '<br/><div id="toolkit'+counter+'" class="row"><div class="col-sm-3"><select class="cheil-select toolkitNode" name="conditionGroupList['+counter+'].toolkitId" id="toolkitNode-' + counter + '" onchange="updateParams(this.value, '+counter+')"><option value="">Select toolkits</option><c:forEach items="${nodes}" var="child"><option value="${child.id}" >${child.identifier}</option></c:forEach></select></div><div class="col-sm-3"><select class="cheil-select" name="conditionGroupList['+counter+'].paramName" placeholder="Select parameter" id="selectpicker' + counter + '"><option selected>Select parameter</option></select></div><div class="col-sm-2"><select name="conditionGroupList['+counter+'].condition"  class="3col active cheil-select"><option value="">Select condition</option><c:forEach items="${conditionOperators}" var="child"><option value="${child}" >${child}</option></c:forEach></select></div><div class="col-sm-3"><input name="conditionGroupList['+counter+'].paramValue" class="inputbox-cheil-small" placeholder="Param value" /></div><div class="col-sm-1"><input type="checkbox" style="width: 20px;transform : scale(1.5);" title="select checkbox for And condition ? " name="conditionGroupList['+counter+'].isOrChain" /><img style="width:32px;height:32px;margin-left:25px;margin-bottom:10px;" onclick="javascript:removeToolkitRow('+counter+')" src="${contextPath}/images/remove.png" /></div></div>';
          $(".after-add-conditions-more").before(html);
          var sitesData4 = new Choices('#toolkitNode-'+counter, {
                                   removeItemButton: true,
                                   maxItemCount:-1,
                                   searchResultLimit:20,
                                   renderChoiceLimit:-1
                    });
          counter++;
          });
        function updateParams(nodeId,currentIndex) {
          $("#selectpicker"+currentIndex).empty();
          var sitesData = document.getElementById('selectpicker'+currentIndex);
          $.ajax({
              type: 'GET',
              url: "/admin/locatorGroup/getMappingParamsForNodeId/" + nodeId,
              datatype: "json",
              success: function(data) {

              sitesData.options[0] = new Option("Select toolkit parameter", "");
              sitesData.options[0].selected = true;
                for(var i=0; i<data.length; i++){
                    sitesData.options[i+1] = new Option(data[i], data[i]);
                }
              },
              error:function(e){
                  console.log(e.statusText);
              }
          });
        }
        function copyGroup(groupId){
            $.ajax({
              type: 'GET',
              url: "/admin/locatorGroup/copyForm/" + groupId,
              datatype: "json",
              success: function(data) {
                fire_ajax_submit('/admin/locatorGroup');
              },
              error:function(e){
                  console.log(e.statusText);
              }
          });
        }
        function editRow(id,groupId) {
            $.ajax({
                  type: 'GET',
                  url: "/admin/locatorGroup/editLocator/" + id+"?groupId="+groupId,
                  datatype: "json",
                  success: function(data) {
                    $("#editModelContent").html(data);
                    $("#myModalEdit").modal('show');
                  },
                  error:function(e){
                      console.log(e.statusText);
                  }
            });
        }
</script>