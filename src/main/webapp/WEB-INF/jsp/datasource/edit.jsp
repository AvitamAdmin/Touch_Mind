<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/datasource/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/datasource')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" title="Save" type="submit">Save</button>
                </div>
            </div>
        </div>
       <%@ include file="../commonFields.jsp" %>
       <form:input path="recordId" class="inputbox-cheil-small" placeholder="Record Id" style="display:none" type="hidden"/>
        <div class="row">
            <div class="col-sm-3">
                <form:input path="identifier" class="inputbox-cheil-small" placeholder="Enter identifier" required="required"/>
                <span>Identifier</span>
                <form:errors path="identifier" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="shortDescription" class="inputbox-cheil" placeholder="Enter Description" required="required"/>
                <span>Description</span>
                <form:errors path="shortDescription" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="separatorSymbol" class="inputbox-cheil-small" placeholder="Input the separator"/>
                <span>Separator</span>
                <form:errors path="separatorSymbol" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3 cheil-select">
                <select name="format"  class="3col active" required="required">
                    <option value="">Please select the file format</option>
                    <c:forEach items="${fileFormats}" var="child">
                        <c:choose>
                            <c:when test="${fn:contains( editForm.format, child ) }">
                              <option value="${child}" selected>${child}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${child}" >${child}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>
        </div>
        <br/><br/>
        <div class="row">
            <div class="col-sm-12">
                <form:input path="sourceAddress" class="inputbox-cheil" placeholder="Enter Source address" required="required"/>
                <span>Source address</span>
                <form:errors path="sourceAddress" class="text-danger"></form:errors>
            </div>
        </div>
        <br/><br/>
        <div class="row">
                    <div class="col-sm-12">
                        <form:input path="skuUrl" class="inputbox-cheil-small" placeholder="Enter Sku link" />
                        <span>Sku link</span>
                        <form:errors path="skuUrl" class="text-danger"></form:errors>
                    </div>
                </div>
                <br/><br/>
        <div class="row">
            <c:forEach items="${editForm.srcInputParams}" var="child">
                <div class="col-sm-4 control-group input-group">
                    <input type="text" name="srcInputParams[]" value="${child}" class="form-control" placeholder="Enter param Here">
                    <span>Param</span>
                    <div class="input-group-btn px-4">
                      <button class="btn btn-danger remove" type="button"><i class="glyphicon glyphicon-remove"></i>Remove</button>
                    </div>
               </div>
            </c:forEach>
            <div class="col-sm-4 input-group control-group after-add-more">
              <button class="btn btn-primary add-more" type="button"><i class="glyphicon glyphicon-add"></i>Add Param</button>
              <div class="input-group-btn"></div>
            </div>
            <div class="copy d-none">
               <div class="col-sm-4 control-group input-group"">
                    <input type="text" name="srcInputParams[]" class="form-control" placeholder="Enter param Here">
                    <span>Param</span>
                    <div class="input-group-btn px-4">
                        <button class="btn btn-danger remove" type="button"><i class="glyphicon glyphicon-remove"></i>Remove</button>
                    </div>
               </div>
            </div>
        </div>
        <c:forEach items="${editForm.inputForms}" var="child" varStatus="loop">
        <c:if test="${child!=''}">
        <div class="row"  id="inputParamRow${loop.index}">
                                <div class="col-sm-5 control-group input-group">
                                    <div class="cat">
                                       <label>
                                       <c:choose>
                                           <c:when test="${editForm.inputForms[loop.index].fileName == 'on'}">
                                                <input type="checkbox" name="inputForms[${loop.index}].fileName" checked><span>Filename</span>
                                           </c:when>
                                           <c:otherwise>
                                                <input type="checkbox" name="inputForms[${loop.index}].fileName" ><span>Filename</span>
                                           </c:otherwise>
                                       </c:choose>

                                       </label>
                                       <label>
                                          <c:choose>
                                             <c:when test="${editForm.inputForms[loop.index].comma == 'on'}">
                                                  <input type="checkbox" name="inputForms[${loop.index}].comma" checked><span>Comma</span>
                                             </c:when>
                                             <c:otherwise>
                                                  <input type="checkbox" name="inputForms[${loop.index}].comma" ><span>Comma</span>
                                             </c:otherwise>
                                         </c:choose>
                                       </label>
                                       <label>
                                          <c:choose>
                                             <c:when test="${editForm.inputForms[loop.index].fixed == 'on'}">
                                                  <input type="checkbox" name="inputForms[${loop.index}].fixed" checked><span>Fixed</span>
                                             </c:when>
                                             <c:otherwise>
                                                  <input type="checkbox" name="inputForms[${loop.index}].fixed" ><span>Fixed</span>
                                             </c:otherwise>
                                         </c:choose>
                                       </label>

                                       <label>
                                         <c:choose>
                                                <c:when test="${editForm.inputForms[loop.index].optional == 'on'}">
                                                     <input type="checkbox" name="inputForms[${loop.index}].optional" checked><span>Optional</span>
                                                </c:when>
                                                <c:otherwise>
                                                     <input type="checkbox" name="inputForms[${loop.index}].optional" ><span>Optional</span>
                                                </c:otherwise>
                                            </c:choose>
                                       </label>
                                       <label>
                                         <c:choose>
                                            <c:when test="${editForm.inputForms[loop.index].importBox == 'on'}">
                                                 <input type="checkbox" name="inputForms[${loop.index}].importBox" checked><span>Import</span>
                                            </c:when>
                                            <c:otherwise>
                                                 <input type="checkbox" name="inputForms[${loop.index}].importBox" ><span>Import</span>
                                            </c:otherwise>
                                        </c:choose>
                                       </label>
                                    </div>
                               </div>
                               <div class="col-sm-2">
                                    <input type="text" name="inputForms[${loop.index}].fieldName" value="${editForm.inputForms[loop.index].fieldName}" class="inputbox-cheil" placeholder="Input the field name without space">
                                </div>
                                    <div class="col-sm-2 cheil-select">
                                        <select onchange="javascript:inputFormatChange(this.value,${loop.index})" name="inputForms[${loop.index}].inputFormat"  class="3col active">
                                            <option value="">Select the field input format</option>
                                            <c:forEach items="${inputFormats}" var="child">
                                                <c:choose>
                                                    <c:when test="${editForm.inputForms[loop.index].inputFormat == child}">
                                                      <option value="${child}" selected>${child}</option>
                                                    </c:when>
                                                    <c:otherwise>
                                                       <option value="${child}" >${child}</option>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <div class="col-sm-2 dropdowninput">
                                    <c:choose>
                                    <c:when test="${not empty editForm.inputForms[loop.index].fieldValue}">
                                        <textarea rows="2"  id="fieldValueRows${loop.index}" name="inputForms[${loop.index}].fieldValue" class="inputbox-cheil-textarea" placeholder="Enter the Values">${editForm.inputForms[loop.index].fieldValue}</textarea>
                                        <span>Values</span>
                                        </c:when>
                                        <c:otherwise>
                                        <textarea rows="2" style="display:none" id="fieldValueRows${loop.index}" name="inputForms[${loop.index}].fieldValue" class="inputbox-cheil-textarea" placeholder="Enter the Values">${editForm.inputForms[loop.index].fieldValue}</textarea>
                                                                                <span>Values</span>
                                        </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <div class="col-sm-1"> <img style="width:32px;height:32px;" onclick="javascript:removeRow(${loop.index})" src="${contextPath}/images/remove.png" /></div>

                            </div>
                                       </br>
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

    $(document).ready(function() {
       var count = $('#existingParamsCount').val();
      $(".add-more").click(function(){
          var html = $(".copy").html();
          $(".after-add-more").before(html);
      });

      $(".add-input-more").click(function(){
      count++;
                var html = '<div class="row" id="inputParamRow' + count + '"><div class="col-sm-5 control-group input-group">    <div class="cat">       <label>          <input type="checkbox" name="inputForms['+count+'].fileName" ><span>Filename</span>       </label>       <label>          <input type="checkbox" name="inputForms['+count+'].comma" ><span>Comma</span>       </label>       <label>          <input type="checkbox" name="inputForms['+count+'].fixed" ><span>Fixed</span>       </label>       <label>          <input type="checkbox" name="inputForms['+count+'].optional" ><span>Optional</span>       </label>       <label>          <input type="checkbox" name="inputForms['+count+'].importBox" ><span>Import</span>       </label>    </div></div><div class="col-sm-2">    <input type="text" name="inputForms['+count+'].fieldName" class="inputbox-cheil" placeholder="Input the field name without space">    <span>Field name</span></div>    <div class="col-sm-2 cheil-select">        <select onchange="javascript:inputFormatChange(this.value, '+count+')" name="inputForms['+count+'].inputFormat"  class="3col active">            <option value="">Select the field input format</option>            <c:forEach items="${inputFormats}" var="child">                 <option value="${child}" >${child}</option>            </c:forEach>        </select>    </div>    <div class="col-sm-2 dropdowninput">        <textarea id="fieldValueRows'+count+'" rows="2" name="inputForms['+count+'].fieldValue" class="inputbox-cheil-textarea" placeholder="Enter the Values"></textarea>        <span>Values</span>    </div> <div class="col-sm-1"> <img style="width:32px;height:32px;" onclick="javascript:removeRow('+count+')" src="${contextPath}/images/remove.png" /></div></div>'
                $(".after-add-input-more").before(html);
            });

      $("body").on("click",".remove",function(){
          $(this).parents(".control-group").remove();
      });

      $(document).on("click","#inputParamRow",function(){
                $(this).remove();
            });

    });

  function removeRow(countVal) {
    $('#inputParamRow'+countVal).remove();
  }

  function inputFormatChange(value, countVal) {
  if(value=='Input Box'){
    $('#fieldValueRows'+countVal).hide();
    $('#fieldValueRows'+countVal).val('');
  }else{
    $('#fieldValueRows'+countVal).show();
  }

    }

</script>