<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/dataRelation/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/dataRelation')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" title="Save" type="submit">Save</button>
                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <form:input path="recordId" class="inputbox-cheil-small" placeholder="Record Id" style="display:none" type="hidden"/>
        <div class="row">
            <div class="col-sm-4">
                <form:input path="identifier" class="inputbox-cheil-small" placeholder="identifier" required="required"/>
                <span>Rel Id</span>
                <form:errors path="identifier" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-4">
                <form:input path="shortDescription" class="inputbox-cheil" placeholder="Enter Description" required="required"/>
                <span>Description</span>
                <form:errors path="shortDescription" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-4">
            <c:choose>
                 <c:when test="${editForm.enableGenerator}">
                     <c:set var="varChecked" value="'checked'"></c:set>
                 </c:when>
                 <c:otherwise>
                     <c:set var="varChecked" value=""></c:set>
                 </c:otherwise>
             </c:choose>
             <form:label path="enableGenerator"><strong>Enable Generator?</strong></form:label>
             <form:checkbox path="enableGenerator" checked="${varChecked}" /></div>
        </div>
        <br/><br/>
        <c:forEach items="${editForm.dataRelationParams}" var="savedChild" varStatus="loop">
            <c:if test="${child!=''}">
              <div class="row"  id="inputParamRow${loop.index}">
                <div class="col-sm-4">
                    <select name="dataRelationParams[${loop.index}].dataSource" class="3col active cheil-select" id="datasource-${loop.index}">
                        <option value="">Please select the Data source</option>
                        <c:forEach items="${dataSources}" var="child">
                            <c:choose>
                                <c:when test="${fn:contains( savedChild.dataSource, child.recordId ) }">
                                  <option value="${child.recordId}" selected>${child.identifier}</option>
                                </c:when>
                                <c:otherwise>
                                   <option value="${child.recordId}" >${child.identifier}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-sm-4"><select name="dataRelationParams[${loop.index}].sourceKeyOne" id="select_param_${loop.index}"  class="3col active cheil-select">
                    <option value="${savedChild.sourceKeyOne}" >${savedChild.sourceKeyOne}</option>
                </select></div>
                <div class="col-sm-4"> <img style="width:32px;height:32px;" onclick="javascript:removeRow(${loop.index})" src="${contextPath}/images/remove.png" /></div>
            </div>
            <br/></br/>
            </c:if>
        </c:forEach>
    <input id="existingParamsCount" name="existingParamsCount" value="${existingParamsCount}" style="display:none;">
    <div class="input-group control-group after-add-input-more">
              <button class="btn btn-primary add-input-more" type="button"><i class="glyphicon glyphicon-add"></i>Add an input field</button>
    </div>
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
        $(".add-input-more").click(function(){
        var html = '<div class="row" id="inputParamRow' + count + '"><div class="col-sm-4"><select name="dataRelationParams[' + count + '].dataSource" class="3col active cheil-select" id="datasource-'+count+'"><option value="">Please select the Data source</option><c:forEach items="${dataSources}" var="child"><option value="${child.id}" >${child.identifier}</option></c:forEach></select></div><div class="col-sm-4"><select name="dataRelationParams[' + count + '].sourceKeyOne" id="select_param_' + count + '"  class="3col active cheil-select"></select></div><div class="col-sm-4"> <img style="width:32px;height:32px;" onclick="javascript:removeRow(' + count + ')" src="${contextPath}/images/remove.png" /></div></div></br></br>'
        $(".after-add-input-more").before(html);
        count++;
    });
    $(document).on("click","#inputParamRow",function(){
              $(this).remove();
          });
          $('body').on('change', '[id*="datasource-"]', (e) => {
                   e.preventDefault();
                   var categoryId = event.target.value;
                   var id = event.target.id;
                   var curIndex = id.split('-')[1];
            $.ajax({
                    type: "GET",
                    url: 'admin/dataRelation/getDatasourceParamsForId?id=' + categoryId,
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
    });
      function removeRow(countVal) {
        $('#inputParamRow'+countVal).remove();
      }
</script>