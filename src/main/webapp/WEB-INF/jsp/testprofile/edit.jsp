<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/testprofile/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/testprofile')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
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
                 <select name="testPlan" class="3col active cheil-select" id="testPlan">
                    <option value="">Select a test plan</option>
                    <c:forEach items="${testPlans}" var="child">
                        <c:choose>
                            <c:when test="${editForm.testPlan == child.id}">
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
        <div class="row">
            <div class="col-sm-6">
                 <form:textarea path="skus" id="skus" rows="6" cols="30" class="inputbox-cheil-textarea" placeholder="please enter SKU's"/>
                 <span class="searchtext">Please enter SKU's</span>
            </div>
            <div class="col-sm-3">
                <c:choose>
                     <c:when test="${editForm.enableSite}">
                         <c:set var="varChecked" value="'checked'"></c:set>
                     </c:when>
                     <c:otherwise>
                         <c:set var="varChecked" value=""></c:set>
                     </c:otherwise>
                 </c:choose>
                 <form:label path="enableSite"><strong>Enable Site?</strong></form:label>
                 <form:checkbox path="enableSite" checked="${varChecked}" />
            </div>
            <div class="col-sm-3">
                <c:choose>
                     <c:when test="${editForm.isDefault}">
                         <c:set var="varChecked" value="'checked'"></c:set>
                     </c:when>
                     <c:otherwise>
                         <c:set var="varChecked" value=""></c:set>
                     </c:otherwise>
                 </c:choose>
                 <form:label path="isDefault"><strong>Default?</strong></form:label>
                 <form:checkbox path="isDefault" checked="${varChecked}" />
            </div>
        </div>
        <br/><br/>
        <c:forEach items="${editForm.paramInput}" var="child" varStatus="loop">
                <c:if test="${child!=''}">
                <div class="row"  id="inputParamRow${loop.index}">
                                <div class="col-sm-3">
                                    <select name="paramInput[${loop.index}].testDataType" id="selectpicker2_${loop.index}" onchange="javascript:updateTestDataSubtypeDropdown(this.value, ${loop.index})" class="3col active cheil-select" required="required">
                                        <option value="">Select Test Data Type</option>
                                        <c:forEach items="${testDataTypes}" var="child">
                                            <c:choose>
                                                <c:when test="${editForm.paramInput[loop.index].testDataType == child.id}">
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
                                    <select name="paramInput[${loop.index}].testDataSubtype" id="selectpicker3_${loop.index}" onchange="javascript:updateTestDataObjectDropdown(this.value, ${loop.index})" class="3col active cheil-select" required="required">
                                        <!--<option value="" selected>${editForm.paramInput[loop.index].testDataSubtype}</option>-->
                                    </select>
                                </div>
                                <div class="col-sm-3">
                                    <select name="paramInput[${loop.index}].testDataObject" id="selectpicker4_${loop.index}" class="3col active cheil-select" required="required">
                                        <!--<option value="" selected>${editForm.paramInput[loop.index].testDataObject}</option>-->
                                    </select>
                                </div>
                                <div class="col-sm-1"> <img style="width:32px;height:32px;" onclick="javascript:removeRow(${loop.index})" src="${contextPath}/images/remove.png" /></div>
                                </div>
                                </br>

                    <script type="text/javascript">
                        $(document).ready(function() {

                            var _testDataTypeChoices = new Choices('#selectpicker2_' + ${loop.index}, {
                                removeItemButton: true,
                                maxItemCount: -1,
                                searchResultLimit: 20,
                                renderChoiceLimit: -1,
                                shouldSort: false
                            });
                            testDataTypeChoices.push(_testDataTypeChoices);

                            var _testDataSubtypeChoices = new Choices('#selectpicker3_' + ${loop.index}, {
                                removeItemButton: true,
                                maxItemCount: -1,
                                searchResultLimit: 20,
                                renderChoiceLimit: -1,
                                shouldSort: false
                            });
                            testDataSubtypeChoices.push(_testDataSubtypeChoices);

                            var _testDataObjectChoices = new Choices('#selectpicker4_' + ${loop.index}, {
                                removeItemButton: true,
                                maxItemCount: -1,
                                searchResultLimit: 20,
                                renderChoiceLimit: -1,
                                shouldSort: false
                            });
                            testDataObjectChoices.push(_testDataObjectChoices);

                            updateTestDataSubtypeDropdown(${editForm.paramInput[loop.index].testDataType}, ${loop.index},${editForm.paramInput[loop.index].testDataSubtype})
                            updateTestDataObjectDropdown(${editForm.paramInput[loop.index].testDataSubtype}, ${loop.index},${editForm.paramInput[loop.index].testDataObject})
                        });
                    </script>

                </c:if>
                </c:forEach>
                            <div class="input-group control-group after-add-input-more">
                                <button class="btn btn-primary add-input-more" type="button"><i class="glyphicon glyphicon-add"></i>Add an input field</button>
                             </div>
                             <input id="existingParamsCount" name="existingParamsCount" value="${existingParamsCount}" style="display:none;">

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
          renderChoiceLimit:-1
        });
       var multipleCancelButton2 = new Choices('#testPlan', {
                 removeItemButton: true,
                 maxItemCount:-1,
                 searchResultLimit:20,
                 renderChoiceLimit:-1
               });

      var testDataTypeChoices = [];
      var testDataSubtypeChoices = [];
      var testDataObjectChoices = [];

    $(document).ready(function() {
       var count = $('#existingParamsCount').val();

      $(".add-more").click(function(){
          var html = $(".copy").html();
          $(".after-add-more").before(html);
      });



        $(".add-input-more").click(function() {
            
            var html = '<div class="row" id="inputParamRow' + count + '">'
                + '<div class="col-sm-3">'
                + '<select name="paramInput['+count+'].testDataType" id="selectpicker2_'+count+'" onchange="javascript:updateTestDataSubtypeDropdown(this.value, ' + count + ')" class="3col active cheil-select" required="required">'
                + '<option value="" selected>Select Test Data Type</option>'
                + '<c:forEach items="${testDataTypes}" var="testDataType">'
                + '<option value="${testDataType.id}">${testDataType.identifier}</option>'
                + '</c:forEach>'
                + '</select>'
                + '</div>'
                + '<div class="col-sm-3">'
                + '<select name="paramInput['+count+'].testDataSubtype" id="selectpicker3_'+count+'" onchange="javascript:updateTestDataObjectDropdown(this.value, ' + count + ')" class="3col active cheil-select" required="required">'
                + '<option value="" selected>Select Test Data Subtype</option>'
                + '</select>'
                + '</div>'
                + '<div class="col-sm-3">'
                + '<select name="paramInput['+count+'].testDataObject" id="selectpicker4_'+count+'" class="3col active cheil-select" required="required">'
                + '<option value="" selected>Select Test Data Object</option>'
                + '</select>'
                + '</div>'
                + '<div class="col-sm-1">'
                + '<img style="width:32px;height:32px;" onclick="javascript:removeRow(' + count + ')" src="${contextPath}/images/remove.png" /></div></div></br>';
            $(".after-add-input-more").before(html);

            var _testDataTypeChoices = new Choices('#selectpicker2_' + count, {
                removeItemButton: true,
                maxItemCount: -1,
                searchResultLimit: 20,
                renderChoiceLimit: -1,
                shouldSort: false
            });
            testDataTypeChoices.push(_testDataTypeChoices);

            var _testDataSubtypeChoices = new Choices('#selectpicker3_' + count, {
                removeItemButton: true,
                maxItemCount: -1,
                searchResultLimit: 20,
                renderChoiceLimit: -1,
                shouldSort: false
            });
            testDataSubtypeChoices.push(_testDataSubtypeChoices);

            var _testDataObjectChoices = new Choices('#selectpicker4_' + count, {
                removeItemButton: true,
                maxItemCount: -1,
                searchResultLimit: 20,
                renderChoiceLimit: -1,
                shouldSort: false
            });
            testDataObjectChoices.push(_testDataObjectChoices);

            count++;
        });


        $("body").on("click", ".remove", function () {
            $(this).parents(".control-group").remove();
        });

        $(document).on("click","#inputParamRow",function(){
                $(this).remove();
            });

    });

      function updateTestDataSubtypeDropdown(selectedTestDataType, rowNumber, selectedTestDataSubtype) {
          if(selectedTestDataType!='') {
              $.ajax({
                  type: 'GET',
                  url: "/admin/testprofile/getTestDataSubtypes/" + selectedTestDataType,
                  datatype: "json",
                  success: function(data){

                      var isMatchFound = false;
                      // Convert fetched data into a format suitable for setChoices
                      var choices = Object.keys(data).map(function (key) {
                          var isSelected = false;
                          if (typeof selectedTestDataSubtype !== 'undefined') {
                              isSelected = String(selectedTestDataSubtype) === String(key);
                              if (isSelected) {
                                  isMatchFound = true;
                              }
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
                      testDataSubtypeChoices[rowNumber].setChoices(choices, 'value', 'label', true);

                  },
                  error:function(e){
                      console.log(e.statusText);
                  }
              });

          }
          //removing all options from the other 2 dropdowns
          testDataObjectChoices[rowNumber].setChoices([{
              value: '',
              label: 'Select Test Data Object',
              selected: true,
              disabled: false
          }], 'value', 'label', true);

          testDataSubtypeChoices[rowNumber].setChoices([{
              value: '',
              label: 'Select Test Data Subtype',
              selected: true,
              disabled: false
          }], 'value', 'label', true);

      }

      function updateTestDataObjectDropdown(selectedTestDataSubtype, rowNumber, selectedTestDataObject) {

          if(selectedTestDataSubtype!='') {

              $.ajax({
                  type: 'GET',
                  url: "/admin/testprofile/getTestDataObjects/" + selectedTestDataSubtype,
                  datatype: "json",
                  success: function(data){

                      var isMatchFound = false;
                      // Convert fetched data into a format suitable for setChoices
                      var choices = Object.keys(data).map(function (key) {
                          var isSelected = false;
                          if (typeof selectedTestDataObject !== 'undefined') {
                              isSelected = String(selectedTestDataObject) === String(key);
                              if (isSelected) {
                                  isMatchFound = true;
                              }
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
                          label: 'Select Test Data Object',
                          selected: !isMatchFound, // Only select prompt if no match is found
                          disabled: false
                      };

                      // Prepend the 'prompt' choice to the choices array
                      choices.unshift(promptChoice);

                      // Update the dropdown with the fetched choices
                      testDataObjectChoices[rowNumber].setChoices(choices, 'value', 'label', true);

                  },
                  error:function(e){
                      console.log(e.statusText);
                  }
              });

          }
          testDataObjectChoices[rowNumber].setChoices([{
              value: '',
              label: 'Select Test Data Object',
              selected: true,
              disabled: false
          }], 'value', 'label', true);
      }

      function removeRow(countVal) {
          $('#inputParamRow'+countVal).remove();
      }

</script>
