<%@ include file="../include.jsp" %>
<div class="main-content">
  <div class="row">
    <div class="col-sm-12">
      <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
    </div>
  </div>
  <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/testplanbuilder/edit" modelAttribute="editForm">
    <div class="row">
      <div class="col-sm-12">
        <div class="dt-buttons">
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/testplanbuilder')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
        </div>
      </div>
    </div>
    <%@ include file="../commonFields.jsp" %>
    <div class="row">
      <div class="col-sm-4">
        <form:input path="identifier" class="inputbox-cheil-small" placeholder="Enter Id" required="required" />
        <span>Enter Id</span>
        <form:errors path="identifier" class="text-danger"></form:errors>
      </div>
      <div class="col-sm-4">
        <form:input path="shortDescription" class="inputbox-cheil-long" placeholder="Enter Description" />
        <span>Enter Description</span>
        <form:errors path="shortDescription" class="text-danger"></form:errors>
      </div>
       <div class="col-sm-4">
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
    <!-- html for param elements loaded from DB-->
    <c:forEach items="${editForm.testSteps}" var="child" varStatus="loop">
      <c:if test="${child!=''}">
        <div class="row" id="inputParamRow${loop.index}">
          <div class="col-sm-3 cheil-select" id="pageInputDiv">
            <select name="testSteps[${loop.index}].className" data-method-dropdown="#actionInput${loop.index}" onchange="onClassSelected(this.value, ${loop.index})" class="3col active">
              <option value="" selected>Select class</option>
              <c:forEach items="${dataSet}" var="dataItem"> <!-- list of classes is sent from the controller -->
                <option value="${dataItem}" <c:if test="${dataItem == editForm.testSteps[loop.index].className}">selected</c:if>>${dataItem}</option>
              </c:forEach>
            </select>
          </div>
          <div class="col-sm-3 cheil-select" id="actionInputDiv${loop.index}"> <!-- will be populated with data in the JS script after the page is fully loaded -->
            <select readonly="true" name="testSteps[${loop.index}].methodName" class="3col active" id="actionInput${loop.index}" onchange="onMethodSelected(this.value, ${loop.index})"></select>
          </div>
          <div class="col-sm-1">
            <img style="width: 32px; height: 32px;" onclick="javascript:removeRow(${loop.index})" src="${contextPath}/images/remove.png" />
          </div>
        </div>
        <br />
        <script type="text/javascript">
          $(document).ready(function() {
            // Trigger onClassSelected function for the current row to populate dropdowns with values
            onClassSelected('${editForm.testSteps[loop.index].className}', ${loop.index}, '${editForm.testSteps[loop.index].methodName}');
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
  $(document).ready(function() {
    var count = $('#existingParamsCount').val();
    $(".add-more").click(function(){
      var html = $(".copy").html();
      $(".after-add-more").before(html);
    });

    $(".add-input-more").click(function(){

      <!-- html for param elements appearing once the "add input" button is clicked" -->
      var html = '<div class="row" id="inputParamRow' + count + '">'
              + '<div class="col-sm-3 cheil-select" id="pageInputDiv">'
              + '<select name="testSteps[' + count + '].className" onchange="onClassSelected(this.value, ' + count + ')" class="3col active">'
              + '<option value="" selected>Select page</option>'
              + '<c:forEach items="${dataSet}" var="dataItem">'
              + '<option value="${dataItem}">${dataItem}</option>'
              + '</c:forEach>'
              + '</select>'
              + '</div>'
              + '<div class="col-sm-3 cheil-select" id="actionInputDiv' + count + '">'
              + '<select onchange="onMethodSelected(this.value, ' + count + ')" name="testSteps[' + count + '].methodName" id="actionInput' + count + '" class="3col active"></select>'
              + '</div>'
              + '<div class="col-sm-1">'
              + '<img style="width: 32px; height: 32px;" onclick="removeRow(' + count + ')" src="${contextPath}/images/remove.png" />'
              + '</div>'
              + '</div>'
              + '<br />';
      count++;

      $(".after-add-input-more").before(html);
    });

    $("body").on("click",".remove",function(){
      $(this).parents(".control-group").remove();
    });

    $(document).on("click","#inputParamRow",function(){
      $(this).remove();
    });

  });

  //updates options in the Methods dropdown according to the selected Class.
  //presetMethodValue - the value that the method dropdown should be set to.
  function onClassSelected(value, countVal, presetMethodValue) {
    if(value!=''){
      // Store the selected page value in a data attribute
      $('#inputParamRow' + countVal).data('selectedPage', value);

      $.ajax({
        type: 'GET',
        url: "/admin/testplanbuilder/getMethodName/" + value,
        datatype: "json",
        success: function(data){
          var toAppend='<option value="">Select action</option>';
          for(var i=0; i<data.length; i++){
            toAppend+='<option value="'+data[i]+'">'+ data[i] +'</option>'
          }
          $('#actionInput'+countVal).empty().append(toAppend); //populating the dropdown with data

          // Check if a presetMethodValue is provided and set it as the selected value
          if (presetMethodValue !== undefined) {
            $('#actionInput' + countVal).val(presetMethodValue);
            onMethodSelected(presetMethodValue, countVal)
          }
        },
        error:function(e){
          console.log(e.statusText);
        }
      });
    }
  }

  //updates options in the Enums dropdown according to the selected Class and Method
  function onMethodSelected(value, countVal) {
    $('#fieldInputDiv'+countVal).hide();
    if(value!=''){
      if(value=='retrieveFieldFromPage'){ //only appears if selected method is "retrieveFieldFromPage"
        $('#fieldInputDiv'+countVal).show();

        // Retrieve the stored selected page value
        var selectedPage = $('#inputParamRow' + countVal).data('selectedPage');

        $.ajax({
          type: 'GET',
          url: "/admin/testplanbuilder/getFieldName/" + selectedPage,
          datatype: "json",
          success: function(data){
            var toAppend='<option value="">Select the field</option>';
            for(var i=0; i<data.length; i++){
              toAppend+='<option value="'+data[i]+'">'+ data[i] +'</option>'
            }
            $('#fieldInput'+countVal).empty().append(toAppend);
          },
          error:function(e){
            console.log(e.statusText);
          }
        });
      }
    }
  }

  function removeRow(countVal) {
    $('#inputParamRow'+countVal).remove();
  }

</script>