<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/mapping/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/mapping')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" title="Save" type="submit">Save</button>
                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <form:input path="recordId" class="inputbox-cheil-small" placeholder="Record Id" style="display:none" type="hidden"/>
        <div class="row">
            <div class="col-sm-3">
                <form:input path="identifier" class="inputbox-cheil-small" placeholder="*Enter Id" required="required"/>
                <span>Enter Id</span>
                <form:errors path="identifier" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <form:input path="shortDescription" class="inputbox-cheil" placeholder="Enter Description" required="required"/>
                <span>Description</span>
                <form:errors path="shortDescription" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-3">
                <select name="node"  class="3col active cheil-select" required="required" id="selectpicker">
                    <option value="">Select Node</option>
                    <c:forEach items="${nodes}" var="child">
                        <c:choose>
                            <c:when test="${fn:contains( editForm.node, child.recordId ) }">
                              <option value="${child.recordId}" selected>${child.identifier}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${child.recordId}" >${child.identifier}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>
            <div class="col-sm-3">
                <select class="cheil-select" name="subsidiaries[]" placeholder="Select subsidiaries" multiple id="selectpicker2" required="required">
                <span>Select subsidiaries</span>
                    <c:forEach items="${subsidiaries}" var="child">
                        <c:choose>
                            <c:when test="${fn:contains( editForm.subsidiaries, child ) }">
                              <option value="${child.recordId}" selected>${child.identifier}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${child.recordId}" >${child.identifier}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>
        </div>
        <br/><br/>
        <div class="row">
            <div class="col-sm-3">
                <select name="dataRelation"  class="3col active cheil-select" required="required"  id="getDataSourcesForRel">
                    <option value="">Please select the Data relation</option>
                    <c:forEach items="${dataRelations}" var="child">
                        <c:choose>
                            <c:when test="${fn:contains( editForm.dataRelation, child.recordId ) }">
                              <option value="${child.recordId}" selected>${child.identifier}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${child.recordId}" >${child.identifier}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>
            <div class="col-sm-3" style="margin: auto; padding: 10px;">
                <c:choose>
                     <c:when test="${editForm.enableVoucher}">
                         <c:set var="varChecked" value="'checked'"></c:set>
                     </c:when>
                     <c:otherwise>
                         <c:set var="varChecked" value=""></c:set>
                     </c:otherwise>
                 </c:choose>
                 <form:label path="enableVoucher"><strong>Enable Voucher?</strong></form:label>
                 <form:checkbox path="enableVoucher" checked="${varChecked}" />
            </div>
            <div class="col-sm-3" style="margin: auto; padding: 10px;">
                <c:choose>
                     <c:when test="${editForm.enableCategory}">
                         <c:set var="varChecked" value="'checked'"></c:set>
                     </c:when>
                     <c:otherwise>
                         <c:set var="varChecked" value=""></c:set>
                     </c:otherwise>
                 </c:choose>
                 <form:label path="enableCategory"><strong>Enable Category?</strong></form:label>
                 <form:checkbox path="enableCategory" checked="${varChecked}" />
            </div>
            <div class="col-sm-3" style="margin: auto; padding: 10px;">
                    <c:choose>
                         <c:when test="${editForm.enableCurrentPage}">
                             <c:set var="varChecked" value="'checked'"></c:set>
                         </c:when>
                         <c:otherwise>
                             <c:set var="varChecked" value=""></c:set>
                         </c:otherwise>
                     </c:choose>
                     <form:label path="enableCurrentPage"><strong>Enable Current Page?</strong></form:label>
                     <form:checkbox path="enableCurrentPage" checked="${varChecked}" />
                </div>

        </div>
        <br/><br/>
        <div class="row">
            <div class="col-sm-3">
                <c:choose>
                     <c:when test="${editForm.enableToggle}">
                         <c:set var="varChecked" value="'checked'"></c:set>
                     </c:when>
                     <c:otherwise>
                         <c:set var="varChecked" value=""></c:set>
                     </c:otherwise>
                 </c:choose>
                 <form:label path="enableToggle"><strong>Enable Bundle?</strong></form:label>
                 <form:checkbox path="enableToggle" checked="${varChecked}" />
            </div>
            <div class="col-sm-3">
                <c:choose>
                     <c:when test="${editForm.enableVariant}">
                         <c:set var="varChecked" value="'checked'"></c:set>
                     </c:when>
                     <c:otherwise>
                         <c:set var="varChecked" value=""></c:set>
                     </c:otherwise>
                 </c:choose>
                 <form:label path="enableVariant"><strong>Enable Variant?</strong></form:label>
                 <form:checkbox path="enableVariant" checked="${varChecked}" />
            </div>
        </div>
        <br/><br/>
        <c:forEach items="${editForm.sourceTargetParamMappingList}" var="savedChild" varStatus="loop">
            <div class="row" id="inputParamRow${loop.index}">
                <div class="col-sm-3">
                    <form:input value="${savedChild.header}" path="sourceTargetParamMappingList[${loop.index}].header" class="inputbox-cheil" placeholder="*Input the report header" />
                    <span>Report header</span>
                    <form:errors path="sourceTargetParamMappingList[${loop.index}].header" class="text-danger"></form:errors>
                </div>
                <div class="col-sm-4">
                    <select name="sourceTargetParamMappingList[${loop.index}].dataSource" class="3col active cheil-select" id="sourceTargetParamMappingId-${loop.index}">
                        <option value="">Please select the Data source</option>
                        <c:forEach items="${dataSources}" var="child">
                            <c:choose>
                                <c:when test="${fn:contains( savedChild.dataSource, child ) }">
                                  <option value="${child.recordId}" selected>${child.identifier}</option>
                                </c:when>
                                <c:otherwise>
                                   <option value="${child.recordId}" >${child.identifier}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                </select>
                </div>
                <div class="col-sm-3"><select name="sourceTargetParamMappingList[${loop.index}].param" id="select_param_${loop.index}"  class="3col active cheil-select">
                    <option value="${savedChild.param}">${savedChild.param}</option>
                </select></div>
                <div class="col-sm-2">
                   <label for="enterKey"><strong>Pivot&nbsp;</strong></label>
                   <c:choose>
                        <c:when test="${savedChild.isPivot}">
                            <c:set var="varIsPivot" value="checked"></c:set>
                        </c:when>
                        <c:otherwise>
                            <c:set var="varIsPivot" value=""></c:set>
                        </c:otherwise>
                    </c:choose>
                 <input type="checkbox" style="width: 20px;transform : scale(1.5);" id="sourceTargetParamMappingList[${loop.index}].isCount" title="pivot the value ?" name="sourceTargetParamMappingList[${loop.index}].isPivot" ${varIsPivot} /> &nbsp;
                 <img style="width:32px;height:32px;" onclick="javascript:removeRow(${loop.index})" src="${contextPath}/images/remove.png" />
                 </div>
            </div>
            <br/><br/>
        </c:forEach>
    <div class="row after-add-more">
        <div class="col-sm-12">
            <button class="btn btn-primary add-more" disabled="disabled" id="addParam" type="button"><i class="glyphicon glyphicon-add"></i>Add Param</button>
        </div>
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
var multipleCancelButtonRel = new Choices('#getDataSourcesForRel', {
                        removeItemButton: true,
                        maxItemCount:-1,
                        searchResultLimit:20,
                        renderChoiceLimit:-1
                      });
