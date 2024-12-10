<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/persona/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/persona')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
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
        <c:forEach items="${editForm.paramInput}" var="child" varStatus="loop">
                <c:if test="${child!=''}">
                <div class="row"  id="inputParamRow${loop.index}">
                                        <div class="col-sm-4 control-group input-group">
                                            <input name="paramInput[${loop.index}].paramKey" value="${editForm.paramInput[loop.index].paramKey}" class="inputbox-cheil-small" placeholder="Key" />
                                            </div>

                                        <div class="col-sm-4">
                                            <input type="text" name="paramInput[${loop.index}].paramValue" value="${editForm.paramInput[loop.index].paramValue}" class="inputbox-cheil" placeholder="Input Value">
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
      count++;
                var html = '<div class="row" id="inputParamRow' + count + '"><div class="col-sm-4">    <input type="text" name="paramInput['+count+'].paramKey" class="inputbox-cheil" placeholder="Enter the element key">    <span>Enter the element key</span></div>     <div class="col-sm-4">    <input type="text" name="paramInput['+count+'].paramValue" class="inputbox-cheil" placeholder="Enter the value">    <span>Enter the value</span></div> <div class="col-sm-1"> <img style="width:32px;height:32px;" onclick="javascript:removeRow('+count+')" src="${contextPath}/images/remove.png" /></div></div></br>'
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

</script>