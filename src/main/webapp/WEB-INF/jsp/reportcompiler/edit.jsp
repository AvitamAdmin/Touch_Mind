<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/reportCompiler/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/reportCompiler')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" title="Save" type="submit">Save</button>
                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <form:input path="recordId" class="inputbox-cheil-small" placeholder="Record Id" style="display:none" type="hidden"/>
        <div class="row">
            <div class="col-sm-4">
                <form:input path="identifier" class="inputbox-cheil-small" placeholder="*Enter Id" required="required"/>
                <span>Enter Id</span>
                <form:errors path="identifier" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-4">
                <form:input path="description" class="inputbox-cheil" placeholder="Enter Description" required="required"/>
                <span>Description</span>
                <form:errors path="description" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-4">
                <select name="node"  class="3col active cheil-select" required="required" id="selectpicker">
                    <option value="">Select Node</option>
                    <c:forEach items="${nodes}" var="child">
                        <c:choose>
                            <c:when test="${fn:contains( editForm.node, child.id ) }">
                              <option value="${child.id}" selected>${child.parentNode.name}-${child.identifier}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${child.id}" >${child.parentNode.name}-${child.identifier}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>

        </div>
        <br/><br/>
        <div class="row">
            <div class="col-sm-4">
                <select name="dataRelation"  class="3col active cheil-select" required="required"  id="getDataSourcesForRel">
                    <option value="">Please select the Data relation</option>
                    <c:forEach items="${dataRelations}" var="child">
                        <c:choose>
                            <c:when test="${fn:contains( editForm.dataRelation, child.dataRelationId ) }">
                              <option value="${child.dataRelationId}" selected>${child.dataRelationId}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${child.dataRelationId}" >${child.dataRelationId}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>
            <div class="col-sm-4">
                <select name="reportInterfaces"  class="3col active cheil-select" required="required" multiple id="selectpicker2">
                    <option value="">Select Node</option>
                    <c:forEach items="${nodes}" var="child">
                        <c:choose>
                            <c:when test="${fn:contains( editForm.reportInterfaces, child.path ) }">
                              <option value="${child.path}" selected>${child.parentNode.name}-${child.identifier}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${child.path}" >${child.parentNode.name}-${child.identifier}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>

        </div>
                <br/><br/>
        <c:forEach items="${editForm.reportCompilerMappings}" var="savedChild" varStatus="loop">
            <div class="row" id="inputParamRow${loop.index}">
                <div class="col-sm-3">
                    <form:input value="${savedChild.header}" path="reportCompilerMappings[${loop.index}].header" class="inputbox-cheil" placeholder="*Input the report header" />
                    <span>Report header</span>
                    <form:errors path="reportCompilerMappings[${loop.index}].header" class="text-danger"></form:errors>
                </div>
                <div class="col-sm-4">
                    <select name="reportCompilerMappings[${loop.index}].dataSource" class="3col active cheil-select" id="reportCompilerMappingsId-${loop.index}">
                        <option value="">Please select the Data source</option>
                        <c:forEach items="${dataSources}" var="child">
                            <c:choose>
                                <c:when test="${fn:contains( savedChild.dataSource, child.id ) }">
                                  <option value="${child.id}" selected>${child.dataSourceId}</option>
                                </c:when>
                                <c:otherwise>
                                   <option value="${child.id}" >${child.dataSourceId}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                </select>
                </div>
                <div class="col-sm-3"><select name="reportCompilerMappings[${loop.index}].param" id="select_param_${loop.index}"  class="3col active cheil-select">
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
                 <input type="checkbox" style="width: 20px;transform : scale(1.5);" id="reportCompilerMappings[${loop.index}].isCount" title="pivot the value ?" name="reportCompilerMappings[${loop.index}].isPivot" ${varIsPivot} /> &nbsp;
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
    $(document).ready(function() {
    if($('#getDataSourcesForRel').val()!=''){
        $('#addParam').removeAttr('disabled');
    }
     var count = $('#existingParamsCount').val();
            $(".add-more").click(function(){
            var html = '<div class="row control-group" id="inputParamRow'+count+'"><div class="col-sm-3"><input name="reportCompilerMappings[' + count + '].header" class="inputbox-cheil" placeholder="*Input the report header" /><span>Report header</span></div><div class="col-sm-4" id="div_param_'+ count +'"><select name="reportCompilerMappings[' + count + '].dataSource" id="reportCompilerMappingsId-'+count+'" class="3col active cheil-select"><option value="">Please select the Data source</option><c:forEach items="${dataSources}" var="child"><option value="${child.id}">${child.dataSourceId}</option></c:forEach></select></div><div class="col-sm-3"><select name="reportCompilerMappings[' + count + '].param" id="select_param_'+ count +'"  class="3col active cheil-select"></select></div><div class="col-sm-2"><label for="enterKey"><strong>Pivot&nbsp;</strong></label><input type="checkbox" style="width: 20px;transform : scale(1.5);" id="reportCompilerMappings['+ count +'].isPivot" title="Count the value" name="reportCompilerMappings[' + count + '].isPivot"  />&nbsp;&nbsp;<img style="width:32px;height:32px;" onclick="javascript:removeRow('+ count +')" src="${contextPath}/images/remove.png" /></div></div><br/><br/>'
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
                    $('#reportCompilerMappingsId-'+ curIndex).html(data);
                    $('#addParam').removeAttr('disabled');
                },
                error: function(e) {
                    $("body").removeClass("loading");
                    $('#reportCompilerMappingsId-'+ curIndex).html(e);
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
                $('[id*="reportCompilerMappingsId-"]').html(data);
                $('#addParam').removeAttr('disabled');
            },
            error: function(e) {
                $("body").removeClass("loading");
                $('[id*="reportCompilerMappingsId-"]').html(e);
            }
        });
        }
        });
    $('body').on('change', '[id*="reportCompilerMappingsId-"]', (e) => {
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