var multipleCancelButton1 = new Choices('#selectpicker', {
          removeItemButton: true,
          maxItemCount:-1,
          searchResultLimit:20,
          renderChoiceLimit:-1
        });
      var multipleCancelButton2 = new Choices('#selectpicker2', {
                      removeItemButton: true,
                      maxItemCount:-1,
                      searchResultLimit:20,
                      renderChoiceLimit:-1
                    });
      var multipleCancelButton3 = new Choices('#selectpicker3', {
                    removeItemButton: true,
                    maxItemCount:-1,
                    searchResultLimit:20,
                    renderChoiceLimit:-1
                  });
    $(document).ready(function() {
    if($('#getDataSourcesForRel').val()!=''){
        $('#addParam').removeAttr('disabled');
    }
     var count = $('#existingParamsCount').val();
            $(".add-more").click(function(){
            var html = '<div class="row control-group" id="inputParamRow'+count+'"><div class="col-sm-3"><input name="sourceTargetParamMappingList[' + count + '].header" class="inputbox-cheil" placeholder="*Input the report header" /><span>Report header</span></div><div class="col-sm-4" id="div_param_'+ count +'"><select name="sourceTargetParamMappingList[' + count + '].dataSource" id="sourceTargetParamMappingId-'+count+'" class="3col active cheil-select"><option value="">Please select the Data source</option><c:forEach items="${dataSources}" var="child"><option value="${child.id}">${child.identifier}</option></c:forEach></select></div><div class="col-sm-3"><select name="sourceTargetParamMappingList[' + count + '].param" id="select_param_'+ count +'"  class="3col active cheil-select"></select></div><div class="col-sm-2"><label for="enterKey"><strong>Pivot&nbsp;</strong></label><input type="checkbox" style="width: 20px;transform : scale(1.5);" id="sourceTargetParamMappingList['+ count +'].isPivot" title="Count the value" name="sourceTargetParamMappingList[' + count + '].isPivot"  />&nbsp;&nbsp;<img style="width:32px;height:32px;" onclick="javascript:removeRow('+ count +')" src="${contextPath}/images/remove.png" /></div></div><br/><br/>'
            $(".after-add-more").before(html);
            var id = $('#getDataSourcesForRel option:selected').val();
            if(id!=''){
            $("body").addClass("loading");
            $.ajax({
                type: "GET",
                url: 'admin/mapping/getDataSourcesByRelationId?id=' + id,
                timeout: 600000,
                success: function(data) {
                var curIndex = count -1;
                    $("body").removeClass("loading");
                    $('#sourceTargetParamMappingId-'+ curIndex).html(data);
                    $('#addParam').removeAttr('disabled');
                },
                error: function(e) {
                    $("body").removeClass("loading");
                    $('#sourceTargetParamMappingId-'+ curIndex).html(e);
                }
            });
            }
            count++;
        });

      });
      $("body").on("click",".remove",function(){
          $(this).parents(".control-group").remove();
      });

      function removeRow(countVal) {
        $('#inputParamRow'+countVal).remove();
      }

    $(document).on("change", "#getDataSourcesForRel", function(e){
        e.preventDefault();
        var id = $(this).val();
        if(id!=''){
        $("body").addClass("loading");
        $.ajax({
            type: "GET",
            url: 'admin/mapping/getDataSourcesByRelationId?id=' + id,
            timeout: 600000,
            success: function(data) {
                $("body").removeClass("loading");
                $('[id*="sourceTargetParamMappingId-"]').html(data);
                $('#addParam').removeAttr('disabled');
            },
            error: function(e) {
                $("body").removeClass("loading");
                $('[id*="sourceTargetParamMappingId-"]').html(e);
            }
        });
        }
        });
    $('body').on('change', '[id*="sourceTargetParamMappingId-"]', (e) => {
           e.preventDefault();
           var categoryId = event.target.value;
           var id = event.target.id;
           var curIndex = id.split('-')[1];
        $.ajax({
            type: "GET",
            url: 'admin/mapping/getDataSourceParamsById?id=' + categoryId,
            timeout: 600000,
            success: function(data) {
                $("body").removeClass("loading");
                $('#select_param_' + curIndex).html(data);
            },
            error: function(e) {
                $("body").removeClass("loading");
                $('#select_param_'+ curIndex).html(e);
            }
        });
           });

</script>